import tensorflow as tf
import numpy as np
import pandas as pd
from tensorflow import keras
from tensorflow.keras import layers

dataframe = pd.read_csv('data.csv')
print(dataframe.shape)
print(dataframe.head())

val_dataframe = dataframe.sample(frac=0.2, random_state=1337)
train_dataframe = dataframe.drop(val_dataframe.index)

print(
    "Using %d samples for training and %d for validation"
    % (len(train_dataframe), len(val_dataframe))
)

def dataframe_to_dataset(dataframe):
    dataframe = dataframe.copy()
    labels = dataframe.pop("hit")
    ds = tf.data.Dataset.from_tensor_slices((dict(dataframe), labels))
    ds = ds.shuffle(buffer_size=len(dataframe))
    return ds

train_ds = dataframe_to_dataset(train_dataframe)
val_ds = dataframe_to_dataset(val_dataframe)

for x, y in train_ds.take(1):
    print("Input:", x)
    print("Target:", y)

train_ds = train_ds.batch(32)
val_ds = val_ds.batch(32)

from tensorflow.keras.layers.experimental.preprocessing import Normalization
from tensorflow.keras.layers.experimental.preprocessing import CategoryEncoding
from tensorflow.keras.layers.experimental.preprocessing import StringLookup


def encode_numerical_feature(feature, name, dataset):
    # Create a Normalization layer for our feature
    normalizer = Normalization()

    # Prepare a Dataset that only yields our feature
    feature_ds = dataset.map(lambda x, y: x[name])
    feature_ds = feature_ds.map(lambda x: tf.expand_dims(x, -1))

    # Learn the statistics of the data
    normalizer.adapt(feature_ds)

    # Normalize the input feature
    encoded_feature = normalizer(feature)
    return encoded_feature


def encode_string_categorical_feature(feature, name, dataset):
    # Create a StringLookup layer which will turn strings into integer indices
    index = StringLookup()

    # Prepare a Dataset that only yields our feature
    feature_ds = dataset.map(lambda x, y: x[name])
    feature_ds = feature_ds.map(lambda x: tf.expand_dims(x, -1))

    # Learn the set of possible string values and assign them a fixed integer index
    index.adapt(feature_ds)

    # Turn the string input into integer indices
    encoded_feature = index(feature)

    # Create a CategoryEncoding for our integer indices
    encoder = CategoryEncoding(output_mode="binary")

    # Prepare a dataset of indices
    feature_ds = feature_ds.map(index)

    # Learn the space of possible indices
    encoder.adapt(feature_ds)

    # Apply one-hot encoding to our indices
    encoded_feature = encoder(encoded_feature)
    return encoded_feature


def encode_integer_categorical_feature(feature, name, dataset):
    # Create a CategoryEncoding for our integer indices
    encoder = CategoryEncoding(output_mode="binary")

    # Prepare a Dataset that only yields our feature
    feature_ds = dataset.map(lambda x, y: x[name])
    feature_ds = feature_ds.map(lambda x: tf.expand_dims(x, -1))

    # Learn the space of possible indices
    encoder.adapt(feature_ds)

    # Apply one-hot encoding to our indices
    encoded_feature = encoder(feature)
    return encoded_feature

# Numerical features


ourX = keras.Input(shape=(1,), name="ourX")
ourY = keras.Input(shape=(1,), name="ourY")
ourHeading = keras.Input(shape=(1,), name="ourHeading")
ourRadarHeading = keras.Input(shape=(1,), name="ourRadarHeading")
distanceToTarget = keras.Input(shape=(1,), name="distanceToTarget")
ourVelocity = keras.Input(shape=(1,), name="ourVelocity")
ourEnergy = keras.Input(shape=(1,), name="ourEnergy")
enemyX = keras.Input(shape=(1,), name="enemyX")
enemyY = keras.Input(shape=(1,), name="enemyY")
enemyHeading = keras.Input(shape=(1,), name="enemyHeading")
enemyVelocity = keras.Input(shape=(1,), name="enemyVelocity")
enemyEnergy = keras.Input(shape=(1,), name="enemyEnergy")

all_inputs = [
    ourX,
    ourY,
    ourHeading,
    ourRadarHeading,
    distanceToTarget,
    ourVelocity,
    ourEnergy,
    enemyX,
    enemyY,
    enemyHeading,
    enemyVelocity,
    enemyEnergy
]

# Numerical features
ourX_encoded = encode_numerical_feature(ourX, "ourX", train_ds)
ourY_encoded = encode_numerical_feature(ourY, "ourY", train_ds)
ourHeading_encoded = encode_numerical_feature(ourHeading, "ourHeading", train_ds)
ourRadarHeading_encoded = encode_numerical_feature(ourRadarHeading, "ourRadarHeading", train_ds)
distanceToTarget_encoded = encode_numerical_feature(distanceToTarget, "distanceToTarget", train_ds)
ourVelocity_encoded = encode_numerical_feature(ourVelocity, "ourVelocity", train_ds)
ourEnergy_encoded = encode_numerical_feature(ourEnergy, "ourEnergy", train_ds)
enemyX_encoded = encode_numerical_feature(enemyX, "enemyX", train_ds)
enemyY_encoded = encode_numerical_feature(enemyY, "enemyY", train_ds)
enemyHeading_encoded = encode_numerical_feature(enemyHeading, "enemyHeading", train_ds)
enemyVelocity_encoded = encode_numerical_feature(enemyVelocity, "enemyVelocity", train_ds)
enemyEnergy_encoded = encode_numerical_feature(enemyEnergy, "enemyEnergy", train_ds)

all_features = layers.concatenate(
    [
        ourX_encoded,
        ourY_encoded,
        ourHeading_encoded,
        ourRadarHeading_encoded,
        distanceToTarget_encoded,
        ourVelocity_encoded,
        ourEnergy_encoded,
        enemyX_encoded,
        enemyY_encoded,
        enemyHeading_encoded,
        enemyVelocity_encoded,
        enemyEnergy_encoded,
    ]
)

x = layers.Dense(32, activation="relu")(all_features)
x = layers.Dropout(0.5)(x)
output = layers.Dense(1, activation="sigmoid")(x)
model = keras.Model(all_inputs, output)
model.compile("adam", "binary_crossentropy", metrics=["accuracy"])

#keras.utils.plot_model(model, show_shapes=True, rankdir="LR")

model.fit(train_ds, epochs=50, validation_data=val_ds)