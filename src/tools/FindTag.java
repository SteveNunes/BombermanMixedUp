package tools;

import java.security.SecureRandom;
import java.util.Random;

public class FindTag {

	private String foundTag;
	private String[] tagValues;
	private static Random random = new Random(new SecureRandom().nextInt(Integer.MAX_VALUE));

	public FindTag(String text, String tag) {
		foundTag = null;
		tagValues = null;
		int f = text.indexOf(tag), i = f + tag.length(), i2;
		if (f >= 0 && i >= 0 && i < text.length() && text.charAt(i) == '\"' &&
				++i < text.length() && (i2 = text.indexOf("\"", i)) >= 0) {
					foundTag = "" + text.subSequence(i, i2);
					tagValues = (foundTag).split(";");
					foundTag = "" + text.subSequence(f, i2 + 1);
		}
	}

	public String getFoundTag()
		{ return foundTag; }

	public String[] getFoundTagValues()
		{ return tagValues; }

	public String getRandomFoundTagValue()
		{ return foundTag == null ? null : tagValues[random.nextInt(tagValues.length)]; }
	
}