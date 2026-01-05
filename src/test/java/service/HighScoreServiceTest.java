package service;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HighScoreServiceTest {

    private HighScoreService service;
    private static final String DB_URL = "jdbc:sqlite:highscore.db";

    @BeforeEach
    void setUp() throws Exception {
        service = new HighScoreService();

        // adatbázis ürítése minden teszt előtt
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM scores");
        }
    }

    @Test
    void testRecordWin_NewPlayer() {
        service.recordWin("Anna");

        String champion = service.getChampion();
        assertEquals("Anna (10 pont)", champion);
    }

    @Test
    void testRecordDraw_NewPlayer() {
        service.recordDraw("Bela");

        String champion = service.getChampion();
        assertEquals("Bela (5 pont)", champion);
    }

    @Test
    void testMultipleWinsSamePlayer() {
        service.recordWin("Csaba");
        service.recordWin("Csaba");

        String champion = service.getChampion();
        assertEquals("Csaba (20 pont)", champion);
    }

    @Test
    void testWinAndDrawSamePlayer() {
        service.recordWin("Dora");
        service.recordDraw("Dora");

        String champion = service.getChampion();
        assertEquals("Dora (15 pont)", champion);
    }

    @Test
    void testChampionWithMultiplePlayers() {
        service.recordWin("Eva");    // 10 pont
        service.recordDraw("Ferenc"); // 5 pont
        service.recordWin("Eva");    // +10 pont

        String champion = service.getChampion();
        assertEquals("Eva (20 pont)", champion);
    }

    @Test
    void testGetChampion_NoData() {
        String champion = service.getChampion();
        assertEquals("Nincs adat", champion);
    }
}
