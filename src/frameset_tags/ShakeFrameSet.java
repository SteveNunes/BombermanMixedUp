package frameset_tags;

import frameset.Sprite;

public class ShakeFrameSet extends ShakeSprite {

	public ShakeFrameSet(Double startStrengthX, Double startStrengthY, Double incStrengthX, Double incStrengthY, Double finalStrengthX, Double finalStrengthY) {
		super(startStrengthX, startStrengthY, incStrengthX, incStrengthY, finalStrengthX, finalStrengthY);
	}

	public ShakeFrameSet(String tags) {
		super(tags);
	}

	@Override
	public ShakeFrameSet getNewInstanceOfThis() {
		return new ShakeFrameSet(startStrengthX, startStrengthY, incStrengthX, incStrengthY, finalStrengthX, finalStrengthY);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceEntity().setShake(startStrengthX, startStrengthY, incStrengthX, incStrengthY, finalStrengthX, finalStrengthY);
	}

}
