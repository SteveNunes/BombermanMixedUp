package maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import application.Main;
import enums.ImageFlip;
import enums.TileProp;
import gui.util.ImageUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import util.MyConverters;

public class Layer {
	
	private List<Tile> tiles;
	private List<Portal> portals;
	private WritableImage layerImage;
	private MapSet originMapSet;
	private Color tint;
	private int layer;
	private int minX;
	private int minY;
	private int outX;
	private int outY;
	private int width;
	private int height;
	public static String[][] tags = new String[200][200];
	
	public Layer(MapSet originMapSet, LayerInfo layerInfo, List<String> tileInfos) {
		this.originMapSet = originMapSet;
		layer = layerInfo.layer;
		minX = layerInfo.minX;
		minY = layerInfo.minY;
		width = layerInfo.maxX;
		height = layerInfo.maxY;
		tiles = new ArrayList<>();
		portals = new ArrayList<>();
		Canvas canvas = new Canvas(width, height);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		canvas.getGraphicsContext2D().setImageSmoothing(false);
		for (String s : tileInfos) {
			TileSpriteInfos info = new TileSpriteInfos(s);
			System.out.printf("Sprite %d,%d Tela %d,%d\n", info.tileX, info.tileY, info.outX, info.outY);
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

	public List<Tile> getTiles()
		{ return tiles; }

	public List<Portal> getPortals()
		{ return portals; }

	public MapSet getOriginMapSet()
		{ return originMapSet; }

	public Color getTint()
		{ return tint; }

	public int getLayer()
		{ return layer; }

	public int getMinX()
		{ return minX; }

	public int getMinY()
		{ return minY; }

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

class TileSpriteInfos {
	
	public int layer;
	public int tileX;
	public int tileY;
	public int outX;
	public int outY;
	public ImageFlip flip;
	public int rotate;
	public TileProp tileProp;
	public Color tint;
	
	public TileSpriteInfos(String strFromIni) {
		String[] split = strFromIni.split(" ");
		if (split.length < 12)
			throw new RuntimeException(strFromIni + " - Too few parameters");
		int n = 0, r, g, b, a;
		try {
			layer = split.length <= n ? 0 : Integer.parseInt(split[n]);
			n++; outX = split.length <= n ? 0 : Integer.parseInt(split[n]) * Main.tileSize;
			n++; outY = split.length <= n ? 0 : Integer.parseInt(split[n]) * Main.tileSize;
			n++; tileX = split.length <= n ? 0 : Integer.parseInt(split[n]);
			n++; tileY = split.length <= n ? 0 : Integer.parseInt(split[n]);
			n++; int f = split.length <= n ? 0 : Integer.parseInt(split[n]);
			flip = Arrays.asList(ImageFlip.NONE, ImageFlip.HORIZONTAL, ImageFlip.VERTICAL, ImageFlip.BOTH).get(f);
			n++; rotate = split.length <= n ? 0 : Integer.parseInt(split[n]);
			n++; int p = split.length <= n ? 0 : Integer.parseInt(split[n]);
			if (p == 0)
				tileProp = TileProp.GROUND;
			else if (p == 1)
				tileProp = TileProp.WALL;
			else
				tileProp = TileProp.WALL;
			n++; r = split.length <= n ? 255 : Integer.parseInt(split[n]);
			n++; g = split.length <= n ? 255 : Integer.parseInt(split[n]);
			n++; b = split.length <= n ? 255 : Integer.parseInt(split[n]);
			n++; a = split.length <= n ? 255 : Integer.parseInt(split[n]);
			tint = ImageUtils.argbToColor(ImageUtils.getRgba(r, g, b, a));
			String str = MyConverters.arrayToString(split, 12);
			if (!str.isEmpty()) {
				int dx = outX / 16, dy = outY / 16;
				Layer.tags[dy][dx] = MyConverters.arrayToString(split, 12);
			}
		}
		catch (Exception e)
			{ throw new RuntimeException(split[n] + " - Invalid parameter"); }
	}
	
}

class LayerInfo {
	
	public int layer;
	public int minX;
	public int minY;
	public int maxX;
	public int maxY;
	public int outX;
	public int outY;
	public Color tint;
	
	public LayerInfo(int layer, String string) {
		this.layer = layer;
		minX = 9999;
		minY = 9999;
		maxX = 0;
		maxY = 0;
		outX = 0;
		outY = 0;
		tint = null;
		String[] split = string.split(" ");
		if (split.length > 14)
			throw new RuntimeException(string + " - Too many parameters");
		if (split.length < 14)
			throw new RuntimeException(string + " - Too few parameters");
		int n = 0, r, g, b, a;
		try {
			outX = split.length <= n ? 0 : Integer.parseInt(split[n]);
			n++; outY = split.length <= n ? 0 : Integer.parseInt(split[n]);
			n += 9; r = split.length <= n ? 255 : Integer.parseInt(split[n]);
			n++; g = split.length <= n ? 255 : Integer.parseInt(split[n]);
			n++; b = split.length <= n ? 255 : Integer.parseInt(split[n]);
			n++; a = split.length <= n ? 255 : Integer.parseInt(split[n]);
			tint = ImageUtils.argbToColor(ImageUtils.getRgba(r, g, b, a));
		}
		catch (Exception e)
			{ throw new RuntimeException(split[n] + " - Invalid parameter"); }
	}
	
	public void update(TileSpriteInfos info) {
		if (info.outX < minX)
			minX = info.outX;
		if (info.outY + Main.tileSize < minY)
			minY = info.outY + Main.tileSize;
		if (info.outX + Main.tileSize > maxX)
			maxX = info.outX + Main.tileSize;
		if (info.outY + Main.tileSize * 2 > maxY)
			maxY = info.outY + Main.tileSize * 2;
	}
	
}