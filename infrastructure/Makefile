all: jobmanager

test: jobmanager
	cd infrastructure && ./Test

jobmanager:
	cd infrastructure && mvn -T 1C package -q -Dmaven.test.skip=true

repo:
	cd infrastructure && make

clean:
	- cd infrastructure && mvn clean -q
	- cd infrastructure && make clean

.PHONY: clean jobmanager repo

