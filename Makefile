all: infrastructure backend simple-haskell-marker

test: backendtest infrastructuretest simple-haskell-marker-test

backend:
	cd backend && mvn package -q

backendtest: backend
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
	- cd backend && mvn clean
	- cd infrastructure/infrastructure && mvn clean
	- cd simple-haskell-marker && make clean
	- rm -rf tmp

.PHONY: clean infrastructure backend simple-haskell-marker simple-haskell-marker-test
