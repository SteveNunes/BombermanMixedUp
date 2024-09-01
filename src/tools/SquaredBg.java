package tools;

import java.security.SecureRandom;

import application.Main;
import gui.util.ImageUtils;
import javafx.scene.canvas.GraphicsContext;

public class SquaredBg {

	private static int[][] squaresBg = null;
	private static int[] rgb;
	private static int squareSize;
	private static int colorMinVal;
	private static int colorMaxVal;
	private static int colorIncVal;
	
	public static void setSquaredBg()
		{ setSquaredBg(null, 2, 5, 50, 255); }
	
	public static void setSquaredBg(int colorIncVal)
		{ setSquaredBg(null, 2, colorIncVal, 50, 255); }
	
	public static void setSquaredBg(int squareSize, int colorIncVal)
		{ setSquaredBg(null, squareSize, colorIncVal, 50, 255); }
	
	public static void setSquaredBg(int squareSize, int colorMinVal, int colorMaxVal)
		{ setSquaredBg(null, squareSize, 5, 50, 255); }
	
	public static void setSquaredBg(int squareSize, int colorIncVal, int colorMinVal, int colorMaxVal)
		{ setSquaredBg(null, squareSize, 5, colorMinVal, colorMaxVal); }

	public static void setSquaredBg(int[] rgb)
		{ setSquaredBg(rgb, 2, 5, 50, 255); }

	public static void setSquaredBg(int[] rgb, int colorIncVal)
		{ setSquaredBg(rgb, 2, colorIncVal, 50, 255); }
	
	public static void setSquaredBg(int[] rgb, int squareSize, int colorIncVal)
		{ setSquaredBg(rgb, squareSize, colorIncVal, 50, 255); }
	
	public static void setSquaredBg(int[] rgb, int squareSize, int colorMinVal, int colorMaxVal)
		{ setSquaredBg(rgb, squareSize, 5, colorMinVal, colorMaxVal); }
	
	public static void setSquaredBg(int[] rgb, int squareSize, int colorIncVal, int colorMinVal, int colorMaxVal) {
		int w = (int)(Main.winW / squareSize) + 1, h = (int)(Main.winH / squareSize) + 1;
		squaresBg = new int[h][w];
		for (int y = 0; y < h; y++)
	    for (int x = 0; x < w; x++)
	      squaresBg[y][x] = Main.getRandom(colorMinVal, colorMaxVal);
		SquaredBg.squareSize = squareSize;
		SquaredBg.colorMinVal = colorMinVal;
		SquaredBg.colorMaxVal = colorMaxVal;
		SquaredBg.colorIncVal= colorIncVal;
		int r, g, b;
		do {
			r = new SecureRandom().nextInt(2);
			g = new SecureRandom().nextInt(2);
			b = new SecureRandom().nextInt(2);
			SquaredBg.rgb = rgb == null ? new int[] {r, g, b} : rgb;
		}
		while (r == 0 && g == 0 && b == 0);
	}
	
	public static void draw(GraphicsContext targetGc) {
		if (squaresBg != null) {
    	targetGc.save();
			for (int y = 0; y < squaresBg.length; y++)
		    for (int x = 0; x < squaresBg[0].length; x++) {
		    	if ((squaresBg[y][x] += colorIncVal) >= colorMaxVal)
		    		squaresBg[y][x] = colorMinVal;
		    	targetGc.setFill(ImageUtils.argbToColor(ImageUtils.getRgba(rgb[0] == 0 ? 0 : squaresBg[y][x], rgb[1] == 0 ? 0 : squaresBg[y][x], rgb[2] == 0 ? 0 : squaresBg[y][x])));
		    	targetGc.fillRect(x * squareSize, y * squareSize, squareSize, squareSize);
		    }
    	targetGc.restore();
		}
	}

}
