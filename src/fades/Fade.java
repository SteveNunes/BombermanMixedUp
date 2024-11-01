package fades;

import enums.FadeState;
import javafx.scene.canvas.Canvas;

public interface Fade {

	Fade fadeIn();

	Fade fadeOut();

	boolean isFadeDone();

	void stopFade();

	FadeState getInitialFadeState();

	FadeState getCurrentFadeState();

	void apply(Canvas canvas);

	Fade setOnFadeDoneEvent(Runnable runnable);

}
