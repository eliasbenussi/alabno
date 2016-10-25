all: infrastructure backend

test: all backendtest infrastructuretest

backend:
	cd backend && mvn package

backendtest:
	cd backend && mvn test

infrastructure:
	cd infrastructure && make

infrastructuretest:
	cd infrastructure && make test

clean:
	- cd backend && mvn clean
	- cd infrastructure/infrastructure && mvn clean

.PHONY: clean infrastructure backend
