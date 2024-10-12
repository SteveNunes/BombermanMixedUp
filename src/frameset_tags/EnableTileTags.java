package frameset_tags;

import frameset.Sprite;
import maps.MapSet;

public class EnableTileTags extends FrameTag {
	
	public EnableTileTags()
		{ super.deleteMeAfterFirstRead = true; }
	
	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + "}"; }

	public EnableTileTags(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags);
		if (params.length > 0)
			throw new RuntimeException(tags + " - Too much parameters");
	}

	@Override
	public EnableTileTags getNewInstanceOfThis()
		{ return new EnableTileTags(); }

	@Override
	public void process(Sprite sprite)
		{ MapSet.getCurrentLayer().getFirstBottomTileFromCoord(sprite.getTileCoord()).tileTags.enableTags(); }

}
