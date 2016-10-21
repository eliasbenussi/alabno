package input

import (
	"bytes"
	"io/ioutil"
	"encoding/json"
	"os"
	"input/cstylecomments"
	"input/pythonstylecomments"
	"input/haskellstylecomments"
)

type Input struct {
	Input_Directory string
	Type            string
}

func ParseJsonToInput(jsonInput string) Input {
	raw, err := ioutil.ReadFile(jsonInput)
	if err != nil {
		panic(err)
	}
	var input Input
	json.Unmarshal(raw, &input)
	return input
}

func CallCleanerForEachFile(fileName, language string) {
	fi, err := os.Stat(fileName)
	if err != nil {
		panic(err)
	}
	switch {
	case err != nil:
		panic(err)
	case fi.IsDir():
		files, err := ioutil.ReadDir(fileName)
		if err != nil {
			panic(err)
		}
		for _, fileInfo := range files {
			CallCleanerForEachFile(fileName + "/" + fileInfo.Name(), language)
		}
	default:
		RouteToLanguageCleaner(fileName, language)
	}
}

func RouteToLanguageCleaner(fileName, language string) {

	raw, err := ioutil.ReadFile(fileName)
	if err != nil {
		panic(err)
	}
	fileContent := bytes.NewBuffer(raw).String()

	var correction string

	switch language {
	case "c", "c++", "java", "scala", "kotlin", "go":
		correction = cstylecomments.RemoveFrom(fileContent)
	case "python", "ruby":
		correction = pythonstylecomments.RemoveFrom(fileContent)
	case "haskell":
		correction = haskellstylecomments.RemoveFrom(fileContent)
	default:
		return
	}

	ioutil.WriteFile(fileName, []byte(correction), os.ModePerm)
}
