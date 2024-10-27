package enums;

import util.CollectionUtils;

public enum BombType {
	
	NES(0),
	NORMAL(1),
	SPIKED(2),
	REMOTE(3),
	P(4),
	LAND_MINE(5),
	RUBBER(6),
	FOLLOW(7),
	MAGNET(8),
	MAGMA(9),
	HEART(10),
	SENSOR(11),
	SPIKED_REMOTE(12);
	
	private int value;
	private static BombType[] list = {NORMAL, SPIKED, REMOTE, P, LAND_MINE, RUBBER, FOLLOW, MAGNET, MAGMA, HEART, SENSOR, SPIKED_REMOTE};
	
	private BombType(int value)
		{ this.value = value; }
	
	public int getValue()
		{ return value; }
	
	public BombType getNext() {
		int i = value + 1;
		if (i == list.length)
			i = 0;
		return list[i];
	}
	
	public BombType getPreview() {
		int i = value - 1;
		if (i == 0)
			i = list.length - 1;
		return list[i];
	}
	
	public static BombType getRandom()
		{ return CollectionUtils.getRandomItemFromArray(list); }
	
	public static BombType getItemById(int bombId) {
		if (bombId < 0 || bombId >= list.length)
			throw new RuntimeException(bombId + " - Invalid bomb ID");
		return list[bombId];
	}
	
	public boolean isUnique() {
		return this == P || this == LAND_MINE || this == FOLLOW ||
				this == MAGNET || this == MAGMA || this == HEART || this == SENSOR;
	}

	public static BombType getBombTypeFromItemType(ItemType type) {
		if (type == ItemType.FOLLOW_BOMB)
			return BombType.FOLLOW;
		if (type == ItemType.HEART_BOMB)
			return BombType.HEART;
		if (type == ItemType.MAGMA_BOMB)
			return BombType.MAGMA;
		if (type == ItemType.LAND_MINE_BOMB)
			return BombType.LAND_MINE;
		if (type == ItemType.MAGNET_BOMB)
			return BombType.MAGNET;
		if (type == ItemType.SPIKE_BOMB)
			return BombType.SPIKED;
		if (type == ItemType.REMOTE_BOMB)
			return BombType.REMOTE;
		if (type == ItemType.SPIKE_REMOTE_BOMB)
			return BombType.SPIKED_REMOTE;
		if (type == ItemType.RUBBER_BOMB)
			return BombType.RUBBER;
		if (type == ItemType.SENSOR_BOMB)
			return BombType.SENSOR;
		if (type == ItemType.P_BOMB)
			return BombType.P;
		return BombType.NORMAL;
	}
	
}
