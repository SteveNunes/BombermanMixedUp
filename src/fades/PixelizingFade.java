package fades;

import enums.FadeType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import tools.Tools;

public class PixelizingFade implements Fade {

	private Runnable onFadeDoneEvent;
	private FadeType fadeType;
	private Double speed;
	private Double value;
	private Color color;
	private Integer inc;
	private int backupSize;
	
	public PixelizingFade()
		{ this(Color.WHITE); }

	public PixelizingFade(Color color)
		{ this(color, 1); }
	
	public PixelizingFade(double speed)
		{ this(Color.WHITE, speed); }

	public PixelizingFade(Color color, double speed) {
		setColor(color);
		setSpeed(speed);
		reset();
	}
	
	private void reset() {
		fadeType = FadeType.NONE;
		backupSize = Tools.getOutputPixelSize();
		value = null;
		inc = null;
	}

	@Override
	public PixelizingFade fadeIn() {
		reset();
		fadeType = FadeType.FADE_IN;
		inc = -1;
		value = 100d;
		return this;
	}

	@Override
	public PixelizingFade fadeOut() {
		reset();
		fadeType = FadeType.FADE_OUT;
		inc = 1;
		value = 2d;
		return this;
	}

	@Override
	public boolean isFadeDone()
		{ return inc == null; }

	@Override
	public void stopFade() {
		inc = null;
		Tools.setOutputPixelSize(backupSize);
	}

	@Override
	public PixelizingFade setOnFadeDoneEvent(Runnable runnable) {
		onFadeDoneEvent = runnable;
		return this;
	}

	@Override
	public FadeType getFadeType()
		{ return fadeType; }

	@Override
	public void apply(Canvas canvas) {
		if (value != null) {
			GraphicsContext gc = canvas.getGraphicsContext2D();
			gc.save();
			Tools.setOutputPixelSize(value.intValue());
			gc.setGlobalAlpha((double)value / 100);
			gc.setFill(color);
			gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
			if (!isFadeDone()) {
				if ((value += speed * inc) >= 100 || value <= 2) {
					value = inc == 1 ? 100 : 1d;
					gc.setGlobalAlpha(inc == 1 ? 1d : 0d);
					inc = null;
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
	}
	
	public void setColor(Color color) {
		if (color == null)
			throw new RuntimeException("color is null");
		this.color = color;
	}

}
