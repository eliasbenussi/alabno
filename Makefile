all: infrastructure backend

test: backendtest infrastructuretest postprocessortest

backend:
	cd backend && mvn package

backendtest:
	cd backend && mvn test

infrastructure:
	cd infrastructure && make

infrastructuretest:
	cd infrastructure && make test

postprocessortest:
	cd backend/postprocessor && mvn test

.PHONY: clean infrastructure backend
