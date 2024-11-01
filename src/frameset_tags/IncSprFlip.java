package frameset_tags;

import frameset.Sprite;

public class IncSprFlip extends FrameTag {

	public IncSprFlip() {}

	@Override
	public String toString() {
		return "{" + getClassName(this) + "}";
	}

	public IncSprFlip(String tags) {
		validateStringTags(this, tags, 0);
	}

	@Override
	public IncSprFlip getNewInstanceOfThis() {
		return new IncSprFlip();
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setFlip(sprite.getFlip().getNext());
	}

}
