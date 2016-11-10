#! /bin/bash

cd frontend/webclient && python ../flaskserver.py &
./infrastructure/infrastructure/WebServer
