package fades;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import tools.Tools;

public class PixelizingFade implements Fade {

	private Double speed;
	private Double value;
	private Color color;
	private Double alpha;
	private boolean done;
	private int inc;
	
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
		value = null;
		alpha = null;
		done = false;
	}

	@Override
	public void fadeIn() {
		reset();
		inc = -1;
		alpha = 1d;
	}

	@Override
	public void fadeOut() {
		reset();
		inc = 1;
		alpha = 0d;
	}

	@Override
	public boolean isFadeDone()
		{ return done; }

	@Override
	public void stopFade()
		{ done = true; }

	@Override
	public void apply(Canvas canvas) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.save();
		if (alpha != null && value == null)
			value = inc == -1 ? canvas.getWidth() / 7 : 1d;
		Tools.pixelizeCanvas(value.intValue());
		gc.setGlobalAlpha(alpha);
		gc.setFill(color);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		if (!done) {
			value += speed * inc;
			double s = speed / 30;
			if (alpha + s * inc > -1 && alpha + s * inc < 1)
				alpha += s * inc;
			else
				alpha = inc == 1 ? 1d : 0d;
			if (value >= canvas.getWidth() / 7 || value <= 1) {
				value = inc == 1 ? canvas.getWidth() : 1d;
				done = true;
			}
		}
		gc.restore();
	}

	public void setSpeed(double speed) {
		if (speed < 0.01)
			throw new RuntimeException("speed must be equal or higher than 0.01");
		this.speed = speed;
	}
	
	public void setColor(Color color) {
		if (color == null)
			throw new RuntimeException("color is null");
		this.color = color;
	}

}
