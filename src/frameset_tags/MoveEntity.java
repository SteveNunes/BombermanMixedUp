package frameset_tags;

import java.util.HashSet;
import java.util.Set;

import application.Main;
import entities.Entity;
import enums.FindType;
import frameset.Sprite;
import objmoveutils.TileCoord;
import tools.Tools;

public class MoveEntity extends FrameTag {

	public double speed;
	public String onColideTrigger;
	public Integer onColideFrameIndex;
	public Set<FindType> colideTypes;

	public MoveEntity(double speed, String onColideTrigger, Integer onColideFrameIndex, Set<FindType> colideType) {
		this.speed = speed;
		this.onColideTrigger = onColideTrigger;
		this.onColideFrameIndex = onColideFrameIndex;
		this.colideTypes = colideType == null ? null : new HashSet<>(colideType);
	}

	@Override
	public String toString() {
		StringBuilder sb = colideTypes == null ? null : new StringBuilder();
		if (sb != null)
			for (FindType ft : colideTypes) {
				if (!sb.isEmpty())
					sb.append(":");
				sb.append(ft.name());
			}
		return "{" + getClassName(this) + ";" + speed + ";" + onColideTrigger + ";" + sb.toString() + "}";
	}

	public MoveEntity(String tags) {
		String[] params = validateStringTags(this, tags);
		if (params.length > 3)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length >= 1) {
			int n = 0;
			try {
				speed = params.length == 0 ? 1 : Double.parseDouble(params[n = 0]);
				if (params.length <= 1)
					onColideTrigger = null;
				else {
					try {
						onColideFrameIndex = Integer.parseInt(params[n = 1]);
						onColideTrigger = null;
					}
					catch (Exception e) {
						onColideFrameIndex = null;
						onColideTrigger = params[n];
					}
				}
				if (params.length <= 2)
					colideTypes = null;
				else {
					String[] split = params[n = 2].split(":");
					colideTypes = new HashSet<>();
					for (String s : split)
						colideTypes.add(FindType.valueOf(s));
				}
			}
			catch (Exception e) {
				throw new RuntimeException(params[n] + " - Invalid parameter");
			}
		}
		else
			speed = -1;
	}

	@Override
	public MoveEntity getNewInstanceOfThis() {
		return new MoveEntity(speed, onColideTrigger, onColideFrameIndex, colideTypes);
	}

	@Override
	public void process(Sprite sprite) {
		Entity entity = sprite.getSourceEntity();
		entity.moveEntity(speed);
		if (colideTypes != null) {
			int[][] inc = {
					{Main.TILE_SIZE / 2, -1},
					{Main.TILE_SIZE + 1, Main.TILE_SIZE / 2},
					{Main.TILE_SIZE / 2, Main.TILE_SIZE + 1},
					{-1, Main.TILE_SIZE / 2}
			};
			TileCoord coord = entity.getPosition().getNewInstance()
					.incPosition(inc[entity.getDirection().get4DirValue()][0],
											 inc[entity.getDirection().get4DirValue()][1]).getTileCoord();
			if (Tools.getWichObjectIsOnTile(coord, colideTypes, entity.getPassThrough()) != null) {
				if (onColideTrigger != null && entity.haveFrameSet(onColideTrigger))
					entity.setFrameSet(onColideTrigger);
				else if (onColideFrameIndex != null)
					entity.getCurrentFrameSet().setCurrentFrameIndex(onColideFrameIndex);
			}
		}
	}

}
