all: jobmanager

test: jobmanager
	cd infrastructure && ./Test

jobmanager:
	cd infrastructure && mvn package -q

clean:
	- cd infrastructure && mvn clean -q

.PHONY: clean jobmanager

