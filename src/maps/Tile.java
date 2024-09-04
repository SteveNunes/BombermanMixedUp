package maps;

import java.util.Arrays;

import application.Main;
import enums.ImageFlip;
import enums.TileProp;
import gui.util.ImageUtils;
import javafx.scene.paint.Color;
import util.MyConverters;

public class Tile {

	public int layer;
	public int tileX;
	public int tileY;
	public int outX;
	public int outY;
	public ImageFlip flip;
	public int rotate;
	public TileProp tileProp;
	public Color tint;
	public static String[][] tags = new String[200][200];
	
	public Tile(MapSet originMapSet, String strFromIni) {
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
			n++; rotate = split.length <= n ? 0 : Integer.parseInt(split[n]) * 90;
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
			if (!str.isEmpty() && (originMapSet.getCopyLayer() == null || layer != originMapSet.getCopyLayer())) {
				int dx = outX / 16, dy = outY / 16;
				tags[dy][dx] = MyConverters.arrayToString(split, 12);
			}
		}
		catch (Exception e)
			{ throw new RuntimeException(split[n] + " - Invalid parameter"); }
	}
	
}
