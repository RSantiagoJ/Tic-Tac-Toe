/**
 * Created by Ricardo Santiago on 10/17/2016.
 * csc-220
 * HW4 - Tic-Tac-Toe
 * Email: rjsantiago0001@student.stcc.edu
 */

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class TicTacToe extends Application {

	private int turns;
	private char currentTurn;
	private Text txtCurrentTurn;
	private Text txtTurnsTaken;

	private boolean playable = true;
	private List<Combo> combos = new ArrayList<>();

	private Pane root = new Pane();
	private Tile[][] board = new Tile[3][3];
	private Line winningLine;

	private Parent createContent() {

		root.setPrefSize(600, 600);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Tile tile = new Tile();
				tile.setTranslateX(j * 200);
				tile.setTranslateY(i * 200);

				root.getChildren().add(tile);

				board[j][i] = tile;
			}
		}

		// horizontal combo
		for (int y = 0; y < 3; y++) {
			this.combos.add(new Combo(board[0][y], board[1][y], board[2][y]));
		}

		// vertical combo
		for (int x = 0; x < 3; x++) {
			this.combos.add(new Combo(board[x][0], board[x][1], board[x][2]));
		}

		// diagonal combo
		this.combos.add(new Combo(board[0][0], board[1][1], board[2][2]));
		this.combos.add(new Combo(board[2][0], board[1][1], board[0][2]));

		return root;
	}

	private BorderPane getBorderPane() {
		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(createContent());
		borderPane.setBottom(getStatsPane());
		return borderPane;
	}

	private HBox getStatsPane() {
		HBox statsPane = new HBox();
		statsPane.setPrefSize(600, 50);
		statsPane.setAlignment(Pos.CENTER);
		statsPane.setPadding(new Insets(10, 10, 10, 10));
		statsPane.setSpacing(15);
		statsPane.setStyle("-fx-font-weight: bold;-fx-font-size: 1.3em;");
		Button btReset = new Button("Reset");
		btReset.setOnAction(e -> resetBoard());

		Label lbCurrentTurn = new Label("Current Turn:");
		this.txtCurrentTurn = new Text("X");
		Label lblTurnsTaken = new Label("Turns Taken:");
		this.txtTurnsTaken = new Text("0");

		statsPane.getChildren().addAll(lbCurrentTurn, this.txtCurrentTurn, lblTurnsTaken, this.txtTurnsTaken, btReset);
		return statsPane;
	}

	private void checkGameState() {
		this.txtCurrentTurn.setText("" + getCurrentTurn());
		setTurns(this.turns + 1);
		this.txtTurnsTaken.setText("" + getTurns());

		// check for winning combnination
		for (Combo combo : this.combos) {
			if (combo.isComplete()) {

				this.playable = false;
				playWinAnimation(combo);
				break;
			}
		}

		// islearningDisabled() == true ?
		if (this.turns == 9 && this.playable == true) {
			showTieAlert();
		}
	}

	private class Tile extends StackPane {
		private Text text = new Text();

		public Tile() {

			Rectangle border = new Rectangle(200, 200);
			border.setFill(null);
			border.setStroke(Color.BLACK);

			text.setFont(Font.font(72));

			setAlignment(Pos.CENTER);
			getChildren().addAll(border, text);

			setOnMouseClicked(event -> {

				if (getValue().isEmpty() == false)
					return;

				if (!playable)
					return;

				if (getTurns() % 2 == 0)
					drawX();
				else
					drawO();

				checkGameState();
			});

		}

		public double getCenterX() {
			return getTranslateX() + 100;
		}

		public double getCenterY() {
			return getTranslateY() + 100;
		}

		public String getValue() {
			return text.getText();
		}

		private void drawX() {
			text.setText("X");
		}

		private void drawO() {
			text.setText("O");
		}

		private void clearTile() {
			text.setText("");
		}
	}

	private class Combo {
		private Tile[] tiles;

		public Combo(Tile... tiles) {
			this.tiles = tiles;
		}

		public boolean isComplete() {
			if (this.tiles[0].getValue().isEmpty())
				return false;

			return this.tiles[0].getValue().equals(this.tiles[1].getValue())
					&& this.tiles[0].getValue().equals(this.tiles[2].getValue());
		}
	}

	private void playWinAnimation(Combo combo) {
		winningLine = new Line();
		winningLine.setStroke(Color.RED);
		winningLine.setStartX(combo.tiles[0].getCenterX());
		winningLine.setStartY(combo.tiles[0].getCenterY());
		winningLine.setEndX(combo.tiles[0].getCenterX());
		winningLine.setEndY(combo.tiles[0].getCenterY());

		root.getChildren().add(winningLine);

		Timeline timeline = new Timeline();
		timeline.getKeyFrames()
				.add(new KeyFrame(Duration.seconds(2),
						new KeyValue(winningLine.endXProperty(), combo.tiles[2].getCenterX()),
						new KeyValue(winningLine.endYProperty(), combo.tiles[2].getCenterY())));
		timeline.play();

	}

	private void resetBoard() {

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {

				this.board[j][i].clearTile();
			}
		}

		root.getChildren().remove(winningLine);
		setTurns(0);
		txtTurnsTaken.setText("0");
		txtCurrentTurn.setText("X");
		this.playable = true;
	}

	public char getCurrentTurn() {

		if (getTurns() % 2 != 0)
			this.currentTurn = 'X';
		else
			this.currentTurn = 'O';

		return this.currentTurn;
	}

	public void setTurns(int turns) {
		this.turns = turns;
	}

	private int getTurns() {
		return this.turns;
	}

	public void showTieAlert() {
		Alert tieAlert = new Alert(Alert.AlertType.INFORMATION);
		tieAlert.setTitle("Information Dialog");
		tieAlert.setHeaderText(null);
		tieAlert.setContentText("Tie game! Reset to play again!");
		tieAlert.showAndWait();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setScene(new Scene(getBorderPane()));
		primaryStage.setResizable(false);
		primaryStage.setTitle("Tic-Tac-Toe");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}