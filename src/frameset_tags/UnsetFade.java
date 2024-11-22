package frameset_tags;

import frameset.Sprite;
import tools.Draw;

public class UnsetFade extends FrameTag {

	public String tags;
	
	public UnsetFade() {}
	
	@Override
	public String toString() {
		return "{" + getClassName(this) + "}";
	}

	public UnsetFade(String tags) {
		validateStringTags(this, tags);
		this.tags = tags;
	}

	@Override
	public UnsetFade getNewInstanceOfThis() {
		return new UnsetFade(tags);
	}

	@Override
	public void process(Sprite sprite) {
		Draw.setFade(null);
	}

}