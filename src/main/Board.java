package main;
import pieces.*;

import java.util.*;

public class Board {
    private Piece[][] b = new Piece[8][8];
    public int[] lastDoubleStepPawn = null; // {row,col} of pawn that just moved two squares (for en passant)
    public boolean whiteToMove = true;

    public Board() { setupInitial(); }

    public Piece get(int r, int c) { return b[r][c]; }
    public void set(int r, int c, Piece p){ b[r][c] = p; }

    public static boolean inBounds(int r, int c) { return r>=0 && r<8 && c>=0 && c<8; }

    public Board copy() {
        Board nb = new Board(false);
        for (int r=0;r<8;r++) for (int c=0;c<8;c++) nb.b[r][c] = this.b[r][c];
        if (this.lastDoubleStepPawn != null) nb.lastDoubleStepPawn = new int[]{this.lastDoubleStepPawn[0], this.lastDoubleStepPawn[1]};
        nb.whiteToMove = this.whiteToMove;
        return nb;
    }

    // private constructor for copy
    private Board(boolean empty) { if (empty) this.b = new Piece[8][8]; }

    private void setupInitial() {
        // clear
        for (int r=0;r<8;r++)
            for (int c=0;c<8;c++)
                b[r][c]=null;

        // pawns
        for (int i=0;i<8;i++){
            b[6][i] = new Pawn(true);
            b[1][i] = new Pawn(false);
        }

        // rooks
        b[7][0] = new Rook(true);
        b[7][7] = new Rook(true);
        b[0][0] = new Rook(false);
        b[0][7] = new Rook(false);

        // knights
        b[7][1] = new Knight(true);
        b[7][6] = new Knight(true);
        b[0][1] = new Knight(false);
        b[0][6] = new Knight(false);

        // bishops
        b[7][2] = new Bishop(true);
        b[7][5] = new Bishop(true);
        b[0][2] = new Bishop(false);
        b[0][5] = new Bishop(false);

        // queens & kings
        b[7][3] = new Queen(true);
        b[7][4] = new King(true);
        b[0][3] = new Queen(false);
        b[0][4] = new King(false);
    }

    // Apply move if it's legal (checks included). Returns true if applied.
    public boolean applyMove(Move m) {
        List<Move> legal = generateLegalMoves(whiteToMove);
        boolean found = false;
        for (Move lm : legal) {
            if (lm.sr==m.sr && lm.sc==m.sc && lm.tr==m.tr && lm.tc==m.tc) { found = true; break; }
        }
        if (!found) return false;

        performMove(m);
        whiteToMove = !whiteToMove;
        // handle lastDoubleStepPawn tracking
        if (m.moved instanceof Pawn && Math.abs(m.tr - m.sr) == 2)
            lastDoubleStepPawn = new int[]{m.tr, m.tc};
        else lastDoubleStepPawn = null;
        return true;
    }

    // move without legality checks; used internally for simulation and castling simulation
    public void moveNoValidation(int sr, int sc, int tr, int tc) {
        Piece p = b[sr][sc];
        Piece target = b[tr][tc];
        // en passant capture handled outside when needed
        b[tr][tc] = p;
        b[sr][sc] = null;
        if (p != null) p.setMoved(true);
    }

    private void performMove(Move m) {
        Piece p = b[m.sr][m.sc];
        // En passant capture: captured pawn is behind target square (for white capturing black it's tr+1)
        if (m.isEnPassant) {
            b[m.tr][m.tc] = p;
            b[m.sr][m.sc] = null;
            // remove the captured pawn
            int capR = m.moved.isWhite() ? m.tr+1 : m.tr-1;
            b[capR][m.tc] = null;
        } else if (m.isCastling) {
            // move king
            b[m.tr][m.tc] = p;
            b[m.sr][m.sc] = null;
            // move rook
            if (m.tc == 6) { // king side
                Piece rook = b[m.tr][7];
                b[m.tr][5] = rook; b[m.tr][7] = null;
                if (rook != null) rook.setMoved(true);
            } else if (m.tc == 2) { // queen side
                Piece rook = b[m.tr][0];
                b[m.tr][3] = rook; b[m.tr][0] = null;
                if (rook != null) rook.setMoved(true);
            }
            if (p != null) p.setMoved(true);
        } else {
            // normal move (may be capture)
            b[m.tr][m.tc] = p;
            b[m.sr][m.sc] = null;
            if (m.isPromotion) {
                if (m.promoteTo != null) {
                    b[m.tr][m.tc] = m.promoteTo;
                } else {
                    b[m.tr][m.tc] = new Queen(p.isWhite());
                }
            }
            if (p != null)
                p.setMoved(true);
        }
    }

    // Generate all legal moves for side (true = white)
    public List<Move> generateLegalMoves(boolean forWhite) {
        List<Move> legal = new ArrayList<>();
        for (int r=0;r<8;r++)
            for (int c=0;c<8;c++){
            Piece p = b[r][c];
            if (p == null || p.isWhite() != forWhite)
                continue;
            List<Move> pm = p.generateMoves(r,c,this);
            for (Move m : pm) {
                Board copy = this.copy();
                // special handling when copying en passant move because captured pawn is behind
                if (m.isEnPassant) {
                    // perform en passant on copy
                    copy.moveNoValidation(m.sr, m.sc, m.tr, m.tc);
                    int capR = m.moved.isWhite() ? m.tr+1 : m.tr-1;
                    copy.set(capR, m.tc, null);
                } else if (m.isCastling) {
                    // perform castling on copy
                    copy.moveNoValidation(m.sr, m.sc, m.tr, m.tc);
                    if (m.tc == 6) { // king side
                        copy.moveNoValidation(m.tr,7,m.tr,5);
                    } else if (m.tc == 2) {
                        copy.moveNoValidation(m.tr,0,m.tr,3);
                    }
                } else {
                    // normal
                    copy.moveNoValidation(m.sr, m.sc, m.tr, m.tc);
                    if (m.isPromotion) copy.set(m.tr, m.tc, new Queen(m.moved.isWhite()));
                }
                // now check if own king is in check
                if (!copy.isInCheck(forWhite)) legal.add(m);
            }
        }
        return legal;
    }

    // check if side's king is in check
    public boolean isInCheck(boolean whiteSide) {
        int kr=-1,kc=-1;
        for (int r=0;r<8;r++) for (int c=0;c<8;c++){
            Piece p = b[r][c];
            if (p instanceof King && p.isWhite() == whiteSide) { kr=r; kc=c; break; }
        }
        if (kr==-1) return false; // shouldn't happen

        // generate all opponent pseudo-legal attacks and see if any hit king
        for (int r=0;r<8;r++) for (int c=0;c<8;c++){
            Piece p = b[r][c];
            if (p == null || p.isWhite() == whiteSide) continue;
            List<Move> mv = p.generateMoves(r,c,this);
            for (Move m : mv) {
                if (m.tr == kr && m.tc == kc) return true;
            }
        }
        return false;
    }
}
