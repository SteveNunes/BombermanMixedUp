package frameset_tags;

import frameset.Sprite;

public class SetGhosting extends FrameTag {

	public int ghostingDistance;
	public double ghostingOpacityDec;
		
	public SetGhosting(int ghostingDistance, double ghostingOpacityDec) {
		this.ghostingDistance = ghostingDistance;
		this.ghostingOpacityDec = ghostingOpacityDec;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + ghostingDistance + ";" + ghostingOpacityDec + "}";
	}

	public SetGhosting(String tags) {
		String[] params = validateStringTags(this, tags, 2);
		int n = 0;
		try {
			ghostingDistance = Integer.parseInt(params[n]);
			ghostingOpacityDec = Double.parseDouble(params[n = 1]);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(params[n] + " - Invalid parameter");
		}
	}

	@Override
	public SetGhosting getNewInstanceOfThis() {
		return new SetGhosting(ghostingDistance, ghostingOpacityDec);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceEntity().setGhosting(ghostingDistance, ghostingOpacityDec);
	}

}
