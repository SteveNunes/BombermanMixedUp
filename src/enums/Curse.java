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
	STUNNED(11);

	private int value;
	private static Curse[] list = { NO_BOMB, MIN_FIRE, MIN_BOMB, MIN_SPEED, ULTRA_SPEED,
			SLOW_EXPLODE_BOMB, FAST_EXPLODE_BOMB, BLINDNESS, INVISIBLE, REVERSED, SWAP_PLAYERS, STUNNED };
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
		put(INVISIBLE, 600);
		put(REVERSED, 600);
		put(SWAP_PLAYERS, 600);
		put(STUNNED, 600);
	}};

	Curse(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

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

	public static Curse getRandom() {
		return CollectionUtils.getRandomItemFromArray(list);
	}

	public static int getDuration(Curse curse) {
		return curse == null ? -1 : duration.get(curse);
	}

}
