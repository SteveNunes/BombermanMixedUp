package frameset_tags;

import java.awt.Rectangle;

import entities.BomberMan;
import frameset.Sprite;
import javafx.scene.image.Image;
import tools.Materials;

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

	@Override
	public String toString() {
		int sx = (int) originSprSizePos.getX(), sy = (int) originSprSizePos.getY(), sw = (int) originSprSizePos.getWidth(), sh = (int) originSprSizePos.getHeight(), tx = (int) outputSprSizePos.getX(), ty = (int) outputSprSizePos.getY(), tw = (int) outputSprSizePos.getWidth(), th = (int) outputSprSizePos.getHeight();
		return "{" + getClassName(this) + ";" + spriteSourceName + ";" + +sx + ";" + sy + ";" + sw + ";" + sh + ";" + spriteIndex + ";" + spritesPerLine + ";" + tx + ";" + ty + ";" + tw + ";" + th + "}";
	}

	public SetSprSource(String tags) {
		String[] params = validateStringTags(this, tags);
		originalSpriteSourceName = params[0];
		spriteSourceName = null;
		Image image = Materials.getImageFromSpriteName(originalSpriteSourceName);
		if (image == null)
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
			spriteSourceName = originalSpriteSourceName;
			if (sprite.getSourceEntity() instanceof BomberMan && spriteSourceName.substring(0, 10).equals("Character."))
				spriteSourceName += "." + ((BomberMan) sprite.getSourceEntity()).getPalleteIndex();
		}
		sprite.setSpriteSourceName(spriteSourceName);
		sprite.setOriginSpritePos(originSprSizePos);
		sprite.setOutputSpritePos(outputSprSizePos);
		sprite.setSpriteIndex(spriteIndex);
		sprite.setSpritesPerLine(spritesPerLine);
	}

}