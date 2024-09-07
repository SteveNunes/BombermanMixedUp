package maps;

import java.util.ArrayList;
import java.util.List;

import drawimage_stuffs.DrawImageEffects;
import gui.util.ImageUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import tools.GameMisc;
import util.MyConverters;

public class Layer {
	
	private List<Tile> tiles;
	private List<Portal> portals;
	private List<Brick> bricks;
	private WritableImage layerImage;
	private MapSet originMapSet;
	private Color tint;
	private int layer;
	private int outX;
	private int outY;
	private int width;
	private int height;
	private double opacity;
	private DrawImageEffects effects;
	
	public Layer(MapSet originMapSet, String layerInfo, List<String> tileInfos) {
		String[] split = layerInfo.split(" ");
		if (split.length < 15)
			GameMisc.throwRuntimeException(layerInfo + " - Too few parameters");
		this.originMapSet = originMapSet;
		tint = null;
		tiles = new ArrayList<>();
		portals = new ArrayList<>();
		int n = 0, r, g, b, a;
		try {
			width = Integer.parseInt(split[n]);
			n++; height = Integer.parseInt(split[n]);
			n++; outX = Integer.parseInt(split[n]);
			n++; outY = Integer.parseInt(split[n]);
			n += 7; opacity = Double.parseDouble(split[n]);
			n++; r = Integer.parseInt(split[n]);
			n++; g = Integer.parseInt(split[n]);
			n++; b = Integer.parseInt(split[n]);
			n++; a = Integer.parseInt(split[n]);
			tint = ImageUtils.argbToColor(ImageUtils.getRgba(r, g, b, a));
			n++; effects = split.length <= n ? null : GameMisc.loadEffectsFromString(MyConverters.arrayToString(split, n));
		}
		catch (Exception e)
			{ GameMisc.throwRuntimeException(split[n] + " - Invalid parameter"); }
		for (String s : tileInfos)
			tiles.add(new Tile(originMapSet, s));
		buildLayer();
	}
	
	public void buildLayer() {
		Canvas canvas = new Canvas(width, height);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		canvas.getGraphicsContext2D().setImageSmoothing(false);
		for (Tile tile : tiles)
		ImageUtils.drawImage(gc, originMapSet.getTileSetImage(),
												 tile.tileX, tile.tileY, 16, 16,
												 tile.outX, tile.outY, 16, 16,
												 tile.flip, tile.rotate);
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

	public List<Brick> getBricks()
		{ return bricks; }

	public List<Portal> getPortals()
		{ return portals; }

	public MapSet getOriginMapSet()
		{ return originMapSet; }

	public Color getTint()
		{ return tint; }

	public int getLayer()
		{ return layer; }

	public int getOutX()
		{ return outX; }

	public int getOutY()
		{ return outY; }

	public int getWidth()
		{ return width; }

	public int getHeight()
		{ return height; }

	public void draw(GraphicsContext gc) {
		gc.drawImage(layerImage, outX, outY);
	}

}