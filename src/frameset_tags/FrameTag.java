package frameset_tags;

import entities.Sprite;
import tools.GameMisc;

public abstract class FrameTag {
	
	public abstract FrameTag getNewInstanceOfThis();

	public abstract void process(Sprite sprite);
	
	public static <T> String[] validateStringTags(T clazz, String tags)
		{ return validateStringTags(clazz, tags, -1); }
	
	public static <T> String[] validateStringTags(T clazz, String tags, int totalParams) {
		String thisClass = getClassName(clazz);
		if (tags.length() < thisClass.length() + 2 ||
				tags.charAt(0) != '{' || tags.charAt(tags.length() - 1) != '}')
					GameMisc.throwRuntimeException(tags + " - Invalid tags");
		tags = tags.substring(1, tags.length() - 1);
		String[] split = tags.split(";");
		if (!split[0].equals(thisClass))
			GameMisc.throwRuntimeException(tags + " - Invalid tags");
		String[] attribs = new String[split.length - 1];
		if (totalParams != -1) {
			if (attribs.length > totalParams)
				GameMisc.throwRuntimeException(tags + " - Too much parameters");
			if (attribs.length < totalParams)
				GameMisc.throwRuntimeException(tags + " - Too few parameters");
		}
		for (int n = 1; n < split.length; n++)
			attribs[n - 1] = split[n];
		return attribs;
	}
	
	public static <T> String getClassName(T clazz)
		{ return clazz.getClass().toString().replace("class frameset_tags.", ""); }
	
}