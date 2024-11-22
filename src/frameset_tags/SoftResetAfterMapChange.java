package frameset_tags;

import entities.BomberMan;
import frameset.Sprite;

public class SoftResetAfterMapChange extends FrameTag {

	public String tags;
	
	public SoftResetAfterMapChange() {}
	
	@Override
	public String toString() {
		return "{" + getClassName(this) + "}";
	}

	public SoftResetAfterMapChange(String tags) {
		validateStringTags(this, tags);
		this.tags = tags;
	}

	@Override
	public SoftResetAfterMapChange getNewInstanceOfThis() {
		return new SoftResetAfterMapChange(tags);
	}

	@Override
	public void process(Sprite sprite) {
		if (sprite.getSourceEntity() instanceof BomberMan)
			((BomberMan)sprite.getSourceEntity()).softResetAfterMapChange();
	}

}