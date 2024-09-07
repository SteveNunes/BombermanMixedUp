package frameset_tags;

import entities.Sprite;
import enums.ImageAlignment;
import tools.GameMisc;

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
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprAlign getNewInstanceOfThis()
		{ return new SetSprAlign(alignment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.setAlignment(getAlignment()); }

}
