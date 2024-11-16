package damage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import application.Main;
import entities.Entity;
import enums.Direction;
import enums.PassThrough;
import enums.SpriteLayerType;
import enums.TileProp;
import javafx.scene.image.WritableImage;
import maps.MapSet;
import objmoveutils.TileCoord;
import tools.Draw;
import tools.Materials;

public class Explosion {

	private List<Direction> directions;
	private int tileRange;
	private boolean passThroughAllBricks;
	private boolean passThroughAny;
	private Set<PassThrough> passThrough;
	private TileCoord centerCoord;
	private Entity owner;
	private int count;
	private int[] fireDis;
	private int explosionIndex;

	private static List<Explosion> explosions = new ArrayList<>();

	private Explosion(Entity owner, TileCoord centerCoord, int tileRange, List<Direction> directions, int explosionIndex, boolean passThroughAllBricks) {
		this.directions = new ArrayList<>(directions);
		this.tileRange = tileRange;
		this.passThroughAllBricks = passThroughAllBricks;
		this.centerCoord = centerCoord.getNewInstance();
		this.owner = owner;
		this.explosionIndex = explosionIndex;
		passThroughAny = false;
		fireDis = new int[] { 0, 0, 0, 0 };
		count = 0;
		passThrough = new HashSet<>(Set.of(PassThrough.HOLE, PassThrough.WATER, PassThrough.PLAYER, PassThrough.MONSTER, PassThrough.ITEM));
		if (passThroughAllBricks)
			passThrough.add(PassThrough.BRICK);
	}
	
	public void setPassThroughAny(boolean state) {
		passThroughAny = state;
	}

	public static Explosion addExplosion(TileCoord centerCoord, int tileRange, boolean passThroughAllBricks) {
		return addExplosion(null, centerCoord, tileRange, new ArrayList<>(), 1, passThroughAllBricks);
	}

	public static Explosion addExplosion(Entity owner, TileCoord centerCoord, int tileRange, boolean passThroughAllBricks) {
		return addExplosion(owner, centerCoord, tileRange, new ArrayList<>(), 1, passThroughAllBricks);
	}

	public static Explosion addExplosion(TileCoord centerCoord, int tileRange, Direction direction, boolean passThroughAllBricks) {
		return addExplosion(null, centerCoord, tileRange, Arrays.asList(direction), 1, passThroughAllBricks);
	}

	public static Explosion addExplosion(Entity owner, TileCoord centerCoord, int tileRange, Direction direction, boolean passThroughAllBricks) {
		return addExplosion(owner, centerCoord, tileRange, Arrays.asList(direction), 1, passThroughAllBricks);
	}

	public static Explosion addExplosion(TileCoord centerCoord, int tileRange, List<Direction> directions, boolean passThroughAllBricks) {
		return addExplosion(null, centerCoord, tileRange, directions, 1, passThroughAllBricks);
	}

	public static Explosion addExplosion(Entity owner, TileCoord centerCoord, int tileRange, List<Direction> directions, boolean passThroughAllBricks) {
		return addExplosion(owner, centerCoord, tileRange, directions, 1, passThroughAllBricks);
	}

	public static Explosion addExplosion(TileCoord centerCoord, int tileRange, int explosionIndex, boolean passThroughAllBricks) {
		return addExplosion(null, centerCoord, tileRange, new ArrayList<>(), explosionIndex, passThroughAllBricks);
	}

	public static Explosion addExplosion(Entity owner, TileCoord centerCoord, int tileRange, int explosionIndex, boolean passThroughAllBricks) {
		return addExplosion(owner, centerCoord, tileRange, new ArrayList<>(), explosionIndex, passThroughAllBricks);
	}

	public static Explosion addExplosion(TileCoord centerCoord, int tileRange, Direction direction, int explosionIndex, boolean passThroughAllBricks) {
		return addExplosion(null, centerCoord, tileRange, Arrays.asList(direction), explosionIndex, passThroughAllBricks);
	}

	public static Explosion addExplosion(Entity owner, TileCoord centerCoord, int tileRange, Direction direction, int explosionIndex, boolean passThroughAllBricks) {
		return addExplosion(owner, centerCoord, tileRange, Arrays.asList(direction), explosionIndex, passThroughAllBricks);
	}

	public static Explosion addExplosion(TileCoord centerCoord, int tileRange, List<Direction> directions, int explosionIndex, boolean passThroughAllBricks) {
		return addExplosion(null, centerCoord, tileRange, directions, explosionIndex, passThroughAllBricks);
	}

	public static Explosion addExplosion(Entity owner, TileCoord centerCoord, int tileRange, List<Direction> directions, int explosionIndex, boolean passThroughAllBricks) {
		if (directions.isEmpty())
			directions.addAll(Arrays.asList(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT));
		Explosion explosion = new Explosion(owner, centerCoord, tileRange, directions, explosionIndex, passThroughAllBricks);
		explosions.add(explosion);
		return explosion;
	}

	public static void drawExplosions() {
		for (int p = explosions.size() - 1; p >= 0; p--) {
			Explosion ex = explosions.get(p);
			WritableImage sprite = Materials.loadedSprites.get("Explosion" + ex.explosionIndex);
			if (++ex.count == 1) {
				Direction dir = Direction.UP;
				ex.fireDis = new int[] { 0, 0, 0, 0 };
				for (int d = 0; d < 4; d++) {
					TileCoord coord = ex.centerCoord.getNewInstance();
					if (ex.directions.contains(dir))
						for (int n = 0; n < ex.tileRange; n++) {
							coord.incCoordsByDirection(dir);
							if (ex.passThroughAny || (MapSet.tileHaveProps(coord) && !MapSet.getTileProps(coord).contains(TileProp.GROUND_NO_FIRE) && MapSet.tileIsFree(coord, ex.passThrough)))
								ex.fireDis[d]++;
							else
								break;
						}
					dir = dir.getNext4WayClockwiseDirection();
				}
			}
			else if (ex.count == 6)
				ex.markTiles(false);
			int z = ex.count / 5;
			z = z < 5 ? z : 8 - z;
			if (z == -1) {
				ex.markTiles(true);
				explosions.remove(p--);
			}
			else {
				int x = ex.centerCoord.getX() * Main.TILE_SIZE, y = ex.centerCoord.getY() * Main.TILE_SIZE;
				if (ex.directions.size() == 4)
					Draw.addDrawQueue(SpriteLayerType.SPRITE, sprite, 80 + z * 16, 224, 16, 16, x, y, Main.TILE_SIZE, Main.TILE_SIZE); // Explosão central
				if (ex.directions.contains(Direction.UP))
					Draw.addDrawQueue(SpriteLayerType.SPRITE, sprite, z * 16, ex.fireDis[0] == ex.tileRange ? 0 : 16, 16, ex.fireDis[0] * 16, x, y - ex.fireDis[0] * Main.TILE_SIZE, Main.TILE_SIZE, ex.fireDis[0] * Main.TILE_SIZE);
				if (ex.directions.contains(Direction.RIGHT))
					Draw.addDrawQueue(SpriteLayerType.SPRITE, sprite, ex.fireDis[1] == ex.tileRange ? 0 : 16, 240 + z * 16, ex.fireDis[1] * 16, 16, x + Main.TILE_SIZE, y, ex.fireDis[1] * Main.TILE_SIZE, Main.TILE_SIZE, 180);
				if (ex.directions.contains(Direction.DOWN))
					Draw.addDrawQueue(SpriteLayerType.SPRITE, sprite, z * 16, ex.fireDis[2] == ex.tileRange ? 0 : 16, 16, ex.fireDis[2] * 16, x, y + Main.TILE_SIZE, Main.TILE_SIZE, ex.fireDis[2] * Main.TILE_SIZE, 180);
				if (ex.directions.contains(Direction.LEFT))
					Draw.addDrawQueue(SpriteLayerType.SPRITE, sprite, ex.fireDis[3] == ex.tileRange ? 0 : 16, 240 + z * 16, ex.fireDis[3] * 16, 16, x - ex.fireDis[3] * Main.TILE_SIZE, y, ex.fireDis[3] * Main.TILE_SIZE, Main.TILE_SIZE);
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
							TileDamage.addTileDamage(coord.getNewInstance(), 40).addDamageTileProps(TileProp.EXPLOSION);
							MapSet.checkTileTrigger(owner, coord.getNewInstance(), TileProp.TRIGGER_BY_EXPLOSION);
						}
						if (x > 0 && (!MapSet.haveTilesOnCoord(coord) || MapSet.getCurrentLayer().getTileProps(coord).contains(TileProp.GROUND_NO_FIRE) || !MapSet.tileIsFree(coord, passThrough)))
							break;
					}
				}
			}
		}
	}

}
