package frameset;

import java.awt.Dimension;
import java.awt.Rectangle;

import application.Main;
import drawimage_stuffs.DrawImageEffects;
import entities.Materials;
import enums.ImageAlignment;
import enums.ImageFlip;
import gui.util.ImageUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Sprite {

	private FrameSet mainFrameSet;
	private Image spriteSource;
	private Rectangle originSpriteSizePos;
	private Rectangle outputSpriteSizePos;
	private DrawImageEffects spriteEffects;
	private int spritesPerLine;
	private int spriteIndex;
	private double alpha;
	private ImageFlip flip;
	private ImageAlignment alignment;
	private float rotation;
	
	public Sprite(Sprite sprite) {
		super();
		mainFrameSet = sprite.mainFrameSet;
		spriteSource = sprite.spriteSource;
		originSpriteSizePos = new Rectangle(sprite.originSpriteSizePos);
		outputSpriteSizePos = new Rectangle(sprite.outputSpriteSizePos);
		alpha = sprite.alpha;
		flip = sprite.flip;
		rotation = sprite.rotation;
		spriteIndex = sprite.spriteIndex;
		alignment = sprite.alignment;
		spritesPerLine = sprite.spritesPerLine;
		spriteEffects = new DrawImageEffects(sprite.spriteEffects);
	}
	
	public Sprite(FrameSet mainFrameSet, Image spriteSource, Rectangle originSpriteSizePos, Rectangle outputSpriteSizePos, int spriteIndex, int spritesPerLine) {
		super();
		this.spriteSource = spriteSource;
		this.spriteIndex = spriteIndex;
		this.spritesPerLine = spritesPerLine;
		this.mainFrameSet = mainFrameSet;
		this.originSpriteSizePos = new Rectangle(originSpriteSizePos);
		this.outputSpriteSizePos = new Rectangle(outputSpriteSizePos);
		rotation = 0;
		alpha = 1;
		flip =ImageFlip.NONE;
		alignment = ImageAlignment.CENTER;
		spriteEffects = new DrawImageEffects();
	}

	public Sprite(FrameSet mainFrameSet, Image spriteSource, Rectangle originSpriteSizePos, int spriteIndex, int spritesPerLine)
		{ this(mainFrameSet, spriteSource, originSpriteSizePos, new Rectangle(0, 0, (int)originSpriteSizePos.getWidth(), (int)originSpriteSizePos.getHeight()), spriteIndex, spritesPerLine); }
	
	public Sprite(FrameSet mainFrameSet, Image spriteSource, Rectangle originSpriteSizePos, Rectangle outputSpriteSizePos, int spritesPerLine)
		{ this(mainFrameSet, spriteSource, originSpriteSizePos, outputSpriteSizePos, spritesPerLine, 0); }

	public Sprite(FrameSet mainFrameSet, Image spriteSource, Rectangle originSpriteSizePos, Rectangle outputSpriteSizePos)
		{ this(mainFrameSet, spriteSource, originSpriteSizePos, outputSpriteSizePos, 0, 0); }

	public Sprite(FrameSet mainFrameSet, Image spriteSource, Rectangle originSpriteSizePos, int spritesPerLine)
		{ this(mainFrameSet, spriteSource, originSpriteSizePos, new Rectangle(0, 0, (int)originSpriteSizePos.getWidth(), (int)originSpriteSizePos.getHeight()), spritesPerLine, 0); }
	
	public Sprite(FrameSet mainFrameSet, Image spriteSource, Rectangle originSpriteSizePos)
		{ this(mainFrameSet, spriteSource, originSpriteSizePos, new Rectangle(0, 0, (int)originSpriteSizePos.getWidth(), (int)originSpriteSizePos.getHeight()), 0, 0); }

	public void setSpriteSource(Image image)
		{ spriteSource = image; }

	public Image getSpriteSource()
		{ return spriteSource; }
	
	public ImageAlignment getAlignment()
		{ return alignment; }
	
	public void setAlignment(ImageAlignment alignment)
		{ this.alignment = alignment; }
	
	public double getX()
		{ return outputSpriteSizePos.getX(); }
	
	public double getY()
		{ return outputSpriteSizePos.getY(); }

	public void setX(int x)
		{ outputSpriteSizePos.setLocation(x, (int)getY()); }
	
	public void setY(int y)
		{ outputSpriteSizePos.setLocation((int)getX(), y); }

	public void incX(int incX)
		{ outputSpriteSizePos.setLocation((int)getX() + incX, (int)getY()); }
	
	public void incY(int incY)
		{ outputSpriteSizePos.setLocation((int)getX(), (int)getY() + incY); }

	public double getAbsoluteX() {
		if (mainFrameSet != null)
			return mainFrameSet.getX() + getX();
		return getX();
	}

	public double getAbsoluteY() {
		if (mainFrameSet != null)
			return mainFrameSet.getY() + getY();
		return getY();
	}

	public void setAbsoluteX(int x) {
		if (mainFrameSet != null)
			setX(x - (int)mainFrameSet.getX());
		else
			setX(x);
	}
	
	public void setAbsoluteY(int y) {
		if (mainFrameSet != null)
			setY(y - (int)mainFrameSet.getY());
		else
			setY(y);
	}
	
	public double getAlpha()
		{ return alpha; }

	public void setAlpha(double alpha)
		{ this.alpha = alpha; }

	public void incAlpha(double value)
		{ alpha += value; }

	public ImageFlip getFlip()
		{ return flip; }

	public void setFlip(ImageFlip flip)
		{ this.flip = flip; }

	public float getRotation()
		{ return rotation; }

	public void setRotation(float rotation)
		{ this.rotation = rotation; }	
	
	public void incRotation(float value)
		{ rotation += value; }	

	public int getSpriteIndex()
		{ return spriteIndex; }

	public void setSpriteIndex(int spriteIndex)
		{ this.spriteIndex = spriteIndex; }

	public void incSpriteIndex(int value) {
		if (spriteIndex + value >= 0)
			spriteIndex += value;
	}
	
	public void decSpriteIndex() {
		if (spriteIndex > 0)
			spriteIndex--;
	}

	public void incSpriteIndex()
		{ spriteIndex++; }

	public Rectangle getOriginSpritePos()
		{ return originSpriteSizePos; }
	
	public void setOriginSpritePos(int x, int y, int w, int h)
		{ originSpriteSizePos.setBounds(x, y, w, h); }
	
	public void setOutputSpritePos(Rectangle outputSpriteSizePos)
		{ this.outputSpriteSizePos.setBounds((int)outputSpriteSizePos.getX(), (int)outputSpriteSizePos.getY(), (int)outputSpriteSizePos.getWidth(), (int)outputSpriteSizePos.getHeight()); }
	
	public int getOriginSpriteX()
		{ return (int)originSpriteSizePos.getX(); }
	
	public void setOriginSpriteX(int x)
		{ originSpriteSizePos.setLocation(x, getOriginSpriteY()); }
	
	public void incOriginSpriteX(int value)
		{ setOriginSpriteX(getOriginSpriteX() + value); }
	
	public int getOriginSpriteY()
		{ return (int)originSpriteSizePos.getY(); }
	
	public void setOriginSpriteY(int y)
		{ originSpriteSizePos.setLocation(getOriginSpriteX(), y); }
	
	public void incOriginSpriteY(int value)
		{ setOriginSpriteY(getOriginSpriteY() + value); }
	
	public double getOriginSpriteWidth()
		{ return originSpriteSizePos.getWidth(); }
	
	public void setOriginSpriteWidth(int x)
		{ originSpriteSizePos.setSize(x, (int)originSpriteSizePos.getHeight()); }
	
	public void incOriginSpriteWidth(int value)
		{ setOriginSpriteWidth((int)originSpriteSizePos.getWidth() + value); }
	
	public double getOriginSpriteHeight()
		{ return originSpriteSizePos.getHeight(); }
	
	public void setOriginSpriteHeight(int h)
		{ originSpriteSizePos.setSize((int)originSpriteSizePos.getWidth(), h); }
	
	public void incOriginSpriteHeight(int value)
		{ setOriginSpriteHeight((int)originSpriteSizePos.getHeight() + value); }
	
	public Dimension getOutputSize()
		{ return outputSpriteSizePos.getSize(); }
	
	public void setOriginSpritePos(Rectangle originSpriteSizePos)
		{ this.originSpriteSizePos.setBounds((int)originSpriteSizePos.getX(), (int)originSpriteSizePos.getY(), (int)originSpriteSizePos.getWidth(), (int)originSpriteSizePos.getHeight()); }

	public int getOutputWidth()
		{ return (int)outputSpriteSizePos.getWidth(); }
	
	public int getOutputHeight()
		{ return (int)outputSpriteSizePos.getHeight(); }

	public void setOutputSize(int w, int h)
		{ outputSpriteSizePos.setSize(w, h); }
	
	public void setOutputWidth(int w)
		{ setOutputSize(w, (int)outputSpriteSizePos.getHeight()); }
	
	public void setOutputHeight(int h)
		{ setOutputSize((int)outputSpriteSizePos.getWidth(), h); }

	public void incOutputSize(int wInc, int hInc)
		{ setOutputSize((int)outputSpriteSizePos.getWidth() + wInc, (int)outputSpriteSizePos.getHeight() + hInc); }
	
	public void incOutputWidth(int wInc)
		{ setOutputSize((int)outputSpriteSizePos.getWidth() + wInc, (int)outputSpriteSizePos.getHeight()); }
	
	public void incOutputHeight(int hInc)
		{ setOutputSize((int)outputSpriteSizePos.getWidth(), (int)outputSpriteSizePos.getHeight() + hInc); }

	public int getSpritesPerLine()
		{ return spritesPerLine; }
	
	public void setSpritesPerLine(int value)
		{ spritesPerLine = value; }

	public void incSpritesPerLine(int value)
		{ spritesPerLine += value; }

	public FrameSet getMainFrameSet()
		{ return mainFrameSet; }

	public void setMainFrameSet(FrameSet frameSet)
		{ mainFrameSet = frameSet; }
	
	public DrawImageEffects getEffects()
		{ return spriteEffects; }

	public int getMaxOutputSpriteY() {
		int[] spritePos = getOutputDrawCoords();
		return spritePos[1] + getOutputHeight();
	}

	public int[] getCurrentSpriteOriginCoords() {
		int w = (int)getOriginSpriteWidth(),
				h = (int)getOriginSpriteHeight(),
				x = (int)getOriginSpriteX() + w * (int)((getSpritesPerLine() == 0 ? spriteIndex : (spriteIndex % getSpritesPerLine()))),
				y = (int)getOriginSpriteY() + h * (int)((getSpritesPerLine() == 0 ? 0 : (spriteIndex / getSpritesPerLine())));
		return new int[] {x, y};
	}
	
	public int[] getOutputDrawCoords() {
		int x = (int)getAbsoluteX(),
				y = (int)getAbsoluteY(),
				w = (int)getOutputWidth(),
				h = (int)getOutputHeight();
		
		switch (alignment) {
			case TOP:
				x -= w / 2;
				y -= Main.tileSize / 2;
				break;
			case BOTTOM:
				x -= w / 2;
				y += Main.tileSize / 2 - h;
				break;
			case LEFT:
				x -= Main.tileSize / 2;
				y -= h / 2;
				break;
			case RIGHT:
				x += Main.tileSize / 2 - w;
				y -= h / 2;
				break;
			case LEFT_TOP:
				x -= Main.tileSize / 2;
				y -= Main.tileSize / 2;
				break;
			case LEFT_BOTTOM:
				x -= Main.tileSize / 2;
				y += Main.tileSize / 2 - h;
				break;
			case RIGHT_TOP:
				x += Main.tileSize / 2 - w;
				y -= Main.tileSize / 2;
				break;
			case RIGHT_BOTTOM:
				x += Main.tileSize / 2 - w;
				y += Main.tileSize / 2 - h;
				break;
			case CENTER:
				x -= w / 2;
				y -= h / 2;
				break;
			default:
				break;
		}
		return new int[] {x, y};
	}
	
	public void draw(GraphicsContext targetGc) {
		if (spriteIndex != -1) {
			int[] in = getCurrentSpriteOriginCoords(), out = getOutputDrawCoords();
			ImageUtils.drawImage(targetGc, Materials.mainSprites, in[0], in[1], (int)getOriginSpriteWidth(), (int)getOriginSpriteHeight(),
				out[0], out[1], getOutputWidth(), getOutputHeight(), flip, rotation, alpha, spriteEffects);
		}
	}

}
