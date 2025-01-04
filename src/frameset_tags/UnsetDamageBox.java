package frameset_tags;

import frameset.Sprite;

public class UnsetDamageBox extends FrameTag {

	public String tags;
	
	public UnsetDamageBox() {}
	
	@Override
	public String toString() {
		return "{" + getClassName(this) + "}";
	}

	public UnsetDamageBox(String tags) {
		validateStringTags(this, tags);
		this.tags = tags;
	}

	@Override
	public UnsetDamageBox getNewInstanceOfThis() {
		return new UnsetDamageBox(tags);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.unsetDamageBox();
	}

}