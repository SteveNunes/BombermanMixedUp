package screen_effects;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class DefaultFade implements Fade {

	private Double value = null;
	private Double speed = null;
	private Double valueInc = null;
	private Color color;
	
	public DefaultFade(Color color, double speed) {
		setColor(color);
		setSpeed(speed);
		valueInc = speed;
	}

	@Override
	public void fadeIn() {
		valueInc = -speed;
		value = 1d;
	}

	@Override
	public void fadeOut() {
		valueInc = speed;
		value = 0d;
	}

	@Override
	public boolean isFadeDone()
		{ return valueInc == 0; }

	@Override
	public void stopFade()
		{ color = null; }

	@Override
	public void apply(Canvas canvas) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		if (color != null) {
			gc.setFill(color);
			gc.setGlobalAlpha(value);
			if (valueInc != 0 && (value += valueInc) > 1 || value < 0d) {
				valueInc = 0d;
				value = value > 1d ? 1d : 0d;
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
