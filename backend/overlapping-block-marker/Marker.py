
import MLUtils
from Splitter import Script_Blocks_Container, Block
from CategoryConverter import CategoryConverter
from Classifier import Classifier
import sys
import json

class Annotation:
    
    def __init__(self, ann_text, file_name, line_n, char_n, error_t):
        self.ann_text = ann_text
        self.file_name = file_name
        self.line_n = line_n
        self.char_n = char_n
        self.error_t = error_t
   
    # Return in form suitable for json
    def get_json(self):
        ann_json = {
            'errortype': self.error_t,
            'filename': self.file_name,
            'lineno': self.line_n,
            'charno': self.char_n,
            'text': self.ann_text
        }
        return ann_json

class Marker:
    
    def __init__(self, training_f, sources, category_file = 'category_map.csv', sample_size =550):
        self.category_converter = CategoryConverter(category_file)
        self.training_f = training_f
        self.sample_size = sample_size
        self.sources = sources
        self.classifier = Classifier(self.format_training_file())

    # Format parsed file.
    # Each pair (category, text) is replaced
    # by (category_number, formatted_text)
    def format_training_file(self):
    
        parsed = MLUtils.parse_training_file(self.training_f)
        formatted = []
    
        for (category, text) in parsed:
            cat_numb = self.category_converter.get_category_number(category)
            unpadded = MLUtils.format_line(text)
            padded = MLUtils.pad_float(unpadded, self.sample_size)
            formatted.append((cat_numb, padded))
    
        return formatted           


    # Updates given block with given category and correspondent annotation
    def update_category_and_annotation(self, block, category_n):
        converter = self.category_converter

        # Translate category number to category
        category = converter.get_category(category_n)

        # Assign it to the block
        block.assigned_category = category
        block.annotation = converter.annotation_map[category]
        return block

    def calculate_total_score(self, source_blocks_map):
        
        tot_blocks = 0
        tot_ok = 0

        for source in self.sources:
            blocks = source_blocks_map[source]
            tot_blocks += len(blocks)
            for block in blocks:
                annotation = block.annotation
                if (annotation != '' and
                    (annotation == 'ok' or annotation == 'comment')):
                    tot_ok += 1
        if tot_block == 0:
            return 0
        return (float(tot_ok)/float(tot_blocks)) * 100
    
    def generate_annotations_output(self, source_blocks_map):
        
        all_annotations = []

        # Generate Annotations
        for source in self.sources:
            blocks = source_blocks_map[source]
            for block in blocks:
                ann = block.annotation
                if (ann != '' and ann != 'ok' and ann != 'comment'):
                    e_type = self.category_converter.error_map[block.assigned_category]
                    annotation = Annotation(
                                    ann, source,
                                    block.lineno, 
                                    block.charno,
                                    e_type)
                    all_annotations.append(annotation.get_json())
        return all_annotations

    # Generates json output for the marker
    def generate_json(self, source_blocks_map, error):
        
        annotations = generate_annotations_output(source_blocks_map)
        score = calculate_total_score(source_block_map)
        json_output = {
            'score': score,
            'annotations': annotations,
            'error': error
        }
        return json.dumps(json_output)

    def write_output(self, output_f):
        if self.output != None:
            _file = open(output_f, 'w')
            json.dump(self.output, _file)
            _file.close()

    def mark():
        
        # Holds mapping between source and file's blocks
        partial_outputs = {}
        error = None

        # Iterate through files to classify
        for source in self.sources:

            # Get blocks
            splitter = Script_Blocks_Container(source, self.sample_size)
            splitter.split()
            blocks = splitter.container
        
            to_classify = []
            for block in blocks:
                block.format_content()
                to_classify.append(block.formatted_content)
            
            # Classify
            try:
                guess = self.classifier.predict(to_classify)
            except Exception as exception:
                error = str(exception)
                break

            # Assign each category to appropriate block in order
            i = 0
            for guess in guesses:
                blocks[i] = update_category_and_annotation(block[i], guess)
                i += 1
            
            partial_outputs[source] = blocks
        
        self.output = generate_json(partial_outputs, error)
            

