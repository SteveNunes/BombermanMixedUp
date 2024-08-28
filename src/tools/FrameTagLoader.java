package tools;

import entities.Tags;
import frameset_tags.*;

public abstract class FrameTagLoader {
	
	public static void loadToTags(String stringWithTags, Tags tags) {
		String[] frameTags = stringWithTags.split("\\,"); // Divisor das FrameTags de cada Sprite
		for (String s : frameTags) {
			if (s.length() < 3)
				continue;
			String tag = s.substring(1).substring(0, s.indexOf(';') - 1);
			if (tag.equals("DecSprAlign"))
				tags.addFrameSetTag(new DecSprAlign(s));
			else if (tag.equals("DecSprFlip"))
				tags.addFrameSetTag(new DecSprFlip(s));
			else if (tag.equals("Goto"))
				tags.addFrameSetTag(new Goto(s));
			else if (tag.equals("IncEntityPos"))
				tags.addFrameSetTag(new IncEntityPos(s));
			else if (tag.equals("IncEntityX"))
				tags.addFrameSetTag(new IncEntityX(s));
			else if (tag.equals("IncEntityY"))
				tags.addFrameSetTag(new IncEntityY(s));
			else if (tag.equals("IncObjPos"))
				tags.addFrameSetTag(new IncObjPos(s));
			else if (tag.equals("IncObjX"))
				tags.addFrameSetTag(new IncObjX(s));
			else if (tag.equals("IncObjY"))
				tags.addFrameSetTag(new IncObjY(s));
			else if (tag.equals("IncOriginSprHeight"))
				tags.addFrameSetTag(new IncOriginSprHeight(s));
			else if (tag.equals("IncOriginSprPerLine"))
				tags.addFrameSetTag(new IncOriginSprPerLine(s));
			else if (tag.equals("IncOriginSprPos"))
				tags.addFrameSetTag(new IncOriginSprPos(s));
			else if (tag.equals("IncOriginSprSize"))
				tags.addFrameSetTag(new IncOriginSprSize(s));
			else if (tag.equals("IncOriginSprWidth"))
				tags.addFrameSetTag(new IncOriginSprWidth(s));
			else if (tag.equals("IncOriginSprX"))
				tags.addFrameSetTag(new IncOriginSprX(s));
			else if (tag.equals("IncOriginSprY"))
				tags.addFrameSetTag(new IncOriginSprY(s));
			else if (tag.equals("IncOutputSprHeight"))
				tags.addFrameSetTag(new IncOutputSprHeight(s));
			else if (tag.equals("IncOutputSprPos"))
				tags.addFrameSetTag(new IncOutputSprPos(s));
			else if (tag.equals("IncOutputSprSize"))
				tags.addFrameSetTag(new IncOutputSprSize(s));
			else if (tag.equals("IncOutputSprWidth"))
				tags.addFrameSetTag(new IncOutputSprWidth(s));
			else if (tag.equals("IncOutputSprX"))
				tags.addFrameSetTag(new IncOutputSprX(s));
			else if (tag.equals("IncOutputSprY"))
				tags.addFrameSetTag(new IncOutputSprY(s));
			else if (tag.equals("IncSprAlign"))
				tags.addFrameSetTag(new IncSprAlign(s));
			else if (tag.equals("IncSprAlpha"))
				tags.addFrameSetTag(new IncSprAlpha(s));
			else if (tag.equals("IncSprBloomThreshold"))
				tags.addFrameSetTag(new IncSprBloomThreshold(s));
			else if (tag.equals("IncSprColorAdjustBrightness"))
				tags.addFrameSetTag(new IncSprColorAdjustBrightness(s));
			else if (tag.equals("IncSprColorAdjustHue"))
				tags.addFrameSetTag(new IncSprColorAdjustHue(s));
			else if (tag.equals("IncSprColorAdjustSaturation"))
				tags.addFrameSetTag(new IncSprColorAdjustSaturation(s));
			else if (tag.equals("IncSprColorAdjustValues"))
				tags.addFrameSetTag(new IncSprColorAdjustValues(s));
			else if (tag.equals("IncSprColorTintAlpha"))
				tags.addFrameSetTag(new IncSprColorTintAlpha(s));
			else if (tag.equals("IncSprColorTintBlue"))
				tags.addFrameSetTag(new IncSprColorTintBlue(s));
			else if (tag.equals("IncSprColorTintGreen"))
				tags.addFrameSetTag(new IncSprColorTintGreen(s));
			else if (tag.equals("IncSprColorTintRed"))
				tags.addFrameSetTag(new IncSprColorTintRed(s));
			else if (tag.equals("IncSprColorTintValues"))
				tags.addFrameSetTag(new IncSprColorTintValues(s));
			else if (tag.equals("IncSprDropShadowOffsetX"))
				tags.addFrameSetTag(new IncSprDropShadowOffsetX(s));
			else if (tag.equals("IncSprDropShadowOffsetY"))
				tags.addFrameSetTag(new IncSprDropShadowOffsetY(s));
			else if (tag.equals("IncSprDropShadowValues"))
				tags.addFrameSetTag(new IncSprDropShadowValues(s));
			else if (tag.equals("IncSprFlip"))
				tags.addFrameSetTag(new IncSprFlip(s));
			else if (tag.equals("IncSprGaussBlurRadius"))
				tags.addFrameSetTag(new IncSprGaussBlurRadius(s));
			else if (tag.equals("IncSprGlowLevel"))
				tags.addFrameSetTag(new IncSprGlowLevel(s));
			else if (tag.equals("IncSprIndex"))
				tags.addFrameSetTag(new IncSprIndex(s));
			else if (tag.equals("IncSprInnerShadowOffsetX"))
				tags.addFrameSetTag(new IncSprInnerShadowOffsetX(s));
			else if (tag.equals("IncSprInnerShadowOffsetY"))
				tags.addFrameSetTag(new IncSprInnerShadowOffsetY(s));
			else if (tag.equals("IncSprInnerShadowValues"))
				tags.addFrameSetTag(new IncSprInnerShadowValues(s));
			else if (tag.equals("IncSprMotionBlurAngle"))
				tags.addFrameSetTag(new IncSprMotionBlurAngle(s));
			else if (tag.equals("IncSprMotionBlurRadius"))
				tags.addFrameSetTag(new IncSprMotionBlurRadius(s));
			else if (tag.equals("IncSprMotionBlurValues"))
				tags.addFrameSetTag(new IncSprMotionBlurValues(s));
			else if (tag.equals("IncSprRotate"))
				tags.addFrameSetTag(new IncSprRotate(s));
			else if (tag.equals("IncSprSepiaToneLevel"))
				tags.addFrameSetTag(new IncSprSepiaToneLevel(s));
			else if (tag.equals("IncTicksPerFrame"))
				tags.addFrameSetTag(new IncTicksPerFrame(s));
			else if (tag.equals("PlaySound"))
				tags.addFrameSetTag(new PlaySound(s));
			else if (tag.equals("RepeatLastFrame"))
				tags.addFrameSetTag(new RepeatLastFrame(s));
			else if (tag.equals("SetEntityInvencibilityFrames"))
				tags.addFrameSetTag(new SetEntityInvencibilityFrames(s));
			else if (tag.equals("SetEntityNoMove"))
				tags.addFrameSetTag(new SetEntityNoMove(s));
			else if (tag.equals("SetEntityPos"))
				tags.addFrameSetTag(new SetEntityPos(s));
			else if (tag.equals("SetEntityShadow"))
				tags.addFrameSetTag(new SetEntityShadow(s));
			else if (tag.equals("SetEntityX"))
				tags.addFrameSetTag(new SetEntityX(s));
			else if (tag.equals("SetEntityY"))
				tags.addFrameSetTag(new SetEntityY(s));
			else if (tag.equals("SetObjPos"))
				tags.addFrameSetTag(new SetObjPos(s));
			else if (tag.equals("SetObjX"))
				tags.addFrameSetTag(new SetObjX(s));
			else if (tag.equals("SetObjY"))
				tags.addFrameSetTag(new SetObjY(s));
			else if (tag.equals("SetOriginSprHeight"))
				tags.addFrameSetTag(new SetOriginSprHeight(s));
			else if (tag.equals("SetOriginSprPerLine"))
				tags.addFrameSetTag(new SetOriginSprPerLine(s));
			else if (tag.equals("SetOriginSprPos"))
				tags.addFrameSetTag(new SetOriginSprPos(s));
			else if (tag.equals("SetOriginSprSize"))
				tags.addFrameSetTag(new SetOriginSprSize(s));
			else if (tag.equals("SetOriginSprWidth"))
				tags.addFrameSetTag(new SetOriginSprWidth(s));
			else if (tag.equals("SetOriginSprX"))
				tags.addFrameSetTag(new SetOriginSprX(s));
			else if (tag.equals("SetOriginSprY"))
				tags.addFrameSetTag(new SetOriginSprY(s));
			else if (tag.equals("SetOutputSprHeight"))
				tags.addFrameSetTag(new SetOutputSprHeight(s));
			else if (tag.equals("SetOutputSprPos"))
				tags.addFrameSetTag(new SetOutputSprPos(s));
			else if (tag.equals("SetOutputSprSize"))
				tags.addFrameSetTag(new SetOutputSprSize(s));
			else if (tag.equals("SetOutputSprWidth"))
				tags.addFrameSetTag(new SetOutputSprWidth(s));
			else if (tag.equals("SetOutputSprX"))
				tags.addFrameSetTag(new SetOutputSprX(s));
			else if (tag.equals("SetOutputSprY"))
				tags.addFrameSetTag(new SetOutputSprY(s));
			else if (tag.equals("SetSprAlign"))
				tags.addFrameSetTag(new SetSprAlign(s));
			else if (tag.equals("SetSprAlpha"))
				tags.addFrameSetTag(new SetSprAlpha(s));
			else if (tag.equals("SetSprBloomBlendMode"))
				tags.addFrameSetTag(new SetSprBloomBlendMode(s));
			else if (tag.equals("SetSprBloomThreshold"))
				tags.addFrameSetTag(new SetSprBloomThreshold(s));
			else if (tag.equals("SetSprBloomValues"))
				tags.addFrameSetTag(new SetSprBloomValues(s));
			else if (tag.equals("SetSprColorAdjustBlendMode"))
				tags.addFrameSetTag(new SetSprColorAdjustBlendMode(s));
			else if (tag.equals("SetSprColorAdjustBrightness"))
				tags.addFrameSetTag(new SetSprColorAdjustBrightness(s));
			else if (tag.equals("SetSprColorAdjustHue"))
				tags.addFrameSetTag(new SetSprColorAdjustHue(s));
			else if (tag.equals("SetSprColorAdjustSaturation"))
				tags.addFrameSetTag(new SetSprColorAdjustSaturation(s));
			else if (tag.equals("SetSprColorAdjustValues"))
				tags.addFrameSetTag(new SetSprColorAdjustValues(s));
			else if (tag.equals("SetSprColorTintAlpha"))
				tags.addFrameSetTag(new SetSprColorTintAlpha(s));
			else if (tag.equals("SetSprColorTintBlendMode"))
				tags.addFrameSetTag(new SetSprColorTintBlendMode(s));
			else if (tag.equals("SetSprColorTintBlue"))
				tags.addFrameSetTag(new SetSprColorTintBlue(s));
			else if (tag.equals("SetSprColorTintGreen"))
				tags.addFrameSetTag(new SetSprColorTintGreen(s));
			else if (tag.equals("SetSprColorTintRed"))
				tags.addFrameSetTag(new SetSprColorTintRed(s));
			else if (tag.equals("SetSprColorTintValues"))
				tags.addFrameSetTag(new SetSprColorTintValues(s));
			else if (tag.equals("SetSprDropShadowBlendMode"))
				tags.addFrameSetTag(new SetSprDropShadowBlendMode(s));
			else if (tag.equals("SetSprDropShadowOffsetX"))
				tags.addFrameSetTag(new SetSprDropShadowOffsetX(s));
			else if (tag.equals("SetSprDropShadowOffsetY"))
				tags.addFrameSetTag(new SetSprDropShadowOffsetY(s));
			else if (tag.equals("SetSprDropShadowValues"))
				tags.addFrameSetTag(new SetSprDropShadowValues(s));
			else if (tag.equals("SetSprEliticMove"))
				tags.addFrameSetTag(new SetSprEliticMove(s));
			else if (tag.equals("SetSprFlip"))
				tags.addFrameSetTag(new SetSprFlip(s));
			else if (tag.equals("SetSprGaussBlurBlendMode"))
				tags.addFrameSetTag(new SetSprGaussBlurBlendMode(s));
			else if (tag.equals("SetSprGaussBlurRadius"))
				tags.addFrameSetTag(new SetSprGaussBlurRadius(s));
			else if (tag.equals("SetSprGaussBlurValues"))
				tags.addFrameSetTag(new SetSprGaussBlurValues(s));
			else if (tag.equals("SetSprGlowBlendMode"))
				tags.addFrameSetTag(new SetSprGlowBlendMode(s));
			else if (tag.equals("SetSprGlowLevel"))
				tags.addFrameSetTag(new SetSprGlowLevel(s));
			else if (tag.equals("SetSprGlowValues"))
				tags.addFrameSetTag(new SetSprGlowValues(s));
			else if (tag.equals("SetSprIndex"))
				tags.addFrameSetTag(new SetSprIndex(s));
			else if (tag.equals("SetSprInnerShadowBlendMode"))
				tags.addFrameSetTag(new SetSprInnerShadowBlendMode(s));
			else if (tag.equals("SetSprInnerShadowOffsetX"))
				tags.addFrameSetTag(new SetSprInnerShadowOffsetX(s));
			else if (tag.equals("SetSprInnerShadowOffsetY"))
				tags.addFrameSetTag(new SetSprInnerShadowOffsetY(s));
			else if (tag.equals("SetSprInnerShadowValues"))
				tags.addFrameSetTag(new SetSprInnerShadowValues(s));
			else if (tag.equals("SetSprMotionBlurAngle"))
				tags.addFrameSetTag(new SetSprMotionBlurAngle(s));
			else if (tag.equals("SetSprMotionBlurBlendMode"))
				tags.addFrameSetTag(new SetSprMotionBlurBlendMode(s));
			else if (tag.equals("SetSprMotionBlurRadius"))
				tags.addFrameSetTag(new SetSprMotionBlurRadius(s));
			else if (tag.equals("SetSprMotionBlurValues"))
				tags.addFrameSetTag(new SetSprMotionBlurValues(s));
			else if (tag.equals("SetSprRotate"))
				tags.addFrameSetTag(new SetSprRotate(s));
			else if (tag.equals("SetSprSepiaToneBlendMode"))
				tags.addFrameSetTag(new SetSprSepiaToneBlendMode(s));
			else if (tag.equals("SetSprSepiaToneLevel"))
				tags.addFrameSetTag(new SetSprSepiaToneLevel(s));
			else if (tag.equals("SetSprSepiaToneValues"))
				tags.addFrameSetTag(new SetSprSepiaToneValues(s));
			else if (tag.equals("SetSprSource"))
				tags.addFrameSetTag(new SetSprSource(s));
			else if (tag.equals("SetTicksPerFrame"))
				tags.addFrameSetTag(new SetTicksPerFrame(s));
		}
	}

}
