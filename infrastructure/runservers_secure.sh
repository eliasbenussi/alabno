#! /bin/bash

cd frontend/webclient && python ..//httpserver.py https &
./infrastructure/infrastructure/WebServer https
./infrastructure/Pause
