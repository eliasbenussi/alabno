all: frontend simple-haskell-marker infrastructure backend

test: all
	make backendtest
	make infrastructuretest
	make simple-haskell-marker-test

backend:
	cd backend && mvn -T 1C package -q -Dmaven.test.skip=true

backendtest:
	cd backend && mvn -T 1C test -q

infrastructure: simple-haskell-marker
	cd infrastructure && make

infrastructuretest:
	cd infrastructure && make test

simple-haskell-marker:
	cd simple-haskell-marker && make

simple-haskell-marker-test:
	cd simple-haskell-marker && make test

frontend:
	cd frontend && make

clean:
	- cd backend && mvn clean -q
	- cd infrastructure/infrastructure && mvn clean -q
	- cd simple-haskell-marker && make clean
	- rm -rf tmp

.PHONY: clean infrastructure backend simple-haskell-marker simple-haskell-marker-test frontend
