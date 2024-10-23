package entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import enums.BombType;
import enums.Curse;
import enums.Direction;
import enums.GameInputs;
import enums.ItemType;
import enums.TileProp;
import frameset.Tags;
import javafx.scene.canvas.GraphicsContext;
import maps.Item;
import maps.MapSet;
import tools.IniFiles;
import tools.Sound;

public class BomberMan extends Entity {
	
	private Curse curse;
	private int curseDuration = Curse.getDuration(curse);;
	private int fireRange;
	private int maxBombs;
	private int bomberIndex;
	private int palleteIndex;
	private String setBombSound;
	private List<Item> gotItens;
	private List<Bomb> bombs;
	private List<Direction> pressedDirs;
	private Set<GameInputs> holdedInputs;
	private List<GameInputs> queuedInputs;
	private int setBombCd;

	public BomberMan(int bomberIndex, int palleteIndex) {
		super();
		this.bomberIndex = bomberIndex;
		this.palleteIndex = palleteIndex;
		pressedDirs = new ArrayList<>();
		holdedInputs = new HashSet<>();
		queuedInputs = new ArrayList<>();
		bombs = new ArrayList<>();
		gotItens = new ArrayList<>();
		setBombCd = 0;
		curseDuration = 0;
		String section = "" + bomberIndex;
		updateStatusByItens();
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
		setBombCd--;
		for (int n = 0; n < bombs.size(); n++)
			if (!bombs.get(n).isActive())
				bombs.remove(n--);
		if (curse != null && --curseDuration == 0)
			removeCurse();
		if (!currentFrameSetNameIsEqual("Dead") && 
				MapSet.tileContainsProp(getTileCoord(), TileProp.DAMAGE_BOMB))
					setFrameSet("Dead");
		super.run(gc, isPaused);
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
				setDirection(curse == Curse.REVERSED ? dir.getReverseDirection() : dir);
			}
			if (holdedInputs.contains(GameInputs.B))
				setBomb();
			if (tileWasChanged()) {
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
					pickItem(Item.getItemAt(getTileCoord()));
			}
		}
	}

	public void setBomb() {
		if (setBombCd <= 0 && bombs.size() < maxBombs && MapSet.tileIsFree(getTileCoord())) {
			BombType type = !gotItens.isEmpty() && gotItens.get(0).getItemType().isBomb() ?
											Bomb.getBombTypeFromItem(gotItens.get(0)) : BombType.NORMAL;
			bombs.add(Bomb.addBomb(this, getTileCoord(), type, fireRange));
			Sound.playWav(setBombSound);
			setBombCd = 20;
		}
	}

	public void pickItem(Item item) {
		item.pick();
		if (item.getItemType().isBomb() && !gotItens.isEmpty())
			gotItens.add(0, item);
		else
			gotItens.add(item);
		updateStatusByItens();
	}

	public void updateStatusByItens() {
		fireRange = 2;
		maxBombs = 1;
		double speed = 0.75;
		curse = null;
		gotItens.forEach(item -> {
			ItemType type = item.getItemType();
			if (item.isCurse())
				setCurse(item.getCurse());
			else if (type == ItemType.BOMB_UP)
				maxBombs++;
			else if (type == ItemType.FIRE_MAX)
				fireRange = 9;
			else if (type == ItemType.FIRE_UP && fireRange < 9)
				fireRange++;
		});
		setSpeed(speed);
		if (curse == null || curse != Curse.INVISIBLE)
			setVisible(true);
	}

	public void removeCurse() {
		curse = null;
		updateStatusByItens();
	}
	
	public Curse getCurse()
		{ return curse; }
	
	public void setCurse(Curse curse) { // FALTA: Implementar BLINDNESS e SWAP_PLAYERS
		this.curse = curse;
		curseDuration = Curse.getDuration(curse);
		if (curse == Curse.MIN_BOMB)
			maxBombs = 1;
		else if (curse == Curse.MIN_FIRE)
			fireRange = 1;
		else if (curse == Curse.INVISIBLE)
			setVisible(false);
		else if (curse == Curse.MIN_SPEED)
			setSpeed(0.25);
		else if (curse == Curse.NO_BOMB)
			maxBombs = 0;
		else if (curse == Curse.ULTRA_SPEED)
			setSpeed(4);
	}

}
