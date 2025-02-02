package frameset_tags;

import frameset.Sprite;
import maps.MapSet;

public class SetMapLayerIndex extends FrameTag {

	public int increment;

	public SetMapLayerIndex(int increment) {
		this.increment = increment;
	}

	public SetMapLayerIndex(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			increment = Integer.parseInt(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetMapLayerIndex getNewInstanceOfThis() {
		return new SetMapLayerIndex(increment);
	}

	@Override
	public void process(Sprite sprite) {
		MapSet.setCurrentLayerIndex(increment);
	}

}
