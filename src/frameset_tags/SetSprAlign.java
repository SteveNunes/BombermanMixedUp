package frameset_tags;

import enums.ImageAlignment;

public class SetSprAlign extends FrameTag {
	
	private ImageAlignment alignment;
	
	public SetSprAlign(ImageAlignment alignment)
		{ this.alignment = alignment; }

	public ImageAlignment getAlignment()
		{ return alignment; }	

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + alignment.name() + "}"; }

	public SetSprAlign(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ alignment = ImageAlignment.valueOf(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprAlign getNewInstanceOfThis()
		{ return new SetSprAlign(alignment); }
	
}
