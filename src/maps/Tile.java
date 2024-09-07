package maps;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
	public Map<TileProp, Integer> tileProp;
	public Color tint;
	private double opacity;
	private DrawImageEffects effects;
	public static String[][] tags = new String[200][200];
	
	public Tile(MapSet originMapSet, int tileX, int tileY, int outX, int outY, Map<TileProp, Integer> tileProp)
		{ this(originMapSet, tileX, tileY, outX, outY, tileProp, ImageFlip.NONE, 0, 1, Color.WHITE, null); }

	public Tile(MapSet originMapSet, int tileX, int tileY, int outX, int outY, Map<TileProp, Integer> tileProp, ImageFlip flip, int rotate, double opacity)
		{ this(originMapSet, tileX, tileY, outX, outY, tileProp, flip, rotate, opacity, Color.WHITE, null); }

	public Tile(MapSet originMapSet, int tileX, int tileY, int outX, int outY, Map<TileProp, Integer> tileProp, ImageFlip flip, int rotate, double opacity, Color tint)
		{ this(originMapSet, tileX, tileY, outX, outY, tileProp, flip, rotate, opacity, tint, null); }
	
	public Tile(MapSet originMapSet, int tileX, int tileY, int outX, int outY, Map<TileProp, Integer> tileProp, ImageFlip flip, int rotate, double opacity, Color tint, DrawImageEffects effects) {
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
		tileProp = new HashMap<>();
		String[] split = strFromIni.split(" ");
		if (split.length < 14)
			GameMisc.throwRuntimeException(strFromIni + " - Too few parameters");
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
				tileProp.put(TileProp.HIGH_WALL, p);
			else if (p == 1)
				tileProp.put(TileProp.GROUND, p);
			else if (p == 2)
				tileProp.put(TileProp.BRICK_RANDOM_SPAWNER, p);
			else if (p == 3 || p == 1015)
				tileProp.put(TileProp.WALL, p);
			else if (p == 6)
				tileProp.put(TileProp.FIXED_BRICK, p);
			else if (p == 7)
				tileProp.put(TileProp.FRAGILE_GROUND_LV1, p);
			else if (p == 8)
				tileProp.put(TileProp.DEEP_HOLE, p);
			else if (p == 14)
				tileProp.put(TileProp.MOVING_BLOCK_HOLE, p);
			else if (p == 28 || p == 38)
				tileProp.put(TileProp.WATER, p);
			else if (p > 1000 && p < 1015) {
				p -= 1000;
				if ((1 & p) != 0)
					tileProp.put(TileProp.GROUND_NO_PLAYER, p);
				if ((2 & p) != 0)
					tileProp.put(TileProp.GROUND_NO_MOB, p);
				if ((4 & p) != 0)
					tileProp.put(TileProp.GROUND_NO_BOMB, p);
				if ((8 & p) != 0)
					tileProp.put(TileProp.GROUND_NO_FIRE, p);
			}
			else
				tileProp.put(TileProp.UNKNOWN, p);
			n++; opacity = split.length <= n ? 1 : Double.parseDouble(split[n]);
			n++; r = split.length <= n ? 255 : Integer.parseInt(split[n]);
			n++; g = split.length <= n ? 255 : Integer.parseInt(split[n]);
			n++; b = split.length <= n ? 255 : Integer.parseInt(split[n]);
			n++; a = split.length <= n ? 255 : Integer.parseInt(split[n]);
			tint = ImageUtils.argbToColor(ImageUtils.getRgba(r, g, b, a));
			n++; effects = split.length <= n ? null : GameMisc.loadEffectsFromString(MyConverters.arrayToString(split, n));
			if (Main.mapEditor != null && split.length >= 14) {
				String str = MyConverters.arrayToString(split, 14);
				if (!str.isEmpty() && ((Main.mapEditor.getCurrentLayerIndex() == 26 && layer == 26) ||
						(originMapSet.getCopyLayer() != null && Main.mapEditor.getCurrentLayerIndex() == originMapSet.getCopyLayer() && layer == originMapSet.getCopyLayer())))
							tags[outY / 16][outX / 16] = str;
			}
		}
		catch (Exception e)
			{ GameMisc.throwRuntimeException(split[n] + " - Invalid parameter"); }
	}
	
	public int getTileDX()
		{ return outX / 16; }
	
	public int getTileDY()
		{ return outY / 16; }
	
}
