all: frontend infrastructure backend

test: all
	make testall

testall: backendtest infrastructuretest 

backend: repo
	cd backend && mvn -T 1C package -q -Dmaven.test.skip=true

backendtest:
	cd backend && mvn -T 1C test -q

infrastructure: repo 
	cd infrastructure && make

infrastructuretest:
	cd infrastructure && make test

repo:
	cd lib && make

frontend:
	cd frontend && make

clean:
	- cd backend && mvn clean -q
	- cd infrastructure/infrastructure && mvn clean -q
	- rm -rf tmp

.PHONY: clean infrastructure backend frontend
