package tests;

import exceptions.*;
import models.Movie;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @BeforeEach
    public void resetUserDatabase() throws Exception {
        // 清空 User 類別中的 static map
        Field userMapField = User.class.getDeclaredField("userMap");
        userMapField.setAccessible(true);
        ((Map) userMapField.get(null)).clear();

        Field passwordMapField = User.class.getDeclaredField("userPasswordMap");
        passwordMapField.setAccessible(true);
        ((Map) passwordMapField.get(null)).clear();
    }

    @Test
    public void testCreateUser() throws Exception {
        User u = new User("Adam", "1234");
        assertEquals("Adam", u.getUsername());
        // 這裡加上 throws Exception 就解決了 Screenshot 3 的紅字
        assertTrue(User.checkValidUser("Adam"));
    }

    @Test
    public void testDuplicateUserThrowsException() {
        assertThrows(UsernameTakenException.class, () -> {
            new User("Adam", "1234");
            new User("Adam", "5678");
        });
    }

    @Test
    public void testChangePassword() throws Exception {
        User u = new User("Adam", "1234");
        u.changePassword("newPass", "newPass");
        Map<String, String> passMap = User.getUserPasswordMap();
        assertEquals("newPass", passMap.get("Adam"));
    }

    @Test
    public void testChangePasswordMismatch() throws Exception {
        User u = new User("Adam", "1234");
        assertThrows(PasswordsNotEqualException.class, () -> {
            u.changePassword("passA", "passB");
        });
    }

    @Test
    public void testChangeUsername() throws Exception {
        User u = new User("Adam", "1234");
        u.changeUsername("Bob");

        assertEquals("Bob", u.getUsername());

        assertFalse(User.checkValidUser("Adam"));

        assertTrue(User.checkValidUser("Bob"));
    }

    @Test
    public void testFavorites() throws Exception {
        User u = new User("Adam", "1234");
        Movie m = new Movie("Inception", "2010", "tt123", "movie", "url");

        u.favoriteMovie(m);
        assertTrue(u.getFavorites().contains(m));

        assertThrows(MovieAlreadyFavoritedException.class, () -> {
            u.favoriteMovie(m);
        });

        u.unfavoriteMovie(m);
        assertFalse(u.getFavorites().contains(m));
    }
}