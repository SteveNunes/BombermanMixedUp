package tools;

import gui.util.ImageUtils;
import javafx.scene.paint.Color;

public class RGBColor {
	
	private int[][] color;
	private int minValue;
	private int speed;

	public RGBColor()
		{ this(1); }
	
	public RGBColor(int speed) {
		minValue = 255;
		while ((minValue -= speed) > 0);
		minValue += speed;
		this.speed = speed;
		color = new int[][] {{255, minValue, minValue},{0, speed, 0}};
	}

	public Color getColor() {
		for (int n = 0; n < 3; n++)
			color[0][n] += color[1][n];
		if (color[0][0] == 255 && color[0][1] == minValue && color[0][2] == minValue)
			color[1] = new int[] {0, speed, 0};
		else if (color[0][0] == 255 && color[0][1] == 255 && color[0][2] == minValue)
			color[1] = new int[] {-speed, 0, 0};
		else if (color[0][0] == minValue && color[0][1] == 255 && color[0][2] == minValue)
			color[1] = new int[] {0, 0, speed};
		else if (color[0][0] == minValue && color[0][1] == 255 && color[0][2] == 255)
			color[1] = new int[] {0, -speed, 0};
		else if (color[0][0] == minValue && color[0][1] == minValue && color[0][2] == 255)
			color[1] = new int[] {speed, 0, 0};
		else if (color[0][0] == 255 && color[0][1] == minValue && color[0][2] == 255)
			color[1] = new int[] {0, 0, -speed};
		return ImageUtils.argbToColor(ImageUtils.getRgba(color[0][0], color[0][1], color[0][2]));
	}

}
