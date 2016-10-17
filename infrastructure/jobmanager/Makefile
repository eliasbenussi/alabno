SOURCE_DIR	:= src
OUTPUT_DIR	:= bin 
CLASSPATHS      := -cp .:bin:lib/json-simple-1.1.jar:sample/

FIND	:= find
RM	:= rm -rf
MKDIR	:= mkdir -p
JAVA	:= java
JAVAC	:= javac

JFLAGS	:= -sourcepath $(SOURCE_DIR) -d $(OUTPUT_DIR)

all: rules

rules:
	- $(FIND) $(SOURCE_DIR) -name '*.java' > $@
	- $(MKDIR) $(OUTPUT_DIR)
	- $(JAVAC) $(CLASSPATHS) $(JFLAGS) @$@
	- rm rules

test: rules
	$(JAVA) $(CLASSPATHS) jobmanager.tests.Test

clean:
	rm -rf $(OUTPUT_DIR)
	- rm rules

.PHONY: clean
