package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetBlockedMovement extends FrameTag {

	public boolean value;

	public SetBlockedMovement(boolean value) {
		this.value = value;
	}

	public SetBlockedMovement(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags);
		if (params.length > 1)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length == 1) {
			int n = 0;
			try {
				value = Boolean.parseBoolean(params[n]);
			}
			catch (Exception e) {
	    	Misc.addErrorOnLog(e, ".\\errors.log");
				throw new RuntimeException(params[n] + " - Invalid parameter");
			}
		}
		else
			value = true;
	}

	@Override
	public SetBlockedMovement getNewInstanceOfThis() {
		return new SetBlockedMovement(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceEntity().setBlockedMovement(value);
	}

}
