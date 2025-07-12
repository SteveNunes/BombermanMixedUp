package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetMultiSprIndexByDirection extends FrameTag {

	public int[] values;

	public SetMultiSprIndexByDirection(int up, int right, int down, int left) { // Passar o valor POSITIVO incrementado de 1, e NEGATIVO decrementado de 1
		values = new int[] { up, right, down, left };
		super.deleteMeAfterFirstRead = true;
	}

	public SetMultiSprIndexByDirection(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 4);
		values = new int[4];
		int n = 0;
		try {
			for (; n < 4; n++) {
				values[n] = Integer.parseInt(params[n].charAt(params[n].length() - 1) != 'f' ? params[n] : params[n].substring(0, params[n].length() - 1));
				if (params[n].charAt(params[n].length() - 1) == 'f')
					values[n] = -(values[n] += 1);
			}
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[n] + " - Invalid parameter");
		}
	}

	@Override
	public SetMultiSprIndexByDirection getNewInstanceOfThis() {
		return new SetMultiSprIndexByDirection(values[0], values[1], values[2], values[3]);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setMultiFrameIndexByDirection(values[0], values[1], values[2], values[3]);
	}

}
