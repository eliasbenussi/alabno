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

        for line in lines:
            if len(line) < self.line_max:
                padding = [' ' for i in range(self.line_max - len(line))]
                line = line + ''.join(padding)

    def split(self, block_size):

        source_file = open(self.source_p, 'r')
        lines = f.readlines()
        pad_lines(lines)

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





                
                                
                
                            



