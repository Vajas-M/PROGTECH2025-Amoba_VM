

package init;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import domain.Board;
import domain.Board;
import service.ConsoleService;

public class BoardInit {
    private final ConsoleService console;
    private final String playerName;
    private final String aiName;

    public BoardInit(ConsoleService console, String playerName, String aiName) {
        this.console = console;
        this.playerName = playerName;
        this.aiName = aiName;
    }

    public Board initBoard() {
        while (true) {
            int option = console.readInt(
                    "1 = fájlból betöltés, 2 = új játék, 3 = highscore lista, 0 = kilépés");

            switch (option) {
                case 1 -> {
                    return loadFromFile();
                }
                case 2 -> {
                    return createNewBoard();
                }
                case 3 -> {
                    showHighScores();
                }
                case 0 -> {
                    console.print("Kilépés...");
                    return null;
                }
                default -> console.print("Érvénytelen menüpont!");
            }
        }
    }

    private Board loadFromFile() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("amoba_save.txt"));
            int rows = Integer.parseInt(lines.get(2).split(":")[1].trim());
            int cols = Integer.parseInt(lines.get(3).split(":")[1].trim());

            Board board = new Board(rows, cols);

            for (int r = 0; r < rows; r++) {
                String line = lines.get(r + 5).substring(2).replace(" ", "");
                for (int c = 0; c < cols; c++) {
                    board.getCells()[r][c] = line.charAt(c);
                }
            }

            console.print("Mentett játék betöltve.");
            return board;

        } catch (Exception e) {
            console.print("Hiba a fájl beolvasásakor, üres 10x10 pálya készül.");
            return new Board(10, 10);
        }
    }

    private Board createNewBoard() {
        int rows;
        do {
            rows = console.readInt("Add meg a sorok számát (4-25):");
            if (rows < 4 || rows > 25) {
                console.print("Hibás érték!");
            }
        } while (rows < 4 || rows > 25);

        int cols;
        do {
            cols = console.readInt("Add meg az oszlopok számát (4-25):");
            if (cols < 4 || cols > 25) {
                console.print("Hibás érték!");
            }
        } while (cols < 4 || cols > 25);

        return new Board(rows, cols);
    }

    private void showHighScores() {
        service.HighScoreService highScoreService = new service.HighScoreService();

        console.print("=== HIGHSCORE LISTA ===");

        List<String> scores = highScoreService.getHighScoreList();
        if (scores.isEmpty()) {
            console.print("Még nincs rögzített pontszám.");
            return;
        }

        int i = 1;
        for (String s : scores) {
            console.print(i + ". " + s);
            i++;
        }
    }
}
