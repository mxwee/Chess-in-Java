package main;
import pieces.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ChessGUI {
    JFrame frame;
    JButton[][] tiles = new JButton[8][8];
    Board board = new Board();
    int selR=-1, selC=-1;
    java.util.List<Move> highlighted = null;
    JLabel statusLabel = new JLabel("White to move");

    public ChessGUI() {
        frame = new JFrame("Chess");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 830);
        frame.setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout());
        top.add(statusLabel, BorderLayout.CENTER);
        JButton btnNew = new JButton("New Game");
        btnNew.addActionListener(e -> { board = new Board(); selR=selC=-1; refresh(); });
        top.add(btnNew, BorderLayout.EAST);
        frame.add(top, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(8,8));
        frame.add(panel, BorderLayout.CENTER);

        for (int r=0;r<8;r++){
            for (int c=0;c<8;c++){
                JButton b = new JButton();
                b.setOpaque(true);
                b.setBorderPainted(false);
                final int rr = r, cc = c;
                b.addActionListener(e -> tileClicked(rr, cc));
                tiles[r][c] = b;
                panel.add(b);
            }
        }

        refresh();
        frame.setVisible(true);
    }

    private void tileClicked(int r, int c) {
        // if no selection, try select a piece of the side to move
        if (selR==-1) {
            Piece p = board.get(r,c);
            if (p != null && p.isWhite() == board.whiteToMove) {
                selR=r; selC=c;
                highlighted = board.generateLegalMoves(board.whiteToMove);
                // filter to only moves from selected square
                highlighted.removeIf(m -> !(m.sr==selR && m.sc==selC));
                refresh();
            }
        } else {
            // check if clicked a highlighted move
            Move chosen = null;
            if (highlighted != null) {
                for (Move m : highlighted) if (m.tr==r && m.tc==c) { chosen = m; break; }
            }
            if (chosen != null) {
                if (chosen.isPromotion) {
                    chosen.promoteTo = askPromotionPiece(chosen.moved.isWhite());
                }
                board.applyMove(chosen);
                // after move, check for checkmate / stalemate
                boolean whiteToMove = board.whiteToMove;
                List<Move> next = board.generateLegalMoves(whiteToMove);
                if (board.isInCheck(whiteToMove) && next.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, (whiteToMove ? "White" : "Black") + " is checkmated. Game over.");
                } else if (!board.isInCheck(whiteToMove) && next.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Stalemate. Game over.");
                }
                selR=selC=-1;
                highlighted = null;
                refresh();
            } else {
                // either select another friendly piece or clear selection
                Piece p = board.get(r,c);
                if (p != null && p.isWhite() == board.whiteToMove) {
                    selR=r; selC=c;
                    highlighted = board.generateLegalMoves(board.whiteToMove);
                    highlighted.removeIf(m -> !(m.sr==selR && m.sc==selC));
                    refresh();
                } else {
                    selR=selC=-1; highlighted = null; refresh();
                }
            }
        }
    }

    // Promotion for pawn
    private Piece askPromotionPiece(boolean white) {
        JDialog dialog = new JDialog(frame, "Promote Pawn", true);
        dialog.setLayout(new GridLayout(1,4));
        dialog.setSize(420, 140);

        Piece[] options = new Piece[]{
                new Queen(white),
                new Rook(white),
                new Bishop(white),
                new Knight(white)
        };

        final Piece[] result = { null };

        for (Piece p : options) {
            JButton b = new JButton();
            b.setIcon(p.getIcon());
            b.addActionListener(e -> {
                result[0] = p;
                dialog.dispose();
            });
            dialog.add(b);
        }

        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);

        return result[0];
    }


    private void refresh() {
        // update tiles
        for (int r=0;r<8;r++){
            for (int c=0;c<8;c++){
                JButton b = tiles[r][c];
                boolean light = (r+c)%2==0;
                b.setBackground(light ? Color.darkGray : Color.white);
                Piece p = board.get(r,c);
                b.setIcon(p==null?null:p.getIcon());
                b.setText("");
            }
        }
        // highlight selection and moves
        if (selR!=-1) {
            tiles[selR][selC].setBackground(Color.gray);
            if (highlighted != null) {
                for (Move m : highlighted) {
                    tiles[m.tr][m.tc].setBackground(Color.gray);                 }
            }
        }

        statusLabel.setText((board.whiteToMove ? "White" : "Black") + (board.isInCheck(board.whiteToMove) ? " (in check)" : "") + " to move");
    }

}
