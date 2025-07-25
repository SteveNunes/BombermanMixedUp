package frameset_tags;

import java.awt.Rectangle;

import entities.BomberMan;
import entities.Ride;
import frameset.Sprite;
import javafx.scene.image.Image;
import tools.Materials;
import util.Misc;

public class SetSprSource extends FrameTag {

	public String originalSpriteSourceName;
	public String spriteSourceName;
	public Rectangle originSprSizePos;
	public Rectangle outputSprSizePos;
	public int spriteIndex;
	public int spritesPerLine;

	public SetSprSource(String spriteSourceName, Rectangle originSprSizePos, Rectangle outputSprSizePos, int spriteIndex, int spritesPerLine) {
		originalSpriteSourceName = spriteSourceName;
		spriteSourceName = null;
		this.originSprSizePos = new Rectangle(originSprSizePos);
		this.outputSprSizePos = new Rectangle(outputSprSizePos);
		this.spritesPerLine = spritesPerLine;
		this.spriteIndex = spriteIndex;
		deleteMeAfterFirstRead = true;
	}

	public SetSprSource(String spriteSourceName, Rectangle originSprSizePos, Rectangle outputSprSizePos) {
		this(spriteSourceName, originSprSizePos, outputSprSizePos, 0, 0);
	}

	public SetSprSource(String spriteSourceName, Rectangle originSprSizePos, int spriteIndex, int spritesPerLine) {
		this(spriteSourceName, originSprSizePos, new Rectangle(0, 0, (int) originSprSizePos.getWidth(), (int) originSprSizePos.getHeight()), spriteIndex, spritesPerLine);
	}

	public SetSprSource(String spriteSourceName, Rectangle originSprSizePos) {
		this(spriteSourceName, originSprSizePos, new Rectangle(0, 0, (int) originSprSizePos.getWidth(), (int) originSprSizePos.getHeight()), 0, 0);
	}

	public SetSprSource(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags);
		originalSpriteSourceName = params[0];
		spriteSourceName = null;
		Image image = Materials.getImageFromSpriteName(originalSpriteSourceName);
		if (image == null && !originalSpriteSourceName.equals("-"))
			throw new RuntimeException(params[0] + " - Invalid sprite source name");
		int n = 0, t = params.length;
		try {
			int sx = Integer.parseInt(params[n = 1]), sy = Integer.parseInt(params[n = 2]), sw = params[n = 3].equals("-") ? (int) image.getWidth() : Integer.parseInt(params[n]), sh = params[n = 4].equals("-") ? (int) image.getHeight() : Integer.parseInt(params[n]), tx = t < 8 ? 0 : Integer.parseInt(params[n = 7]), ty = t < 9 ? 0 : Integer.parseInt(params[n = 8]), tw = t < 10 || params[n = 9].equals("-") ? sw : Integer.parseInt(params[n]), th = t < 11 || params[n = 10].equals("-") ? sh : Integer.parseInt(params[n]);
			spriteIndex = t < 6 ? 0 : Integer.parseInt(params[n = 5]);
			spritesPerLine = t < 7 ? 0 : Integer.parseInt(params[n = 6]);
			originSprSizePos = new Rectangle(sx, sy, sw, sh);
			outputSprSizePos = new Rectangle(tx, ty, tw, th);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[n] + " - Invalid parameter");
		}
	}

	@Override
	public SetSprSource getNewInstanceOfThis() {
		return new SetSprSource(originalSpriteSourceName, originSprSizePos, outputSprSizePos, spriteIndex, spritesPerLine);
	}

	@Override
	public void process(Sprite sprite) {
		if (spriteSourceName == null) {
			String[] strings = {"Character.", "Ride."};
			for (int n = 0; n < 2; n++) {
				String s = strings[n];
				spriteSourceName = originalSpriteSourceName;
				if (sprite.getSourceEntity() instanceof BomberMan && spriteSourceName.length() > s.length() && spriteSourceName.substring(0, s.length()).equals(s)) {
					spriteSourceName += "." + (n == 0 ? ((BomberMan) sprite.getSourceEntity()).getPalleteIndex()
							: spriteSourceName.equals("Ride.0") ? ((BomberMan) sprite.getSourceEntity()).getPlayerId() :
							((Ride) sprite.getSourceEntity()).getPalleteIndex());
					break;
				}
			}
		}
		sprite.setSpriteSourceName(spriteSourceName);
		sprite.setOriginSpritePos(originSprSizePos);
		sprite.setOutputSpritePos(outputSprSizePos);
		sprite.setSpriteIndex(spriteIndex);
		sprite.setSpritesPerLine(spritesPerLine);
	}

}