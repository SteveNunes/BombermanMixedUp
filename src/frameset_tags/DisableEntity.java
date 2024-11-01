package frameset_tags;

import frameset.Sprite;

public class DisableEntity extends FrameTag {

	public DisableEntity() {}

	@Override
	public String toString() {
		return "{" + getClassName(this) + "}";
	}

	public DisableEntity(String tags) {
		validateStringTags(this, tags, 0);
	}

	@Override
	public DisableEntity getNewInstanceOfThis() {
		return new DisableEntity();
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceEntity().setDisabled();
	}

}
