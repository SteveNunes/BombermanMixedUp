package tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import entities.Effect;
import gui.util.ImageUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import util.FindFile;

public abstract class Materials {

	public static WritableImage mainSprites;
	public static WritableImage frames;
	public static WritableImage auras;
	public static WritableImage thunders;
	public static WritableImage bombs;
	public static WritableImage hud;
	public static WritableImage blankImage;
	public static List<Image> characters;
	public static List<Image> rides;
	public static Map<String, Image> tileSets;
	public static Map<Integer, Integer> bomberSpriteIndex;
	public static Map<String, WritableImage> loadedSprites;
	public static Map<String, WritableImage> tempSprites;

	public static void loadFromFiles() {
		System.out.println("Carregando materiais...");
		long ms = System.currentTimeMillis();
		loadedSprites = new HashMap<>();
		tempSprites = new HashMap<>();
		rides = new ArrayList<>();
		tileSets = new HashMap<>();
		bomberSpriteIndex = new HashMap<>();
		blankImage = new WritableImage(320, 240);
		for (int n = 0; n <= 34; n++)
			rides.add(loadImage("/rides/" + n, Color.valueOf("#03E313")));

		characters = new ArrayList<>();
		for (int n = 0, index = 0; n <= 13; n++) {
			WritableImage image = loadImage("/characters/" + n, Color.valueOf("#03E313"));
			List<Integer> rgbList = new ArrayList<>();
			List<Integer> originalRgb = null;
			for (int i = 0, rgba = 1, rgba2 = 2; rgba != rgba2 || rgba != 0; i++) {
				rgba = image.getPixelReader().getArgb(i, 0);
				rgba2 = image.getPixelReader().getArgb(i + 1, 0);
				if (rgba != 0)
					rgbList.add(rgba);
				else {
					WritableImage img = ImageUtils.cloneWritableImage(image);
					if (originalRgb == null) {
						originalRgb = new ArrayList<>(rgbList);
						bomberSpriteIndex.put(n, index);
					}
					else
						img = ImageUtils.replaceColor(img, originalRgb.toArray(new Integer[rgbList.size()]), rgbList.toArray(new Integer[rgbList.size()]));
					for (int x = 0; x < img.getWidth(); x++)
						img.getPixelWriter().setArgb(x, 0, 0);
					characters.add(img);
					rgbList.clear();
					index++;
				}
			}
		}

		bombs = loadImage("Bombs", Color.valueOf("#03E313"));
		mainSprites = loadImage("MainSprites", Color.valueOf("#03E313"));
		frames = loadImage("HUD", Color.valueOf("#03E313"));
		auras = loadImage("Auras", Color.valueOf("#03E313"));
		thunders = loadImage("Thunders", Color.valueOf("#03E313"));
		hud = loadImage("HUD", Color.valueOf("#03E313"));
		generateExplosionImage();
		Effect.loadEffects();
		FindFile.findFile("./appdata/sprites/tileset", "Tile*.png").forEach(file -> {
			tileSets.put(file.getName().replace(".png", ""), loadImage("/tileset/" + file.getName().replace(".png", ""), Color.valueOf("#FF00FF")));
		});
		System.out.println("... Concluido em " + (System.currentTimeMillis() - ms) + "ms");
	}

	private static void generateExplosionImage() {
		int sz = 320;
		Canvas c = new Canvas(sz, sz);
		GraphicsContext gc = c.getGraphicsContext2D();
		Image exp = ImageUtils.removeBgColor(new Image("file:./appdata/sprites/Explosion.png"), Color.valueOf("#03E313"));
		gc.setImageSmoothing(false);
		int repColors[][] = { { 1, 2, 3 }, { 1, 2, 3 }, { 1, 2, 2 }, { 2, 2, 3 }, { 2, 3, 3 }, { 3, 3, 2 }, { 3, 2, 1 }, { 3, 3, 3 }, { 3, 3, 1 }, { 1, 3, 3 }, { 1, 3, 2 }, { 2, 3, 1 }, { 3, 2, 3 },
				/*
				 * CORES SEM USO {2, 1, 3}, {3, 1, 2}, {2, 1, 1}, {3, 1, 1}, {1, 2, 1}, {1, 3,
				 * 1}, {1, 1, 2}, {1, 1, 3}, {3, 2, 2}, {2, 1, 2}, {2, 3, 2}, {2, 2, 1}, {3, 1,
				 * 3}, {1, 1, 1}, {2, 2, 2},
				 */
		};
		WritableImage exp3 = new WritableImage(256, repColors.length * 20 + 80);
		PixelWriter pw2 = exp3.getPixelWriter();
		for (int ox = 0, oy = 0, cc = 0; cc < repColors.length; cc++) {
			WritableImage exp2 = new WritableImage(64, 80);
			PixelWriter pw = exp2.getPixelWriter();
			for (int y = 0; y < 80; y++)
				for (int x = 0; x < 64; x++) {
					int[] rgba = ImageUtils.getRgbaArray(exp.getPixelReader().getArgb(x + (cc == 0 ? 64 : 0), y));
					int r = rgba[repColors[cc][0]], g = rgba[repColors[cc][1]], b = rgba[repColors[cc][2]];
					if (r + g + b != 0) {
						pw.setColor(x, y, ImageUtils.argbToColor(ImageUtils.getRgba(r, g, b)));
						if (x < 64)
							pw2.setColor(ox + x, oy + y, ImageUtils.argbToColor(ImageUtils.getRgba(r, g, b)));
					}
				}
			if ((ox += 64) == 256) {
				ox = 0;
				oy += 80;
			}
			for (int nes = 0; nes < 2; nes++) {
				gc.clearRect(0, 0, sz, sz);
				for (int d = 0; d < 2; d++) {
					for (int x = 0; x < 5; x++) {
						for (int y = 0; y < 15; y++) {
							int sprX = y == 0 ? 48 : 32, sprY = 16 * x, outX = d == 0 ? x * Main.TILE_SIZE : y * Main.TILE_SIZE, outY = d == 0 ? y * Main.TILE_SIZE : 240 + x * Main.TILE_SIZE;
							if (nes == 0)
								sprX += 64;
							ImageUtils.drawImage(gc, exp2, sprX, sprY, Main.TILE_SIZE, Main.TILE_SIZE, outX, outY, Main.TILE_SIZE, Main.TILE_SIZE, d == 1 && y == 0 ? 270 : d * 90);
						}
						ImageUtils.drawImage(gc, exp2, 16 * d + (nes == 0 ? 64 : 0), x * Main.TILE_SIZE, Main.TILE_SIZE, Main.TILE_SIZE, 80 + x * Main.TILE_SIZE, 208 + 16 * d, Main.TILE_SIZE, Main.TILE_SIZE);
					}
				}
				loadedSprites.put((nes == 0 ? "NesExplosion" : "Explosion") + cc, Draw.getCanvasSnapshot(c));
			}
		}
		// ImageUtils.saveImageToFile(exp3, "D:\\Java\\Bomberman - Mixed
		// Up!\\appdata\\sprites\\Explosions2.png");
	}

	public static WritableImage loadImage(String imagePartialPath, Color removeColor) throws RuntimeException { // Informe apenas o nome do arquivo (com pasta ou nao) a partir da pasta
	                                                                                                            // sprites, sem o .png
		if (!loadedSprites.containsKey(imagePartialPath)) {
			if (removeColor != null)
				loadedSprites.put(imagePartialPath, (WritableImage) ImageUtils.removeBgColor(new Image("file:./appdata/sprites/" + imagePartialPath + ".png"), removeColor));
			else
				loadedSprites.put(imagePartialPath, (WritableImage) new Image("file:./appdata/sprites/" + imagePartialPath + ".png"));
		}
		return loadedSprites.get(imagePartialPath);
	}

	public static WritableImage getImageFromSpriteName(String spriteName) {
		if (spriteName.length() > 10 && spriteName.substring(0, 10).equals("Character.")) {
			try {
				int n = spriteName.indexOf(".") + 1;
				int n2 = spriteName.lastIndexOf(".") + 1;
				int charId, palleteId;
				if (n == n2) {
					charId = Integer.parseInt(spriteName.substring(n));
					palleteId = 0;
				}
				else {
					charId = Integer.parseInt(spriteName.substring(n, n2 - 1));
					palleteId = Integer.parseInt(spriteName.substring(n2, spriteName.length()));
				}
				return (WritableImage) getCharacterSprite(charId, palleteId);
			}
			catch (Exception e) {
				return null;
			}
		}
		if (loadedSprites.containsKey(spriteName))
			return loadedSprites.get(spriteName);
		if (tempSprites.containsKey(spriteName))
			return tempSprites.get(spriteName);
		return null;
	}

	public static Image getCharacterSprite(int characterId, int palleteId) {
		if (!bomberSpriteIndex.containsKey(characterId))
			return null;
		int id = bomberSpriteIndex.get(characterId) + palleteId;
		if (id >= characters.size() || (bomberSpriteIndex.containsKey(characterId + 1) && id >= bomberSpriteIndex.get(characterId + 1)))
			return null;
		return characters.get(id);
	}

}
