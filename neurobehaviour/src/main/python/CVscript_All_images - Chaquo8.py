# -*- coding: utf-8 -*-
"""
Created on Thu Feb 27 17:37:42 2020

@author: lucarri
"""


import os
import matplotlib.pyplot as plt
import cv2
import numpy as np
import tensorflow.keras as k
from tensorflow.keras.models import model_from_json
from tensorflow.keras.metrics import categorical_accuracy

#for stack trace
import traceback


def current_path():
    
    return os.path.dirname(os.path.realpath(__file__))


#cargar el modelo:
def baseline_model_saved(dirpath):
    #load json and create model
    print("loading json")
    json_file = open(dirpath + '/model_4layer_2_2_pool.json', 'r')
    print("reading json")
    loaded_model_json = json_file.read()
    json_file.close()
    print("loading model")
    try:
        model_from_json(loaded_model_json)
    except Exception as ex:
        traceback.print_exception(type(ex), ex, ex.__traceback__)
    model = model_from_json(loaded_model_json)
    #load weights from h5 file
    print("loading weights")
    model.load_weights(dirpath + "/model_4layer_2_2_pool.h5")
    print("compiling model")
    model.compile(optimizer='adam', loss='binary_crossentropy', metrics=[categorical_accuracy])
    return model


def faces(pic):
    #Current path for files:
    dirpath = current_path()

    #absolute path to picture
    path = pic
    
    print("Python path")
    
    emotion = []
    scoreEmotion = []
    
    #Clear Keras session
    k.backend.clear_session()
            
    img = cv2.imread(path)
    img_grey = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    faceCascade = cv2.CascadeClassifier(dirpath + "/haarcascade_frontalface_default.xml")
    faces = faceCascade.detectMultiScale(
        img_grey,
        scaleFactor=1.3,
        minNeighbors=3,
        minSize=(30, 30)
    )
        
    #cantidad de caras
    print("[INFO] Found {0} Faces.".format(len(faces)))
    
    for (x, y, w, h) in faces:
        cv2.rectangle(img, (x, y), (x + w, y + h), (0, 255, 0), 2)
    #img_plot = plt.imshow(cv2.cvtColor(img, cv2.COLOR_BGR2RGB))
    #plt.show()
    
    model = baseline_model_saved(dirpath)
    label_map = ['Anger', 'Disgust', 'Fear', 'Happy', 'Sad', 'Surprise', 'Neutral']
    
    print("Keras - emotions model")
    
    	#emoción de cada cara - keras:
    for (x, y, w, h) in faces:   
        roi_color = img_grey[y:y + h, x:x + w]
        print("[INFO] Face found") 
        X = np.array(roi_color[:,:])
        X = cv2.resize(X, (48,48))
        X = np.expand_dims(X, axis = 0)
        X = np.expand_dims(X, axis = 3)
        score = model.predict(X)
        #print(score)
        plt.imshow(cv2.cvtColor(img[y:y + h, x:x + w], cv2.COLOR_BGR2RGB))
		#busca el valor max en el array
        plt.title(label_map[np.argmax(score)]+": " +str(np.max(score)))
        #array to store emotion and score
        emotion.append(label_map[np.argmax(score)])
        scoreEmotion.append(str(np.max(score)))
		#muestra la emoción más probable con el %
        #plt.show()
            
    return len(faces), faces, emotion, scoreEmotion
