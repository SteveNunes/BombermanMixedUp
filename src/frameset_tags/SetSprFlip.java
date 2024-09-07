package frameset_tags;

import entities.Sprite;
import enums.ImageFlip;
import tools.GameMisc;

public class SetSprFlip extends FrameTag {
	
	private ImageFlip flip;
	
	public SetSprFlip(ImageFlip flip)
		{ this.flip = flip; }

	public ImageFlip getFlip()
		{ return flip; }	

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + flip.name() + "}"; }

	public SetSprFlip(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		int n = 0;
		try
			{ flip = ImageFlip.valueOf(params[n++]); }
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public SetSprFlip getNewInstanceOfThis()
		{ return new SetSprFlip(flip); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.setFlip(getFlip()); }

}