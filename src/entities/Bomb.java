package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import enums.BombType;
import enums.Direction;
import enums.ImageFlip;
import enums.TileProp;
import gui.util.ImageUtils;
import javafx.scene.canvas.GraphicsContext;
import maps.MapSet;
import maps.Tile;
import tools.Materials;
import tools.Sound;

public class Bomb extends Entity {

	private static Map<TileCoord, Bomb> bombs = new HashMap<>();
	private MapSet originMapSet;
	private Entity owner;
	BombType type;
	private int timer;
	private int fireDistance;
	private int[] fireDis = {0, 0, 0, 0};

	
	public Bomb(Bomb bomb) {
		super(bomb);
		setTileSize(Main.tileSize);
		originMapSet = bomb.originMapSet;
		owner = bomb.owner;
		type = bomb.type;
		timer = bomb.timer;
		fireDistance = bomb.fireDistance;
	}
	
	public Bomb(MapSet originMapSet, Entity owner, TileCoord coord, BombType type, int fireDistance) {
		super();
		setTileSize(Main.tileSize);
		this.originMapSet = originMapSet;
		this.type = type;
		this.fireDistance = fireDistance;
		int y = 32 + 16 * type.getValue();
		timer = type == BombType.REMOTE || type == BombType.SPIKED_REMOTE ? -1 : 0;
		String fragileGroundFrameSet = "{SetSprSource;MainSprites;64;" + y + ";16;16;0;0;0;0;16;16},{SetTicksPerFrame;15},{SetSprIndex;0}|{SetSprIndex;1}|{SetSprIndex;2}|{SetSprIndex;3}|{Goto;0}";
		addNewFrameSetFromString("StandFrames", fragileGroundFrameSet);
		setFrameSet("StandFrames");
		setPosition(coord.getPosition(Main.tileSize));
	}
	
	public static void addBomb(MapSet originMapSet, TileCoord coord) {
	}
	
	public static void addBomb(Bomb bomb)
		{ bombs.put(bomb.getTileCoord(), bomb); }

	public static void removeBomb(Bomb bomb)
		{ removeBomb(bomb.getTileCoord()); }
	
	public static void removeBomb(TileCoord coord) {
		if (haveBombAt(coord)) {
			Bomb bomb = bombs.get(coord);
			TileCoord coord2 = coord.getNewInstance();
			coord2.setY(coord.getY() + 1);
			bombs.remove(coord);
			Tile.removeTileShadow(bomb.originMapSet, coord2);
		}
	}
	
	public static void clearBombs() {
		if (!bombs.isEmpty()) {
			Bomb bomb = null;
			while (!bombs.isEmpty()) {
				bomb = bombs.values().iterator().next();
				removeBomb(bomb.getTileCoord());
			}
			bomb.originMapSet.getLayer(26).buildLayer();
		}
	}
	
	public static int totalBombs()
		{ return bombs.size(); }

	public static List<Bomb> getBombs()
		{ return new ArrayList<>(bombs.values()); }
	
	public static void drawBombs(GraphicsContext gc) {
		List<Bomb> removeBombs = new ArrayList<>();
		for (Bomb bomb : bombs.values()) {
			if (++bomb.timer < 180 && bomb.originMapSet.tileContainsProp(bomb.getTileCoord(), TileProp.EXPLOSION))
				bomb.timer = 180;
			if (bomb.timer >= 180) {
				if (bomb.timer == 180) {
					Sound.playWav("Explosion" + (int)(bomb.fireDistance / 3));
					Direction dir = Direction.UP;
					for (int d = 0; d < 4; d++)
						bomb.fireDis[d] = 0;
					for (int d = 0; d < 4; d++) {
						TileCoord coord = bomb.getTileCoord().getNewInstance();
						for (int n = 0; n < bomb.fireDistance; n++) {
							coord.incByDirection(dir);
							if (bomb.originMapSet.tileIsFree(coord))
								bomb.fireDis[d]++;
							else
								break;
						}
						dir = dir.getNext4WayClockwiseDirection();
					}
				}
				if (bomb.timer == 188)
					bomb.markTiles(false);
				int z = (bomb.timer - 180) / 5;
				z = z < 5 ? z : 8 - z; 
				if (z == -1) {
					bomb.markTiles(true);
					removeBombs.add(bomb);
				}
				else {
					ImageUtils.drawImage(gc, Materials.mainSprites, 16, 32 + z * 16, 16, 16, (int)bomb.getX(), (int)bomb.getY(), Main.tileSize, Main.tileSize); // Explosão central
					ImageUtils.drawImage(gc, Materials.explosions, z * 16, bomb.fireDis[0] == bomb.fireDistance ? 0 : 16, 16, bomb.fireDis[0] * 16, (int)bomb.getX(), (int)bomb.getY() - bomb.fireDis[0] * Main.tileSize, Main.tileSize, bomb.fireDis[0] * Main.tileSize); // Explosão pra cima
					ImageUtils.drawImage(gc, Materials.explosions, bomb.fireDis[1] == bomb.fireDistance ? 0 : 16, 240 + z * 16, bomb.fireDis[1] * 16, 16, (int)bomb.getX() + Main.tileSize, (int)bomb.getY(), bomb.fireDis[1] * Main.tileSize, Main.tileSize, 180); // Explosão pra direita
					ImageUtils.drawImage(gc, Materials.explosions, z * 16, bomb.fireDis[2] == bomb.fireDistance ? 0 : 16, 16, bomb.fireDis[2] * 16, (int)bomb.getX(), (int)bomb.getY() + Main.tileSize, Main.tileSize, bomb.fireDis[2] * Main.tileSize, 180); // Explosão pra baixo
					ImageUtils.drawImage(gc, Materials.explosions, bomb.fireDis[3] == bomb.fireDistance ? 0 : 16, 240 + z * 16, bomb.fireDis[3] * 16, 16, (int)bomb.getX() - bomb.fireDis[3] * Main.tileSize, (int)bomb.getY(), bomb.fireDis[3] * Main.tileSize, Main.tileSize); // Explosão pra esquerda
				}
			}
			else
				bomb.run(gc, false);
		}
		removeBombs.forEach(bomb -> removeBomb(bomb));
	}
	
	private void markTiles(boolean remove) {
		Direction dir = Direction.LEFT;
		for (int d = 0; d < 4; d++) {
			dir = dir.getNext4WayClockwiseDirection();
			TileCoord coord = getTileCoord().getNewInstance();
			for (int x = 0; x <= fireDistance; x++) {
				if (x > 0)
					coord.incByDirection(dir);
				if (originMapSet.haveTilesOnCoord(coord)) {
					if (remove)
						originMapSet.removePropFromTile(coord, TileProp.EXPLOSION);
					else
						originMapSet.addPropToTile(coord, TileProp.EXPLOSION);
				}
				if (x > 0 && !originMapSet.tileIsFree(coord))
					break;
			}
		}
	}

	public static boolean haveBombAt(TileCoord coord)
		{ return bombs.containsKey(coord); }

}
