package frameset_tags;

import frameset.Sprite;

public class IncHoldingDesloc extends FrameTag {

	public int incrementX;
	public int incrementY;

	public IncHoldingDesloc(int incrementX, int incrementY) {
		this.incrementX = incrementX;
		this.incrementY = incrementY;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + incrementX + ";" + incrementY + "}";
	}

	public IncHoldingDesloc(String tags) {
		String[] params = validateStringTags(this, tags, 2);
		int n = 0;
		try {
			incrementX = Integer.parseInt(params[n++]);
			incrementY = Integer.parseInt(params[n++]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public IncHoldingDesloc getNewInstanceOfThis() {
		return new IncHoldingDesloc(incrementX, incrementY);
	}

	@Override
	public void process(Sprite sprite) {
		if (sprite.getSourceEntity().getHoldingEntity() != null)
			sprite.getSourceEntity().getHoldingEntity().incHolderDesloc(incrementX, incrementY);
	}

}