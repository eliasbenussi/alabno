package cstylecomments

import (
	"regexp"
)

func RemoveFrom(fileContent string) string {
	multiLineComment := regexp.MustCompile(`/\*(.*\n*)*\*/\n?`)
	singleLineComment := regexp.MustCompile(`//.*\n?`)
	tmp := multiLineComment.ReplaceAllString(fileContent, "")
	result := singleLineComment.ReplaceAllString(tmp, "")
	return result
}