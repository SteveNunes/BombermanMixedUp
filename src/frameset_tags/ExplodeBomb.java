package frameset_tags;

import java.util.ArrayList;
import java.util.List;

import entities.Bomb;
import frameset.Sprite;

public class ExplodeBomb extends FrameTag {

	public List<TileCoord2> targetCoords;

	public ExplodeBomb(List<TileCoord2> targetCoords) {
		this.targetCoords = targetCoords == null ? null : new ArrayList<>(targetCoords);
	}

	@Override
	public String toString() {
		if (targetCoords == null)
			return "{" + getClassName(this) + ";" + tileCoord2ListToString(targetCoords) + "}";
		return "{" + getClassName(this) + "}";
	}

	public ExplodeBomb(String tags) {
		String[] params = validateStringTags(this, tags);
		if (params.length > 1)
			throw new RuntimeException(tags + " - Too few parameters");
		try {
			targetCoords = params.length == 0 ? null : stringToTileCoord2List(params[0]);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public ExplodeBomb getNewInstanceOfThis() {
		return new ExplodeBomb(targetCoords);
	}

	@Override
	public void process(Sprite sprite) {
		if (targetCoords == null) {
			if (sprite.getSourceEntity() instanceof Bomb) {
				((Bomb) sprite.getSourceEntity()).detonate();
				return;
			}
			else
				throw new RuntimeException("This tag was called without coords and the source entity is not a bomb");
		}
		processTile(sprite.getTileCoord(), targetCoords, coord -> {
			if (Bomb.haveBombAt(sprite.getSourceEntity(), coord))
				Bomb.getBombAt(coord).detonate();
		});
	}

}