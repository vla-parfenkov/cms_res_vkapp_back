package cmsrvkapp.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import cmsrvkapp.authorization.service.UserService;
import cmsrvkapp.authorization.views.UserView;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;


    private UserView testUser;

    @Before
    public void setUp(){
        testUser = new UserView("yaho@bb.com", "yaho", "qwerty");
    }


    @Test(expected = DuplicateKeyException.class)
    public void addUser() throws Exception {
        userService.addUser(testUser);

        final UserView newUser = new UserView("yaho@bb.com", "bob", "12345");
        userService.addUser(newUser);

    }

    @Test
    public void getByLogin(){
        final UserView returnUser = userService.getByLoginOrEmail("yaho");
        assertNotNull(returnUser);
        assertEquals(returnUser.getEmail(), testUser.getEmail());
        assertEquals(returnUser.getLogin(), testUser.getLogin());
        assertEquals(returnUser.getPassword(), testUser.getPassword());

    }

    @Test
    public void getByEmail(){
        final UserView returnUser = userService.getByLoginOrEmail("yaho@bb.com");
        assertNotNull(returnUser);
        assertEquals(returnUser.getEmail(), testUser.getEmail());
        assertEquals(returnUser.getLogin(), testUser.getLogin());
        assertEquals(returnUser.getPassword(), testUser.getPassword());

    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void getByNotExistUser(){
        userService.getByLoginOrEmail("bob");
        userService.getByLoginOrEmail("bob@gmail.com");
    }

    @Test
    public void changeUser(){
        final UserView changeUser = new UserView("adaw67@bb.com", "Bred", "qwerty123");
        final UserView returnUser  = userService.changeUser(changeUser);
        assertNotNull(returnUser);
        assertEquals(returnUser.getEmail(), changeUser.getEmail());
        assertEquals(returnUser.getLogin(), changeUser.getLogin());
        assertEquals(returnUser.getPassword(), changeUser.getPassword());

    }


    @Test
    public void changeNotExistUser(){
        assertEquals(userService.changeUser(new UserView("bobi@bb.com", "bobi", "qwerty")), null);
    }

}