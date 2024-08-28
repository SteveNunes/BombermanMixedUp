package maps;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;

public class Layer {
	
	private List<Tile> tiles;
	private List<Portal> portals;
	private BufferedImage layer;
	private Image tileSet;
	private MapSet originMapSet;
	
	public Layer(MapSet originMapSet) {
		tiles = new ArrayList<>();
		portals = new ArrayList<>();
	}

}
