package frameset_tags;

import java.awt.Rectangle;

import frameset.Sprite;
import javafx.scene.image.Image;
import tools.Tools;
import tools.Materials;

public class SetSprSource extends FrameTag {
	
	private Image spriteSource;
	private Rectangle originSprSizePos;
	private Rectangle outputSprSizePos;
	private int spriteIndex;
	private int spritesPerLine;
	
	public SetSprSource(Image spriteSource, Rectangle originSprSizePos, Rectangle outputSprSizePos, int spriteIndex, int spritesPerLine) {
		this.spriteSource = spriteSource;
		this.originSprSizePos = originSprSizePos;
		this.outputSprSizePos = outputSprSizePos;
		this.spritesPerLine = spritesPerLine;
		this.spriteIndex = spriteIndex;
	}

	public SetSprSource(Image spriteSource, Rectangle originSprSizePos, Rectangle outputSprSizePos)
		{ this(spriteSource, originSprSizePos, outputSprSizePos, 0, 0);	}
	
	public SetSprSource(Image spriteSource, Rectangle originSprSizePos, int spriteIndex, int spritesPerLine)
		{ this(spriteSource, originSprSizePos, new Rectangle(0, 0, (int)originSprSizePos.getWidth(), (int)originSprSizePos.getHeight()), spriteIndex, spritesPerLine);	}

	public SetSprSource(Image spriteSource, Rectangle originSprSizePos)
		{ this(spriteSource, originSprSizePos, new Rectangle(0, 0, (int)originSprSizePos.getWidth(), (int)originSprSizePos.getHeight()), 0, 0);	}

	public Image getSpriteSource()
		{ return spriteSource; }

	public Rectangle getOriginSprSizePos()
		{ return originSprSizePos; }

	public Rectangle getOutputSprSizePos()
		{ return outputSprSizePos; }

	public int getSpritesPerLine() {
		return spritesPerLine;
	}

	public int getSpriteIndex()
		{ return spriteIndex; }

	@Override
	public String toString() {
		int sx = (int)originSprSizePos.getX(), sy = (int)originSprSizePos.getY(),
				sw = (int)originSprSizePos.getWidth(), sh = (int)originSprSizePos.getHeight(),
				tx = (int)outputSprSizePos.getX(), ty = (int)outputSprSizePos.getY(),
				tw = (int)outputSprSizePos.getWidth(), th = (int)outputSprSizePos.getHeight();
		return "{" + FrameTag.getClassName(this) + ";" + Materials.getSpriteNameFromImage(spriteSource) + ";" + + sx + ";" + sy + ";" + sw + ";" + sh + ";" + spriteIndex + ";" + spritesPerLine + ";" + tx + ";" + ty + ";" + tw + ";" + th + "}";
	}

	public SetSprSource(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags);
		Image	image = Materials.getImageFromSpriteName(params[0]);
		if (image == null)
			throw new RuntimeException(params[1] + " - Invalid sprite source name");
		spriteSource = image;
		int n = 0, t = params.length;
		try {
			int sx = Integer.parseInt(params[n = 1]),
					sy = Integer.parseInt(params[n = 2]),
					sw = Integer.parseInt(params[n = 3]),
					sh = Integer.parseInt(params[n = 4]),
					tx = t < 8 ? 0 : Integer.parseInt(params[n = 7]),
					ty = t < 9 ? 0 : Integer.parseInt(params[n = 8]),
					tw = t < 10 ? sw : Integer.parseInt(params[n = 9]),
					th = t < 11 ? sh : Integer.parseInt(params[n = 10]);
			spriteIndex = t < 6 ? 0 : Integer.parseInt(params[n = 5]);
			spritesPerLine = t < 7 ? 0 : Integer.parseInt(params[n = 6]);
			originSprSizePos = new Rectangle(sx, sy, sw, sh);
			outputSprSizePos = new Rectangle(tx, ty, tw, th);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[n] + " - Invalid parameter"); }
	}

	@Override
	public SetSprSource getNewInstanceOfThis()
		{ return new SetSprSource(spriteSource, originSprSizePos, outputSprSizePos, spriteIndex, spritesPerLine); }
	
	@Override
	public void process(Sprite sprite) {
		sprite.setSpriteSource(getSpriteSource());
		sprite.setOriginSpritePos(getOriginSprSizePos());
		sprite.setOutputSpritePos(getOutputSprSizePos());
		sprite.setSpriteIndex(getSpriteIndex());
		sprite.setSpritesPerLine(getSpritesPerLine());
	}

}