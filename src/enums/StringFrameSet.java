package enums;

public enum StringFrameSet {

	FIRE_SKULL_EXPLOSION("{SetSprSource;MainSprites;278;32;16;16;0;5;0;0;16;16},{SetTicksPerFrame;5},{SetSprIndex;0},{SetOutputSprPos;0;0},{SetSprFlip;NONE}"
			+ ",,{SetSprSource;MainSprites;278;32;16;16;0;5;0;0;16;16},{SetSprIndex;9},{SetOutputSprPos;12;-4},{SetSprFlip;NONE}"
			+ ",,{SetSprSource;MainSprites;278;32;16;16;0;5;0;0;16;16},{SetSprIndex;9},{SetOutputSprPos;-12;-4},{SetSprFlip;HORIZONTAL},{SetSprFlip;NONE}"
			+ ",,{SetSprSource;MainSprites;278;32;16;16;0;5;0;0;16;16},{SetSprIndex;9},{SetOutputSprPos;0;-20},{SetSprFlip;NONE}"
			+ "|{SetSprIndex;1},{SetOutputSprPos;0;-16}"
			+ ",,{SetSprIndex;2},{SetOutputSprPos;0;0}"
			+ ",,{SetSprIndex;-}"
			+ ",,{SetSprIndex;-}"
			+ "|{SetSprIndex;3}"
			+ ",,{SetSprIndex;4}"
			+ "|{SetSprIndex;5},{SetOutputSprPos;0;0}"
			+ ",,{SetSprIndex;6},{SetOutputSprY;-17}"
			+ "|{SetSprIndex;6},{SetSprFlip;VERTICAL}"
			+ ",,{SetSprIndex;7},{SetOutputSprY;-27}"
			+ "|{SetSprIndex;7},{SetOutputSprPos;0;0},{SetSprFlip;NONE}"
			+ ",,{SetSprIndex;-}"
			+ "|{SetSprIndex;8}|{SetSprIndex;9}");

	private String value;
	
	private StringFrameSet(String s)
		{ value = s; }
	
	public String getString()
		{ return value; }
	
}
