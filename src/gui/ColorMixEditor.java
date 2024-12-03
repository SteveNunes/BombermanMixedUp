package gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import application.Main;
import enums.Icons;
import gui.util.Alerts;
import gui.util.ControllerUtils;
import gui.util.ImageUtils;
import gui.util.ListenerHandle;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import tools.Draw;
import tools.Tools;
import util.Misc;
import util.MyFile;

public class ColorMixEditor {
	
	@FXML
	private HBox hBoxControls;
	@FXML
	private HBox hBoxButtons;
  @FXML
  private Button buttonPrint;
  @FXML
  private Button buttonLoadPatternFromString;
  @FXML
  private Button buttonLoadImageFromDisk;
  @FXML
  private Button buttonSaveImageToDisk;
  @FXML
  private Canvas canvasMain;
  @FXML
  private RadioButton radioBlue1;
  @FXML
  private RadioButton radioBlue2;
  @FXML
  private RadioButton radioBlue3;
  @FXML
  private RadioButton radioGreen1;
  @FXML
  private RadioButton radioGreen2;
  @FXML
  private RadioButton radioGreen3;
  @FXML
  private RadioButton radioRed1;
  @FXML
  private RadioButton radioRed2;
  @FXML
  private RadioButton radioRed3;
  @FXML
  private Slider sliderColorPorcent1;
  @FXML
  private Slider sliderColorPorcent2;
  @FXML
  private Slider sliderColorPorcent3;
  @FXML
  private Text textColorPorcent1;
  @FXML
  private Text textColorPorcent2;
  @FXML
  private Text textColorPorcent3;
  @FXML
  private Text textInfos;
  @FXML
  private Button buttonAddPallete;
  @FXML
  private Button buttonRemovePallete;
  @FXML
  private ComboBox<Integer> comboBoxPalleteIndex;
  @FXML
  private HBox hBoxPalleteControls;
  
  private WritableImage originalSprite;
  private WritableImage currentSprite;
  private GraphicsContext gcMain;
	private Color greenColor;
  private String fileName;
  private List<List<Color>> palletes;
  private int currentPalleteIndex;
  private ListenerHandle<Integer> listenerHandle;

	public void init() {
		Main.stageMain.setTitle("Color Mix Editor");
		palletes = null;
		originalSprite = null;
		currentPalleteIndex = 0;
		greenColor = Color.valueOf("#03E313");
		gcMain = canvasMain.getGraphicsContext2D();
		gcMain.setImageSmoothing(false);
		ControllerUtils.addIconToButton(buttonLoadImageFromDisk, Icons.OPEN_FILE.getValue(), 16, 16, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonSaveImageToDisk, Icons.SAVE.getValue(), 16, 16, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonAddPallete, Icons.PLUS.getValue(), 16, 16, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonRemovePallete, Icons.DELETE.getValue(), 16, 16, Color.WHITE, 150);
		hBoxControls.setDisable(true);
		hBoxButtons.setDisable(true);
		buttonSaveImageToDisk.setDisable(true);
		hBoxPalleteControls.setDisable(true);
		buttonLoadImageFromDisk.setOnAction(e -> loadFromDisk());
		buttonSaveImageToDisk.setOnAction(e -> saveToDisk());
		buttonAddPallete.setOnAction(e -> {
			palletes.add(new ArrayList<>(Arrays.asList(Color.valueOf("#010203"), Color.valueOf("#646464"))));
			comboBoxPalleteIndex.getItems().add(palletes.size());
			currentPalleteIndex = palletes.size() - 1;
			comboBoxPalleteIndex.getSelectionModel().select(currentPalleteIndex);
			setControllsFromPattern(Tools.convertColorsToColorPattern(getCurrentPallete()));
			updateCurrentImage();
		});
		listenerHandle = new ListenerHandle<>(comboBoxPalleteIndex.valueProperty(), (obs, olvV, newV) -> {
			currentPalleteIndex = newV - 1;
			textInfos.setText(getCurrentPallete().size() == 2 ? "" : "Paleta de cores editavel somente através do \"PALLETE_EDITOR\"");
			hBoxControls.setDisable(getCurrentPallete().size() != 2);
			if (getCurrentPallete().size() == 2)
				setControllsFromPattern(Tools.convertColorsToColorPattern(getCurrentPallete()));
			updateCurrentImage();
		});
		buttonPrint.setOnAction(e -> {
			String s = Tools.colorPatternToString(getCurrentColorPattern());
			System.out.println(s);
			Misc.putTextOnClipboard(s);
		});
		buttonLoadPatternFromString.setOnAction(e -> {
			String s = Alerts.textPrompt("Prompt", "Digite o padrão de cor");
			if (s != null) {
				try {
					setControllsFromPattern(Tools.stringToColorPattern(s));
					updateCurrentImage();
				}
				catch (Exception ex) { ex.printStackTrace();
					throw new RuntimeException("Formato inválido de padrão de cor!");
				}
			}
		});
		for (RadioButton r : new RadioButton[] {radioRed1, radioGreen1, radioBlue1, radioRed2, radioGreen2, radioBlue2, radioRed3, radioGreen3, radioBlue3})
			r.setOnAction(e -> { 
				updateCurrentColorMixPallete();
				updateCurrentImage();
			});
		for (Slider s : new Slider[] {sliderColorPorcent1, sliderColorPorcent2, sliderColorPorcent3})
			s.valueProperty().addListener(e -> {
				updateCurrentColorMixPallete();
				updateCurrentImage();
			});
	}
	
	private void updateCurrentColorMixPallete() {
		if (getCurrentPallete().size() == 2) {
			Color[] colors = Tools.convertColorPatternToColors(getCurrentColorPattern());
			getCurrentPallete().set(0, colors[0]);
			getCurrentPallete().set(1, colors[1]);
			
		}
	}
	
	private List<Color> getCurrentPallete() {
		return palletes.get(currentPalleteIndex);
	}

	private void setControllsFromPattern(int[] pattern) {
		radioRed1.setSelected(pattern[0] == 1);
		radioGreen1.setSelected(pattern[0] == 2);
		radioBlue1.setSelected(pattern[0] == 3);
		radioRed2.setSelected(pattern[2] == 1);
		radioGreen2.setSelected(pattern[2] == 2);
		radioBlue2.setSelected(pattern[2] == 3);
		radioRed3.setSelected(pattern[4] == 1);
		radioGreen3.setSelected(pattern[4] == 2);
		radioBlue3.setSelected(pattern[4] == 3);
		sliderColorPorcent1.setValue(pattern[1]);
		sliderColorPorcent2.setValue(pattern[3]);
		sliderColorPorcent3.setValue(pattern[5]);
	}

	void updateCanvas() {
		gcMain.setFill(greenColor);
		gcMain.fillRect(0, 0, (int)currentSprite.getWidth() * 3, (int)currentSprite.getHeight() * 3);
		gcMain.drawImage(currentSprite, 0, 0, (int)currentSprite.getWidth(), (int)currentSprite.getHeight(), 0, 0, (int)currentSprite.getWidth() * 3, (int)currentSprite.getHeight() * 3);
		textColorPorcent1.setText(String.format("%d%%", (int)sliderColorPorcent1.getValue()));
		textColorPorcent2.setText(String.format("%d%%", (int)sliderColorPorcent2.getValue()));
		textColorPorcent3.setText(String.format("%d%%", (int)sliderColorPorcent3.getValue()));
	}
	
	private int[] getCurrentColorPattern() {
		int r = radioRed1.isSelected() ? 1 : radioGreen1.isSelected() ? 2 : 3,
				g = radioRed2.isSelected() ? 1 : radioGreen2.isSelected() ? 2 : 3,
				b = radioRed3.isSelected() ? 1 : radioGreen3.isSelected() ? 2 : 3;
		return new int[] {r, (int)sliderColorPorcent1.getValue(), g, (int)sliderColorPorcent2.getValue(), b, (int)sliderColorPorcent3.getValue()};
	}

	private void loadFromDisk() {
    File file = MyFile.selectFileJavaFX(Main.stageMain, "./appdata/sprites/", new FileChooser.ExtensionFilter("Imagens PNG", "*.png"), "Selecione o arquivo de sprite");
    if (file != null) {
    	fileName = file.getAbsolutePath();
			originalSprite = (WritableImage)ImageUtils.removeBgColor(new Image("file:" + fileName), greenColor);
			int w = (int)originalSprite.getWidth(), h = (int)originalSprite.getHeight();
			canvasMain.setWidth(w * 3);
			canvasMain.setHeight(h * 3);
			Main.stageMain.sizeToScene();
			hBoxControls.setDisable(false);
			hBoxButtons.setDisable(false);
			buttonSaveImageToDisk.setDisable(false);
			hBoxPalleteControls.setDisable(false);
			palletes = Tools.getPalleteListFromImage(originalSprite);
			if (palletes == null) {
				palletes = new ArrayList<>();
				palletes.add(new ArrayList<>(Arrays.asList(Color.valueOf("#010203"), Color.valueOf("#646464"))));
				Canvas c = new Canvas(w, h + 1);
				GraphicsContext gc = c.getGraphicsContext2D();
				gc.setImageSmoothing(false);
				gc.setFill(greenColor);
				gc.fillRect(0, 0, w, h + 1);
				gc.drawImage(originalSprite, 0, 0, w, h, 0, 1, w, h);
				originalSprite = (WritableImage)ImageUtils.removeBgColor(Draw.getCanvasSnapshot(c), greenColor);
				canvasMain.setHeight((h + 1) * 3);
			}
			listenerHandle.detach();
			for (int n = 1; n <= palletes.size(); n++)
				comboBoxPalleteIndex.getItems().add(n);
			listenerHandle.attach();
			comboBoxPalleteIndex.getSelectionModel().select(0);
			for (int x = 0; x < w; x++)
				originalSprite.getPixelWriter().setColor(x, 0, Color.TRANSPARENT);
			updateCurrentImage();
		}
	}
	
	private void updateCurrentImage() {
		if (getCurrentPallete().size() == 2)
			currentSprite = Tools.applyColorChangeOnImage(originalSprite, getCurrentColorPattern());
		else
			currentSprite = Tools.applyColorPalleteOnImage(originalSprite, palletes.get(0), getCurrentPallete());
		updateCanvas();
	}

	private void saveToDisk() {
		int x = 0, w = (int)originalSprite.getWidth(), h = (int)originalSprite.getHeight();
		Canvas c = new Canvas(w, h);
		GraphicsContext gc = c.getGraphicsContext2D();
		gc.setImageSmoothing(false);
		gc.setFill(greenColor);
		gc.fillRect(0, 0, w, h);
		gc.drawImage(originalSprite, 0, 0);
		WritableImage image = Draw.getCanvasSnapshot(c);
		PixelWriter pw = image.getPixelWriter();
		for (List<Color> colors : palletes) {
			if (x > 0)
				pw.setColor(x++, 0, greenColor);
			for (Color color : colors)
				pw.setColor(x++, 0, color);
		}
		while (x < image.getWidth())
			pw.setColor(x++, 0, greenColor);
		ImageUtils.saveImageToFile(image, fileName);
	}
	
}
