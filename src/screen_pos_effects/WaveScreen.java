package screen_pos_effects;

import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class WaveScreen {

	private Integer speed;
	private int speedTick;
	private int posStart;
	private int[] wave;
	private boolean disabled;
	
	public WaveScreen()
		{ this(1); }

	public WaveScreen(int speed) {
		setSpeed(speed);
		speedTick = 0;
		posStart = 0;
		wave = new int[] {1, 1, 0, 0, 0, -1, -1, -1, -1, -1, -2, -1, -1, -1, -1, -1, 0, 0, 0, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
		disabled = false;
	}
	
	public void disable()
		{ disabled = true; }
	
	public void enable()
		{ disabled = false; }
	
	public boolean isDisabled()
		{ return disabled; }
	
	public void setWavePattern(int[] pattern) {
		if (pattern == null)
			throw new RuntimeException("pattern is null");
		posStart = 0;
		wave = pattern;
	}

	public Image apply(Canvas canvas) {
		Image i = getCanvasScreenshot(canvas);
		if (speed != null) {
			GraphicsContext gc = canvas.getGraphicsContext2D();
			int wavePos = posStart;
			for (int y = 0; y < canvas.getHeight(); y++) {
				gc.drawImage(i, 0, y, canvas.getWidth(), 1, wave[wavePos], y, canvas.getWidth(), 1);
				if (++wavePos == wave.length)
					wavePos = 0;
			}
			if (speed == 1 || ++speedTick == speed) {
				speedTick = 0;
				if (++posStart == wave.length)
					posStart = 0;
			}
			return getCanvasScreenshot(canvas);
		}
		return i;
	}

	public void setSpeed(int speed) {
		if (speed < 0)
			throw new RuntimeException("speed must be higher than 0");
		this.speed = speed;
	}

	private Image getCanvasScreenshot(Canvas canvas) {
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		params.setViewport(new Rectangle2D(0, 0, canvas.getWidth(), canvas.getHeight()));
		return canvas.snapshot(params, null);
	}
	
}
