package pythonstylecomments

import (
	"regexp"
)

//TODO: add multiline comments
func RemoveFrom(fileContent string) string {
	comment := regexp.MustCompile(`#.*\n`)
	result := comment.ReplaceAllString(fileContent, "")
	return result
}
