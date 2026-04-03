package pieces;

import main.Board;
import main.Move;
import java.util.*;

public class Knight extends Piece {
    public Knight(boolean white) {
        super(white, "/images/" + (white ? "white_knight.png" : "black_knight.png"));
    }


    @Override
    public List<Move> generateMoves(int r, int c, Board board) {
        List<Move> moves = new ArrayList<>();
        int[] dr = {2,2,-2,-2,1,1,-1,-1};
        int[] dc = {1,-1,1,-1,2,-2,2,-2};
        for (int k=0;k<8;k++){
            int rr = r+dr[k], cc = c+dc[k];
            if (!Board.inBounds(rr,cc)) continue;
            Piece p = board.get(rr,cc);
            if (p == null || p.isWhite() != this.isWhite())
                moves.add(new Move(r,c,rr,cc,this,p,false,false,false));
        }
        return moves;
    }
}

