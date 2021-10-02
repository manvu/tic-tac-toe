package sample;

import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.HashMap;

public class GameTree {

    static boolean DEBUG = true; // not final to allow local debug toggles, ie use debug in 1 method only

    public static HashMap<String, TicTacToeNode> nodes = new HashMap<>();

    public static int[] buildBoards(TicTacToeNode root, char symbol, int level) {
        // A straight forward way to generate at most 9 children per level.
        // If the spot is already filled we skip that child, generating the
        // expected tree.  Note we create the child after the if check to
        // avoid throwing our count of nodes created off.


        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                char[][] newBoard = root.copyBoard();
                if (newBoard[i][j] == Character.MIN_VALUE) {
                    newBoard[i][j] = symbol;
                    String boardStr = TicTacToeNode.getBoardString(newBoard);
                    boolean contains = nodes.containsKey(boardStr);

                    TicTacToeNode child = new TicTacToeNode();
                    child.board = newBoard;
                    child.depth = level;
                    root.children.add(child);

                    if (!contains) {
                        nodes.put(boardStr, child);
                    }
                }
            }
        }

        // Heuristic algorithm for generating game tree

        for (TicTacToeNode child : root.children) {
            if (child.isGameWon()) {
                // stop creating branches, we are at a leaf node
                // do not return from here so that the other siblings
                // will be correctly generated
                if (child.isGameWon('X')) {
                    root.xWins++;
                    child.xWins++;
                }
                if (child.isGameWon('O')) {
                    root.oWins++;
                    child.oWins++;
                }
            } else if (!child.isFull()) {
                // if the board is full there's nothing to do, simply iterate
                // to the next sibling.  If the board is not a win, nor a tie
                // then there are more children to create, thus the recursive
                // step appears here
                int[] scores;
                scores = buildBoards(child, symbol == 'O' ? 'X' : 'O', level + 1);
                root.xWins += scores[0];
                root.oWins += scores[1];
            }
        }

        // Once all children are processed we can return the results of the parent
        // node.  To reach this return statement we will have already have created
        // all sub-branches and counted their scores.
        int[] scores = {root.xWins, root.oWins};
        return scores;
    }

    public GameTree() {
        TicTacToeNode tttTree = new TicTacToeNode();
        nodes.put(tttTree.toString(), tttTree);
        buildBoards(tttTree, 'X', 0);

        System.out.println(TicTacToeNode.nodes + " nodes created.");
        System.out.printf("X: %d\t\tO: %d\n", tttTree.xWins, tttTree.oWins);

        // Demonstrate symmetry by presenting opening moves
        for (TicTacToeNode child : tttTree.children) {
            System.out.println(child);
            System.out.printf("X: %d\t\tO: %d\n", child.xWins, child.oWins);
        }

        System.out.println("");

    }

    public static HashMap<String, TicTacToeNode> initialize() {
        TicTacToeNode tttTree = new TicTacToeNode();
        nodes.put(tttTree.toString(), tttTree);
        buildBoards(tttTree, 'X', 0);

        //total number nodes < 1 + 9 + 9*8 + 9*8*7 + ... + 9*8*7*6*5*4*3*2*1 = 986 410
        // 9! = 362,880
        // 3^9 = 19,683
        // Choose Formula with "one too many moves" error = 6,046
        // Minimal without rotation = 5,478
        // Minimal with rotation/reflections = 765


//        System.out.println(TicTacToeNode.nodes + " nodes created.");
//        System.out.printf("X: %d\t\tO: %d\n", tttTree.xWins, tttTree.oWins);
//
//        // Demonstrate symmetry by presenting opening moves
//        for (TicTacToeNode child : tttTree.children) {
//            System.out.println(child);
//            System.out.printf("X: %d\t\tO: %d\n", child.xWins, child.oWins);
//        }
//
//        System.out.println("");

        return nodes;
    }

    public int startMinimax(TicTacToeNode root, boolean isMaximizer) {
        if (root.children.size() == 0) { // leaf node
            if (root.xWins == 0 && root.oWins == 0) {
                root.minimaxScore = 0;
            } else if (root.xWins == 1) {
                root.minimaxScore = 100 - root.depth;
            } else if (root.oWins == 1) {
                root.minimaxScore = -100 + root.depth;
            }

            return root.minimaxScore;
        }

        if (isMaximizer) {
            root.minimaxScore = Integer.MIN_VALUE;

            for (TicTacToeNode child : root.children) {
                startMinimax(child, false);
                if (child.minimaxScore > root.minimaxScore) {
                    root.minimaxScore = child.minimaxScore;
                }
            }
        } else {
            root.minimaxScore = Integer.MAX_VALUE;

            for (TicTacToeNode child : root.children) {
                startMinimax(child, true);
                if (child.minimaxScore < root.minimaxScore) {
                    root.minimaxScore = child.minimaxScore;
                }
            }
        }

        return root.minimaxScore;
    }

    public int minimax(ObservableList board) throws Exception {
        String boardStr;

        char[][] charBoard = new char[3][3];

        charBoard[0][0] = board.get(6) == "" ? charBoard[0][0] : String.valueOf(board.get(6)).charAt(0);
        charBoard[0][1] = board.get(7) == "" ? charBoard[0][1] : String.valueOf(board.get(7)).charAt(0);
        charBoard[0][2] = board.get(8) == "" ? charBoard[0][2] : String.valueOf(board.get(8)).charAt(0);
        charBoard[1][0] = board.get(3) == "" ? charBoard[1][0] : String.valueOf(board.get(3)).charAt(0);
        charBoard[1][1] = board.get(4) == "" ? charBoard[1][1] : String.valueOf(board.get(4)).charAt(0);
        charBoard[1][2] = board.get(5) == "" ? charBoard[1][2] : String.valueOf(board.get(5)).charAt(0);
        charBoard[2][0] = board.get(0) == "" ? charBoard[2][0] : String.valueOf(board.get(0)).charAt(0);
        charBoard[2][1] = board.get(1) == "" ? charBoard[2][1] : String.valueOf(board.get(1)).charAt(0);
        charBoard[2][2] = board.get(2) == "" ? charBoard[2][2] : String.valueOf(board.get(2)).charAt(0);


        boardStr = TicTacToeNode.getBoardString(charBoard);

        TicTacToeNode currentNode = nodes.get(boardStr);

        if (currentNode == null) {
            System.out.println("current node cannot be null");
            throw new Exception("current node cannot be null");

        }

        System.out.println(boardStr);

        int bestEvaluationScore = startMinimax(currentNode, false);
        TicTacToeNode selectedNode = null;
        for (TicTacToeNode child : currentNode.children) {
            if (child.minimaxScore == bestEvaluationScore) {
                selectedNode = child;
            }
        }

        int nextPosition = currentNode.nextMove(selectedNode);

        return nextPosition;
    }
}
