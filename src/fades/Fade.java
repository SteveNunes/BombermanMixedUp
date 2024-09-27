package fades;

import javafx.scene.canvas.Canvas;

public interface Fade {
	
	void fadeIn();
	
	void fadeOut();
	
	boolean isFadeDone();
	
	void stopFade();
	
	void apply(Canvas canvas);

}
