package eu.h2020.helios_social.modules.neurobehaviour;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import eu.h2020.helios_social.core.messaging.HeliosMessage;
import eu.h2020.helios_social.core.messaging.HeliosMessageListener;
import eu.h2020.helios_social.core.messaging.HeliosTopic;

//LAB - Listener to send data to Neurobehaviour module
import eu.h2020.helios_social.modules.neurobehaviour.NeurobehaviourListener;

public class SentimentalAnalysis extends AppCompatActivity {

    String script = "CVscript_All_images";  //main/python/CVscript_All_images.py
    String textScript = "Text_Analysis";  //main/python/Text_Analysis.py

    ImageView imageView;

    //File with picture to analyze
    String picture;
    //File with audio to analyze
    String audioFile;

    //real image width
    private Integer imgWidth = 0;
    private Integer imgViewWidth = 0;
    private Integer realImgWidth = 0;
    private Float scale = 0f;

    private HeliosMessage heliosMessage;
    private String oldFileName;
    private String newFileName;

    private String time;
    private long timestamp;

    //LAB - Neurobehaviour listener
    private NeurobehaviourListener neuroListener = new NeurobehaviourListener();

    public void runThread(final Context context, final String fileName, final HeliosMessageListener messageListener, final HeliosTopic topic, final HeliosMessage message, final String senderName) {

        new Thread() {
            public void run() {
                    try {
                        // Current time in Milliseconds
                        long actualTimeMillis = System.currentTimeMillis();
                        timestamp = actualTimeMillis / 1000;

                        // Time with format hh:mm:ss:mmm
                        time = getDate(actualTimeMillis);

                        Thread.sleep(500);

                        //LAB - used to return message to chat activity with analyzed image
                        //see end of paitingImage function
                        heliosMessage = message;

                        if (fileName != null) {

                            //Message is a picture or audio file
                            String fileExt = getExtension(fileName);
                            Log.v("cv", "SENTIMENTAL ANALYSIS - File extension: " + fileExt);

                            switch (fileExt) {
                                case "jpg":
                                case "png":
                                    //Message is a picture
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context, "Analizando la imagen", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    //UPV - Image sentimental analysis
                                    picture = context.getExternalFilesDir(null) + "/HELIOS/" + fileName;
                                    oldFileName = fileName;
                                    Log.v("cv", "Picture path: " + picture);

                                    //Get current image width
                                    Drawable drawable = Drawable.createFromPath(picture);
                                    //imgWidth = drawable.getIntrinsicWidth();
                                    //imgViewWidth = imageView.getWidth();
                                    //realImgWidth = realImageWidth(picture);
                                    //scale = (float)(imgWidth / imgViewWidth);
                                    //scale = (float)imgWidth / (float)realImgWidth;

                                    scale = 1f;

                                    pythonAnalysis(context, script, picture, scale, imageView, messageListener, topic, senderName);
                                    break;

                                case "m4a":
                                    //Message is an Audio
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context, "Analizando el mensaje de voz", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    //UPV - Audio sentimental analysis
                                    audioFile = context.getExternalFilesDir(null) + "/HELIOS/" + fileName;
                                    Log.v("audio", "AUDIO SENTIMENTAL ANALYSIS · Path: " + audioFile);

                                    //Calling method to analyze audio

                                    break;
                            }

                        } else {
                            //Text Message
                            Log.v("text", "File null: TEXT MESSAGE");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "Analizando el texto", Toast.LENGTH_SHORT).show();
                                }
                            });

                            String msgFromHeliosMessage = message.getMessage();
                            String[] msgArray = msgFromHeliosMessage.split("\"");
                            String msgText = msgArray[3];
                            Log.v("text", "Message text: " + msgText);

                            textAnalysis(context, msgText, messageListener, topic, senderName);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        }.start();
    }

    public int realImageWidth(String resource) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(resource, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String imageType = options.outMimeType;
        return imageWidth;
    }

    private String getExtension(String word) {
        if (word.length() == 3) {
            return word;
        } else if (word.length() > 3) {
            return word.substring(word.length() - 3);
        } else {
            // whatever is appropriate in this case
            throw new IllegalArgumentException("word has less than 3 characters!");
        }
    }

    //Text sentimental analysis
    private void textAnalysis (Context context, String message, HeliosMessageListener messageListener, HeliosTopic topic, String senderName) {

        Log.v("text", "Calling to Python script - Text analysis");

        //Si no se ha iniciado con el proyecto, iniciamos Python
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(context));
        }

        //Python object instance:
        Python py = null;
        py = Python.getInstance();

        //Object with Python file:
        PyObject mod = py.getModule(textScript);

        Log.v("text", "Python object OK");

        // returns a list with 4 text arrays:
        // original, words, tags, sentiments
        //
        // call to script:
        // textBlobAnalyzer(text to analyze, algorithm > NaivesBayes)

        try (PyObject resultMsg = mod.callAttr("textBlobAnalyzer", message, "nb"))
        {
            Log.v("text", "Python analysis OK");
            Log.v("text", resultMsg.toString());

            List<PyObject> list = resultMsg.asList();
            PyObject originalText = list.get(0);
            Log.v("text", "Texto original: " + originalText.toString());

            PyObject textInEnglish = list.get(1);
            Log.v("text", "Texto en inglés: " + textInEnglish.toString());

            PyObject tags = list.get(2);
            Log.v("text", "Tags: " + tags.toString());

            PyObject emotions = list.get(3);
            Log.v("text", "Emotions and score: " + emotions.toString());

            //LAB - Saving image analysis data to file
            saveTextData(context, originalText, textInEnglish, tags, emotions, senderName);

            //LAB - Showing analyzed text in a new activity
            /*
            List<PyObject> textList = originalText.asList();
            List<PyObject> engList = textInEnglish.asList();
            List<PyObject> tagsList = tags.asList();
            List<PyObject> emotionsList = emotions.asList();

            String text, engText, tagsText, emotionsText;

            text = "";
            for (Integer i=0; i < textList.size(); i++) {
                text += textList.get(i) + " ";
            }
            engText = "";
            for (Integer j=0; j < textList.size(); j++) {
                engText += engList.get(j) + " ";
            }
            tagsText = "";
            for (Integer k=0; k < textList.size(); k++) {
                tagsText += tagsList.get(k) + " ";
            }
            emotionsText = "";
            for (Integer l=0; l < textList.size(); l++) {
                emotionsText += emotionsList.get(l) + " ";
            }

            Intent intent = new Intent(context, AnalyzedTextActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("text", text);
            intent.putExtra("engText", engText);
            intent.putExtra("tags", tagsText);
            intent.putExtra("emotions", emotionsText);
            context.startActivity(intent);

             */

        } catch (Exception e){

            String error = e.getMessage();
            Log.v("text", "Error analisis de texto Python: " + error);
        }

        //Eliminar la referencia al objeto Python
        mod.close();
        Log.v("text", "Python object closed");
    }

    //Audio sentimental analysis
    private void audioAnalysis(Context context, String audioData) {
        saveAudioData(context, audioData);
    }

    //Image sentimental analysis
    private void pythonAnalysis (Context context, String script, String picture, Float scale, ImageView imageView, HeliosMessageListener messageListener, HeliosTopic topic, String senderName) {

        //Si no se ha iniciado con el proyecto, iniciamos Python
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(context));
        }

        //Python object instance:
        Python py = null;
        py = Python.getInstance();

        //Object with Python file:
        PyObject mod = py.getModule(script);

        Log.v("cv", "Python object OK");
        Log.v("cv", "PICTURE: " + picture);

        //returns a list with 4 parameters:
        // 0 - number of faces in the picture
        // 1 - square for each face - square > (x, y, w, h)
        // 2 - array with emotional analysis for each face
        // 3 - array with score of each emotion
        try (PyObject faces = mod.callAttr("faces", picture))
        {
            List<PyObject> list = faces.asList();

            if (list.isEmpty()) Log.v("cv", "La lista de resultados está vacía");
            else {

                Integer numFaces = list.get(0).toInt();
                Log.v("cv", "Número de CARAS en la imagen: " + numFaces.toString());

                PyObject squares = list.get(1);

                PyObject emotions = list.get(2);
                Log.v("cv", "Emotions list: " + emotions.toString());

                PyObject score = list.get(3);
                Log.v("cv", "Emotions score: " + score.toString());

                //Paint bounding box in image faces with emotions and score
                paintingImage(squares, emotions, score, picture, scale, imageView, context, messageListener, topic, senderName);

            } //end else > list is not empty

        } catch (Exception e){

            String error = e.getMessage();
            Log.v("cv", error);
        }

        //Eliminar la referencia al objeto Python
        mod.close();
        Log.v("cv", "Python object closed");

    }

    private void paintingImage(PyObject squares, PyObject emotions, PyObject scores, String imageResource, Float scale, ImageView imageView, Context context, HeliosMessageListener messageListener, HeliosTopic topic, String senderName) {

        Paint currentPaint;
        Paint textPaint;

        String x1st, y1st, x2st, y2st;
        Integer x1, y1, x2, y2;

        String emotion, score;

        Integer ySpace;

        //image like bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(imageResource);
        //Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        //Set stroke style to paint rectangle
        currentPaint = new Paint();
        currentPaint.setDither(true);
        currentPaint.setColor(Color.RED);
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeJoin(Paint.Join.ROUND);
        currentPaint.setStrokeCap(Paint.Cap.ROUND);
        currentPaint.setStrokeWidth(5);

        //Setting paint style to write text over image
        textPaint = new Paint();
        textPaint.setColor(Color.GREEN);
        textPaint.setTextSize(24f*scale);
        textPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)); // Text Overlapping Pattern

        //Data to send results to Neurobehaviour module
        int numFaces = squares.asList().size();
        String emotionsData = "";
        String scoreData = "";

        List<PyObject> list = squares.asList();

        for (Integer i=0; i < list.size(); i++) {

            Log.v("cv", "Square " + i.toString() + ": " + list.get(i).toString());

            List<PyObject> square = list.get(i).asList();

            x1st = square.get(0).toString();
            y1st = square.get(1).toString();
            x2st = square.get(2).toString();
            y2st = square.get(3).toString();

            x1 = Integer.valueOf(x1st);
            y1 = Integer.valueOf(y1st);
            x2 = Integer.valueOf(x2st);
            y2 = Integer.valueOf(y2st);

            List<PyObject> listEmotions = emotions.asList();
            List<PyObject> listScores = scores.asList();
            emotion = listEmotions.get(i).toString();
            score = listScores.get(i).toString();

            //Data to send results to Neurobehaviour module
            emotionsData += emotion + ",";
            scoreData += score + ",";

            //Rect functions > 0,0 in top left corner. left,top > x,y first point. right,bottom > x,y second point.
            //https://stackoverflow.com/questions/46914736/how-canvas-drawrect-draws-a-rectangle/46927389

            //Create a new image bitmap and attach a brand new canvas to it
            Bitmap tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
            Canvas tempCanvas = new Canvas(tempBitmap);

            //Draw the image bitmap into the cavas
            tempCanvas.drawBitmap(bitmap, 0, 0, null);

            //Values from Python: x, y, w, h
            x2 = x1 + x2;
            y2 = y1 + y2;

            //Scaling squares
            x1 = (int)(x1*scale);
            y1 = (int)(y1*scale);
            x2 = (int)(x2*scale);
            y2 = (int)(y2*scale);

            //Space between elements
            ySpace = (int)(23*scale);

            //Drawing rectangle with rounded edges
            tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 5, 5, currentPaint);

            //Drawing emotional texts over image
            tempCanvas.drawText(emotion, x1, (y2 + ySpace), textPaint);

            //Drawing score of each emotion over image
            tempCanvas.drawText(score, x1, (y2 + (ySpace * 2)), textPaint);

            //Attach the canvas to the ImageView
            //BitmapDrawable newImage = new BitmapDrawable(context.getResources(), tempBitmap);
            //imageView.setImageDrawable(newImage);

            tempCanvas.setBitmap(tempBitmap);
            bitmap = tempBitmap;
        }

        Log.v("cv", "Render OK. Scale: " + scale.toString());

        //LAB - Write image to file
        //Remove extension
        String newName = imageResource.substring(0, imageResource.length() - 4);
        //New path to analyzed image
        newName = newName + "-Analysis.jpg";

        //New name to send with the message (new image name without path)
        String fileNameWithoutExt = oldFileName.substring(0, oldFileName.length() - 4);
        newFileName = fileNameWithoutExt + "-Analysis.jpg";

        File newImage = new File(newName);

        //Delete old image
        //newImage.delete();
        //Log.v("cv", "Old image deleted. Writing image");

        try {
            newImage.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.v("cv", "Error writing image: " + e);
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(newImage);
            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
                Log.v("cv", "IMAGE SAVED");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("cv", "Error writing image: " + e);
        }

        //LAB - Re-write sent image with analyzed image to show it in chat activity
        //deleteAndSaveImage(imageResource, newName);

        //LAB - Saving image analysis data to file
        saveImageData(context, numFaces, emotionsData, scoreData, senderName);

        //LAB - Showing analyzed image in a new activity
        /*
        Intent intent = new Intent(context, AnalyzedImageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("imagePath", newName);
        context.startActivity(intent);
        */
    }

    private void deleteAndSaveImage(String oldImage, String newImage) {

        File file = new File(oldImage);

        //Delete old image
        file.delete();
        Log.v("cv", "Old image deleted. Writing image");

        Bitmap bitmap = BitmapFactory.decodeFile(newImage);

        try {
            file.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.v("cv", "Error writing image: " + e);
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(newImage);
            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
                Log.v("cv", "IMAGE SAVED");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("cv", "Error writing image: " + e);
        }

    }


    public String getDate(long time) {
        long mil = time % 1000;
        long s = (time / 1000) % 60;
        long m = (time / (1000 * 60)) % 60;
        long h = (time / (1000 * 60 * 60)) % 24;
        //UTC + 2
        h = h + 2;
        return String.format("%d:%02d:%02d:%03d", h,m,s,mil);
    }

    private void saveImageData (Context context, int numFaces, String emotionsData, String scoreData, String senderName) {
        //Sending data to Neurobehaviour module

                String userName = neuroListener.getUserName();
                Boolean createdFile = neuroListener.GetCsvImageReady();
                if (!createdFile) {
                    neuroListener.createCsv("Image", context, userName);
                    Log.v("storage", "Creating CSV Image File");
                }

                String separator = System.getProperty("line.separator");
                neuroListener.writeImageData(timestamp + ";" + time + ";"+ senderName + ";" + newFileName + ";" + numFaces + ";" + emotionsData + ";" + scoreData + separator);
    }

    private void saveTextData (Context context, PyObject origText, PyObject engText, PyObject tags, PyObject emotions, String senderName) {
        //Sending data to Neurobehaviour module

        String userName = neuroListener.getUserName();
        Boolean createdFile = neuroListener.GetCsvTextReady();
        if (!createdFile) {
            neuroListener.createCsv("Text", context, userName);
            Log.v("text", "Creating CSV Text File");
        } else {
            Log.v("text", "CsvTextReady = true");
        }

        String separator = System.getProperty("line.separator");

        List<PyObject> textList = origText.asList();
        String text = extractList(textList);

        List<PyObject> engList = engText.asList();
        String translatedText = extractList(engList);

        List<PyObject> tagsList = tags.asList();
        String stTags = extractList(tagsList);

        List<PyObject> emotionsList = emotions.asList();
        String stEmotions = extractList(emotionsList);

        //Parse emotions list to extract Classfication and Score
        String classification = stEmotions.substring((stEmotions.indexOf("cation")+8), stEmotions.indexOf("p_pos")-3);
        String positiveScore = stEmotions.substring((stEmotions.indexOf("p_pos=")+6), stEmotions.indexOf("p_neg")-2);
        String negativeScore = stEmotions.substring((stEmotions.indexOf("p_neg=")+6), stEmotions.length()-2);

        neuroListener.writeTextData(timestamp + ";" + time + ";" + senderName + ";" + text + ";" + translatedText + ";" + stTags + ";" + classification +  ";" + positiveScore + ";" + negativeScore +  ";" + separator);

    }

    private String extractList(List<PyObject> list) {
        String text = "";
        for (Integer i=0; i < list.size(); i++) {
            text += " " + list.get(i);
        }
        return text;
    }

    private void saveAudioData (Context context, String audioData) {

        //Sending AUDIO data to Neurobehaviour module

        String userName = neuroListener.getUserName();
        //Writing audio info in Text log file:
        Boolean createdFile = neuroListener.GetCsvTextReady();
        if (!createdFile) {
            neuroListener.createCsv("Text", context, userName);
            Log.v("text", "Creating CSV Text File");
        } else {
            Log.v("text", "CsvTextReady = true");
        }

        String separator = System.getProperty("line.separator");
        neuroListener.writeTextData( separator + timestamp + ";" + time + ";"+ "Sending AUDIO message" + ";" + audioData + separator + separator);
    }

}
