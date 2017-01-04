
import MLUtils
import CategoryConverter
import Splitter
import Classifier
import Marker
import unittest

# Test module for the microservice

class BlockDeepMarkerTest(unittest.TestCase):
    
    # CategoryConverter ==============================
    def test_get_right_category_number(self):
        print 'Testing Category Converter...'
        converter = CategoryConverter.CategoryConverter('testFiles/category_map_test.csv')
        self.assertIs(converter.get_category_number('ok'), 0)
        self.assertIs(converter.get_category_number('magic'), 29)
        self.assertIs(converter.get_category_number('minustenk'), 30)
        self.assertEquals(converter.get_category(30), 'minustenk')
        self.assertEquals(converter.get_category(3), 'unimplemented')
    
    def test_maps(self):
        converter = CategoryConverter.CategoryConverter('testFiles/category_map_test.csv')
        annotation_m = converter.annotation_map
        error_m = converter.error_map
        self.assertEquals(annotation_m['ok'], 'ok')
        self.assertEquals(annotation_m['hs9527549314'], 'don\'t write rubbish')
        self.assertEquals(error_m['ok'], 'unknown')
        self.assertEquals(error_m['hs9527549314'], 'semantic')

    # MLUtils =======================================
    def test_parse_training(self):
        print 'Testing Utils... '
        parsed = MLUtils.parse_training_file('testFiles/training.txt')
        self.assertTrue(('ok', ' &#124; otherwise = y') in parsed)
        self.assertTrue(('comment', '-- Geometric sequence') in parsed)

    
if __name__ == '__main__':
    unittest.main()
