package frameset_tags;

import frameset.Sprite;
import maps.MapSet;

public class ReloadMap extends FrameTag {

	public ReloadMap(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}
	
	@Override
	public ReloadMap getNewInstanceOfThis() {
		return new ReloadMap(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		MapSet.reloadMap();
	}

}