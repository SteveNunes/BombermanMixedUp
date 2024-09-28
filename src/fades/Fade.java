package fades;

import enums.FadeType;
import javafx.scene.canvas.Canvas;

public interface Fade {
	
	Fade fadeIn();
	
	Fade fadeOut();
	
	boolean isFadeDone();
	
	void stopFade();
	
	FadeType getFadeType();
	
	void apply(Canvas canvas);
	
	Fade setOnFadeDoneEvent(Runnable runnable);

}
