package gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import application.Main;
import enums.Icons;
import gui.util.ControllerUtils;
import gui.util.ImageUtils;
import gui.util.ListenerHandle;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import tools.ColorMix;
import tools.Draw;
import tools.Materials;
import tools.Tools;
import util.MyFile;

public class ColorMixEditor {
	
	@FXML
	private HBox hBoxControls;
	@FXML
	private HBox hBoxSepiaToneControls;
	@FXML
	private HBox hBoxBloomControls;
	@FXML
	private HBox hBoxGlowControls;
	@FXML
	private HBox hBoxButtons;
	@FXML
	private HBox hBoxZoomControls;
	@FXML
	private VBox vBoxColorAdjustControls;
  @FXML
  private Button buttonLoadImageFromDisk;
  @FXML
  private Button buttonSaveImageToDisk;
  @FXML
  private Button buttonAddPallete;
  @FXML
  private Button buttonRemovePallete;
  @FXML
  private Button buttonZoom1;
  @FXML
  private Button buttonZoom2;
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
  private Slider sliderColorOpacity;
  @FXML
  private Label labelColorPorcent1;
  @FXML
  private Label labelColorPorcent2;
  @FXML
  private Label labelColorPorcent3;
  @FXML
  private Label labelOpacity;
  @FXML
  private Label labelColorAdjustHue;
  @FXML
  private Label labelColorAdjustSaturation;
  @FXML
  private Label labelColorAdjustBrightness;
  @FXML
  private Label labelColorTintRed;
  @FXML
  private Label labelColorTintGreen;
  @FXML
  private Label labelColorTintBlue;
  @FXML
  private Label labelColorTintOpacity;
  @FXML
  private Label labelSepiaToneLevel;
  @FXML
  private Label labelBloomThreshold;
  @FXML
  private Label labelGlowLevel;
  @FXML
  private Text textInfos;
  @FXML
  private ComboBox<Integer> comboBoxPalleteIndex;
  @FXML
  private HBox hBoxPalleteControls;
  @FXML
  private CheckBox checkBoxColorAdjust;
  @FXML
  private Slider sliderColorAdjustHue;
  @FXML
  private Slider sliderColorAdjustSaturation;
  @FXML
  private Slider sliderColorAdjustBrightness;
  @FXML
  private Slider sliderColorTintRed;
  @FXML
  private Slider sliderColorTintGreen;
  @FXML
  private Slider sliderColorTintBlue;
  @FXML
  private Slider sliderColorTintOpacity;
  @FXML
  private CheckBox checkBoxSepiaTone;
  @FXML
  private Slider sliderSepiaToneLevel;
  @FXML
  private CheckBox checkBoxBloom;
  @FXML
  private Slider sliderBloomThreshold;
  @FXML
  private CheckBox checkBoxGlow;
  @FXML
  private Slider sliderGlowLevel;
  
  private static boolean disabledControlEvents = false;

  private WritableImage originalSprite;
  private GraphicsContext gcMain;
  private String fileName;
  private List<ColorMix> palletes;
  private int currentPalleteIndex;
  private ListenerHandle<Integer> comboBoxPalletesListenerHandle;
  private int zoom;

	public void init() {
		zoom = 2;
		Main.stageMain.setTitle("Color Mix Editor");
		palletes = null;
		originalSprite = null;
		currentPalleteIndex = 0;
		gcMain = canvasMain.getGraphicsContext2D();
		gcMain.setImageSmoothing(false);
		ControllerUtils.addIconToButton(buttonLoadImageFromDisk, Icons.OPEN_FILE.getValue(), 16, 16, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonSaveImageToDisk, Icons.SAVE.getValue(), 16, 16, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonAddPallete, Icons.PLUS.getValue(), 16, 16, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonRemovePallete, Icons.DELETE.getValue(), 16, 16, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonZoom1, Icons.ZOOM_MINUS.getValue(), 16, 16, Color.WHITE, 150);
		ControllerUtils.addIconToButton(buttonZoom2, Icons.ZOOM_PLUS.getValue(), 16, 16, Color.WHITE, 150);
		hBoxControls.setDisable(true);
		hBoxSepiaToneControls.setDisable(true);
		hBoxBloomControls.setDisable(true);
		hBoxGlowControls.setDisable(true);
		vBoxColorAdjustControls.setDisable(true);
		hBoxButtons.setDisable(true);
		buttonSaveImageToDisk.setDisable(true);
		hBoxPalleteControls.setDisable(true);
		hBoxZoomControls.setDisable(true);
		buttonLoadImageFromDisk.setOnAction(e -> loadFromDisk());
		buttonSaveImageToDisk.setOnAction(e -> saveToDisk());
		buttonZoom1.setOnAction(e -> {
			if (zoom > 1)
				setZoom(zoom - 1);
		});
		buttonZoom2.setOnAction(e -> setZoom(zoom + 1));
		checkBoxColorAdjust.selectedProperty().addListener(e -> vBoxColorAdjustControls.setDisable(!checkBoxColorAdjust.isSelected()));
		checkBoxSepiaTone.selectedProperty().addListener(e -> hBoxSepiaToneControls.setDisable(!checkBoxSepiaTone.isSelected()));
		checkBoxBloom.selectedProperty().addListener(e -> hBoxBloomControls.setDisable(!checkBoxBloom.isSelected()));
		checkBoxGlow.selectedProperty().addListener(e -> hBoxGlowControls.setDisable(!checkBoxGlow.isSelected()));
		buttonAddPallete.setOnAction(e -> {
			palletes.add(new ColorMix());
			comboBoxPalleteIndex.getItems().add(palletes.size());
			currentPalleteIndex = palletes.size() - 1;
			comboBoxPalleteIndex.getSelectionModel().select(currentPalleteIndex);
			setControlls();
			updateCanvas();
		});
		comboBoxPalletesListenerHandle = new ListenerHandle<>(comboBoxPalleteIndex.valueProperty(), (obs, olvV, newV) -> {
			currentPalleteIndex = newV - 1;
			setControlls();
			updateCanvas();
		});
		setControlsAction();
	}
	
	private void setControlsAction() {
		for (RadioButton r : new RadioButton[] {radioRed1, radioGreen1, radioBlue1, radioRed2, radioGreen2, radioBlue2, radioRed3, radioGreen3, radioBlue3})
			r.setOnAction(e -> { 
				if (!disabledControlEvents) {
					updateCurrentColorMixPallete();
					updateCanvas();
				}
			});
		for (Slider s : new Slider[] {sliderColorPorcent1, sliderColorPorcent2, sliderColorPorcent3, sliderColorOpacity, sliderColorAdjustHue, sliderColorAdjustSaturation, sliderColorAdjustBrightness, sliderColorTintRed, sliderColorTintGreen, sliderColorTintBlue, sliderColorTintOpacity, sliderSepiaToneLevel, sliderBloomThreshold, sliderGlowLevel})
			s.valueProperty().addListener(e -> {
				if (!disabledControlEvents) {
					updateCurrentColorMixPallete();
					updateLabels();
					updateCanvas();
				}
			});
		for (CheckBox c : new CheckBox[] {checkBoxColorAdjust, checkBoxSepiaTone, checkBoxBloom, checkBoxGlow})
			c.selectedProperty().addListener(e -> {
				if (!disabledControlEvents) {
					updateCurrentColorMixPallete();
					updateCanvas();
				}
			});
	}

	private List<Color> getCurrentPallete() {
		return getCurrentColorMix().getColorMixPallete();
	}

	private ColorMix getCurrentColorMix() {
		return palletes.get(currentPalleteIndex);
	}

	private void updateCurrentColorMixPallete() {
		if (Tools.isColorMixPallete(getCurrentPallete())) {
			getCurrentColorMix()
				.setColorsIndex(radioRed1.isSelected() ? 0 : radioGreen1.isSelected() ? 1 : 2,
												radioRed2.isSelected() ? 0 : radioGreen2.isSelected() ? 1 : 2,
												radioRed3.isSelected() ? 0 : radioGreen3.isSelected() ? 1 : 2)
				.setColorsStrenght(sliderColorPorcent1.getValue(),
													 sliderColorPorcent2.getValue(),
													 sliderColorPorcent3.getValue())
				.setGlobalOpacity(sliderColorOpacity.getValue())
				.setColorAdjustValues(sliderColorAdjustHue.getValue(),
															sliderColorAdjustSaturation.getValue(),
															sliderColorAdjustBrightness.getValue(),
															checkBoxColorAdjust.isSelected())
				.setColorTintValues(sliderColorTintRed.getValue(),
														sliderColorTintGreen.getValue(),
														sliderColorTintBlue.getValue(),
														sliderColorTintOpacity.getValue())
				.setSepiaToneValues(sliderSepiaToneLevel.getValue(), checkBoxSepiaTone.isSelected())
				.setBloomValues(1.0 - sliderBloomThreshold.getValue(), checkBoxBloom.isSelected())
				.setGlowValues(sliderGlowLevel.getValue(), checkBoxGlow.isSelected());
		}
	}
	
	private void setControlls() {
		disabledControlEvents = true;
		textInfos.setText(Tools.isColorMixPallete(getCurrentPallete()) ? "" : "Paleta de cores editavel somente através do \"PALLETE_EDITOR\"");
		hBoxControls.setDisable(!Tools.isColorMixPallete(getCurrentPallete()));
		if (Tools.isColorMixPallete(getCurrentPallete())) {
			radioRed1.setSelected(getCurrentColorMix().getRedIndex() == 0);
			radioGreen1.setSelected(getCurrentColorMix().getRedIndex() == 1);
			radioBlue1.setSelected(getCurrentColorMix().getRedIndex() == 2);
			radioRed2.setSelected(getCurrentColorMix().getGreenIndex() == 0);
			radioGreen2.setSelected(getCurrentColorMix().getGreenIndex() == 1);
			radioBlue2.setSelected(getCurrentColorMix().getGreenIndex() == 2);
			radioRed3.setSelected(getCurrentColorMix().getBlueIndex() == 0);
			radioGreen3.setSelected(getCurrentColorMix().getBlueIndex() == 1);
			radioBlue3.setSelected(getCurrentColorMix().getBlueIndex() == 2);
			sliderColorPorcent1.setValue(getCurrentColorMix().getRedStrenght());
			sliderColorPorcent2.setValue(getCurrentColorMix().getGreenStrenght());
			sliderColorPorcent3.setValue(getCurrentColorMix().getBlueStrenght());
			sliderColorOpacity.setValue(getCurrentColorMix().getGlobalOpacity());
			sliderColorAdjustHue.setValue(getCurrentColorMix().getColorAdjustHue());
			sliderColorAdjustSaturation.setValue(getCurrentColorMix().getColorAdjustSaturation());
			sliderColorAdjustBrightness.setValue(getCurrentColorMix().getColorAdjustBrightness());
			checkBoxColorAdjust.setSelected(getCurrentColorMix().getColorAdjustState());
			sliderColorTintRed.setValue(getCurrentColorMix().getColorTintRed());
			sliderColorTintGreen.setValue(getCurrentColorMix().getColorTintGreen());
			sliderColorTintBlue.setValue(getCurrentColorMix().getColorTintBlue());
			sliderColorTintOpacity.setValue(getCurrentColorMix().getColorTintOpacity());
			checkBoxSepiaTone.setSelected(getCurrentColorMix().getSepiaToneState());
			sliderSepiaToneLevel.setValue(getCurrentColorMix().getSepiaToneLevel());
			checkBoxBloom.setSelected(getCurrentColorMix().getBloomState());
			sliderBloomThreshold.setValue(1.0 - getCurrentColorMix().getBloomThreshold());
			checkBoxGlow.setSelected(getCurrentColorMix().getGlowState());
			sliderGlowLevel.setValue(getCurrentColorMix().getGlowLevel());
		}
		disabledControlEvents = false;
		updateLabels();
	}
	
	private void updateLabels() {
		labelColorPorcent1.setText(String.format("%d%%", (int)(sliderColorPorcent1.getValue() * 100)));
		labelColorPorcent2.setText(String.format("%d%%", (int)(sliderColorPorcent2.getValue() * 100)));
		labelColorPorcent3.setText(String.format("%d%%", (int)(sliderColorPorcent3.getValue() * 100)));
		labelColorAdjustHue.setText(String.format("%d%%", (int)(sliderColorAdjustHue.getValue() * 100)));
		labelColorAdjustSaturation.setText(String.format("%d%%", (int)(sliderColorAdjustSaturation.getValue() * 100)));
		labelColorAdjustBrightness.setText(String.format("%d%%", (int)(sliderColorAdjustBrightness.getValue() * 100)));
		labelColorTintRed.setText(String.format("%d%%", (int)(sliderColorTintRed.getValue() * 100)));
		labelColorTintGreen.setText(String.format("%d%%", (int)(sliderColorTintGreen.getValue() * 100)));
		labelColorTintBlue.setText(String.format("%d%%", (int)(sliderColorTintBlue.getValue() * 100)));
		labelColorTintOpacity.setText(String.format("%d%%", (int)(sliderColorTintOpacity.getValue() * 100)));
		labelSepiaToneLevel.setText(String.format("%d%%", (int)(sliderSepiaToneLevel.getValue() * 100)));
		labelBloomThreshold.setText(String.format("%d%%", (int)(sliderBloomThreshold.getValue() * 100)));
		labelGlowLevel.setText(String.format("%d%%", (int)(sliderGlowLevel.getValue() * 100)));
		labelOpacity.setText(String.format("%d%%", (int)(sliderColorOpacity.getValue() * 100)));
	}

	void updateCanvas() {
		WritableImage image = Tools.isColorMixPallete(getCurrentPallete()) ?
				Tools.applyColorMixPalleteOnImage(originalSprite, getCurrentPallete()) :
				Tools.applyColorPalleteOnImage(originalSprite, palletes.get(0).getColorMixPallete(), getCurrentPallete());
		gcMain.setFill(Materials.getGreenColor());
		gcMain.fillRect(0, 0, canvasMain.getWidth(), canvasMain.getHeight());
		gcMain.drawImage(image, 0, 0, (int)originalSprite.getWidth(), (int)originalSprite.getHeight(),
				0, 0, (int)originalSprite.getWidth() * zoom, (int)originalSprite.getHeight() * zoom);
	}
	
	private void loadFromDisk() {
    File file = MyFile.selectFileJavaFX(Main.stageMain, "./appdata/sprites/", new FileChooser.ExtensionFilter("Imagens PNG", "*.png"), "Selecione o arquivo de sprite");
    if (file != null) {
    	fileName = file.getAbsolutePath();
			originalSprite = ImageUtils.loadWritableImageFromFile(fileName);
			int w = (int)originalSprite.getWidth(), h = (int)originalSprite.getHeight();
			canvasMain.setWidth(w * zoom);
			canvasMain.setHeight(h * zoom);
			Main.stageMain.sizeToScene();
			hBoxControls.setDisable(false);
			hBoxButtons.setDisable(false);
			buttonSaveImageToDisk.setDisable(false);
			hBoxPalleteControls.setDisable(false);
			hBoxZoomControls.setDisable(false);
			currentPalleteIndex = 0;
			List<List<Color>> palls = Tools.getPalleteListFromImage(originalSprite, Materials.getGreenColor());
			palletes = new ArrayList<>();
			if (palls == null || palls.size() == 1) {
				palletes.add(new ColorMix());
				Canvas c = new Canvas(w, h + 1);
				GraphicsContext gc = c.getGraphicsContext2D();
				gc.setImageSmoothing(false);
				gc.setFill(Materials.getGreenColor());
				gc.fillRect(0, 0, w, h + 1);
				gc.drawImage(originalSprite, 0, 0, w, h, 0, 1, w, h);
				originalSprite = Draw.getCanvasSnapshot(c);
				canvasMain.setHeight((h + 1) * zoom);
			}
			else {
				for (List<Color> pallete : palls)
					palletes.add(new ColorMix(pallete));
			}
			comboBoxPalletesListenerHandle.detach();
			for (int n = 1; n <= palletes.size(); n++)
				comboBoxPalleteIndex.getItems().add(n);
			originalSprite = (WritableImage)ImageUtils.removeBgColor(originalSprite, Materials.getGreenColor());
			for (int x = 0; x < w; x++)
				originalSprite.getPixelWriter().setColor(x, 0, Color.TRANSPARENT);
			setControlls();
			comboBoxPalleteIndex.getSelectionModel().select(0);
			comboBoxPalletesListenerHandle.attach();
			Main.stageMain.sizeToScene();
			updateCanvas();
		}
	}
	
	private void saveToDisk() {
		int x = 0, w = (int)originalSprite.getWidth(), h = (int)originalSprite.getHeight();
		int l = palletes.size();
		for (ColorMix colorMix : palletes)
			l += colorMix.getColorMixPallete().size();
		if (w < l)
			w = l;
		Canvas c = new Canvas(w, h);
		GraphicsContext gc = c.getGraphicsContext2D();
		gc.setImageSmoothing(false);
		gc.setFill(Materials.getGreenColor());
		gc.fillRect(0, 0, w, h);
		gc.drawImage(originalSprite, 0, 0);
		WritableImage image = Draw.getCanvasSnapshot(c);
		PixelWriter pw = image.getPixelWriter();
		for (int xx = 0; xx < w; xx++)
			pw.setColor(xx, 0, Color.TRANSPARENT);
		for (ColorMix colorMix : palletes) {
			for (Color color : colorMix.getColorMixPallete())
				pw.setColor(x++, 0, color);
			pw.setColor(x++, 0, Materials.getGreenColor());
		}
		while (x < w)
			pw.setColor(x++, 0, Materials.getGreenColor());
		ImageUtils.saveImageToFile(image, fileName);
	}
	
	private void setZoom(int zoom) {
		this.zoom = zoom;
		canvasMain.setWidth(originalSprite.getWidth() * zoom);
		canvasMain.setHeight(originalSprite.getHeight() * zoom);
		Main.stageMain.sizeToScene();
		updateCanvas();
	}
	
}
