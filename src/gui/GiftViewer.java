package gui;

import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import application.Main;
import gui.util.ImageUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.util.Duration;
import util.DurationTimerFX;
import util.IniFile;
import util.Misc;


public class GiftViewer {

	@FXML
	private Button buttonCopyId;
	@FXML
	private Button buttonCopyName;
	@FXML
	private Button buttonCopyCost;
  @FXML
  private Canvas canvasGift;
  @FXML
  private Label labelCombo;
  @FXML
  private Label labelCost;
  @FXML
  private Label labelName;
  @FXML
  private RadioButton radioSearchByName;
  @FXML
  private RadioButton radioSearchById;
  @FXML
  private RadioButton radioSearchByCost;
  @FXML
  private ComboBox<String> comboGiftList;
  @FXML
  private TextField textFieldSearch;
  
  private Map<String, Image> downloadedImages;
  private IniFile ini;
  private String lastWebpImage;
  private boolean listOnlyMissingImage;
  
	public void init() {
		listOnlyMissingImage = false;
		Misc.putTextOnClipboard("");
		lastWebpImage = null;
		downloadedImages = new HashMap<>();
		ini = IniFile.getNewIniFileInstance("appdata/gifts/gifts.ini");
		loadGiftsFromDisk();
		refreshComboList();
		buttonCopyId.setOnAction(e -> Misc.putTextOnClipboard(comboGiftList.getSelectionModel().getSelectedItem()));
		buttonCopyName.setOnAction(e -> Misc.putTextOnClipboard(labelName.getText()));
		buttonCopyCost.setOnAction(e -> Misc.putTextOnClipboard(labelCost.getText()));
		comboGiftList.valueProperty().addListener((o, oldV, newV) -> loadGift(newV));
		textFieldSearch.textProperty().addListener((o, oldV, newV) -> refreshComboList());
		DurationTimerFX.createTimer("cb", Duration.millis(100), 0, () -> checkForImagesOnClipBoard());
	}

	private void loadGiftsFromDisk() {
		ini.getSectionList().forEach(id -> {
			String name = ini.read(id, "name", "-");
			String cost = ini.read(id, "diamondCost", "-");
			String fileName = "D:/Gifts/" + id + "¡" + name + "¡" + cost + ".png";
			if (new File(fileName).exists())
				downloadedImages.put(id, new Image("file:" + fileName));
		});
	}

	private void refreshComboList() {
		comboGiftList.getItems().clear();
		ini.getSectionList().forEach(id -> {
			String name = ini.read(id, "name", "-").toLowerCase();
			String cost = ini.read(id, "diamondCost", "-");
			String search = textFieldSearch.getText().toLowerCase();
			String fileName = "D:/Gifts/" + id + "¡" + name + "¡" + cost + ".png";
			if ((!listOnlyMissingImage || !new File(fileName).exists()) &&
					(textFieldSearch.getText().isEmpty() ||
					(radioSearchByName.isSelected() && name.contains(search)) ||
					(radioSearchByCost.isSelected() && cost.equals(search)) ||
					(radioSearchById.isSelected() && id.contains(search))))
						comboGiftList.getItems().add(id);
		});
		comboGiftList.getSelectionModel().select(0);
	}

	private void loadGift(String giftId) {
		String idStr = comboGiftList.getSelectionModel().getSelectedItem();
		String name = ini.read(idStr, "name", "-");
		String cost = ini.read(idStr, "diamondCost", "-");
		labelName.setText(name);
		labelCost.setText(cost);
		labelCombo.setText(ini.read(idStr, "combo", "-"));
    new Thread(() -> {
	    if (downloadedImages.containsKey(idStr)) {
		    Platform.runLater(() -> {
		      Image pic = downloadedImages.get(idStr);
		    	canvasGift.getGraphicsContext2D().clearRect(0, 0, canvasGift.getWidth(), canvasGift.getHeight());
		    	canvasGift.getGraphicsContext2D().drawImage(pic, 0, 0, pic.getWidth(), pic.getHeight(), 10, 10, canvasGift.getWidth() - 20, canvasGift.getHeight() - 20);
	    	});
	    }
	    else if (ini.read(idStr, "image") != null) {
	    	Image i = new Image(ini.getLastReadVal());
				final Image image = i.getWidth() > 0 && i.getHeight() > 0 ? i : new Image("file:D:/Gifts/webp.png");
	    	downloadedImages.put(idStr, image);
	    	lastWebpImage = "D:/Gifts/" + idStr + "¡" + name + "¡" + cost + ".png";
    		ImageUtils.saveImageToFile(image, lastWebpImage);
	    	if (i.getWidth() > 0 && i.getHeight() > 0) {
			    Platform.runLater(() -> {
			    	canvasGift.getGraphicsContext2D().clearRect(0, 0, canvasGift.getWidth(), canvasGift.getHeight());
			    	canvasGift.getGraphicsContext2D().drawImage(image, 0, 0, image.getWidth(), image.getHeight(), 10, 10, canvasGift.getWidth() - 20, canvasGift.getHeight() - 20);
			    });
	    	}
	    	else {
	    		try {
	    			System.out.println("Vá até o navegador, e dê um 'Copiar Imagem' para que ela seja salva na pasta de Gifts");
						Desktop.getDesktop().browse(new URI(ini.getLastReadVal()));
					}
					catch (Exception e) {
			    	Misc.addErrorOnLog(e, ".\\errors.log");
						e.printStackTrace();
					}
	    	}
	    }
    }).start();
   }
	
	public void checkForImagesOnClipBoard() {
		if (Main.close) {
			DurationTimerFX.stopTimer("cb");
			return;
		}
		try {
			if (lastWebpImage != null) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				if (clipboard.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
					java.awt.Image image = (java.awt.Image) clipboard.getData(DataFlavor.imageFlavor);
					BufferedImage bufferedImage = toBufferedImage(image);
					File outputFile = new File(lastWebpImage);
					ImageIO.write(bufferedImage, "png", outputFile);
					lastWebpImage = null;
					Misc.putTextOnClipboard("");
				}
			}
		}
		catch (Exception e) {}
	}

	private static BufferedImage toBufferedImage(java.awt.Image img) {
		if (img instanceof BufferedImage)
			return (BufferedImage) img;
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bimage.createGraphics();
		g2d.drawImage(img, 0, 0, null);
		g2d.dispose();
		return bimage;
	}

}