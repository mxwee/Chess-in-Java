package pieces;

import main.Board;
import main.Move;
import java.util.*;

public class Queen extends Piece {
    public Queen(boolean white) {
        super(white, "/images/" + (white ? "white_queen.png" : "black_queen.png"));
    }


    @Override
    public List<Move> generateMoves(int r, int c, Board board) {
        List<Move> moves = new ArrayList<>();
        // combine rook + bishop
        int[] dr = {1,-1,0,0,1,1,-1,-1}, dc = {0,0,1,-1,1,-1,1,-1};
        for (int k=0;k<8;k++){
            int rr = r+dr[k], cc = c+dc[k];
            while (Board.inBounds(rr,cc)){
                Piece p = board.get(rr,cc);
                if (p == null) moves.add(new Move(r,c,rr,cc,this,null,false,false,false));
                else {
                    if (p.isWhite() != this.isWhite())
                        moves.add(new Move(r,c,rr,cc,this,p,false,false,false));
                    break;
                }
                rr += dr[k]; cc += dc[k];
            }
        }
        return moves;
    }
}
