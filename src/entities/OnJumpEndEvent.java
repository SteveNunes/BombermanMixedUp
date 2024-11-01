package entities;

import java.util.function.Consumer;

import objmoveutils.JumpMove;

public class OnJumpEndEvent {

	private Consumer<JumpMove> onFallAtFreeTileEvent;
	private Consumer<JumpMove> onFallAtNonFreeTileEvent;

	public OnJumpEndEvent() {
		onFallAtFreeTileEvent = null;
		onFallAtNonFreeTileEvent = null;
	}

	public OnJumpEndEvent(Consumer<JumpMove> onFallAtFreeTileEvent, Consumer<JumpMove> onFallAtNonFreeTileEvent) {
		this.onFallAtFreeTileEvent = onFallAtFreeTileEvent;
		this.onFallAtNonFreeTileEvent = onFallAtNonFreeTileEvent;
	}

	public void setOnFallAtFreeTileEvent(Consumer<JumpMove> onFallAtFreeTileEvent) {
		this.onFallAtFreeTileEvent = onFallAtFreeTileEvent;
	}

}
