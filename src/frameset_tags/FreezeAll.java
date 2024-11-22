package frameset_tags;

import application.Main;
import frameset.Sprite;

public class FreezeAll extends FrameTag {

	public String tags;
	
	public FreezeAll() {}
	
	@Override
	public String toString() {
		return "{" + getClassName(this) + "}";
	}

	public FreezeAll(String tags) {
		validateStringTags(this, tags);
		this.tags = tags;
	}

	@Override
	public FreezeAll getNewInstanceOfThis() {
		return new FreezeAll(tags);
	}

	@Override
	public void process(Sprite sprite) {
		Main.freezeAll();
	}

}