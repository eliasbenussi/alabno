all: infrastructure backend

test: backendtest infrastructuretest

backend:
	cd backend && mvn package

backendtest:
	cd backend && mvn test

infrastructure:
	cd infrastructure && make

infrastructuretest:
	cd infrastructure && make test

.PHONY: clean infrastructure backend
