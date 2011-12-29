package extension.model;

public interface TransitionMakerListener {
	void newTransitionStarted(TransitionMaker maker);
	void newTransitionUpdated(TransitionMaker maker);
	void newTransitionStopped(TransitionMaker maker);
}

