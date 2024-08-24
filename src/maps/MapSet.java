package maps;

import java.util.ArrayList;
import java.util.List;

public class MapSet {
	
	private List<Layer> layers;
	private List<Bricks> bricks;
	private int tileSetIndex;
	
	public MapSet(int tileSetIndex) {
		this.tileSetIndex = tileSetIndex;
		layers = new ArrayList<>();
		bricks = new ArrayList<>();
	}
	
}
