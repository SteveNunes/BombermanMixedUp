package frameset_tags;

import frameset.Sprite;

public class DecSprFlip extends FrameTag {

	public DecSprFlip() {}

	@Override
	public String toString() {
		return "{" + getClassName(this) + "}";
	}

	public DecSprFlip(String tags) {
		validateStringTags(this, tags, 0);
	}

	@Override
	public DecSprFlip getNewInstanceOfThis() {
		return new DecSprFlip();
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setFlip(sprite.getFlip().getPreview());
	}

}
