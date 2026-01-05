package init;

import domain.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ConsoleService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BoardInitTest {

    private ConsoleService consoleMock;
    private BoardInit boardInit;

    @BeforeEach
    void setUp() {
        consoleMock = mock(ConsoleService.class);
        // Javítás: Átadjuk a hiányzó név paramétereket is
        boardInit = new BoardInit(consoleMock, "Játékos", "Gép");
    }

    @Test
    void testInitBoardExit() {
        when(consoleMock.readInt(anyString())).thenReturn(0);

        Board result = boardInit.initBoard();

        assertNull(result);
        verify(consoleMock).print("Kilépés...");
    }

    @Test
    void testCreateNewBoardValidSize() {
        when(consoleMock.readInt(anyString()))
                .thenReturn(2)   // Menü: új játék
                .thenReturn(10)  // Sorok száma
                .thenReturn(15); // Oszlopok száma

        Board board = boardInit.initBoard();

        assertNotNull(board);
        // Javítás: A Board struktúrájának megfelelő ellenőrzés
        assertEquals(10, board.getCells().length);
        assertEquals(15, board.getCells()[0].length);
    }

    @Test
    void testCreateNewBoardInvalidThenValid() {
        when(consoleMock.readInt(anyString()))
                .thenReturn(2)   // Új játék
                .thenReturn(2)   // Hibás sorok (túl kicsi)
                .thenReturn(6)   // Jó sorok
                .thenReturn(30)  // Hibás oszlopok (túl nagy)
                .thenReturn(8);  // Jó oszlopok

        Board board = boardInit.initBoard();

        assertNotNull(board);
        assertEquals(6, board.getCells().length);
        assertEquals(8, board.getCells()[0].length);

        // Ellenőrizzük, hogy a hibaüzenet megjelent-e a konzolon
        verify(consoleMock, atLeastOnce()).print("Hibás érték!");
    }

    @Test
    void testInvalidMenuOptionThenExit() {
        when(consoleMock.readInt(anyString()))
                .thenReturn(99)  // Érvénytelen menüpont
                .thenReturn(0);   // Kilépés

        boardInit.initBoard();

        verify(consoleMock).print("Érvénytelen menüpont!");
    }

    @Test
    void testShowHighScores_EmptyList() {
        // Menü: 3 (Highscore), majd 0 (Kilépés a ciklusból)
        when(consoleMock.readInt(anyString())).thenReturn(3, 0);

        // a teszt eredménye attól függ, van-e fájl a gépen.reService-t nem
        // Ha nincs fájl, ezt kell látnunk:
        boardInit.initBoard();

        // Ellenőrizzük a fejlécet
        verify(consoleMock).print("=== HIGHSCORE LISTA ===");
    }

    @Test
    void testShowHighScores_FlowCheck() {
        // Ellenőrizzük, hogy a menüválasztás után visszatér-e a főmenübe (ciklus teszt)
        when(consoleMock.readInt(anyString()))
                .thenReturn(3) // 1. kör: Megnézzük a listát
                .thenReturn(0); // 2. kör: Kilépünk

        boardInit.initBoard();

        // Legalább egyszer meg kell hívódnia a listázásnak
        verify(consoleMock, atLeastOnce()).print(contains("HIGHSCORE"));
        // A kilépésnek is meg kell történnie a végén
        verify(consoleMock).print("Kilépés...");
    }
}