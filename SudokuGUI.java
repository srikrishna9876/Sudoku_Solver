import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class SudokuGUI extends Application {

    private TextField[][] cells = new TextField[9][9];
    private int[][] board = new int[9][9];
    private int[][] fixedBoard = new int[9][9]; // Stores the fixed puzzle cells
    private String difficultyLevel;
    private TextField selectedCell = null; // Track the currently selected cell
    private Button checkButton; // Moved checkButton to class level

    public SudokuGUI(String difficulty) {
        this.difficultyLevel = difficulty;
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane mainLayout = new BorderPane();

        // Back button to go to the home screen
        Button backButton = new Button("<--");
        backButton.setOnAction(e -> goToHomeScreen(primaryStage));
        backButton.getStyleClass().add("back-button");

        // Sudoku grid with borders
        GridPane grid = createSudokuGrid();
        grid.setId("mainGrid"); // Add CSS ID for styling
        generatePuzzle(difficultyLevel);

        // Buttons for Next, Undo, and Check at the bottom
        HBox buttonBox = new HBox(10);
        Button nextButton = new Button("Next");
        Button undoButton = new Button("Undo");
        checkButton = new Button("Check"); // Initialize checkButton here

        nextButton.getStyleClass().add("button");
        undoButton.getStyleClass().add("button");
        checkButton.getStyleClass().add("button-check");

        nextButton.setOnAction(e -> generatePuzzle(difficultyLevel));
        checkButton.setOnAction(e -> checkPuzzle());
        undoButton.setOnAction(e -> undoLastInput());

        Button customPuzzleButton = new Button("Custom Puzzle");
        customPuzzleButton.setOnAction(e -> openCustomPuzzle());
        buttonBox.getChildren().add(customPuzzleButton);

        buttonBox.getChildren().addAll(nextButton, undoButton, checkButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 10, 0));

        // Solve button below grid and other buttons
        Button solveButton = new Button("Solve");
        solveButton.setOnAction(e -> solvePuzzle());
        solveButton.getStyleClass().add("button");

        // Number buttons from 1 to 9
        HBox numberButtons = new HBox(10);
        for (int i = 1; i <= 9; i++) {
            int num = i;
            Button numberButton = new Button(String.valueOf(i));
            numberButton.setPrefSize(50, 50);
            numberButton.getStyleClass().add("number-button");
            numberButton.setOnAction(e -> enterNumber(num));
            numberButtons.getChildren().add(numberButton);
        }
        numberButtons.setAlignment(Pos.CENTER);
        numberButtons.setPadding(new Insets(10, 0, 10, 0));

        VBox centerLayout = new VBox(10, grid, buttonBox, solveButton, numberButtons);
        centerLayout.setAlignment(Pos.CENTER);

        mainLayout.setTop(backButton);
        BorderPane.setAlignment(backButton, Pos.TOP_LEFT);
        mainLayout.setCenter(centerLayout);

        Scene scene = new Scene(mainLayout, 600, 700);
        scene.getStylesheets().add(getClass().getResource("sudokuStyle.css").toExternalForm()); // Apply CSS

        primaryStage.setScene(scene);
        primaryStage.setTitle("Sudoku Solver");
        primaryStage.show();
    }
    private void openCustomPuzzle() {
        // Clear the current board
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col].setText(""); // Clear the text
                cells[row][col].setEditable(true); // Make cells editable
                cells[row][col].setStyle("-fx-background-color: white; -fx-font-size: 20; -fx-alignment: center; -fx-border-color: black;"); // Reset styles
                fixedBoard[row][col] = 0; // Reset the fixed board
            }
        }
        System.out.println("Custom puzzle opened. Please enter your values.");
    }


    private void goToHomeScreen(Stage primaryStage) {
        System.out.println("Back to home screen");
        primaryStage.close(); // Placeholder action for returning to home screen
    }

    private void undoLastInput() {
        if (selectedCell != null) {
            selectedCell.setText(""); // Clear the selected cell
            selectedCell.setStyle("-fx-background-color: white; -fx-font-size: 20; -fx-alignment: center; -fx-border-color: black; -fx-border-width: 1;"); // Reset style
            selectedCell = null; // Reset selected cell
        }
    }

    private void checkPuzzle() {
        checkButton.setDisable(true); // Disable the button

        // Solve the puzzle and store the solution in 'board' if not already solved
        if (!solveSudoku(board)) {
            System.out.println("Error: Unable to solve the puzzle.");
            return;
        }

        // Reset styles for all editable cells before checking
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (fixedBoard[row][col] == 0) { // Only reset editable cells
                    cells[row][col].setStyle("-fx-background-color: white; -fx-font-size: 20; -fx-alignment: center; -fx-border-color: black; -fx-border-width: 1; -fx-text-fill: black;");
                }
            }
        }

        // Check the user's input against the solution
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                String userInput = cells[row][col].getText();
                if (!userInput.isEmpty() && fixedBoard[row][col] == 0) { // Only check editable cells
                    try {
                        int userValue = Integer.parseInt(userInput);
                        // Compare with the solved board
                        if (userValue == board[row][col]) {
                            cells[row][col].setStyle("-fx-background-color: white; -fx-font-size: 20; -fx-alignment: center; -fx-border-color: black; -fx-border-width: 1; -fx-text-fill: green;");
                        } else {
                            cells[row][col].setStyle("-fx-background-color: white; -fx-font-size: 20; -fx-alignment: center; -fx-border-color: black; -fx-border-width: 1; -fx-text-fill: red;");
                        }
                    } catch (NumberFormatException e) {
                        // Handle non-numeric input
                        cells[row][col].setStyle("-fx-background-color: white; -fx-font-size: 20; -fx-alignment: center; -fx-border-color: black; -fx-border-width: 1; -fx-text-fill: red;");
                    }
                }
            }
        }

        // Re-enable the button after 3 seconds and reset colors to black
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    if (fixedBoard[row][col] == 0) { // Reset only editable cells
                        cells[row][col].setStyle("-fx-background-color: white; -fx-font-size: 20; -fx-alignment: center; -fx-border-color: black; -fx-border-width: 1; -fx-text-fill: black;");
                    }
                }
            }
            checkButton.setDisable(false); // Re-enable the button
        }));
        timeline.play();
    }


    private GridPane createSudokuGrid() {
        GridPane mainGrid = new GridPane();
        mainGrid.setAlignment(Pos.CENTER);
        mainGrid.setPadding(new Insets(10));
        mainGrid.setVgap(10);
        mainGrid.setHgap(10);
        mainGrid.setStyle("-fx-border-color: black; -fx-border-width: 2;");

        for (int subGridRow = 0; subGridRow < 3; subGridRow++) {
            for (int subGridCol = 0; subGridCol < 3; subGridCol++) {
                GridPane subGrid = new GridPane();
                subGrid.setPadding(new Insets(2));
                subGrid.setVgap(1);
                subGrid.setHgap(1);

                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 3; col++) {
                        int globalRow = subGridRow * 3 + row;
                        int globalCol = subGridCol * 3 + col;

                        cells[globalRow][globalCol] = new TextField();
                        cells[globalRow][globalCol].setPrefSize(50, 50);
                        cells[globalRow][globalCol].setStyle(
                                "-fx-font-size: 20; -fx-alignment: center; -fx-border-color: black; -fx-border-width: 1;");
                        cells[globalRow][globalCol].setEditable(false); // Start as non-editable
                        cells[globalRow][globalCol].setOnMouseClicked(e -> selectCell(globalRow, globalCol));

                        subGrid.add(cells[globalRow][globalCol], col, row);
                    }
                }
                mainGrid.add(subGrid, subGridCol, subGridRow);
            }
        }
        return mainGrid;
    }

    private void selectCell(int row, int col) {
        if (selectedCell != null) {
            selectedCell.setStyle("-fx-background-color: white; -fx-font-size: 20; -fx-alignment: center; -fx-border-color: black;");
        }
        selectedCell = cells[row][col];
        selectedCell.setStyle("-fx-background-color: lightblue; -fx-font-size: 20; -fx-alignment: center; -fx-border-color: black;");
    }

    private void enterNumber(int num) {
        if (selectedCell != null && fixedBoard[getCellRow(selectedCell)][getCellCol(selectedCell)] == 0) {
            selectedCell.setText(String.valueOf(num));
        }
    }

    private int getCellRow(TextField cell) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (cells[row][col] == cell) {
                    return row;
                }
            }
        }
        return -1; // Not found
    }

    private int getCellCol(TextField cell) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (cells[row][col] == cell) {
                    return col;
                }
            }
        }
        return -1; // Not found
    }

    private void generatePuzzle(String difficultyLevel) {
        // Reset board and fixed board
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j] = 0;
                fixedBoard[i][j] = 0;
            }
        }

        fillBoard();
        createPuzzle(difficultyLevel);
        updateCells();
    }

    private void fillBoard() {
        solvePuzzle(); // Solve to get a completed board
    }

    private void createPuzzle(String difficultyLevel) {
        Random rand = new Random();
        int numberOfCellsToRemove;

        switch (difficultyLevel) {
            case "easy":
                numberOfCellsToRemove = 36; // Easy puzzles have more cells
                break;
            case "medium":
                numberOfCellsToRemove = 45; // Medium puzzles
                break;
            case "hard":
                numberOfCellsToRemove = 54; // Hard puzzles have fewer cells
                break;
            default:
                numberOfCellsToRemove = 36;
        }

        // Copy board to fixedBoard and remove random cells
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                fixedBoard[row][col] = board[row][col];
            }
        }

        while (numberOfCellsToRemove > 0) {
            int row = rand.nextInt(9);
            int col = rand.nextInt(9);
            if (fixedBoard[row][col] != 0) {
                fixedBoard[row][col] = 0; // Remove the number
                numberOfCellsToRemove--;
            }
        }
    }

    private void updateCells() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (fixedBoard[row][col] != 0) {
                    cells[row][col].setText(String.valueOf(fixedBoard[row][col]));
                    cells[row][col].setEditable(false); // Make fixed cells non-editable
                    cells[row][col].setStyle("-fx-background-color: lightgrey;"); // Optional styling for fixed cells
                } else {
                    cells[row][col].setText("");
                    cells[row][col].setEditable(true); // Editable cells
                    cells[row][col].setStyle("-fx-background-color: white;"); // Reset style for editable cells
                }
            }
        }
    }

    private void solvePuzzle() {
        // Update the board with user inputs
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                String userInput = cells[row][col].getText();
                if (!userInput.isEmpty()) {
                    try {
                        int value = Integer.parseInt(userInput);
                        board[row][col] = value; // Use user's input for solving
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input detected at row " + row + ", col " + col);
                    }
                } else {
                    board[row][col] = 0; // Treat empty cells as zero
                }
            }
        }

        // Now solve the board
        if (solveSudoku(board)) {
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    if (fixedBoard[row][col] == 0) { // Update only editable cells
                        cells[row][col].setText(String.valueOf(board[row][col])); // Update the cell with the solved value
                    }
                }
            }
        } else {
            System.out.println("Puzzle cannot be solved.");
        }
    }



    private boolean solveSudoku(int[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) { // Find an empty cell
                    for (int num = 1; num <= 9; num++) {
                        if (isValidMove(board, row, col, num)) {
                            board[row][col] = num; // Try this number
                            if (solveSudoku(board)) {
                                return true; // If solved, return true
                            }
                            board[row][col] = 0; // Reset on backtrack
                        }
                    }
                    return false; // No valid number found
                }
            }
        }
        return true; // Solved
    }

    private boolean isValidMove(int[][] board, int row, int col, int num) {
        // Check row and column
        for (int x = 0; x < 9; x++) {
            if (board[row][x] == num || board[x][col] == num) {
                return false;
            }
        }
        // Check 3x3 grid
        int startRow = row - row % 3, startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i + startRow][j + startCol] == num) {
                    return false;
                }
            }
        }
        return true; // Valid move
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
