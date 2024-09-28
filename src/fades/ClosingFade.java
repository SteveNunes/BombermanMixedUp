package fades;

import enums.FadeType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import objmoveutils.Position;

public class ClosingFade implements Fade {

	private Runnable onFadeDoneEvent;
	private FadeType fadeType;
	private Position center;
	private Double radius;
	private Double speed;
	private Double valueInc;
	private Color color;
  private WritableImage mask;
	
	public ClosingFade(int centerX, int centerY)
		{ this(Color.BLACK, centerX, centerY); }
	
	public ClosingFade(Color color, int centerX, int centerY)
		{ this(color, centerX, centerY, 1); }
	
	public ClosingFade(int centerX, int centerY, double speed)
		{ this(Color.BLACK, centerX, centerY, speed); }
	
	public ClosingFade(Color color, int centerX, int centerY, double speed)
		{ this(color, new Position(centerX, centerY), speed); }

	public ClosingFade(Position center)
		{ this(Color.BLACK, center); }

	public ClosingFade(Color color, Position center)
		{ this(color, center, 1); }
	
	public ClosingFade(Position center, double speed)
		{ this(Color.BLACK, center, speed); }

	public ClosingFade(Color color, Position center, double speed) {
		setColor(color);
		setSpeed(speed);
		setPosition(center);
		mask = null;
		radius = null;
		valueInc = null;
		fadeType = FadeType.NONE;
	}
	
	private void setPosition(Position center)
		{ this.center = center; }

	@Override
	public ClosingFade fadeIn() {
		fadeType = FadeType.FADE_IN;
		valueInc = speed;
		mask = null;
		radius = 0d;
		return this;
	}

	@Override
	public ClosingFade fadeOut() {
		fadeType = FadeType.FADE_OUT;
		valueInc = -speed;
		mask = null;
		radius = 0d;
		return this;
	}

	@Override
	public ClosingFade setOnFadeDoneEvent(Runnable runnable) {
		onFadeDoneEvent = runnable;
		return this;
	}

	@Override
	public boolean isFadeDone()
		{ return valueInc == null; }

	@Override
	public void stopFade()
		{ radius = null; }
	
	@Override
	public FadeType getFadeType()
		{ return fadeType; }

	@Override
	public void apply(Canvas canvas) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		if (radius != null) {
      int w = (int)canvas.getWidth(), h = (int)canvas.getHeight(),
      		cx = (int)center.getX(), cy = (int)center.getY(), wMax = (int)(w / 1.5);
      if (mask == null) {
        mask = new WritableImage(w, h);
				radius = valueInc > 0 ? 0 : (double)wMax;
      }
			gc.setGlobalAlpha(1);
      gc.clearRect(0, 0, w, h);
      boolean done = true;
			for (int y = 0; y < h; y++)
				for (int x = 0; x < w; x++) {
					double dx = x - cx, dy = y - cy, distance = Math.sqrt(dx * dx + dy * dy);
					boolean b = valueInc == null || (radius > 0 && distance <= radius);
					if (valueInc == null || (valueInc > 0 && !b) || (valueInc < 0 && b))
						done = false;
					mask.getPixelWriter().setColor(x, y, b ? Color.TRANSPARENT : color);
				}
      gc.drawImage(mask, 0, 0);			
			if (valueInc != null) {
				radius += valueInc;
				if (done) {
					radius = valueInc > 0 ? radius : 0d;
					valueInc = null;
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
		valueInc = speed;
	}
	
	public void setColor(Color color) {
		if (color == null)
			throw new RuntimeException("color is null");
		this.color = color;
	}

}
