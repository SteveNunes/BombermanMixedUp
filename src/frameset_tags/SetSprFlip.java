package frameset_tags;

import enums.ImageFlip;
import frameset.Sprite;

public class SetSprFlip extends FrameTag {

	public ImageFlip flip;

	public SetSprFlip(ImageFlip flip) {
		this.flip = flip;
	}

	public SetSprFlip(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		int n = 0;
		try {
			flip = ImageFlip.valueOf(params[n++]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public SetSprFlip getNewInstanceOfThis() {
		return new SetSprFlip(flip);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setFlip(flip);
	}

}
