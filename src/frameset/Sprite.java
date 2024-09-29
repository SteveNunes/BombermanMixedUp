package frameset;

import java.awt.Dimension;
import java.awt.Rectangle;

import application.Main;
import drawimage_stuffs.DrawImageEffects;
import enums.ImageAlignment;
import enums.ImageFlip;
import enums.SpriteLayerType;
import gui.util.ImageUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import objmoveutils.EliticMove;
import objmoveutils.GotoMove;
import objmoveutils.JumpMove;
import objmoveutils.Position;
import objmoveutils.RectangleMove;
import tools.Materials;
import tools.Tools;

public class Sprite {

	private FrameSet mainFrameSet;
	private Image spriteSource;
	private Rectangle originSpriteSizePos;
	private Rectangle outputSpriteSizePos;
	private DrawImageEffects spriteEffects;
	private Position outputSpritePos;
	private EliticMove eliticMove;
	private RectangleMove rectangleMove;
	private JumpMove jumpMove;
	private GotoMove gotoMove;
	private ImageFlip flip;
	private ImageAlignment alignment;
	private int spritesPerLine;
	private Integer spriteIndex;
	private double alpha;
	private int rotation;
	private boolean visibleSprite;
	private SpriteLayerType layerType;
	
	public Sprite(Sprite sprite)
		{ this(sprite, sprite.getMainFrameSet()); }
	
	public Sprite(Sprite sprite, FrameSet mainFrameSet) {
		super();
		this.mainFrameSet = mainFrameSet;
		originSpriteSizePos = new Rectangle(sprite.originSpriteSizePos);
		outputSpriteSizePos = new Rectangle(sprite.outputSpriteSizePos);
		spriteEffects = new DrawImageEffects(sprite.spriteEffects);
		outputSpritePos = new Position();
		spriteSource = sprite.spriteSource;
		alpha = sprite.alpha;
		flip = sprite.flip;
		rotation = sprite.rotation;
		spriteIndex = sprite.spriteIndex;
		alignment = sprite.alignment;
		spritesPerLine = sprite.spritesPerLine;
		eliticMove = sprite.eliticMove == null ? null : new EliticMove(sprite.eliticMove);
		rectangleMove = sprite.rectangleMove == null ? null : new RectangleMove(sprite.rectangleMove);
		jumpMove = sprite.jumpMove == null ? null : new JumpMove(sprite.jumpMove);
		gotoMove = sprite.gotoMove == null ? null : new GotoMove(sprite.gotoMove);
		layerType = sprite.layerType;
		visibleSprite = sprite.visibleSprite;
	}
	
	public Sprite(FrameSet mainFrameSet, Image spriteSource, Rectangle originSpriteSizePos, Rectangle outputSpriteSizePos, int spriteIndex, int spritesPerLine) {
		super();
		this.spriteSource = spriteSource;
		this.spriteIndex = spriteIndex;
		this.spritesPerLine = spritesPerLine;
		this.mainFrameSet = mainFrameSet;
		this.originSpriteSizePos = new Rectangle(originSpriteSizePos);
		this.outputSpriteSizePos = new Rectangle(outputSpriteSizePos);
		outputSpritePos = new Position();
		spriteEffects = new DrawImageEffects();
		flip =ImageFlip.NONE;
		alignment = ImageAlignment.NONE;
		rotation = 0;
		alpha = 1;
		eliticMove = null;
		rectangleMove = null;
		jumpMove = null;
		gotoMove = null;
		visibleSprite = true;
		layerType = SpriteLayerType.GROUND;
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
	
	public SpriteLayerType getLayerType()
		{ return layerType; }

	public void setLayerType(SpriteLayerType layerType)
		{ this.layerType = layerType; } 
	
	public boolean isVisibleSprite()
		{ return visibleSprite; }
	
	public void setVisibleSprite(boolean state)
		{ visibleSprite = state; }

	public double getX()
		{ return outputSpriteSizePos.getX(); }
	
	public double getY()
		{ return outputSpriteSizePos.getY(); }

	public void setX(double x)
		{ outputSpriteSizePos.setFrame(x, getY(), getOutputWidth(), getOutputHeight()); }
	
	public void setY(double y)
		{ outputSpriteSizePos.setFrame(getX(), y, getOutputWidth(), getOutputHeight()); }

	public void incX(double incX)
		{ outputSpriteSizePos.setFrame(getX() + incX, getY(), getOutputWidth(), getOutputHeight()); }
	
	public void incY(double incY)
		{ outputSpriteSizePos.setFrame(getX(), getY() + incY, getOutputWidth(), getOutputHeight()); }

	public double getAbsoluteX() {
		if (mainFrameSet != null)
			return mainFrameSet.getAbsoluteX() + getX();
		return getX();
	}

	public double getAbsoluteY() {
		if (mainFrameSet != null)
			return mainFrameSet.getAbsoluteY() + getY();
		return getY();
	}

	public void setAbsoluteX(int x) {
		if (mainFrameSet != null)
			setX(x - (int)mainFrameSet.getAbsoluteX());
		else
			setX(x);
	}
	
	public void setAbsoluteY(int y) {
		if (mainFrameSet != null)
			setY(y - (int)mainFrameSet.getAbsoluteY());
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

	public void setRotation(int rotation)
		{ this.rotation = rotation; }	
	
	public void incRotation(float value)
		{ rotation += value; }	

	public int getSpriteIndex()
		{ return spriteIndex; }

	public void setSpriteIndex(int spriteIndex)
		{ this.spriteIndex = spriteIndex; }

	public void incSpriteIndex(int value)
		{ spriteIndex += value; }
	
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

	public EliticMove getEliticMove()
		{ return eliticMove; }

	public void setEliticMove(EliticMove eliticMove)
		{ this.eliticMove = eliticMove; }

	public RectangleMove getRectangleMove()
		{ return rectangleMove; }

	public void setRectangleMove(RectangleMove rectangleMove)
	 { this.rectangleMove = rectangleMove; }

	public JumpMove getJumpMove()
		{ return jumpMove; }
	
	public void setJumpMove(JumpMove jumpMove)
		{ this.jumpMove = jumpMove; }

	public GotoMove getGotoMove()
		{ return gotoMove; }
	
	public void setGotoMove(GotoMove gotoMove)
		{ this.gotoMove = gotoMove; }

	public int getMaxOutputSpriteY()
		{ return (int)getOutputDrawCoords().getY() + getOutputHeight(); }

	public int[] getCurrentSpriteOriginCoords() {
		if (spriteIndex == null)
			return new int[] {0, 0};
		int w = (int)getOriginSpriteWidth(),
				h = (int)getOriginSpriteHeight(),
				x = (int)getOriginSpriteX() + w * (int)((getSpritesPerLine() == 0 ? spriteIndex : (spriteIndex % getSpritesPerLine()))),
				y = (int)getOriginSpriteY() + h * (int)((getSpritesPerLine() == 0 ? 0 : (spriteIndex / getSpritesPerLine())));
		return new int[] {x, y};
	}
	
	public Position getOutputDrawCoords()
		{ return outputSpritePos; }
	
	public void updateOutputDrawCoords() {
		int x = (int)getAbsoluteX(),
				y = (int)getAbsoluteY(),
				w = (int)getOutputWidth(),
				h = (int)getOutputHeight();
		if (eliticMove != null) {
			x += eliticMove.getPosition().getX();
			y += eliticMove.getPosition().getY();
			eliticMove.move();
		}
		switch (alignment) {
			case TOP:
				x += Main.TILE_SIZE / 2 - w / 2;
				break;
			case BOTTOM:
				x += Main.TILE_SIZE / 2 - w / 2;
				y += Main.TILE_SIZE - h;
				break;
			case LEFT:
				y += Main.TILE_SIZE / 2 - h / 2;
				break;
			case RIGHT:
				x += Main.TILE_SIZE - w;
				y += Main.TILE_SIZE / 2 - h / 2;
				break;
			case LEFT_TOP:
				break;
			case LEFT_BOTTOM:
				y += Main.TILE_SIZE - h;
				break;
			case RIGHT_TOP:
				x += Main.TILE_SIZE - w;
				break;
			case RIGHT_BOTTOM:
				x += Main.TILE_SIZE - w;
				y += Main.TILE_SIZE - h;
				break;
			case CENTER:
				x += Main.TILE_SIZE / 2 - w / 2;
				y += Main.TILE_SIZE / 2 - h / 2;
				break;
			default:
				break;
		}
		outputSpritePos.setPosition(x, y);
	}
	
	public void draw()
		{ draw(null); }
	
	public void draw(GraphicsContext gc) {
		if (visibleSprite) {
			updateOutputDrawCoords();
			int[] in = getCurrentSpriteOriginCoords();
			int sx = in[0], sy = in[1], tx = (int)getOutputDrawCoords().getX(), ty = (int)getOutputDrawCoords().getY();
			ImageUtils.drawImage(gc != null ? gc : Tools.getGcMap().get(layerType), spriteIndex == null ? Materials.blankImage : spriteSource, sx, sy, (int)getOriginSpriteWidth(), (int)getOriginSpriteHeight(),
				tx, ty, getOutputWidth(), getOutputHeight(), flip, rotation, alpha, spriteEffects);
		}
	}

}
