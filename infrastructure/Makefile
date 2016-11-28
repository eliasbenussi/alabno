all: jobmanager

test:
	cd infrastructure && ./Test

jobmanager: repo
	cd infrastructure && mvn -T 1C package -q -Dmaven.test.skip=true

clean:
	- cd infrastructure && mvn clean -q

.PHONY: clean jobmanager repo
