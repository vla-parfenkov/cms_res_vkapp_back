package cmsrvkapp.authorization.controllers;

import cmsrvkapp.authorization.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import cmsrvkapp.authorization.service.UserService;
import cmsrvkapp.authorization.views.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
@RestController
@CrossOrigin(origins = { "https://cmsvkapp-test.herokuapp.com",
        "https://cmsvkapp-admin.herokuapp.com",
        "http://localhost:8080",
        "http://localhost:9000" },
        allowCredentials = "true")
@RequestMapping(path = "/api/users")
@Validated
public class UserController {

    @Autowired
    private UserService dbUsers;

    @Autowired
    private ApplicationService dbApplications;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String CURRENT_USER_KEY = "currentUser";

    @RequestMapping(method = RequestMethod.GET, path = "info")
    public ResponseEntity info(HttpSession httpSession) {
        final String currentUser = (String) httpSession.getAttribute(CURRENT_USER_KEY);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.OK).body(ResponseView.ERROR_NOT_LOGGED_IN);
        }
        try {
            UserView user = dbUsers.getByLoginOrEmail(currentUser);
            user.setPassword(null);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (DataAccessException ex) {
            httpSession.setAttribute(CURRENT_USER_KEY, null);
            return ResponseEntity.status(HttpStatus.OK).body(ResponseView.ERROR_NOT_LOGGED_IN);
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "login", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login(@Valid @RequestBody AuthorisationView loggingData, HttpSession httpSession) {
        try {
            UserView currentUser = dbUsers.getByLoginOrEmail(loggingData.getLoginEmail());

            if (passwordEncoder.matches(loggingData.getPassword(), currentUser.getPassword())) {
                httpSession.setAttribute(CURRENT_USER_KEY, currentUser.getLogin());
                currentUser.setPassword(null);
                return ResponseEntity.status(HttpStatus.OK).body(currentUser);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseView.ERROR_BAD_LOGIN_DATA);
        } catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseView.ERROR_BAD_LOGIN_DATA);
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "logout")
    public ResponseEntity<ResponseView> logout(HttpSession httpSession) {
        httpSession.setAttribute(CURRENT_USER_KEY, null);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseView.SUCCESS_LOGOUT);
    }

    @RequestMapping(method = RequestMethod.POST, path = "", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity register(@Valid @RequestBody UserView registerData) {
        try {
            String encodedPassword = passwordEncoder.encode(registerData.getPassword());
            registerData.setPassword(encodedPassword);
            dbUsers.addUser(registerData);
        } catch (DuplicateKeyException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseView.ERROR_USER_ALREADY_EXISTS);
        }
        registerData.setPassword(null);

        return ResponseEntity.status(HttpStatus.CREATED).body(registerData);
    }

    @RequestMapping(method = RequestMethod.POST, path = "{changedUser}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity change(@Valid @RequestBody UserView newData,
                                 @PathVariable(value = "changedUser") String changedUser,
                                 HttpSession httpSession) {
        final String currentUser = (String) httpSession.getAttribute(CURRENT_USER_KEY);
        if (currentUser.equals(changedUser)) {
            try {
                UserView oldUser = dbUsers.getByLoginOrEmail(changedUser);
                if (newData.getEmail() != null) {
                    oldUser.setEmail(newData.getEmail());
                }
                if (newData.getPassword() != null) {
                    String encodedPassword = passwordEncoder.encode(newData.getPassword());
                    oldUser.setPassword(encodedPassword);
                }
                dbUsers.changeUser(oldUser);
                return ResponseEntity.status(HttpStatus.OK).body(oldUser);
            } catch (DataAccessException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseView.ERROR_USER_NOT_FOUND);
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseView.ERROR_NO_RIGHTS_TO_CHANGE_USER);
    }

    @RequestMapping(method = RequestMethod.GET, path = "appsInfo")
    public ResponseEntity appsInfo(HttpSession httpSession) {
        final String currentUser = (String) httpSession.getAttribute(CURRENT_USER_KEY);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.OK).body(ResponseView.ERROR_NOT_LOGGED_IN);
        }
        try {
            UserView user = dbUsers.getByLoginOrEmail(currentUser);
            return ResponseEntity.status(HttpStatus.OK).body(dbApplications.getByCreatorLogin(user.getLogin()));
        } catch (DataAccessException ex) {
            httpSession.setAttribute(CURRENT_USER_KEY, null);
            return ResponseEntity.status(HttpStatus.OK).body(ResponseView.ERROR_NOT_LOGGED_IN);
        }
    }

}
