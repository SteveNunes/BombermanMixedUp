package fades;

import enums.ClosingFadeShape;
import enums.FadeState;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import objmoveutils.Position;

public class ClosingCircleFade implements Fade {

	private Runnable onFadeDoneEvent;
	private FadeState fadeState;
	private FadeState fadeInitialState;
	private ClosingFadeShape closingFadeShape;
	private Position center;
	private Double radius;
	private Double speed;
	private Double valueInc;
	private Color color;
	private WritableImage mask;

	public ClosingCircleFade(int centerX, int centerY) {
		this(Color.BLACK, centerX, centerY);
	}

	public ClosingCircleFade(Color color, int centerX, int centerY) {
		this(color, centerX, centerY, 1);
	}

	public ClosingCircleFade(int centerX, int centerY, double speed) {
		this(Color.BLACK, centerX, centerY, speed);
	}

	public ClosingCircleFade(Color color, int centerX, int centerY, double speed) {
		this(color, new Position(centerX, centerY), speed);
	}

	public ClosingCircleFade(Position center) {
		this(Color.BLACK, center);
	}

	public ClosingCircleFade(Color color, Position center) {
		this(color, center, 1);
	}

	public ClosingCircleFade(Position center, double speed) {
		this(Color.BLACK, center, speed);
	}

	public ClosingCircleFade(Color color, Position center, double speed) {
		setColor(color);
		setSpeed(speed);
		setPosition(center);
		reset(FadeState.NONE);
		closingFadeShape = ClosingFadeShape.SQUARE;
	}

	private void setPosition(Position center) {
		this.center = center;
	}

	public ClosingFadeShape getClosingFadeShape() {
		return closingFadeShape;
	}

	public ClosingCircleFade setStyle(ClosingFadeShape closingFadeShape) {
		this.closingFadeShape = closingFadeShape;
		return this;
	}

	@Override
	public ClosingCircleFade fadeIn() {
		reset(FadeState.FADE_IN);
		return this;
	}

	@Override
	public ClosingCircleFade fadeOut() {
		reset(FadeState.FADE_OUT);
		return this;
	}

	private void reset(FadeState state) {
		fadeState = state;
		fadeInitialState = state;
		valueInc = state == FadeState.FADE_IN ? speed : state == FadeState.FADE_OUT ? -speed : 0;
		mask = null;
		radius = null;
	}

	@Override
	public ClosingCircleFade setOnFadeDoneEvent(Runnable runnable) {
		onFadeDoneEvent = runnable;
		return this;
	}

	@Override
	public boolean isFadeDone() {
		return fadeState == FadeState.DONE;
	}

	@Override
	public void stopFade() {
		fadeState = FadeState.NONE;
	}

	@Override
	public FadeState getInitialFadeState() {
		return fadeInitialState;
	}

	@Override
	public FadeState getCurrentFadeState() {
		return fadeState;
	}

	private double getMaxRadius(Canvas canvas) {
		int w = (int) canvas.getWidth() / 3, h = (int) canvas.getHeight() / 3, cx = (int) center.getX(), cy = (int) center.getY();
		double rMax = 1, vInc = speed;
		if (closingFadeShape == ClosingFadeShape.CIRCLE) {
			for (boolean ok = false; !ok; rMax += vInc) {
				ok = true;
				for (int y = 0; ok && y < h; y++)
					for (int x = 0; ok && x < w; x++) {
						double dx = x - cx, dy = y - cy, distance = Math.sqrt(dx * dx + dy * dy);
						if (distance > rMax)
							ok = false;
					}
				valueInc *= 1.005;
			}
		}
		else {
			for (boolean ok = false; !ok; rMax += vInc) {
				ok = true;
				if (cx - rMax / 2 > 0 || cx + rMax < w || cy - rMax / 2 > 0 || cy + rMax < h) {
					ok = false;
					valueInc *= 1.005;
				}
			}
			rMax -= vInc * 2;
		}
		return rMax;
	}

	@Override
	public void apply(Canvas canvas) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		if (fadeState != FadeState.NONE) {
			int w = (int) canvas.getWidth() / 3, h = (int) canvas.getHeight() / 3, cx = (int) center.getX(), cy = (int) center.getY();
			if (mask == null) {
				mask = new WritableImage(w, h);
				radius = fadeState == FadeState.FADE_IN ? 0 : getMaxRadius(canvas);
			}
			boolean done = true;
			for (int y = 0; y < h; y++)
				for (int x = 0; x < w; x++) {
					double dx = x - cx, dy = y - cy, distance = Math.sqrt(dx * dx + dy * dy);

					boolean b = (fadeState == FadeState.DONE && fadeInitialState == FadeState.FADE_IN) || (fadeState != FadeState.DONE && ((closingFadeShape == ClosingFadeShape.CIRCLE && radius > 0 && distance <= radius) || (closingFadeShape == ClosingFadeShape.SQUARE && radius > 0 && x >= cx - radius / 2 && x <= cx + radius / 2 && y >= cy - radius / 2 && y <= cy + radius / 2)));
					if ((fadeState == FadeState.FADE_IN && !b) || (fadeState == FadeState.FADE_OUT && (radius > 0 || b)))
						done = false;
					mask.getPixelWriter().setColor(x, y, b ? Color.TRANSPARENT : color);
				}
			gc.drawImage(mask, 0, 0, w, h, 0, 0, w * 3, h * 3);
			if (fadeState != FadeState.DONE) {
				radius += valueInc;
				if (fadeState == FadeState.FADE_IN)
					valueInc *= 1.01;
				else
					valueInc /= 1.01;

				if (done) {
					radius = fadeState == FadeState.FADE_IN ? radius : 0d;
					valueInc = 0d;
					fadeState = FadeState.DONE;
					if (onFadeDoneEvent != null)
						onFadeDoneEvent.run();
				}
			}
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
