package io.github.fa1connn.reversi;

public class Board {
    //Standard
    public static final int SIZE = 8;

    //An array holding the tokens on the board
    private CellState[][] grid;

    public Board() {
        grid = new CellState[SIZE][SIZE];
        reset();
    }

    public void reset() {
        //Clear all cells
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                grid[row][col] = CellState.EMPTY;
            }
        }

        //Initial positions
        grid[3][3] = CellState.WHITE;
        grid[4][4] = CellState.WHITE;
        grid[3][4] = CellState.BLACK;
        grid[4][3] = CellState.BLACK;
    }

    //Condition of token
    public CellState getCell(int row, int col) {
        // Instead of giving an error return EMPTY
        if (!isInsideBoard(row, col)) {
            return CellState.EMPTY;
        }
        return grid[row][col];
    }

    //Putting tokens
    public void setCell(int row, int col, CellState state) {
        if (isInsideBoard(row, col)) {
            grid[row][col] = state;
        }
    }

    //Check if it is not inside
    public boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }
}
