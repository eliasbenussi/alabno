
import Splitter
import CategoryConverter
import Classifier
import sys

class Marker:
    
    def __init__(self, training_f, category_map_f, sources):
        self.category_converter = CategoryCoverter(category_map_f)
        self.classifier = Classifier(training_f)
        self.sources = sources    

    def mark():
        
        # Holds mapping between source and 
        partial_outputs = {}

        for source in self.sources:
            splitter = Script_Blocks_Container(source)
            splitter.split()
            blocks = splitter.containe
            converter = self.category_converterr
        
            to_classify = []
            for block in blocks:
                block.format_content()
                to_classify.append(block.formatted_content)
            
            # Classify
            guesses = self.classifier.predict(to_classify)
        
            i = 0
            for guess in guesses:
                blocks[i].assigned_category = converter.get_category_number(guess)
                i += 1
            
            outputs[source] = blocks

        json_final_output = generate_json(partial_outputs)
            


        


