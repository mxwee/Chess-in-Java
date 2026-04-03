package main;

import pieces.Piece;

public class Move {
    public final int sr, sc, tr, tc;
    public final Piece moved;
    public final Piece captured;
    public final boolean isEnPassant;
    public final boolean isCastling;
    public final boolean isPromotion; // true when pawn promotes

    public Piece promoteTo;

    public Move(int sr, int sc, int tr, int tc, Piece moved, Piece captured,
                boolean isEnPassant, boolean isCastling, boolean isPromotion) {
        this.sr = sr; this.sc = sc; this.tr = tr; this.tc = tc;
        this.moved = moved; this.captured = captured;
        this.isEnPassant = isEnPassant;
        this.isCastling = isCastling;
        this.isPromotion = isPromotion;
        this.promoteTo = null;
    }

    public String toString() {
        return String.format("%s (%d,%d)->(%d,%d)%s",
                moved.getClass().getSimpleName(), sr, sc, tr, tc,
                isPromotion ? " promote" : "");
    }

}
