all: hsmarker

hsmarker: repo
	mvn package -q -Dmaven.test.skip=true

test: hsmarker
	mvn test -q

repo:
	wget https://gitlab.com/giuliojiang/MachineLearningLibDir/raw/master/repo.tar.gz
	tar -xf repo.tar.gz

clean:
	mvn clean -q
	- rm -rf repo
	- rm -rf repo.tar.gz
	- rm -rf training

.PHONY: test clean
