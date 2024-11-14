package enums;

import java.util.HashMap;
import java.util.Map;

public enum GameInput {

	UP(0),
	RIGHT(1),
	DOWN(2),
	LEFT(3),
	A(4),
	B(5),
	C(6),
	D(7),
	E(8),
	F(9),
	SELECT(10),
	START(11);

	private int value;
	
	@SuppressWarnings("unused")
	private static Map<GameInput, String> inputNames = new HashMap<>() {{
		put(UP, "Cima");
		put(DOWN, "Baixo");
		put(LEFT, "Esquerda");
		put(RIGHT, "Direita");
		put(A, "Special e Detonador");
		put(B, "Soltar bomba");
		put(C, "Special do Item");
		put(D, "Special da montaria");
		put(E, "Largar último item");
		put(F, "Não definido");
		put(SELECT, "Select");
		put(START, "Start");
	}};

	private GameInput(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public boolean isDirection() {
		return getDirection() != null;
	}

	public Direction getDirection() {
		return this == UP ? Direction.UP : this == RIGHT ? Direction.RIGHT : this == DOWN ? Direction.DOWN : this == LEFT ? Direction.LEFT : null;
	}

	public String getName() {
		return inputNames.get(this);
	}

}
