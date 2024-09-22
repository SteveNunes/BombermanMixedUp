package enums;

import tools.GameMisc;
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
			GameMisc.throwRuntimeException(bombId + " - Invalid bomb ID");
		return list[bombId];
	}

}
