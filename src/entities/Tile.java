package entities;

import java.util.List;

import enums.TileProps;
import frameset.Sprite;
import objmoveutils.Position;

public class Tile {
	
	private Position position;
	private List<Sprite> animationStand;
	private float animationStandFrameSpeed;
	private List<Sprite> animationBreaking;
	private float animationBreakingFrameSpeed;
	private List<Sprite> animationRegenerating;
	private float animationRegeneratingFrameSpeed;
	private List<TileProps> typeProps;
	
	public Tile(Position position, List<TileProps> typeProps, List<Sprite> animationStand, float animationStandFrameSpeed) {
		this.animationStand = animationStand;
		this.animationStandFrameSpeed = animationStandFrameSpeed;
		this.typeProps = typeProps;
		this.position = position;
		animationBreaking = null;
		animationRegenerating = null;
	}

	public Position getPosotion()
		{ return position; }

	public void setPosotion(Position position)
		{ this.position = position; }

	public List<Sprite> getAnimationStand()
		{ return animationStand; }

	public void setAnimationStand(List<Sprite> animationStand)
		{ this.animationStand = animationStand; }

	public List<Sprite> getAnimationBreaking()
		{ return animationBreaking; }

	public void setAnimationBreaking(List<Sprite> animationBreaking)
		{ this.animationBreaking = animationBreaking; }

	public List<Sprite> getAnimationRegenerating()
		{ return animationRegenerating; }

	public void setAnimationRegenerating(List<Sprite> animationRegenerating)
		{ this.animationRegenerating = animationRegenerating; }

	public List<TileProps> getTypeProps()
		{ return typeProps; }

	public void setTypeProps(List<TileProps> typeProps)
		{ this.typeProps = typeProps; }

	public float getAnimationStandFrameSpeed() {
		return animationStandFrameSpeed;
	}

	public void setAnimationStandFrameSpeed(float animationStandFrameSpeed)
		{ this.animationStandFrameSpeed = animationStandFrameSpeed; }

	public float getAnimationBreakingFrameSpeed()
		{ return animationBreakingFrameSpeed; }

	public void setAnimationBreakingFrameSpeed(float animationBreakingFrameSpeed)
		{ this.animationBreakingFrameSpeed = animationBreakingFrameSpeed; }

	public float getAnimationRegeneratingFrameSpeed()
		{ return animationRegeneratingFrameSpeed; }

	public void setAnimationRegeneratingFrameSpeed(float animationRegeneratingFrameSpeed)
		{ this.animationRegeneratingFrameSpeed = animationRegeneratingFrameSpeed; }	

}
