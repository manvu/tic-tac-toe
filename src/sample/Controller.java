package sample;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Man Vu
 */
public class Controller {
    @FXML
    private Label label;
    @FXML
    private AnchorPane pane;
    @FXML
    private Button startGameButton;
    @FXML
    private Button one;
    @FXML
    private Button two;
    @FXML
    private Button three;
    @FXML
    private Button four;
    @FXML
    private Button five;
    @FXML
    private Button six;
    @FXML
    private Button seven;
    @FXML
    private Button eight;
    @FXML
    private Button nine;
    private int turn = 0; // count the turn
    private Service<Integer> minimaxMoveTask; //service to create bot move on another thread
    private boolean isWinner = false; // Did we get the winner?
    private String winner = ""; // name of the winner X or O
    // check if player move is valid
    private GameTree tree;

    ObservableList board; // present the board
    ArrayList<Button> buttonsList = new ArrayList<>(); // list of button
    Map<String, Integer> map = new HashMap<>() {
        {
            put("one", 0);
            put("two", 1);
            put("three", 2);
            put("four", 3);
            put("five", 4);
            put("six", 5);
            put("seven", 6);
            put("eight", 7);
            put("nine", 8);
        }
    };

    int[][] winCombos = {
            {0, 1, 2},
            {3, 4, 5},
            {6, 7, 8},
            {0, 3, 6},
            {1, 4, 7},
            {2, 5, 8},
            {0, 4, 8},
            {2, 4, 6}
    };

    // assign the main
    public void setMain(Main main) {
    }

    private Line line; // Draw the line after win

    /**
     * Set the default parameter
     */
    public void initialize() {
        this.board = FXCollections.observableArrayList("", "", "", "", "", "", "", "", "");
        // add all buttons to a list
        buttonsList.add(one);
        buttonsList.add(two);
        buttonsList.add(three);
        buttonsList.add(four);
        buttonsList.add(five);
        buttonsList.add(six);
        buttonsList.add(seven);
        buttonsList.add(eight);
        buttonsList.add(nine);

        // bind all the button's value
        one.textProperty().bind(Bindings.stringValueAt(board, 0));
        two.textProperty().bind(Bindings.stringValueAt(board, 1));
        three.textProperty().bind(Bindings.stringValueAt(board, 2));
        four.textProperty().bind(Bindings.stringValueAt(board, 3));
        five.textProperty().bind(Bindings.stringValueAt(board, 4));
        six.textProperty().bind(Bindings.stringValueAt(board, 5));
        seven.textProperty().bind(Bindings.stringValueAt(board, 6));
        eight.textProperty().bind(Bindings.stringValueAt(board, 7));
        nine.textProperty().bind(Bindings.stringValueAt(board, 8));

        // initialize game tree
        tree = new GameTree();
    }

    volatile boolean isComputerTurn = false;

    /**
     * User click handler, create new task to let the bot play
     *
     * @param event handle user event
     */
    public void buttonPressed(Event event) {
        if (isComputerTurn) {
            return;
        }

        // create move on another thread
        minimaxMoveTask = new Service<>() {
            protected Task<Integer> createTask() {
                return new Task<>() {
                    @Override
                    // First get a random number, then check if the position is empty. If not, get another one
                    protected Integer call() throws Exception {
                        isComputerTurn = true;
                        int position = tree.minimax(board);

                        try {
                            Platform.runLater(() -> {
                                label.setText("Opponent is thinking...");
                            });
                            Thread.sleep(500);
                            turn++;
                        } catch (InterruptedException e) {
                            if (isCancelled()) {
                                updateMessage("Cancelled");
                                System.out.println("Task was cancelled because New Game button was clicked");
                            }
                        } finally {
                            isComputerTurn = false;
                        }

                        return position;
                    }
                };
            }
        };
        // return the new random move, assign to the board
        minimaxMoveTask.setOnSucceeded(workerStateEvent -> {
            label.setText("Your turn!");
            System.out.println("Last value " + minimaxMoveTask.getValue());
            board.set(minimaxMoveTask.getValue(), "O");
            checkStatus();
        });

        Button btn = (Button) event.getSource();
        // set play move
        if (!isWinner && turn < 9) {
            int position = map.get(btn.getId());
            checkStatus();
            if (board.get(position) == "") {
                board.set(position, "X");
                System.out.println(board);
                turn++;
                checkStatus();
                if (!isWinner && turn < 9) {
                    System.out.println(board);
                    minimaxMoveTask.start();
                    checkStatus();
                }
            } else {
                label.setTextFill(Color.RED);
                label.setText("Your move is invalid ");
            }
        }
    }

    /**
     * Start a new game, reset everything to default
     */
    public void startGame() {
        if (minimaxMoveTask != null) {
            minimaxMoveTask.cancel();
        }

        for (int i = 0; i < board.size(); i++) {
            board.set(i, "");
        }
        this.isComputerTurn = false;
        this.turn = 0;
        this.isWinner = false;
        pane.getChildren().remove(line);
        label.setTextFill(Color.BLACK);
        label.setText("Unbeatable Tic Tac Toe");
    }

    /**
     * Decide who is the winner, present it to the screen, draw a line
     */
    private void checkStatus() {
        int[] combo;

        for (int[] winCombo : winCombos) {
            if (board.get(winCombo[0]) != "" &&
                    board.get(winCombo[0]) == board.get(winCombo[1]) &&
                    board.get(winCombo[1]) == board.get(winCombo[2])) {
                isWinner = true;
                winner = (String) board.get(winCombo[0]);
                combo = winCombo;

                Button start = buttonsList.get(combo[0]);
                Button end = buttonsList.get(combo[2]);

                line = new Line(start.getLayoutX() + start.getPrefWidth() / 2,
                        start.getLayoutY() + start.getPrefHeight() / 2,
                        end.getLayoutX() + end.getPrefWidth() / 2,
                        end.getLayoutY() + end.getPrefHeight() / 2);
                line.setStrokeWidth(5.0);
                line.setStroke(Color.RED);
                pane.getChildren().add(line);

                break;
            }
        }

        System.out.println(isWinner);
        if (isWinner) {
            label.setTextFill(Color.RED);
            label.setText("The winner is " + winner + (winner == "X" ? " (You)" : " (Your opponent)"));
        }
        if (turn >= 9 && !isWinner) {
            label.setTextFill(Color.RED);
            label.setText("There is no winner ");
        }
    }
}
