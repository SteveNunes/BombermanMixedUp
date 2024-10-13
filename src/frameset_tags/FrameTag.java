package frameset_tags;

import frameset.Sprite;

public abstract class FrameTag {
	
	// Mudar esse valor para 'true' no construtor da classe que herda FrameTag, se for uma Tag que só precisa ser lida uma unica vez.
	public boolean deleteMeAfterFirstRead = false;
	public int triggerDelay = 0;
	
	public abstract FrameTag getNewInstanceOfThis();

	public abstract void process(Sprite sprite);
	
	public int getTriggerDelay()
		{ return triggerDelay; }
	
	public void setTriggerDelay(int delay)
		{ triggerDelay = delay; }
	
	public static <T> String[] validateStringTags(T clazz, String tags)
		{ return validateStringTags(clazz, tags, -1); }
	
	public static <T> String[] validateStringTags(T clazz, String tags, int totalParams) {
		String thisClass = getClassName(clazz);
		if (tags.length() < thisClass.length() + 2 ||
				tags.charAt(0) != '{' || tags.charAt(tags.length() - 1) != '}')
					throw new RuntimeException(tags + " - Invalid tags");
		tags = tags.substring(1, tags.length() - 1);
		String[] split = tags.split(";");
		if (!split[0].equals(thisClass))
			throw new RuntimeException(tags + " - Invalid tags");
		String[] attribs = new String[split.length - 1];
		if (totalParams != -1) {
			if (attribs.length > totalParams)
				throw new RuntimeException(tags + " - Too much parameters");
			if (attribs.length < totalParams)
				throw new RuntimeException(tags + " - Too few parameters");
		}
		for (int n = 1; n < split.length; n++)
			attribs[n - 1] = split[n];
		return attribs;
	}
	
	public static <T> String getClassName(T clazz)
		{ return clazz.getClass().toString().replace("class frameset_tags.", ""); }
	
	public static int[] getPosWithDeslocFromString(String str) {
		int val = 0, offset = 0;
		if (str.length() > 2 && (str.subSequence(0, 2).equals("--") || str.subSequence(0, 2).equals("++"))) {
			val = -1;
			offset = Integer.parseInt(str.substring(1));
		}
		else {
			try
				{ val = str.equals("-") ? -1 : Integer.parseInt(str); }
			catch (Exception e)
				{ return null; }
		}
		return new int[] {val, offset};
	}
	
}