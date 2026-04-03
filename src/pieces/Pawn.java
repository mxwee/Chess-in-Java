package pieces;

import main.Board;
import main.Move;
import java.util.*;

public class Pawn extends Piece {
    public Pawn(boolean white) {
        super(white, "/images/" + (white ? "white_pawn.png" : "black_pawn.png"));
    }


    @Override
    public List<Move> generateMoves(int r, int c, Board board) {
        List<Move> moves = new ArrayList<>();
        int dir = white ? -1 : 1;
        int tr = r + dir;

        // single forward
        if (Board.inBounds(tr, c) && board.get(tr, c) == null) {
            boolean promo = (white && tr == 0) || (!white && tr == 7);
            moves.add(new Move(r, c, tr, c, this, null, false, false, promo));
            // double step
            int startRow = white ? 6 : 1;
            if (r == startRow) {
                int tr2 = r + 2*dir;
                if (Board.inBounds(tr2, c) && board.get(tr2, c) == null)
                    moves.add(new Move(r, c, tr2, c, this, null, false, false, false));
            }
        }

        // captures
        for (int dc : new int[]{-1,1}) {
            int tc = c + dc;
            if (Board.inBounds(tr, tc)) {
                Piece p = board.get(tr, tc);
                if (p != null && p.isWhite() != this.isWhite()) {
                    boolean promo = (white && tr == 0) || (!white && tr == 7);
                    moves.add(new Move(r, c, tr, tc, this, p, false, false, promo));
                }
            }
        }

        // en passant
        if (board.lastDoubleStepPawn != null) {
            int lr = board.lastDoubleStepPawn[0], lc = board.lastDoubleStepPawn[1];
            if (r == lr && Math.abs(lc - c) == 1) { // adjacent pawn that double stepped
                int epRow = r + dir;
                if (Board.inBounds(epRow, lc))
                    moves.add(new Move(r, c, epRow, lc, this, board.get(lr, lc), true, false, false));
            }
        }
        return moves;
    }
}
