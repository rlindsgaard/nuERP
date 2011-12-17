SHELL:=/bin/bash
default: run

boot: 
	pushd bootstrap/ && ant boot && popd

run:
	~/.cabal/bin/poetsserver iex.config

clean:
	-rm events/*.log logging/*.log
