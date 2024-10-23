package entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import application.Main;
import enums.Direction;
import enums.PassThrough;
import enums.SpriteLayerType;
import enums.TileProp;
import maps.MapSet;
import objmoveutils.TileCoord;
import tools.Materials;
import tools.Tools;

public class Explosion {

	private List<Direction> directions;
	private int tileRange;
	private boolean passThroughAllBricks;
	private TileCoord centerCoord;
	private Entity owner;
	private int count;
	private int[] fireDis;

	private static List<Explosion> explosions = new ArrayList<>();
	
	private Explosion(Entity owner, TileCoord centerCoord, int tileRange, List<Direction> directions, boolean passThroughAllBricks) {
		this.directions = new ArrayList<>(directions);
		this.tileRange = tileRange;
		this.passThroughAllBricks = passThroughAllBricks;
		this.centerCoord = centerCoord.getNewInstance();
		this.owner = owner;
		fireDis = new int[] {0, 0, 0, 0};
		count = 0;
	}

	public static void addExplosion(TileCoord centerCoord, int tileRange, boolean passThroughAllBricks)
		{ addExplosion(null, centerCoord, tileRange, new ArrayList<>(), passThroughAllBricks); }
	
	public static void addExplosion(Entity owner, TileCoord centerCoord, int tileRange, boolean passThroughAllBricks)
		{ addExplosion(owner, centerCoord, tileRange, new ArrayList<>(), passThroughAllBricks); }

	public static void addExplosion(TileCoord centerCoord, int tileRange, Direction direction, boolean passThroughAllBricks)
		{ addExplosion(null, centerCoord, tileRange, Arrays.asList(direction), passThroughAllBricks); }

	public static void addExplosion(Entity owner, TileCoord centerCoord, int tileRange, Direction direction, boolean passThroughAllBricks)
		{ addExplosion(owner, centerCoord, tileRange, Arrays.asList(direction), passThroughAllBricks); }

	public static void addExplosion(TileCoord centerCoord, int tileRange, List<Direction> directions, boolean passThroughAllBricks)
		{ addExplosion(null, centerCoord, tileRange, directions, passThroughAllBricks); }
	
	public static void addExplosion(Entity owner, TileCoord centerCoord, int tileRange, List<Direction> directions, boolean passThroughAllBricks) {
		if (directions.isEmpty())
			directions.addAll(Arrays.asList(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT));
		explosions.add(new Explosion(owner, centerCoord, tileRange, directions, passThroughAllBricks));
	}
	
	public static void drawExplosions() {
		for (int p = explosions.size() - 1; p >= 0; p--) {
			Explosion ex = explosions.get(p);
			boolean nes = ((Bomb)ex.owner).isNesBomb();
			if (++ex.count == 1) {
				Direction dir = Direction.UP;
				ex.fireDis = new int[] {0, 0, 0, 0};
				for (int d = 0; d < 4; d++) {
					TileCoord coord = ex.centerCoord.getNewInstance();
					if (ex.directions.contains(dir))
						for (int n = 0; n < ex.tileRange; n++) {
							coord.incCoordsByDirection(dir);
							if (MapSet.tileIsFree(coord, ex.passThroughAllBricks ? Arrays.asList(PassThrough.BRICK) : null))
								ex.fireDis[d]++;
							else
								break;
						}
					dir = dir.getNext4WayClockwiseDirection();
				}
			}
			else if (ex.count == 6)
				ex.markTiles(false);
			int z = ex.count / (nes ? 4 : 5);
			z = z < (nes ? 4 : 5) ? z : (nes ? 7 : 8) - z;
			if (z == -1) {
				ex.markTiles(true);
				explosions.remove(p--);
			}
			else {
				int x = ex.centerCoord.getX() * Main.TILE_SIZE,
						y = ex.centerCoord.getY() * Main.TILE_SIZE;
				if (ex.directions.size() == 4)
					Tools.addDrawQueue(SpriteLayerType.SPRITE, Materials.mainSprites, 16, (nes ? 112 : 32) + z * 16, 16, 16, x, y, Main.TILE_SIZE, Main.TILE_SIZE); // Explosão central
				if (ex.directions.contains(Direction.UP))
					Tools.addDrawQueue(SpriteLayerType.SPRITE, Materials.explosions[nes ? 1 : 0], z * 16, ex.fireDis[0] == ex.tileRange ? 0 : 16, 16, ex.fireDis[0] * 16, x, y - ex.fireDis[0] * Main.TILE_SIZE, Main.TILE_SIZE, ex.fireDis[0] * Main.TILE_SIZE);
				if (ex.directions.contains(Direction.RIGHT))
					Tools.addDrawQueue(SpriteLayerType.SPRITE, Materials.explosions[nes ? 1 : 0], ex.fireDis[1] == ex.tileRange ? 0 : 16, 240 + z * 16, ex.fireDis[1] * 16, 16, x + Main.TILE_SIZE, y, ex.fireDis[1] * Main.TILE_SIZE, Main.TILE_SIZE, 180);
				if (ex.directions.contains(Direction.DOWN))
					Tools.addDrawQueue(SpriteLayerType.SPRITE, Materials.explosions[nes ? 1 : 0], z * 16, ex.fireDis[2] == ex.tileRange ? 0 : 16, 16, ex.fireDis[2] * 16, x, y + Main.TILE_SIZE, Main.TILE_SIZE, ex.fireDis[2] * Main.TILE_SIZE, 180);
				if (ex.directions.contains(Direction.LEFT))
					Tools.addDrawQueue(SpriteLayerType.SPRITE, Materials.explosions[nes ? 1 : 0], ex.fireDis[3] == ex.tileRange ? 0 : 16, 240 + z * 16, ex.fireDis[3] * 16, 16, x - ex.fireDis[3] * Main.TILE_SIZE, y, ex.fireDis[3] * Main.TILE_SIZE, Main.TILE_SIZE);
			}
		}
	}

	private void markTiles(boolean remove) {
		Direction dir = Direction.LEFT;
		for (int d = 0; d < 4; d++) {
			dir = dir.getNext4WayClockwiseDirection();
			if (directions.contains(dir)) {
				TileCoord coord = centerCoord.getNewInstance();
				for (int x = d == 0 ? 0 : 1; x <= tileRange; x++) {
					if (x > 0)
						coord.incCoordsByDirection(dir);
					if (x > 0 || directions.size() == 4) {
						if (MapSet.haveTilesOnCoord(coord) && !remove) {
							TileDamage.addTileDamage(owner, coord, 44).setDamageToAll();
							MapSet.checkTileTrigger(owner, coord, TileProp.TRIGGER_BY_EXPLOSION);
						}
						if (x > 0 && (MapSet.getCurrentLayer().getTileProps(coord).contains(TileProp.GROUND_NO_FIRE) || !MapSet.tileIsFree(coord, passThroughAllBricks ? Arrays.asList(PassThrough.BRICK) : null)))
							break;
					}
				}
			}
		}
	}
	
}
