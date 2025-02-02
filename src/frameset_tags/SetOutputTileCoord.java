package frameset_tags;

import application.Main;
import frameset.Sprite;

//PS: Só utilizar esse FrameTag com Tags de Stage ou de Tiles, pois isso na pratica muda o local de saida do sprite que chamou essa FrameTag
public class SetOutputTileCoord extends FrameTag {

	public int x;
	public int y;

	public SetOutputTileCoord(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public SetOutputTileCoord(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 2);
		int n = 0;
		try {
			x = Integer.parseInt(params[n++]);
			y = Integer.parseInt(params[n++]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public SetOutputTileCoord getNewInstanceOfThis() {
		return new SetOutputTileCoord(x, y);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setX(x * Main.TILE_SIZE);
		sprite.setY(y * Main.TILE_SIZE);
	}

}
