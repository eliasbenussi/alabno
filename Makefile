all: infrastructure backend

test: infrastructuretest backendtest

backend:
	cd backend && mvn package

backendtest:
	cd backend && mvn test

infrastructure:
	cd infrastructure && make

infrastructuretest:
	cd infrastructure && make test

.PHONY: clean infrastructure backend
