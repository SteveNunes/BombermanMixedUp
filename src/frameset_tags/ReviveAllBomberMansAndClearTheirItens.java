package frameset_tags;

import entities.BomberMan;
import frameset.Sprite;

public class ReviveAllBomberMansAndClearTheirItens extends FrameTag {

	public ReviveAllBomberMansAndClearTheirItens(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}
	
	@Override
	public ReviveAllBomberMansAndClearTheirItens getNewInstanceOfThis() {
		return new ReviveAllBomberMansAndClearTheirItens(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		BomberMan.reviveAllBomberMansAndClearTheirItens();
	}

}