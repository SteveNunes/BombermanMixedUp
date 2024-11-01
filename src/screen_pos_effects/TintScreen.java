package screen_pos_effects;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TintScreen {

	private Double strenght;
	private Color color;
	private boolean disabled;

	public TintScreen(Color color) {
		this(color, 0.5);
	}

	public TintScreen(Color color, double strenght) {
		setColor(color);
		setStrenght(strenght);
		disabled = false;
	}

	public void disable() {
		disabled = true;
	}

	public void enable() {
		disabled = false;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void apply(Canvas canvas) {
		if (!disabled) {
			GraphicsContext gc = canvas.getGraphicsContext2D();
			gc.setFill(color);
			gc.setGlobalAlpha(strenght);
			gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		}
	}

	public void setStrenght(double strenght) {
		if (strenght < 0.001 || strenght > 1)
			throw new RuntimeException("speed must be between 0.0 and 1.0");
		this.strenght = strenght;
	}

	public void setColor(Color color) {
		if (color == null)
			throw new RuntimeException("color is null");
		this.color = color;
	}

}
