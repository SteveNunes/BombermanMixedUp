package frameset_tags;

import entities.BomberMan;
import frameset.Sprite;

public class ReviveAllBomberMansAndClearTheirItens extends FrameTag {

	public String tags;
	
	public ReviveAllBomberMansAndClearTheirItens() {}
	
	@Override
	public String toString() {
		return "{" + getClassName(this) + "}";
	}

	public ReviveAllBomberMansAndClearTheirItens(String tags) {
		validateStringTags(this, tags);
		this.tags = tags;
	}

	@Override
	public ReviveAllBomberMansAndClearTheirItens getNewInstanceOfThis() {
		return new ReviveAllBomberMansAndClearTheirItens(tags);
	}

	@Override
	public void process(Sprite sprite) {
		BomberMan.reviveAllBomberMansAndClearTheirItens();
	}

}