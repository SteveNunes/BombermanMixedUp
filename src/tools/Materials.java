package tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import entities.BomberMan;
import entities.Entity;
import gui.util.ImageUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import maps.Item;
import maps.MapSet;
import util.FindFile;
import util.IniFile;
import util.Misc;

public abstract class Materials {

	public static WritableImage mainSprites;
	public static WritableImage frames;
	public static WritableImage thunders;
	public static WritableImage bombs;
	public static WritableImage itens;
	public static WritableImage hud;
	public static WritableImage blankImage;
	public static Map<Integer, List<WritableImage>> characters;
	public static Map<Integer, List<WritableImage>> rides;
	public static Map<Integer, List<WritableImage>> effects;
	public static List<WritableImage> explosions;
	public static Map<Integer, List<WritableImage>> auras;
	public static Map<String, WritableImage> tileSets;
	public static Map<String, WritableImage> loadedSprites;
	public static Map<String, WritableImage> tempSprites;
	private static Color greenColor = Color.valueOf("#03E313");

	public static Color getGreenColor() {
		return greenColor;
	}
	
	public static void loadFromFiles() {
		System.out.println("Carregando...");
		long ms = System.currentTimeMillis();
		loadedSprites = new HashMap<>();
		tempSprites = new HashMap<>();
		characters = new HashMap<>();
		rides = new HashMap<>();
		effects = new HashMap<>();
		auras = new HashMap<>();
		tileSets = new HashMap<>();
		explosions = new ArrayList<>();
		blankImage = new WritableImage(320, 240);
		int ms2 = Misc.bench(() -> loadGeneralSprites());
		System.out.println("Concluido em " + ms2 + "ms");
		ms2 = Misc.bench(() -> generateExplosionImage());
		System.out.println("Concluido em " + ms2 + "ms");
		ms2 = Misc.bench(() -> loadTilesSprites());
		System.out.println("Concluido em " + ms2 + "ms");
		ms2 = Misc.bench(() -> loadMiscSprites());
		System.out.println("Concluido em " + ms2 + "ms");
		System.out.println("Carregamento concluido em " + (System.currentTimeMillis() - ms) + "ms");
	}

	private static void loadTilesSprites() {
		System.out.println("Carregando sprites dos tiles...");
		FindFile.findFile("./appdata/sprites/tileset", "Tile*.png").forEach(file -> {
			tileSets.put(file.getName().replace(".png", ""), loadImage("/tileset/" + file.getName().replace(".png", ""), getGreenColor()));
		});
	}

	private static void loadMiscSprites() {
		System.out.println("Carregando sprites diversos...");
		itens = loadImage("Itens", getGreenColor());
		bombs = loadImage("Bombs", getGreenColor());
		mainSprites = loadImage("MainSprites", getGreenColor());
		frames = loadImage("HUD", getGreenColor());
		thunders = loadImage("Thunders", getGreenColor());
		hud = loadImage("HUD", getGreenColor());
		Item.createItemEdgeImage();
	}

	private static void loadGeneralSprites() {
		System.out.println("Carregando sprites dos personagens e montarias...");
		List<Map<Integer, List<WritableImage>>> spritesList = Arrays.asList(characters, rides, effects);
		String[] folders = { "characters/", "rides/", "effects/"	};
		for (int n = 0; n < folders.length; n++) {
			Map<Integer, List<WritableImage>> sprites = spritesList.get(n);
			String folder = folders[n];
			FindFile.findFile("./appdata/sprites/" + folder).forEach(file -> {
				int n2 = Integer.parseInt(file.getName().replace(".png", ""));
				WritableImage image = loadImage(folder + file.getName().replace(".png", ""), getGreenColor());
				List<List<Color>> palletes = Tools.getPalleteListFromImage(image);
				sprites.put(n2, new ArrayList<>());
				if (palletes == null)
					sprites.get(n2).add(image);
				else {
					for (int i = 0; i < palletes.size(); i++) {
						if (Tools.isColorMixPallete(palletes.get(i)))
							sprites.get(n2).add(Tools.applyColorMixPalleteOnImage(image, palletes.get(i)));
						else
							sprites.get(n2).add(Tools.applyColorPalleteOnImage(image, palletes.get(0), palletes.get(i)));
					}
				}
			});
		}
	}

	private static void generateExplosionImage() { // Criar palletas de color mix para as auras
		System.out.println("Carregando sprites das explosões...");
		WritableImage exp1 = (WritableImage)ImageUtils.removeBgColor(new Image("file:./appdata/sprites/ExplosionNES.png"), getGreenColor());
		WritableImage exp2 = (WritableImage)ImageUtils.removeBgColor(new Image("file:./appdata/sprites/Explosion.png"), getGreenColor());
		List<List<Color>> palletes = Tools.getPalleteListFromImage(exp2);
		int sz = 320;
		Canvas c = new Canvas(sz, sz);
		GraphicsContext gc = c.getGraphicsContext2D();
		gc.setImageSmoothing(false);
		for (int p = -1; p < palletes.size(); p++) {
			gc.clearRect(0, 0, sz, sz);
			for (int d = 0; d < 2; d++) {
				for (int x = 0; x < 5; x++) {
					for (int y = 0; y < 15; y++) {
						int sprX = y == 0 ? 48 : 32, sprY = 16 * x, outX = d == 0 ? x * Main.TILE_SIZE : y * Main.TILE_SIZE, outY = d == 0 ? y * Main.TILE_SIZE : 240 + x * Main.TILE_SIZE;
						ImageUtils.drawImage(gc, p == -1 ? exp1 : exp2, sprX, sprY + 1, Main.TILE_SIZE, Main.TILE_SIZE, outX, outY + 1, Main.TILE_SIZE, Main.TILE_SIZE, d == 1 && y == 0 ? 270 : d * 90);
					}
					ImageUtils.drawImage(gc, p == -1 ? exp1 : exp2, 16 * d, x * Main.TILE_SIZE + 1, Main.TILE_SIZE, Main.TILE_SIZE, 80 + x * Main.TILE_SIZE, 208 + 16 * d + 1, Main.TILE_SIZE, Main.TILE_SIZE);
				}
			}
			if (p > -1)
				explosions.add(Tools.applyColorMixPalleteOnImage(Draw.getCanvasSnapshot(c), palletes.get(p)));
			else
				explosions.add(Draw.getCanvasSnapshot(c));
		}
	}
	
	public static void loadFrameSets() {
		System.out.println("Carregando FrameSets...");
		IniFiles.characters.getSectionList().forEach(section -> new BomberMan(0, Integer.parseInt(section), 0));
		IniFiles.frameSets.getSectionList().forEach(section -> {
			Entity entity = new Entity();
			IniFiles.frameSets.getItemList(section).forEach(item ->
				entity.addNewFrameSetFromIniFile(entity, item, IniFiles.frameSets.fileName(), section, item));
		});
		FindFile.findFile("./appdata/tileset/","*.tiles").forEach(file -> {
			Entity entity = new Entity();
			IniFile ini = IniFile.getNewIniFileInstance(file.getAbsolutePath());
			if (ini.sectionExists("CONFIG"))
				ini.getItemList("CONFIG").forEach(item -> {
					if (item.contains("FrameSet"))
						entity.addNewFrameSetFromIniFile(entity, item, ini.fileName(), "CONFIG", item);
				});
			ini.closeFile();
		});
	}

	public static WritableImage loadImage(String imagePartialPath, Color removeColor) throws RuntimeException { // Informe apenas o nome do arquivo (com pasta ou nao) a partir da pasta
		if (!loadedSprites.containsKey(imagePartialPath)) {
			if (removeColor != null)
				loadedSprites.put(imagePartialPath, (WritableImage) ImageUtils.removeBgColor(new Image("file:./appdata/sprites/" + imagePartialPath + ".png"), removeColor));
			else
				loadedSprites.put(imagePartialPath, (WritableImage) new Image("file:./appdata/sprites/" + imagePartialPath + ".png"));
		}
		return loadedSprites.get(imagePartialPath);
	}

	public static WritableImage getImageFromSpriteName(String spriteName) {
		if (spriteName.equals("TileSet"))
			return MapSet.getTileSetImage();
		List<String> strings = Arrays.asList("Character.", "Ride.", "Effect.");
		for (int z = 0; z < strings.size(); z++) {
			String string = strings.get(z);
			int l = string.length();
			if (spriteName.length() > l && spriteName.substring(0, l).equals(string)) {
				try {
					int n = spriteName.indexOf(".") + 1, n2 = spriteName.lastIndexOf(".") + 1, id, palleteId;
					if (n == n2) {
						id = Integer.parseInt(spriteName.substring(n));
						palleteId = 0;
					}
					else {
						id = Integer.parseInt(spriteName.substring(n, n2 - 1));
						palleteId = Integer.parseInt(spriteName.substring(n2, spriteName.length()));
					}
					return z == 0 ? getCharacterSprite(id, palleteId) :
						z == 1 ? getRideSprite(id, palleteId) : getEffectSprite(id, palleteId);
				}
				catch (Exception e) {
					return null;
				}
			}
		}
		if (loadedSprites.containsKey(spriteName))
			return loadedSprites.get(spriteName);
		if (tempSprites.containsKey(spriteName))
			return tempSprites.get(spriteName);
		return null;
	}

	public static WritableImage getCharacterSprite(int id, int palleteId) {
		if (!characters.containsKey(id))
			throw new RuntimeException(id + " - Invalid character ID");
		if (palleteId < 0 || palleteId >= characters.get(id).size())
			throw new RuntimeException(palleteId + " - Invalid pallete ID for character " + id);
		return characters.get(id).get(palleteId);
	}

	public static WritableImage getRideSprite(int id, int palleteId) {
		if (!rides.containsKey(id))
			throw new RuntimeException(id + " - Invalid ride ID");
		if (palleteId < 0 || palleteId >= rides.get(id).size())
			throw new RuntimeException(palleteId + " - Invalid pallete ID for ride " + id);
		return rides.get(id).get(palleteId);
	}

	public static WritableImage getEffectSprite(int id, int palleteId) {
		if (!effects.containsKey(id))
			throw new RuntimeException(id + " - Invalid ride ID");
		if (palleteId < 0 || palleteId >= effects.get(id).size())
			throw new RuntimeException(palleteId + " - Invalid pallete ID for effect " + id);
		return effects.get(id).get(palleteId);
	}

	public static WritableImage getExplosionSprite(int id) {
		if (id < 0 || id >= explosions.size())
			throw new RuntimeException(id + " - Invalid explosion ID");
		return explosions.get(id);
	}

}
