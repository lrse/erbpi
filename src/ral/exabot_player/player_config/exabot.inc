define exabot position
(
  drive "diff"
  localization "gps"
  size [0.22 0.18 0.10]
  gui_nose 1
  obstacle_return 1
  
  exabot_telemeters()
)

define exabot_telemeter sensor
(
  size [0.05 0.01 0.01]
  color_rgba [ 0 0 0 0.2 ]
  samples 1
  fov 5
  range [0.06 0.8]
)

define exabot_telemeters ranger
(
  # no cambiar el orden de estas lineas! se corresponde con el exabotPlayerRAL.cpp
  sensor (pose [0 0 0 90])
  sensor (pose [0 0 0 45])
  sensor (pose [0 0 0 0])
  sensor (pose [0 0 0 -45])
  sensor (pose [0 0 0 -90])
  sensor (pose [0 0 0 180])
  # sensor (pose [0 0 0 -90])
  # sensor (pose [0 0 0 -45])
  # sensor (pose [0 0 0 0])
  # sensor (pose [0 0 0 45])
  # sensor (pose [0 0 0 90])
  # sensor (pose [0 0 0 180])
)

