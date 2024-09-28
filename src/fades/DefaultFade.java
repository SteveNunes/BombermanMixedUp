package fades;

import enums.FadeType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class DefaultFade implements Fade {

	private Runnable onFadeDoneEvent;
	private FadeType fadeType;
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
		value = null;
		valueInc = speed;
		fadeType = FadeType.NONE;
	}

	@Override
	public DefaultFade fadeIn() {
		fadeType = FadeType.FADE_IN;
		valueInc = -speed;
		value = 1d;
		return this;
	}

	@Override
	public DefaultFade fadeOut() {
		fadeType = FadeType.FADE_OUT;
		valueInc = speed;
		value = 0d;
		return this;
	}

	@Override
	public boolean isFadeDone()
		{ return valueInc == 0; }

	@Override
	public void stopFade()
		{ value = null; }

	@Override
	public DefaultFade setOnFadeDoneEvent(Runnable runnable) {
		onFadeDoneEvent = runnable;
		return this;
	}

	@Override
	public FadeType getFadeType()
		{ return fadeType; }

	@Override
	public void apply(Canvas canvas) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		if (value != null) {
			gc.setFill(color);
			gc.setGlobalAlpha(value);
			if (valueInc != 0 && (value += valueInc) > 1 || value < 0d) {
				valueInc = 0d;
				value = value > 1d ? 1d : 0d;
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
		valueInc = speed;
	}
	
	public void setColor(Color color) {
		if (color == null)
			throw new RuntimeException("color is null");
		this.color = color;
	}

}
