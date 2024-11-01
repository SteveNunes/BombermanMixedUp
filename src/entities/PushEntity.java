package entities;

import java.util.function.Consumer;

import enums.Direction;
import maps.MapSet;
import objmoveutils.TileCoord;

public class PushEntity {

	private Entity entity;
	private Double startStrenght;
	private Double strenght;
	private Double decStrenght;
	private Direction direction;
	private Consumer<Entity> consumerWhenHits;
	private TileCoord targetTile;

	public PushEntity(Entity entity, Double strenght) {
		this(entity, strenght, null, null);
	}

	public PushEntity(Entity entity, Double strenght, Direction direction) {
		this(entity, strenght, null, direction);
	}

	public PushEntity(Entity entity, Double startStrenght, Double decStrenght) {
		this(entity, startStrenght, decStrenght, null);
	}

	public PushEntity(Entity entity, Double startStrenght, Double decStrenght, Direction direction) {
		this.entity = entity;
		this.startStrenght = startStrenght;
		this.decStrenght = decStrenght;
		this.direction = direction == null ? entity.getDirection() : direction;
		consumerWhenHits = null;
		targetTile = null;
		strenght = startStrenght;
	}

	public PushEntity(PushEntity pushEntity) {
		entity = pushEntity.entity;
		startStrenght = pushEntity.startStrenght;
		decStrenght = pushEntity.decStrenght;
		direction = pushEntity.direction;
		strenght = startStrenght;
		consumerWhenHits = pushEntity.consumerWhenHits;
		targetTile = pushEntity.targetTile == null ? null : new TileCoord(pushEntity.targetTile);
	}

	public PushEntity setDirection(Direction dir) {
		direction = dir;
		return this;
	}

	public PushEntity setOnColideEvent(Consumer<Entity> consumer) {
		consumerWhenHits = consumer;
		return this;
	}

	public TileCoord getTargetTile() {
		return targetTile;
	}

	public PushEntity setTargetTile(TileCoord coord) {
		targetTile = coord.getNewInstance();
		return this;
	}

	public PushEntity testIfPathIsFree() {
		if (targetTile == null && MapSet.haveTilesOnCoord(targetTile))
			return this;
		TileCoord coord = entity.getTileCoord().getNewInstance();
		do {
			coord.incCoordsByDirection(direction);
			if (!MapSet.tileIsFree(coord) || !MapSet.haveTilesOnCoord(coord) || Entity.haveAnyEntityAtCoord(coord))
				direction = null;
		}
		while (direction != null && !coord.equals(targetTile));
		return this;
	}

	public void process() {
		if (direction != null) {
			if (entity == null) {
				direction = null;
				return;
			}
			entity.moveEntity(direction, strenght);
			if ((targetTile != null && targetTile.equals(entity.getTileCoord()) || (decStrenght == null && entity.isPerfectlyBlockedDir(direction)) || (decStrenght != null && (strenght -= decStrenght) <= 0))) {
				if (entity.isPerfectlyBlockedDir(direction) || (targetTile != null && targetTile.equals(entity.getTileCoord()))) {
					entity.centerToTile();
					if (entity.isPerfectlyBlockedDir(direction) && consumerWhenHits != null)
						consumerWhenHits.accept(entity);
				}
				direction = null;
			}
		}
	}

	public boolean isActive() {
		return direction != null;
	}

	public void stop() {
		direction = null;
	}

	@Override
	public String toString() {
		return "PushEntity [entity=" + entity + ", startStrenght=" + startStrenght + ", strenght=" + strenght + ", decStrenght=" + decStrenght + ", direction=" + direction + ", consumerWhenHits=" + consumerWhenHits + ", targetTile=" + targetTile + "]";
	}

}
