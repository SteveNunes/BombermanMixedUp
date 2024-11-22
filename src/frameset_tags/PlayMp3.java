package frameset_tags;

import application.Main;
import frameset.Sprite;
import javafx.util.Duration;
import javafx.util.Pair;
import maps.MapSet;
import tools.Sound;

public class PlayMp3 extends FrameTag {

	public String partialSoundPath;
	public double rate;
	public double balance;
	public double volume;
	public boolean stopCurrent;
	public Integer seekEnd;
	public Integer seekStart;
	public String stageTagEventAfterEndOfMedia;

	public PlayMp3(String partialSoundPath, double rate, double balance, double volume, boolean stopCurrent, Integer seekEnd, Integer seekStart, String stageTagEventAfterEndOfMedia) {
		this.partialSoundPath = partialSoundPath;
		this.rate = rate;
		this.balance = balance;
		this.volume = volume;
		this.stopCurrent = stopCurrent;
		this.stageTagEventAfterEndOfMedia = stageTagEventAfterEndOfMedia;
		this.seekEnd = seekEnd;
		this.seekStart = seekStart;
	}

	public PlayMp3(String tags) {
		String[] params = validateStringTags(this, tags);
		if (params.length > 9)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 1)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			partialSoundPath = params[n];
			rate = params.length <= ++n || params[n].equals("-") ? 1 : Double.parseDouble(params[n]);
			balance = params.length <= ++n || params[n].equals("-") ? 0 : Double.parseDouble(params[n]);
			volume = params.length <= ++n || params[n].equals("-") ? Sound.getMasterGain() : Double.parseDouble(params[n]);
			stopCurrent = params.length <= ++n || params[n].equals("-") ? false : Boolean.parseBoolean(params[n]);
			seekEnd = params.length <= ++n || params[n].equals("-") ? null : Integer.parseInt(params[n]);
			seekStart = params.length <= ++n || params[n].equals("-") ? null : Integer.parseInt(params[n]);
			stageTagEventAfterEndOfMedia = params.length <= ++n ? "-" : params[n];
		}
		catch (Exception e) {
			throw new RuntimeException(params[n] + " - Invalid parameter");
		}
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + partialSoundPath + ";" + rate + ";" + balance + ";" + volume + ";" + stopCurrent + ";" + seekEnd + ";" + seekStart + ";" + stageTagEventAfterEndOfMedia + "}";
	}

	@Override
	public PlayMp3 getNewInstanceOfThis() {
		return new PlayMp3(partialSoundPath, rate, balance, volume, stopCurrent, seekEnd, seekStart, stageTagEventAfterEndOfMedia);
	}

	@Override
	public void process(Sprite sprite) {
		if (!Main.frameSetEditorIsPaused()) {
			Pair<Duration, Duration> doLoop = seekEnd == null || seekStart == null ? null : new Pair<>(Duration.millis(seekEnd), Duration.millis(seekStart));
			Sound.playMp3(partialSoundPath, rate, balance, volume, stopCurrent, doLoop).thenAccept(mp3 -> {
				if (!partialSoundPath.equals("-"))
					MapSet.runStageTag(stageTagEventAfterEndOfMedia);
			});
		}
	}

}
