package enums;

public enum CpuDificult {
	
	VERY_EASY(0),
	EASY(1),
	NORMAL(2),
	HARD(3),
	VERY_HARD(4);
	
	private int value;
	
	private CpuDificult(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}

}
