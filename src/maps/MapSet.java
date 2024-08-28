package maps;

import java.util.ArrayList;
import java.util.List;

public class MapSet {
	
	private List<Layer> layers;
	private List<Bricks> bricks;
	private String mapName;
	
	public MapSet(String mapName) {
		this.mapName = mapName;
		layers = new ArrayList<>();
		bricks = new ArrayList<>();
	}
	
}
