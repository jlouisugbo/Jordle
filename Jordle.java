import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.media.AudioClip;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

/**
 * The Jordle class represents a word-guessing game inspired by Wordle.
 * It provides a graphical interface for users to guess a 5-letter word,
 * with feedback given for correct letters, misplaced letters, and incorrect
 * letters.
 * The game supports both light and dark themes and includes sound and visual
 * feedback.
 * 
 * @author Tumelo Ngono
 * @version 1.00
 */
public class Jordle extends Application {
    private Backend backend;
    private GridPane gameGrid;
    private Label statusLabel;
    private StringBuilder currentGuess;
    private int currentRow;
    private Label title;
    private boolean isDarkMode = false;
    private VBox gameLayout;
    private AudioClip incorrectGuess = new AudioClip(
            "https://drive.google.com/uc?export=download&id=1tUUrO09c4JqjUafgdFkfOowbkgSqEwFB");
    private AudioClip correctGuess = new AudioClip(
            "https://drive.google.com/uc?export=download&id=1w9_rBJP5TlJXHPiGhcZ8DKDujSxcnn0M");
    private AudioClip outOfAttempts = new AudioClip(
            "https://drive.google.com/uc?export=download&id=1LVmzpqaRcNIjPMkxDeUXZt1nl4xA_97Z");

    @Override
    public void start(Stage primaryStage) {
        backend = new Backend();
        currentGuess = new StringBuilder();
        currentRow = 0;

        primaryStage.setTitle("Jordle");

        // Welcome Screen
        VBox welcomeScreen = new VBox(20);
        welcomeScreen.setStyle("-fx-alignment: center; -fx-background-color: #f5f5f5;");

        title = new Label("Jordle");
        title.setFont(new Font("Arial", 36));

        Image jordleImage = new Image("jordleImage.jpg");
        ImageView imageView = new ImageView(jordleImage);
        imageView.setFitWidth(300);
        imageView.setPreserveRatio(true);

        Button playButton = new Button("Play");
        playButton.setOnAction(e -> primaryStage.setScene(createGameScreen(primaryStage)));

        welcomeScreen.getChildren().addAll(title, imageView, playButton);

        Scene welcomeScene = new Scene(welcomeScreen, 400, 400);
        primaryStage.setScene(welcomeScene);
        primaryStage.show();
    }

    private Scene createGameScreen(Stage primaryStage) {
        // Use the class-level gameLayout variable
        gameLayout = new VBox(10);
        gameLayout.setStyle("-fx-alignment: center; -fx-padding: 10; -fx-background-color: white;");

        primaryStage.setTitle("Jordle");
        title = new Label("Jordle");
        title.setFont(new Font("Arial", 36));

        // Game Grid
        gameGrid = new GridPane();
        gameGrid.setStyle("-fx-hgap: 5; -fx-vgap: 5; -fx-alignment: center;");
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                Label cell = new Label();
                cell.setPrefSize(40, 40);
                cell.setStyle("-fx-border-color: black; -fx-background-color: white;");
                cell.setFont(Font.font(20)); // Set a readable font size
                cell.setAlignment(Pos.CENTER); // Center text within the label
                gameGrid.add(cell, j, i);
            }
        }

        // Status Label
        statusLabel = new Label("Try guessing a word!");
        statusLabel.setFont(new Font("Arial", 14));
        statusLabel.setStyle("-fx-text-fill: black;");

        // Restart Button
        Button restartButton = new Button("Restart");
        restartButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                resetGame();
            }
        });
        restartButton.setFocusTraversable(false); // Prevent button focus

        // Instructions Button
        Button instructionsButton = new Button("Instructions");
        instructionsButton.setOnAction(e -> showInstructions());
        instructionsButton.setFocusTraversable(false); // Prevent button focus

        // Toggle Theme Button
        Button toggleThemeButton = new Button("Toggle Theme");
        toggleThemeButton.setFocusTraversable(false); // Prevent button focus
        toggleThemeButton.setOnAction(e -> new ThemeManager().toggleDarkMode());

        // Grouping Buttons with HBox
        HBox buttonBox = new HBox(10);
        buttonBox.setStyle("-fx-alignment: center;");
        buttonBox.getChildren().addAll(restartButton, instructionsButton, toggleThemeButton);

        // Add components to the main layout
        gameLayout.getChildren().addAll(title, gameGrid, statusLabel, buttonBox);

        Scene gameScene = new Scene(gameLayout, 400, 500);

        // Add KeyEvent listener directly to the Scene
        gameScene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                    processGuess(); // Process guess on Enter key
                    break;
                case BACK_SPACE:
                    handleBackspace(); // Handle backspace for deletions
                    break;
                default:
                    handleCharacterInput(event.getText().toLowerCase()); // Handle character input
                    break;
            }
        });

        return gameScene;
    }

    /**
     * The ThemeManager class manages the light and dark themes for the Jordle game.
     * It updates the styles of the game grid and other UI components to match the
     * selected theme.
     * 
     * @author Tumelo Ngono
     * @version 1.00
     */
    private class ThemeManager {
        private void toggleDarkMode() {
            isDarkMode = !isDarkMode; // Toggle the dark mode state
            applyTheme(); // Apply the appropriate theme
        }

        private void applyTheme() {
            if (isDarkMode) {
                // Apply dark mode styles
                gameLayout.setStyle(
                        "-fx-alignment: center; -fx-padding: 10;-fx-background-color: #2e2e2e; -fx-text-fill: white;");
                for (var node : gameGrid.getChildren()) {
                    if (node instanceof Label) {
                        Label cell = (Label) node;
                        String style = cell.getStyle();
                        if (style.contains("-fx-background-color: gray;")
                                || style.contains("-fx-background-color: green;")
                                || style.contains("-fx-background-color: yellow;")) {
                            style = style.replaceAll("-fx-border-color: black;", "-fx-border-color: white;");
                            cell.setStyle(style);
                            continue;
                        }
                        cell.setStyle("-fx-border-color: white; -fx-background-color: #444444; -fx-text-fill: white;");
                    }
                }
                title.setStyle("-fx-text-fill: white;");
                statusLabel.setStyle("-fx-text-fill: white;");
            } else {
                // Apply light mode styles
                gameLayout.setStyle(
                        "-fx-alignment: center; -fx-padding: 10;-fx-background-color: #f0f0f0; -fx-text-fill: black;");
                for (var node : gameGrid.getChildren()) {
                    if (node instanceof Label) {
                        Label cell = (Label) node;
                        String style = cell.getStyle();
                        if (style.contains("-fx-background-color: gray;")
                                || style.contains("-fx-background-color: green;")
                                || style.contains("-fx-background-color: yellow;")) {
                            style = style.replaceAll("-fx-border-color: white;", "-fx-border-color: black;");
                            cell.setStyle(style);
                            continue;
                        }
                        cell.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-text-fill: black;");
                    }
                }
                title.setStyle("-fx-text-fill: black;");
                statusLabel.setStyle("-fx-text-fill: black;");
            }
        }
    }

    private void handleBackspace() {
        if (currentGuess.length() > 0) {
            currentGuess.deleteCharAt(currentGuess.length() - 1);
            updateGrid();
        }
    }

    private void handleCharacterInput(String input) {
        if (input.matches("[a-z]") && currentGuess.length() < 5) {
            currentGuess.append(input);
            updateGrid();
        }
    }

    private void updateGrid() {
        String bColor = isDarkMode ? "white" : "black";
        String fColor = isDarkMode ? "#444444;" : "white;";
        for (int i = 0; i < 5; i++) {
            Label cell = (Label) gameGrid.getChildren().get(currentRow * 5 + i);
            if (i < currentGuess.length()) {
                cell.setText(String.valueOf(currentGuess.charAt(i)).toUpperCase());
                cell.setStyle(String.format("-fx-text-fill: %s; -fx-border-color: %s; -fx-background-color: %s", bColor,
                        bColor, fColor));
            } else {
                cell.setText("");
            }
        }
    }

    private void processGuess() {
        if (currentGuess.length() < 5) {
            incorrectGuess.play();
            vibrateRow(currentRow); // Vibrate the current row
            statusLabel.setText("Your guess must be 5 letters!");
            return;
        }

        try {
            String result = backend.check(currentGuess.toString());
            for (int i = 0; i < 5; i++) {
                Label cell = (Label) gameGrid.getChildren().get(currentRow * 5 + i);
                char feedback = result.charAt(i);
                String color = isDarkMode ? "white;" : "black;";
                if (feedback == 'g') {
                    cell.setStyle("-fx-background-color: green; -fx-text-fill: black; -fx-border-color: " + color);
                } else if (feedback == 'y') {
                    cell.setStyle("-fx-text-fill: black; -fx-background-color: yellow; -fx-border-color: " + color);
                } else {
                    cell.setStyle("-fx-text-fill: black; -fx-background-color: gray; -fx-border-color: " + color);
                }
            }

            if (result.equals("ggggg")) {
                correctGuess.play();
                flipText(currentRow);
                statusLabel.setText("Congratulations! You've guessed the word!");
                return;
            } else if (currentRow < 5) {
                incorrectGuess.play();
                vibrateRow(currentRow); // Vibrate for incorrect guess
            }

            currentRow++;
            currentGuess.setLength(0);

            if (currentRow == 6) {
                outOfAttempts.play();
                vibrateRow(currentRow);
                statusLabel.setText("Game over. The word was " + backend.getTarget() + ".");
            }
        } catch (InvalidGuessException e) {
            statusLabel.setText(e.getMessage());
        }
    }

    private void flipText(int row) {
        int delay = 0; // Delay for sequential flipping

        for (Node node : gameGrid.getChildren()) {
            if (GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) == row) {
                Label cell = (Label) node;
                Timeline flipTimeline = new Timeline(
                        // First half: Flip out (scale Y to 0)
                        new KeyFrame(Duration.millis(delay), new KeyValue(cell.scaleYProperty(), 1)),
                        new KeyFrame(Duration.millis(delay + 150), new KeyValue(cell.scaleYProperty(), 0)),

                        // Second half: Change content and flip back (scale Y to 1)
                        new KeyFrame(Duration.millis(delay + 150), e -> {
                            String color = isDarkMode ? "white;" : "black;";
                            String first = "-fx-background-color: green; -fx-border-color: " + color;
                            String second = "-fx-font-size: 20px;-fx-text-fill: " + "black;";
                            cell.setStyle(first + second);
                        }),
                        new KeyFrame(Duration.millis(delay + 300), new KeyValue(cell.scaleYProperty(), 1)));

                flipTimeline.play();
                delay += 150; // Increment delay for sequential animation
            }
        }
    }

    private void vibrateRow(int row) {
        Timeline timeline = new Timeline();
        int shakeDistance = 10; // Distance to shake in pixels

        for (Node node : gameGrid.getChildren()) {
            if (GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) == row) {
                timeline.getKeyFrames().addAll(
                        new KeyFrame(Duration.millis(0), new KeyValue(node.translateXProperty(), 0)),
                        new KeyFrame(Duration.millis(50), new KeyValue(node.translateXProperty(), shakeDistance)),
                        new KeyFrame(Duration.millis(100), new KeyValue(node.translateXProperty(), -shakeDistance)),
                        new KeyFrame(Duration.millis(150), new KeyValue(node.translateXProperty(), shakeDistance)),
                        new KeyFrame(Duration.millis(200), new KeyValue(node.translateXProperty(), -shakeDistance)),
                        new KeyFrame(Duration.millis(250), new KeyValue(node.translateXProperty(), 0)));
            }
        }

        timeline.play();
    }

    private void resetGame() {
        backend.reset();
        gameGrid.getChildren().forEach(node -> {
            Label cell = (Label) node;
            cell.setText("");
            if (isDarkMode) {
                cell.setStyle("-fx-border-color: white; -fx-background-color: #444444; -fx-text-fill: white;");
            } else {
                cell.setStyle("-fx-border-color: black; -fx-background-color: white;");
            }
        });
        statusLabel.setText("Try guessing a word!");
        currentGuess.setLength(0);
        currentRow = 0;
    }

    private void showInstructions() {
        Stage instructionsStage = new Stage();
        instructionsStage.setTitle("How to Play Jordle");

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10; -fx-background-color: #f5f5f5; -fx-alignment: center;");

        Label instructions = new Label(
                "Welcome to Jordle!\n\n"
                        + "1. Guess a 5-letter word.\n"
                        + "2. Green means correct letter and position.\n"
                        + "3. Yellow means correct letter, wrong position.\n"
                        + "4. Gray means the letter is not in the word.\n"
                        + "5. You have 6 tries. Good luck!");
        instructions.setFont(new Font("Arial", 14));

        layout.getChildren().add(instructions);

        Scene instructionsScene = new Scene(layout, 300, 200);
        instructionsStage.setScene(instructionsScene);
        instructionsStage.show();
    }

    /**
     * Main method of Jordle class.
     * Runs the GUI application
     * 
     * @param args arguement as list of Strings
     */
    public static void main(String[] args) {
        launch(args);
    }
}
