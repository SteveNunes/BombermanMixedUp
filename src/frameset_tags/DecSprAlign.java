package frameset_tags;

import frameset.Sprite;

public class DecSprAlign extends FrameTag {

	public DecSprAlign(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}

	@Override
	public DecSprAlign getNewInstanceOfThis() {
		return new DecSprAlign(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setAlignment(sprite.getAlignment().getPreview());
	}

}