import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SudokuHomeScreen extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));

        // Title
        Label titleLabel = new Label("Sudoku Solver");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-font-style: italic; -fx-text-fill: #2b2b2b;");

        // Difficulty Label
        Label difficultyLabel = new Label("Select Difficulty");
        difficultyLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #4a4a4a;");

        // Difficulty Buttons
        Button easyButton = createStyledButton("Easy", "#7BC950");
        Button mediumButton = createStyledButton("Medium", "#FFD966");
        Button hardButton = createStyledButton("Hard", "#E06666");
        Button customButton = createStyledButton("Custom", "#6FA3EF");

        // Button Actions
        easyButton.setOnAction(e -> openSudokuGame("Easy"));
        mediumButton.setOnAction(e -> openSudokuGame("Medium"));
        hardButton.setOnAction(e -> openSudokuGame("Hard"));
        customButton.setOnAction(e -> openCustomPuzzleInput(primaryStage));

        // Arrange Difficulty Section
        VBox difficultyBox = new VBox(15, difficultyLabel, easyButton, mediumButton, hardButton, customButton);
        difficultyBox.setAlignment(Pos.CENTER_LEFT);
        difficultyBox.setPadding(new Insets(15));
        difficultyBox.setStyle("-fx-background-color: #f3f4f6; -fx-border-color: #c1c1c1; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Main Center Layout with Title and Difficulty Selection
        VBox centerLayout = new VBox(25, titleLabel, difficultyBox);
        centerLayout.setAlignment(Pos.TOP_CENTER);

        mainLayout.setCenter(centerLayout);

        // Set up the Scene
        Scene scene = new Scene(mainLayout, 600, 400);
        scene.getStylesheets().add(getClass().getResource("sudokuStyle.css").toExternalForm()); // CSS file if needed
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sudoku Solver");
        primaryStage.show();
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: " + color +
                "; -fx-background-radius: 10; -fx-border-radius: 10;");
        button.setPrefSize(120, 50);
        return button;
    }

    private void openSudokuGame(String difficulty) {
        SudokuGUI sudokuGUI = new SudokuGUI(difficulty);
        Stage gameStage = new Stage();
        sudokuGUI.start(gameStage);
    }

    private void openCustomPuzzleInput(Stage primaryStage) {
        // This is where you’ll set up the TextArea and button for custom puzzle input
        VBox customLayout = new VBox(15);
        customLayout.setPadding(new Insets(20));
        customLayout.setAlignment(Pos.CENTER);

        Label customLabel = new Label("Enter Custom Puzzle (0 for empty cells)");
        customLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #4a4a4a;");

        TextArea customPuzzleInput = new TextArea();
        customPuzzleInput.setPromptText("Enter custom puzzle in a 9x9 format, using commas or spaces...");

        Button submitButton = createStyledButton("Submit", "#6FA3EF");
        submitButton.setOnAction(e -> {
            String input = customPuzzleInput.getText();
            int[][] customBoard = parseCustomPuzzle(input);
            SudokuGUI sudokuGUI = new SudokuGUI(customBoard);
            Stage customGameStage = new Stage();
            sudokuGUI.start(customGameStage);
        });

        customLayout.getChildren().addAll(customLabel, customPuzzleInput, submitButton);

        // Set up custom puzzle scene
        Scene customScene = new Scene(customLayout, 400, 300);
        primaryStage.setScene(customScene);
    }

    private int[][] parseCustomPuzzle(String input) {
        int[][] board = new int[9][9];
        // Parsing logic to convert the input to a 2D array
        return board;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
