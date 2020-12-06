import os, io
import numpy as np
import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt

from keras import Sequential
from keras.layers import *
from keras.models import load_model
from sklearn.metrics import confusion_matrix
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler

# CREATING THE MODEL AND ITS TOPOLOGY
def createModelWithTopology():
    model = Sequential() # Sequential is one of two main Keras models (the other one is Model)

    # Topology 1:
    # # First Hidden Layer
    # model.add(Dense(4, activation='relu', kernel_initializer='random_normal', input_dim=columns))
    # # Second  Hidden Layer
    # model.add(Dense(4, activation='relu', kernel_initializer='random_normal'))
    # # Output Layer - Sigmoid is commonly used in binary classification
    # model.add(Dense(1, activation='sigmoid', kernel_initializer='random_normal'))

    # Topology 2:
    model.add(Dense(8, activation='relu', kernel_initializer='random_normal', input_dim=columns))
    model.add(Dense(8, activation='relu', kernel_initializer='random_normal'))
    model.add(Dropout(0.3))
    model.add(Dense(8, activation='relu', kernel_initializer='random_normal'))
    model.add(Dropout(0.3))
    model.add(Dense(1, activation='sigmoid', kernel_initializer='random_normal'))

    # Compiling the neural network
    # binary_crossentropy is used for calculation the loss function between actual output vs predicted output
    model.compile(optimizer='adam', loss='binary_crossentropy', metrics=['accuracy'])

    return model

# Set working directory to be a local directory (files will be taken from same directory as this script)
os.chdir(os.path.dirname(os.path.abspath(__file__)))

dataset = pd.read_csv('robocode_data.csv')
columns = dataset.shape[1] - 1

print("---------- first_5_rows --------------\n", dataset.head(5)) # Returns first n rows
print("---------- statistics_summary --------------\n", dataset.describe(include='all')) # Generate various summary statistics, excluding NaN values.

# creating input features and target variables
x = dataset.iloc[:,0:columns] # first argument: all rows; second argument: zero to twelve columns
y = dataset.iloc[:,columns]

# standardize different input scales
sc = StandardScaler()
x = sc.fit_transform(x)

# Split the data
x_train, x_test, y_train, y_test = train_test_split(x, y, test_size=0.25)

# Once the model is created, you can config the model with losses and metrics with model.compile(),
# train the model with model.fit(), or use the model to do prediction with model.predict().
model = createModelWithTopology()

# Fit the model
model.fit(x_train, y_train, batch_size=5, epochs=100)

# Return the loss value & metrics values for the model in test mode
eval_model=model.evaluate(x_train, y_train)
print("---------- eval_model --------------\n", eval_model)

# Predict output for our test dataset - make it a boolean based on its value
y_pred=model.predict(x_test)
y_pred=(y_pred>0.5)

# Check the accuracy of NN
# left-upper and right-down are the values we want (true-positive and true-negative)
# přidat i do prezentace
cm = confusion_matrix(y_test, y_pred)
print("---------- confusion_matrix --------------\n", cm)

model.save(
    'savedModel.h5',
    overwrite=True,
    include_optimizer=True,
)