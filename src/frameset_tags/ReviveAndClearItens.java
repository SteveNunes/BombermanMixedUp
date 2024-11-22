package frameset_tags;

import entities.BomberMan;
import frameset.Sprite;

public class ReviveAndClearItens extends FrameTag {

	public String tags;
	
	public ReviveAndClearItens() {}
	
	@Override
	public String toString() {
		return "{" + getClassName(this) + "}";
	}

	public ReviveAndClearItens(String tags) {
		validateStringTags(this, tags);
		this.tags = tags;
	}

	@Override
	public ReviveAndClearItens getNewInstanceOfThis() {
		return new ReviveAndClearItens(tags);
	}

	@Override
	public void process(Sprite sprite) {
		if (sprite.getSourceEntity() instanceof BomberMan)
			((BomberMan)sprite.getSourceEntity()).reviveAndClearItens();
	}

}