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
	public static Map<String, String> tags = new HashMap<>();
	
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
			if (p == 1)
				tileProp.put(TileProp.GROUND, p);
			if (p == 2)
				tileProp.put(TileProp.BRICK_RANDOM_SPAWNER, p);
			if (p == 3 || p == 1015)
				tileProp.put(TileProp.WALL, p);
			if (p == 5)
				tileProp.put(TileProp.JUMP_OVER, p);
			if (p == 6)
				tileProp.put(TileProp.FIXED_BRICK, p);
			if (p == 7)
				tileProp.put(TileProp.FRAGILE_GROUND_LV1, p);
			if (p == 8)
				tileProp.put(TileProp.DEEP_HOLE, p);
			if (p == 9)
				tileProp.put(TileProp.TELEPORT_FROM_FLOATING_PLATFORM, p);
			if (p == 13)
				tileProp.put(TileProp.PINE, p);
			if (p == 14)
				tileProp.put(TileProp.MOVING_BLOCK_HOLE, p);
			if (p == 28)
				tileProp.put(TileProp.WATER, p);
			if (p == 38)
				tileProp.put(TileProp.DEEP_WATER, p);
			if (p == 51)
				tileProp.put(TileProp.MAP_EDGE, p);
			// 11 - Aparece em alguns maas em camadas sem sentido algum, podendo ser substituitas pelo tipo NOTHING
			// 12 - Relacionado a area onde o P2 começa em mapas do modo aventura
			// 16, 26, 36, 46 - Relacionados a area onde os jogadores iniciam em mapas do modo batalha
			// 18 - Só aparece no mapa SBM4_1-3, e nem é visivel, podendo ser substituitas pelo tipo NOTHING
			// 41 - Tile escorregadio (Só foi usado em um mapa para testar a função de escorregar)
			// 102 - Limitador de tela da parte de baixo (teleporta bomba e jogadores para o canto de cima do lado de fora do mapa, da mesma maneira como se ele tivesse simplesmente passado do limite do mapa)
			// 103 - Limitador de tela da parte da direita (teleporta bomba e jogadores para o canto da esquerda do lado de fora do mapa, da mesma maneira como se ele tivesse simplesmente passado do limite do mapa)
			//			 102 e 103 são usados apenas em mapas que são grandes para fora da tela para uso especifico das plataformas flutuantes, agindo como limitador
			// 1016 - Só aparece no mapa SBM4_Bonus-2, e nem é visivel, podendo ser substituitas pelo tipo NOTHING
			// 1046 - Aparece nas escadas das 3 primeiras fases do SBM4
			// 1032 - Aparece nos tiles ANTES e DEPOIS das escadas marcadas com 1046
			//        Tudo indica que seja pra algo NAO cair ou passar nesses tiles, similar aos 1001-1015
			// 1061 - Só tem esse tile na fase da estação do SBM5, bem na entrada da estação
			if (p > 1000 && p < 1015) {
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
			if (tileProp.isEmpty())
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
							addStringTag(outX / 16, outY / 16, str);
			}
		}
		catch (Exception e)
			{ GameMisc.throwRuntimeException(split[n] + " - Invalid parameter"); }
	}
	
	public static void addStringTag(int tileDX, int tileDY, String tag)
		{ tags.put(tileDX + "," + tileDY, tag); }
	
	public static String getStringTag(int tileDX, int tileDY)
		{ return tags.containsKey(tileDX + "," + tileDY) ? tags.get(tileDX + "," + tileDY) : null; }
	
	public int getTileDX()
		{ return outX / 16; }
	
	public int getTileDY()
		{ return outY / 16; }
	
}
