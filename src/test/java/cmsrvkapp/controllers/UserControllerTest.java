package cmsrvkapp.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import cmsrvkapp.authorization.service.UserService;
import cmsrvkapp.authorization.views.AuthorisationView;
import cmsrvkapp.authorization.views.UserView;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TestRestTemplate restTemplate;


    @SuppressWarnings("all")
    @Test
    public void successfulRegister() {
        register();
    }

    private void register(){
        final UserView newUser = new UserView("Bob@mail.ru", "Bob", "02103452");
        final HttpEntity<UserView> httpEntity = new HttpEntity<>(newUser);
        final ResponseEntity<UserView> registerResponce = restTemplate.exchange("/api/users/",
                HttpMethod.POST, httpEntity, UserView.class);
        assertEquals(newUser.getEmail(), registerResponce.getBody().getEmail());
        assertEquals(newUser.getLogin(), registerResponce.getBody().getLogin());
    }

    @Test
    public void userExistRegister() {
        final UserView newUser = new UserView("Odin@mail.ru", "Odin", "02103452");
        doThrow(new DuplicateKeyException("")).when(userService).addUser(eq(newUser));

        final HttpEntity<UserView> httpEntity = new HttpEntity<>(newUser);
        final ResponseEntity<String> registerResponce = restTemplate.exchange("/api/users/",
                HttpMethod.POST, httpEntity, String.class);
        assertEquals("{\"status\":4,\"response\":\"User already exists!\"}", registerResponce.getBody());
        verify(userService).addUser(eq(newUser));
    }

    @Test
    public void wrongEmailRegister() {
        final UserView newUser = new UserView("Odinz", "Odin", "02103452");
        final HttpEntity<UserView> httpEntity = new HttpEntity<>(newUser);
        final ResponseEntity<String> registerResponce = restTemplate.exchange("/api/users/",
                HttpMethod.POST, httpEntity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, registerResponce.getStatusCode());
    }

    @Test
    public void successfulLogin() {
        login();
    }

    @SuppressWarnings("all")
    private List<String> login() {
        final UserView currentUser = new UserView("Alik@ad.com", "Alik", passwordEncoder.encode("13213"));
        when(userService.getByLoginOrEmail(eq("Alik"))).thenReturn(currentUser);

        final AuthorisationView user = new AuthorisationView("Alik", "13213");
        final HttpEntity<AuthorisationView> httpEntity = new HttpEntity<>(user);
        final ResponseEntity<UserView> responseEntity = restTemplate.exchange("/api/login/",
                HttpMethod.POST, httpEntity, UserView.class);
        assertEquals(user.getLoginEmail(), responseEntity.getBody().getLogin());
        verify(userService).getByLoginOrEmail(eq("Alik"));

        final List<String> coockies = responseEntity.getHeaders().get("Set-Cookie");
        assertNotNull(coockies);
        assertFalse(coockies.isEmpty());


        return coockies;
    }


    @Test
    public void info() {
        final List<String> coockies = login();
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.put(HttpHeaders.COOKIE, coockies);
        final HttpEntity<Void> requestEntity = new HttpEntity<>(requestHeaders);

        final ResponseEntity<UserView> responseEntity = restTemplate.exchange("/api/info/",
                HttpMethod.GET, requestEntity, UserView.class);
        assertNotNull(responseEntity.getBody());
        assertEquals("Alik", responseEntity.getBody().getLogin());
    }



    @Test
    public void wrongLoginOrEmailLogin() {
        doThrow(new DataAccessException("") {}).when(userService).getByLoginOrEmail(eq("bobi"));

        final AuthorisationView user = new AuthorisationView("bobi", "qwerty");
        final HttpEntity<AuthorisationView> httpEntity = new HttpEntity<>(user);
        final ResponseEntity<String> responseEntity = restTemplate.exchange("/api/login/",
                HttpMethod.POST, httpEntity, String.class);
        assertEquals("{\"status\":1,\"response\":\"Wrong login/email or password!\"}", responseEntity.getBody());
        verify(userService).getByLoginOrEmail(eq("bobi"));
    }

    @SuppressWarnings("all")
    @Test
    public void successfulChangeUser() {
        final List<String> coockies = login();

        final UserView changeUser = new UserView("adaw@bb.com", "Alik", "qwerty");
        when(userService.changeUser(eq(changeUser))).thenReturn(changeUser);

        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.put(HttpHeaders.COOKIE, coockies);
        final HttpEntity<UserView> httpEntity = new HttpEntity<>(changeUser, requestHeaders);
        final ResponseEntity<UserView> responseEntity = restTemplate.exchange("/api/users/Alik/",
                HttpMethod.POST, httpEntity, UserView.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(changeUser.getEmail(), responseEntity.getBody().getEmail());
        assertEquals(changeUser.getLogin(), responseEntity.getBody().getLogin());

        verify(userService).changeUser(eq(changeUser));
    }

    @Test
    public void unsuccessfulChangeUser(){
        final List<String> coockies = login();
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.put(HttpHeaders.COOKIE, coockies);
        final UserView changeUser = new UserView("bobi@bb.com", "bobi", "qwerty");
        final HttpEntity<UserView> httpEntity = new HttpEntity<>(changeUser, requestHeaders);
        final ResponseEntity<String> responseEntity = restTemplate.exchange("/api/users/bobi/",
                HttpMethod.POST, httpEntity, String.class);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

}