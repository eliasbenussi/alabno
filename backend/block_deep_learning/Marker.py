
import Splitter
import CategoryConverter
import Classifier
import sys

class Marker:
    
    def __init__(self, training_f, category_map_f, source_f):
        self.category_converter = CategoryCoverter(category_map_f)
        self.classifier = Classifier(training_f)
        self.splitter = Script_Block_Container(source_f)

    def mark():
        
        self.splitter.split()
        blocks = self.splitter.container
        
        for block in blocks:
            block.format_content()
            


        


