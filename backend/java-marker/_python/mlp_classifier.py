import ast
import sys
import MLFileUtils
from sklearn.neural_network import MLPClassifier

class Classifier:
    
    def __init__(self, training_file_path):
        
        self.X = []
        self.y = []

        formatted_training = MLFileUtils.format_training_file(training_file_path) 
        for (cat_numb, formatted_text) in formatted_training:
            self.X.append(formatted_text)
            self.y.append(cat_numb)
        
        # Initialize the classifier (stochastic gradient-descent)
        self.clf = MLPClassifier(solver='adam', alpha=1e-5, 
                                   hidden_layer_sizes=(5, 2), random_state=1)
        try: 
            self.clf.fit(self.X, self.y)
        except:
            print ('Training data from {} is malformed. Aborting.'.format(training_file_path))
            sys.exit(1)

    def predict(self, new_data):
        prediction
        try:
            prediction = self.clf.predict(new_data)
        except:
            print 'New data provided is malformed. Aborting.'
            sys.exit(1)
        

#    print 'Usage: <training sample(s)> <training label(s)> <new data>'
#    print 'Each input must be passed without white spaces between the characters'
#    print 'i.e. python mlp_classifier.py [[0,0],[1,1]] [1,0] [[2,2],[-2,-1]]'
 
