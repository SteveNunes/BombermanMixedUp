package gui;

import java.util.Arrays;
import java.util.List;

import entities.BomberMan;
import entities.Entity;
import enums.Icons;
import frameset.FrameSet;
import gui.util.Alerts;
import gui.util.ControllerUtils;
import gui.util.ListenerHandle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import tools.IniFiles;
import util.FindFile;
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
  private Button buttonPlay;
  @FXML
  private Button buttonCopyFrameTag;
  @FXML
  private Label labelError;
  @FXML
  private ListView<String> listViewFrameTags;
  @FXML
  private Button buttonShortcutsAdd;
  @FXML
  private Button buttonShortcutsCopy;
  @FXML
  private Button buttonShortcutsDel;
  @FXML
  private ListView<String> listViewShortcuts;
  
	private ListenerHandle<String> listenerHandleComboBox;
	private ListenerHandle<String> listenerHandleTextArea;
	private FrameSet currentFrameSet;
	private String currentFrameSetName;
	private Entity entity;
  
  public void init() {
  	entity = new Entity();
  	String font = "-fx-font-size: 14px; -fx-font-family: \"Lucida Console\";";
  	listViewFrameTags.setStyle(font);
  	listViewShortcuts.setStyle(font);
  	comboBox.setStyle(font);
  	FrameSet fs = new FrameSet();
  	fs.loadFromString(entity, "{}");
  	entity.addFrameSet("teste", fs);
		Entity.addEntityToList(entity);
		ControllerUtils.addIconToButton(buttonCopyToClipboard, Icons.COPY.getValue(), 20, 20);
		ControllerUtils.addIconToButton(buttonRefreshFrameSets, Icons.REFRESH.getValue(), 20, 20);
		ControllerUtils.addIconToButton(buttonFormat, Icons.FORMAT_TEXT.getValue(), 24, 24);
		ControllerUtils.addIconToButton(buttonSetPosition, Icons.MOVE.getValue(), 20, 20);
		ControllerUtils.addIconToButton(buttonPlay, Icons.PLAY.getValue(), 20, 20);
		ControllerUtils.addIconToButton(buttonCopyFrameTag, Icons.COPY.getValue(), 20, 20);
		ControllerUtils.addIconToButton(buttonShortcutsCopy, Icons.COPY.getValue(), 20, 20);
		ControllerUtils.addIconToButton(buttonShortcutsAdd, Icons.NEW_ITEM.getValue(), 20, 20);
		ControllerUtils.addIconToButton(buttonShortcutsDel, Icons.DELETE.getValue(), 20, 20);
		FindFile.findFile("src/frameset_tags", "*.java").forEach(file -> {
			if (!file.getName().equals("FrameTag.java"))
				listViewFrameTags.getItems().add(file.getName().replace(".java", ""));
		});
		String shortCuts = IniFiles.gameConfigs.read("FRAMESET_EDITOR", "SHORTCUTS");
		if (shortCuts != null)
			for (String shortCut : shortCuts.split("¡"))
				listViewShortcuts.getItems().add(shortCut);
		listViewShortcuts.getSelectionModel().select(0);
		listViewFrameTags.getSelectionModel().select(0);
		buttonShortcutsDel.setDisable(listViewShortcuts.getItems().isEmpty());
		buttonShortcutsCopy.setDisable(listViewShortcuts.getItems().isEmpty());
		buttonCopyFrameTag.setDisable(listViewFrameTags.getItems().isEmpty());
		buttonCopyFrameTag.setOnAction(e -> Misc.putTextOnClipboard("{" + listViewFrameTags.getSelectionModel().getSelectedItem() + "}"));
		buttonShortcutsCopy.setOnAction(e -> Misc.putTextOnClipboard(listViewShortcuts.getSelectionModel().getSelectedItem()));
		buttonCopyToClipboard.setTooltip(new Tooltip("Copiar FrameSet para o clipboard"));
		buttonFormat.setTooltip(new Tooltip("Quebrar FrameSet de linha unica para multiplas linhas"));
		buttonRefreshFrameSets.setTooltip(new Tooltip("Atualizar lista de FrameSets"));
		buttonSetPosition.setTooltip(new Tooltip("Definir posição da Entity com o FrameSet de teste"));
		buttonShortcutsAdd.setOnAction(e -> {
			String s = Alerts.textPrompt("Prompt", "Digite o novo atalho");
			if (s != null) {
				if (listViewShortcuts.getItems().contains(s)) {
					Alerts.error("Erro", "O atalho já está na lista");
					return;
				}
				listViewShortcuts.getItems().add(s);
				listViewShortcuts.getSelectionModel().select(s);
				buttonShortcutsDel.setDisable(false);
				buttonShortcutsCopy.setDisable(false);
				updateIniFile();
			}
		});
		buttonShortcutsDel.setOnAction(e -> {
			if (Alerts.confirmation("Confirmação", "Deseja mesmo excluir o atalho\n\"" + listViewShortcuts.getSelectionModel().getSelectedItem() + "\"?")) {
				listViewShortcuts.getItems().remove(listViewShortcuts.getSelectionModel().getSelectedIndex());
				if (listViewShortcuts.getItems().isEmpty()) {
					buttonShortcutsDel.setDisable(true);
					buttonShortcutsCopy.setDisable(true);
				}
				else
					listViewShortcuts.getSelectionModel().select(0);
				updateIniFile();
			}
		});
		for (RadioButton radio : Arrays.asList(radioBomber, radioRide, radioFrameSet))
  		radio.setOnAction(e -> {
  			loadFrameSetList();
  			buttonSetPosition.setDisable(radio != radioFrameSet);
  		});
		buttonPlay.setOnAction(e -> {
			if (currentFrameSet != null) {
				currentFrameSet.resetTags();
				currentFrameSet.setCurrentFrameIndex(0);
			}
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
	  				currentFrameSet.resetTags();
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
  
  private void updateIniFile() {
		StringBuilder sb = new StringBuilder();
		for (String string : listViewShortcuts.getItems()) {
			if (!sb.isEmpty())
				sb.append("¡");
			sb.append(string);
		}
		IniFiles.gameConfigs.write("FRAMESET_EDITOR", "SHORTCUTS", sb.toString());
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
		Entity.removeEntityFromList(entity);
	}

}