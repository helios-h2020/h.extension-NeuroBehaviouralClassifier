# -*- coding: utf-8 -*-
"""
Created on Thu Feb 27 17:37:42 2020

@author: lucarri
"""


import os
import matplotlib.pyplot as plt
import cv2
import numpy as np


import tensorflow.compat.v1 as tf
from tensorflow.compat.v1 import ConfigProto
from tensorflow.compat.v1 import InteractiveSession
config = ConfigProto()
config.gpu_options.allow_growth = True
session = InteractiveSession(config=config)
tf.keras.backend.set_session(tf.Session(config=config))

from keras.models import Sequential
from keras.layers import Dense , Activation , Dropout ,Flatten
from keras.layers.convolutional import Conv2D
from keras.layers.convolutional import MaxPooling2D
from keras.metrics import categorical_accuracy
from keras.models import model_from_json, Model

from keras.optimizers import *
from keras.layers.normalization import BatchNormalization




print(tf.__version__)

def baseline_model_saved():
    #load json and create model
    json_file = open('model_4layer_2_2_pool.json', 'r')
    loaded_model_json = json_file.read()
    json_file.close()
    model = model_from_json(loaded_model_json)
    #load weights from h5 file
    
    model.compile(optimizer='adam', loss='binary_crossentropy', metrics=[categorical_accuracy])
    model.load_weights("model_4layer_2_2_pool.h5")
    
    return model

def baseline_model(num_class):
    # Initialising the CNN
    model = Sequential()

    # 1 - Convolution
    model.add(Conv2D(64,(3,3), border_mode='same', input_shape=(48, 48,1)))
    model.add(BatchNormalization())
    model.add(Activation('relu'))
    model.add(MaxPooling2D(pool_size=(2, 2)))
    model.add(Dropout(0.25))

    # 2nd Convolution layer
    model.add(Conv2D(128,(5,5), border_mode='same'))
    model.add(BatchNormalization())
    model.add(Activation('relu'))
    model.add(MaxPooling2D(pool_size=(2, 2)))
    model.add(Dropout(0.25))

    # 3rd Convolution layer
    model.add(Conv2D(512,(3,3), border_mode='same'))
    model.add(BatchNormalization())
    model.add(Activation('relu'))
    model.add(MaxPooling2D(pool_size=(2, 2)))
    model.add(Dropout(0.25))

    # 4th Convolution layer
    model.add(Conv2D(512,(3,3), border_mode='same'))
    model.add(BatchNormalization())
    model.add(Activation('relu'))
    model.add(MaxPooling2D(pool_size=(2, 2)))
    model.add(Dropout(0.25))


    # Flattening
    model.add(Flatten())

    # Fully connected layer 1st layer
    model.add(Dense(256))
    model.add(BatchNormalization())
    model.add(Activation('relu'))
    model.add(Dropout(0.25))


    # Fully connected layer 2nd layer
    model.add(Dense(512))
    model.add(BatchNormalization())
    model.add(Activation('relu'))
    model.add(Dropout(0.25))

    model.add(Dense(num_class, activation='sigmoid'))

    #model.compile(optimizer='adam', loss='binary_crossentropy', metrics=[categorical_accuracy])
    return model

#model = baseline_model(num_class=7)
#model.compile(optimizer='adam', loss='binary_crossentropy', metrics=[categorical_accuracy])

#model.load_weights("model_4layer_2_2_pool.h5")

model = baseline_model_saved()
label_map = ['Anger', 'Disgust', 'Fear', 'Happy', 'Sad', 'Surprise', 'Neutral']
path = './images/'

for imgPath in os.listdir(path):
    
    img = cv2.imread(path + imgPath)
    img_grey = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    
    faceCascade = cv2.CascadeClassifier(cv2.data.haarcascades + "haarcascade_frontalface_default.xml")
    faces = faceCascade.detectMultiScale(
        img_grey,
        scaleFactor=1.3,
        minNeighbors=3,
        minSize=(30, 30)
    )
    
    print("[INFO] Found {0} Faces.".format(len(faces)))
    for (x, y, w, h) in faces:
        cv2.rectangle(img, (x, y), (x + w, y + h), (0, 255, 0), 2)
    plt.imshow(cv2.cvtColor(img, cv2.COLOR_BGR2RGB))
    plt.show()
        
    for (x, y, w, h) in faces:   
        roi_color = img_grey[y:y + h, x:x + w]
        #print("[INFO] Face found.") 
        X = np.array(roi_color[:,:])
        X = cv2.resize(X, (48,48))
        X = np.expand_dims(X, axis = 0)
        X = np.expand_dims(X, axis = 3)
        score = model.predict(X)
        #print(score)
        plt.imshow(cv2.cvtColor(img[y:y + h, x:x + w], cv2.COLOR_BGR2RGB))
        plt.title(label_map[np.argmax(score)]+": " +str(np.max(score)))
        plt.show()
