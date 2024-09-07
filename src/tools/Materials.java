package tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gui.util.ImageUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import util.FindFile;

public abstract class Materials {
	
	public static Image mainSprites;
	public static Image frames;
	public static Image auras;
	public static Image thunders;
	public static Image blankImage;
	public static List<Image> characters;
	public static List<Image> rides;
	public static Map<String, Image> tileSets;
	public static Map<Integer, Integer> bomberSpriteIndex;
	public static Map<String, Image> loadedSprites;
	public static Map<Image, String> loadedSprites2;
	
	public static void loadFromFiles() {
		System.out.print("Carregando materiais... ");
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
		FindFile.findFile("./appdata/sprites/tileset", "Tile*.png").forEach(file ->
			{ tileSets.put(file.getName().replace(".png", ""), loadImage("/tileset/" + file.getName().replace(".png", ""), Color.valueOf("#FF00FF"))); });
		System.out.println("concluido em " + (System.currentTimeMillis() - ms) + "ms");
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
