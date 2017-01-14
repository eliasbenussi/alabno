
import os
import sys
from Splitter import Block, Script_Blocks_Container

# Args: <file_path> <line_number>
file_path = sys.argv[1]
line_no = sys.argv[2]

# By default, training file and all 
# the source files would be splitted
# in the marker in chunks of 200 characters.
DEFAULT_BLOCK_SIZE = 200

splitter = Script_Blocks_Container(file_path, DEFAULT_BLOCK_SIZE)
splitter.split()
desired_line = splitter.get_block_content_at_line(line_no)
#for block in splitter.container:
#    print block.__str__()
print desired_line
