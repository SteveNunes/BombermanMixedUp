package entities;

import application.Main;
import enums.BombType;
import enums.Direction;
import enums.GameInput;
import enums.SpriteLayerType;
import enums.TileProp;
import maps.MapSet;
import objmoveutils.Position;
import objmoveutils.TileCoord;
import tools.Draw;
import tools.Materials;
import util.Misc;

public class BomberShip {

	private BomberMan bomberMan;
	private BomberMan victim;
	private TileCoord lastCoord;
	private TileCoord victimDeadCoord;
	private Direction direction;
	private int speed;
	private boolean isBusy;
	private long pressedCTime;
	private Bomb bomb;
	
	public BomberShip(BomberMan bomberMan, TileCoord coord) {
		this.bomberMan = bomberMan;
		speed = 1;
		direction = getDirectionFromCurrentTileProp(coord);
		bomberMan.setFrameSet("BomberShip.Entering_" + direction);
		bomberMan.setPosition(coord.getPosition());
		lastCoord = coord.getNewInstance();
		pressedCTime = 0;
		isBusy = true;
		victim = null;
		victimDeadCoord = null;
	}
	
	public BomberMan getVictim() {
		return victim;
	}
	
	public void setVictim(BomberMan victim) {
		this.victim = victim;
		victimDeadCoord = victim.getTileCoordFromCenter().getNewInstance();
	}
	
	public boolean bIsPressed() {
		return pressedCTime > 0;
	}
	
	public void pressB() {
		pressedCTime = System.currentTimeMillis();
	}
	
	private TileCoord getReleaseBombTile() {
		if (!bIsPressed())
			return null;
		int distance = (int)((System.currentTimeMillis() - pressedCTime) + 200) / 200;
		if (distance < 2)
			distance = 2;
		if (distance > 5)
			distance = 5;
		Direction dir = getDirectionFromCurrentTileProp();
		return bomberMan.getTileCoordFromCenter().getNewInstance().incCoordsByDirection(dir, distance);
	}
	
	public void releaseB() {
		if (!isBusy && bomberMan.isPerfectTileCentred() && (bomb == null || bomb.wasExploded())) {
			Direction dir = getDirectionFromCurrentTileProp();
			bomb = new Bomb(bomberMan, bomberMan.getTileCoordFromCenter(), BombType.NORMAL, 3);
			bomb.setDirection(dir);
			bomb.jumpTo(bomberMan, getReleaseBombTile(), 4, 1.2, 20);
			Bomb.addBomb(bomb);
			bomberMan.setFrameSet("BomberShip.Fire_" + dir.name());
		}
		pressedCTime = 0;
	}
	
	public TileCoord getLastCoord() {
		return lastCoord;
	}
	
	public void setLastCoord(TileCoord coord) {
		lastCoord.setCoords(coord);
	}

	public void setLastCoord(Position position) {
		setLastCoord(position.getTileCoord());
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	private Direction getDirectionFromCurrentTileProp() {
		return getDirectionFromCurrentTileProp(bomberMan.getTileCoordFromCenter());
	}
	
	private Direction getDirectionFromCurrentTileProp(TileCoord coord) {
		if (!MapSet.getBomberShipTileList().contains(coord))
			return null;
		TileProp prop = MapSet.getTileProps(coord).get(0);
		return prop != TileProp.BOMBER_SHIP_CORNER ? Direction.valueOf(prop.name().substring(12)) : null;
		
	}
	
	public void setDirection(Direction dir) {
		Direction dir2 = getDirectionFromCurrentTileProp();
		if (dir2 != null && dir.get4DirValue() % 2 != dir2.get4DirValue() % 2)
			direction = dir;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public void updateFrameSet(boolean isStopped) {
		if (!bomberMan.isDead())
			return;
		String currentFrameSet = bomberMan.getCurrentFrameSetName();
		isBusy = MapSet.tileContainsProp(bomberMan.getTileCoordFromCenter(), TileProp.BOMBER_SHIP_CORNER) || (!currentFrameSet.substring(11, 16).equals("Stand") && !currentFrameSet.substring(11, 17).equals("Moving"));
		if (!isBusy) {
			Direction dir = getDirectionFromCurrentTileProp();
			if (victim != null && !victim.currentFrameSetNameIsEqual("Dead"))
				bomberMan.setFrameSet("BomberShip.LeavingAndRevive_" + dir.name());
			else if (dir.get4DirValue() % 2 != direction.get4DirValue() % 2) {
				String frameSet;
				if (isStopped)
					frameSet = "BomberShip.Stand_" + dir.name();
				else
					frameSet = "BomberShip.Moving_" + dir.name() + "_" + direction.name();
				if (frameSet != null && !currentFrameSet.substring(11).equals(frameSet.substring(11)))
					bomberMan.setFrameSet(frameSet);
			}
		}
	}
	
	public void run() {
		if (bIsPressed()) {
			TileCoord coord = getReleaseBombTile();
			Draw.addDrawQueue(SpriteLayerType.CEIL, Materials.mainSprites, Misc.blink(100) ? 761 : 776, 221, 15, 15, (int)coord.getPosition().getX(), (int)coord.getPosition().getY(), 15, 15);
			if (!bomberMan.getHoldedInputs().contains(GameInput.B))
				releaseB();
		}
		for (GameInput input : bomberMan.getHoldedInputs()) {
			if (input.isDirection()) {
				if (bomberMan.isPerfectTileCentred() && !bomberMan.getPressedDirs().contains(input.getDirection()))
					bomberMan.getPressedDirs().add(input.getDirection());
				else if (!bomberMan.isPerfectTileCentred() && !bomberMan.getPressedDirs().contains(input.getDirection()) && bomberMan.getPressedDirs().contains(input.getDirection().getReverseDirection())) {
					bomberMan.getPressedDirs().add(input.getDirection());
					bomberMan.getPressedDirs().remove(input.getDirection().getReverseDirection());
				}
			}
		}
		if (!bomberMan.getPressedDirs().isEmpty()) {
			Direction dir = bomberMan.getPressedDirs().get(0);
			setDirection(dir);
			bomberMan.moveEntity(dir, getSpeed());
			if (bomberMan.isPerfectTileCentred()) {
				bomberMan.getPressedDirs().clear();
				if (MapSet.tileContainsProp(bomberMan.getTileCoordFromCenter(), TileProp.BOMBER_SHIP_CORNER))
					for (Direction dir2 : Direction.values4Directions()) {
						TileCoord coord = bomberMan.getTileCoordFromCenter().getNewInstance().incCoordsByDirection(dir2);
						if (MapSet.getBomberShipTileList().contains(coord) && !coord.equals(getLastCoord())) {
							bomberMan.getPressedDirs().add(dir2);
							break;
						}
					}
				if (bomberMan.getPressedDirs().isEmpty())
					for (GameInput input : bomberMan.getHoldedInputs())
						if (input.getDirection() == dir) {
							bomberMan.getPressedDirs().add(input.getDirection());
							break;
						}
				setSpeed(!(bomberMan.getHoldedInputs().contains(GameInput.E) || bomberMan.getHoldedInputs().contains(GameInput.F)) ? 1 : bomberMan.getHoldedInputs().contains(GameInput.F) ? 4 : 2);
				setLastCoord(bomberMan.getTileCoordFromCenter());
			}
		}
		updateFrameSet(bomberMan.getPressedDirs().isEmpty());
	}
	
	public void reviveFromBomberShip() {
		if (!MapSet.hurryUpIsActive() && !Main.isFreeze() && !MapSet.stageObjectiveIsCleared() && Draw.getFade() == null && BomberMan.getBomberAlives() > 1) {
			bomberMan.reviveAndClearItens();
			bomberMan.setPosition(victimDeadCoord.getPosition());
			bomberMan.setInvencibleFrames(180);
			bomberMan.unsetBomberShip();
		}
	}
	
	public void disableBomberShip() {
		if (!bomberMan.getCurrentFrameSetName().substring(11, 18).equals("Leaving")) {
			Direction dir = getDirectionFromCurrentTileProp();
			bomberMan.setFrameSet("BomberShip.Leaving_" + dir.name());
		}
	}
	
}
