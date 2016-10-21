package cstylecomments

import (
	"regexp"
)

//TODO: reason about newlines
//TODO: make preprocessor that passes single files?

func RemoveFrom(fileContent string) string {
	multiLineComment := regexp.MustCompile(`/\*(.*\n*)*\*/\n?`)
	singleLineComment := regexp.MustCompile(`//.*\n?`)
	tmp := multiLineComment.ReplaceAllString(fileContent, "")
	result := singleLineComment.ReplaceAllString(tmp, "")
	return result
}