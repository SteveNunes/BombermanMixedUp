package frameset_tags;

import enums.ImageAlignment;
import frameset.Sprite;
import util.Misc;

public class SetSprAlign extends FrameTag {

	public ImageAlignment alignment;

	public SetSprAlign(ImageAlignment alignment) {
		this.alignment = alignment;
	}

	public SetSprAlign(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			alignment = ImageAlignment.valueOf(params[0]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetSprAlign getNewInstanceOfThis() {
		return new SetSprAlign(alignment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setAlignment(alignment);
	}

}
