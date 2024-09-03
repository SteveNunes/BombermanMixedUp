package enums;

import application.Main;

public enum Curse {
	
	NO_BOMB(0),
	MIN_FIRE(1),
	MIN_BOMB(2),
	MIN_SPEED(3),
	ULTRA_SPEED(4),
  SLOW_EXPLODE_BOMB(5),
  FAST_EXPLODE_BOMB(6),
  BLINDNESS(7),
	INVISIBLE(8),
	REVERSED(9),
	SWAP_PLAYERS(10);
	
	private int value;
	private static Curse[] list = {NO_BOMB, MIN_FIRE, MIN_BOMB, MIN_SPEED, ULTRA_SPEED,
			SLOW_EXPLODE_BOMB, FAST_EXPLODE_BOMB, BLINDNESS, INVISIBLE, REVERSED, SWAP_PLAYERS};
	
	Curse(int value)
		{ this.value = value;	}
	
	public int getValue()
		{ return value; }
	
	public Curse getNext() {
		int i = value + 1;
		if (i == list.length)
			i = 0;
		return list[i];
	}

	public Curse getPreview() {
		int i = value - 1;
		if (i == 0)
			i = list.length - 1;
		return list[i];
	}
	
	public static Curse getRandom()
		{ return list[Main.getRandom(0, list.length - 1)]; }

}
