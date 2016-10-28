package main

import (
	in "input"
	"os"
)

func main() {

	if len(os.Args) < 2 {
		panic("Not enough arguments passed to the microservice")
	}

	jsonInput := os.Args[1]
	input := in.ParseJsonToInput(jsonInput)
	in.CallCleanerForEachFile(input.Input_Directory, input.Type)
}
