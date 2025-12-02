package tests;

import controllers.LoginManager;
import exceptions.UserNotFoundException;
import exceptions.UsernameTakenException;
import exceptions.WrongPasswordException;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LoginManagerTest {

    private LoginManager loginManager;

    @BeforeEach
    public void setUp() throws Exception {
        // 同樣需要清空 User static 資料
        Field userMapField = User.class.getDeclaredField("userMap");
        userMapField.setAccessible(true);
        ((Map) userMapField.get(null)).clear();

        Field passwordMapField = User.class.getDeclaredField("userPasswordMap");
        passwordMapField.setAccessible(true);
        ((Map) passwordMapField.get(null)).clear();

        loginManager = new LoginManager();
    }

    @Test
    public void testCreateAndLoginSuccess() throws Exception {
        loginManager.createAccount("Garry", "password123");

        assertFalse(loginManager.isLoggedIn());

        loginManager.login("Garry", "password123");
        assertTrue(loginManager.isLoggedIn());
        assertEquals("Garry", loginManager.getLoggedInUser().getUsername());
    }

    @Test
    public void testLoginUserNotFound() {
        assertThrows(UserNotFoundException.class, () -> {
            loginManager.login("Ghost", "123");
        });
    }

    @Test
    public void testLoginWrongPassword() throws UsernameTakenException {
        loginManager.createAccount("Garry", "correct");
        assertThrows(WrongPasswordException.class, () -> {
            loginManager.login("Garry", "wrong");
        });
    }

    @Test
    public void testLogout() throws Exception {
        loginManager.createAccount("User", "Pass");
        loginManager.login("User", "Pass");

        assertTrue(loginManager.isLoggedIn());
        loginManager.logout();
        assertFalse(loginManager.isLoggedIn());
        assertNull(loginManager.getLoggedInUser());
    }
}