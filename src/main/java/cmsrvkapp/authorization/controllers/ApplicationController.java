package cmsrvkapp.authorization.controllers;

import cmsrvkapp.authorization.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import cmsrvkapp.authorization.views.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@SuppressWarnings({"SpringAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/api")
@Validated
public class ApplicationController {

    @Autowired
    private ApplicationService dbApplications;

    @RequestMapping(method = RequestMethod.GET, path = "/{appName}/config")
    public ResponseEntity getConfig(@PathVariable(value = "appName") String appName,
                                    @RequestHeader(value = "accept", required = false) String accept,
                                    HttpSession httpSession) {
        try {
            ApplicationView app = dbApplications.getByName(appName);
            String config = dbApplications.getConfig(app);
            return ResponseEntity.status(HttpStatus.OK).body(config);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseView.ERROR_USER_NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }

    }


    @RequestMapping(method = RequestMethod.POST, path = "/{appName}/config")
    public ResponseEntity setConfig(@Valid @RequestBody  String config,
                                    @PathVariable(value = "appName") String appName,
                                    @RequestHeader(value = "Accept", required = false) String accept,
                                    HttpSession httpSession) {
        try {
            ApplicationView app = dbApplications.getByName(appName);
            dbApplications.setConfig(app, config);
            return ResponseEntity.status(HttpStatus.OK).body(dbApplications.getConfig(app));
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseView.ERROR_USER_NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{addName}/downloadJS")
    public ResponseEntity dowloadJS(@PathVariable(value = "appId") Integer appId,
                                    HttpSession httpSession) {
        return null;
    }

}
