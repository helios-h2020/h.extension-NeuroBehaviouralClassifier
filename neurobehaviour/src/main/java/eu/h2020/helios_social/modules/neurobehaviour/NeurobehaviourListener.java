package eu.h2020.helios_social.modules.neurobehaviour;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

//Message module format
import eu.h2020.helios_social.core.messaging.HeliosMessage;

//Acceleration Class
import eu.h2020.helios_social.modules.neurobehaviour.Acceleration;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.os.ParcelFileDescriptor.MODE_APPEND;


public class NeurobehaviourListener implements NeurobehaviourInterface {

    private Acceleration dataAccel;

    //Bool to set csv file ready
    private static boolean csvReady = false;
    private static boolean csvImageReady = false;
    private static boolean csvTextReady = false;

    //LAB - Storage system
    //Static var > same file for all process
    private static File accelFile;
    private static File imageFile;
    private static File textFile;
    private FileWriter fileWriter;
    private BufferedWriter bfWriter;
    private String separator = System.getProperty("line.separator");

    //LAB - User name
    private static String appUserName = "";

    @Override
    public void writingMsg(String alterUser, Context context) {
        Log.v("listen", "WRITING - Message UUID: " + " - Alter: " + alterUser);
    }

    @Override
    public void readingChat (String alterUser, Context context) {
        Log.v("listen", "Reading Chat - Accelerometer start");
        //Acceleration java class instance
        dataAccel = new Acceleration();
        //Init Acceleration java class
        dataAccel.accelerationInit(context);
    }

    //LAB - Start accelerometer when session begins
    public void startAccel(String uuid, Context context) {
        Log.v("accel", "START SESSION - ACCELEROMETER ON");

        //UPV - Start accelerometer
        //using Notification Service for Android 9+
        Log.v("accel", "Setting notification channel");
        String CHANNEL_ID = "1";
        CharSequence name = "Accel";
        String description = "Channel for acceleration sensor";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        // add the NotificationChannel
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        //Acceleration java class instance
        dataAccel = new Acceleration();


        //Init Acceleration java class
        dataAccel.accelerationInit(context);

    }

    @Override
    public void chatClosed (String alterUser) {
        Log.v("Accel", "Reading chat stopped - Alter user: " + alterUser);
        //Desactivar el listener
        dataAccel.accelerometerOff();
    }

    public void stopAccel( ) {
        Log.v("Accel", "END OF SESSION - STOPPING ACCEL");
        dataAccel.accelerometerOff();
    }

    @Override
    public void inboxMsg(HeliosMessage message, Context context) {
        Log.v("cv", "MESSAGE RECEIVED IN NEUROBEHAVIOUR MODULE: " + message.getMessage());
        Log.v("cv", "File: " + message.getMediaFileName());
    }

    @Override
    public void sendingMsg(HeliosMessage message, Context context) {
        Log.v("cv", "MESSAGE SENT BY USER TO NEUROBEHAVIOUR MODULE: " + message.getMessage());
        Log.v("cv", "File: " + message.getMediaFileName());
    }

    @Override
    public int[] egoAlterTrust(String alterUser) {
        Log.v("listen", "EGO-ALTER TRUST - Alter: " + alterUser);
        int[] dummyArray = {4, 5, 3, 6};
        return dummyArray;
    }

    @Override
    public void createCsv(String fileType, Context context, String userName) {

        //LAB - Public user name
        setUserName(userName);

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss Z");
        String time = formatter.format(date);

        if(Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED)) {
            // External storage is usable
            switch (fileType) {
                case "Acceleration":

                    accelFile = new File(context.getExternalFilesDir(null), userName + "-" + time + "-Accelerometer.csv");
                    String path = context.getExternalFilesDir(null).getAbsolutePath().toString();
                    try {
                        accelFile.createNewFile();
                    } catch (IOException e) {
                        Log.v("storage", "Error creating csv file: " + e.toString());
                    }
                    Log.v("Storage", "Creating Acceleration.csv file in " + path);

                    String header = "Start time: ;" + time + separator;
                    header += "User: ;" + userName + separator;
                    header += " ;" + separator;
                    header += "TIMESTAMP;TIME;ACCELERATION;ACCEL AVERAGE" + separator;
                    header += "ms offset in Sensor of device; App time;m/s2;m/s2" + separator;
                    header += " ;" + separator;

                    //LAB - Open stream to file
                    //Append flag = true
                    try {
                        fileWriter  = new FileWriter(accelFile);
                        bfWriter = new BufferedWriter(fileWriter);
                        //LAB - writing table header
                        bfWriter.write(header);
                        //LAB - we must to close file to be sure that header is written in this first step
                        bfWriter.close();

                        //LAB - Start to write data
                        SetCsvReady(true);
                    } catch (IOException e) {
                        Log.v("storage", "Error writing Header: " + e.toString());
                    }

                    break;

                case "Image":
                    imageFile = new File(context.getExternalFilesDir(null), userName + "-" + time + "-ImageAnalysis.csv");
                    String imagePath = context.getExternalFilesDir(null).getAbsolutePath().toString();
                    try {
                        imageFile.createNewFile();
                    } catch (IOException e) {
                        Log.v("storage", "Error creating csv file for Image Analysis: " + e.toString());
                    }
                    Log.v("Storage", "Creating ImageAnalysis.csv file in " + imagePath);

                    String imageHeader = "Sending images start: ;" + time + separator;
                    imageHeader += "User: ;" + userName + separator;
                    imageHeader += " ;" + separator;
                    imageHeader += "TIME;FILE;NUM OF FACES;EMOTION / SCORE" + separator;
                    imageHeader += " ;" + separator;

                    //LAB - Open stream to file
                    //Append flag = true
                    try {
                        fileWriter  = new FileWriter(imageFile);
                        bfWriter = new BufferedWriter(fileWriter);
                        //LAB - writing table header
                        bfWriter.write(imageHeader);
                        //LAB - we must to close file to be sure that header is written in this first step
                        bfWriter.close();

                        //LAB - Start to write data
                        SetCsvImageReady(true);
                    } catch (IOException e) {
                        Log.v("storage", "Error writing images Header: " + e.toString());
                    }

                    break;

                case "Text":
                    textFile = new File(context.getExternalFilesDir(null), userName + "-" + time + "-TextAnalysis.csv");
                    String textPath = context.getExternalFilesDir(null).getAbsolutePath().toString();
                    try {
                        textFile.createNewFile();
                    } catch (IOException e) {
                        Log.v("storage", "Error creating csv file for Text Analysis: " + e.toString());
                    }
                    Log.v("Storage", "Creating TextAnalysis.csv file in " + textPath);

                    String textHeader = "Sending text message start: ;" + time + separator;
                    textHeader += "User: ;" + userName + separator;
                    textHeader += " ;" + separator;
                    textHeader += "TIME;MESSAGE;TRANSLATED MSG; TAGS; EMOTION / SCORE" + separator;
                    textHeader += " ;" + separator;

                    //LAB - Open stream to file
                    //Append flag = true
                    try {
                        fileWriter  = new FileWriter(textFile);
                        bfWriter = new BufferedWriter(fileWriter);
                        //LAB - writing table header
                        bfWriter.write(textHeader);
                        //LAB - we must to close file to be sure that header is written in this first step
                        bfWriter.close();

                        //LAB - Start to write data
                        SetCsvTextReady(true);
                    } catch (IOException e) {
                        Log.v("storage", "Error writing text file Header: " + e.toString());
                    }

                    break;
            }
        } else {
            // External storage is not usable
            Log.v("Storage", "ERROR: Unable to mount external memory.");
            Toast.makeText(context, "SDCard no disponible", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void writeData(String data) {

        if (csvReady) {
            Log.v("storage", "csvReady = TRUE");
            try {
                //LAB - Append data to file > append = true
                fileWriter  = new FileWriter(accelFile, true);
                bfWriter = new BufferedWriter(fileWriter);
                bfWriter.write(data);
                bfWriter.close();
                Log.v("storage", "Writing data");
            } catch (IOException e) {
                Log.v("storage", "Error writing data in file: " + e.toString());
            }
        }
    }

    public void writeImageData(String data) {

        if (csvImageReady) {
            try {
                //LAB - Append data to file > append = true
                fileWriter  = new FileWriter(imageFile, true);
                bfWriter = new BufferedWriter(fileWriter);
                bfWriter.write(data);
                bfWriter.close();
                Log.v("storage", "Writing image data");
            } catch (IOException e) {
                Log.v("storage", "Error writing image data in file: " + e.toString());
            }
        }

    }

    public void writeTextData(String data) {

        if (csvTextReady) {
            try {
                //LAB - Append data to file > append = true
                fileWriter  = new FileWriter(textFile, true);
                bfWriter = new BufferedWriter(fileWriter);
                bfWriter.write(data);
                bfWriter.close();
                Log.v("storage", "Writing text message data");
            } catch (IOException e) {
                Log.v("storage", "Error writing text data in file: " + e.toString());
            }
        }

    }


    public Boolean GetCsvReady() {
        return csvReady;
    }

    public void SetCsvReady(Boolean value) {
        csvReady = value;
    }

    public void setUserName (String name) {
        appUserName = name;
    }

    public String getUserName() {
        return appUserName;
    }

    public Boolean GetCsvImageReady() {
        return csvImageReady;
    }

    public Boolean GetCsvTextReady() {
        return csvTextReady;
    }

    public void SetCsvImageReady(Boolean value) {
        csvImageReady = value;
    }

    public void SetCsvTextReady(Boolean value) {
        csvTextReady = value;
    }

}
