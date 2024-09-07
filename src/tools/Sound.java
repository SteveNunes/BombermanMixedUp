package tools;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javafx.concurrent.Task;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public abstract class Sound {
	
	private static String lastPlayedWavPath = null;
	private static String lastPlayedMp3Path = null;
	private static AudioClip lastPlayedAudioClip = null;
	private static MediaPlayer lastPlayedMediaPlayer = null;
	private static Map<String, AudioClip> waves = new LinkedHashMap<>();
	private static Map<String, MediaPlayer> mp3s = new LinkedHashMap<>();
	
	// ============================ PLAY Mp3 =====================================
	
	public static MediaPlayer getMediaPlayer(String mp3Path)
		{ return mp3s.containsKey(mp3Path) ? mp3s.get(mp3Path) : null; }
	
	public static String getLastPlayedMp3Path()
		{ return lastPlayedMp3Path; }
	
	public static MediaPlayer getLastPlayedMediaPlayer()
		{ return lastPlayedMediaPlayer; }
	
	public static void playMp3(String mp3Path)
		{ playMp3(mp3Path, 1, 1, 1, false); }

	public static void playMp3(String mp3Path, boolean stopCurrent)
		{ playMp3(mp3Path, 1, 1, 1, stopCurrent); }

	public static void playMp3(String mp3Path, double rate, double balance, double volume)
		{ playMp3(mp3Path, rate, balance, volume, false); }
	
	public static void playMp3(String mp3Path, double rate, double balance, double volume, boolean stopCurrent) {
		if (stopCurrent && mp3s.containsKey(mp3Path))
			mp3s.get(mp3Path).stop();
  	File file = new File("appdata/musics/" + mp3Path);
		if (!file.exists())
			GameMisc.throwRuntimeException("Não foi possível reproduzir o arquivo \"" + file.getName() + "\" Arquivo não encontrado no local informado.");
		try {
			MediaPlayer mp3;
			if (mp3s.containsKey(mp3Path))
				mp3 = mp3s.get(mp3Path);
			else
				mp3 = new MediaPlayer(new Media(file.toURI().toString()));
			if (stopCurrent) {
				mp3s.put(mp3Path, mp3);
				lastPlayedMp3Path = mp3Path;
				lastPlayedMediaPlayer = mp3;
				mp3.setOnEndOfMedia(() -> { 
					mp3.dispose();
					mp3s.remove(mp3Path);
					if (lastPlayedMp3Path.equals(mp3Path))
						lastPlayedMp3Path = null;
				});
			}
			else
				mp3.setOnEndOfMedia(() -> mp3.dispose());
			mp3.setRate(rate);
			mp3.setBalance(balance);
			mp3.setVolume(volume);
      mp3.play();
		}
		catch (Exception e)
			{ e.printStackTrace(); }
	}
	
	public static void stopAllMp3s() {
		for (MediaPlayer mp3 : new ArrayList<>(mp3s.values()))
			mp3.stop();
	}
	
	// ============================ PLAY Wav =====================================
	
	public static AudioClip getAudioClip(String wavPath)
		{ return waves.containsKey(wavPath) ? waves.get(wavPath) : null; }
	
	public static String getLastPlayedWavPath()
		{ return lastPlayedWavPath; }
	
	public static AudioClip getLastPlayedAudioClip()
		{ return lastPlayedAudioClip; }
	
	public static void playWav(String wavPath)
		{ playWav(wavPath, 1, 1, 1, 1, false); }
	
	public static void playWav(String wavPath, boolean stopCurrent)
		{ playWav(wavPath, 1, 1, 1, 1, stopCurrent); }
	
	public static void playWav(final String wavPath, double rate, double pan, double balance, double volume)
		{ playWav(wavPath, rate, pan, balance, volume, false); }
	
	public static void playWav(String wavPath, double rate, double pan, double balance, double volume, boolean stopCurrent) {
		if (stopCurrent && waves.containsKey(wavPath))
			waves.get(wavPath).stop();
		Task<Void> task = new Task<>() {
	    @Override
	    protected Void call() throws Exception {
	    	File file = new File("appdata/sounds/" + wavPath);
				if (!file.exists())
					GameMisc.throwRuntimeException("Não foi possível reproduzir o arquivo \"" + file.getName() + "\" Arquivo não encontrado no local informado.");
				try {
					final AudioClip clip;
					if (waves.containsKey(wavPath))
						clip = waves.get(wavPath);
					else
						clip = new AudioClip(file.toURI().toString());
					if (stopCurrent) {
						waves.put(wavPath, clip);
						lastPlayedWavPath = wavPath;
						lastPlayedAudioClip = clip;
					}
					clip.setRate(rate);
					clip.setPan(pan);
					clip.setBalance(balance);
					clip.setVolume(volume);
	        clip.play();
				}
				catch (Exception e)
					{ e.printStackTrace(); }
				return null;
	    }
	  };
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
	}
	
	public static void stopAllWaves() {
		for (AudioClip clip : new ArrayList<>(waves.values()))
			clip.stop();
	}
}
