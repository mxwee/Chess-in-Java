package pieces;

import main.Board;
import main.Move;
import java.util.*;

public class King extends Piece {
    public King(boolean white) {
        super(white, "/images/" + (white ? "white_king.png" : "black_king.png"));
    }


    @Override
    public List<Move> generateMoves(int r, int c, Board board) {
        List<Move> moves = new ArrayList<>();
        for (int dr=-1; dr<=1; dr++){
            for (int dc=-1; dc<=1; dc++){
                if (dr==0 && dc==0) continue;
                int rr = r+dr, cc = c+dc;
                if (!Board.inBounds(rr,cc)) continue;
                Piece p = board.get(rr,cc);
                if (p == null || p.isWhite() != this.isWhite())
                    moves.add(new Move(r,c,rr,cc,this,p,false,false,false));
            }
        }

        // Castling: only if not moved, rook exists, path empty, and squares not under attack
        if (!hasMoved) {
            // king side
            if (canCastle(r, c, r, c+3, board)) {
                moves.add(new Move(r,c,r,c+2,this,null,false,true,false));
            }
            // queen side
            if (canCastle(r, c, r, c-4, board)) {
                moves.add(new Move(r,c,r,c-2,this,null,false,true,false));
            }
        }

        return moves;
    }

    private boolean canCastle(int kr, int kc, int rr, int rc, Board board) {
        // rr,rc is expected rook pos; ensure inside
        if (!Board.inBounds(rr, rc)) return false;
        Piece rook = board.get(rr, rc);
        if (!(rook instanceof Rook) || rook.isWhite() != this.isWhite() || rook.hasMoved()) return false;
        int step = rc > kc ? 1 : -1;
        // path between king and rook must be empty
        for (int cc = kc + step; cc != rc; cc += step)
            if (board.get(kr, cc) != null) return false;

        // squares king crosses (including target) must not be under attack and king not in check
        Board copy = board.copy();
        // king must not currently be in check
        if (copy.isInCheck(this.isWhite())) return false;
        int curc = kc;
        for (int i=0;i<2;i++){
            curc += step;
            // simulate move
            copy.moveNoValidation(kr, kc, kr, curc);
            if (copy.isInCheck(this.isWhite())) return false;
            // revert by copying original board for next iteration
            copy = board.copy();
        }
        return true;
    }
}
