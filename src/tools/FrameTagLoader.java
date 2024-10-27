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
				tags.addTag(newTag = new AddColoredLightSpot(s));
			else if (tag.equals("AddColoredLightSpotToSprite"))
				tags.addTag(newTag = new AddColoredLightSpotToSprite(s));
			else if (tag.equals("AddLightSpot"))
				tags.addTag(newTag = new AddLightSpot(s));
			else if (tag.equals("AddLightSpotToSprite"))
				tags.addTag(newTag = new AddLightSpotToSprite(s));
			else if (tag.equals("AddTempColoredLightSpot"))
				tags.addTag(newTag = new AddTempColoredLightSpot(s));
			else if (tag.equals("AddTempColoredLightSpotToSprite"))
				tags.addTag(newTag = new AddTempColoredLightSpotToSprite(s));
			else if (tag.equals("AddTempLightSpot"))
				tags.addTag(newTag = new AddTempLightSpot(s));
			else if (tag.equals("AddTempLightSpotToSprite"))
				tags.addTag(newTag = new AddTempLightSpotToSprite(s));
			else if (tag.equals("DecSprAlign"))
				tags.addTag(newTag = new DecSprAlign(s));
			else if (tag.equals("DecSprFlip"))
				tags.addTag(newTag = new DecSprFlip(s));
			else if (tag.equals("DoJump"))
				tags.addTag(newTag = new DoJump(s));
			else if (tag.equals("Goto"))
				tags.addTag(newTag = new Goto(s));
			else if (tag.equals("IncEntityPos"))
				tags.addTag(newTag = new IncEntityPos(s));
			else if (tag.equals("IncEntityX"))
				tags.addTag(newTag = new IncEntityX(s));
			else if (tag.equals("IncEntityY"))
				tags.addTag(newTag = new IncEntityY(s));
			else if (tag.equals("IncObjPos"))
				tags.addTag(newTag = new IncObjPos(s));
			else if (tag.equals("IncObjX"))
				tags.addTag(newTag = new IncObjX(s));
			else if (tag.equals("IncObjY"))
				tags.addTag(newTag = new IncObjY(s));
			else if (tag.equals("IncOriginSprHeight"))
				tags.addTag(newTag = new IncOriginSprHeight(s));
			else if (tag.equals("IncOriginSprPerLine"))
				tags.addTag(newTag = new IncOriginSprPerLine(s));
			else if (tag.equals("IncOriginSprPos"))
				tags.addTag(newTag = new IncOriginSprPos(s));
			else if (tag.equals("IncOriginSprSize"))
				tags.addTag(newTag = new IncOriginSprSize(s));
			else if (tag.equals("IncOriginSprWidth"))
				tags.addTag(newTag = new IncOriginSprWidth(s));
			else if (tag.equals("IncOriginSprX"))
				tags.addTag(newTag = new IncOriginSprX(s));
			else if (tag.equals("IncOriginSprY"))
				tags.addTag(newTag = new IncOriginSprY(s));
			else if (tag.equals("IncOutputSprHeight"))
				tags.addTag(newTag = new IncOutputSprHeight(s));
			else if (tag.equals("IncOutputSprPos"))
				tags.addTag(newTag = new IncOutputSprPos(s));
			else if (tag.equals("IncOutputSprSize"))
				tags.addTag(newTag = new IncOutputSprSize(s));
			else if (tag.equals("IncOutputSprWidth"))
				tags.addTag(newTag = new IncOutputSprWidth(s));
			else if (tag.equals("IncOutputSprX"))
				tags.addTag(newTag = new IncOutputSprX(s));
			else if (tag.equals("IncOutputSprY"))
				tags.addTag(newTag = new IncOutputSprY(s));
			else if (tag.equals("IncSprAlign"))
				tags.addTag(newTag = new IncSprAlign(s));
			else if (tag.equals("IncSprAlpha"))
				tags.addTag(newTag = new IncSprAlpha(s));
			else if (tag.equals("IncSprBloomThreshold"))
				tags.addTag(newTag = new IncSprBloomThreshold(s));
			else if (tag.equals("IncSprColorAdjustBrightness"))
				tags.addTag(newTag = new IncSprColorAdjustBrightness(s));
			else if (tag.equals("IncSprColorAdjustHue"))
				tags.addTag(newTag = new IncSprColorAdjustHue(s));
			else if (tag.equals("IncSprColorAdjustSaturation"))
				tags.addTag(newTag = new IncSprColorAdjustSaturation(s));
			else if (tag.equals("IncSprColorAdjustValues"))
				tags.addTag(newTag = new IncSprColorAdjustValues(s));
			else if (tag.equals("IncSprColorTintAlpha"))
				tags.addTag(newTag = new IncSprColorTintAlpha(s));
			else if (tag.equals("IncSprColorTintBlue"))
				tags.addTag(newTag = new IncSprColorTintBlue(s));
			else if (tag.equals("IncSprColorTintGreen"))
				tags.addTag(newTag = new IncSprColorTintGreen(s));
			else if (tag.equals("IncSprColorTintRed"))
				tags.addTag(newTag = new IncSprColorTintRed(s));
			else if (tag.equals("IncSprColorTintValues"))
				tags.addTag(newTag = new IncSprColorTintValues(s));
			else if (tag.equals("IncSprDropShadowOffsetX"))
				tags.addTag(newTag = new IncSprDropShadowOffsetX(s));
			else if (tag.equals("IncSprDropShadowOffsetY"))
				tags.addTag(newTag = new IncSprDropShadowOffsetY(s));
			else if (tag.equals("IncSprDropShadowValues"))
				tags.addTag(newTag = new IncSprDropShadowValues(s));
			else if (tag.equals("IncSprFlip"))
				tags.addTag(newTag = new IncSprFlip(s));
			else if (tag.equals("IncSprGaussBlurRadius"))
				tags.addTag(newTag = new IncSprGaussBlurRadius(s));
			else if (tag.equals("IncSprGlowLevel"))
				tags.addTag(newTag = new IncSprGlowLevel(s));
			else if (tag.equals("IncSprIndex"))
				tags.addTag(newTag = new IncSprIndex(s));
			else if (tag.equals("DecSprIndex"))
				tags.addTag(newTag = new DecSprIndex(s));
			else if (tag.equals("IncSprInnerShadowOffsetX"))
				tags.addTag(newTag = new IncSprInnerShadowOffsetX(s));
			else if (tag.equals("IncSprInnerShadowOffsetY"))
				tags.addTag(newTag = new IncSprInnerShadowOffsetY(s));
			else if (tag.equals("IncSprInnerShadowValues"))
				tags.addTag(newTag = new IncSprInnerShadowValues(s));
			else if (tag.equals("IncSprMotionBlurAngle"))
				tags.addTag(newTag = new IncSprMotionBlurAngle(s));
			else if (tag.equals("IncSprMotionBlurRadius"))
				tags.addTag(newTag = new IncSprMotionBlurRadius(s));
			else if (tag.equals("IncSprMotionBlurValues"))
				tags.addTag(newTag = new IncSprMotionBlurValues(s));
			else if (tag.equals("IncSprRotate"))
				tags.addTag(newTag = new IncSprRotate(s));
			else if (tag.equals("IncSprSepiaToneLevel"))
				tags.addTag(newTag = new IncSprSepiaToneLevel(s));
			else if (tag.equals("IncTicksPerFrame"))
				tags.addTag(newTag = new IncTicksPerFrame(s));
			else if (tag.equals("PlayMp3"))
				tags.addTag(newTag = new PlayMp3(s));
			else if (tag.equals("PlayWav"))
				tags.addTag(newTag = new PlayWav(s));
			else if (tag.equals("RepeatLastFrame"))
				tags.addTag(newTag = new RepeatLastFrame(s));
			else if (tag.equals("RunEffectAt"))
				tags.addTag(newTag = new RunEffectAt(s));
			else if (tag.equals("RunEffectAtEntity"))
				tags.addTag(newTag = new RunEffectAtEntity(s));
			else if (tag.equals("RunEffectAtSprite"))
				tags.addTag(newTag = new RunEffectAtSprite(s));
			else if (tag.equals("RunEffectAtTile"))
				tags.addTag(newTag = new RunEffectAtTile(s));
			else if (tag.equals("SetEntityNoMove"))
				tags.addTag(newTag = new SetEntityNoMove(s));
			else if (tag.equals("SetEntityPos"))
				tags.addTag(newTag = new SetEntityPos(s));
			else if (tag.equals("SetEntityShadow"))
				tags.addTag(newTag = new SetEntityShadow(s));
			else if (tag.equals("SetEntityX"))
				tags.addTag(newTag = new SetEntityX(s));
			else if (tag.equals("SetEntityY"))
				tags.addTag(newTag = new SetEntityY(s));
			else if (tag.equals("SetFrameSet"))
				tags.addTag(newTag = new SetFrameSet(s));
			else if (tag.equals("SetObjPos"))
				tags.addTag(newTag = new SetObjPos(s));
			else if (tag.equals("SetObjX"))
				tags.addTag(newTag = new SetObjX(s));
			else if (tag.equals("SetObjY"))
				tags.addTag(newTag = new SetObjY(s));
			else if (tag.equals("SetOriginSprHeight"))
				tags.addTag(newTag = new SetOriginSprHeight(s));
			else if (tag.equals("SetOriginSprPerLine"))
				tags.addTag(newTag = new SetOriginSprPerLine(s));
			else if (tag.equals("SetOriginSprPos"))
				tags.addTag(newTag = new SetOriginSprPos(s));
			else if (tag.equals("SetOriginSprSize"))
				tags.addTag(newTag = new SetOriginSprSize(s));
			else if (tag.equals("SetOriginSprWidth"))
				tags.addTag(newTag = new SetOriginSprWidth(s));
			else if (tag.equals("SetOriginSprX"))
				tags.addTag(newTag = new SetOriginSprX(s));
			else if (tag.equals("SetOriginSprY"))
				tags.addTag(newTag = new SetOriginSprY(s));
			else if (tag.equals("SetOutputSprHeight"))
				tags.addTag(newTag = new SetOutputSprHeight(s));
			else if (tag.equals("SetOutputSprPos"))
				tags.addTag(newTag = new SetOutputSprPos(s));
			else if (tag.equals("SetOutputSprSize"))
				tags.addTag(newTag = new SetOutputSprSize(s));
			else if (tag.equals("SetOutputSprWidth"))
				tags.addTag(newTag = new SetOutputSprWidth(s));
			else if (tag.equals("SetOutputSprX"))
				tags.addTag(newTag = new SetOutputSprX(s));
			else if (tag.equals("SetOutputSprY"))
				tags.addTag(newTag = new SetOutputSprY(s));
			else if (tag.equals("SetSprAlign"))
				tags.addTag(newTag = new SetSprAlign(s));
			else if (tag.equals("SetSprAlpha"))
				tags.addTag(newTag = new SetSprAlpha(s));
			else if (tag.equals("SetSprBloomBlendMode"))
				tags.addTag(newTag = new SetSprBloomBlendMode(s));
			else if (tag.equals("SetSprBloomThreshold"))
				tags.addTag(newTag = new SetSprBloomThreshold(s));
			else if (tag.equals("SetSprBloomValues"))
				tags.addTag(newTag = new SetSprBloomValues(s));
			else if (tag.equals("SetSprColorAdjustBlendMode"))
				tags.addTag(newTag = new SetSprColorAdjustBlendMode(s));
			else if (tag.equals("SetSprColorAdjustBrightness"))
				tags.addTag(newTag = new SetSprColorAdjustBrightness(s));
			else if (tag.equals("SetSprColorAdjustHue"))
				tags.addTag(newTag = new SetSprColorAdjustHue(s));
			else if (tag.equals("SetSprColorAdjustSaturation"))
				tags.addTag(newTag = new SetSprColorAdjustSaturation(s));
			else if (tag.equals("SetSprColorAdjustValues"))
				tags.addTag(newTag = new SetSprColorAdjustValues(s));
			else if (tag.equals("SetSprColorTintAlpha"))
				tags.addTag(newTag = new SetSprColorTintAlpha(s));
			else if (tag.equals("SetSprColorTintBlendMode"))
				tags.addTag(newTag = new SetSprColorTintBlendMode(s));
			else if (tag.equals("SetSprColorTintBlue"))
				tags.addTag(newTag = new SetSprColorTintBlue(s));
			else if (tag.equals("SetSprColorTintGreen"))
				tags.addTag(newTag = new SetSprColorTintGreen(s));
			else if (tag.equals("SetSprColorTintRed"))
				tags.addTag(newTag = new SetSprColorTintRed(s));
			else if (tag.equals("SetSprColorTintValues"))
				tags.addTag(newTag = new SetSprColorTintValues(s));
			else if (tag.equals("SetSprDropShadowBlendMode"))
				tags.addTag(newTag = new SetSprDropShadowBlendMode(s));
			else if (tag.equals("SetSprDropShadowOffsetX"))
				tags.addTag(newTag = new SetSprDropShadowOffsetX(s));
			else if (tag.equals("SetSprDropShadowOffsetY"))
				tags.addTag(newTag = new SetSprDropShadowOffsetY(s));
			else if (tag.equals("SetSprDropShadowValues"))
				tags.addTag(newTag = new SetSprDropShadowValues(s));
			else if (tag.equals("SetSprEliticMove"))
				tags.addTag(newTag = new SetSprEliticMove(s));
			else if (tag.equals("SetSprFlip"))
				tags.addTag(newTag = new SetSprFlip(s));
			else if (tag.equals("SetSprGaussBlurBlendMode"))
				tags.addTag(newTag = new SetSprGaussBlurBlendMode(s));
			else if (tag.equals("SetSprGaussBlurRadius"))
				tags.addTag(newTag = new SetSprGaussBlurRadius(s));
			else if (tag.equals("SetSprGaussBlurValues"))
				tags.addTag(newTag = new SetSprGaussBlurValues(s));
			else if (tag.equals("SetSprGlowBlendMode"))
				tags.addTag(newTag = new SetSprGlowBlendMode(s));
			else if (tag.equals("SetSprGlowLevel"))
				tags.addTag(newTag = new SetSprGlowLevel(s));
			else if (tag.equals("SetSprGlowValues"))
				tags.addTag(newTag = new SetSprGlowValues(s));
			else if (tag.equals("SetSprIndex"))
				tags.addTag(newTag = new SetSprIndex(s));
			else if (tag.equals("SetSprInnerShadowBlendMode"))
				tags.addTag(newTag = new SetSprInnerShadowBlendMode(s));
			else if (tag.equals("SetSprInnerShadowOffsetX"))
				tags.addTag(newTag = new SetSprInnerShadowOffsetX(s));
			else if (tag.equals("SetSprInnerShadowOffsetY"))
				tags.addTag(newTag = new SetSprInnerShadowOffsetY(s));
			else if (tag.equals("SetSprInnerShadowValues"))
				tags.addTag(newTag = new SetSprInnerShadowValues(s));
			else if (tag.equals("SetSprMotionBlurAngle"))
				tags.addTag(newTag = new SetSprMotionBlurAngle(s));
			else if (tag.equals("SetSprMotionBlurBlendMode"))
				tags.addTag(newTag = new SetSprMotionBlurBlendMode(s));
			else if (tag.equals("SetSprMotionBlurRadius"))
				tags.addTag(newTag = new SetSprMotionBlurRadius(s));
			else if (tag.equals("SetSprMotionBlurValues"))
				tags.addTag(newTag = new SetSprMotionBlurValues(s));
			else if (tag.equals("SetSprRotate"))
				tags.addTag(newTag = new SetSprRotate(s));
			else if (tag.equals("SetSprSepiaToneBlendMode"))
				tags.addTag(newTag = new SetSprSepiaToneBlendMode(s));
			else if (tag.equals("SetSprSepiaToneLevel"))
				tags.addTag(newTag = new SetSprSepiaToneLevel(s));
			else if (tag.equals("SetSprSepiaToneValues"))
				tags.addTag(newTag = new SetSprSepiaToneValues(s));
			else if (tag.equals("SetSprSource"))
				tags.addTag(newTag = new SetSprSource(s));
			else if (tag.equals("SetTicksPerFrame"))
				tags.addTag(newTag = new SetTicksPerFrame(s));
			else if (tag.equals("SetSprWaving"))
				tags.addTag(newTag = new SetSprWaving(s));
			else if (tag.equals("SetSprScrolling"))
				tags.addTag(newTag = new SetSprScrolling(s));
			else if (tag.equals("SetSwitchValue"))
				tags.addTag(newTag = new SetSwitchValue(s));
			else if (tag.equals("DecSwitchValue"))
				tags.addTag(newTag = new DecSwitchValue(s));
			else if (tag.equals("IncSwitchValue"))
				tags.addTag(newTag = new IncSwitchValue(s));
			else if (tag.equals("SetMapLayerIndex"))
				tags.addTag(newTag = new SetMapLayerIndex(s));
			else if (tag.equals("SetMapFrameSet"))
				tags.addTag(newTag = new SetMapFrameSet(s));
			else if (tag.equals("DisableTileTags"))
				tags.addTag(newTag = new DisableTileTags(s));
			else if (tag.equals("EnableTileTags"))
				tags.addTag(newTag = new EnableTileTags(s));
			else if (tag.equals("CopySprFromCopyLayer"))
				tags.addTag(newTag = new CopySprFromCopyLayer(s));
			else if (tag.equals("AddTileProp"))
				tags.addTag(newTag = new AddTileProp(s));
			else if (tag.equals("RemoveTileProp"))
				tags.addTag(newTag = new RemoveTileProp(s));
			else if (tag.equals("DelayTags"))
				tags.addTag(newTag = new DelayTags(s));
			else if (tag.equals("RunStageTags"))
				tags.addTag(newTag = new RunStageTags(s));
			else if (tag.equals("DisableEntity"))
				tags.addTag(newTag = new DisableEntity(s));
			else if (tag.equals("MoveEntity"))
				tags.addTag(newTag = new MoveEntity(s));
			else if (tag.equals("SetBlockedMovement"))
				tags.addTag(newTag = new SetBlockedMovement(s));
			else if (tag.equals("SetEntitySpeed"))
				tags.addTag(newTag = new SetEntitySpeed(s));
			else if (tag.equals("SetEntityTempSpeed"))
				tags.addTag(newTag = new SetEntityTempSpeed(s));
			else if (tag.equals("PushEntity"))
				tags.addTag(newTag = new PushEntity(s));
			else if (tag.equals("PushEntityFromTile"))
				tags.addTag(newTag = new PushEntityFromTile(s));
			else if (tag.equals("PushPlayerFromTile"))
				tags.addTag(newTag = new PushPlayerFromTile(s));
			else if (tag.equals("PushBombFromTile"))
				tags.addTag(newTag = new PushBombFromTile(s));
			else if (tag.equals("SetTileTags"))
				tags.addTag(newTag = new SetTileTags(s));
			else if (tag.equals("ClearTileTags"))
				tags.addTag(newTag = new ClearTileTags(s));
			else if (tag.equals("SetBombStucked"))
				tags.addTag(newTag = new SetBombStucked(s));
			else if (tag.equals("SetInvencibleFrames"))
				tags.addTag(newTag = new SetInvencibleFrames(s));
			else if (tag.equals("SetBlinkingFrames"))
				tags.addTag(newTag = new SetBlinkingFrames(s));
			else if (tag.equals("ExplodeBomb"))
				tags.addTag(newTag = new ExplodeBomb(s));
			else if (tag.equals("ShakeSprite"))
				tags.addTag(newTag = new ShakeSprite(s));
			else if (tag.equals("ShakeFrameSet"))
				tags.addTag(newTag = new ShakeFrameSet(s));
			if (delay > 0)
				newTag.setTriggerDelay(delay);
		}
	}

}
