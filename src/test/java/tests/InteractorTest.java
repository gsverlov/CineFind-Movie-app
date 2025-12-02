package tests;

import controllers.AddFavoriteInteractor;
import controllers.UpdateSearchHistoryInteractor;
import exceptions.UsernameTakenException;
import models.Movie;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class InteractorTest {

    @BeforeEach
    public void clearUsers() throws Exception {
        Field userMapField = User.class.getDeclaredField("userMap");
        userMapField.setAccessible(true);
        ((Map) userMapField.get(null)).clear();

        Field passwordMapField = User.class.getDeclaredField("userPasswordMap");
        passwordMapField.setAccessible(true);
        ((Map) passwordMapField.get(null)).clear();
    }

    @Test
    public void testSearchHistoryLimit() throws UsernameTakenException {
        User user = new User("Tester", "123");
        UpdateSearchHistoryInteractor interactor = new UpdateSearchHistoryInteractor();

        // 模擬搜尋 6 部電影
        for (int i = 1; i <= 6; i++) {
            Movie m = new Movie("Movie" + i, "202" + i, "id" + i, "movie", "url");
            interactor.execute(user, m);
        }

        List<Movie> history = user.getSearchHistory();

        // 驗證數量限制為 5
        assertEquals(5, history.size());

        // 驗證最後搜尋的在最前面 (Stack行為)
        assertEquals("id6", history.get(0).imdbID);

        // 驗證最早的被移除了 (id1 應該不在清單內)
        boolean hasId1 = history.stream().anyMatch(m -> m.imdbID.equals("id1"));
        assertFalse(hasId1);
    }

    @Test
    public void testAddFavoriteInteractor() throws UsernameTakenException {
        User user = new User("FavTester", "123");
        Movie m = new Movie("Matrix", "1999", "tt0133093", "movie", "poster");

        AddFavoriteInteractor interactor = new AddFavoriteInteractor();

        // 第一次加入應該成功
        boolean result1 = interactor.execute(user, m);
        assertTrue(result1);
        assertEquals(1, user.getFavorites().size());

        // 第二次加入應該失敗 (重複)
        boolean result2 = interactor.execute(user, m);
        assertFalse(result2);
        assertEquals(1, user.getFavorites().size());
    }
}