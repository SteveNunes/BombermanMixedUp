package maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import entities.Effect;
import entities.Entity;
import entities.TileCoord;
import enums.ItemType;
import enums.TileProp;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import objmoveutils.Position;
import tools.Materials;
import tools.RGBColor;

public class Item extends Entity{

	private static Map<TileCoord, Item> items = new HashMap<>();
	private static RGBColor itemEdigeColor;
	
	private ItemType itemType;
	private TileCoord coord;
	private int startInvFrames;
	
	static {
		Materials.loadedSprites.put("ItemEdge", new WritableImage(18, 18));
		itemEdigeColor = new RGBColor(20);
	}
	
	public Item()
		{ this(new Position(), null); }

	public Item(TileCoord coord)
		{ this(coord.getPosition(Main.TILE_SIZE), null); }
	
	public Item(TileCoord coord, ItemType itemType)
		{ this(coord.getPosition(Main.TILE_SIZE), itemType); }
	
	public Item(Item item)
		{ this(item.getPosition(), item.itemType); }

	public Item(Position position)
		{ this(position, null); }
	
	public Item(Position position, ItemType itemType) {
		setPosition(new Position(position));
		this.itemType = itemType; 
		coord = new TileCoord(getTileX(), getTileY());
		startInvFrames = 10;
		int itemIndex = itemType.getValue() - 1;
		String itemStandFrameSet =
							"{SetSprSource;ItemEdge;0;0;18;18;0;0;0;0;18;18},{SetEntityShadow;0;0;18;4;0.5},{SetTicksPerFrame;1},{SetSprIndex;0},{SetOutputSprPos;-2;-13},{DoJump;1;1.1;50}"
						+	",,{SetSprSource;MainSprites;0;16;16;16;0;0;0;0;16;16},{SetSprIndex;" + itemIndex + "},{SetOutputSprPos;-1;-12}"
						+ "|{SetSprIndex;1},{SetEntityShadow;0;0;16;3;0.35},{IncOutputSprWidth;-2},{IncOutputSprX;1},,{IncOutputSprWidth;-2},{IncOutputSprX;1}"
						+ "|{SetSprIndex;2},{IncOutputSprWidth;-2},{IncOutputSprX;1},,{IncOutputSprWidth;-2},{IncOutputSprX;1}"
						+ "|{SetSprIndex;0},{IncOutputSprWidth;-2},{IncOutputSprX;1},,{IncOutputSprWidth;-2},{IncOutputSprX;1}|{Goto;-3;2}"
						+ "|{},,{SetSprIndex;85}"
						+ "|{SetSprIndex;1},{SetEntityShadow;0;0;14;2;0.2},{IncOutputSprWidth;2},{IncOutputSprX;-1},,{IncOutputSprWidth;2},{IncOutputSprX;-1}"
						+ "|{SetSprIndex;2},{IncOutputSprWidth;2},{IncOutputSprX;-1},,{IncOutputSprWidth;2},{IncOutputSprX;-1}"
						+ "|{SetSprIndex;0},{IncOutputSprWidth;2},{IncOutputSprX;-1},,{IncOutputSprWidth;2},{IncOutputSprX;-1}|{Goto;-3;2}"
						+ "|{SetSprIndex;1},{SetEntityShadow;0;0;16;3;0.35},{IncOutputSprWidth;-2},{IncOutputSprX;1},,{IncOutputSprWidth;-2},{IncOutputSprX;1}"
						+ "|{SetSprIndex;2},{IncOutputSprWidth;-2},{IncOutputSprX;1},,{IncOutputSprWidth;-2},{IncOutputSprX;1}"
						+ "|{SetSprIndex;0},{IncOutputSprWidth;-2},{IncOutputSprX;1},,{IncOutputSprWidth;-2},{IncOutputSprX;1}|{Goto;-3;2}"
						+ "|{},,{SetSprIndex;" + itemIndex + "}"
						+ "|{SetSprIndex;1},{IncOutputSprWidth;2},{IncOutputSprX;-1},,{IncOutputSprWidth;2},{IncOutputSprX;-1}"
						+ "|{SetSprIndex;2},{IncOutputSprWidth;2},{IncOutputSprX;-1},,{IncOutputSprWidth;2},{IncOutputSprX;-1}"
						+ "|{SetSprIndex;0},{IncOutputSprWidth;2},{IncOutputSprX;-1},,{IncOutputSprWidth;2},{IncOutputSprX;-1}|{Goto;-3;2}"
						+ "|{SetTicksPerFrame;5},{SetEntityShadow;0;0;16;3;0.5}"
						+ "|{SetSprIndex;1},{IncOutputSprY;1},,{IncOutputSprY;1}|{SetSprIndex;2}|{SetEntityShadow;0;0;18;4;0.65}|{SetSprIndex;0}|{Goto;-4;1}"
						+ "|{SetSprIndex;1},{IncOutputSprY;1},,{IncOutputSprY;1}|{SetSprIndex;2}|{SetEntityShadow;0;0;18;4;0.8}|{SetSprIndex;0}|{Goto;-4;1}"
						+ "|{SetSprIndex;1},{IncOutputSprY;-1},,{IncOutputSprY;-1}|{SetSprIndex;2}|{SetEntityShadow;0;0;18;4;0.65}|{SetSprIndex;0}|{Goto;-4;1}"
						+ "|{SetSprIndex;1},{IncOutputSprY;-1},,{IncOutputSprY;-1}|{SetSprIndex;2}|{SetEntityShadow;0;0;16;3;0.5}|{SetSprIndex;0}|{Goto;-4;1}|{Goto;0}";
		String itemPickUpFrameSet =
							"{SetSprSource;MainSprites;0;16;16;16;0;0;0;0;16;16},{SetSprIndex;" + itemIndex + "},{SetTicksPerFrame;3},{IncOutputSprY;-8}"
						+ "|{SetSprIndex;-},{IncOutputSprY;-1}|{SetSprIndex;" + itemIndex + "},{IncOutputSprY;-1}|{Goto;-2;8}";
		addNewFrameSetFromString("ItemStandFrameSet", itemStandFrameSet);
		addNewFrameSetFromString("ItemPickedUpFrameSet", itemPickUpFrameSet);
		setFrameSet("ItemStandFrameSet");
	}
	
	public ItemType getItemType()
		{ return itemType; }

	public void setItemType(ItemType itemType)
		{ this.itemType = itemType; }
	
	public void setItemType(int itemId)
		{ itemType = ItemType.getItemById(itemId); }

	public static void addItem(TileCoord coord)
		{ addItem(new Item(coord, null)); }
	
	public static void addItem(TileCoord coord, ItemType itemType)
		{ addItem(new Item(coord, itemType)); }

	public static void addItem(Item item) {
		if (!haveItemAt(item.coord))
			items.put(item.coord.getNewInstance(), item);
	}

	public static void removeItem(Item item)
		{ removeItem(item.coord); }
	
	public static void removeItem(TileCoord coord) {
		if (haveItemAt(coord))
			items.remove(coord);
	}
	
	public static void clearItems()
		{ items.clear(); }
	
	public static int totalItems()
		{ return items.size(); }

	public static List<Item> getItems()
		{ return new ArrayList<>(items.values()); }
	
	public static void drawItems() {
		List<Item> removeItems = new ArrayList<>();
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
			
		for (Item item : items.values()) {
			if (--item.startInvFrames <= 0 && item.getCurrentFrameSetName().equals("ItemStandFrameSet") &&
					MapSet.tileContainsProp(item.getTileCoord(), TileProp.DAMAGE_ITEM)) {
				removeItems.add(item);
				Effect.runEffect(item.getPosition(), "FIRE_SKULL_EXPLOSION");
			}
			else if (!item.getCurrentFrameSetName().equals("ItemStandFrameSet") && !item.getCurrentFrameSet().isRunning())
				removeItems.add(item);
			else
				item.run();
		}
		removeItems.forEach(item -> removeItem(item));
	}
	
	public void pick() { // NOTA: Adicionar som ao pegar item
		if (getCurrentFrameSetName().equals("ItemStandFrameSet"))
			setFrameSet("ItemPickedUpFrameSet");
	}

	public static boolean haveItemAt(TileCoord coord)
		{ return items.containsKey(coord); }

	public static Item getItemAt(TileCoord tileCoord)
		{ return haveItemAt(tileCoord) ? items.get(tileCoord) : null; }

}
