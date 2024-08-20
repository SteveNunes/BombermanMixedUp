package entities;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import gui.util.ImageUtils;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javazoom.jl.player.advanced.AdvancedPlayer;
import util.FindFile;

public abstract class Materials {
	
	public static List<Image> tileSets;
	public static List<Image> characters;
	public static List<Image> rides;
	public static Image mainSprites;
	public static Image frames;
	public static Image auras;
	public static Image thunders;
	public static Map<String, Clip> audios;
	public static Map<String, Clip> voices;
	public static Map<String, Clip> thunder;
	public static Map<String, Clip> nestalgia;
	public static Map<String, AdvancedPlayer> musics;
	public static Map<Integer, Integer> bomberSpriteIndex;
	public static Map<String, Image> loadedSprites;
	public static Map<Image, String> loadedSprites2;
	
	public static void loadFromFiles() {
		System.out.print("Carregando materiais... ");
		long ms = System.currentTimeMillis();
		loadedSprites = new HashMap<>();
		loadedSprites2 = new HashMap<>();
		rides = new ArrayList<>();
		tileSets = new ArrayList<>();
		audios = new HashMap<>();
		voices = new HashMap<>();
		thunder = new HashMap<>();
		nestalgia = new HashMap<>();
		musics = new HashMap<>();
		bomberSpriteIndex = new HashMap<>();
		for (int n = 0; n <= 34; n++)
			rides.add(loadImage("/rides/" + n, Color.valueOf("#03E313")));
		characters = new ArrayList<>();
		for (int n = 0; n <= 13; n++) {
			// Adicionar copias dos arquivos dos personagens ja com a paleta aplicada
			characters.add(loadImage("/characters/" + n, Color.valueOf("#03E313")));
		}
		mainSprites = loadImage("MainSprites", Color.valueOf("#03E313"));
		frames = loadImage("HUD", Color.valueOf("#03E313"));
		auras = loadImage("Auras", Color.valueOf("#03E313"));
		thunders = loadImage("Thunders", Color.valueOf("#03E313"));
		FindFile.findFile("./appdata/sprites/tileset", "Tile*.png").forEach(file ->
			{ tileSets.add(loadImage("/tileset/" + file.getName().replace(".png", ""), Color.valueOf("#FF00FF"))); });
		FindFile.findFile("appdata/sounds", "*.wav").forEach(file -> {
      try (FileInputStream fis = new FileInputStream(file)) {
        AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(fis));
        Clip clip = AudioSystem.getClip();
        clip.open(ais);
        if (file.getAbsoluteFile().toString().contains("/nestalgia/"))
        	nestalgia.put(file.getName(), clip);
        else if (file.getAbsoluteFile().toString().contains("/voices/"))
        	voices.put(file.getName(), clip);
        else if (file.getAbsoluteFile().toString().contains("/thunders/"))
        	thunder.put(file.getName(), clip);
      	audios.put(file.getName(), clip);
      }
			catch (Exception e)
				{ throw new RuntimeException("Erro ao carregar o arquivo de áudio: \"" + file.getAbsolutePath() + "\"\n" + e); }
		});
		FindFile.findFile("appdata/musics", "*.mp3").forEach(file -> {
			try (FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath())) {
				AdvancedPlayer player = new AdvancedPlayer(fileInputStream);
				/**
						player.setPlayBackListener(new PlaybackListener() {
							@Override
							public void playbackStarted(PlaybackEvent evt) {
								System.out.println("Playback started");
							}
		
							@Override
							public void playbackFinished(PlaybackEvent evt) {
								System.out.println("Playback finished");
							}
						});
				*/
				musics.put(file.getName(), player);
			}
			catch (Exception e)
				{ throw new RuntimeException("Erro ao carregar o arquivo de áudio: \"" + file.getAbsolutePath() + "\"\n" + e); }
		});
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

}
