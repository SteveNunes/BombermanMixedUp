package gui;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import application.Main;
import gui.util.Alerts;
import gui.util.ImageUtils;
import gui.util.ListenerHandle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tools.Draw;
import tools.Tools;
import util.Misc;
import util.MyFile;

public class PalleteEditor {
	
	@FXML
	private HBox hBoxCanvas;
	@FXML
	private VBox vBoxControls;
	@FXML
	private Button buttonLoadFromDisk;
	@FXML
	private Button buttonSaveToDisk;
  @FXML
  private Button buttonAddColor;
  @FXML
  private Button buttonAddPallete;
  @FXML
  private Button buttonRemovePallete;
  @FXML
  private Canvas canvasMain;
  @FXML
  private ComboBox<Integer> comboBoxPalleteIndex;
  @FXML
  private FlowPane flowPaneOriginalColors;
  @FXML
  private FlowPane flowPanePalleteColors;
  @FXML
  private HBox hBoxPalleteColors;

  private ContextMenu colorContextMenu;
  private Point mousePosition;
  private Point previewMousePosition;
  private ImageView pickingColorImageView;
  private int pickingColorIndex;
  private Canvas colorSquareCanvas = new Canvas(20, 20);
  private GraphicsContext colorSquareGc = colorSquareCanvas.getGraphicsContext2D();
  private ListenerHandle<Integer> listenerHandle;
	private WritableImage originalSprite;
	private WritableImage currentSprite;
	private GraphicsContext gcMain;
	private List<List<Color>> palletes;
	private int palleteIndex;
	private int blinkIndex;
	private boolean showOriginal;
	private Color greenColor;
	private String originalSpriteFileName;
	private Robot robot;
	
	public void init() {
		try {
			robot = new Robot();
			mousePosition = new Point();
			Main.stageMain.setTitle("Pallete Editor");
			gcMain = canvasMain.getGraphicsContext2D();
			gcMain.setImageSmoothing(false);
			vBoxControls.setDisable(true);
			buttonSaveToDisk.setDisable(true);
			greenColor = Color.valueOf("#03E313");
			showOriginal = false;
			pickingColorImageView = null;
			currentSprite = null;
			colorContextMenu = null;
			blinkIndex = -1;
			listenerHandle = new ListenerHandle<>(comboBoxPalleteIndex.valueProperty(), (obs, olvV, newV) -> {
				blinkIndex = -1;
				palleteIndex = newV;
				hBoxPalleteColors.setDisable(newV == 0);
				if (newV > 0)
					regeneratePalleteColors(flowPanePalleteColors);
				else
					flowPanePalleteColors.getChildren().clear();
				buttonRemovePallete.setDisable(newV == 0);
			});
			for (FlowPane flowPane : Arrays.asList(flowPaneOriginalColors, flowPanePalleteColors)) {
				flowPane.setHgap(5);
				flowPane.setOrientation(Orientation.HORIZONTAL);
			}
			canvasMain.setOnMouseMoved(e -> mousePosition.setLocation(e.getX(), e.getY()));
			canvasMain.setOnMouseClicked(e -> {
				canvasMain.requestFocus();
				if (e.getButton() == MouseButton.PRIMARY && pickingColorImageView != null) {
					Color color = originalSprite.getPixelReader().getColor((int)e.getX() / 3, (int)e.getY() / 3);
					if (originalPallete().contains(color)) {
						Alerts.error("Erro", "Essa cor já está presente na paleta principal");
						pickingColorImageView.setImage(getColoredSquare(originalPallete().get(pickingColorIndex)));
						pickingColorImageView = null;
						return;
					}
					originalPallete().set(pickingColorIndex, color);
					updateOriginalSpritePalletes();
					robot.mouseMove((int)previewMousePosition.getX(), (int)previewMousePosition.getY());
					pickingColorImageView = null;
				}
			});
			Main.sceneMain.setOnKeyPressed(e -> {
				if (e.getCode() == KeyCode.SPACE)
					showOriginal = true;
			});
			Main.sceneMain.setOnKeyReleased(e -> {
				if (e.getCode() == KeyCode.SPACE)
					showOriginal = false;
			});
			buttonAddPallete.setOnAction(e -> {
				palletes.add(new ArrayList<>(originalPallete()));
				comboBoxPalleteIndex.getItems().add(palletes.size() - 1);
				comboBoxPalleteIndex.getSelectionModel().select(palletes.size() - 1);
			});
			buttonRemovePallete.setOnAction(e -> {
				if (Alerts.confirmation("Excluir paleta", "Deseja mesmo excluir a paleta de cores atual?")) {
					listenerHandle.detach();
					comboBoxPalleteIndex.getItems().clear();
					palletes.remove(palleteIndex);
					if (palleteIndex == palletes.size())
						palleteIndex--;
					for (int n = 0; n < palletes.size(); n++)
						comboBoxPalleteIndex.getItems().add(n);
					listenerHandle.attach();
					comboBoxPalleteIndex.getSelectionModel().select(palleteIndex);
				}
			});
			buttonLoadFromDisk.setOnAction(e -> loadFromDisk());
			buttonSaveToDisk.setOnAction(e -> {
				ImageUtils.saveImageToFile(originalSprite, originalSpriteFileName);
			});
			buttonAddColor.setOnAction(e -> {
				if (originalPallete().get(originalPallete().size() - 1).equals(greenColor)) {
					Alerts.error("Erro", "Edite a cor adicionada previamente para adicionar novas cores");
					return;
				}
				for (int n = 0; n < palletes.size(); n++)
					palletes.get(n).add(greenColor);
				regeneratePalleteColors(flowPaneOriginalColors);
				regeneratePalleteColors(flowPanePalleteColors);
			});
			drawMainCanvas();
		}
		catch (Exception e) {
			e.printStackTrace();
			Main.close();
		}
	}
	
	private void loadFromDisk() {
    File file = MyFile.selectFileJavaFX(Main.stageMain, "./appdata/sprites/", new FileChooser.ExtensionFilter("Imagens PNG", "*.png"), "Selecione o arquivo de sprite");
    if (file != null) {
			originalSpriteFileName = file.getAbsoluteFile().toString();
			originalSprite = ImageUtils.loadWritableImageFromFile(originalSpriteFileName);
			canvasMain.setWidth(originalSprite.getWidth() * 3);
			canvasMain.setHeight(originalSprite.getHeight() * 3);
			Main.stageMain.sizeToScene();
			loadPallete();
			vBoxControls.setDisable(false);
			buttonSaveToDisk.setDisable(false);
		}
	}
	
	private WritableImage getColoredSquare(Color color) {
		colorSquareGc.setFill(color);
		colorSquareGc.fillRect(0, 0, colorSquareCanvas.getWidth(), colorSquareCanvas.getHeight());
		return Draw.getCanvasSnapshot(colorSquareCanvas);
	}
	
	private void drawMainCanvas() {
		if (originalSprite != null) {
			updateCurrentSprite();
			gcMain.drawImage(showOriginal ? originalSprite : currentSprite, 0, 0, (int)currentSprite.getWidth(), (int)currentSprite.getHeight(), 0, 0, (int)currentSprite.getWidth() * 3, (int)currentSprite.getHeight() * 3);
			if (pickingColorImageView != null) {
				int x = (int)mousePosition.getX(), y = (int)mousePosition.getY();
				Color color = originalSprite.getPixelReader().getColor(x / 3, y / 3);
				pickingColorImageView.setImage(getColoredSquare(color));
				gcMain.setStroke(Color.BLACK);
				gcMain.setLineWidth(4);
				gcMain.setFill(greenColor);
				gcMain.fillRect(x - 150, y - 150, 300, 300);
				gcMain.drawImage(originalSprite, x / 3 - 15, y / 3 - 15, 30, 30, x - 150, y - 150, 300, 300);
				gcMain.strokeRect(x - 150, y - 150, 300, 300);
			}
		}
		Tools.getFPSHandler().fpsCounter();
		if (!Main.close)
			Platform.runLater(() -> drawMainCanvas());
	}

	private void loadPallete() {
		listenerHandle.detach();
		palleteIndex = 0;
		palletes = new ArrayList<>();
		PixelReader pr = originalSprite.getPixelReader();
		int index = -1, w = (int)originalSprite.getWidth();
		Color prev = greenColor;
		for (int x = 0; x < w; x++) {
			int[] rgba = ImageUtils.getRgbaArray(pr.getArgb(x, 0));
			int r = rgba[1], g = rgba[2], b = rgba[3];
			Color color = ImageUtils.argbToColor(ImageUtils.getRgba(r, g, b));
			if (color.equals(greenColor)) {
				if (prev.equals(greenColor))
					break;
			}
			else {
				if (prev.equals(greenColor)) {
					palletes.add(new ArrayList<>());
					index++;
				}
				palletes.get(index).add(color);
			}
			prev = color;
		}
		comboBoxPalleteIndex.getItems().clear();
		for (int n = 0; n < palletes.size(); n++)
			comboBoxPalleteIndex.getItems().add(n);
		listenerHandle.attach();
		comboBoxPalleteIndex.getSelectionModel().select(0);
		regeneratePalleteColors(flowPaneOriginalColors);
	}
	
	private List<Color> originalPallete() {
		return palletes.get(0);
	}
	
	private List<Color> currentPallete() {
		return palletes.get(palleteIndex);
	}
	
	private void regeneratePalleteColors(FlowPane flowPane) {
		List<Color> colors = flowPane == flowPaneOriginalColors ? originalPallete() : currentPallete();
		flowPane.getChildren().clear();
		flowPane.setPrefWidth(colors.size() * 25);
		for (int n = 0; n < colors.size(); n++) {
			ImageView iv = new ImageView(getColoredSquare(colors.get(n)));
			final int n2 = n;
			iv.setOnMouseMoved(e -> blinkIndex = n2);
			iv.setOnMouseExited(e -> blinkIndex = -1);
			if (flowPane == flowPanePalleteColors)
				setColorPicker(iv, n);
			else {
				iv.setOnMouseClicked(e -> {
					if (e.getButton() == MouseButton.PRIMARY) {
						pickingColorIndex = n2;
						pickingColorImageView = iv;
		      	previewMousePosition = MouseInfo.getPointerInfo().getLocation();
		      	robot.mouseMove((int) (canvasMain.localToScreen(0, 0).getX() + canvasMain.getWidth() / 2),
		      												(int) (canvasMain.localToScreen(0, 0).getY() + canvasMain.getHeight() / 2));
		      }
					else if (e.getButton() == MouseButton.SECONDARY) {
						colorContextMenu = new ContextMenu(); 
						MenuItem menuItem = new MenuItem("Excluir cor");
						menuItem.setOnAction(ex -> {
							if (Alerts.confirmation("Excluir cor", "Deseja mesmo excluir a cor selecionada da paleta de cores atual?")) {
								originalPallete().remove(n2);
								currentPallete().remove(n2);
								regeneratePalleteColors(flowPaneOriginalColors);
								regeneratePalleteColors(flowPanePalleteColors);
							}
						});
						colorContextMenu.getItems().add(menuItem);
						colorContextMenu.show(canvasMain, e.getScreenX(), e.getScreenY());
					}
				});
			}
			flowPane.getChildren().add(iv);
		}
	}

	private void setColorPicker(ImageView iv, int colorIndex) {
		iv.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				ColorPicker colorPicker = new ColorPicker(currentPallete().get(colorIndex));
				colorPicker.valueProperty().addListener((o, oldC, newC) ->{
					currentPallete().set(colorIndex, newC);
					iv.setImage(getColoredSquare(newC));
				});
				Stage stage = new Stage();
				VBox vBox = new VBox(colorPicker);
				Scene scene = new Scene(vBox);
				stage.setTitle("Select a color");
				stage.setOnCloseRequest(ex -> updateOriginalSpritePalletes());
				stage.setScene(scene);
				stage.show();
			}
		});
	}

	private void updateCurrentSprite() {
		int w = (int)originalSprite.getWidth(), h = (int)originalSprite.getHeight();
		currentSprite = new WritableImage(w, h);
		PixelReader pr = originalSprite.getPixelReader();
		PixelWriter pw = currentSprite.getPixelWriter();
		for (int y = 0; y < h; y++)
			for (int x = 0; x < w; x++) {
				Color color = ImageUtils.argbToColor(pr.getArgb(x, y));
				if (y == 0)
					pw.setColor(x, y, greenColor);
				else if (!color.equals(greenColor) && palleteIndex > 0 && originalPallete().contains(color)) {
					int i = originalPallete().indexOf(color);
					Color color2 = currentPallete().get(i);
					pw.setColor(x, y, blinkIndex != i || !Misc.blink(100) ? color2 : greenColor);
				}
				else
					pw.setColor(x, y, color);
			}
	}
	
	private void updateOriginalSpritePalletes() {
		int x = 0;
		PixelWriter pw = originalSprite.getPixelWriter();
		for (int xx = 0; xx < originalSprite.getWidth(); xx++)
			pw.setColor(xx++, 0, greenColor);
		for (List<Color> colors : palletes) {
			for (Color color : colors)
				pw.setColor(x++, 0, color);
			pw.setColor(x++, 0, greenColor);
		}
	}

}
