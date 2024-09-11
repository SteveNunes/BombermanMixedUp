package maps;

import java.util.ArrayList;
import java.util.List;

import application.Main;
import gui.util.ImageUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Layer {
	
	private List<Tile> tiles;
	private WritableImage layerImage;
	private MapSet originMapSet;
	private int layer;
	
	public Layer(MapSet originMapSet, List<String> tileInfos) {
		this.originMapSet = originMapSet;
		tiles = new ArrayList<>();
		for (String s : tileInfos)
			tiles.add(new Tile(originMapSet, s));
		buildLayer();
	}
	
	public void buildLayer() {
		int w, h, width = 0, height = 0;
		for (Tile tile : tiles) {
			if ((w = tile.getTileDX() * Main.tileSize + 16) > width)
				width = w;
			if ((h = tile.getTileDY() * Main.tileSize + 16) > height)
				height = h;
		}
		Canvas canvas = new Canvas(width, height);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		canvas.getGraphicsContext2D().setImageSmoothing(false);
		for (Tile tile : tiles)
			ImageUtils.drawImage(gc, originMapSet.getTileSetImage(),
													 tile.spriteX, tile.spriteY, 16, 16,
													 tile.outX, tile.outY, 16, 16,
													 tile.flip, tile.rotate, tile.opacity, tile.effects);
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		layerImage = canvas.snapshot(params, null);
	}

	public WritableImage getLayerImage()
		{ return layerImage; }
	
	public void setLayerImage(WritableImage image)
		{ layerImage = image; }

	public List<Tile> getTiles()
		{ return tiles; }

	public void setTiles(List<Tile> tiles)
		{ this.tiles = new ArrayList<>(tiles); }	

	public MapSet getOriginMapSet()
		{ return originMapSet; }

	public int getLayer()
		{ return layer; }

	public int getWidth()
		{ return (int)layerImage.getWidth(); }

	public int getHeight()
		{ return (int)layerImage.getHeight(); }

}