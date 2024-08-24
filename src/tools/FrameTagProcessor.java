package tools;

import application.Main;
import entities.FrameSet;
import entities.Sprite;
import entities.Tags;
import frameset_tags.DecSprAlign;
import frameset_tags.DecSprFlip;
import frameset_tags.FrameTag;
import frameset_tags.Goto;
import frameset_tags.IncObjPos;
import frameset_tags.IncObjX;
import frameset_tags.IncObjY;
import frameset_tags.IncOriginSprHeight;
import frameset_tags.IncOriginSprPerLine;
import frameset_tags.IncOriginSprPos;
import frameset_tags.IncOriginSprSize;
import frameset_tags.IncOriginSprWidth;
import frameset_tags.IncOriginSprX;
import frameset_tags.IncOriginSprY;
import frameset_tags.IncOutputSprHeight;
import frameset_tags.IncOutputSprPos;
import frameset_tags.IncOutputSprSize;
import frameset_tags.IncOutputSprWidth;
import frameset_tags.IncOutputSprX;
import frameset_tags.IncOutputSprY;
import frameset_tags.IncSprAlign;
import frameset_tags.IncSprAlpha;
import frameset_tags.IncSprBloomThreshold;
import frameset_tags.IncSprColorAdjustBrightness;
import frameset_tags.IncSprColorAdjustHue;
import frameset_tags.IncSprColorAdjustSaturation;
import frameset_tags.IncSprColorAdjustValues;
import frameset_tags.IncSprColorTintAlpha;
import frameset_tags.IncSprColorTintBlue;
import frameset_tags.IncSprColorTintGreen;
import frameset_tags.IncSprColorTintRed;
import frameset_tags.IncSprColorTintValues;
import frameset_tags.IncSprDropShadowValues;
import frameset_tags.IncSprFlip;
import frameset_tags.IncSprGaussBlurRadius;
import frameset_tags.IncSprGlowLevel;
import frameset_tags.IncSprIndex;
import frameset_tags.IncSprInnerShadowValues;
import frameset_tags.IncSprMotionBlurAngle;
import frameset_tags.IncSprMotionBlurRadius;
import frameset_tags.IncSprRotate;
import frameset_tags.IncSprSepiaToneLevel;
import frameset_tags.IncTicksPerFrame;
import frameset_tags.PlaySound;
import frameset_tags.RepeatLastFrame;
import frameset_tags.SetObjPos;
import frameset_tags.SetObjX;
import frameset_tags.SetObjY;
import frameset_tags.SetOriginSprHeight;
import frameset_tags.SetOriginSprPerLine;
import frameset_tags.SetOriginSprPos;
import frameset_tags.SetOriginSprSize;
import frameset_tags.SetOriginSprWidth;
import frameset_tags.SetOriginSprX;
import frameset_tags.SetOriginSprY;
import frameset_tags.SetOutputSprHeight;
import frameset_tags.SetOutputSprPos;
import frameset_tags.SetOutputSprSize;
import frameset_tags.SetOutputSprWidth;
import frameset_tags.SetOutputSprX;
import frameset_tags.SetOutputSprY;
import frameset_tags.SetSprAlign;
import frameset_tags.SetSprAlpha;
import frameset_tags.SetSprBloomBlendMode;
import frameset_tags.SetSprBloomThreshold;
import frameset_tags.SetSprBloomValues;
import frameset_tags.SetSprColorAdjustBlendMode;
import frameset_tags.SetSprColorAdjustBrightness;
import frameset_tags.SetSprColorAdjustHue;
import frameset_tags.SetSprColorAdjustSaturation;
import frameset_tags.SetSprColorAdjustValues;
import frameset_tags.SetSprColorTintAlpha;
import frameset_tags.SetSprColorTintBlendMode;
import frameset_tags.SetSprColorTintBlue;
import frameset_tags.SetSprColorTintGreen;
import frameset_tags.SetSprColorTintRed;
import frameset_tags.SetSprColorTintValues;
import frameset_tags.SetSprDropShadowBlendMode;
import frameset_tags.SetSprDropShadowOffsetX;
import frameset_tags.SetSprDropShadowOffsetY;
import frameset_tags.SetSprDropShadowValues;
import frameset_tags.SetSprFlip;
import frameset_tags.SetSprGaussBlurBlendMode;
import frameset_tags.SetSprGaussBlurValues;
import frameset_tags.SetSprGlowBlendMode;
import frameset_tags.SetSprGlowValues;
import frameset_tags.SetSprIndex;
import frameset_tags.SetSprInnerShadowBlendMode;
import frameset_tags.SetSprInnerShadowOffsetX;
import frameset_tags.SetSprInnerShadowOffsetY;
import frameset_tags.SetSprInnerShadowValues;
import frameset_tags.SetSprMotionBlurBlendMode;
import frameset_tags.SetSprMotionBlurValues;
import frameset_tags.SetSprRotate;
import frameset_tags.SetSprSepiaToneBlendMode;
import frameset_tags.SetSprSepiaToneValues;
import frameset_tags.SetSprSource;
import frameset_tags.SetTicksPerFrame;

public abstract class FrameTagProcessor {
	
	public static void process(Tags tags) {
			Sprite sprite = tags.getRootSprite();
			FrameSet frameSet = sprite.getMainFrameSet();
			for (FrameTag tag : tags.getFrameSetTags()) {
				if (tag instanceof Goto) {
					Goto tag2 = (Goto)tag;
					if ((!Main.spriteEditor || !FrameSetEditor.isPaused) &&
							!sprite.getMainFrameSet().isStopped()) {
								if (tag2.haveLeftCycles()) {
									tag2.incCycles();
									int index = tag2.getIndex() < 0 ? frameSet.getCurrentFrameIndex() + tag2.getIndex() : tag2.getIndex();
									if (index < 0)
										index = 0;
									else if (index >= frameSet.getTotalFrames())
										index = frameSet.getTotalFrames() == 0 ? 0 : frameSet.getTotalFrames() - 1;
									frameSet.setCurrentFrameIndex(index);
								}
								else {
									tag2.resetCycles();
									frameSet.incFrameIndex();
								}
					}
				}
				else if (tag instanceof RepeatLastFrame) {
					RepeatLastFrame tag2 = (RepeatLastFrame)tag;
					if ((!Main.spriteEditor || !FrameSetEditor.isPaused) &&
							!sprite.getMainFrameSet().isStopped()) {
								if (tag2.haveLeftCycles()) {
									tag2.incCycles();
									int index = frameSet.getCurrentFrameIndex() - 1;
									if (index < 0)
										index = 0;
									else if (index >= frameSet.getTotalFrames())
										index = frameSet.getTotalFrames() == 0 ? 0 : frameSet.getTotalFrames() - 1;
									frameSet.setCurrentFrameIndex(index);
								}
								else {
									tag2.resetCycles();
									frameSet.incFrameIndex();
								}
					}
				}
				else if (tag instanceof PlaySound) {
					if (!Main.spriteEditor || !FrameSetEditor.isPaused)
						Sound.playSound(((PlaySound)tag).getPartialSoundPath());
				}
				else if (tag instanceof SetSprSource) {
					sprite.setSpriteSource(((SetSprSource)tag).getSpriteSource());
					sprite.setOriginSpritePos(((SetSprSource)tag).getOriginSprSizePos());
					sprite.setOutputSpritePos(((SetSprSource)tag).getOutputSprSizePos());
					sprite.setSpriteIndex(((SetSprSource)tag).getSpriteIndex());
					sprite.setSpritesPerLine(((SetSprSource)tag).getSpritesPerLine());
				}
				else if (tag instanceof SetTicksPerFrame)
					frameSet.setFramesPerTick(((SetTicksPerFrame)tag).getValue());
				else if (tag instanceof IncTicksPerFrame)
					frameSet.incFramesPerTick(((IncTicksPerFrame)tag).getIncrement());
				else if (tag instanceof SetSprIndex)
					sprite.setSpriteIndex(((SetSprIndex)tag).getValue());
				else if (tag instanceof IncSprIndex) 
					sprite.incSpriteIndex(((IncSprIndex)tag).getIncrement());
				else if (tag instanceof SetOutputSprPos) {
					sprite.setX(((SetOutputSprPos)tag).getX());
					sprite.setY(((SetOutputSprPos)tag).getY());
				}
				else if (tag instanceof SetOutputSprX)
					sprite.setX(((SetOutputSprX)tag).getValue());
				else if (tag instanceof SetOutputSprY)
					sprite.setY(((SetOutputSprY)tag).getValue());
				else if (tag instanceof IncOutputSprPos) {
					sprite.incX(((IncOutputSprPos)tag).getIncrementX());
					sprite.incY(((IncOutputSprPos)tag).getIncrementY());
				}
				else if (tag instanceof IncOutputSprX)
					sprite.incX(((IncOutputSprX)tag).getIncrement());
				else if (tag instanceof IncOutputSprY)
					sprite.incY(((IncOutputSprY)tag).getIncrement());
				else if (tag instanceof SetObjPos) {
					frameSet.getEntity().incX(((SetObjPos)tag).getX());
					frameSet.getEntity().incY(((SetObjPos)tag).getY());
				}
				else if (tag instanceof SetObjX)
					sprite.setX(((SetObjX)tag).getValue());
				else if (tag instanceof SetObjY)
					sprite.setY(((SetObjY)tag).getValue());
				else if (tag instanceof IncObjPos) {
					frameSet.getEntity().incX(((IncObjPos)tag).getIncrementX());
					frameSet.getEntity().incY(((IncObjPos)tag).getIncrementY());
				}
				else if (tag instanceof IncObjX)
					sprite.incX(((IncObjX)tag).getIncrement());
				else if (tag instanceof IncObjY)
					sprite.incY(((IncObjY)tag).getIncrement());
				else if (tag instanceof SetSprAlign)
					sprite.setAlignment(((SetSprAlign)tag).getAlignment());
				else if (tag instanceof DecSprAlign)
					sprite.setAlignment(sprite.getAlignment().getPreview());
				else if (tag instanceof IncSprAlign)
					sprite.setAlignment(sprite.getAlignment().getNext());
				else if (tag instanceof SetSprFlip)
					sprite.setFlip(((SetSprFlip)tag).getFlip());
				else if (tag instanceof DecSprFlip)
					sprite.setFlip(sprite.getFlip().getPreview());
				else if (tag instanceof IncSprFlip)
					sprite.setFlip(sprite.getFlip().getNext());
				else if (tag instanceof SetSprRotate)
					sprite.setRotation(((SetSprRotate)tag).getValue());
				else if (tag instanceof IncSprRotate)
					sprite.incRotation(((IncSprRotate)tag).getIncrement());
				else if (tag instanceof SetSprAlpha)
					sprite.setAlpha(((SetSprAlpha)tag).getValue());
				else if (tag instanceof IncSprAlpha)
					sprite.incAlpha(((IncSprAlpha)tag).getIncrement());
				else if (tag instanceof SetSprBloomValues)
					sprite.getEffects().setBloom(((SetSprBloomValues)tag).getThreshold(),
																			 ((SetSprBloomValues)tag).getBlendMode());
				else if (tag instanceof SetSprBloomThreshold)
					sprite.getEffects().getBloom().setThreshold(((SetSprBloomThreshold)tag).getValue());
				else if (tag instanceof SetSprBloomBlendMode)
					sprite.getEffects().getBloom().setBlendMode(((SetSprBloomBlendMode)tag).getBlendMode());
				else if (tag instanceof IncSprBloomThreshold)
					sprite.getEffects().getBloom().incThreshold(((IncSprBloomThreshold)tag).getIncrement());
				else if (tag instanceof SetSprColorAdjustValues)
					sprite.getEffects().setColorAdjust(((SetSprColorAdjustValues)tag).getHue(),
																						 ((SetSprColorAdjustValues)tag).getSaturation(),
																						 ((SetSprColorAdjustValues)tag).getBrightness(),
																						 ((SetSprColorAdjustValues)tag).getBlendMode());
				else if (tag instanceof SetSprColorAdjustHue)
					sprite.getEffects().getColorAdjust().setHue(((SetSprColorAdjustHue)tag).getValue());
				else if (tag instanceof SetSprColorAdjustSaturation)
					sprite.getEffects().getColorAdjust().setSaturation(((SetSprColorAdjustSaturation)tag).getValue());
				else if (tag instanceof SetSprColorAdjustBrightness)
					sprite.getEffects().getColorAdjust().setBrightness(((SetSprColorAdjustBrightness)tag).getValue());
				else if (tag instanceof SetSprColorAdjustBlendMode)
					sprite.getEffects().getColorAdjust().setBlendMode(((SetSprColorAdjustBlendMode)tag).getBlendMode());
				else if (tag instanceof IncSprColorAdjustValues)
					sprite.getEffects().getColorAdjust().incValues(((SetSprColorAdjustValues)tag).getHue(),
																												 ((SetSprColorAdjustValues)tag).getSaturation(),
																												 ((SetSprColorAdjustValues)tag).getBrightness());

				else if (tag instanceof IncSprColorAdjustHue)
					sprite.getEffects().getColorAdjust().incHue(((IncSprColorAdjustValues)tag).getIncrementHue());
				else if (tag instanceof IncSprColorAdjustSaturation)
					sprite.getEffects().getColorAdjust().incSaturation(((IncSprColorAdjustValues)tag).getIncrementSaturation());
				else if (tag instanceof IncSprColorAdjustBrightness)
					sprite.getEffects().getColorAdjust().incBrightness(((IncSprColorAdjustValues)tag).getIncrementBrightness());
				else if (tag instanceof SetSprColorTintValues)
					sprite.getEffects().setColorTint(((SetSprColorTintValues)tag).getRed(),
																					 ((SetSprColorTintValues)tag).getGreen(),
																					 ((SetSprColorTintValues)tag).getBlue(),
																					 ((SetSprColorTintValues)tag).getAlpha());
				else if (tag instanceof SetSprColorTintRed)
					sprite.getEffects().getColorTint().setRed(((SetSprColorTintRed)tag).getValue());
				else if (tag instanceof SetSprColorTintGreen)
					sprite.getEffects().getColorTint().setGreen(((SetSprColorTintGreen)tag).getValue());
				else if (tag instanceof SetSprColorTintBlue)
					sprite.getEffects().getColorTint().setBlue(((SetSprColorTintBlue)tag).getValue());
				else if (tag instanceof SetSprColorTintAlpha)
					sprite.getEffects().getColorTint().setAlpha(((SetSprColorTintAlpha)tag).getValue());
				else if (tag instanceof SetSprColorTintBlendMode)
					sprite.getEffects().getColorTint().setBlendMode(((SetSprColorTintBlendMode)tag).getBlendMode());
				else if (tag instanceof IncSprColorTintValues) {
					sprite.getEffects().getColorTint().incRed(((IncSprColorTintValues)tag).getIncrementRed());
					sprite.getEffects().getColorTint().incGreen(((IncSprColorTintValues)tag).getIncrementGreen());
					sprite.getEffects().getColorTint().incBlue(((IncSprColorTintValues)tag).getIncrementBlue());
					sprite.getEffects().getColorTint().incAlpha(((IncSprColorTintValues)tag).getIncrementAlpha());
				}
				else if (tag instanceof IncSprColorTintRed)
					sprite.getEffects().getColorTint().incRed(((IncSprColorTintRed)tag).getIncrement());
				else if (tag instanceof IncSprColorTintGreen)
					sprite.getEffects().getColorTint().incGreen(((IncSprColorTintGreen)tag).getIncrement());
				else if (tag instanceof IncSprColorTintBlue)
					sprite.getEffects().getColorTint().incBlue(((IncSprColorTintBlue)tag).getIncrement());
				else if (tag instanceof IncSprColorTintAlpha)
					sprite.getEffects().getColorTint().incAlpha(((IncSprColorTintAlpha)tag).getIncrement());
				else if (tag instanceof SetSprGaussBlurValues)
					sprite.getEffects().setGaussianBlur(((SetSprGaussBlurValues)tag).getRadius(),
																							((SetSprGaussBlurValues)tag).getBlendMode());
				else if (tag instanceof IncSprGaussBlurRadius)
					sprite.getEffects().getGaussianBlur().incRadius(((IncSprGaussBlurRadius)tag).getIncrement());
				else if (tag instanceof SetSprGaussBlurBlendMode)
					sprite.getEffects().setGaussianBlur(sprite.getEffects().getGaussianBlur().getRadius(), ((SetSprGaussBlurValues)tag).getBlendMode());
				else if (tag instanceof SetSprGlowValues)
					sprite.getEffects().setGlow(((SetSprGlowValues)tag).getLevel(), ((SetSprGlowValues)tag).getBlendMode());
				else if (tag instanceof IncSprGlowLevel)
					sprite.getEffects().setGlow(sprite.getEffects().getGlow().getLevel() + ((IncSprGlowLevel)tag).getIncrement(), sprite.getEffects().getGlow().getBlendMode());
				else if (tag instanceof SetSprGlowBlendMode)
					sprite.getEffects().setGlow(sprite.getEffects().getGlow().getLevel(), ((SetSprGlowBlendMode)tag).getBlendMode());
				else if (tag instanceof SetSprMotionBlurValues)
					sprite.getEffects().setMotionBlur(((SetSprMotionBlurValues)tag).getAngle(),
																						((SetSprMotionBlurValues)tag).getRadius(),
																						((SetSprMotionBlurValues)tag).getBlendMode());
				else if (tag instanceof SetSprMotionBlurBlendMode)
					sprite.getEffects().getMotionBlur().setBlendMode(((SetSprMotionBlurBlendMode)tag).getBlendMode());
				else if (tag instanceof IncSprMotionBlurAngle)
					sprite.getEffects().getMotionBlur().incAngle(((IncSprMotionBlurAngle)tag).getIncrement());
				else if (tag instanceof IncSprMotionBlurRadius)
					sprite.getEffects().getMotionBlur().incRadius(((IncSprMotionBlurRadius)tag).getIncrement());
				else if (tag instanceof SetSprSepiaToneValues)
					sprite.getEffects().setSepiaTone(((SetSprSepiaToneValues)tag).getGlow(), ((SetSprSepiaToneValues)tag).getBlendMode());
				else if (tag instanceof SetSprSepiaToneBlendMode)
					sprite.getEffects().getSepiaTone().setBlendMode(((SetSprSepiaToneBlendMode)tag).getBlendMode());
				else if (tag instanceof IncSprSepiaToneLevel)
					sprite.getEffects().getSepiaTone().incLevel(((IncSprSepiaToneLevel)tag).getIncrement());
				else if (tag instanceof SetSprDropShadowValues)
					sprite.getEffects().setDropShadow(((SetSprDropShadowValues)tag).getOffsetX(),
																						((SetSprDropShadowValues)tag).getOffsetY(),
																						((SetSprDropShadowValues)tag).getBlendMode());
				else if (tag instanceof SetSprDropShadowOffsetX)
					sprite.getEffects().getDropShadow().setOffsetX(((SetSprDropShadowOffsetX)tag).getValue());
				else if (tag instanceof SetSprDropShadowOffsetY)
					sprite.getEffects().getDropShadow().setOffsetY(((SetSprDropShadowOffsetY)tag).getValue());
				else if (tag instanceof SetSprDropShadowBlendMode)
					sprite.getEffects().getDropShadow().setBlendMode(((SetSprDropShadowBlendMode)tag).getBlendMode());
				else if (tag instanceof IncSprDropShadowValues) {
					sprite.getEffects().getDropShadow().incOffsetX(((IncSprDropShadowValues)tag).getIncrementOffsetX());
					sprite.getEffects().getDropShadow().incOffsetY(((IncSprDropShadowValues)tag).getIncrementOffsetY());
				}
				else if (tag instanceof SetSprInnerShadowValues)
					sprite.getEffects().setInnerShadow(((SetSprInnerShadowValues)tag).getOffsetX(),
																						((SetSprInnerShadowValues)tag).getOffsetY(),
																						((SetSprInnerShadowValues)tag).getBlendMode());
				else if (tag instanceof SetSprInnerShadowOffsetX)
					sprite.getEffects().getInnerShadow().setOffsetX(((SetSprInnerShadowOffsetX)tag).getValue());
				else if (tag instanceof SetSprInnerShadowOffsetY)
					sprite.getEffects().getInnerShadow().setOffsetY(((SetSprInnerShadowOffsetY)tag).getValue());
				else if (tag instanceof SetSprInnerShadowBlendMode)
					sprite.getEffects().getInnerShadow().setBlendMode(((SetSprInnerShadowBlendMode)tag).getBlendMode());
				else if (tag instanceof IncSprInnerShadowValues) {
					sprite.getEffects().getInnerShadow().incOffsetX(((IncSprInnerShadowValues)tag).getIncrementOffsetX());
					sprite.getEffects().getInnerShadow().incOffsetY(((IncSprInnerShadowValues)tag).getIncrementOffsetY());
				}
				else if (tag instanceof SetOutputSprSize) {
					sprite.setOutputWidth(((SetOutputSprSize)tag).getWidth());
					sprite.setOutputHeight(((SetOutputSprSize)tag).getHeight());
				}
				else if (tag instanceof SetOutputSprWidth)
					sprite.setOutputWidth(((SetOutputSprWidth)tag).getValue());
				else if (tag instanceof SetOutputSprHeight)
					sprite.setOutputHeight(((SetOutputSprHeight)tag).getValue());
				else if (tag instanceof IncOutputSprSize) {
					sprite.incOutputWidth(((IncOutputSprSize)tag).getIncrementWidth());
					sprite.incOutputHeight(((IncOutputSprSize)tag).getIncrementHeight());
				}
				else if (tag instanceof IncOutputSprWidth)
					sprite.incOutputWidth(((IncOutputSprWidth)tag).getIncrement());
				else if (tag instanceof IncOutputSprHeight)
					sprite.incOutputHeight(((IncOutputSprHeight)tag).getIncrement());
				else if (tag instanceof SetOriginSprPerLine)
					sprite.setSpritesPerLine(((SetOriginSprPerLine)tag).getValue());
				else if (tag instanceof IncOriginSprPerLine)
					sprite.incSpritesPerLine(((IncOriginSprPerLine)tag).getIncrement());
				else if (tag instanceof SetOriginSprPos) {
					sprite.setOriginSpriteX(((SetOriginSprPos)tag).getX());
					sprite.setOriginSpriteY(((SetOriginSprPos)tag).getY());
				}
				else if (tag instanceof SetOriginSprX)
					sprite.setOriginSpriteX(((SetOriginSprX)tag).getValue());
				else if (tag instanceof SetOriginSprY)
					sprite.setOriginSpriteY(((SetOriginSprY)tag).getValue());
				else if (tag instanceof IncOriginSprPos) {
					sprite.incOriginSpriteX(((IncOriginSprPos)tag).getIncrementX());
					sprite.incOriginSpriteY(((IncOriginSprPos)tag).getIncrementY());
				}
				else if (tag instanceof IncOriginSprX)
					sprite.incOriginSpriteX(((IncOriginSprX)tag).getIncrement());
				else if (tag instanceof IncOriginSprY)
					sprite.incOriginSpriteY(((IncOriginSprY)tag).getIncrement());
				else if (tag instanceof SetOriginSprSize) {
					sprite.setOriginSpriteWidth(((SetOriginSprSize)tag).getWidth());
					sprite.setOriginSpriteHeight(((SetOriginSprSize)tag).getHeight());
				}
				else if (tag instanceof SetOriginSprWidth)
					sprite.setOriginSpriteWidth(((SetOriginSprWidth)tag).getValue());
				else if (tag instanceof SetOriginSprHeight)
					sprite.setOriginSpriteHeight(((SetOriginSprHeight)tag).getValue());
				else if (tag instanceof IncOriginSprSize) {
					sprite.incOriginSpriteWidth(((IncOriginSprSize)tag).getIncrementWidth());
					sprite.incOriginSpriteHeight(((IncOriginSprSize)tag).getIncrementHeight());
				}
				else if (tag instanceof IncOriginSprWidth)
					sprite.incOriginSpriteWidth(((IncOriginSprWidth)tag).getIncrement());
				else if (tag instanceof IncOriginSprHeight)
					sprite.incOriginSpriteHeight(((IncOriginSprHeight)tag).getIncrement());
			}
	}

}
