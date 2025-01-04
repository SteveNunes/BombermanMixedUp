package frameset;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashSet;

import application.Main;
import drawimage_stuffs.DrawImageEffects;
import entities.BomberMan;
import entities.Entity;
import entities.Ride;
import entityTools.ShakeEntity;
import enums.Curse;
import enums.DamageType;
import enums.Direction;
import enums.ForceDirection;
import enums.ImageAlignment;
import enums.ImageFlip;
import enums.SpriteLayerType;
import gui.util.ImageUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import maps.MapSet;
import objmoveutils.EliticMove;
import objmoveutils.JumpMove;
import objmoveutils.Position;
import objmoveutils.RectangleMove;
import objmoveutils.TileCoord;
import screen_pos_effects.WavingImage;
import tools.Draw;
import tools.DrawParams;
import tools.Materials;
import tools.Sound;
import util.Misc;
import util.MyMath;

public class Sprite extends Position {

	private FrameSet sourceFrameSet;
	private String spriteSourceName;
	private Rectangle originSpriteSizePos;
	public Rectangle outputSpriteSizePos;
	private DrawImageEffects spriteEffects;
	private Position absoluteOutputSpritePos;
	private Position spriteScroll;
	private DamageBox damageBox;
	private EliticMove eliticMove;
	private RectangleMove rectangleMove;
	private JumpMove jumpMove;
	private ShakeEntity shake;
	private ImageFlip flip;
	private ImageAlignment alignment;
	private int spritesPerLine;
	private Integer spriteIndex;
	private double alpha;
	private int rotation;
	public int frontValue;
	public int extraFrontValue;
	private SpriteLayerType layerType;
	private WavingImage wavingImage;
	private boolean isVisible;
	private int ghostingDistance;
	private Double ghostingOpacityDec;
	private int[] multiFrameIndexByDirection;

	public Sprite(Sprite sprite) {
		this(sprite, sprite.getSourceFrameSet());
	}

	public Sprite(Sprite sprite, FrameSet mainFrameSet) {
		super(sprite);
		sourceFrameSet = mainFrameSet;
		originSpriteSizePos = new Rectangle(sprite.originSpriteSizePos);
		outputSpriteSizePos = new Rectangle(sprite.outputSpriteSizePos);
		spriteEffects = new DrawImageEffects(sprite.spriteEffects);
		absoluteOutputSpritePos = new Position();
		spriteSourceName = sprite.spriteSourceName;
		ghostingDistance = sprite.ghostingDistance;
		ghostingOpacityDec = sprite.ghostingOpacityDec;
		multiFrameIndexByDirection = sprite.multiFrameIndexByDirection;
		alpha = sprite.alpha;
		flip = sprite.flip;
		rotation = sprite.rotation;
		spriteIndex = sprite.spriteIndex;
		alignment = sprite.alignment;
		spritesPerLine = sprite.spritesPerLine;
		eliticMove = sprite.eliticMove == null ? null : new EliticMove(sprite.eliticMove);
		rectangleMove = sprite.rectangleMove == null ? null : new RectangleMove(sprite.rectangleMove);
		jumpMove = sprite.jumpMove == null ? null : new JumpMove(sprite.jumpMove);
		shake = sprite.shake == null ? null : new ShakeEntity(sprite.shake);
		layerType = sprite.layerType;
		wavingImage = sprite.wavingImage == null ? null : new WavingImage(sprite.wavingImage);
		spriteScroll = sprite.spriteScroll == null ? null : new Position(sprite.spriteScroll);
		damageBox = sprite.damageBox == null ? null : new DamageBox(sprite.damageBox.damageType, sprite.damageBox.variant, sprite.damageBox.triggerTargetFrameSet, sprite.damageBox.forceTargetDirection, new Rectangle(sprite.damageBox.damageBoxRect), sprite.damageBox.soundWhenHits);
		frontValue = sprite.frontValue;
		isVisible = sprite.isVisible;
		extraFrontValue = sprite.extraFrontValue;
		updateOutputDrawCoords();
	}

	public Sprite(FrameSet mainFrameSet, String spriteSourceName, Rectangle originSpriteSizePos, Rectangle outputSpriteSizePos, int spriteIndex, int spritesPerLine) {
		super(originSpriteSizePos.getX(), originSpriteSizePos.getY());
		this.spriteSourceName = spriteSourceName;
		this.spriteIndex = spriteIndex;
		this.spritesPerLine = spritesPerLine;
		this.sourceFrameSet = mainFrameSet;
		this.originSpriteSizePos = new Rectangle(originSpriteSizePos);
		this.outputSpriteSizePos = new Rectangle(outputSpriteSizePos);
		spriteScroll = null;
		wavingImage = null;
		shake = null;
		absoluteOutputSpritePos = new Position();
		spriteEffects = new DrawImageEffects();
		flip = ImageFlip.NONE;
		alignment = ImageAlignment.NONE;
		rotation = 0;
		alpha = 1;
		eliticMove = null;
		rectangleMove = null;
		jumpMove = null;
		layerType = SpriteLayerType.SPRITE;
		frontValue = 0;
		extraFrontValue = 0;
		isVisible = true;
		ghostingDistance = 0;
		ghostingOpacityDec = null;
		multiFrameIndexByDirection = null;
		damageBox = null;
		updateOutputDrawCoords();
	}

	public Sprite(FrameSet mainFrameSet, String spriteSourceName, Rectangle originSpriteSizePos, int spriteIndex, int spritesPerLine) {
		this(mainFrameSet, spriteSourceName, originSpriteSizePos, new Rectangle(0, 0, (int) originSpriteSizePos.getWidth(), (int) originSpriteSizePos.getHeight()), spriteIndex, spritesPerLine);
	}

	public Sprite(FrameSet mainFrameSet, String spriteSourceName, Rectangle originSpriteSizePos, Rectangle outputSpriteSizePos, int spritesPerLine) {
		this(mainFrameSet, spriteSourceName, originSpriteSizePos, outputSpriteSizePos, spritesPerLine, 0);
	}

	public Sprite(FrameSet mainFrameSet, String spriteSourceName, Rectangle originSpriteSizePos, Rectangle outputSpriteSizePos) {
		this(mainFrameSet, spriteSourceName, originSpriteSizePos, outputSpriteSizePos, 0, 0);
	}

	public Sprite(FrameSet mainFrameSet, String spriteSourceName, Rectangle originSpriteSizePos, int spritesPerLine) {
		this(mainFrameSet, spriteSourceName, originSpriteSizePos, new Rectangle(0, 0, (int) originSpriteSizePos.getWidth(), (int) originSpriteSizePos.getHeight()), spritesPerLine, 0);
	}

	public Sprite(FrameSet mainFrameSet, String spriteSourceName, Rectangle originSpriteSizePos) {
		this(mainFrameSet, spriteSourceName, originSpriteSizePos, new Rectangle(0, 0, (int) originSpriteSizePos.getWidth(), (int) originSpriteSizePos.getHeight()), 0, 0);
	}

	public void setShake(Double incStrength, Double finalStrength) {
		shake = new ShakeEntity(incStrength, incStrength, finalStrength, finalStrength);
	}

	public void setShake(Double startStrength, Double incStrength, Double finalStrength) {
		shake = new ShakeEntity(startStrength, startStrength, incStrength, incStrength, finalStrength, finalStrength);
	}

	public void setShake(Double incStrengthX, Double incStrengthY, Double finalStrengthX, Double finalStrengthY) {
		shake = new ShakeEntity(incStrengthX > 0 ? 0 : finalStrengthX, incStrengthY > 0 ? 0 : finalStrengthY, incStrengthX, incStrengthY, finalStrengthX, finalStrengthY);
	}

	public void setShake(Double startStrengthX, Double startStrengthY, Double incStrengthX, Double incStrengthY, Double finalStrengthX, Double finalStrengthY) {
		shake = new ShakeEntity(startStrengthX, startStrengthY, incStrengthX, incStrengthY, finalStrengthX, finalStrengthY);
	}

	public void stopShake() {
		shake.stop();
	}

	public ShakeEntity getShake() {
		return shake;
	}

	public void unsetShake() {
		shake = null;
	}
	
	public DrawImageEffects getSpriteEffects() {
		return getSourceEntity().getImageEffect() != null ? getSourceEntity().getImageEffect() : spriteEffects;
	}

	public void setGhosting(int ghostingDistance, double ghostingOpacityDec) {
		this.ghostingDistance = ghostingDistance;
		this.ghostingOpacityDec = ghostingOpacityDec;
	}

	public void unsetGhosting() {
		ghostingDistance = 0;
		ghostingOpacityDec = null;
	}

	public void setMultiFrameIndexByDirection(int up, int right, int down, int left) // Definir em cada indice o incremento de Indice de acordo com a direcao. Se o
	                                                                                 // valor for negativo, aplica um flip horizontal na imagem.
	{
		multiFrameIndexByDirection = new int[] { up, right, down, left };
	}

	public void unsetMultiFrameIndexByDirection() {
		multiFrameIndexByDirection = null;
	}

	public void setVisible(boolean state) {
		isVisible = state;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public WavingImage getWavingImage() {
		return wavingImage;
	}

	public void setWavingImage() {
		setWavingImage(1, null);
	}

	public void setWavingImage(int speed) {
		setWavingImage(speed, null);
	}

	public void setWavingImage(int[] wavingPattern) {
		setWavingImage(1, wavingPattern);
	}

	public void setWavingImage(int speed, int[] wavingPattern) {
		wavingImage = new WavingImage(speed, wavingPattern);
	}

	public void setSpriteSourceName(String spriteSourceName) {
		this.spriteSourceName = spriteSourceName;
	}

	public WritableImage getSpriteSource() {
		if (wavingImage != null) {
			wavingImage.setBounds((int) originSpriteSizePos.getX(), (int) originSpriteSizePos.getY(), (int) originSpriteSizePos.getWidth(), (int) originSpriteSizePos.getHeight());
			return wavingImage.apply(Materials.getImageFromSpriteName(spriteSourceName));
		}
		return Materials.getImageFromSpriteName(spriteSourceName);
	}

	public String getSpriteSourceName() {
		return spriteSourceName;
	}

	public ImageAlignment getAlignment() {
		return alignment;
	}

	public void setAlignment(ImageAlignment alignment) {
		this.alignment = alignment;
	}

	public SpriteLayerType getLayerType() {
		return layerType;
	}

	public void setLayerType(SpriteLayerType layerType) {
		this.layerType = layerType;
	}

	@Override
	public Position setX(double x) {
		outputSpriteSizePos.setFrame(x, getY(), getOutputWidth(), getOutputHeight());
		super.setX(x);
		updateOutputDrawCoords();
		return this;
	}

	@Override
	public Position setY(double y) {
		outputSpriteSizePos.setFrame(getX(), y, getOutputWidth(), getOutputHeight());
		super.setY(y);
		updateOutputDrawCoords();
		return this;
	}

	@Override
	public Position setPosition(double x, double y) {
		setX(x);
		setY(y);
		return this;
	}

	@Override
	public Position setPosition(Position position) {
		setX(position.getX());
		setY(position.getY());
		return this;
	}

	@Override
	public Position incX(double incX) {
		setX(getX() + incX);
		return this;
	}

	@Override
	public Position incY(double incY) {
		setY(getY() + incY);
		return this;
	}

	public double getAbsoluteX() {
		if (sourceFrameSet != null)
			return sourceFrameSet.getAbsoluteX() + getX();
		return getX();
	}

	public double getAbsoluteY() {
		if (sourceFrameSet != null)
			return sourceFrameSet.getAbsoluteY() + getY();
		return getY();
	}

	public Position getAbsolutePosition() {
		return new Position(getAbsoluteX(), getAbsoluteY());
	}

	public void setAbsoluteX(double x) {
		if (sourceFrameSet != null)
			setX(x - sourceFrameSet.getAbsoluteX());
		else
			setX(x);
	}

	public void setAbsoluteY(double y) {
		if (sourceFrameSet != null)
			setY(y - sourceFrameSet.getAbsoluteY());
		else
			setY(y);
	}

	public void setAbsolutePosition(int x, int y) {
		setAbsoluteX(x);
		setAbsoluteY(y);
	}

	public void setAbsolutePosition(Position position) {
		setAbsoluteX(position.getX());
		setAbsoluteY(position.getY());
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public void incAlpha(double value) {
		alpha += value;
	}

	public ImageFlip getFlip() {
		return flip;
	}

	public void setFlip(ImageFlip flip) {
		this.flip = flip;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public void incRotation(float value) {
		rotation += value;
	}

	public int getSpriteIndex() {
		return spriteIndex;
	}

	public void setSpriteIndex(int spriteIndex) {
		this.spriteIndex = spriteIndex;
	}

	public void incSpriteIndex() {
		incSpriteIndex(1);
	}

	public void incSpriteIndex(int value) {
		spriteIndex += value;
	}

	public void decSpriteIndex() {
		decSpriteIndex(1);
	}

	public void decSpriteIndex(int value) {
		if (spriteIndex - value >= 0)
			spriteIndex -= value;
	}

	public Rectangle getOriginSpritePos() {
		return originSpriteSizePos;
	}

	public void setOriginSpritePos(int x, int y, int w, int h) {
		originSpriteSizePos.setBounds(x, y, w, h);
	}

	public void setOutputSpritePos(Rectangle outputSpriteSizePos) {
		this.outputSpriteSizePos.setBounds(outputSpriteSizePos);
		setPosition(outputSpriteSizePos.getX(), outputSpriteSizePos.getY());
		updateOutputDrawCoords();
	}

	public int getOriginSpriteX() {
		return (int) originSpriteSizePos.getX();
	}

	public void setOriginSpriteX(int x) {
		originSpriteSizePos.setLocation(x, getOriginSpriteY());
	}

	public void incOriginSpriteX(int value) {
		setOriginSpriteX(getOriginSpriteX() + value);
	}

	public int getOriginSpriteY() {
		return (int) originSpriteSizePos.getY();
	}

	public void setOriginSpriteY(int y) {
		originSpriteSizePos.setLocation(getOriginSpriteX(), y);
	}

	public void incOriginSpriteY(int value) {
		setOriginSpriteY(getOriginSpriteY() + value);
	}

	public double getOriginSpriteWidth() {
		return originSpriteSizePos.getWidth();
	}

	public void setOriginSpriteWidth(int x) {
		originSpriteSizePos.setSize(x, (int) originSpriteSizePos.getHeight());
	}

	public void incOriginSpriteWidth(int value) {
		setOriginSpriteWidth((int) originSpriteSizePos.getWidth() + value);
	}

	public double getOriginSpriteHeight() {
		return originSpriteSizePos.getHeight();
	}

	public void setOriginSpriteHeight(int h) {
		originSpriteSizePos.setSize((int) originSpriteSizePos.getWidth(), h);
	}

	public void incOriginSpriteHeight(int value) {
		setOriginSpriteHeight((int) originSpriteSizePos.getHeight() + value);
	}

	public Dimension getOutputSize() {
		return outputSpriteSizePos.getSize();
	}

	public void setOriginSpritePos(Rectangle originSpriteSizePos) {
		this.originSpriteSizePos.setBounds(originSpriteSizePos);
	}

	public int getOutputWidth() {
		return (int) outputSpriteSizePos.getWidth();
	}

	public int getOutputHeight() {
		return (int) outputSpriteSizePos.getHeight();
	}

	public void setOutputSize(int w, int h) {
		outputSpriteSizePos.setSize(w, h);
		updateOutputDrawCoords();
	}

	public void setOutputWidth(int w) {
		setOutputSize(w, (int) outputSpriteSizePos.getHeight());
	}

	public void setOutputHeight(int h) {
		setOutputSize((int) outputSpriteSizePos.getWidth(), h);
	}

	public void incOutputSize(int wInc, int hInc) {
		setOutputSize((int) outputSpriteSizePos.getWidth() + wInc, (int) outputSpriteSizePos.getHeight() + hInc);
	}

	public void incOutputWidth(int wInc) {
		setOutputSize((int) outputSpriteSizePos.getWidth() + wInc, (int) outputSpriteSizePos.getHeight());
	}

	public void incOutputHeight(int hInc) {
		setOutputSize((int) outputSpriteSizePos.getWidth(), (int) outputSpriteSizePos.getHeight() + hInc);
	}

	public int getSpritesPerLine() {
		return spritesPerLine;
	}

	public void setSpritesPerLine(int value) {
		spritesPerLine = value;
	}

	public void incSpritesPerLine(int value) {
		spritesPerLine += value;
	}

	public Entity getSourceEntity() {
		return sourceFrameSet.getSourceEntity();
	}

	public FrameSet getSourceFrameSet() {
		return sourceFrameSet;
	}

	public void setSourceFrameSet(FrameSet frameSet) {
		sourceFrameSet = frameSet;
	}

	public DrawImageEffects getEffects() {
		return spriteEffects;
	}

	public EliticMove getEliticMove() {
		return eliticMove;
	}

	public void setEliticMove(EliticMove eliticMove) {
		this.eliticMove = eliticMove;
	}

	public RectangleMove getRectangleMove() {
		return rectangleMove;
	}

	public void setRectangleMove(RectangleMove rectangleMove) {
		this.rectangleMove = rectangleMove;
	}

	public JumpMove getJumpMove() {
		return jumpMove;
	}

	public void setJumpMove(JumpMove jumpMove) {
		this.jumpMove = jumpMove;
	}

	public void unsetJumpMove() {
		jumpMove = null;
	}
	
	public int getFrontValue() {
		return frontValue + extraFrontValue + getSourceFrameSet().getFrontValue();
	}
	
	public void setFrontValue(int value) {
		extraFrontValue = value;
	}

	public void incFrontValue(int value) {
		extraFrontValue += value;
	}

	public int[] getCurrentSpriteOriginCoords() {
		if (spriteIndex == null)
			return new int[] { 0, 0 };
		int i = spriteIndex;
		if (multiFrameIndexByDirection != null) {
			i = multiFrameIndexByDirection[getSourceEntity().getDirection().get4DirValue()];
			if (i < 0) {
				flip = ImageFlip.HORIZONTAL;
				i = Math.abs(i) - 1;
			}
			else
				flip = ImageFlip.NONE;
			i = spriteIndex + i * spritesPerLine;
		}
		int w = (int) getOriginSpriteWidth(), h = (int) getOriginSpriteHeight(), x = (int) getOriginSpriteX() + w * (int) ((getSpritesPerLine() == 0 ? i : (i % getSpritesPerLine()))), y = (int) getOriginSpriteY() + h * (int) ((getSpritesPerLine() == 0 ? 0 : (i / getSpritesPerLine())));
		return new int[] { x, y };
	}

	public Position getAbsoluteOutputPosition() {
		return absoluteOutputSpritePos;
	}

	public Position getSpritePosition() {
		return this;
	}

	public void updateOutputDrawCoords() {
		int x = (int) getAbsoluteX(), y = (int) getAbsoluteY(), w = (int) getOutputWidth(), h = (int) getOutputHeight();
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
		absoluteOutputSpritePos.setPosition(x, y);
	}

	public void draw() {
		draw(null);
	}

	public void draw(GraphicsContext gc) {
		if (damageBox != null) {
			boolean hitted = false;
			for (Entity entity : new HashSet<>(Entity.getEntityList())) {
				if (entity.hashCode() != getSourceEntity().hashCode() && (!(getSourceEntity() instanceof Ride) || ((Ride)getSourceEntity()).getOwner().hashCode() != entity.hashCode()) && 
						(!(getSourceEntity() instanceof BomberMan) || ((BomberMan)getSourceEntity()).getRide() == null || ((BomberMan)getSourceEntity()).getRide().hashCode() != entity.hashCode())) {
							int ex = (int)entity.getX(), ey = (int)entity.getY(),
									x = (int)(getAbsoluteOutputPosition().getX() + damageBox.damageBoxRect.getX()),
									y = (int)(getAbsoluteOutputPosition().getY() + damageBox.damageBoxRect.getY()),
									w = (int)damageBox.damageBoxRect.getWidth(),
									h = (int)damageBox.damageBoxRect.getHeight();
							DamageType type = damageBox.damageType;
							String sound = damageBox.soundWhenHits;
							String frameSet = damageBox.triggerTargetFrameSet;
							ForceDirection forceDir = damageBox.forceTargetDirection; 
							if (x + w > ex && y + h > ey && x < ex + Main.TILE_SIZE && y < ey + Main.TILE_SIZE) {
								if (type == DamageType.STUN)
									entity.setCurse(Curse.STUNNED, damageBox.variant);
								else if (type == DamageType.REMOVE_ITEM && entity instanceof BomberMan)
									((BomberMan)entity).dropItem(damageBox.variant, true, false);
								if (sound != null)
									Sound.playWav(sound);
								if (forceDir != null) {
									if (forceDir == ForceDirection.SAME)
										entity.forceDirection(getSourceEntity().getDirection());
									else if (forceDir == ForceDirection.REVERSE)
										entity.forceDirection(getSourceEntity().getDirection().getReverseDirection());
									else if (forceDir == ForceDirection.NEXT_CLOCKWISE)
										entity.forceDirection(getSourceEntity().getDirection().getNext4WayClockwiseDirection());
									else if (forceDir == ForceDirection.PREVIEW_CLOCKWISE)
										entity.forceDirection(getSourceEntity().getDirection().getNext4WayClockwiseDirection(-1));
									else
										entity.forceDirection(Direction.get4DirectionFromValue((int)MyMath.getRandom(0, 3)));
								}
								if (frameSet != null && entity.haveFrameSet(frameSet))
									entity.setFrameSet(frameSet);
								hitted = true;
							}
						}
			}
			if (hitted)
				damageBox = null;
		}
		if (shake != null) {
			shake.proccess();
			if (!shake.isActive())
				shake = null;
		}
		if (getSourceEntity().isVisible() && isVisible) {
			int frontValue2 = getFrontValue();
			updateOutputDrawCoords();
			boolean blink = Misc.blink(getSourceEntity().getBlinkingFrames() > 600 ? 200 : getSourceEntity().getBlinkingFrames() > 180 ? 100 : 50);
			double localAlpha = getSourceEntity().isBlinking() && blink ? alpha / 2 : alpha;
			int[] in = getCurrentSpriteOriginCoords();
			int sx = in[0], sy = in[1], tx = (int) absoluteOutputSpritePos.getX(), ty = (int) absoluteOutputSpritePos.getY();

			if (getEliticMove() != null) {
				tx += getEliticMove().getIncrements().getX();
				ty += getEliticMove().getIncrements().getY();
				getEliticMove().move();
				frontValue2++;
			}
			if (getSourceEntity() instanceof BomberMan && ((BomberMan)getSourceEntity()).isRiding()) {
				BomberMan bomber = ((BomberMan)getSourceEntity());
				Ride ride = bomber.getRide();
				tx += ride.getRiderDesloc().getX();
				ty += ride.getRiderDesloc().getY();
				frontValue2 += ride.getRiderFrontValue();
			}
			if (getSourceEntity().getHolder() != null) {
				tx += getSourceEntity().getHolderDesloc().getX();
				ty += getSourceEntity().getHolderDesloc().getY();
				frontValue2 += 3;
			}
			ShakeEntity shake;
			if ((shake = MapSet.getShake()) != null || (shake = getSourceEntity().getShake()) != null || (shake = this.shake) != null) {
				if (this.shake != null)
					shake.proccess();
				tx += shake.getX();
				ty += shake.getY();
				if (!shake.isActive())
					shake = null;
			}
			if (jumpMove != null) {
				jumpMove.move();
				if (jumpMove.jumpIsFinished())
					unsetJumpMove();
				else {
					tx += jumpMove.getIncrements().getX();
					ty += jumpMove.getIncrements().getY();
					frontValue2++;
				}
			}
			if (getSourceEntity().getJumpMove() != null) {
				tx += (int) getSourceEntity().getJumpMove().getIncrements().getX();
				ty += (int) getSourceEntity().getJumpMove().getIncrements().getY();
				frontValue2++;
			}
			if ((getSourceEntity().getY() + Main.TILE_SIZE - ty) > getSourceEntity().getEntityHeight())
				getSourceEntity().setEntityHeight((int)(getSourceEntity().getY() + Main.TILE_SIZE - ty));
			if (gc != null)
				ImageUtils.drawImage(gc, spriteIndex == null ? Materials.blankImage : getSpriteSource(), sx, sy, (int) getOriginSpriteWidth(), (int) getOriginSpriteHeight(), tx, ty, getOutputWidth(), getOutputHeight(), flip, rotation, localAlpha, getSpriteEffects());
			else {
				DrawParams drawParams = Draw.addDrawQueue((int) getSourceEntity().getY() + frontValue2, layerType, spriteIndex == null ? Materials.blankImage : getSpriteSource(), sx, sy, (int) getOriginSpriteWidth(), (int) getOriginSpriteHeight(), tx, ty, getOutputWidth(), getOutputHeight(), flip, rotation, localAlpha, getSpriteEffects());
				if (getSourceEntity().ghostingOpacityDec != null)
					drawParams.setGhosting(getSourceEntity().ghostingDistance, getSourceEntity().ghostingOpacityDec);
				else if (ghostingOpacityDec != null)
					drawParams.setGhosting(ghostingDistance, ghostingOpacityDec);
			}
		}
		scrollSprite();
	}

	public void setSpriteScroll(double scrollX, double scrollY) {
		spriteScroll = new Position(scrollX, scrollY);
	}

	public void stopSpriteScroll() {
		spriteScroll = null;
	}

	public void scrollSprite() {
		if (spriteScroll != null) {
			Image i = getSpriteSource();
			int w = (int) i.getWidth(), h = (int) i.getHeight();
			double incX = spriteScroll.getX(), incY = spriteScroll.getX();
			Canvas canvas = new Canvas(w, h);
			GraphicsContext gc = canvas.getGraphicsContext2D();
			gc.setImageSmoothing(false);
			if (incX != 0) {
				if (incX > 0) {
					gc.drawImage(i, 0, 0, w - incX, h, incX, 0, w - incX, h);
					gc.drawImage(i, w - incX, 0, incX, h, 0, 0, incX, h);
				}
				else if (incX < 0) {
					gc.drawImage(i, -incX, 0, w - -incX, h, 0, 0, w - -incX, h);
					gc.drawImage(i, 0, 0, -incX, h, w - -incX, 0, -incX, h);
				}
				i = Draw.getCanvasSnapshot(canvas, getSpriteSource());
			}
			if (incY != 0) {
				gc.clearRect(0, 0, w, h);
				if (incY > 0) {
					gc.drawImage(i, 0, 0, w, h - incY, 0, incY, w, h - incY);
					gc.drawImage(i, 0, h - incY, w, incY, 0, 0, w, incY);
				}
				else if (incY < 0) {
					gc.drawImage(i, 0, -incY, w, h - -incY, 0, 0, w, h - -incY);
					gc.drawImage(i, 0, 0, w, -incY, 0, h - -incY, w, -incY);
				}
				Draw.getCanvasSnapshot(canvas, getSpriteSource());
			}
		}
	}

	@Override
	public TileCoord getTileCoord() {
		return new TileCoord((int) absoluteOutputSpritePos.getX() / Main.TILE_SIZE, (int) absoluteOutputSpritePos.getY() / Main.TILE_SIZE);
	}

	@Override
	public TileCoord getTileCoordFromCenter() {
		return new TileCoord((int) (absoluteOutputSpritePos.getX() + getOutputWidth() / 2) / Main.TILE_SIZE, (int) (absoluteOutputSpritePos.getY() + getOutputHeight() / 2) / Main.TILE_SIZE);
	}
	
	public void unsetDamageBox() {
		damageBox = null;
	}

	public void setDamageBox(DamageType damageType, Rectangle damageBoxRect) {
		setDamageBox(damageType, null, null, null, damageBoxRect, null);
	}	

	public void setDamageBox(DamageType damageType, ForceDirection forceTargetDirection, Rectangle damageBoxRect) {
		setDamageBox(damageType, null, null, forceTargetDirection, damageBoxRect, null);
	}	

	public void setDamageBox(DamageType damageType, String triggerTargetFrameSet, Rectangle damageBoxRect) {
		setDamageBox(damageType, null, triggerTargetFrameSet, null, damageBoxRect, null);
	}	

	public void setDamageBox(DamageType damageType, String triggerTargetFrameSet, ForceDirection forceTargetDirection, Rectangle damageBoxRect) {
		setDamageBox(damageType, null, triggerTargetFrameSet, forceTargetDirection, damageBoxRect, null);
	}	

	public void setDamageBox(DamageType damageType, Rectangle damageBoxRect, String soundWhenHits) {
		setDamageBox(damageType, null, null, null, damageBoxRect, soundWhenHits);
	}	

	public void setDamageBox(DamageType damageType, ForceDirection forceTargetDirection, Rectangle damageBoxRect, String soundWhenHits) {
		setDamageBox(damageType, null, null, forceTargetDirection, damageBoxRect, soundWhenHits);
	}	

	public void setDamageBox(DamageType damageType, String triggerTargetFrameSet, Rectangle damageBoxRect, String soundWhenHits) {
		setDamageBox(damageType, null, triggerTargetFrameSet, null, damageBoxRect, soundWhenHits);
	}	
	public void setDamageBox(DamageType damageType, String triggerTargetFrameSet, ForceDirection forceTargetDirection, Rectangle damageBoxRect, String soundWhenHits) {
		setDamageBox(damageType, null, triggerTargetFrameSet, forceTargetDirection, damageBoxRect, soundWhenHits);
	}	

	public void setDamageBox(DamageType damageType, Integer variant, Rectangle damageBoxRect) {
		setDamageBox(damageType, variant, null, null, damageBoxRect, null);
	}	

	public void setDamageBox(DamageType damageType, Integer variant, ForceDirection forceTargetDirection, Rectangle damageBoxRect) {
		setDamageBox(damageType, variant, null, forceTargetDirection, damageBoxRect, null);
	}	

	public void setDamageBox(DamageType damageType, Integer variant, String triggerTargetFrameSet, Rectangle damageBoxRect) {
		setDamageBox(damageType, variant, triggerTargetFrameSet, null, damageBoxRect, null);
	}	

	public void setDamageBox(DamageType damageType, Integer variant, String triggerTargetFrameSet, ForceDirection forceTargetDirection, Rectangle damageBoxRect) {
		setDamageBox(damageType, variant, triggerTargetFrameSet, forceTargetDirection, damageBoxRect, null);
	}	

	public void setDamageBox(DamageType damageType, Integer variant, Rectangle damageBoxRect, String soundWhenHits) {
		setDamageBox(damageType, variant, null, null, damageBoxRect, soundWhenHits);
	}	

	public void setDamageBox(DamageType damageType, Integer variant, ForceDirection forceTargetDirection, Rectangle damageBoxRect, String soundWhenHits) {
		setDamageBox(damageType, variant, null, forceTargetDirection, damageBoxRect, soundWhenHits);
	}	

	public void setDamageBox(DamageType damageType, Integer variant, String triggerTargetFrameSet, Rectangle damageBoxRect, String soundWhenHits) {
		setDamageBox(damageType, variant, triggerTargetFrameSet, null, damageBoxRect, soundWhenHits);
	}	

	public void setDamageBox(DamageType damageType, Integer variant, String triggerTargetFrameSet, ForceDirection forceTargetDirection, Rectangle damageBoxRect, String soundWhenHits) {
		if (damageBoxRect == null)
			damageBoxRect = new Rectangle(0, 0, getOutputWidth(), getOutputHeight());
		if (damageBoxRect.getWidth() == -1)
			damageBoxRect.setSize(getOutputWidth(), (int)damageBoxRect.getHeight());
		if (damageBoxRect.getHeight() == -1)
			damageBoxRect.setSize((int)damageBoxRect.getWidth(), getOutputHeight());
		this.damageBox = new DamageBox(damageType, variant, triggerTargetFrameSet, forceTargetDirection, damageBoxRect, soundWhenHits);
	}

}

class DamageBox {
	
	DamageType damageType;
	Integer variant;
	String triggerTargetFrameSet;
	ForceDirection forceTargetDirection;
	Rectangle damageBoxRect;
	String soundWhenHits;
	
	public DamageBox(DamageType damageType, Integer variant, String triggerTargetFrameSet, ForceDirection forceTargetDirection, Rectangle damageBoxRect, String soundWhenHits) {
		this.damageType = damageType;  
		this.variant = variant;
		this.triggerTargetFrameSet = triggerTargetFrameSet;
		this.forceTargetDirection = forceTargetDirection;
		this.damageBoxRect = damageBoxRect == null ? null : new Rectangle(damageBoxRect); 
		this.soundWhenHits = soundWhenHits;
	}
	
}