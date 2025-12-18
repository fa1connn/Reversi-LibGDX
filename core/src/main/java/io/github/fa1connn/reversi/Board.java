package io.github.fa1connn.reversi;

public class Board {
    // Reversi tahtası standart olarak 8x8 boyutundadır.
    public static final int SIZE = 8;

    // Tahtadaki taşları tutan iki boyutlu dizi (Matris)
    private CellState[][] grid;

    public Board() {
        // Tahtayı bellekte oluştur
        grid = new CellState[SIZE][SIZE];
        // Oyunu başlangıç pozisyonuna getir
        reset();
    }

    /**
     * Tahtayı temizler ve başlangıçtaki 4 taşı yerleştirir.
     */
    public void reset() {
        // 1. Tüm kareleri boşalt
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                grid[row][col] = CellState.EMPTY;
            }
        }

        // 2. Reversi Başlangıç Kurulumu (Ortadaki 4 taş)
        // Dizi indisleri 0'dan başlar. O yüzden 4. satır -> index 3 olur.

        // Sol Üst (3,3) -> Beyaz
        grid[3][3] = CellState.WHITE;
        // Sağ Alt (4,4) -> Beyaz
        grid[4][4] = CellState.WHITE;

        // Sağ Üst (3,4) -> Siyah
        grid[3][4] = CellState.BLACK;
        // Sol Alt (4,3) -> Siyah
        grid[4][3] = CellState.BLACK;
    }

    /**
     * Belirtilen satır ve sütundaki taşın durumunu döndürür.
     */
    public CellState getCell(int row, int col) {
        // Eğer tahta sınırları dışında bir yer istenirse EMPTY döndür (Hata vermesin)
        if (!isInsideBoard(row, col)) {
            return CellState.EMPTY;
        }
        return grid[row][col];
    }

    /**
     * Tahtaya taş koymak veya durumunu değiştirmek için kullanılır.
     */
    public void setCell(int row, int col, CellState state) {
        if (isInsideBoard(row, col)) {
            grid[row][col] = state;
        }
    }

    /**
     * Verilen koordinatın tahta sınırları içinde olup olmadığını kontrol eder.
     * @return Tahta içindeyse true, dışındaysa false.
     */
    public boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }
}
