package gui;

import java.util.Arrays;
import java.util.List;

import application.Main;
import entities.BomberMan;
import entities.Entity;
import frameset.FrameSet;
import gui.util.Alerts;
import gui.util.ListenerHandle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Font;
import objmoveutils.Position;
import util.Misc;

public class FrameSetEditor {

  @FXML
  private ComboBox<String> comboBox;
  @FXML
  private RadioButton radioBomber;
  @FXML
  private RadioButton radioFrameSet;
  @FXML
  private RadioButton radioRide;
  @FXML
  private ToggleGroup radios;
  @FXML
  private TextArea textArea;
  @FXML
  private Button buttonRefreshFrameSets;
  @FXML
  private Button buttonCopyToClipboard;
  @FXML
  private Button buttonSetPosition;
  @FXML
  private Button buttonFormat;
  @FXML
  private Label labelError;
  
	private ListenerHandle<String> listenerHandleComboBox;
	private ListenerHandle<String> listenerHandleTextArea;
	private FrameSet currentFrameSet;
	private String currentFrameSetName;
	private Entity entity;
  
  public void init() {
  	textArea.setFont(new Font("Lucida Console", 16));
  	entity = new Entity();
  	FrameSet fs = new FrameSet();
  	fs.loadFromString(entity, "{}");
  	entity.addFrameSet("teste", fs);
		Entity.addEntityToList(new Position(Main.mainCanvas.getWidth() / Main.getZoom(), Main.mainCanvas.getHeight() / Main.getZoom()).getTileCoord(), entity);
  	for (RadioButton radio : Arrays.asList(radioBomber, radioRide, radioFrameSet))
  		radio.setOnAction(e -> {
  			loadFrameSetList();
  			buttonSetPosition.setDisable(radio != radioFrameSet);
  		});
  	buttonSetPosition.setOnAction(e -> {
  		String s = Alerts.textPrompt("Prompt", "Digite a posição no formato X,Y");
  		if (s != null) {
  			try {
  				String[] split = s.split(",");
  				int x = Integer.parseInt(split[0]), y = Integer.parseInt(split[1]);
  				entity.setPosition(x, y);
  			}
  			catch (Exception ex) {
  				Alerts.error("Erro", "Formato inválido para posição");
  			}
  		}
  	});
  	buttonRefreshFrameSets.setOnAction(e -> loadFrameSetList());
  	buttonFormat.setOnAction(e -> formatTextArea(getFrameSetFromTextArea()));
  	buttonCopyToClipboard.setOnAction(e -> {
  		if (currentFrameSet != null)
  			Misc.putTextOnClipboard(getFrameSetFromTextArea());
  	});
  	listenerHandleComboBox = new ListenerHandle<>(comboBox.valueProperty(), (o, oldValue, newValue) -> {
  		currentFrameSetName = newValue;
    	if (radioBomber.isSelected())
    		currentFrameSet = BomberMan.getBomberMan(0).getFrameSet(newValue);
    	else if (radioRide.isSelected())
    		currentFrameSet = BomberMan.getBomberMan(0).getRide().getFrameSet(newValue);
    	else
    		currentFrameSet = entity.getFrameSet("teste");
  		loadCurrentFrameSet();
  	});
  	listenerHandleTextArea = new ListenerHandle<>(textArea.textProperty(), (o, oldValue, newValue) -> {
  		if (currentFrameSet != null) {
  			labelError.setText("");
				FrameSet.removePreLoadedFrameSet(currentFrameSetName);
  			try {
  				Entity entity = null;
  				if (currentFrameSetName != null) {
  		    	if (radioBomber.isSelected() && BomberMan.getBomberMan(0).haveFrameSet(currentFrameSetName))
  		    		entity = BomberMan.getBomberMan(0);
  		    	else if (radioRide.isSelected() && BomberMan.getBomberMan(0).getRide().haveFrameSet(currentFrameSetName))
  		    		entity = BomberMan.getBomberMan(0).getRide();
  		    	else if (this.entity.haveFrameSet(currentFrameSetName))
  		    		entity = this.entity;
  				}
  				if (entity != null) {
    				newValue = getFrameSetFromTextArea();
    				FrameSet frameSet = new FrameSet(entity);
    				frameSet.loadFromString(entity, newValue);
	  				entity.removeFrameSet(currentFrameSetName);
	  				entity.addFrameSet(currentFrameSetName, currentFrameSet = frameSet);
	  				entity.setFrameSet(currentFrameSetName);
  				}
  			}
  			catch (Exception e) {
  				labelError.setText(e.getMessage());
  			}
  		}
  	});
  	buttonSetPosition.setDisable(true);
  	loadFrameSetList();
  }
  
  private String getFrameSetFromTextArea() {
  	return textArea.getText().replace(" ", "").replace("\n", "");
  }
  
  private void loadCurrentFrameSet() {
		if (currentFrameSet != null)
			formatTextArea(currentFrameSet.getStringFromFrameSetTags());
	}

	private void formatTextArea(String string) {
  	listenerHandleTextArea.detach();
  	textArea.clear();
		List<String> frames = Arrays.asList(string.split("\\|"));
  	for (int n = 0; n < frames.size(); n++) {
  		String frame = frames.get(n);
  		List<String> sprites = Arrays.asList(frame.split(",,"));
  		if (n > 0)
  			textArea.appendText("|");
	  	for (int n2 = 0; n2 < sprites.size(); n2++) {
	  		String sprite = sprites.get(n2);
	  		if (sprite.isEmpty())
	  			sprite = "{}";
	  		textArea.appendText((n2 == 0 ? "" : ",,") + sprite + "\n");
	  	}
  	}
  	listenerHandleTextArea.attach();
	}

	private void loadFrameSetList() {
		currentFrameSet = null;
  	listenerHandleComboBox.detach();
  	comboBox.getItems().clear();
  	if (radioBomber.isSelected() && BomberMan.getTotalBomberMans() > 0) {
  		for (String frameSet : BomberMan.getBomberMan(0).getFrameSetsNames())
  			comboBox.getItems().add(frameSet);
  	}
  	else if (radioRide.isSelected() && BomberMan.getTotalBomberMans() > 0 && BomberMan.getBomberMan(0).getRide() != null) {
  		for (String frameSet : BomberMan.getBomberMan(0).getRide().getFrameSetsNames())
  			comboBox.getItems().add(frameSet);
  	}
  	else if (radioFrameSet.isSelected())
  		comboBox.getItems().add("Default FrameSet");
  	if (!comboBox.getItems().isEmpty()) {
  		comboBox.getItems().sort((s1, s2) -> s1.compareTo(s2));
	  	listenerHandleComboBox.attach();
			comboBox.getSelectionModel().select(0);
  	}
  	loadCurrentFrameSet();
  }
  
	void close() {
		Entity.removeEntityFromList(entity.getTileCoord(), entity);
	}

}