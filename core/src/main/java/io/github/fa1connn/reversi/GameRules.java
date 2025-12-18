package io.github.fa1connn.reversi;

public class GameRules {

    // 8 Yönü Temsil Eden Dizi: {Satır Değişimi, Sütun Değişimi}
    // Örnek: {-1, 0} Yukarı, {1, 1} Sağ Alt Çapraz
    private static final int[][] DIRECTIONS = {
        {-1, 0}, {1, 0}, {0, -1}, {0, 1},   // Dikey ve Yatay
        {-1, -1}, {-1, 1}, {1, -1}, {1, 1}  // Çaprazlar
    };

    /**
     * Bir hamlenin geçerli olup olmadığını kontrol eder.
     * @param board Oyun tahtası
     * @param player Hamleyi yapan oyuncu (Siyah veya Beyaz)
     * @param row Satır
     * @param col Sütun
     * @return Geçerli ise true, değilse false
     */
    public static boolean isValidMove(Board board, CellState player, int row, int col) {
        // 1. Kural: Hücre zaten doluysa oraya taş konamaz.
        if (board.getCell(row, col) != CellState.EMPTY) {
            return false;
        }

        // 2. Kural: En az bir yönde rakip taşları kıstırıyor mu?
        for (int[] dir : DIRECTIONS) {
            if (canCaptureInDirection(board, player, row, col, dir[0], dir[1])) {
                return true; // Tek bir yön bile tutsa yeterli
            }
        }

        return false;
    }

    /**
     * Belirtilen yönde taş kıstırma mümkün mü diye bakar.
     */
    private static boolean canCaptureInDirection(Board board, CellState player, int startRow, int startCol, int dRow, int dCol) {
        CellState opponent = player.getOpponent();
        int r = startRow + dRow;
        int c = startCol + dCol;
        boolean foundOpponent = false;

        // Tahta sınırları içinde kaldığımız sürece ilerle
        while (board.isInsideBoard(r, c)) {
            CellState current = board.getCell(r, c);

            if (current == opponent) {
                // Rakip taşı bulduk, ilerlemeye devam et
                foundOpponent = true;
            } else if (current == player) {
                // Kendi taşımızı bulduk!
                // Eğer arada rakip taşlar varsa (foundOpponent == true), bu geçerli bir kıstırmadır.
                return foundOpponent;
            } else {
                // Boş kareye geldik, kıstırma başarısız.
                return false;
            }

            // Bir adım daha ileri git
            r += dRow;
            c += dCol;
        }

        return false; // Tahta dışına çıktık
    }

    /**
     * Hamleyi gerçekleştirir: Taşı koyar ve aradaki rakip taşları çevirir.
     * Not: Bu metodu çağırmadan önce isValidMove ile kontrol yapılmış olmalıdır.
     */
    public static void makeMove(Board board, CellState player, int row, int col) {
        // 1. Taşı koy
        board.setCell(row, col, player);

        // 2. 8 Yöne bak ve çevrilecek taşları çevir
        for (int[] dir : DIRECTIONS) {
            int dRow = dir[0];
            int dCol = dir[1];

            // Eğer bu yönde kıstırma varsa, taşları çevir
            if (canCaptureInDirection(board, player, row, col, dRow, dCol)) {
                flipStones(board, player, row, col, dRow, dCol);
            }
        }
    }

    /**
     * Belirtilen yöndeki rakip taşları bizim rengimize çevirir.
     */
    private static void flipStones(Board board, CellState player, int r, int c, int dRow, int dCol) {
        CellState opponent = player.getOpponent();
        r += dRow;
        c += dCol;

        // Rakip taşları gördüğümüz sürece ilerle ve onları bizim rengimize yap
        while (board.getCell(r, c) == opponent) {
            board.setCell(r, c, player);
            r += dRow;
            c += dCol;
        }
    }

    /**
     * Oyuncunun yapabileceği herhangi bir hamle var mı?
     * (Pas geçme durumunu kontrol etmek için)
     */
    public static boolean hasValidMove(Board board, CellState player) {
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                if (isValidMove(board, player, r, c)) {
                    return true;
                }
            }
        }
        return false;
    }
}
