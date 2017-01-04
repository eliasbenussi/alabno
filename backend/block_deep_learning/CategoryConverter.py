
import sys
import os

class CategoryConverter:
    
    # The category_map file is of the form
    # category_key      error_type      annotation
    def __init__(self, _file = 'category_map.csv'):
        self._file = _file
        self.annotation_map = {}
        self.error_map = {}
        self.category_list = []

        for line in self._file:
            splitted = line.split('\t')
            
            # Check every line is well formed
            if len(splitted) != 3:
                print ('[Category converter] Wrong input: {}'.format(line))
                continue
                
            self.error_map[splitted[0]] = splitted[1]          
            self.annotation_map[splitted[0]] = splitted[2] 

            # The category number corresponds to the index
            # of a category in the list
            self.category_list.append(splitted[0])

