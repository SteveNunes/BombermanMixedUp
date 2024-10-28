package frameset_tags;

import entities.Entity;
import enums.Direction;
import frameset.Sprite;
import tools.Sound;

public class PushEntity extends FrameTag {
	
	public Double startStrenght;
	public Double decStrenght;
	public Direction direction;
	public TileCoord2 targetTile;
	public String triggerSound;
	public String soundWhenHits;
	
	public PushEntity(Double startStrenght, Double decStrenght, Direction direction, TileCoord2 targetTile, String triggerSound, String soundWhenHits) {
		this.targetTile = targetTile;
		this.startStrenght = startStrenght;
		this.decStrenght = decStrenght;
		this.direction = direction;
		this.triggerSound = triggerSound;
		this.soundWhenHits = soundWhenHits;
	}

	@Override
	public String toString()
		{ return "{" + getClassName(this) + ";" + startStrenght + ";" + (decStrenght == null ? "-" : decStrenght) + ";" + direction + ";" + targetTile.getOriginalTag() + ";" + triggerSound + ";" + soundWhenHits + "}"; }

	public PushEntity(String tags) {
		String[] params = validateStringTags(this, tags);
		if (params.length > 7)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 5)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			startStrenght = Double.parseDouble(params[n++]);
			decStrenght = params[n].equals("-") ? null : Double.parseDouble(params[n]); n++;
			direction = params[n].equals("-") ? null : Direction.valueOf(params[n]);
			targetTile = stringToTileCoord2(params[++n]);
			triggerSound = ++n >= params.length || params[n].equals("-") ? null : params[n];
			soundWhenHits = ++n >= params.length || params[n].equals("-") ? null : params[n];
		}
		catch (Exception e)
			{ e.printStackTrace(); throw new RuntimeException(params[n] + " - Invalid parameter"); }
	}

	@Override
	public PushEntity getNewInstanceOfThis()
		{ return new PushEntity(startStrenght, decStrenght, direction, targetTile, triggerSound, soundWhenHits); }

	@Override
	public void process(Sprite sprite) {
		Entity entity = sprite.getSourceEntity();
		if (entity.getPushEntity() != null)
			set(sprite, entity);
	}
	
	public void set(Sprite sprite, Entity entity) {
		processTile(sprite.getTileCoord(), targetTile, coord -> {
			if (triggerSound != null)
				Sound.playWav(triggerSound);
			entities.PushEntity pushEntity = new entities.PushEntity(entity, startStrenght, decStrenght, direction);
			if (soundWhenHits != null)
				pushEntity.setOnColideEvent(e -> {
					Sound.playWav(soundWhenHits);
					sprite.getSourceEntity().setShake(2d, -0.05, 0d);
					entity.unsetGhosting();
				});
			pushEntity.setTargetTile(coord);
			pushEntity.testIfPathIsFree();
			if (pushEntity.isActive())
				entity.setPushEntity(pushEntity);
			entity.setGhosting(2, 0.2);
		});
	}

}
