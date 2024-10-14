package tools;

import frameset.Tags;
import frameset_tags.*;

public abstract class FrameTagLoader {
	
	public static void loadToTags(String stringWithTags, Tags tags) {
		String[] frameTags = stringWithTags.split("\\,"); // Divisor das FrameTags de cada Sprite
		for (int n = 0; n < frameTags.length; n++) {
			String s = frameTags[n];
			if (s.length() < 3)
				continue;
			String tag;
			int x, delay = 0;
			if (s.charAt(0) != '{')
				throw new RuntimeException(s + " - Invalid Tag format");
			if ((x = s.indexOf(';')) > 0)
				tag = s.substring(1).substring(0, x - 1);
			else if ((x = s.indexOf('}')) > 0)
				tag = s.substring(1).substring(0, x - 1);
			else
				throw new RuntimeException(s + " - Invalid Tag format");
			if (tag.equals("Delay")) {
				try {
					String delayStr = s.substring(s.indexOf(';') + 1);
					tag = delayStr.substring(delayStr.indexOf(';') + 1);
					tag = tag.substring(0, tag.indexOf(';'));
					int d = Integer.parseInt(delayStr.substring(0, delayStr.indexOf(';')));
					delay = d;
					for (int z = 0; z < 2; z++)
						s = (z == 1 ? "{" : "") + s.substring(s.indexOf(';') + 1);
				}
				catch (Exception e)
					{ throw new RuntimeException(s + " - Invalid numeric value for Delay param"); }
			}
			FrameTag newTag = null;
			if (tag.equals("AddColoredLightSpot"))
				tags.addFrameSetTag(newTag = new AddColoredLightSpot(s));
			else if (tag.equals("AddColoredLightSpotToSprite"))
				tags.addFrameSetTag(newTag = new AddColoredLightSpotToSprite(s));
			else if (tag.equals("AddLightSpot"))
				tags.addFrameSetTag(newTag = new AddLightSpot(s));
			else if (tag.equals("AddLightSpotToSprite"))
				tags.addFrameSetTag(newTag = new AddLightSpotToSprite(s));
			else if (tag.equals("AddTempColoredLightSpot"))
				tags.addFrameSetTag(newTag = new AddTempColoredLightSpot(s));
			else if (tag.equals("AddTempColoredLightSpotToSprite"))
				tags.addFrameSetTag(newTag = new AddTempColoredLightSpotToSprite(s));
			else if (tag.equals("AddTempLightSpot"))
				tags.addFrameSetTag(newTag = new AddTempLightSpot(s));
			else if (tag.equals("AddTempLightSpotToSprite"))
				tags.addFrameSetTag(newTag = new AddTempLightSpotToSprite(s));
			else if (tag.equals("DecSprAlign"))
				tags.addFrameSetTag(newTag = new DecSprAlign(s));
			else if (tag.equals("DecSprFlip"))
				tags.addFrameSetTag(newTag = new DecSprFlip(s));
			else if (tag.equals("DoJump"))
				tags.addFrameSetTag(newTag = new DoJump(s));
			else if (tag.equals("Goto"))
				tags.addFrameSetTag(newTag = new Goto(s));
			else if (tag.equals("IncEntityPos"))
				tags.addFrameSetTag(newTag = new IncEntityPos(s));
			else if (tag.equals("IncEntityX"))
				tags.addFrameSetTag(newTag = new IncEntityX(s));
			else if (tag.equals("IncEntityY"))
				tags.addFrameSetTag(newTag = new IncEntityY(s));
			else if (tag.equals("IncObjPos"))
				tags.addFrameSetTag(newTag = new IncObjPos(s));
			else if (tag.equals("IncObjX"))
				tags.addFrameSetTag(newTag = new IncObjX(s));
			else if (tag.equals("IncObjY"))
				tags.addFrameSetTag(newTag = new IncObjY(s));
			else if (tag.equals("IncOriginSprHeight"))
				tags.addFrameSetTag(newTag = new IncOriginSprHeight(s));
			else if (tag.equals("IncOriginSprPerLine"))
				tags.addFrameSetTag(newTag = new IncOriginSprPerLine(s));
			else if (tag.equals("IncOriginSprPos"))
				tags.addFrameSetTag(newTag = new IncOriginSprPos(s));
			else if (tag.equals("IncOriginSprSize"))
				tags.addFrameSetTag(newTag = new IncOriginSprSize(s));
			else if (tag.equals("IncOriginSprWidth"))
				tags.addFrameSetTag(newTag = new IncOriginSprWidth(s));
			else if (tag.equals("IncOriginSprX"))
				tags.addFrameSetTag(newTag = new IncOriginSprX(s));
			else if (tag.equals("IncOriginSprY"))
				tags.addFrameSetTag(newTag = new IncOriginSprY(s));
			else if (tag.equals("IncOutputSprHeight"))
				tags.addFrameSetTag(newTag = new IncOutputSprHeight(s));
			else if (tag.equals("IncOutputSprPos"))
				tags.addFrameSetTag(newTag = new IncOutputSprPos(s));
			else if (tag.equals("IncOutputSprSize"))
				tags.addFrameSetTag(newTag = new IncOutputSprSize(s));
			else if (tag.equals("IncOutputSprWidth"))
				tags.addFrameSetTag(newTag = new IncOutputSprWidth(s));
			else if (tag.equals("IncOutputSprX"))
				tags.addFrameSetTag(newTag = new IncOutputSprX(s));
			else if (tag.equals("IncOutputSprY"))
				tags.addFrameSetTag(newTag = new IncOutputSprY(s));
			else if (tag.equals("IncSprAlign"))
				tags.addFrameSetTag(newTag = new IncSprAlign(s));
			else if (tag.equals("IncSprAlpha"))
				tags.addFrameSetTag(newTag = new IncSprAlpha(s));
			else if (tag.equals("IncSprBloomThreshold"))
				tags.addFrameSetTag(newTag = new IncSprBloomThreshold(s));
			else if (tag.equals("IncSprColorAdjustBrightness"))
				tags.addFrameSetTag(newTag = new IncSprColorAdjustBrightness(s));
			else if (tag.equals("IncSprColorAdjustHue"))
				tags.addFrameSetTag(newTag = new IncSprColorAdjustHue(s));
			else if (tag.equals("IncSprColorAdjustSaturation"))
				tags.addFrameSetTag(newTag = new IncSprColorAdjustSaturation(s));
			else if (tag.equals("IncSprColorAdjustValues"))
				tags.addFrameSetTag(newTag = new IncSprColorAdjustValues(s));
			else if (tag.equals("IncSprColorTintAlpha"))
				tags.addFrameSetTag(newTag = new IncSprColorTintAlpha(s));
			else if (tag.equals("IncSprColorTintBlue"))
				tags.addFrameSetTag(newTag = new IncSprColorTintBlue(s));
			else if (tag.equals("IncSprColorTintGreen"))
				tags.addFrameSetTag(newTag = new IncSprColorTintGreen(s));
			else if (tag.equals("IncSprColorTintRed"))
				tags.addFrameSetTag(newTag = new IncSprColorTintRed(s));
			else if (tag.equals("IncSprColorTintValues"))
				tags.addFrameSetTag(newTag = new IncSprColorTintValues(s));
			else if (tag.equals("IncSprDropShadowOffsetX"))
				tags.addFrameSetTag(newTag = new IncSprDropShadowOffsetX(s));
			else if (tag.equals("IncSprDropShadowOffsetY"))
				tags.addFrameSetTag(newTag = new IncSprDropShadowOffsetY(s));
			else if (tag.equals("IncSprDropShadowValues"))
				tags.addFrameSetTag(newTag = new IncSprDropShadowValues(s));
			else if (tag.equals("IncSprFlip"))
				tags.addFrameSetTag(newTag = new IncSprFlip(s));
			else if (tag.equals("IncSprGaussBlurRadius"))
				tags.addFrameSetTag(newTag = new IncSprGaussBlurRadius(s));
			else if (tag.equals("IncSprGlowLevel"))
				tags.addFrameSetTag(newTag = new IncSprGlowLevel(s));
			else if (tag.equals("IncSprIndex"))
				tags.addFrameSetTag(newTag = new IncSprIndex(s));
			else if (tag.equals("IncSprInnerShadowOffsetX"))
				tags.addFrameSetTag(newTag = new IncSprInnerShadowOffsetX(s));
			else if (tag.equals("IncSprInnerShadowOffsetY"))
				tags.addFrameSetTag(newTag = new IncSprInnerShadowOffsetY(s));
			else if (tag.equals("IncSprInnerShadowValues"))
				tags.addFrameSetTag(newTag = new IncSprInnerShadowValues(s));
			else if (tag.equals("IncSprMotionBlurAngle"))
				tags.addFrameSetTag(newTag = new IncSprMotionBlurAngle(s));
			else if (tag.equals("IncSprMotionBlurRadius"))
				tags.addFrameSetTag(newTag = new IncSprMotionBlurRadius(s));
			else if (tag.equals("IncSprMotionBlurValues"))
				tags.addFrameSetTag(newTag = new IncSprMotionBlurValues(s));
			else if (tag.equals("IncSprRotate"))
				tags.addFrameSetTag(newTag = new IncSprRotate(s));
			else if (tag.equals("IncSprSepiaToneLevel"))
				tags.addFrameSetTag(newTag = new IncSprSepiaToneLevel(s));
			else if (tag.equals("IncTicksPerFrame"))
				tags.addFrameSetTag(newTag = new IncTicksPerFrame(s));
			else if (tag.equals("PlayMp3"))
				tags.addFrameSetTag(newTag = new PlayMp3(s));
			else if (tag.equals("PlayWav"))
				tags.addFrameSetTag(newTag = new PlayWav(s));
			else if (tag.equals("RepeatLastFrame"))
				tags.addFrameSetTag(newTag = new RepeatLastFrame(s));
			else if (tag.equals("RunEffectAt"))
				tags.addFrameSetTag(newTag = new RunEffectAt(s));
			else if (tag.equals("RunEffectAtEntity"))
				tags.addFrameSetTag(newTag = new RunEffectAtEntity(s));
			else if (tag.equals("RunEffectAtSprite"))
				tags.addFrameSetTag(newTag = new RunEffectAtSprite(s));
			else if (tag.equals("RunEffectAtTile"))
				tags.addFrameSetTag(newTag = new RunEffectAtTile(s));
			else if (tag.equals("SetEntityNoMove"))
				tags.addFrameSetTag(newTag = new SetEntityNoMove(s));
			else if (tag.equals("SetEntityPos"))
				tags.addFrameSetTag(newTag = new SetEntityPos(s));
			else if (tag.equals("SetEntityShadow"))
				tags.addFrameSetTag(newTag = new SetEntityShadow(s));
			else if (tag.equals("SetEntityX"))
				tags.addFrameSetTag(newTag = new SetEntityX(s));
			else if (tag.equals("SetEntityY"))
				tags.addFrameSetTag(newTag = new SetEntityY(s));
			else if (tag.equals("SetFrameSet"))
				tags.addFrameSetTag(newTag = new SetFrameSet(s));
			else if (tag.equals("SetObjPos"))
				tags.addFrameSetTag(newTag = new SetObjPos(s));
			else if (tag.equals("SetObjX"))
				tags.addFrameSetTag(newTag = new SetObjX(s));
			else if (tag.equals("SetObjY"))
				tags.addFrameSetTag(newTag = new SetObjY(s));
			else if (tag.equals("SetOriginSprHeight"))
				tags.addFrameSetTag(newTag = new SetOriginSprHeight(s));
			else if (tag.equals("SetOriginSprPerLine"))
				tags.addFrameSetTag(newTag = new SetOriginSprPerLine(s));
			else if (tag.equals("SetOriginSprPos"))
				tags.addFrameSetTag(newTag = new SetOriginSprPos(s));
			else if (tag.equals("SetOriginSprSize"))
				tags.addFrameSetTag(newTag = new SetOriginSprSize(s));
			else if (tag.equals("SetOriginSprWidth"))
				tags.addFrameSetTag(newTag = new SetOriginSprWidth(s));
			else if (tag.equals("SetOriginSprX"))
				tags.addFrameSetTag(newTag = new SetOriginSprX(s));
			else if (tag.equals("SetOriginSprY"))
				tags.addFrameSetTag(newTag = new SetOriginSprY(s));
			else if (tag.equals("SetOutputSprHeight"))
				tags.addFrameSetTag(newTag = new SetOutputSprHeight(s));
			else if (tag.equals("SetOutputSprPos"))
				tags.addFrameSetTag(newTag = new SetOutputSprPos(s));
			else if (tag.equals("SetOutputSprSize"))
				tags.addFrameSetTag(newTag = new SetOutputSprSize(s));
			else if (tag.equals("SetOutputSprWidth"))
				tags.addFrameSetTag(newTag = new SetOutputSprWidth(s));
			else if (tag.equals("SetOutputSprX"))
				tags.addFrameSetTag(newTag = new SetOutputSprX(s));
			else if (tag.equals("SetOutputSprY"))
				tags.addFrameSetTag(newTag = new SetOutputSprY(s));
			else if (tag.equals("SetSprAlign"))
				tags.addFrameSetTag(newTag = new SetSprAlign(s));
			else if (tag.equals("SetSprAlpha"))
				tags.addFrameSetTag(newTag = new SetSprAlpha(s));
			else if (tag.equals("SetSprBloomBlendMode"))
				tags.addFrameSetTag(newTag = new SetSprBloomBlendMode(s));
			else if (tag.equals("SetSprBloomThreshold"))
				tags.addFrameSetTag(newTag = new SetSprBloomThreshold(s));
			else if (tag.equals("SetSprBloomValues"))
				tags.addFrameSetTag(newTag = new SetSprBloomValues(s));
			else if (tag.equals("SetSprColorAdjustBlendMode"))
				tags.addFrameSetTag(newTag = new SetSprColorAdjustBlendMode(s));
			else if (tag.equals("SetSprColorAdjustBrightness"))
				tags.addFrameSetTag(newTag = new SetSprColorAdjustBrightness(s));
			else if (tag.equals("SetSprColorAdjustHue"))
				tags.addFrameSetTag(newTag = new SetSprColorAdjustHue(s));
			else if (tag.equals("SetSprColorAdjustSaturation"))
				tags.addFrameSetTag(newTag = new SetSprColorAdjustSaturation(s));
			else if (tag.equals("SetSprColorAdjustValues"))
				tags.addFrameSetTag(newTag = new SetSprColorAdjustValues(s));
			else if (tag.equals("SetSprColorTintAlpha"))
				tags.addFrameSetTag(newTag = new SetSprColorTintAlpha(s));
			else if (tag.equals("SetSprColorTintBlendMode"))
				tags.addFrameSetTag(newTag = new SetSprColorTintBlendMode(s));
			else if (tag.equals("SetSprColorTintBlue"))
				tags.addFrameSetTag(newTag = new SetSprColorTintBlue(s));
			else if (tag.equals("SetSprColorTintGreen"))
				tags.addFrameSetTag(newTag = new SetSprColorTintGreen(s));
			else if (tag.equals("SetSprColorTintRed"))
				tags.addFrameSetTag(newTag = new SetSprColorTintRed(s));
			else if (tag.equals("SetSprColorTintValues"))
				tags.addFrameSetTag(newTag = new SetSprColorTintValues(s));
			else if (tag.equals("SetSprDropShadowBlendMode"))
				tags.addFrameSetTag(newTag = new SetSprDropShadowBlendMode(s));
			else if (tag.equals("SetSprDropShadowOffsetX"))
				tags.addFrameSetTag(newTag = new SetSprDropShadowOffsetX(s));
			else if (tag.equals("SetSprDropShadowOffsetY"))
				tags.addFrameSetTag(newTag = new SetSprDropShadowOffsetY(s));
			else if (tag.equals("SetSprDropShadowValues"))
				tags.addFrameSetTag(newTag = new SetSprDropShadowValues(s));
			else if (tag.equals("SetSprEliticMove"))
				tags.addFrameSetTag(newTag = new SetSprEliticMove(s));
			else if (tag.equals("SetSprFlip"))
				tags.addFrameSetTag(newTag = new SetSprFlip(s));
			else if (tag.equals("SetSprGaussBlurBlendMode"))
				tags.addFrameSetTag(newTag = new SetSprGaussBlurBlendMode(s));
			else if (tag.equals("SetSprGaussBlurRadius"))
				tags.addFrameSetTag(newTag = new SetSprGaussBlurRadius(s));
			else if (tag.equals("SetSprGaussBlurValues"))
				tags.addFrameSetTag(newTag = new SetSprGaussBlurValues(s));
			else if (tag.equals("SetSprGlowBlendMode"))
				tags.addFrameSetTag(newTag = new SetSprGlowBlendMode(s));
			else if (tag.equals("SetSprGlowLevel"))
				tags.addFrameSetTag(newTag = new SetSprGlowLevel(s));
			else if (tag.equals("SetSprGlowValues"))
				tags.addFrameSetTag(newTag = new SetSprGlowValues(s));
			else if (tag.equals("SetSprIndex"))
				tags.addFrameSetTag(newTag = new SetSprIndex(s));
			else if (tag.equals("SetSprInnerShadowBlendMode"))
				tags.addFrameSetTag(newTag = new SetSprInnerShadowBlendMode(s));
			else if (tag.equals("SetSprInnerShadowOffsetX"))
				tags.addFrameSetTag(newTag = new SetSprInnerShadowOffsetX(s));
			else if (tag.equals("SetSprInnerShadowOffsetY"))
				tags.addFrameSetTag(newTag = new SetSprInnerShadowOffsetY(s));
			else if (tag.equals("SetSprInnerShadowValues"))
				tags.addFrameSetTag(newTag = new SetSprInnerShadowValues(s));
			else if (tag.equals("SetSprMotionBlurAngle"))
				tags.addFrameSetTag(newTag = new SetSprMotionBlurAngle(s));
			else if (tag.equals("SetSprMotionBlurBlendMode"))
				tags.addFrameSetTag(newTag = new SetSprMotionBlurBlendMode(s));
			else if (tag.equals("SetSprMotionBlurRadius"))
				tags.addFrameSetTag(newTag = new SetSprMotionBlurRadius(s));
			else if (tag.equals("SetSprMotionBlurValues"))
				tags.addFrameSetTag(newTag = new SetSprMotionBlurValues(s));
			else if (tag.equals("SetSprRotate"))
				tags.addFrameSetTag(newTag = new SetSprRotate(s));
			else if (tag.equals("SetSprSepiaToneBlendMode"))
				tags.addFrameSetTag(newTag = new SetSprSepiaToneBlendMode(s));
			else if (tag.equals("SetSprSepiaToneLevel"))
				tags.addFrameSetTag(newTag = new SetSprSepiaToneLevel(s));
			else if (tag.equals("SetSprSepiaToneValues"))
				tags.addFrameSetTag(newTag = new SetSprSepiaToneValues(s));
			else if (tag.equals("SetSprSource"))
				tags.addFrameSetTag(newTag = new SetSprSource(s));
			else if (tag.equals("SetTicksPerFrame"))
				tags.addFrameSetTag(newTag = new SetTicksPerFrame(s));
			else if (tag.equals("SetSprWaving"))
				tags.addFrameSetTag(newTag = new SetSprWaving(s));
			else if (tag.equals("SetSprScrolling"))
				tags.addFrameSetTag(newTag = new SetSprScrolling(s));
			else if (tag.equals("SetSwitchValue"))
				tags.addFrameSetTag(newTag = new SetSwitchValue(s));
			else if (tag.equals("DecSwitchValue"))
				tags.addFrameSetTag(newTag = new DecSwitchValue(s));
			else if (tag.equals("IncSwitchValue"))
				tags.addFrameSetTag(newTag = new IncSwitchValue(s));
			else if (tag.equals("SetMapLayerIndex"))
				tags.addFrameSetTag(newTag = new SetMapLayerIndex(s));
			else if (tag.equals("SetMapFrameSet"))
				tags.addFrameSetTag(newTag = new SetMapFrameSet(s));
			else if (tag.equals("DisableTileTags"))
				tags.addFrameSetTag(newTag = new DisableTileTags(s));
			else if (tag.equals("EnableTileTags"))
				tags.addFrameSetTag(newTag = new EnableTileTags(s));
			else if (tag.equals("CopySprFromCopyLayer"))
				tags.addFrameSetTag(newTag = new CopySprFromCopyLayer(s));
			else if (tag.equals("AddTileProp"))
				tags.addFrameSetTag(newTag = new AddTileProp(s));
			else if (tag.equals("RemoveTileProp"))
				tags.addFrameSetTag(newTag = new RemoveTileProp(s));
			else if (tag.equals("DelayTags"))
				tags.addFrameSetTag(newTag = new DelayTags(s));
			else if (tag.equals("RunStageTags"))
				tags.addFrameSetTag(newTag = new RunStageTags(s));
			if (delay > 0)
				newTag.setTriggerDelay(delay);
		}
	}

}
