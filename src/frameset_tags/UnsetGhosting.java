package frameset_tags;

import frameset.Sprite;

public class UnsetGhosting extends FrameTag {

	public String tags;
	
	public UnsetGhosting() {}
	
	@Override
	public String toString() {
		return "{" + getClassName(this) + "}";
	}

	public UnsetGhosting(String tags) {
		validateStringTags(this, tags);
		this.tags = tags;
	}

	@Override
	public UnsetGhosting getNewInstanceOfThis() {
		return new UnsetGhosting(tags);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceEntity().unsetGhosting();
	}

}