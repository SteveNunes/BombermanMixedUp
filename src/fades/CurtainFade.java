package fades;

import enums.Direction;
import enums.FadeState;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CurtainFade implements Fade {

	private Runnable onFadeDoneEvent;
	private FadeState fadeState;
	private FadeState fadeInitialState;
	private Direction closingDirection;
	private Double speed;
	private Double value;
	private Double valueInc;
	private Color color;
	
	public CurtainFade()
		{ this(Color.BLACK); }
	
	public CurtainFade(Direction closingDirection)
		{ this(Color.BLACK, closingDirection, 1); }

	public CurtainFade(Color color)
		{ this(color, Direction.DOWN, 1); }

	public CurtainFade(double speed)
		{ this(Color.BLACK, Direction.DOWN, speed); }

	public CurtainFade(Color color, double speed)
		{ this(color, Direction.DOWN, speed); }
	
	public CurtainFade(Direction closingDirection, double speed)
		{ this(Color.BLACK, closingDirection, speed); }

	public CurtainFade(Color color, Direction closingDirection)
		{ this(Color.BLACK, closingDirection, 1); }
	
	public CurtainFade(Color color, Direction closingDirection, double speed) {
		setColor(color);
		setSpeed(speed);
		reset(FadeState.NONE);
		this.closingDirection = Direction.DOWN;
	}
	
	public Direction getClosingDirection()
		{ return closingDirection; }
	
	public CurtainFade setClosingDirection(Direction closingDirection) {
		this.closingDirection = closingDirection;
		return this;
	}

	@Override
	public CurtainFade fadeIn() {
		reset(FadeState.FADE_IN);
		return this;
	}
	
	@Override
	public CurtainFade fadeOut() {
		reset(FadeState.FADE_OUT);
		return this;
	}

	private void reset(FadeState state) {
		fadeState = state;
		fadeInitialState = state;
		value = null;
		valueInc = state == FadeState.FADE_IN ? speed : -speed;
	}

	@Override
	public CurtainFade setOnFadeDoneEvent(Runnable runnable) {
		onFadeDoneEvent = runnable;
		return this;
	}

	@Override
	public boolean isFadeDone()
		{ return fadeState == FadeState.DONE; }

	@Override
	public void stopFade()
		{ fadeState = FadeState.NONE; }
	
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
			double w = canvas.getWidth(), h = canvas.getHeight();
			if (value == null)
				value = valueInc > 0 ? 0 : closingDirection.isHorizontal() ? w : h;
			gc.save();
			gc.setFill(color);
			value += valueInc;
			if (closingDirection.isHorizontal()) {
				gc.fillRect(closingDirection == Direction.LEFT ? 0 : value, 0, w - value, h);
				if (value < valueInc || value >= w - valueInc)
					fadeDone();
			}
			else {
				gc.fillRect(0, closingDirection == Direction.UP ? 0 : value, w, h - value);
				if (value < valueInc || value >= h - valueInc)
					fadeDone();
			}
			gc.restore();
		}
	}
	
	private void fadeDone() {
		fadeState = FadeState.DONE;
		if (onFadeDoneEvent != null)
			onFadeDoneEvent.run();
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
