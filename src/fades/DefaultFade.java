package fades;

import enums.FadeState;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class DefaultFade implements Fade {

	private Runnable onFadeDoneEvent;
	private FadeState fadeState;
	private FadeState fadeInitialState;
	private Double value;
	private Double speed;
	private Double valueInc;
	private Color color;
	
	public DefaultFade()
		{ this(Color.BLACK); }

	public DefaultFade(Color color)
		{ this(color, 0.02); }
	
	public DefaultFade(double speed)
		{ this(Color.BLACK, speed); }

	public DefaultFade(Color color, double speed) {
		setColor(color);
		setSpeed(speed);
		reset(FadeState.NONE);
	}

	@Override
	public DefaultFade fadeIn() {
		reset(FadeState.FADE_IN);
		return this;
	}

	@Override
	public DefaultFade fadeOut() {
		reset(FadeState.FADE_OUT);
		return this;
	}
	
	private void reset(FadeState state) {
		fadeState = state;
		fadeInitialState = state;
		valueInc = state == FadeState.FADE_IN ? -speed : speed;
		value = state == FadeState.FADE_IN ? 1d : 0d;
	}

	@Override
	public boolean isFadeDone()
		{ return fadeState == FadeState.DONE; }

	@Override
	public void stopFade()
		{ fadeState = FadeState.NONE; }

	@Override
	public DefaultFade setOnFadeDoneEvent(Runnable runnable) {
		onFadeDoneEvent = runnable;
		return this;
	}

	@Override
	public FadeState getInitialFadeState()
		{ return fadeInitialState; }

	@Override
	public FadeState getCurrentFadeState()
		{ return fadeState; }

	@Override
	public void apply(Canvas canvas) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		if (fadeState != FadeState.NONE) {
			gc.setFill(color);
			gc.setGlobalAlpha(value);
			if (fadeState != FadeState.DONE && ((value += valueInc) > 1 || value < 0d)) {
				fadeState = FadeState.DONE;
				value = fadeState == FadeState.FADE_IN ? 0d : 1d;
				if (onFadeDoneEvent != null)
					onFadeDoneEvent.run();
			}
			gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		}
	}

	public void setSpeed(double speed) {
		if (speed < 0.001 || speed > 1)
			throw new RuntimeException("speed must be between 0.001 and 1.0");
		this.speed = speed;
		reset(fadeInitialState);
	}
	
	public void setColor(Color color) {
		if (color == null)
			throw new RuntimeException("color is null");
		this.color = color;
	}

}
