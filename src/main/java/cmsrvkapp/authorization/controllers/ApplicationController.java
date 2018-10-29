package cmsrvkapp.authorization.controllers;

import cmsrvkapp.authorization.service.ApplicationService;
import cmsrvkapp.authorization.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import cmsrvkapp.authorization.views.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@SuppressWarnings({"SpringAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
@RestController
@CrossOrigin(origins = { "https://cmsvkapp-test.herokuapp.com",
        "https://cmsvkapp-admin.herokuapp.com",
        "http://localhost:8080",
        "http://localhost:9000" },
        allowCredentials = "true")
@RequestMapping(path = "/api/apps")
@Validated
public class ApplicationController {

    @Autowired
    private ApplicationService dbApplications;

    @Autowired
    private ServerService dbServers;

    private static final String CURRENT_USER_KEY = "currentUser";

    @RequestMapping(method = RequestMethod.GET, path = "{appName}/config")
    public ResponseEntity getConfig(@PathVariable(value = "appName") String appName,
                                    @RequestHeader(value = "accept", required = false) String accept,
                                    HttpSession httpSession) {
        try {
            ApplicationView app = dbApplications.getByName(appName);
            final String currentUser = (String) httpSession.getAttribute(CURRENT_USER_KEY);
            if (!currentUser.equals(app.getCreatorLogin())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseView.ERROR_NO_RIGHTS_TO_CHANGE_APP);
            }
            String config = dbApplications.getConfig(app);
            return ResponseEntity.status(HttpStatus.OK).body(config);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseView.ERROR_APP_NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }

    }

    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity addApplication(@RequestBody ApplicationView app,
                                         HttpSession httpSession) {
        try {
            final String currentUser = (String) httpSession.getAttribute(CURRENT_USER_KEY);
            if (!currentUser.equals(app.getCreatorLogin())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseView.ERROR_NO_RIGHTS_TO_CHANGE_APP);
            }
            dbApplications.addApplication(app);
            return ResponseEntity.status(HttpStatus.CREATED).body(app);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseView.ERROR_APP_NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
    }


    @RequestMapping(method = RequestMethod.POST, path = "{appName}/config")
    public ResponseEntity setConfig(@Valid @RequestBody String config,
                                    @PathVariable(value = "appName") String appName,
                                    @RequestHeader(value = "Accept", required = false) String accept,
                                    HttpSession httpSession) {
        try {
            ApplicationView app = dbApplications.getByName(appName);
            final String currentUser = (String) httpSession.getAttribute(CURRENT_USER_KEY);
            if (!currentUser.equals(app.getCreatorLogin())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseView.ERROR_NO_RIGHTS_TO_CHANGE_APP);
            }
            dbApplications.setConfig(app, config);
            return ResponseEntity.status(HttpStatus.OK).body(dbApplications.getConfig(app));
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseView.ERROR_APP_NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "{appName}/downloadJSON")
    public ResponseEntity dowloadJSON(@PathVariable(value = "appName") String appName,
                                      @RequestParam(value = "server_key") String serverKey,
                                    HttpSession httpSession) {
        try {
            if (dbServers.checkKey(serverKey)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseView.ERROR_NO_RIGHTS_TO_CHANGE_APP);
            }
            ApplicationView app = dbApplications.getByName(appName);
            String config = dbApplications.getConfig(app);
            String pagesJSContent = "const json = " + config + ";\nexport default json;";
            return ResponseEntity.status(HttpStatus.OK).body(pagesJSContent);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseView.ERROR_APP_NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "{appName}/deploy")
    public ResponseEntity deploy(@PathVariable(value = "appName") String appName,
                                    @RequestHeader(value = "accept", required = false) String accept,
                                    HttpSession httpSession) {
        try {
            ApplicationView app = dbApplications.getByName(appName);
            final String currentUser = (String) httpSession.getAttribute(CURRENT_USER_KEY);
            if (!currentUser.equals(app.getCreatorLogin())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseView.ERROR_NO_RIGHTS_TO_CHANGE_APP);
            }
            RestTemplate restTemplate = new RestTemplate();
            String url = dbServers.getUrl();
            HttpEntity<DeployView> requestBody = new HttpEntity<>(new DeployView(app.getAppName(),
                    dbServers.getKey(url)));
            String urlDeploy = url + "/deploy";
            ResponseEntity<String> result = restTemplate.postForEntity(urlDeploy,
                    requestBody, String.class);

            if (result.getStatusCode() == HttpStatus.OK) {
                dbServers.instanceWasAdded(url);
                return ResponseEntity.status(HttpStatus.OK).body("{\"url\": \"" + url + "\"}");
            } else if (result.getStatusCode() == HttpStatus.ALREADY_REPORTED) {
                return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(ResponseView.ALREADY_DEPLOYED);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseView.ERROR_DEPLOY);
            }
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseView.ERROR_APP_NOT_FOUND);
        }

    }


}
