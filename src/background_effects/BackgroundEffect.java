package background_effects;

import javafx.scene.canvas.Canvas;

public interface BackgroundEffect {

	void apply(Canvas canvas);

	void disable();

	void enable();

	boolean isDisabled();

}
