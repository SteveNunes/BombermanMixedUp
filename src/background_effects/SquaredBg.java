package background_effects;

import java.security.SecureRandom;

import gui.util.ImageUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import tools.Draw;
import util.MyMath;

public class SquaredBg implements BackgroundEffect {

	private int[][] squaresBg;
	private int[] rgb;
	private int squareSize;
	private int colorMinVal;
	private int colorMaxVal;
	private int colorIncVal;
	private boolean disabled;
	private WritableImage image;
	
	public SquaredBg()
		{ this(null, 2, 5, 50, 255); }
	
	public SquaredBg(int colorIncVal)
		{ this(null, 2, colorIncVal, 50, 255); }
	
	public SquaredBg(int squareSize, int colorIncVal)
		{ this(null, squareSize, colorIncVal, 50, 255); }
	
	public SquaredBg(int squareSize, int colorMinVal, int colorMaxVal)
		{ this(null, squareSize, 5, 50, 255); }
	
	public SquaredBg(int squareSize, int colorIncVal, int colorMinVal, int colorMaxVal)
		{ this(null, squareSize, 5, colorMinVal, colorMaxVal); }

	public SquaredBg(int[] rgb)
		{ this(rgb, 2, 5, 50, 255); }

	public SquaredBg(int[] rgb, int colorIncVal)
		{ this(rgb, 2, colorIncVal, 50, 255); }
	
	public SquaredBg(int[] rgb, int squareSize, int colorIncVal)
		{ this(rgb, squareSize, colorIncVal, 50, 255); }
	
	public SquaredBg(int[] rgb, int squareSize, int colorMinVal, int colorMaxVal)
		{ this(rgb, squareSize, 5, colorMinVal, colorMaxVal); }
	
	public SquaredBg(int[] rgb, int squareSize, int colorIncVal, int colorMinVal, int colorMaxVal) {
		int w = (int)Draw.getTempCanvas().getWidth() / squareSize + 1, 
				h = (int)Draw.getTempCanvas().getHeight() / squareSize + 1;
		squaresBg = new int[h][w];
		image = new WritableImage(w, h);
		for (int y = 0; y < h; y++)
	    for (int x = 0; x < w; x++)
	    	squaresBg[y][x] = (int)MyMath.getRandom(colorMinVal, colorMaxVal);
		this.squareSize = squareSize;
		this.colorMinVal = colorMinVal;
		this.colorMaxVal = colorMaxVal;
		this.colorIncVal= colorIncVal;
		int r, g, b;
		do {
			r = new SecureRandom().nextInt(2);
			g = new SecureRandom().nextInt(2);
			b = new SecureRandom().nextInt(2);
			this.rgb = rgb == null ? new int[] {r, g, b} : rgb;
		}
		while (r == 0 && g == 0 && b == 0);
		disabled = false;
	}
	
	@Override
	public void apply(Canvas canvas) {
		if (!disabled) {
			for (int y = 0; y < squaresBg.length; y++)
		    for (int x = 0; x < squaresBg[0].length; x++) {
		    	if ((squaresBg[y][x] += colorIncVal) >= colorMaxVal)
		    		squaresBg[y][x] = colorMinVal;
		    	Color c = ImageUtils.argbToColor(ImageUtils.getRgba(rgb[0] == 0 ? 0 : squaresBg[y][x], rgb[1] == 0 ? 0 : squaresBg[y][x], rgb[2] == 0 ? 0 : squaresBg[y][x]));
    			image.getPixelWriter().setColor(x, y, c);
		    }
			canvas.getGraphicsContext2D().drawImage(image, 0, 0, image.getWidth(), image.getHeight(), 0, 0, image.getWidth() * squareSize, image.getHeight() * squareSize);
		}
	}
	
	@Override
	public void disable()
		{ disabled = true; }

	@Override
	public void enable()
		{ disabled = false; }

	@Override
	public boolean isDisabled()
		{ return disabled; }

}
