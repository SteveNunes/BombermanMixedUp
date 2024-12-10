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
import enums.RideType;
import enums.TileProp;
import frameset.FrameSet;
import frameset_tags.SetSprIndex;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import objmoveutils.JumpMove;
import objmoveutils.TileCoord;
import tools.GameConfigs;
import tools.Materials;
import tools.RGBColor;
import tools.Sound;
import util.DurationTimerFX;
import util.MyMath;

public class Item extends Entity {

	private final static int INV_FRAMES = 20;

	private static Map<TileCoord, List<Item>> items = new HashMap<>();
	private static List<Item> itemList = new ArrayList<>();
	private static RGBColor itemEdigeColor;

	private Curse curse;
	private ItemType itemType;
	private RideType rideType;
	private Integer coins;

	public static void createItemEdgeImage() {
		Materials.loadedSprites.put("ItemEdge", new WritableImage(18, 18));
		itemEdigeColor = new RGBColor(20);
	}

	public Item(Item item) {
		this(item.getTileCoordFromCenter(), item.itemType, item.rideType, item.coins);
	}

	public Item(TileCoord coord, ItemType itemType) {
		this(coord, itemType, null, null);
	}

	public Item(TileCoord coord, RideType rideType) {
		this(coord, null, rideType, null);
	}

	public Item(TileCoord coord, Integer coins) {
		this(coord, null, null, coins);
	}

	private Item(TileCoord coord, ItemType itemType, RideType rideType, Integer coins) {
		setPosition(coord.getPosition());
		this.itemType = itemType;
		this.rideType = rideType;
		this.coins = coins;
		String s = itemType != null ? "ITEM" : rideType != null ? (rideType.isMech() ? "ITEM-MEGG" : "ITEM-EGG") : (coins < 0 ? "ITEM-GCOIN" : "ITEM-SCOIN");
		addNewFrameSetFromIniFile(this, "StandFrameSet", "FrameSets", s, "StandFrameSet");
		addNewFrameSetFromIniFile(this, "JumpingFrameSet", "FrameSets", s, "JumpingFrameSet");
		addNewFrameSetFromIniFile(this, "FallingFromSky", "FrameSets", s, "FallingFromSky");
		if (itemType != null) {
			setUpItemPickUpFrameSet();
			if (itemType == ItemType.CURSE_SKULL)
				curse = Curse.getRandom();
			else
				curse = null;
		}
		else if (rideType != null) {
			addNewFrameSetFromIniFile(this, "Following-LEFT", "FrameSets", s, "Following-LEFT");
			addNewFrameSetFromIniFile(this, "Following-UP", "FrameSets", s, "Following-UP");
			addNewFrameSetFromIniFile(this, "Following-RIGHT", "FrameSets", s, "Following-RIGHT");
			addNewFrameSetFromIniFile(this, "Following-DOWN", "FrameSets", s, "Following-DOWN");
		}
		else if (coins == null)
			throw new RuntimeException("You must specify at least one of these params: 'itemType', 'rideType', 'coins'");
		setFrameSet("StandFrameSet");
		setInvencibleFrames(INV_FRAMES);
		setPassThroughs(true, PassThrough.MONSTER, PassThrough.PLAYER, PassThrough.ITEM);
	}
	
	public int coinsValue() {
		return coins == null ? 0 : Math.abs(coins);
	}
	
	public boolean isItem() {
		return itemType != null;
	}
	
	public boolean isEgg() {
		return rideType != null;
	}
	
	public boolean isCoin() {
		return coins != null;
	}
	
	public boolean isSilverCoin() {
		return coins != null && coins > 0;
	}
	
	public boolean isGoldCoin() {
		return coins != null && coins < 0;
	}
	
	@Override
	public void setFrameSet(String frameSetName) {
		super.setFrameSet(frameSetName);
		if (isItem()) {
			int itemIndex = itemType.getValue() - 1;
			FrameSet frameSet = getFrameSet(frameSetName);
				frameSet.iterateFrameTags(tag -> {
					if (tag instanceof SetSprIndex && ((SetSprIndex)tag).value != null && ((SetSprIndex)tag).value == -1)
						((SetSprIndex)tag).value = itemIndex;
				});
		}
	}
	
	public void jumpToRandomTileAround(int radius) {
		jumpTo(getTileCoordFromCenter().getNewInstance()
				.incCoordsByDirection(Direction.get8DirectionFromValue((int) MyMath.getRandom(0, 7)), radius));
	}

	public void jumpTo(TileCoord coord) {
		setFrameSet("JumpingFrameSet");
		jumpTo(this, coord, 6, 1.2, 20);
		setInvencibleFrames(INV_FRAMES);
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

	public static void addItem(TileCoord coord, ItemType itemType) {
		addItem(new Item(coord, itemType), false);
	}

	public static void addSilverCoins(TileCoord coord, int totalSilverCoins) {
		addItem(new Item(coord, null, null, totalSilverCoins), false);
	}

	public static void addGoldCoins(TileCoord coord, int totalGoldCoins) {
		addItem(new Item(coord, null, null, totalGoldCoins), false);
	}

	public static void addEgg(TileCoord coord, ItemType itemType) {
		addItem(new Item(coord, itemType), false);
	}

	public static void addItem(TileCoord coord, ItemType itemType, boolean startJumpingAround) {
		addItem(new Item(coord, itemType), startJumpingAround);
	}

	public static void addSilverCoins(TileCoord coord, int totalSilverCoins, boolean startJumpingAround) {
		addItem(new Item(coord, null, null, totalSilverCoins), startJumpingAround);
	}

	public static void addGoldCoins(TileCoord coord, int totalGoldCoins, boolean startJumpingAround) {
		addItem(new Item(coord, null, null, totalGoldCoins), startJumpingAround);
	}

	public static void addEgg(TileCoord coord, ItemType itemType, boolean startJumpingAround) {
		addItem(new Item(coord, itemType), startJumpingAround);
	}

	public static void addItem(Item item) {
		itemList.add(item);
	}
	
	public static void addItem(Item item, boolean startJumpingAround) {
		item.setInvencibleFrames(INV_FRAMES);
		itemList.add(item);
		if (startJumpingAround || !MapSet.tileIsFree(item.getTileCoordFromCenter()))
			item.jumpToRandomTileAround(2);
		else {
			putOnMap(item.getTileCoordFromCenter().getNewInstance(), item);
			MapSet.checkTileTrigger(item, item.getTileCoordFromCenter(), TileProp.TRIGGER_BY_ITEM);
		}
	}

	public static void removeItem(Item item) {
		if (item != null) {
			itemList.remove(item);
			if (items.containsKey(item.getTileCoordFromCenter())) {
				items.get(item.getTileCoordFromCenter()).remove(item);
				if (items.get(item.getTileCoordFromCenter()).isEmpty())
					items.remove(item.getTileCoordFromCenter());
			}
		}
	}

	public static void removeItem(TileCoord coord) {
		if (haveItemAt(coord)) {
			itemList.removeAll(items.get(coord));
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

	public static Map<TileCoord, List<Item>> getItemMap() {
		return items;
	}
	
	private static void updateItemEdgeFrame() {
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
	}

	public static void drawItems() {
		List<Item> tempItems = new ArrayList<>(itemList);
		updateItemEdgeFrame();
		for (Item item : tempItems) {
			if (MapSet.tileContainsProp(item.getTileCoordFromCenter(), TileProp.INSTAKILL))
				item.forceDestroy();
			else if (!item.isInvencible() &&
					(MapSet.tileContainsProp(item.getTileCoordFromCenter(), TileProp.DAMAGE_ITEM) ||
					 MapSet.tileContainsProp(item.getTileCoordFromCenter(), TileProp.EXPLOSION))) {
						if (!item.getCurrentFrameSetName().equals("StandFrameSet"))
							item.forceDestroy();
						else
							item.destroy();
			}
			else if (!item.getCurrentFrameSetName().equals("StandFrameSet") && !item.getCurrentFrameSet().isRunning())
				removeItem(item);
			else
				item.run();
		}
	}
	
	public void forceDestroy() {
		destroy(true);
	}
	
	public void destroy() {
		destroy(false);
	}

	public void destroy(boolean forceDestroy) {
		removeItem(this);
		if (!forceDestroy && getItemType() == ItemType.CURSE_SKULL)
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
		if (isItem()) {
			if (itemType == ItemType.RANDOM) {
				itemType = ItemType.getRandom();
				setUpItemPickUpFrameSet();
			}
			setFrameSet("PickedUpFrameSet");
			if (itemType.getSound() == null || itemType.getSoundDelay() > 0)
				Sound.playWav("ItemPickUp");
			if (itemType.getSound() != null)
				DurationTimerFX.createTimer("ItemPickUp@" + hashCode(), Duration.millis(itemType.getSoundDelay()), () -> Sound.playWav(itemType.getSound()));
		}
		else if (isEgg()) {
			
		}
		else {
			// COIN
		}
	}

	public static boolean haveItemAt(TileCoord coord) {
		return items.containsKey(coord);
	}

	public static Item getItemAt(TileCoord tileCoord) {
		return haveItemAt(tileCoord) ? items.get(tileCoord).get(0) : null;
	}

	private void removeThisFromTile(TileCoord coord) {
		if (items.containsKey(coord) && items.get(coord) == this) {
			for (Item item : items.get(coord))
				itemList.remove(item);
			items.remove(coord);
		}
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
		if (!items.containsKey(coord))
			items.put(coord.getNewInstance(), new ArrayList<>());
		if (item.getElevation() == Elevation.ON_GROUND && !items.get(coord).contains(item))
			items.get(coord).add(item);
	}

	public static Item dropItemFromSky(TileCoord coord) {
		return dropItemFromSky(coord, ItemType.getRandom());
	}

	public static Item dropItemFromSky(TileCoord coord, ItemType itemType) {
		Item item = new Item(coord, itemType);
		Item.addItem(item);
		item.setFrameSet("FallingFromSky");
		item.setJumpMove(8, 0, GameConfigs.FALLING_FROM_SKY_STARTING_HEIGHT);
		item.getJumpMove().skipToFall();
		item.setGhosting(2, 0.2);
		item.getJumpMove().setOnCycleCompleteEvent(e -> {
			item.setElevation(Elevation.ON_GROUND);
			if (item.tileIsFree(item.getTileCoordFromCenter())) {
				item.setShake(2d, -0.05, 0d);
				item.removeShadow();
				item.unsetGhosting();
				Sound.playWav(item, "ItemBounce");
				putOnMap(item.getTileCoordFromCenter(), item);
				item.setFrameSet("StandFrameSet");
			}
			else
				item.onJumpFallAtOccupedTileEvent(item.getJumpMove());
		});
		return item;
	}

}
