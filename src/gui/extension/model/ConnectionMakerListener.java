package extension.model;

public interface ConnectionMakerListener {
	void newConnectionStarted(ConnectionMaker maker);
	void newConnectionUpdated(ConnectionMaker maker);
	void newConnectionStopped(ConnectionMaker maker);
}

