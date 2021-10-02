package sample;

import java.util.ArrayList;
import java.util.Arrays;

public class TicTacToeNode {
    static int nodes = 0;
    char[][] board;
    ArrayList<TicTacToeNode> children;
    int xWins = 0;
    int oWins = 0;
    int minimaxScore = 0;
    int depth;

    public TicTacToeNode() {
        nodes++;
        board = new char[3][3];
        children = new ArrayList<>();
    }

    public static String getBoardString(char[][] board) {
        String boardStr;

        boardStr = "---\n";
        boardStr += Arrays.toString(board[0]) + "\n";
        boardStr += Arrays.toString(board[1]) + "\n";
        boardStr += Arrays.toString(board[2]) + "\n";
        boardStr += "---\n";

        return boardStr;
    }

    @Override
    public String toString() {
        return getBoardString(board);
    }

    public boolean isGameWon() {
        return isGameWon('X') || isGameWon('O');
    }

    public boolean isGameWon(char symbol) {
        boolean won;

        // 2 diagonals
        won = (board[1][1] == symbol) &&
                ((board[0][0] == board[1][1] && board[1][1] == board[2][2]) ||
                        (board[0][2] == board[1][1] && board[1][1] == board[2][0]));

        for (int i = 0; i < 3; i++) {
            // 3 rows
            won = won || (board[i][0] == symbol && board[i][0] == board[i][1] && board[i][1] == board[i][2]);
            // 3 columns
            won = won || (board[0][i] == symbol && board[0][i] == board[1][i] && board[1][i] == board[2][i]);
        }

        return won;
    }

    public boolean isFull() {
        boolean full = true;

        for (char[] row : board)
            for (char pos : row)
                full &= (pos != Character.MIN_VALUE);

        return full;
    }

    public char[][] copyBoard() {
        // Java's .clone() does not handle 2D arrays
        // by copying values, rather we get a clone of refs
        char[][] boardCopy = new char[3][3];

        for (int i = 0; i < 3; i++)
            System.arraycopy(board[i], 0, boardCopy[i], 0, 3);

        return boardCopy;
    }

    public int nextMove(TicTacToeNode nextNode) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (this.board[i][j] != nextNode.board[i][j]) {
                    switch (i) {
                        case 0:
                            return switch (j) {
                                case 0 -> 6;
                                case 1 -> 7;
                                default -> 8;
                            };
                        case 1:
                            return switch (j) {
                                case 0 -> 3;
                                case 1 -> 4;
                                default -> 5;
                            };
                        default:
                            return switch (j) {
                                case 0 -> 0;
                                case 1 -> 1;
                                default -> 2;
                            };
                    }
                }
            }
        }
        return -1;
    }
}
