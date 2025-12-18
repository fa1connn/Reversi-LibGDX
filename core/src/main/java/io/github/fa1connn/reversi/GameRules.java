package io.github.fa1connn.reversi;

public class GameRules {

    //Row and column
    private static final int[][] DIRECTIONS = {
        {-1, 0}, {1, 0}, {0, -1}, {0, 1},
        {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
    };

    //Board = game board, player = Black or White
    public static boolean isValidMove(Board board, CellState player, int row, int col) {
        //Check if it is not EMPTY
        if (board.getCell(row, col) != CellState.EMPTY) {
            return false;
        }

        //Has taken at least one token of the opposite color between tokens of its own color
        for (int[] dir : DIRECTIONS) {
            if (canCaptureInDirection(board, player, row, col, dir[0], dir[1])) {
                return true; //One is enough
            }
        }
        return false;
    }

    //Is it possible to take in between
    private static boolean canCaptureInDirection(Board board, CellState player, int startRow, int startCol, int dRow, int dCol) {
        CellState opponent = player.getOpponent();
        int r = startRow + dRow;
        int c = startCol + dCol;
        boolean foundOpponent = false;

        //Bound is our board
        while (board.isInsideBoard(r, c)) {
            CellState current = board.getCell(r, c);

            if (current == opponent) {
                //Opponent token different from player's token
                foundOpponent = true;
            }
            else if (current == player) {
                //Same token as player
                return foundOpponent;
            }
            else {
                //EMPTY cell
                return false;
            }

            //One more step
            r += dRow;
            c += dCol;
        }
        //Out of bounds
        return false;
    }

    //Puts the token and change colors which are taken in between
    public static void makeMove(Board board, CellState player, int row, int col) {
        board.setCell(row, col, player);

        //8 direction
        for (int[] dir : DIRECTIONS) {
            int dRow = dir[0];
            int dCol = dir[1];

            //If it is taken in between change
            if (canCaptureInDirection(board, player, row, col, dRow, dCol)) {
                flipStones(board, player, row, col, dRow, dCol);
            }
        }
    }

    //If it is taken in between change
    private static void flipStones(Board board, CellState player, int r, int c, int dRow, int dCol) {
        CellState opponent = player.getOpponent();
        r += dRow;
        c += dCol;

        //Unless unseen opponent's token change the color
        while (board.getCell(r, c) == opponent) {
            board.setCell(r, c, player);
            r += dRow;
            c += dCol;
        }
    }

    //It checks if there is a valid move or not
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
