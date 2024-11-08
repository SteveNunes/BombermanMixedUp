package gui;

import java.util.Locale;

import gui.util.Alerts;
import gui.util.ImageUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import util.Misc;

public class ExplosionEditor {
	
  @FXML
  private Button buttonPrint;
  @FXML
  private Button buttonLoad;
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
  
  private WritableImage originalExplosion;
  private GraphicsContext gcMain;

	public void init() {
		originalExplosion = (WritableImage)ImageUtils.removeBgColor(new Image("file:./appdata/sprites/Explosion.png"), Color.valueOf("#03E313"));
		gcMain = canvasMain.getGraphicsContext2D();
		buttonPrint.setOnAction(e -> {
			String s = String.format(Locale.US, "{ %d, %.2f, %d, %.2f, %d, %.2f },\n",
																(radioRed1.isSelected() ? 1 : radioGreen1.isSelected() ? 2 : 3),
																sliderColorPorcent1.getValue(),
																(radioRed2.isSelected() ? 1 : radioGreen2.isSelected() ? 2 : 3),
																sliderColorPorcent1.getValue(),
																(radioRed3.isSelected() ? 1 : radioGreen3.isSelected() ? 2 : 3),
																sliderColorPorcent1.getValue());
			System.out.println(s);
			Misc.putTextOnClipboard(s);
		});
		buttonLoad.setOnAction(e -> {
			String s = Alerts.textPrompt("Prompt", "Digite o padrão de cor");
			if (s != null) {
				try {
					String[] split = s.replace("{", "").replace("}", "").replace(" ", "").split(",");
					int r = Integer.parseInt(split[0]),
							g = Integer.parseInt(split[2]),
							b = Integer.parseInt(split[4]);
					double rr = Double.parseDouble(split[1]),
								 gg = Double.parseDouble(split[3]),
								 bb = Double.parseDouble(split[5]);
					radioRed1.setSelected(r == 1);
					radioGreen1.setSelected(r == 2);
					radioBlue1.setSelected(r == 3);
					radioRed2.setSelected(g == 1);
					radioGreen2.setSelected(g == 2);
					radioBlue2.setSelected(g == 3);
					radioRed3.setSelected(b == 1);
					radioGreen3.setSelected(b == 2);
					radioBlue3.setSelected(b == 3);
					sliderColorPorcent1.setValue(rr);
					sliderColorPorcent2.setValue(gg);
					sliderColorPorcent3.setValue(bb);
					updateImage();
				}
				catch (Exception ex) { ex.printStackTrace();
					throw new RuntimeException("Formato inválido de padrão de cor!");
				}
			}
		});
		for (RadioButton r : new RadioButton[] {radioRed1, radioGreen1, radioBlue1, radioRed2, radioGreen2, radioBlue2, radioRed3, radioGreen3, radioBlue3})
			r.setOnAction(e -> updateImage());
		for (Slider s : new Slider[] {sliderColorPorcent1, sliderColorPorcent2, sliderColorPorcent3})
			s.valueProperty().addListener(e -> updateImage());
		updateImage();
	}
	
	void updateImage() {
		WritableImage i = generateExplosionImage();
		gcMain.setFill(Color.BLACK);
		gcMain.fillRect(0, 0, (int)i.getWidth() * 3, (int)i.getHeight() * 3);
		gcMain.drawImage(i, 0, 0, (int)i.getWidth(), (int)i.getHeight(), 0, 0, (int)i.getWidth() * 3, (int)i.getHeight() * 3);
		textColorPorcent1.setText(String.format("%d%%", (int)(100 * sliderColorPorcent1.getValue())));
		textColorPorcent2.setText(String.format("%d%%", (int)(100 * sliderColorPorcent2.getValue())));
		textColorPorcent3.setText(String.format("%d%%", (int)(100 * sliderColorPorcent3.getValue())));
	}

	private WritableImage generateExplosionImage() {
		int w = (int)originalExplosion.getWidth(), h = (int)originalExplosion.getHeight();
		Canvas c = new Canvas(w, h);
		GraphicsContext gc = c.getGraphicsContext2D();
		gc.setImageSmoothing(false);
		WritableImage i = new WritableImage(128, 80);
		PixelWriter pw = i.getPixelWriter();
		int r = radioRed1.isSelected() ? 1 : radioGreen1.isSelected() ? 2 : 3,
				g = radioRed2.isSelected() ? 1 : radioGreen2.isSelected() ? 2 : 3,
				b = radioRed3.isSelected() ? 1 : radioGreen3.isSelected() ? 2 : 3;
		for (int y = 0; y < h; y++)
			for (int x = 0; x < w; x++) {
				int[] rgba = ImageUtils.getRgbaArray(originalExplosion.getPixelReader().getArgb(x, y));
				int rr = (int)(rgba[r] * sliderColorPorcent1.getValue()),
						gg = (int)(rgba[g] * sliderColorPorcent2.getValue()),
						bb = (int)(rgba[b] * sliderColorPorcent3.getValue());
				if (rr + gg + bb != 0)
					pw.setColor(x, y, ImageUtils.argbToColor(ImageUtils.getRgba(rr, gg, bb)));
			}
		return i;
	}

}
