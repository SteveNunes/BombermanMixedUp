package frameset_tags;

import enums.FadeState;
import fades.DefaultFade;
import frameset.Sprite;
import javafx.scene.paint.Color;
import maps.MapSet;
import tools.Draw;

public class SetDefaultFade extends FrameTag {

	public Color color;
	public double speed;
	public FadeState fadeState;
	public String stageTagEventAfterFade;

	public SetDefaultFade(Color color, double speed, FadeState fadeState, String stageTagEventAfterFade) {
		this.color = color;
		this.speed = speed;
		this.fadeState = fadeState;
		this.stageTagEventAfterFade = stageTagEventAfterFade;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + color.toString() + ";" + speed + ";" + fadeState.name() + ";" + stageTagEventAfterFade + "}";
	}

	public SetDefaultFade(String tags) {
		String[] params = validateStringTags(this, tags, 4);
		if (params.length > 4)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 3)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			color = Color.valueOf(params[n++]);
			speed = Double.parseDouble(params[n++]);
			fadeState = FadeState.valueOf(params[n++]);
			stageTagEventAfterFade = n >= params.length ? "-" : params[n]; 
		}
		catch (Exception e) {
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public SetDefaultFade getNewInstanceOfThis() {
		return new SetDefaultFade(color, speed, fadeState, stageTagEventAfterFade);
	}

	@Override
	public void process(Sprite sprite) {
		DefaultFade fade = new DefaultFade(speed);
		if (!stageTagEventAfterFade.equals("-"))
			fade.setOnFadeDoneEvent(() -> MapSet.runStageTag(stageTagEventAfterFade));
		fade.setColor(color);
		fade.setSpeed(speed);
		if (fadeState == FadeState.FADE_IN)
			fade.fadeIn();
		else
			fade.fadeOut();
		Draw.setFade(fade);
	}

}
