package frameset_tags;

import frameset.Sprite;

public class SetJumpMove extends FrameTag {

	public double jumpStrenght;
	public double strenghtMultipiler;
	public int durationFrames;

	public SetJumpMove(double jumpStrenght, double strenghtMultipiler, int durationFrames) {
		this.jumpStrenght = jumpStrenght;
		this.strenghtMultipiler = strenghtMultipiler;
		this.durationFrames = durationFrames;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + jumpStrenght + ";" + strenghtMultipiler + ";" + durationFrames + "}";
	}

	public SetJumpMove(String tags) {
		String[] params = validateStringTags(this, tags, 3);
		int n = 0;
		try {
			jumpStrenght = Double.parseDouble(params[n++]);
			strenghtMultipiler = Double.parseDouble(params[n++]);
			durationFrames = Integer.parseInt(params[n++]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public SetJumpMove getNewInstanceOfThis() {
		return new SetJumpMove(jumpStrenght, strenghtMultipiler, durationFrames);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceEntity().setJumpMove(jumpStrenght, strenghtMultipiler, durationFrames);
	}

}