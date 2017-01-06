import os
import sys
import MLUtils

class Block:
    
    def __init__(self, lineno, charno):
        self.lineno = lineno
        self.charno = charno

        # Default values
        self.content = ''
        self.formatted_content = []
        self.assigned_category = -1
        self.annotation = ''

    def format_content(self):
        self.formatted_content = MLUtils.format_line(self.content)

    def __str__(self):
        return '[{}, {}]'.format(self.lineno, self.content)

# Split source in overlapping blocks
# ensuring lines of same length
class Script_Blocks_Container:
        
    def __init__(self, source_p, BLOCK_SIZE, BLOCK_OFFSET = 50):
        self.source_p = source_p
        self.BLOCK_SIZE = BLOCK_SIZE
        self.BLOCK_OFFSET = BLOCK_OFFSET
        self.container = []

    def split(self):
        
        source_file = open(self.source_p, 'r')
        inverse_map = {}
        char_index = 0
        line_index = 1

        # Initialize inverse map
        for line in source_file:
            for c in line:
                inverse_map[char_index] = line_index
                char_index += 1
            line_index += 1
        
        source_file.close()
        source_file = open(self.source_p, 'r')
        source_text = source_file.read()

        all_blocks = []
        i = 0
        while i < len(source_text):
            j = 0
            current_block = Block(inverse_map[i], i)
            while j < self.BLOCK_SIZE and i < len(source_text):
                current_block.content += source_text[i]
                i += 1
                j += 1
            current_block.content = MLUtils.pad(current_block.content, self.BLOCK_SIZE)
            all_blocks.append(current_block)
            if i >= len(source_text):
                break
            i -= self.BLOCK_SIZE - self.BLOCK_OFFSET

        source_file.close()
        self.container = all_blocks


# Main ====================================================

#block_container = Script_Blocks_Container("testFiles/split_test.txt")
#block_container.split()
#_container = block_container.container
#
#for b in container:
#    print b.__str__()



