package frameset_tags;

import java.awt.Rectangle;

import enums.DamageType;
import enums.ForceDirection;
import frameset.Sprite;
import util.Misc;

public class SetDamageBox extends FrameTag {

	public DamageType damageType;
	public String triggerTargetFrameSet;
	public ForceDirection forceTargetDirection;
	public Rectangle damageRectangle; // offsetX, offsetY, width, height
	public Integer variant; // total de itens a tirar ou qualquer outro parametro complementar ao tipo de dano
	public String soundWhenHits;

	public SetDamageBox(DamageType damageType, Integer variant, String triggerTargetFrameSet, ForceDirection forceTargetDirection, Rectangle damageRectangle, String soundWhenHits) {
		this.damageType = damageType;
		this.variant = variant;
		this.triggerTargetFrameSet = triggerTargetFrameSet;
		this.forceTargetDirection = forceTargetDirection;
		this.damageRectangle = new Rectangle(damageRectangle);
		this.soundWhenHits = soundWhenHits;
	}

	public SetDamageBox(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags);
		if (params.length > 9)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 2)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			damageType = params[n].equals("-") ? null : DamageType.valueOf(params[n = 0]);
			variant = params[n = 1].equals("-") ? null : Integer.parseInt(params[n]);
			triggerTargetFrameSet = params.length <= (n = 2) || params[n].equals("-") ? null : params[n];
			forceTargetDirection = params.length <= (n = 3) || params[n].equals("-") ? null : ForceDirection.valueOf(params[n]);
			damageRectangle = new Rectangle(
					params.length <= (n = 4) || params[n].equals("-") ? 0 : Integer.parseInt(params[n]),
					params.length <= (n = 5) || params[n].equals("-") ? 0 :Integer.parseInt(params[n]),
					params.length <= (n = 6) || params[n].equals("-") ? -1 :Integer.parseInt(params[n]),
					params.length <= (n = 7) || params[n].equals("-") ? -1 :Integer.parseInt(params[n]));
			soundWhenHits = params.length <= (n = 8) || params[n].equals("-") ? null : params[n];
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[n] + " - Invalid parameter");
		}
	}

	@Override
	public SetDamageBox getNewInstanceOfThis() {
		return new SetDamageBox(damageType, variant, triggerTargetFrameSet, forceTargetDirection, damageRectangle, soundWhenHits);
	}

	@Override
	public void process(Sprite sprite) {
		if (damageRectangle.getWidth() == -1)
			damageRectangle.setSize(sprite.getOutputWidth(), (int)damageRectangle.getHeight());
		if (damageRectangle.getHeight() == -1)
			damageRectangle.setSize((int)damageRectangle.getHeight(), sprite.getOutputHeight());
		sprite.setDamageBox(damageType, variant, triggerTargetFrameSet, forceTargetDirection, damageRectangle, soundWhenHits);
	}

}
