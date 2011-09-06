#!/bin/bash
IP_EXA="192.168.0.2"
PORT_EXA=7654

if [ "$3" == "exabot" ]; then
  nc $IP_EXA $PORT_EXA < $1
  trap "echo STOP | nc $IP_EXA $PORT_EXA; echo 'stopped'" SIGINT SIGTERM
  cat > /dev/null
else
  cd ../core
  ./core_exe $1 $2
fi
