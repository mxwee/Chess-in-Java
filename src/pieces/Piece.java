package pieces;

import main.Board;
import main.Move;
import javax.swing.*;

public abstract class Piece {
    protected boolean white;
    protected boolean hasMoved = false; // useful for castling & pawn first move
    protected ImageIcon icon;


    public Piece(boolean white, String imagePath) {
        this.white = white;
        if (imagePath != null) {
            java.net.URL url = getClass().getResource(imagePath);
            if (url != null) icon = new ImageIcon(url);
            else icon = null;
        }
    }

    public boolean isWhite() { return white; }
    public boolean hasMoved() { return hasMoved; }
    public void setMoved(boolean v) { hasMoved = v; }

    public ImageIcon getIcon() { return icon; }

    /** Generate pseudo-legal moves for this piece (may include moves that leave king in check). */
    public abstract java.util.List<Move> generateMoves(int r, int c, Board board);

}
