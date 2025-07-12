package tools;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import application.Main;
import entities.BomberMan;
import entities.Entity;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javafx.util.Pair;
import util.DurationTimerFX;
import util.Misc;

public abstract class Sound {

	private static MediaPlayer currentMediaPlayer = null;
	private static Map<String, AudioClip> waves = new LinkedHashMap<>();
	private static Map<String, MediaPlayer> mp3s = new LinkedHashMap<>();
	private static Map<MediaPlayer, String> mp3Timers = new LinkedHashMap<>();
	private static double masterGain = 1;
	
	public static double getMasterGain() {
		return masterGain;
	}
	
	public static void setMasterGain(double gain) {
		masterGain = gain;
	}

	// ============================ PLAY Mp3 =====================================
	
	public static MediaPlayer getCurrentMediaPlayer() {
		return currentMediaPlayer;
	}

	public static MediaPlayer getMediaPlayer(String mp3Path) {
		return mp3s.containsKey(mp3Path) ? mp3s.get(mp3Path) : null;
	}

	public static CompletableFuture<MediaPlayer> playMp3(String mp3Path) {
		return playMp3(mp3Path, 1, 0, masterGain, false, null);
	}

	public static CompletableFuture<MediaPlayer> playMp3(String mp3Path, boolean stopCurrent) {
		return playMp3(mp3Path, 1, 0, masterGain, stopCurrent, null);
	}

	public static CompletableFuture<MediaPlayer> playMp3(String mp3Path, double rate, double balance, double volume) {
		return playMp3(mp3Path, rate, balance, volume, false, null);
	}

	public static CompletableFuture<MediaPlayer> playMp3(String mp3Path, double rate, double balance, double volume, boolean stopCurrent) {
		return playMp3(mp3Path, rate, balance, volume, stopCurrent, null);
	}
	
	public static CompletableFuture<MediaPlayer> playMp3(String mp3Path, Pair<Duration, Duration> doLoop) {
		return playMp3(mp3Path, 1, 0, masterGain, false, doLoop);
	}

	public static CompletableFuture<MediaPlayer> playMp3(String mp3Path, boolean stopCurrent, Pair<Duration, Duration> doLoop) {
		return playMp3(mp3Path, 1, 0, masterGain, stopCurrent, doLoop);
	}

	public static CompletableFuture<MediaPlayer> playMp3(String mp3Path, double rate, double balance, double volume, Pair<Duration, Duration> doLoop) {
		return playMp3(mp3Path, rate, balance, volume, false, doLoop);
	}

	public static CompletableFuture<MediaPlayer> playMp3(String mp3Path, double rate, double balance, double volume, boolean stopCurrent, Pair<Duration, Duration> doLoop) {
		final String mp3Path2 = mp3Path += ".mp3";
		if (stopCurrent && mp3s.containsKey(mp3Path2))
			mp3s.get(mp3Path2).stop();
		File file = new File("appdata/musics/" + mp3Path2);
		if (!file.exists())
			throw new RuntimeException("Não foi possível reproduzir o arquivo \"" + file.getName() + "\" Arquivo não encontrado no local informado.");
		return CompletableFuture.supplyAsync(() -> {
			try {
				MediaPlayer mp3;
				if (mp3s.containsKey(mp3Path2)) {
					mp3 = mp3s.get(mp3Path2);
					mp3.seek(Duration.ZERO);
				}
				else
					mp3 = new MediaPlayer(new Media(file.toURI().toString()));
				currentMediaPlayer = mp3;
				mp3s.put(mp3Path2, mp3);
				if (stopCurrent) {
					mp3.setOnEndOfMedia(() -> {
						mp3.dispose();
						mp3s.remove(mp3Path2);
						mp3Timers.remove(mp3);
					});
				}
				mp3.setRate(rate);
				mp3.setBalance(balance);
				mp3.setVolume(volume);
				mp3.setOnReady(() -> {
					mp3.play();
					if (doLoop != null)
						mp3.setOnPlaying(() -> {
							String timerName = "playMp3DoLoop@" + Main.uniqueTimerId++;
							long seekEnd = (long)doLoop.getKey().toMillis();
							long seekStart = (long)doLoop.getValue().toMillis();
							mp3Timers.put(mp3, timerName);
							if (seekEnd < 0)
								seekEnd += (long)mp3.getTotalDuration().toMillis();
							if (seekStart < 0)
								seekStart += (long)mp3.getTotalDuration().toMillis();
							final long seekEnd2 = seekEnd, seekStart2 = seekStart;
							//mp3.seek(Duration.millis(seekEnd2 - 5000)); // Para testar o seek, isso faz pular direto 5 segundos antes do momentoi q vai dar seek pro ponto inicial
							DurationTimerFX.createTimer(timerName, Duration.millis(20), 0, () -> {
								if (mp3.getCurrentTime().toMillis() >= seekEnd2)
									mp3.seek(Duration.millis(seekStart2));
							}); 
						});
				});
				mp3.setOnError(() -> {
					DurationTimerFX.createTimer("mp3TryAgain@" + mp3.hashCode(), Duration.millis(100), () -> playMp3(mp3Path2, rate, balance, volume, stopCurrent, doLoop));
				});
				
				return mp3;
			}
			catch (Exception e) {
				Misc.addErrorOnLog(e, ".\\errors.log");
				e.printStackTrace();
				currentMediaPlayer = null;
				return null;
			}
		});
	}
	
	public static void stopMp3(String mp3Path) {
		if (mp3Timers.containsKey(mp3s.get(mp3Path))) {
			DurationTimerFX.stopTimer(mp3Timers.get(mp3s.get(mp3Path)));
			mp3Timers.remove(mp3s.get(mp3Path));
		}
		mp3s.get(mp3Path).stop();
	}

	public static void stopAllMp3s() {
		new ArrayList<>(mp3s.keySet()).forEach(mp3Path -> stopMp3(mp3Path));
	}

	// ============================ PLAY Wav =====================================

	public static AudioClip getAudioClip(String wavPath) {
		return waves.containsKey(wavPath) ? waves.get(wavPath) : null;
	}

	public static CompletableFuture<AudioClip> playWav(String wavPath) {
		return playWav(null, wavPath, 1, 0, 0, masterGain, false);
	}

	public static CompletableFuture<AudioClip> playWav(String wavPath, boolean stopCurrent) {
		return playWav(null, wavPath, 1, 0, 0, masterGain, stopCurrent);
	}

	public static CompletableFuture<AudioClip> playWav(final String wavPath, double rate, double pan, double balance, double volume) {
		return playWav(null, wavPath, rate, pan, balance, volume, false);
	}

	public static CompletableFuture<AudioClip> playWav(String wavPath, double rate, double pan, double balance, double volume, boolean stopCurrent) {
		return playWav(null, wavPath, rate, pan, balance, volume, false);
	}

	public static CompletableFuture<AudioClip> playWav(Entity entity, String wavPath) {
		return playWav(entity, wavPath, 1, 0, 0, masterGain, false);
	}

	public static CompletableFuture<AudioClip> playWav(Entity entity, String wavPath, boolean stopCurrent) {
		return playWav(entity, wavPath, 1, 0, 0, masterGain, stopCurrent);
	}

	public static CompletableFuture<AudioClip> playWav(Entity entity, final String wavPath, double rate, double pan, double balance, double volume) {
		return playWav(entity, wavPath, rate, pan, balance, volume, false);
	}

	public static CompletableFuture<AudioClip> playWav(Entity entity, String wavPath, double rate, double pan, double balance, double volume, boolean stopCurrent) {
		if (wavPath.length() > 5 && entity != null && entity instanceof BomberMan && ((BomberMan) entity).getNameSound() != null && wavPath.substring(0, 5).equals("VOICE"))
			wavPath = ((BomberMan) entity).getSoundByName(wavPath.replace("VOICE", ""));
		String wavPath2 = wavPath + ".wav";
		if (stopCurrent && waves.containsKey(wavPath2))
			waves.get(wavPath2).stop();
		return CompletableFuture.supplyAsync(() -> {
			File file = new File("appdata/sounds/" + wavPath2);
			if (!file.exists())
				throw new RuntimeException("Não foi possível reproduzir o arquivo \"" + file.getName() + "\" Arquivo não encontrado no local informado.");
			try {
				final AudioClip clip;
				if (waves.containsKey(wavPath2))
					clip = waves.get(wavPath2);
				else
					clip = new AudioClip(file.toURI().toString());
				if (stopCurrent)
					waves.put(wavPath2, clip);
				clip.setRate(rate);
				clip.setPan(pan);
				clip.setBalance(balance);
				clip.setVolume(volume);
				clip.play();
				return clip;
			}
			catch (Exception e) {
				Misc.addErrorOnLog(e, ".\\errors.log");
				e.printStackTrace();
				return null;
			}
		});
	}

	public static void stopAllWaves() {
		waves.values().forEach(clip -> clip.stop());
	}

}
