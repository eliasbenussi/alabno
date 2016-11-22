all: hsmarker

hsmarker: repo
	mvn install -q -Dmaven.test.skip=true

test:
	mvn test -q

repo:
	wget https://gitlab.com/giuliojiang/MachineLearningLibDir/raw/master/repo.tar.gz
	tar -xf repo.tar.gz

clean:
	mvn clean -q
	- rm -rf training

.PHONY: test clean
