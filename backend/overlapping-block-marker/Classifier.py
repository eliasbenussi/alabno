import ast
import sys
from sklearn.neural_network import MLPClassifier
from sklearn.preprocessing import StandardScaler

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
        self.clf = MLPClassifier(activation='relu', alpha=1e-05, batch_size='auto',
                        beta_1=0.9, beta_2=0.999, early_stopping=False,
                        epsilon=1e-08, hidden_layer_sizes=(5, 2), learning_rate='constant',
                        learning_rate_init=0.001, max_iter=200, momentum=0.9,
                        nesterovs_momentum=True, power_t=0.5, random_state=1, shuffle=True,
                        solver='lbfgs', tol=0.0001, validation_fraction=0.1, verbose=False,
                        warm_start=False)
                
        try: 
            scaler = StandardScaler()
            scaler.fit(self.X)
            transformed_X = scaler.transform(self.X)
            self.clf.fit(transformed_X, self.y)
            self.scaler = scaler
        except Exception as e:
            print (str(e))
            sys.exit(1)

    def predict(self, new_data):
        transformed_data = self.scaler.transform(new_data)
        prediction = self.clf.predict(transformed_data)
        if len(prediction) != len(new_data):
            raise Exception('[Classifier] Classification failed. Aborting.')
        return prediction

 
