all: jobmanager

test: jobmanager
	cd infrastructure && ./Test

jobmanager:
	cd infrastructure && mvn package -q -Dmaven.test.skip=true

clean:
	- cd infrastructure && mvn clean -q

.PHONY: clean jobmanager

