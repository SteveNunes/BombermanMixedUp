package frameset_tags;

import frameset.Sprite;

public class UnsetDamageBox extends FrameTag {

	public UnsetDamageBox(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}
	
	@Override
	public UnsetDamageBox getNewInstanceOfThis() {
		return new UnsetDamageBox(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.unsetDamageBox();
	}

}