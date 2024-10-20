package frameset_tags;

import frameset.Sprite;

public class DoJump extends FrameTag {
	
	public double jumpStrenght;
	public double strenghtMultipiler;
	public int speedInFrames;
	
	public DoJump(double jumpStrenght, double strenghtMultipiler, int speedInFrames) {
		this.jumpStrenght = jumpStrenght;
		this.strenghtMultipiler = strenghtMultipiler;
		this.speedInFrames = speedInFrames;
	}

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + jumpStrenght + ";" + strenghtMultipiler + ";" + speedInFrames + "}"; }

	public DoJump(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 3);
		int n = 0;
		try {
			jumpStrenght = Double.parseDouble(params[n++]);
			strenghtMultipiler = Double.parseDouble(params[n++]);
			speedInFrames = Integer.parseInt(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}
	
	@Override
	public DoJump getNewInstanceOfThis()
		{ return new DoJump(jumpStrenght, strenghtMultipiler, speedInFrames); }

	@Override
	public void process(Sprite sprite)
		{ sprite.getSourceFrameSet().setJumpMove(jumpStrenght, strenghtMultipiler, speedInFrames); }

}
