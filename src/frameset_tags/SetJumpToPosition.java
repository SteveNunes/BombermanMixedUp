package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetJumpToPosition extends FrameTag {

	public Position2 target;
	public double jumpStrenght;
	public double strenghtMultipiler;
	public int durationFrames; 
	
	public SetJumpToPosition(double jumpStrenght, double strenghtMultipiler, int durationFrames, Position2 target) {
		this.jumpStrenght = jumpStrenght;
		this.strenghtMultipiler = strenghtMultipiler;
		this.durationFrames = durationFrames;
		this.target = target == null ? null : new Position2(target);
	}

	public SetJumpToPosition(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags);
		if (params.length > 4)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 3)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			jumpStrenght = Double.parseDouble(params[n]);
			strenghtMultipiler = Double.parseDouble(params[n = 1]);
			durationFrames = Integer.parseInt(params[n = 2]);
			target = params.length == 3 ? null : stringToPosition2(params[n = 3]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			e.printStackTrace();
			throw new RuntimeException(params[n] + " - Invalid parameter");
		}
	}

	@Override
	public SetJumpToPosition getNewInstanceOfThis() {
		return new SetJumpToPosition(jumpStrenght, strenghtMultipiler, durationFrames, target);
	}

	@Override
	public void process(Sprite sprite) {
		if (target == null)
			sprite.getSourceEntity().jumpTo(sprite.getSourceEntity().getPosition(), jumpStrenght, strenghtMultipiler, durationFrames);
		else 
			processTile(sprite.getSourceEntity().getPosition(), target, position -> {
				sprite.getSourceEntity().jumpTo(position, jumpStrenght, strenghtMultipiler, durationFrames);
			});
	}

}
