#! /bin/bash

cd frontend/webclient && python ../flaskserver.py https &
./infrastructure/infrastructure/WebServer https
