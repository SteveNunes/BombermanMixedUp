package frameset_tags;

import entities.BomberMan;
import frameset.Sprite;

public class ReviveAndClearItens extends FrameTag {

	public ReviveAndClearItens(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}
	
	@Override
	public ReviveAndClearItens getNewInstanceOfThis() {
		return new ReviveAndClearItens(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		if (sprite.getSourceEntity() instanceof BomberMan)
			((BomberMan)sprite.getSourceEntity()).reviveAndClearItens();
	}

}