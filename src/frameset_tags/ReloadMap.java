package frameset_tags;

import frameset.Sprite;
import maps.MapSet;

public class ReloadMap extends FrameTag {

	public String tags;
	
	public ReloadMap() {}
	
	@Override
	public String toString() {
		return "{" + getClassName(this) + "}";
	}

	public ReloadMap(String tags) {
		validateStringTags(this, tags);
		this.tags = tags;
	}

	@Override
	public ReloadMap getNewInstanceOfThis() {
		return new ReloadMap(tags);
	}

	@Override
	public void process(Sprite sprite) {
		MapSet.reloadMap();
	}

}