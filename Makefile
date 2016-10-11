all: infrastructure

test: infrastructuretest

infrastructure:
	cd infrastructure && make

infrastructuretest:
	cd infrastructure && make test

.PHONY: clean infrastructure
