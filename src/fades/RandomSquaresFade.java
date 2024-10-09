package fades;

import enums.FadeState;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import util.MyMath;

public class RandomSquaresFade implements Fade {

	private Runnable onFadeDoneEvent;
	private FadeState fadeState;
	private FadeState fadeInitialState;
	private Integer speed;
	private int tick;
	private Integer squareSize;
	private Color color;
	private int squares[][];
	private WritableImage image;
	private Integer count;
	private Integer total;
	
	public RandomSquaresFade()
		{ this(Color.BLACK); }
	
	public RandomSquaresFade(Integer squareSize) 
		{ this(Color.BLACK, squareSize); }

	public RandomSquaresFade(Color color)
		{ this(color, 20); }

	public RandomSquaresFade(Color color, Integer squareSize)
		{ this(color, 1, squareSize); }

	public RandomSquaresFade(Color color, Integer speed, Integer squareSize) {
		setColor(color);
		setSpeed(speed);
		reset(FadeState.NONE);
		this.squareSize = squareSize;
	}
	
	private void reset(FadeState state) {
		fadeState = state;
		fadeInitialState = state;
		squares = null;
		total = 0;
		tick = 0;
		count = fadeState == FadeState.FADE_IN ? 1 : -1;
	}

	@Override
	public RandomSquaresFade fadeIn() {
		reset(FadeState.FADE_IN);
		return this;
	}

	@Override
	public RandomSquaresFade fadeOut() {
		reset(FadeState.FADE_OUT);
		return this;
	}

	@Override
	public boolean isFadeDone()
		{ return fadeState == FadeState.DONE; }

	@Override
	public void stopFade()
		{ fadeState = FadeState.NONE; }
	
	@Override
	public RandomSquaresFade setOnFadeDoneEvent(Runnable runnable) {
		onFadeDoneEvent = runnable;
		return this;
	}

	@Override
	public FadeState getInitialFadeState()
		{ return fadeInitialState; }

	@Override
	public FadeState getCurrentFadeState()
		{ return fadeState; }

	@Override
	public void apply(Canvas canvas) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		int w = (int)canvas.getWidth() / 3, h = (int)canvas.getHeight() / 3;
		if (fadeState != FadeState.DONE && tick == 0) {
			if (squares == null) {
				image = new WritableImage(w, h);
				squares = new int[h / squareSize + 1][w / squareSize + 1];
				total = squares[0].length * squares.length + 1;
				for (int y = 0; count < 0 && y < h; y++)
					for (int x = 0; x < w; x++)
						if (x < w && y < h)
							image.getPixelWriter().setColor(x, y, color);
			}
			if (count < total) {
				int x = (int)MyMath.getRandom(0, squares[0].length - 1),
						y = (int)MyMath.getRandom(0, squares.length - 1);
				while (squares[y][x] == 1) {
					if (++x == squares[0].length) {
						x = 0;
						if (++y == squares.length)
							y = 0;
					}
				}
				squares[y][x] = 1;
				for (int yy = 0; yy < squareSize; yy++)
					for (int xx = 0; xx < squareSize; xx++)
						if (x * squareSize + xx < w && y * squareSize + yy < h)
							image.getPixelWriter().setColor(x * squareSize + xx, y * squareSize + yy, count < 0 ? Color.TRANSPARENT : color);
				if (Math.abs((count += count < 0 ? -1 : 1)) == total) {
					fadeState = FadeState.DONE;
					if (onFadeDoneEvent != null)
						onFadeDoneEvent.run();
				}
			}
		}
		if (fadeState != FadeState.DONE && ++tick == speed)
			tick = 0;
		gc.drawImage(image, 0, 0, w, h, 0, 0, w * 3, h * 3);
	}

	public void setSpeed(Integer speed) {
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
