package io.github.fa1connn.reversi;

//Possible conditions for cells
public enum CellState {
    EMPTY,
    BLACK,
    WHITE;

    //Change to opponent version
    public CellState getOpponent() {
        if (this == BLACK) {
            return WHITE;
        }
        if (this == WHITE) {
            return BLACK;
        }
        return EMPTY;
    }
}
