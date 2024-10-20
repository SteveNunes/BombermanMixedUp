package entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import enums.BombType;
import enums.Direction;
import enums.GameInputs;
import enums.TileProp;
import frameset.Tags;
import javafx.scene.canvas.GraphicsContext;
import maps.Item;
import maps.MapSet;
import tools.IniFiles;
import tools.Sound;

public class BomberMan extends Entity {
	
	private int bomberIndex;
	private int palleteIndex;
	private String setBombSound;
	private List<Direction> pressedDirs;
	private List<GameInputs> holdedInputs;
	private List<GameInputs> queuedInputs;

	public BomberMan(int bomberIndex, int palleteIndex) {
		super();
		this.bomberIndex = bomberIndex;
		this.palleteIndex = palleteIndex;
		pressedDirs = new ArrayList<>();
		holdedInputs = new ArrayList<>();
		queuedInputs = new ArrayList<>();
		String section = "" + bomberIndex;
		setSpeed(1);
		if (IniFiles.characters.read(section, "DefaultTags") != null)
			setDefaultTags(Tags.loadTagsFromString(IniFiles.characters.read(section, "DefaultTags")));
		for (String item : IniFiles.characters.getItemList(section)) {
			if (item.length() > 9 && item.substring(0, 9).equals("FrameSet."))
				addNewFrameSetFromString(item.substring(9), IniFiles.characters.read(section, item));
		}
		setBombSound = IniFiles.characters.read(section, "SetBombSound");
		if (setBombSound == null)
			setBombSound = "SetBomb";
		setFrameSet("Stand");
		if (IniFiles.characters.read(section, "DefaultStartTags") != null) {
			Tags tags = Tags.loadTagsFromString(IniFiles.characters.read(section, "DefaultStartTags"));
			tags.setRootSprite(getCurrentFrameSet().getSprite(0));
			tags.run();
		}
	}
	
	public int getBomberIndex()
		{ return bomberIndex; }

	public void setBomberIndex(int bomberIndex)
		{ this.bomberIndex = bomberIndex; }

	public int getPalleteIndex()
		{ return palleteIndex; }

	public void setPalleteIndex(int palleteIndex)
		{ this.palleteIndex = palleteIndex; }

	public boolean isPressed(GameInputs input)
		{ return holdedInputs.contains(input); }

	public void keyPress(GameInputs input) {
		if (isBlockedMovement()) {
			queuedInputs.add(input);
			return;
		}
		Direction dir = input.getDirection();
		if (dir != null && !pressedDirs.contains(dir)) {
			if (!pressedDirs.isEmpty() && (isPerfectlyBlockedDir(getDirection()) || pressedDirs.get(0).getReverseDirection() == dir))
				pressedDirs.add(0, dir);
			else
				pressedDirs.add(dir);
		}
		holdedInputs.add(input);
	}
	
	public void keyRelease(GameInputs input) {
		if (isBlockedMovement()) {
			queuedInputs.remove(input);
			return;
		}
		if (input.isDirection())
			pressedDirs.removeAll(Arrays.asList(input.getDirection()));
		holdedInputs.remove(input);
	}

	@Override
	public void run()
		{ run(null, false); }
	
	@Override
	public void run(boolean isPaused)
		{ run(null, isPaused); }
	
	@Override
	public void run(GraphicsContext gc)
		{ run(gc, false); }
	
	@Override
	public void run(GraphicsContext gc, boolean isPaused) {
		if (!isBlockedMovement()) {
			if (!queuedInputs.isEmpty()) {
				List<GameInputs> list = new ArrayList<>(queuedInputs);
				queuedInputs.clear();
				list.forEach(i -> keyPress(i));
			}
			if (pressedDirs.isEmpty()) {
				setElapsedSteps(0);
				setFrameSet("Stand");
			}
			else {
				Direction dir = pressedDirs.get(0); 
				setFrameSet("Moving");
				setDirection(dir);
			}
			if (holdedInputs.contains(GameInputs.B) && !Bomb.haveBombAt(null, getTileCoord())) {
				Bomb.addBomb(this, getTileCoord(), BombType.NORMAL, 4);
				Sound.playWav(setBombSound);
			}
		}
		super.run(gc, isPaused);
		if (!isBlockedMovement() && tileWasChanged()) {
			if (pressedDirs.size() > 1) {
				Direction dir = pressedDirs.get(1);
				if (isPerfectlyFreeDir(dir)) {
					dir = pressedDirs.get(0); 
					pressedDirs.remove(0);
					pressedDirs.add(dir);
				}
				setElapsedSteps(0);
			}
			MapSet.checkTileTrigger(this, getTileCoord(), TileProp.TRIGGER_BY_PLAYER);
			if (Item.haveItemAt(getTileCoord()))
				Item.getItemAt(getTileCoord()).pick();
		}
	}

}
