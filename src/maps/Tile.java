package maps;

import java.util.Arrays;

import application.Main;
import drawimage_stuffs.DrawImageEffects;
import enums.ImageFlip;
import enums.TileProp;
import gui.util.ImageUtils;
import javafx.scene.paint.Color;
import tools.GameMisc;
import util.MyConverters;

public class Tile {

	public MapSet originMapSet;
	public int layer;
	public int tileX;
	public int tileY;
	public int outX;
	public int outY;
	public ImageFlip flip;
	public int rotate;
	public TileProp tileProp;
	public Color tint;
	private double opacity;
	private DrawImageEffects effects;
	public static String[][] tags = new String[200][200];
	
	public Tile(MapSet originMapSet, int tileX, int tileY, int outX, int outY, TileProp tileProp)
		{ this(originMapSet, tileX, tileY, outX, outY, tileProp, ImageFlip.NONE, 0, 1, Color.WHITE, null); }

	public Tile(MapSet originMapSet, int tileX, int tileY, int outX, int outY, TileProp tileProp, ImageFlip flip, int rotate, double opacity)
		{ this(originMapSet, tileX, tileY, outX, outY, tileProp, flip, rotate, opacity, Color.WHITE, null); }

	public Tile(MapSet originMapSet, int tileX, int tileY, int outX, int outY, TileProp tileProp, ImageFlip flip, int rotate, double opacity, Color tint)
		{ this(originMapSet, tileX, tileY, outX, outY, tileProp, flip, rotate, opacity, tint, null); }
	
	public Tile(MapSet originMapSet, int tileX, int tileY, int outX, int outY, TileProp tileProp, ImageFlip flip, int rotate, double opacity, Color tint, DrawImageEffects effects) {
		this.originMapSet = originMapSet;
		this.tileX = tileX;
		this.tileY = tileY;
		this.outX = outX;
		this.outY = outY;
		this.flip = flip;
		this.rotate = rotate;
		this.tileProp = tileProp;
		this.tint = tint;
		this.opacity = opacity;
		this.effects = effects;
	}
	
	public Tile(MapSet originMapSet, String strFromIni) {
		String[] split = strFromIni.split(" ");
		if (split.length < 14)
			throw new RuntimeException(strFromIni + " - Too few parameters");
		int n = 0, r, g, b, a;
		this.originMapSet = originMapSet;
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
			n++; opacity = split.length <= n ? 1 : Double.parseDouble(split[n]);
			n++; r = split.length <= n ? 255 : Integer.parseInt(split[n]);
			n++; g = split.length <= n ? 255 : Integer.parseInt(split[n]);
			n++; b = split.length <= n ? 255 : Integer.parseInt(split[n]);
			n++; a = split.length <= n ? 255 : Integer.parseInt(split[n]);
			tint = ImageUtils.argbToColor(ImageUtils.getRgba(r, g, b, a));
			n++; effects = split.length <= n ? null : GameMisc.loadEffectsFromString(MyConverters.arrayToString(split, n));
			if (Main.mapEditor != null && split.length > 16) {
				String str = MyConverters.arrayToString(split, 16);
				if (!str.isEmpty() && ((Main.mapEditor.getCurrentLayerIndex() == 26 && layer == 26) ||
						(originMapSet.getCopyLayer() != null && Main.mapEditor.getCurrentLayerIndex() == originMapSet.getCopyLayer() && layer == originMapSet.getCopyLayer())))
							tags[outY / 16][outX / 16] = str;
			}
		}
		catch (Exception e)
			{ throw new RuntimeException(split[n] + " - Invalid parameter"); }
	}
	
	public int getTileDX()
		{ return outX / 16; }
	
	public int getTileDY()
		{ return outY / 16; }
	
}
