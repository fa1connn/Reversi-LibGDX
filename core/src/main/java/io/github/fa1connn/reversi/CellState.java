package io.github.fa1connn.reversi;

/**
 * Reversi oyunundaki bir hücrenin olası durumlarını temsil eder.
 */
public enum CellState {
    EMPTY,  // Hücre boş
    BLACK,  // Hücrede Siyah taş var
    WHITE;  // Hücrede Beyaz taş var

    /**
     * Bu rengin tam tersini (rakibi) döndürür.
     * Siyahsa -> Beyaz, Beyazsa -> Siyah döner.
     */
    public CellState getOpponent() {
        if (this == BLACK) {
            return WHITE;
        }
        if (this == WHITE) {
            return BLACK;
        }
        return EMPTY; // Boşun rakibi yoktur
    }
}
