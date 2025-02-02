package frameset_tags;

import entities.BomberMan;
import frameset.Sprite;

public class SoftResetAfterMapChange extends FrameTag {

	public SoftResetAfterMapChange(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}
	
	@Override
	public SoftResetAfterMapChange getNewInstanceOfThis() {
		return new SoftResetAfterMapChange(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		if (sprite.getSourceEntity() instanceof BomberMan)
			((BomberMan)sprite.getSourceEntity()).softResetAfterMapChange();
	}

}