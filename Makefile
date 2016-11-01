all: infrastructure backend

test: backendtest infrastructuretest

backend:
	cd backend && mvn package -q

backendtest: backend
	cd backend && mvn test -q

infrastructure:
	cd infrastructure && make

infrastructuretest:
	cd infrastructure && make test

clean:
	- cd backend && mvn clean
	- cd infrastructure/infrastructure && mvn clean
	- rm -rf tmp

.PHONY: clean infrastructure backend

