package input

import (
	"bytes"
	"io/ioutil"
	"encoding/json"
	"os"
	"input/cstylecomments"
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
		CleanFile(fileName, language)
	}
}

func CleanFile(fileName, language string) {
	raw, err := ioutil.ReadFile(fileName)
	if err != nil {
		panic(err)
	}
	fileContent := bytes.NewBuffer(raw).String()

	fileContent = RouteToLanguageCleaner(fileContent, language)
	ioutil.WriteFile(fileName, []byte(fileContent), os.ModePerm)
}

func RouteToLanguageCleaner(fileContent, language string) string {
	switch language {
	case "c", "c++", "java", "scala", "kotlin", "go":
		 return cstylecomments.RemoveFrom(fileContent)
	case "haskell":
		return haskellstylecomments.RemoveFrom(fileContent)
	}

	return fileContent
}
