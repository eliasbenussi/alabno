import ast
import sys
from sklearn.neural_network import MLPClassifier

class Classifier:
    
    def __init__(self, formatted_training):
        
        self.X = []
        self.y = []
        self.formatted_training = formatted_training
        
        unpadded_X = []
        for (cat_numb, formatted_text) in formatted_training:
            # Ensure all values in X are float
            float_formatted = map(lambda n: float(n), formatted_text)
            self.X.append(float_formatted)
            self.y.append(cat_numb)
        
        
        # Initialize the classifier (stochastic gradient-descent)
        self.clf = MLPClassifier(solver='adam', alpha=1e-5, 
                                   hidden_layer_sizes=(5, 2), random_state=1)
        
        try: 
            self.clf.fit(self.X, self.y)
        except Exception as e:
            print (str(e))
            sys.exit(1)

    def predict(self, new_data):
        prediction = self.clf.predict(new_data)
        if len(prediction) != len(new_data):
            raise Exception('[Classifier] Classification failed. Aborting.')
        return prediction

 
