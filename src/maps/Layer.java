package maps;

import java.util.ArrayList;
import java.util.List;

import gui.util.ImageUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Layer {
	
	private List<Brick> tiles;
	private List<Portal> portals;
	private WritableImage layerImage;
	private MapSet originMapSet;
	private Color tint;
	private int layer;
	private int outX;
	private int outY;
	private int width;
	private int height;
	
	public Layer(MapSet originMapSet, String layerInfo, List<String> tileInfos) {
		String[] split = layerInfo.split(" ");
		if (split.length > 14)
			throw new RuntimeException(layerInfo + " - Too many parameters");
		if (split.length < 14)
			throw new RuntimeException(layerInfo + " - Too few parameters");
		this.originMapSet = originMapSet;
		tint = null;
		tiles = new ArrayList<>();
		portals = new ArrayList<>();
		int n = 0, r, g, b, a;
		try {
			width = split.length <= n ? 0 : Integer.parseInt(split[n]);
			n++; height = split.length <= n ? 0 : Integer.parseInt(split[n]);
			n++; outX = split.length <= n ? 0 : Integer.parseInt(split[n]);
			n++; outY = split.length <= n ? 0 : Integer.parseInt(split[n]);
			n += 7; r = split.length <= n ? 255 : Integer.parseInt(split[n]);
			n++; g = split.length <= n ? 255 : Integer.parseInt(split[n]);
			n++; b = split.length <= n ? 255 : Integer.parseInt(split[n]);
			n++; a = split.length <= n ? 255 : Integer.parseInt(split[n]);
			tint = ImageUtils.argbToColor(ImageUtils.getRgba(r, g, b, a));
		}
		catch (Exception e)
			{ throw new RuntimeException(split[n] + " - Invalid parameter"); }
		Canvas canvas = new Canvas(width, height);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		canvas.getGraphicsContext2D().setImageSmoothing(false);
		for (String s : tileInfos) {
			Tile info = new Tile(originMapSet, s);
			ImageUtils.drawImage(gc, originMapSet.getTileSetImage(),
					info.tileX,
					info.tileY,
					16, 16,
					info.outX,
					info.outY,
					16, 16,
					info.flip,
					info.rotate);
		}
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		layerImage = canvas.snapshot(params, null);
	}
	
	public WritableImage getLayerImage()
		{ return layerImage; }
	
	public void setLayerImage(WritableImage image)
		{ layerImage = image; }

	public List<Brick> getTiles()
		{ return tiles; }

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