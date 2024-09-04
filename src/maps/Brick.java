package maps;

import java.util.List;

import entities.Sprite;
import enums.TileProp;
import gui.util.ImageUtils;
import objmoveutils.Position;

public class Brick {
	
	private Position position;
	private List<Sprite> animationStand;
	private float animationStandFrameSpeed;
	private List<Sprite> animationBreaking;
	private float animationBreakingFrameSpeed;
	private List<Sprite> animationRegenerating;
	private float animationRegeneratingFrameSpeed;
	private List<TileProp> typeProps;
	
	public Brick(Position position, List<TileProp> typeProps, List<Sprite> animationStand, float animationStandFrameSpeed) {
		this.animationStand = animationStand;
		this.animationStandFrameSpeed = animationStandFrameSpeed;
		this.typeProps = typeProps;
		this.position = position;
		animationBreaking = null;
		animationRegenerating = null;
		ImageUtils.copyAreaFromWritableImage(null);
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

	public List<TileProp> getTypeProps()
		{ return typeProps; }

	public void setTypeProps(List<TileProp> typeProps)
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
