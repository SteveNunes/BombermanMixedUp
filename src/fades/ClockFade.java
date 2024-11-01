package fades;

import enums.Direction;
import enums.FadeState;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

public class ClockFade implements Fade {

	private Runnable onFadeDoneEvent;
	private FadeState fadeState;
	private FadeState fadeInitialState;
	private Integer arc;
	private Integer speed;
	private Integer inc;
	private Color color;

	public ClockFade() {
		this(Color.BLACK);
	}

	public ClockFade(Direction closingDirection) {
		this(Color.BLACK, closingDirection, 1);
	}

	public ClockFade(Color color) {
		this(color, Direction.DOWN, 1);
	}

	public ClockFade(int speed) {
		this(Color.BLACK, Direction.DOWN, speed);
	}

	public ClockFade(Color color, int speed) {
		this(color, Direction.DOWN, speed);
	}

	public ClockFade(Direction closingDirection, int speed) {
		this(Color.BLACK, closingDirection, speed);
	}

	public ClockFade(Color color, Direction closingDirection) {
		this(Color.BLACK, closingDirection, 1);
	}

	public ClockFade(Color color, Direction closingDirection, int speed) {
		setColor(color);
		setSpeed(speed);
		reset(FadeState.NONE);
		closingDirection = Direction.DOWN;
	}

	@Override
	public ClockFade fadeIn() {
		reset(FadeState.FADE_IN);
		return this;
	}

	@Override
	public ClockFade fadeOut() {
		reset(FadeState.FADE_OUT);
		return this;
	}

	private void reset(FadeState state) {
		fadeState = state;
		fadeInitialState = state;
		inc = state == FadeState.FADE_IN ? -speed : speed;
		arc = state == FadeState.FADE_IN ? 360 : 0;
	}

	@Override
	public ClockFade setOnFadeDoneEvent(Runnable runnable) {
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

	@Override
	public void apply(Canvas canvas) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		if (fadeState != FadeState.NONE) {
			double w = canvas.getWidth(), h = canvas.getHeight();
			int radius = (int) h;
			gc.save();
			gc.setFill(color);
			gc.fillArc(w / 2 - radius, h / 2 - radius, radius * 2, radius * 2, 90, arc, ArcType.ROUND);
			if (fadeState != FadeState.DONE && ((arc += inc) > 360 || arc < 0)) {
				arc = fadeState == FadeState.FADE_IN ? 0 : 360;
				fadeState = FadeState.DONE;
				if (onFadeDoneEvent != null)
					onFadeDoneEvent.run();
			}
			gc.restore();
		}
	}

	public void setSpeed(int speed) {
		if (speed < 1)
			throw new RuntimeException("speed must be equal or higher than 1");
		this.speed = speed;
		reset(fadeInitialState);
	}

	public void setColor(Color color) {
		if (color == null)
			throw new RuntimeException("color is null");
		this.color = color;
	}

}
