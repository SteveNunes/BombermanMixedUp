package enums;

import java.util.HashMap;
import java.util.Map;

import util.CollectionUtils;

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
	SWAP_PLAYERS(10),
	STUNNED(11),
	SPAM_BOMB(12);

	private int value;
	
	@SuppressWarnings("serial")
	private static Map<Curse, Integer> duration = new HashMap<>() {{ // FALTA: Ajustar a duração dos curses
		put(NO_BOMB, 600);
		put(MIN_FIRE, 600);
		put(MIN_BOMB, 600);
		put(MIN_SPEED, 600);
		put(ULTRA_SPEED, 600);
		put(SLOW_EXPLODE_BOMB, 600);
		put(FAST_EXPLODE_BOMB, 600);
		put(BLINDNESS, 600);
		put(INVISIBLE, 300);
		put(REVERSED, 600);
		put(SWAP_PLAYERS, 0);
		put(STUNNED, 150);
		put(SPAM_BOMB, 600);
	}};

	Curse(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public Curse getNext() { 
		int i = value + 1;
		if (i == Curse.values().length)
			i = 0;
		return Curse.values()[i];
	}

	public Curse getPreview() {
		int i = value - 1;
		if (i == 0)
			i = Curse.values().length - 1;
		return Curse.values()[i];
	}

	public static Curse getRandom() {
		return CollectionUtils.getRandomItemFromArray(Curse.values());
	}

	public static int getDuration(Curse curse) {
		return curse == null ? -1 : duration.get(curse);
	}

}
