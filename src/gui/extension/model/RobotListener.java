package extension.model;

import extension.model.elements.SensorBox;

public interface RobotListener {
	void sensorFocused(SensorBox sensor, boolean b);
}

