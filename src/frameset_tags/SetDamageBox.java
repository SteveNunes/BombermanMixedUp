package frameset_tags;

import java.awt.Rectangle;

import enums.DamageType;
import frameset.Sprite;

public class SetDamageBox extends FrameTag {

	public DamageType damageType;
	public Rectangle damageRectangle; // offsetX, offsetY, width, height
	public int variant; // total de itens a tirar

	public SetDamageBox(DamageType damageType, Rectangle damageRectangle, int variant) {
		this.damageType = damageType;
		this.damageRectangle = new Rectangle(damageRectangle);
		this.variant = variant;
		this.deleteMeAfterFirstRead = true;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + damageType.name() + ";" + damageRectangle.getX() + ";" + damageRectangle.getY() + ";" + damageRectangle.getWidth() + ";" + damageRectangle.getHeight() + ";" + variant + "}";
	}

	public SetDamageBox(String tags) {
		String[] params = validateStringTags(this, tags);
		if (params.length > 6)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 1)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			damageType = DamageType.valueOf(params[n = 0]);
			if (params.length >= 5) {
				damageRectangle = new Rectangle(
						params[n = 1].equals("-") ? 0 : Integer.parseInt(params[n]),
						params[n = 2].equals("-") ? 0 :Integer.parseInt(params[n]),
						params[n = 3].equals("-") ? -1 :Integer.parseInt(params[n]),
						params[n = 4].equals("-") ? -1 :Integer.parseInt(params[n]));
				variant = params.length < 6 ? 0 : Integer.parseInt(params[n = 5]);
			}
			else if (params.length >= 3) {
				damageRectangle = new Rectangle(0, 0,
						params[n = 1].equals("-") ? -1 : Integer.parseInt(params[n]),
						params[n = 2].equals("-") ? -1 :Integer.parseInt(params[n]));
				variant = params.length < 4 ? 0 : Integer.parseInt(params[n = 3]);
			}
			else {
				damageRectangle = new Rectangle(0, 0, -1, -1);
				variant = params.length < 2 ? 0 : Integer.parseInt(params[n = 1]);
			}
		}
		catch (Exception e) {
			throw new RuntimeException(params[n] + " - Invalid parameter");
		}
	}

	@Override
	public SetDamageBox getNewInstanceOfThis() {
		return new SetDamageBox(damageType, damageRectangle, variant);
	}

	@Override
	public void process(Sprite sprite) {
		if (damageRectangle.getWidth() == -1)
			damageRectangle.setSize(sprite.getOutputWidth(), (int)damageRectangle.getHeight());
		if (damageRectangle.getHeight() == -1)
			damageRectangle.setSize((int)damageRectangle.getHeight(), sprite.getOutputHeight());
		sprite.setDamageBox(damageType, damageRectangle, variant);
	}

}
