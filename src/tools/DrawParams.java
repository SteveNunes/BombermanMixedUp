package tools;

import drawimage_stuffs.DrawImageEffects;
import enums.DrawType;
import enums.ImageFlip;
import gui.util.ImageUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DrawParams {

	private Color color;
	private double[] params;
	private DrawType drawType;
	private Integer ghostingDistance;
	private double ghostingOpacityDec;
	private double ghostingCount;

	private Image image;
	private Integer sourceX;
	private Integer sourceY;
	private Integer sourceWidth;
	private Integer sourceHeight;
	private Integer targetX;
	private Integer targetY;
	private Integer targetWidth;
	private Integer targetHeight;
	private ImageFlip flip;
	private Integer rotateAngle;
	private Double opacity;
	private DrawImageEffects effects;
	private int frontValue;
	private Font font;
	private String text;

	DrawParams(int frontValue, DrawType drawType, Color color, Font font, String text, double... params) {
		this.frontValue = frontValue;
		this.font = font;
		this.color = color;
		this.text = text;
		this.params = params;
		this.drawType = drawType;
		ghostingDistance = null;
	}

	DrawParams(int frontValue, DrawType drawType, Color color, double... params) {
		this.frontValue = frontValue;
		this.color = color;
		this.params = params;
		this.drawType = drawType;
		ghostingDistance = null;
	}

	DrawParams(int frontValue, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Integer rotateAngle, Double opacity, DrawImageEffects effects) {
		drawType = DrawType.IMAGE;
		this.image = image;
		this.sourceX = sourceX;
		this.sourceY = sourceY;
		this.sourceWidth = sourceWidth;
		this.sourceHeight = sourceHeight;
		this.targetX = targetX;
		this.targetY = targetY;
		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;
		this.flip = flip;
		this.rotateAngle = rotateAngle;
		this.opacity = opacity;
		this.effects = effects;
		this.frontValue = frontValue;
		ghostingDistance = null;
	}

	public DrawParams setGhosting(int distance, double opacityDec) {
		ghostingDistance = distance;
		ghostingOpacityDec = opacityDec;
		ghostingCount = 1;
		return this;
	}

	public boolean isGhosting() {
		return ghostingDistance != null;
	}

	public int getFrontValue() {
		return frontValue;
	}
	
	private boolean checkCoords(double drawX, double drawY, double drawW, double drawH, double startX, double startY, double width, double height) {
		int endX = (int)(startX + width), endY = (int)(startY + height);
		return ((drawX >= startX || drawX + drawW >= startX || drawX <= endX || drawX + drawW <= endX) &&
						(drawY >= startY || drawY + drawH >= startY || drawY <= endY || drawY + drawH <= endY));
	}

	public void draw(GraphicsContext gc, int startX, int startY, int width, int height) {
		if (ghostingDistance == null || --ghostingCount == 0) {
			if (drawType == DrawType.IMAGE) {
				if (checkCoords(targetX, targetY, (targetWidth != null ? targetWidth : sourceWidth == null ? 0 : sourceWidth), (targetHeight != null ? targetHeight : sourceHeight == null ? 0 : sourceHeight), startX, startY, width, height))
					ImageUtils.drawImage(gc, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, rotateAngle, opacity, effects);
			}
			else {
				if (drawType == DrawType.FILL_OVAL) {
					if (checkCoords(params[0] - params[2] / 2, params[1] - params[3] / 2, params[2], params[3], startX, startY, width, height))
						gc.fillOval(params[0], params[1], params[2], params[3]);
				}
				else if (drawType == DrawType.FILL_RECT) {
					if (checkCoords(params[0] - params[2] / 2, params[1] - params[3] / 2, params[2], params[3], startX, startY, width, height))
						gc.fillRect(params[0], params[1], params[2], params[3]);
				}
				else if (drawType == DrawType.STROKE_OVAL) {
					if (checkCoords(params[0] - params[2] / 2, params[1] - params[3] / 2, params[2], params[3], startX, startY, width, height))
						gc.strokeOval(params[0], params[1], params[2], params[3]);
				}
				else if (drawType == DrawType.STROKE_RECT) {
					if (checkCoords(params[0] - params[2] / 2, params[1] - params[3] / 2, params[2], params[3], startX, startY, width, height))
						gc.strokeRect(params[0], params[1], params[2], params[3]);
				}
				else if (drawType == DrawType.STROKE_LINE) {
					if (checkCoords(params[0], params[1], params[2] - params[0], params[3] - params[1], startX, startY, width, height))
						gc.strokeLine(params[0], params[1], params[2], params[3]);
				}
				else if (drawType == DrawType.SET_FILL)
					gc.setFill(color);
				else if (drawType == DrawType.SET_STROKE)
					gc.setStroke(color);
				else if (drawType == DrawType.SET_LINE_WIDTH)
					gc.setLineWidth(params[0]);
				else if (drawType == DrawType.SET_FONT)
					gc.setFont(font);
				else if (drawType == DrawType.STROKE_TEXT)
					gc.strokeText(text, params[0], params[1]);
				else if (drawType == DrawType.FILL_TEXT)
					gc.fillText(text, params[0], params[1]);
				else if (drawType == DrawType.SAVE)
					gc.save();
				else if (drawType == DrawType.RESTORE)
					gc.restore();
				else if (drawType == DrawType.SET_GLOBAL_ALPHA)
					gc.setGlobalAlpha(params[0]);
			}
		}
		if (ghostingDistance != null) {
			if (ghostingCount == 0 && (opacity -= ghostingOpacityDec) <= 0)
				ghostingDistance = null;
			else if (ghostingCount == 0)
				ghostingCount = ghostingDistance;
		}
	}

}