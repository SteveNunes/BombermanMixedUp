package frameset_tags;

import application.Main;
import frameset.Sprite;

public class UnFreezeAll extends FrameTag {

	public String tags;
	
	public UnFreezeAll() {}
	
	@Override
	public String toString() {
		return "{" + getClassName(this) + "}";
	}

	public UnFreezeAll(String tags) {
		validateStringTags(this, tags);
		this.tags = tags;
	}

	@Override
	public UnFreezeAll getNewInstanceOfThis() {
		return new UnFreezeAll(tags);
	}

	@Override
	public void process(Sprite sprite) {
		Main.unFreezeAll();
	}

}