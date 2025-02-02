package frameset_tags;

import frameset.Sprite;

public class DisableEntity extends FrameTag {

	public DisableEntity(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}

	@Override
	public DisableEntity getNewInstanceOfThis() {
		return new DisableEntity(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceEntity().setDisabled();
	}

}
