package input

import (
	"testing"
)

func testRouteToLanguageCleanerSingleLine(t *testing.T, fileContent, language string) {
	javaFileWithComments := fileContent
	output := RouteToLanguageCleaner(javaFileWithComments, language)
	if output != "" {
		t.Error("doesn't clean " + language + " code single line")
	}
}

func testRouteToLanguageCleanerMultiLine(t *testing.T, fileContent, language string) {
	javaFileWithComments := fileContent
	output := RouteToLanguageCleaner(javaFileWithComments, language)
	if output != "" {
		t.Error("doesn't clean " + language + " code multi line")
	}
}

func TestRouteToLanguageCleanerJavaSingleLine(t *testing.T) {
	javaFileWithComments := `//sljdhfgjshfg\n`
	testRouteToLanguageCleanerSingleLine(t, javaFileWithComments, "java")

}

func TestRouteToLanguageCleanerJavaMultiLine(t *testing.T) {
	javaFileWithComments := "/* ajsfhasjhgkjs\n asdjkfhaksjf\n*/\n"
	testRouteToLanguageCleanerMultiLine(t, javaFileWithComments, "java")
}

func TestRouteToLanguageCleanerHaskellSingleLine(t *testing.T) {
	javaFileWithComments := "--sljdhfgjshfg"
	testRouteToLanguageCleanerSingleLine(t, javaFileWithComments, "haskell")
}

func TestRouteToLanguageCleanerHaskellMultiLine(t *testing.T) {
	javaFileWithComments := "{-ajsfhasjhgkjs asdjkfhaksjf-}"
	testRouteToLanguageCleanerMultiLine(t, javaFileWithComments, "haskell")
}
