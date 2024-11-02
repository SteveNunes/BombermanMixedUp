package entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import enums.TileProp;
import maps.Layer;
import maps.MapSet;
import objmoveutils.TileCoord;

public class TileDamage {

	private static Map<TileCoord, List<TileDamage>> tileDamageList = new HashMap<>();

	private Layer layer;
	private TileCoord coord;
	private List<TileProp> tileProps;
	private int duration;

	private TileDamage(TileCoord coord, int framesDuration) {
		this(coord, new ArrayList<>(), framesDuration);
	}

	private TileDamage(TileCoord coord, TileProp damageTileProp, int framesDuration) {
		this(coord, Arrays.asList(damageTileProp), framesDuration);
	}

	private TileDamage(TileCoord coord, List<TileProp> damageTileProps, int framesDuration) {
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

	public TileDamage addDamageTileProps(TileProp... props) {
		for (TileProp prop : props) {
			tileProps.add(prop);
			layer.getTileProps(coord).add(prop);
		}
		return this;
	}

	public void setDamageToAll() {
		addDamageTileProps(TileProp.DAMAGE_BOMB, TileProp.DAMAGE_BRICK, TileProp.DAMAGE_ENEMY, TileProp.DAMAGE_ITEM, TileProp.DAMAGE_PLAYER);
	}

	public void setDamageToAllExcept(TileProp... execeptionProps) {
		List<TileProp> exceptions = Arrays.asList(execeptionProps);
		for (TileProp prop : Arrays.asList(TileProp.EXPLOSION, TileProp.DAMAGE_BOMB, TileProp.DAMAGE_BRICK, TileProp.DAMAGE_ENEMY, TileProp.DAMAGE_ITEM, TileProp.DAMAGE_PLAYER))
			if (!exceptions.contains(prop))
				layer.addTileProp(coord, prop);
	}

	public static void iterateAllTileDamages(Consumer<TileDamage> consumer) {
		for (TileCoord coord : tileDamageList.keySet())
			for (TileDamage tileDamage : tileDamageList.get(coord))
				consumer.accept(tileDamage);
	}

	public static void runTileDamages() {
		List<TileDamage> remove = new ArrayList<>();
		iterateAllTileDamages(tileDamage -> {
			if (tileDamage.duration > 0 && --tileDamage.duration == 0) {
				tileDamage.removeDamageTileProps();
				remove.add(tileDamage);
			}
		});
		for (TileDamage tileDamage : remove) {
			tileDamageList.get(tileDamage.coord).remove(tileDamage);
			if (tileDamageList.get(tileDamage.coord).isEmpty())
				tileDamageList.remove(tileDamage.coord);
		}
	}

	public static void clearTileDamages() {
		iterateAllTileDamages(tileDamage -> tileDamage.removeDamageTileProps());
		tileDamageList.clear();
	}

	public static void removeTileDamage(TileCoord coord) {
		if (!tileDamageList.containsKey(coord))
			throw new RuntimeException("There's no TileDamage setted at coord " + coord);
		for (TileDamage tileDamage : tileDamageList.get(coord))
			tileDamage.removeDamageTileProps();
		tileDamageList.remove(coord);
	}

	public static TileDamage addTileDamage(TileCoord coord, int framesDuration) {
		return addTileDamage(coord, new ArrayList<>(), framesDuration);
	}

	public static TileDamage addTileDamage(TileCoord coord, TileProp damageTileProp, int framesDuration) {
		return addTileDamage(coord, Arrays.asList(damageTileProp), framesDuration);
	}

	public static TileDamage addTileDamage(TileCoord coord, List<TileProp> damageTileProps, int framesDuration) {
		if (!tileDamageList.containsKey(coord))
			tileDamageList.put(coord, new ArrayList<>());
		TileDamage tileDamage = new TileDamage(coord, damageTileProps, framesDuration);
		tileDamageList.get(coord.getNewInstance()).add(tileDamage);
		return tileDamage;
	}

}
