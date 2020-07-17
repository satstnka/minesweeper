package jp.trans_it.game.minesweeper;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import jp.trans_it.game.minesweeper.model.Cell;
import jp.trans_it.game.minesweeper.model.Panel;

public class MainFrame implements Initializable {
	private Panel panel;
	private long startTime;
	private Timeline timeline;
	private	 Map<Cell, ToggleButton> buttonMap;

	@FXML
	private VBox box;

	@FXML
	private ComboBox<String> sizeCombo;

	@FXML
	private ComboBox<String> densityCombo;

	@FXML
	private Label remainLabel;

	@FXML
	private Label timeLabel;

	@FXML
	private void onReset(ActionEvent event) {
		this.resetAndLayout();
	}

	@FXML
	private void onClose(ActionEvent event) {
		Node node = (Node)event.getSource();
		Scene scene = node.getScene();
		Stage stage = (Stage)scene.getWindow();
		stage.close();
	}

	@FXML
	private void onSizeCombo(ActionEvent event) {
		this.resetAndLayout();
	}

	@FXML
	private void onDensityCombo(ActionEvent event) {
		this.resetAndLayout();
	}

	private void onCell(MouseEvent event) {
		ToggleButton button = (ToggleButton)event.getSource();
		Cell cell = (Cell)button.getUserData();

		if(event.getButton() == MouseButton.SECONDARY) {
			this.panel.toggleFlag(cell.getX(), cell.getY());
		}
		else {
			this.panel.open(cell.getX(), cell.getY());
		}

		this.update();
	}

	private void reset() {
		this.panel = this.createPanel();
		this.layout();
		this.startTimer();
		this.displayRemain();
	}

	private void resetAndLayout() {
		Scene scene = this.sizeCombo.getScene();
		Stage stage = (Stage)scene.getWindow();
		stage.hide();

		this.reset();

		stage.show();
	}

	private Panel createPanel() {
		int[] widths = {8, 16, 32};
		int[] heights = {8, 16, 16};
		int[] densities = {8, 12, 18};

		int sizeIndex = this.sizeCombo.getSelectionModel().getSelectedIndex();
		int densityIndex = this.densityCombo.getSelectionModel().getSelectedIndex();

		int width = widths[sizeIndex];
		int height = heights[sizeIndex];
		int density = densities[densityIndex];
		int bombs = width * height * density / 100;

		Panel panel = new Panel(width, height, bombs);
		return panel;
	}

	private void stopTimer() {
		if(this.timeline != null) {
			this.timeline.stop();
			this.timeline = null;
		}
	}

	private void startTimer() {
		this.stopTimer();

		this.startTime = System.currentTimeMillis();
		this.timeline = new Timeline(
			new KeyFrame(
				Duration.millis(1000.0),
				(event) -> {
					long currentTime = System.currentTimeMillis();

					int time = (int)(currentTime - this.startTime) / 1000;
					int second = time % 60;

					time = time / 60;
					int minute = time % 60;
					int hour = time / 60;

					String string = String.format("%02d:%02d",  minute, second);
					if(hour > 0) {
						string = String.format("%02d:", hour) + string;
					}
					this.timeLabel.setText(string);
				}
			)
		);
		this.timeline.setCycleCount(Timeline.INDEFINITE);
		this.timeline.play();
	}

	private void layout() {
		double fontSize = 20.0;
		double buttonSize = 50.0;

		Font font = new Font(fontSize);
		this.buttonMap = new HashMap<Cell, ToggleButton>();

		this.box.getChildren().clear();
		for(int y = 0; y < this.panel.getHeight(); y++) {
			HBox hbox = new HBox();
			for(int x = 0; x < this.panel.getWidth(); x++) {
				Cell cell = this.panel.getCell(x,  y);

				MainFrame me = this;

				ToggleButton button = new ToggleButton();
				button.setFont(font);
				button.setAlignment(Pos.CENTER);
				button.setSelected(false);
				button.setMinSize(buttonSize,  buttonSize);
				button.setPrefSize(buttonSize, buttonSize);
				button.setMaxSize(buttonSize,  buttonSize);
				button.setUserData(cell);
				button.setOnMouseClicked(
					(event) -> { me.onCell(event); }
				);

				hbox.getChildren().add(button);
				this.buttonMap.put(cell, button);
			}
			this.box.getChildren().add(hbox);
		}
	}

	private void update() {
		Image bomb = new Image(getClass().getResourceAsStream("/images/bomb.png"));
		Image flag = new Image(getClass().getResourceAsStream("/images/flag.png"));

		for(int x = 0; x < this.panel.getWidth(); x++) {
			for(int y = 0; y < this.panel.getHeight(); y++) {
				Cell cell = this.panel.getCell(x, y);
				ToggleButton button = this.buttonMap.get(cell);

				if(cell.isOpened()) {
					button.setSelected(true);
					if(cell.isHavingBomb()) {
						button.setGraphic(new ImageView(bomb));
					}
					else {
						button.setGraphic(null);
						int count = cell.getNeighborBombs();
						if( count > 0) {
							button.setText(Integer.toString(count));
						}
						else {
							button.setText("");
						}
					}
				}
				else {
					if(cell.isFlagged()) {
						button.setGraphic(new ImageView(flag));
					}
					else {
						button.setGraphic(null);
					}
				}
			}
		}

		displayRemain();
		judgesFinish();
	}

	private void displayRemain() {
		int remain = this.panel.getNumberOfBombs() - this.panel.countFlagged();
		this.remainLabel.setText(Integer.toString(remain));
	}

	private void judgesFinish() {
		boolean completed = this.panel.judgesCompleted();
		boolean gameOver = this.panel.judgesGameOver();

		if(completed && !gameOver) {
			Alert alert = new Alert(AlertType.INFORMATION, "Congraturations!!!", ButtonType.OK);
			alert.setTitle("成功");
			alert.setHeaderText("成功");
			alert.showAndWait();
		}

		if(completed || gameOver) {
			for(Cell cell : this.buttonMap.keySet()) {
				ToggleButton button = this.buttonMap.get(cell);
				button.setDisable(true);
			}
			this.stopTimer();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.timeline = null;

		String[] sizes = {"小さい", "普通", "大きい"};
		String[] densities = {"少ない", "普通", "多い"};

		for(String size : sizes) {
			this.sizeCombo.getItems().add(size);
		}
		this.sizeCombo.getSelectionModel().select(1);

		for(String density : densities) {
			this.densityCombo.getItems().add(density);
		}
		this.densityCombo.getSelectionModel().select(1);

		this.reset();
	}
}
