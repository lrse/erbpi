package extension.model;


public interface FSMListener {
	public void behaviorAdded( BehaviorNode behavior );
	public void behaviorRemoved( BehaviorNode behavior );
	public void transitionAdded( Transition transition );
	public void transitionRemoved( Transition transition );
}
