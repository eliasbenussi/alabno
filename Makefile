all: infrastructure backend simple-haskell-marker

test: all backendtest infrastructuretest simple-haskell-marker-test

backend:
	cd backend && mvn package -q -Dmaven.test.skip=true

backendtest:
	cd backend && mvn test -q

infrastructure:
	cd infrastructure && make

infrastructuretest:
	cd infrastructure && make test

simple-haskell-marker:
	cd simple-haskell-marker && make

simple-haskell-marker-test:
	cd simple-haskell-marker && make test

clean:
	- cd backend && mvn clean -q
	- cd infrastructure/infrastructure && mvn clean -q
	- cd simple-haskell-marker && make clean
	- rm -rf tmp

.PHONY: clean infrastructure backend simple-haskell-marker simple-haskell-marker-test
