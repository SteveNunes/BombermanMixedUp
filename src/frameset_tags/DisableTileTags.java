package frameset_tags;

import frameset.Sprite;
import maps.MapSet;

public class DisableTileTags extends FrameTag {
	
	public DisableTileTags()
		{ super.deleteMeAfterFirstRead = true; }
	
	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + "}"; }

	public DisableTileTags(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags);
		if (params.length > 0)
			throw new RuntimeException(tags + " - Too much parameters");
	}

	@Override
	public DisableTileTags getNewInstanceOfThis()
		{ return new DisableTileTags(); }

	@Override
	public void process(Sprite sprite)
		{ MapSet.getCurrentLayer().getFirstBottomTileFromCoord(sprite.getTileCoord()).tileTags.disableTags(); }

}
