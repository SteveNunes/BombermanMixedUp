package tools;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public abstract class GameMisc {
	
	private static Map<String, MediaPlayer> medias = new LinkedHashMap<>();
	private static List<MediaPlayer> playSoundQueue = new LinkedList<>();
	
	public static <T> void moveItemTo(List<T> list, T item, int index) {
		if (list.contains(item)) {
			int max = list.size();
			if (index < -1 || index > max)
				throw new RuntimeException(index + " - Invalid Index (Min: -1, Max: " + max + ")");
			if (index == -1)
				index = max - 1;
			else if (index == max)
				index = 0;
			list.remove(item);
			list.add(index, item);
		}
	}
	
	public static MediaPlayer getMediaPlayer(String partialSoundPath)
		{ return medias.containsKey(partialSoundPath) ? medias.get(partialSoundPath) : null; }
	
	public static void playSound(String partialSoundPath)
		{ playSound(partialSoundPath, false); }
	
	public static void playSound(String partialSoundPath, boolean stopCurrent) {
		if (medias.containsKey(partialSoundPath) && stopCurrent)
			medias.get(partialSoundPath).stop();
		new Thread(() -> {
			Media media = new Media(new File("appdata/sounds/" + partialSoundPath).toURI().toString());
			MediaPlayer mp = new MediaPlayer(media);
			medias.put(partialSoundPath, mp);
			playSoundQueue.add(mp);
		}).start();
	}
	
	public static void playAllSoundsFromQueue() {
		new Thread(() -> {
			for (MediaPlayer mp : new ArrayList<>(playSoundQueue)) {
				mp.stop();
				mp.play();
			}
			playSoundQueue.clear();
		}).start();
	}

}
