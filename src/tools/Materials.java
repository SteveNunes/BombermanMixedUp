package tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gui.util.ImageUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import util.FindFile;

public abstract class Materials {
	
	public static Image mainSprites;
	public static Image frames;
	public static Image auras;
	public static Image thunders;
	public static Image explosions;
	public static Image blankImage;
	public static List<Image> characters;
	public static List<Image> rides;
	public static Map<String, Image> tileSets;
	public static Map<Integer, Integer> bomberSpriteIndex;
	public static Map<String, Image> loadedSprites;
	public static Map<Image, String> loadedSprites2;
	
	public static void loadFromFiles() {
		System.out.println("Carregando materiais...");
		long ms = System.currentTimeMillis();
		loadedSprites = new HashMap<>();
		loadedSprites2 = new HashMap<>();
		rides = new ArrayList<>();
		tileSets = new HashMap<>();
		bomberSpriteIndex = new HashMap<>();
		blankImage = new WritableImage(320, 240);
		for (int n = 0; n <= 34; n++)
			rides.add(loadImage("/rides/" + n, Color.valueOf("#03E313")));

		characters = new ArrayList<>();
		for (int n = 0, index = 0; n <= 13; n++) {
			WritableImage image = (WritableImage)loadImage("/characters/" + n, Color.valueOf("#03E313"));
			List<Integer> rgbList = new ArrayList<>();
			List<Integer> originalRgb = null;
			for  (int i = 0, rgba = 1, rgba2 = 2; rgba != rgba2 || rgba != 0; i++) {
				rgba = image.getPixelReader().getArgb(i, 0);
				rgba2 = image.getPixelReader().getArgb(i + 1, 0);
				if (rgba != 0)
					rgbList.add(rgba);
				else {
					WritableImage img = ImageUtils.cloneWritableImage(image);
					for (int x = 0; x < img.getWidth(); x++)
						img.getPixelWriter().setArgb(x, 0, 0);
					if (originalRgb == null) {
						originalRgb = new ArrayList<>(rgbList);
						characters.add(img);
						bomberSpriteIndex.put(n, index);
					}
					else
						characters.add(ImageUtils.replaceColor(img, originalRgb.toArray(new Integer[rgbList.size()]), rgbList.toArray(new Integer[rgbList.size()])));
					rgbList.clear();
					index++;
				}
			}
		}
		
		mainSprites = loadImage("MainSprites", Color.valueOf("#03E313"));
		frames = loadImage("HUD", Color.valueOf("#03E313"));
		auras = loadImage("Auras", Color.valueOf("#03E313"));
		thunders = loadImage("Thunders", Color.valueOf("#03E313"));
		generateExplosionImage(); 
		FindFile.findFile("./appdata/sprites/tileset", "Tile*.png").forEach(file ->
			{ tileSets.put(file.getName().replace(".png", ""), loadImage("/tileset/" + file.getName().replace(".png", ""), Color.valueOf("#FF00FF"))); });
		System.out.println("... Concluido em " + (System.currentTimeMillis() - ms) + "ms");
	}

	private static void generateExplosionImage() {
		int sz = 320;
		Canvas c = new Canvas(sz, sz);
		GraphicsContext gc = c.getGraphicsContext2D();
		gc.setImageSmoothing(false);
		gc.clearRect(0, 0, sz, sz);
		for (int d = 0; d < 2; d++)
			for (int x = 0; x < 5; x++)
				for (int y = 0; y < 15; y++) {
					int sprX = y == 0 ? 48 : 32, sprY = 32 + 16 * x,
							outX = d == 0 ? x * 16 : y * 16,
							outY = d == 0 ? y * 16 : 240 + x * 16;
					ImageUtils.drawImage(gc, mainSprites, sprX, sprY, 16, 16, outX, outY, 16, 16, d == 1 && y == 0 ? 270 : d * 90);
				}
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		explosions = c.snapshot(params, null);
		ImageUtils.saveImageToFile(explosions, "D:\\Explosoes.png");
	}

	public static Image loadImage(String imagePartialPath, Color removeColor) throws RuntimeException { // Informe apenas o nome do arquivo (com pasta ou nao) a partir da pasta sprites, sem o .png
		if (!loadedSprites.containsKey(imagePartialPath)) {
			Image image;
			if (removeColor != null)
				loadedSprites.put(imagePartialPath, image = ImageUtils.removeBgColor(new Image("file:./appdata/sprites/" + imagePartialPath + ".png"), removeColor));
			else
				loadedSprites.put(imagePartialPath, image = new Image("file:./appdata/sprites/" + imagePartialPath + ".png"));
			loadedSprites2.put(image, imagePartialPath);
		}
		return loadedSprites.get(imagePartialPath);
	}
	
	public static Image getImageFromSpriteName(String spriteName) {
		if (loadedSprites.containsKey(spriteName))
			return loadedSprites.get(spriteName);
		return null;
	}

	public static String getSpriteNameFromImage(Image image) {
		if (loadedSprites2.containsKey(image))
			return loadedSprites2.get(image);
		return null;
	}
	
	public static Image getCharacterSprite(int characterId, int palleteId) {
		if (!bomberSpriteIndex.containsKey(characterId))
			return null;
		int id = bomberSpriteIndex.get(characterId) + palleteId;
		if (id >= characters.size() || (bomberSpriteIndex.containsKey(characterId + 1) && 
				id >= bomberSpriteIndex.get(characterId + 1)))
			return null;
		return characters.get(id); 
	}

}
