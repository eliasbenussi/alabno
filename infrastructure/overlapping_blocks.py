import os
import sys

# Split source in overlapping blocks
# ensuring lines of same length
class Script_Blocks_Container:
        
    def __init__(self, source_p, step, line_max):
        self.source_p = source_p
        self.step = step
        self.line_max = line_max 
        self.container = []

    def get_container(self):
        return self.container

    def pad_lines(self, lines):
        
        padded_lines = []

        for line in lines:
            _line = line
            if len(_line) < self.line_max:
                padding = [' ' for i in range(self.line_max - len(_line))]
                _line = _line + ''.join(padding)

            padded_lines.append(_line)

        return padded_lines

    def split(self, block_size):

        source_file = open(self.source_p, 'r')
        lines = f.readlines()

        # get all lines of the same length
        # adding trailing spaces if necessary
        lines = pad_lines(lines)

        offset = 0
        while (offset <= (len(lines) - block_size)):
            
            lineno_content_map = {} 
            for i in range(block_size):
                
                # index to access lines list
                index = offset + i
                content = lines[index]

                # add line number and content in block
                lineno_content_map[index + 1] = content

            # add map for this block to container
            self.container.append(lineno_content_map)

            offset = offset + self.step





                
                                
                
                            



