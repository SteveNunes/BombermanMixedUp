package frameset_tags;

import frameset.Sprite;

public class IncSprAlign extends FrameTag {

	public IncSprAlign(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}

	@Override
	public IncSprAlign getNewInstanceOfThis() {
		return new IncSprAlign(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setAlignment(sprite.getAlignment().getNext());
	}

}
