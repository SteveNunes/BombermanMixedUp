package entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import enums.TileProp;
import maps.Layer;
import maps.MapSet;
import objmoveutils.TileCoord;

public class TileDamage {

	private static Map<TileCoord, TileDamage> tileDamageList = new HashMap<>();
	
	private Entity entity;
	private Layer layer;
	private TileCoord coord;
	private List<TileProp> tileProps;
	private int duration;

	private TileDamage(TileCoord coord, int framesDuration)
		{ this(null, coord, new ArrayList<>(), framesDuration); }
	
	private TileDamage(TileCoord coord, TileProp damageTileProp, int framesDuration)
		{ this(null, coord, Arrays.asList(damageTileProp), framesDuration); }
	
	private TileDamage(TileCoord coord, List<TileProp> damageTileProps, int framesDuration)
		{ this(null, coord, damageTileProps, framesDuration); }

	private TileDamage(Entity entity, TileCoord coord, int framesDuration)
		{ this(entity, coord, new ArrayList<>(), framesDuration); }

	private TileDamage(Entity entity, TileCoord coord, TileProp damageTileProp, int framesDuration)
		{ this(entity, coord, Arrays.asList(damageTileProp), framesDuration); }
	
	private TileDamage(Entity entity, TileCoord coord, List<TileProp> damageTileProps, int framesDuration) {
		this.entity = entity;
		this.coord = coord.getNewInstance();
		this.tileProps = damageTileProps == null ? new ArrayList<>() : new ArrayList<>(damageTileProps);
		duration = framesDuration;
		layer = MapSet.getCurrentLayer();
		addDamageTileProps(damageTileProps.toArray(new TileProp[damageTileProps.size()]));
	}
	
	private void removeDamageTileProps() {
		for (TileProp prop : tileProps)
			layer.removeTileProp(coord, prop);
	}
	
	private boolean isActive()
		{ return duration > 0; }

	public TileDamage addDamageTileProps(TileProp ... props) {
		for (TileProp prop : props)
			if (!tileProps.contains(prop)) {
				tileProps.add(prop);
				layer.getTileProps(coord).add(prop);
			}
		return this;
	}
	
	public void setDamageToAll()
		{ addDamageTileProps(TileProp.DAMAGE_BOMB, TileProp.DAMAGE_BRICK, TileProp.DAMAGE_ENEMY, TileProp.DAMAGE_ITEM, TileProp.DAMAGE_PLAYER); }

	public void setDamageToAllExcept(TileProp ... execeptionProps) {
		List<TileProp> exceptions = Arrays.asList(execeptionProps);
		for (TileProp prop : Arrays.asList(TileProp.DAMAGE_BOMB, TileProp.DAMAGE_BRICK, TileProp.DAMAGE_ENEMY, TileProp.DAMAGE_ITEM, TileProp.DAMAGE_PLAYER))
			if (!exceptions.contains(prop))
				layer.addTileProp(coord, prop);
	}

	public static void runTileDamages() {
		for (TileDamage tileDamage : tileDamageList.values())
			if (tileDamage.duration > 0 && --tileDamage.duration == 0)
				tileDamage.removeDamageTileProps();
	}
	
	public static void clearTileDamages() {
		for (TileDamage tileDamage : tileDamageList.values())
			tileDamage.removeDamageTileProps();
		tileDamageList.clear();
	}
	
	public static void removeTileDamage(TileCoord coord) {
		if (!tileDamageList.containsKey(coord))
			throw new RuntimeException("There's no TileDamage setted at coord " + coord);
		tileDamageList.get(coord).removeDamageTileProps();
		tileDamageList.remove(coord);
	}
	
	public static TileDamage addTileDamage(TileCoord coord, int framesDuration)
		{ return addTileDamage(null, coord, new ArrayList<>(), framesDuration); }
	
	public static TileDamage addTileDamage(TileCoord coord, TileProp damageTileProp, int framesDuration)
		{ return addTileDamage(null, coord, Arrays.asList(damageTileProp), framesDuration); }
	
	public static TileDamage addTileDamage(TileCoord coord, List<TileProp> damageTileProps, int framesDuration)
		{ return addTileDamage(null, coord, damageTileProps, framesDuration); }
	
	public static TileDamage addTileDamage(Entity entity, TileCoord coord, int framesDuration)
		{ return addTileDamage(entity, coord, new ArrayList<>(), framesDuration); }
	
	public static TileDamage addTileDamage(Entity entity, TileCoord coord, TileProp damageTileProp, int framesDuration)
		{ return addTileDamage(entity, coord, Arrays.asList(damageTileProp), framesDuration); }
	
	public static TileDamage addTileDamage(Entity entity, TileCoord coord, List<TileProp> damageTileProps, int framesDuration) {
		if (tileDamageList.containsKey(coord) && tileDamageList.get(coord).isActive()) {
			for (TileProp prop : damageTileProps)
				tileDamageList.get(coord).addDamageTileProps(prop);
			return tileDamageList.get(coord);
		}
		if (tileDamageList.containsKey(coord))
			tileDamageList.get(coord).removeDamageTileProps();
		TileDamage tileDamage = new TileDamage(entity, coord, damageTileProps, framesDuration);
		tileDamageList.put(coord.getNewInstance(), tileDamage);
		return tileDamage;
	}

}
