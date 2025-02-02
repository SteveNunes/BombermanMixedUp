package frameset_tags;

import entities.Ride;
import frameset.Sprite;

public class SetRiderFrontValue extends FrameTag {

	public Integer value;

	public SetRiderFrontValue(Integer value) {
		this.value = value;
	}

	public SetRiderFrontValue(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			value = Integer.parseInt(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetRiderFrontValue getNewInstanceOfThis() {
		return new SetRiderFrontValue(value);
	}

	@Override
	public void process(Sprite sprite) {
		if (sprite.getSourceEntity() instanceof Ride &&
				((Ride)sprite.getSourceEntity()).getOwner() != null)
					((Ride)sprite.getSourceEntity()).setRiderFrontValue(value);
	}

}
