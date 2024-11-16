package maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.BomberMan;
import entities.Effect;
import entities.Entity;
import enums.Curse;
import enums.Direction;
import enums.Elevation;
import enums.ItemType;
import enums.PassThrough;
import enums.TileProp;
import frameset.FrameSet;
import frameset_tags.SetSprIndex;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import objmoveutils.JumpMove;
import objmoveutils.Position;
import objmoveutils.TileCoord;
import tools.Materials;
import tools.RGBColor;
import tools.Sound;
import util.MyMath;
import util.TimerFX;

public class Item extends Entity {

	private static Map<TileCoord, Item> items = new HashMap<>();
	private static List<Item> itemList = new ArrayList<>();
	private static RGBColor itemEdigeColor;

	private Curse curse;
	private ItemType itemType;
	private int startInvFrames;

	public static void createItemEdgeImage() {
		Materials.loadedSprites.put("ItemEdge", new WritableImage(18, 18));
		itemEdigeColor = new RGBColor(20);
	}

	public Item() {
		this(new Position(), null);
	}

	public Item(TileCoord coord) {
		this(coord.getPosition(), null);
	}

	public Item(TileCoord coord, ItemType itemType) {
		this(coord.getPosition(), itemType);
	}

	public Item(Item item) {
		this(item.getPosition(), item.itemType);
	}

	public Item(Position position) {
		this(position, null);
	}

	public Item(Position position, ItemType itemType) {
		setPosition(new Position(position));
		this.itemType = itemType;
		startInvFrames = 20;
		addNewFrameSetFromIniFile(this, "StandFrameSet", "FrameSets", "ITEM", "StandFrameSet");
		addNewFrameSetFromIniFile(this, "JumpingFrameSet", "FrameSets", "ITEM", "JumpingFrameSet");
		setUpItemPickUpFrameSet();
		setFrameSet("StandFrameSet");
		setPassThroughs(true, PassThrough.MONSTER, PassThrough.PLAYER);
		if (itemType == ItemType.CURSE_SKULL)
			curse = Curse.getRandom();
		else
			curse = null;
	}
	
	@Override
	public void setFrameSet(String frameSetName) {
		super.setFrameSet(frameSetName);
		int itemIndex = itemType.getValue() - 1;
		FrameSet frameSet = getFrameSet(frameSetName);
			frameSet.changeTagValues(tag -> {
				if (tag instanceof SetSprIndex && ((SetSprIndex)tag).value != null && ((SetSprIndex)tag).value == -1)
					((SetSprIndex)tag).value = itemIndex;
			});
	}
	
	public void jumpToRandomTileAround(int radius) {
		jumpTo(getTileCoordFromCenter().getNewInstance()
				.incCoordsByDirection(Direction.get8DirectionFromValue((int) MyMath.getRandom(0, 7)), radius));
	}

	public void jumpTo(TileCoord coord) {
		setFrameSet("JumpingFrameSet");
		jumpTo(this, coord, 6, 1.2, 20);
		startInvFrames = 30;
	}

	private void setUpItemPickUpFrameSet() {
		if (haveFrameSet("PickedUpFrameSet"))
			replaceFrameSetFromIniFile(this, "PickedUpFrameSet", "FrameSets", "ITEM", "PickedUpFrameSet");
		else
			addNewFrameSetFromIniFile(this, "PickedUpFrameSet", "FrameSets", "ITEM", "PickedUpFrameSet");
	}

	public boolean isCurse() {
		return curse != null;
	}

	public Curse getCurse() {
		return curse;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	public void setItemType(int itemId) {
		itemType = ItemType.getItemById(itemId);
	}

	public static void addItem(TileCoord coord) {
		addItem(new Item(coord, null), false);
	}

	public static void addItem(TileCoord coord, ItemType itemType) {
		addItem(new Item(coord, itemType), false);
	}

	public static void addItem(Item item) {
		itemList.add(item);
	}
	
	public static void addItem(TileCoord coord, boolean startJumpingAround) {
		addItem(new Item(coord, null), startJumpingAround);
	}

	public static void addItem(TileCoord coord, ItemType itemType, boolean startJumpingAround) {
		addItem(new Item(coord, itemType), startJumpingAround);
	}

	public static void addItem(Item item, boolean startJumpingAround) {
		if (startJumpingAround || !haveItemAt(item.getTileCoordFromCenter())) {
			itemList.add(item);
			if (!item.tileIsFree(item.getTileCoordFromCenter()) &&
					(MapSet.tileContainsProp(item.getTileCoordFromCenter(), TileProp.DAMAGE_ITEM) ||
					 MapSet.tileContainsProp(item.getTileCoordFromCenter(), TileProp.EXPLOSION)))
						item.destroy();
			else if (startJumpingAround || !MapSet.tileIsFree(item.getTileCoordFromCenter()))
				item.jumpToRandomTileAround(2);
			else {
				putOnMap(item.getTileCoordFromCenter().getNewInstance(), item);
				MapSet.checkTileTrigger(item, item.getTileCoordFromCenter(), TileProp.TRIGGER_BY_ITEM);
			}
		}
	}

	public static void removeItem(Item item) {
		itemList.remove(item);
		items.remove(item.getTileCoordFromCenter());
	}

	public static void removeItem(TileCoord coord) {
		if (haveItemAt(coord)) {
			itemList.remove(items.get(coord));
			items.remove(coord);
		}
	}

	public static void clearItems() {
		items.clear();
		itemList.clear();
	}

	public static int totalItems() {
		return itemList.size();
	}

	public static List<Item> getItems() {
		return itemList;
	}

	public static Map<TileCoord, Item> getItemMap() {
		return items;
	}

	public static void drawItems() {
		List<Item> tempItems = new ArrayList<>(itemList);
		WritableImage i = Materials.loadedSprites.get("ItemEdge");
		Color c = itemEdigeColor.getColor();
		for (int y = 0; y < 18; y++) {
			i.getPixelWriter().setColor(0, y, c);
			i.getPixelWriter().setColor(17, y, c);
		}
		for (int x = 0; x < 18; x++) {
			i.getPixelWriter().setColor(x, 0, c);
			i.getPixelWriter().setColor(x, 17, c);
		}

		for (Item item : tempItems) {
			if (--item.startInvFrames <= 0 && item.getCurrentFrameSetName().equals("StandFrameSet") &&
					(MapSet.tileContainsProp(item.getTileCoordFromCenter(), TileProp.DAMAGE_ITEM) ||
					 MapSet.tileContainsProp(item.getTileCoordFromCenter(), TileProp.EXPLOSION)))
						item.destroy();
			else if (!item.getCurrentFrameSetName().equals("StandFrameSet") && !item.getCurrentFrameSet().isRunning())
				removeItem(item);
			else
				item.run();
		}
	}

	public void destroy() {
		removeItem(this);
		if (getItemType() == ItemType.CURSE_SKULL)
			Item.addItem(getTileCoordFromCenter(), ItemType.CURSE_SKULL, true);
		else
			Effect.runEffect(getPosition(), "FireSkullExplosion");
	}

	@Override
	public void run(GraphicsContext gc, boolean isPaused) {
		super.run(gc, isPaused);
		TileCoord coord = getTileCoordFromCenter().getNewInstance();
		if (!isBlockedMovement() && tileWasChanged()) {
			TileCoord prevCoord = getPreviewTileCoord().getNewInstance();
			MapSet.checkTileTrigger(this, coord, TileProp.TRIGGER_BY_ITEM);
			MapSet.checkTileTrigger(this, prevCoord, TileProp.TRIGGER_BY_ITEM, true);
			removeThisFromTile(prevCoord);
			if (!items.containsKey(coord))
				putOnMap(coord, this);
		}
		if (getCurrentFrameSetName().equals("StandFrameSet") && Entity.haveAnyEntityAtCoord(coord))
			for (Entity entity : Entity.getEntityListFromCoord(coord))
				if (entity instanceof BomberMan)
						((BomberMan)entity).pickItem(this);
	}

	public void pick() {
		if (itemType == ItemType.RANDOM) {
			itemType = ItemType.getRandom();
			setUpItemPickUpFrameSet();
		}
		setFrameSet("PickedUpFrameSet");
		if (itemType.getSound() == null || itemType.getSoundDelay() > 0)
			Sound.playWav("ItemPickUp");
		if (itemType.getSound() != null)
			TimerFX.createTimer("ItemPickUp@" + hashCode(), itemType.getSoundDelay(), () -> Sound.playWav(itemType.getSound()));
	}

	public static boolean haveItemAt(TileCoord coord) {
		return items.containsKey(coord);
	}

	public static Item getItemAt(TileCoord tileCoord) {
		return haveItemAt(tileCoord) ? items.get(tileCoord) : null;
	}

	private void removeThisFromTile(TileCoord coord) {
		if (items.containsKey(coord) && items.get(coord) == this)
			items.remove(coord);
	}
	
	@Override
	public void onBeingHoldEvent(Entity holder) {
		removeThisFromTile(getTileCoordFromCenter());
	}

	@Override
	public void onSetPushEntityTrigger() {
		removeThisFromTile(getTileCoordFromCenter());
	}

	@Override
	public void onSetGotoMoveTrigger() {
		removeThisFromTile(getTileCoordFromCenter());
	}
	
	@Override
	public void onSetJumpMoveTrigger() {
		if (!currentFrameSetNameIsEqual("StandFrameSet"))
			removeThisFromTile(getTileCoordFromCenter());
	}

	@Override
	public void onPushEntityStop() {
		putOnMap(getTileCoordFromCenter(), this);
	}

	@Override
	public void onJumpFallAtFreeTileEvent(JumpMove jumpMove) {
		centerToTile();
		if (haveItemAt(getTileCoordFromCenter()) && getItemAt(getTileCoordFromCenter()) != this) {
			setElevation(Elevation.FLYING);
			onJumpFallAtOccupedTileEvent(jumpMove);
			return;
		}
		Sound.playWav("ItemBounce");
		TileCoord coord = getTileCoordFromCenter().getNewInstance();
		putOnMap(coord, this);
		setFrameSet("StandFrameSet");
		MapSet.checkTileTrigger(this, coord, TileProp.TRIGGER_BY_ITEM);
	}

	@Override
	public void onJumpFallAtOccupedTileEvent(JumpMove jumpMove) {
		centerToTile();
		forceDirection(getDirection().getNext8WayClockwiseDirection((int)(1 - MyMath.getRandom(0, 2))));
		Sound.playWav("ItemBounce");
		jumpMove.resetJump(4, 1.2, 14);
		TileCoord coord = getTileCoordFromCenter().getNewInstance();
		setGotoMove(coord.incCoordsByDirection(getDirection()).getPosition(), jumpMove.getDurationFrames());
	}

	private static void putOnMap(TileCoord coord, Item item) {
		if (item.getElevation() == Elevation.ON_GROUND)
			items.put(coord, item);
	}

	public static Item dropItemFromSky(TileCoord coord) {
		return dropItemFromSky(coord, ItemType.getRandom());
	}

	public static Item dropItemFromSky(TileCoord coord, ItemType itemType) {
		Item item = new Item(coord, itemType);
		Item.addItem(item);
		item.setFrameSet("JumpingFrameSet");
		item.setJumpMove(8, 0, 80);
		item.getJumpMove().skipToFall();
		item.setShadow(0, 0, -12 ,-6 ,0.35f);
		item.setGhosting(2, 0.2);
		item.getJumpMove().setOnCycleCompleteEvent(e -> {
			item.setElevation(Elevation.ON_GROUND);
			if (item.tileIsFree(item.getTileCoordFromCenter())) {
				item.setShake(2d, -0.05, 0d);
				item.removeShadow();
				item.unsetGhosting();
				Sound.playWav(item, "ItemBounce");
				items.put(coord, item);
				item.setFrameSet("StandFrameSet");
			}
			else
				item.onJumpFallAtOccupedTileEvent(item.getJumpMove());
		});
		return item;
	}

}
