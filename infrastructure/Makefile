all: jobmanager

test: jobmanager
	cd infrastructure && ./Test

jobmanager:
	cd infrastructure && mvn package

clean:
	- cd infrastructure && make clean

.PHONY: clean jobmanager

