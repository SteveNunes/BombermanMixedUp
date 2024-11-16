package frameset_tags;

import application.Main;
import frameset.Sprite;
import tools.Sound;

public class PlayMp3 extends FrameTag {

	public String partialSoundPath;
	public double rate;
	public double balance;
	public double volume;
	public boolean stopCurrent;

	public PlayMp3(String partialSoundPath, double rate, double balance, double volume, boolean stopCurrent) {
		this.partialSoundPath = partialSoundPath;
		this.rate = rate;
		this.balance = balance;
		this.volume = volume;
		this.stopCurrent = stopCurrent;
	}

	public PlayMp3(String tags) {
		String[] params = validateStringTags(this, tags);
		if (params.length > 6)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 1)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			partialSoundPath = params[n];
			n++;
			rate = params.length <= n || params[n].equals("-") ? 1 : Double.parseDouble(params[n]);
			n++;
			balance = params.length <= n || params[n].equals("-") ? 0 : Double.parseDouble(params[n]);
			n++;
			volume = params.length <= n || params[n].equals("-") ? Sound.getMasterGain() : Double.parseDouble(params[n]);
			n++;
			stopCurrent = params.length <= n || params[n].equals("-") ? false : Boolean.parseBoolean(params[n]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[n] + " - Invalid parameter");
		}
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + partialSoundPath + ";" + rate + ";" + balance + ";" + volume + ";" + stopCurrent + "}";
	}

	@Override
	public PlayMp3 getNewInstanceOfThis() {
		return new PlayMp3(partialSoundPath, rate, balance, volume, stopCurrent);
	}

	@Override
	public void process(Sprite sprite) {
		if (!Main.frameSetEditorIsPaused()) {
			Sound.playMp3(partialSoundPath, rate, balance, volume, stopCurrent);
		}
	}

}
