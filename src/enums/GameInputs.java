package enums;

public enum GameInputs {

	UP(0),
	RIGHT(1),
	DOWN(2),
	LEFT(3),
	A(4),
	B(5),
	C(6),
	D(7),
	SELECT(8),
	START(9);
	
	private int value;
	
	private GameInputs(int value)
		{ this.value = value; }
	
	static GameInputs[] inputs = {UP,	RIGHT, DOWN, LEFT, A, B, C, D, SELECT, START};
	
	public static GameInputs[] getList()
		{ return inputs; }
	
	public int getValue()	
		{ return value; }
	
	public boolean isDirection()
		{ return getDirection() != null; }
	
	public Direction getDirection()
		{ return this == UP ? Direction.UP : this == RIGHT ? Direction.RIGHT : this == DOWN ? Direction.DOWN : this == LEFT ? Direction.LEFT : null; }
	
}
