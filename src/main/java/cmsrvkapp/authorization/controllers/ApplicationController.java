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
        "http://localhost:9000",
        "https://cmsvkapp.site",
        "http://cmsvkapp.site"},
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
            if (currentUser == null || !currentUser.equals(app.getCreatorLogin())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseView.ERROR_NO_RIGHTS_TO_CHANGE_APP);
            }
            String config = dbApplications.getConfig(app);
            return ResponseEntity.status(HttpStatus.OK).body(config);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseView.ERROR_APP_NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.OK).body(ResponseView.ERROR_CONFIG_NOT_FOUND);
        }

    }

    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity addApplication(@RequestBody ApplicationView app,
                                         HttpSession httpSession) {
        try {
            final String currentUser = (String) httpSession.getAttribute(CURRENT_USER_KEY);
            if (currentUser == null || !currentUser.equals(app.getCreatorLogin())) {
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
            if (currentUser == null || !currentUser.equals(app.getCreatorLogin())) {
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
            app.setState(ApplicationState.STARTED);
            dbApplications.setState(app);
            return ResponseEntity.status(HttpStatus.OK).body(pagesJSContent);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseView.ERROR_APP_NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "{appName}/stop")
    public ResponseEntity stop(@PathVariable(value = "appName") String appName,
                                      @RequestParam(value = "server_key") String serverKey,
                                      HttpSession httpSession) {
        try {
            if (dbServers.checkKey(serverKey)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseView.ERROR_NO_RIGHTS_TO_CHANGE_APP);
            }
            ApplicationView app = dbApplications.getByName(appName);
            app.setState(ApplicationState.STOPPED);
            dbApplications.setState(app);
            return ResponseEntity.status(HttpStatus.OK).body(ResponseView.SUCCESS_STOP);
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
            if (currentUser == null || !currentUser.equals(app.getCreatorLogin())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseView.ERROR_NO_RIGHTS_TO_CHANGE_APP);
            }
            if (app.getState().equals(ApplicationState.STARTS)) {
                return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(ResponseView.ALREADY_DEPLOYED);
            }
            app.setState(ApplicationState.STARTS);
            dbApplications.setState(app);
            if (app.getServerUrl() == null) {
                app.setServerUrl(dbServers.getUrl());
                dbApplications.setUrl(app);
                dbServers.instanceWasAdded(app.getServerUrl());
            }
            String url = app.getServerUrl();
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<DeployView> requestBody = new HttpEntity<>(new DeployView(app.getAppName(),
                    dbServers.getKey(url)));
            String urlDeploy = url + "/deploy";
            ResponseEntity<String> result = restTemplate.postForEntity(urlDeploy,
                    requestBody, String.class);

            if (result.getStatusCode() == HttpStatus.OK) {
                url += "/" + app.getAppName();
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
