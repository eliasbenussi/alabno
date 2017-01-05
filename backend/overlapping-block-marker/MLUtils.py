
# Collection of utilities to manipulate
# files for the MLPClassifier


# Parse file in a list of the form 
# [ (category, text) ]
def parse_training_file(file_path):
    
    parsed = []

    training_file = open(file_path, 'r')
    for line in training_file:
        try:
            splitted = line.replace('\n','').split('\t')
            category = splitted[0]
            text = splitted[1].replace('\\n', '')
            parsed.append((category, text))
        except:
            continue 
    return parsed

# Produces list of integers, 
# ASCII values of the characters in given line
def format_line(line):
    
    formatted = []
    for char in line:
        formatted.append(ord(char))
    return formatted

# Format parsed file.
# Each pair (category, text) is replaced
# by (category_number, formatted_text)
def format_training_file(file_path):
    
    parsed = parse_training_file(file_path)
    formatted = []

    i = 0
    for (category, text) in parsed:
        formatted.append((i, format_line(text)))
        i += 1

    return formatted
