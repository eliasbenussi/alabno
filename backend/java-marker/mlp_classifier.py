import ast
import sys
from sklearn.neural_network import MLPClassifier

# Initialize training data and target categories
# PRE: the samples passed have the same length (i.e. they
# were padded appropriately before.
# Also, y_train is assumed to have same length of X_train.
try:
    X_train = ast.literal_eval(sys.argv[1])
    y_train = ast.literal_eval(sys.argv[2])
    new_data = ast.literal_eval(sys.argv[3])
except:
    print 'Usage: <training sample(s)> <training label(s)> <new data>'
    print 'Each input must be passed without white spaces between the characters'
    print 'i.e. python mlp_classifier.py [[0,0],[1,1]] [1,0] [[2,2],[-2,-1]]'
    sys.exit(1)

# Initialize the classifier (stochastic gradient-descent)
classifier = MLPClassifier(solver='adam', alpha=1e-5, 
                           hidden_layer_sizes=(5, 2), random_state=1)

# Fit training data to model
try:
    print classifier.fit(X_train, y_train)
except:
    print 'Training data is malformed'
    sys.exit(1)

# Infer category
try: 
    print classifier.predict(new_data)
except:
    print 'Prediction failed'
    sys.exit(1)
