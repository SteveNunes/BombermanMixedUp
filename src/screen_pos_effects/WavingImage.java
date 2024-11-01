package screen_pos_effects;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import tools.Draw;

public class WavingImage {

	private Integer speed;
	private int startX;
	private int startY;
	private int width;
	private int height;
	private int speedTick;
	private int posStart;
	private int[] wavingPattern;
	private boolean disabled;

	public WavingImage() {
		this(0, 0, -1, -1, 1, null);
	}

	public WavingImage(int speed) {
		this(0, 0, -1, -1, speed, null);
	}

	public WavingImage(int[] wavingPattern) {
		this(0, 0, -1, -1, 1, wavingPattern);
	}

	public WavingImage(int speed, int[] wavingPattern) {
		this(0, 0, -1, -1, speed, wavingPattern);
	}

	public WavingImage(int startX, int startY, int width, int height) {
		this(startX, startY, width, height, 1, null);
	}

	public WavingImage(int startX, int startY, int width, int height, int[] wavingPattern) {
		this(startX, startY, width, height, 1, wavingPattern);
	}

	public WavingImage(int startX, int startY, int width, int height, int speed) {
		this(startX, startY, width, height, speed, null);
	}

	public WavingImage(int startX, int startY, int width, int height, int speed, int[] wavingPattern) {
		setBounds(startX, startY, width, height);
		setSpeed(speed);
		speedTick = 0;
		posStart = 0;
		this.wavingPattern = wavingPattern != null ? wavingPattern : new int[] { 1, 1, 0, 0, 0, -1, -1, -1, -1, -1, -2, -1, -1, -1, -1, -1, 0, 0, 0, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
		disabled = false;
	}

	public WavingImage(WavingImage wavingImage) {
		this(wavingImage.startX, wavingImage.startY, wavingImage.width, wavingImage.height, wavingImage.speed, wavingImage.wavingPattern);
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

	public void setWavePattern(int[] pattern) {
		if (pattern == null)
			throw new RuntimeException("pattern is null");
		posStart = 0;
		wavingPattern = pattern;
	}

	public int[] getWavePattern() {
		return wavingPattern;
	}

	public int getSpeed() {
		return speed;
	}

	public WritableImage apply(WritableImage image) {
		if (speed != null) {
			if (width == -1)
				width = (int) image.getWidth();
			if (height == -1)
				height = (int) image.getHeight();
			Canvas canvas = new Canvas(image.getWidth(), image.getHeight());
			GraphicsContext gc = canvas.getGraphicsContext2D();
			gc.setImageSmoothing(false);
			gc.drawImage(image, 0, 0);
			int wavePos = posStart;
			for (int y = startY; y < startY + height; y++) {
				gc.drawImage(image, startX, y, width, 1, startX + wavingPattern[wavePos], y, width, 1);
				if (++wavePos == wavingPattern.length)
					wavePos = 0;
			}
			if (speed == 1 || ++speedTick == speed) {
				speedTick = 0;
				if (++posStart == wavingPattern.length)
					posStart = 0;
			}
			return Draw.getCanvasSnapshot(canvas);
		}
		return image;
	}

	public void setSpeed(int speed) {
		if (speed < 0)
			throw new RuntimeException("speed must be higher than 0");
		this.speed = speed;
	}

	public void setBounds(int startX, int startY, int width, int height) {
		this.startX = startX;
		this.startY = startY;
		this.width = width;
		this.height = height;
	}

}
