package frameset_tags;

import entities.Bomb;
import frameset.Sprite;
import util.Misc;

public class SetBombStucked extends FrameTag {
	
	public TileCoord2 targetTile;
	public boolean state;
	
	public SetBombStucked(boolean state, TileCoord2 targetTile) {
		this.targetTile = targetTile;
		this.state = state;
	}

	@Override
	public String toString()
		{ return "{" + getClassName(this) + ";" + state + ";" + targetTile.getOriginalTag() + "}"; }

	public SetBombStucked(String tags) {
		String[] params = validateStringTags(this, tags, 2);
		int n = 0;
		try {
			state = Boolean.parseBoolean(params[n = 0]);
			targetTile = stringToTileCoord2(params[n = 1]);
		}
		catch (Exception e)
			{ e.printStackTrace(); throw new RuntimeException(params[n] + " - Invalid parameter"); }
	}

	@Override
	public SetBombStucked getNewInstanceOfThis()
		{ return new SetBombStucked(state, targetTile); }

	@Override
	public void process(Sprite sprite) { 
		processTile(sprite.getTileCoord(), targetTile, coord -> {
			Bomb bomb = Bomb.getBombAt(coord);
			if (bomb != null)
				bomb.setStucked(state);
		});
	}

}