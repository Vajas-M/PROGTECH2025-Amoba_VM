package service;

import java.sql.*;

public class HighScoreService {

    private static final String DB_URL = "jdbc:sqlite:highscore.db";

    public HighScoreService() {
        initDb();
    }

    private void initDb() {
        String sql = """
            CREATE TABLE IF NOT EXISTS scores (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                player_name TEXT UNIQUE NOT NULL,
                wins INTEGER DEFAULT 0,
                draws INTEGER DEFAULT 0,
                points INTEGER DEFAULT 0
            )
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("DB init hiba: " + e.getMessage());
        }
    }

    // Győzelem rögzítése
    public void recordWin(String name) {
        upsert(name, 1, 0, 10);
    }

    // Döntetlen rögzítése
    public void recordDraw(String name) {
        upsert(name, 0, 1, 5);
    }

    private void upsert(String name, int win, int draw, int points) {
        String sql = """
            INSERT INTO scores(player_name, wins, draws, points)
            VALUES (?, ?, ?, ?)
            ON CONFLICT(player_name) DO UPDATE SET
                wins = wins + ?,
                draws = draws + ?,
                points = points + ?
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, win);
            ps.setInt(3, draw);
            ps.setInt(4, points);
            ps.setInt(5, win);
            ps.setInt(6, draw);
            ps.setInt(7, points);

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Pontszám mentési hiba: " + e.getMessage());
        }
    }

    // Bajnok lekérdezése
    public String getChampion() {
        String sql = """
            SELECT player_name, points
            FROM scores
            ORDER BY points DESC
            LIMIT 1
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getString("player_name") +
                        " (" + rs.getInt("points") + " pont)";
            }
        } catch (SQLException e) {
            return "Nincs adat";
        }
        return "Nincs adat";
    }
}

