all: frontend simple-haskell-marker infrastructure backend

test: all
	make testall

testall: backendtest infrastructuretest simple-haskell-marker-test

backend: repo
	cd backend && mvn -T 1C package -q -Dmaven.test.skip=true

backendtest: backend
	cd backend && mvn -T 1C test -q

infrastructure: repo
	cd infrastructure && make

infrastructuretest:
	cd infrastructure && make test

simple-haskell-marker: repo
	cd simple-haskell-marker && mvn -T 1C package -q -Dmaven.test.skip=true
	
repo:
	cd lib && make

simple-haskell-marker-test:
	cd simple-haskell-marker && mvn -T 1C test -q

frontend:
	cd frontend && make

clean:
	- cd backend && mvn clean -q
	- cd infrastructure/infrastructure && mvn clean -q
	- cd simple-haskell-marker && make clean
	- rm -rf tmp

.PHONY: clean infrastructure backend simple-haskell-marker simple-haskell-marker-test frontend
