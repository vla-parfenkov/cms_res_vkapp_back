package cmsrvkapp.authorization.controllers;

import cmsrvkapp.authorization.service.ApplicationService;
import cmsrvkapp.authorization.service.ServerService;
import cmsrvkapp.config.Generator;
import org.json.JSONObject;
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
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@SuppressWarnings({"SpringAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
@RestController
@CrossOrigin(origins = "*")
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
            dbApplications.addApplication(app);
            return ResponseEntity.status(HttpStatus.CREATED).body(app);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseView.ERROR_APP_NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
    }


    @RequestMapping(method = RequestMethod.POST, path = "{appName}/config")
    public ResponseEntity setConfig(@Valid @RequestBody  String config,
                                    @PathVariable(value = "appName") String appName,
                                    @RequestHeader(value = "Accept", required = false) String accept,
                                    HttpSession httpSession) {
        try {
            ApplicationView app = dbApplications.getByName(appName);
            dbApplications.setConfig(app, config);
            return ResponseEntity.status(HttpStatus.OK).body(dbApplications.getConfig(app));
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseView.ERROR_APP_NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "{appName}/downloadJSON")
    public ResponseEntity dowloadJSON(@PathVariable(value = "appName") String appName,
                                    HttpSession httpSession) {
        try {
            ApplicationView app = dbApplications.getByName(appName);
            String config = dbApplications.getConfig(app);

            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("template.json");

            String template = new BufferedReader(new InputStreamReader(is))
                    .lines().collect(Collectors.joining("\n"));

            JSONObject templateJSON = new JSONObject(template);
            JSONObject configJSON = new JSONObject(config);

            Generator.generate(configJSON, templateJSON);

            return ResponseEntity.status(HttpStatus.OK).body(templateJSON.toString());
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
            //final String currentUser = (String) httpSession.getAttribute(CURRENT_USER_KEY);
            //if (!currentUser.equals(app.getCreatorLogin())) {
            //    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseView.ERROR_NO_RIGHTS_TO_CHANGE_APP);
            //}
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<ApplicationView> requestBody = new HttpEntity<>(app);
            String url = dbServers.getUrl();
            String urlDeploy = url + "/" + app.getAppName() + "/deploy";
            ResponseEntity<ApplicationView> result = restTemplate.postForEntity(urlDeploy,
                    requestBody, ApplicationView.class);

            if (result.getStatusCode() == HttpStatus.OK) {
                dbServers.instanceWasAdded(url);
                return ResponseEntity.status(HttpStatus.OK).body(url);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseView.ERROR_DEPLOY);
            }
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseView.ERROR_APP_NOT_FOUND);
        }

    }

    @RequestMapping(method = RequestMethod.POST, path = "server")
    public ResponseEntity addServer(@Valid @RequestBody  String url,
                                    @RequestHeader(value = "Accept", required = false) String accept) {
        try {
            dbServers.addServer(url);
            return ResponseEntity.status(HttpStatus.OK).body(url);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

}
