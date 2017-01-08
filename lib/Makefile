all: repo scikit

repo:
	wget https://github.com/giuliojiang/stanfordclassifier-local-repo/raw/master/repo.tar.gz
	tar -xf repo.tar.gz

# This assumes the presence of numpy and scipy  
scikit:
	pip -q install -U scikit-learn

clean:
	- rm -rf repo

.PHONY: test clean
