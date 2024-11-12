package enums;

public enum GameInput {

	UP(0),
	RIGHT(1),
	DOWN(2),
	LEFT(3),
	A(4),
	B(5),
	C(6),
	D(7),
	L(8),
	R(9),
	SELECT(10),
	START(11);

	private int value;

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

}
