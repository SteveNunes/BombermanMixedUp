package fades;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import util.MyMath;

public class RandomSquaresFade implements Fade {

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
		this.squareSize = squareSize;
		count = null;
	}
	
	private void reset() {
		squares = null;
		total = 0;
		tick = 0;
		count = null;
	}

	@Override
	public void fadeIn() {
		reset();
		count = 1;
	}

	@Override
	public void fadeOut() {
		reset();
		count = -1;
	}

	@Override
	public boolean isFadeDone()
		{ return total != null && total == -1; }

	@Override
	public void stopFade()
		{ count = null; }

	@Override
	public void apply(Canvas canvas) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		if (count != null && tick == 0) {
			if (squares == null) {
				image = new WritableImage((int)canvas.getWidth(), (int)canvas.getHeight());
				squares = new int[(int)canvas.getHeight() / squareSize][(int)canvas.getWidth() / squareSize];
				total = squares[0].length * squares.length + 1;
				for (int y = 0; count < 0 && y < image.getHeight(); y++)
					for (int x = 0; x < image.getWidth(); x++)
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
						image.getPixelWriter().setColor(x * squareSize + xx, y * squareSize + yy, count < 0 ? Color.TRANSPARENT : color);
				if (Math.abs((count += count < 0 ? -1 : 1)) == total) {
					count = null;
					total = -1;
				}
			}
		}
		if (count != null && ++tick == speed)
			tick = 0;
		gc.drawImage(image, 0, 0);
	}

	public void setSpeed(Integer speed) {
		if (speed < 1)
			throw new RuntimeException("speed must be equal or higher than 1");
		this.speed = speed;
	}
	
	public void setColor(Color color) {
		if (color == null)
			throw new RuntimeException("color is null");
		this.color = color;
	}

}
