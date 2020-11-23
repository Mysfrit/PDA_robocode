import os, io
import numpy as np
import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt

os.chdir(os.path.dirname(os.path.abspath(__file__)))
dataset = pd.read_csv('data.csv')
print(dataset.head(5))
print(dataset.describe(include='all'))

# print(sns.pairplot(dataset, hue='hit'))

x = dataset.iloc[:,0:12]
y = dataset.iloc[:,12]

from sklearn.preprocessing import StandardScaler
sc = StandardScaler()
x = sc.fit_transform(x)

from sklearn.model_selection import train_test_split
x_train, x_test, y_train, y_test = train_test_split(x, y, test_size=0.3)

from keras import Sequential
from keras.layers import Dense

classifier = Sequential()
#First Hidden Layer
classifier.add(Dense(4, activation='relu', kernel_initializer='random_normal', input_dim=12)) #Second  Hidden Layer
classifier.add(Dense(4, activation='relu', kernel_initializer='random_normal')) #Output Layer
classifier.add(Dense(1, activation='sigmoid', kernel_initializer='random_normal'))

#Compiling the neural network
classifier.compile(optimizer ='adam', loss='binary_crossentropy', metrics =['accuracy'])

classifier.fit(x_train,y_train, batch_size=10, epochs=100)

eval_model=classifier.evaluate(x_train, y_train)
print(eval_model)

y_pred=classifier.predict(x_test)
y_pred =(y_pred>0.5)

from sklearn.metrics import confusion_matrix
cm = confusion_matrix(y_test, y_pred)
print(cm)