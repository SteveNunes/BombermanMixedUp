package fades;

import enums.FadeState;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import tools.Draw;

public class PixelizingFade implements Fade {

	private Runnable onFadeDoneEvent;
	private FadeState fadeState;
	private FadeState fadeInitialState;
	private Double speed;
	private Double value;
	private Color color;
	private Integer inc;
	private int backupSize;

	public PixelizingFade() {
		this(Color.WHITE);
	}

	public PixelizingFade(Color color) {
		this(color, 1);
	}

	public PixelizingFade(double speed) {
		this(Color.WHITE, speed);
	}

	public PixelizingFade(Color color, double speed) {
		setColor(color);
		setSpeed(speed);
		reset(FadeState.NONE);
		backupSize = Draw.getOutputPixelSize();
	}

	@Override
	public PixelizingFade fadeIn() {
		reset(FadeState.FADE_IN);
		return this;
	}

	@Override
	public PixelizingFade fadeOut() {
		reset(FadeState.FADE_OUT);
		return this;
	}

	private void reset(FadeState state) {
		fadeState = state;
		fadeInitialState = state;
		value = state == FadeState.FADE_IN ? 100d : 2d;
		inc = state == FadeState.FADE_IN ? -1 : 1;
	}

	@Override
	public boolean isFadeDone() {
		return fadeState == FadeState.DONE;
	}

	@Override
	public void stopFade() {
		fadeState = FadeState.NONE;
		Draw.setOutputPixelSize(backupSize);
	}

	@Override
	public PixelizingFade setOnFadeDoneEvent(Runnable runnable) {
		onFadeDoneEvent = runnable;
		return this;
	}

	@Override
	public FadeState getInitialFadeState() {
		return fadeInitialState;
	}

	@Override
	public FadeState getCurrentFadeState() {
		return fadeState;
	}

	@Override
	public void apply(Canvas canvas) {
		if (value != null) {
			GraphicsContext gc = canvas.getGraphicsContext2D();
			gc.save();
			Draw.setOutputPixelSize(value.intValue());
			gc.setGlobalAlpha((double) value / 100);
			gc.setFill(color);
			gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
			if (!isFadeDone()) {
				if ((value += speed * inc) >= 100 || value <= 2) {
					value = inc == 1 ? 100 : 1d;
					gc.setGlobalAlpha(fadeState == FadeState.FADE_IN ? 1d : 0d);
					fadeState = FadeState.DONE;
					if (onFadeDoneEvent != null)
						onFadeDoneEvent.run();
				}
			}
			gc.restore();
		}
	}

	public void setSpeed(double speed) {
		if (speed < 0.1)
			throw new RuntimeException("speed must be equal or higher than 0.1");
		this.speed = speed;
		reset(fadeInitialState);
	}

	public void setColor(Color color) {
		if (color == null)
			throw new RuntimeException("color is null");
		this.color = color;
	}

}
