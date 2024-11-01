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
import enums.ItemType;
import enums.TileProp;
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

	static {
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
		startInvFrames = 10;
		int itemIndex = itemType.getValue() - 1;
		String itemStandFrameSet = "{SetSprSource;ItemEdge;0;0;18;18;0;0;0;0;18;18},{SetEntityShadow;0;0;18;4;0.5},{SetTicksPerFrame;1},{SetSprIndex;0},{SetOutputSprPos;-2;-13},{SetJumpMove;1;1.1;50},{SetSprFrontValue;1},,{SetSprSource;MainSprites;0;16;16;16;0;0;0;0;16;16},{SetSprIndex;" + itemIndex + "},{SetOutputSprPos;-1;-12},{SetSprFrontValue;1}|{SetSprIndex;1},{SetEntityShadow;0;0;16;3;0.35},{IncOutputSprWidth;-2},{IncOutputSprX;1},,{IncOutputSprWidth;-2},{IncOutputSprX;1}|{SetSprIndex;2},{IncOutputSprWidth;-2},{IncOutputSprX;1},,{IncOutputSprWidth;-2},{IncOutputSprX;1}|{SetSprIndex;0},{IncOutputSprWidth;-2},{IncOutputSprX;1},,{IncOutputSprWidth;-2},{IncOutputSprX;1}|{Goto;-3;2}|{},,{SetSprIndex;85}|{SetSprIndex;1},{SetEntityShadow;0;0;14;2;0.2},{IncOutputSprWidth;2},{IncOutputSprX;-1},,{IncOutputSprWidth;2},{IncOutputSprX;-1}|{SetSprIndex;2},{IncOutputSprWidth;2},{IncOutputSprX;-1},,{IncOutputSprWidth;2},{IncOutputSprX;-1}"
		    + "|{SetSprIndex;0},{IncOutputSprWidth;2},{IncOutputSprX;-1},,{IncOutputSprWidth;2},{IncOutputSprX;-1}|{Goto;-3;2}|{SetSprIndex;1},{SetEntityShadow;0;0;16;3;0.35},{IncOutputSprWidth;-2},{IncOutputSprX;1},,{IncOutputSprWidth;-2},{IncOutputSprX;1}|{SetSprIndex;2},{IncOutputSprWidth;-2},{IncOutputSprX;1},,{IncOutputSprWidth;-2},{IncOutputSprX;1}|{SetSprIndex;0},{IncOutputSprWidth;-2},{IncOutputSprX;1},,{IncOutputSprWidth;-2},{IncOutputSprX;1}|{Goto;-3;2}|{},,{SetSprIndex;" + itemIndex + "}|{SetSprIndex;1},{IncOutputSprWidth;2},{IncOutputSprX;-1},,{IncOutputSprWidth;2},{IncOutputSprX;-1}|{SetSprIndex;2},{IncOutputSprWidth;2},{IncOutputSprX;-1},,{IncOutputSprWidth;2},{IncOutputSprX;-1}|{SetSprIndex;0},{IncOutputSprWidth;2},{IncOutputSprX;-1},,{IncOutputSprWidth;2},{IncOutputSprX;-1}|{Goto;-3;2}|{SetTicksPerFrame;5},{SetEntityShadow;0;0;16;3;0.5}"
		    + "|{SetSprIndex;1},{IncOutputSprY;1},,{IncOutputSprY;1}|{SetSprIndex;2}|{SetEntityShadow;0;0;18;4;0.65}|{SetSprIndex;0}|{Goto;-4;1}|{SetSprIndex;1},{IncOutputSprY;1},,{IncOutputSprY;1}|{SetSprIndex;2}|{SetEntityShadow;0;0;18;4;0.8}|{SetSprIndex;0}|{Goto;-4;1}|{SetSprIndex;1},{IncOutputSprY;-1},,{IncOutputSprY;-1}|{SetSprIndex;2}|{SetEntityShadow;0;0;18;4;0.65}|{SetSprIndex;0}|{Goto;-4;1}|{SetSprIndex;1},{IncOutputSprY;-1},,{IncOutputSprY;-1}|{SetSprIndex;2}|{SetEntityShadow;0;0;16;3;0.5}|{SetSprIndex;0}|{Goto;-4;1}|{Goto;0}";
		addNewFrameSetFromString("ItemStandFrameSet", itemStandFrameSet);
		String itemJumpingFrameSet = "{SetSprSource;MainSprites;1280;16;16;16;0;0;0;0;16;16},{SetEntityShadow;0;0;18;4;0.5},{SetTicksPerFrame;2},{SetSprIndex;0},{IncSprFrontValue;2}"
		    + "|{IncSprIndex;1}|{Goto;-1;3}|{DecSprIndex;1}|{Goto;-1;3}|{Goto;-4}";
		addNewFrameSetFromString("ItemJumpingFrameSet", itemJumpingFrameSet);
		setUpItemPickUpFrameSet();
		setFrameSet("ItemStandFrameSet");
		if (itemType == ItemType.CURSE_SKULL)
			curse = Curse.getRandom();
		else
			curse = null;
	}
	
	public void jumpToRandomTileAround(int radius) {
		jumpTo(getTileCoordFromCenter().getNewInstance()
				.incCoordsByDirection(Direction.get8DirectionFromValue((int) MyMath.getRandom(0, 7)), radius));
	}

	public void jumpTo(TileCoord coord) {
		setFrameSet("ItemJumpingFrameSet");
		jumpTo(this, coord, 6, 1.2, 20);
	}

	private void setUpItemPickUpFrameSet() {
		int itemIndex = itemType.getValue() - 1;
		String itemPickUpFrameSet = "{SetSprSource;MainSprites;0;16;16;16;0;0;0;0;16;16},{SetSprIndex;" + itemIndex + "},{SetTicksPerFrame;3},{IncOutputSprY;-12}" + "|{SetSprIndex;-},{IncOutputSprY;-1}|{SetSprIndex;" + itemIndex + "},{IncOutputSprY;-2}|{Goto;-2;7}";
		if (haveFrameSet("ItemPickedUpFrameSet"))
			replaceFrameSetFromString("ItemPickedUpFrameSet", itemPickUpFrameSet);
		else
			addNewFrameSetFromString("ItemPickedUpFrameSet", itemPickUpFrameSet);
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
		addItem(item, false);
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
			if (startJumpingAround || !MapSet.tileIsFree(item.getTileCoordFromCenter()))
				item.jumpToRandomTileAround(2);
			else {
				items.put(item.getTileCoordFromCenter().getNewInstance(), item);
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
			if (--item.startInvFrames <= 0 && item.getCurrentFrameSetName().equals("ItemStandFrameSet") && MapSet.tileContainsProp(item.getTileCoordFromCenter(), TileProp.DAMAGE_ITEM))
				item.destroy();
			else if (!item.getCurrentFrameSetName().equals("ItemStandFrameSet") && !item.getCurrentFrameSet().isRunning())
				removeItem(item);
			else
				item.run();
		}
	}

	public void destroy() {
		removeItem(this);
		Effect.runEffect(getPosition(), "FIRE_SKULL_EXPLOSION");
	}

	@Override
	public void run(GraphicsContext gc, boolean isPaused) {
		super.run(gc, isPaused);
		if (getPushEntity() != null && items.containsKey(getTileCoordFromCenter()))
			items.remove(getTileCoordFromCenter());
		TileCoord coord = getTileCoordFromCenter().getNewInstance();
		if (!isBlockedMovement() && tileWasChanged()) {
			TileCoord prevCoord = getPreviewTileCoord().getNewInstance();
			MapSet.checkTileTrigger(this, coord, TileProp.TRIGGER_BY_ITEM);
			MapSet.checkTileTrigger(this, prevCoord, TileProp.TRIGGER_BY_ITEM, true);
			items.remove(prevCoord);
			if (!items.containsKey(coord))
				items.put(coord, this);
		}
		if (Entity.haveAnyEntityAtCoord(coord))
			for (Entity entity : Entity.getEntityListFromCoord(coord))
				if (entity instanceof BomberMan)
						((BomberMan)entity).pickItem(this);
	}

	public void pick() {
		if (itemType == ItemType.RANDOM) {
			itemType = ItemType.getRandom();
			setUpItemPickUpFrameSet();
		}
		if (getCurrentFrameSetName().equals("ItemStandFrameSet")) {
			setFrameSet("ItemPickedUpFrameSet");
			if (itemType.getSound() == null || itemType.getSoundDelay() > 0)
				Sound.playWav("ItemPickUp");
			if (itemType.getSound() != null)
				TimerFX.createTimer("ItemPickUp@" + hashCode(), itemType.getSoundDelay(), () -> Sound.playWav(itemType.getSound()));
		}
	}

	public static boolean haveItemAt(TileCoord coord) {
		return items.containsKey(coord);
	}

	public static Item getItemAt(TileCoord tileCoord) {
		return haveItemAt(tileCoord) ? items.get(tileCoord) : null;
	}

	@Override
	public void onBeingHoldEvent(Entity holder) {
		items.remove(holder.getTileCoordFromCenter());
	}

	@Override
	public void onJumpStartEvent(TileCoord coord, JumpMove jumpMove) {
		items.remove(coord);
	}

	@Override
	public void onJumpFallAtFreeTileEvent(JumpMove jumpMove) {
		checkOutScreenCoords();
		centerToTile();
		if (haveItemAt(getTileCoordFromCenter())) {
			onJumpFallAtOccupedTileEvent(jumpMove);
			return;
		}
			
		Sound.playWav("ItemBounce");
		TileCoord coord = getTileCoordFromCenter().getNewInstance();
		items.put(coord, this);
		setFrameSet("ItemStandFrameSet");
		MapSet.checkTileTrigger(this, coord, TileProp.TRIGGER_BY_ITEM);
	}

	@Override
	public void onJumpFallAtOccupedTileEvent(JumpMove jumpMove) {
		checkOutScreenCoords();
		centerToTile();
		forceDirection(getDirection().getNext8WayClockwiseDirection((int)(1 - MyMath.getRandom(0, 2))));
		Sound.playWav("ItemBounce");
		jumpMove.resetJump(4, 1.2, 14);
		TileCoord coord = getTileCoordFromCenter().getNewInstance();
		setGotoMove(coord.incCoordsByDirection(getDirection()).getPosition(), jumpMove.getDurationFrames());
	}

}
