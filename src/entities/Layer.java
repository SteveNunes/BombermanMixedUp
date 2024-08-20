package entities;

import java.awt.image.BufferedImage;
import java.util.List;

import javafx.scene.image.Image;

public class Layer {
	
	private List<Tile> tiles;
	private BufferedImage layer;
	private Image tileSet;
	private MapSet originMapSet;
	
	public Layer(MapSet originMapSet) {
		//this.tileSet = Materials.tileSets.get(this.tileSetIndex = tileSetIndex);
	}

}
