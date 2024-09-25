package screen_effects;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import tools.Tools;

public class WaveScreen {

	private Integer speed;
	private int speedTick;
	private int posStart;
	private int[] wave;
	private boolean disabled;

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

	public void apply(Image tempCanvasSnapshot, Canvas canvas) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		if (speed != null) {
			int wavePos = posStart;
			for (int y = 0; y < canvas.getHeight(); y++) {
				gc.drawImage(tempCanvasSnapshot, 0, y, canvas.getWidth(), 1, wave[wavePos], y, canvas.getWidth(), 1);
				if (++wavePos == wave.length)
					wavePos = 0;
			}
			if (speed == 1 || ++speedTick == speed) {
				speedTick = 0;
				if (++posStart == wave.length)
					posStart = 0;
			}
			tempCanvasSnapshot = Tools.getTempCanvasSnapshot();
		}
	}

	public void setSpeed(int speed) {
		if (speed < 0)
			throw new RuntimeException("speed must be higher than 0");
		this.speed = speed;
	}
	
}
