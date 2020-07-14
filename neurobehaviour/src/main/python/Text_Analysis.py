from com.chaquo.python import Python
import nltk
import os

#downloading nltk-data from Chaquopy repo
nltk.download('punkt')
print ('downloading NLTK data...')

#downloading tagger resource
nltk.download('averaged_perceptron_tagger')

#downloading movie reviews
nltk.download('movie_reviews')

from textblob import TextBlob
from textblob import Blobber
from textblob.sentiments import NaiveBayesAnalyzer
tb = Blobber(analyzer=NaiveBayesAnalyzer())


def textBlobAnalyzer(mensaje, analyzer):
    
    original = []
    words = []
    tags = []
    sentiments = []
    
    mensaje = TextBlob(mensaje)
    
    original.append(mensaje.words)
    
    #detecting language of original message
    language = mensaje.detect_language()
    print(language)
    if language != "en":
        mensaje = mensaje.translate(to = "eng")
        print(mensaje)
    
    words.append(mensaje.words)
    tags.append(mensaje.tags)
    if analyzer == "pa":
        sentiments.append(mensaje.sentiment)
    elif analyzer == "nb":
        sentiments.append(tb(str(mensaje)).sentiment)
    return  original, words, tags, sentiments
    