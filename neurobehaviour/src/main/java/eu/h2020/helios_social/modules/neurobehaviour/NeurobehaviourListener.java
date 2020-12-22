package eu.h2020.helios_social.modules.neurobehaviour;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.res.TypedArrayUtils;
import androidx.room.Room;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//Message module format
import eu.h2020.helios_social.core.messaging.HeliosMessage;


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

    //Instance to get the current Helios context
    private eu.h2020.helios_social.core.context.Context heliosContext = new eu.h2020.helios_social.core.context.Context(1, "atWork", true);

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
        Log.v("log", "MESSAGE RECEIVED IN NEUROBEHAVIOUR MODULE FROM OTHER USER: " + message.getMessage());
        Log.v("log", "File: " + message.getMediaFileName());
        //Writing data in text file
        String stringMsg = message.getMessage();
        Log.v("log", "Mensaje recibido: " + stringMsg);

        //Writing response messages from other users in Text log file
        boolean fileReady = GetCsvTextReady();
        if (fileReady) {
            receivedData(stringMsg);
        } else {
            Log.v("log", "El archivo CSV no está listo");
            createCsv("Text", context, getUserName());
            SetCsvTextReady(true);
            receivedData(stringMsg);
        }
    }

    private void receivedData(String stringMsg) {
        try {
            //LAB - Append data to file > append = true
            fileWriter  = new FileWriter(textFile, true);
            bfWriter = new BufferedWriter(fileWriter);

            bfWriter.write(separator + " Timestamp ; Time ; From ; Received message ; File ; Sent at " + separator);

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss Z");
            String time = formatter.format(date);

            //Timestamp
            // Current time in Milliseconds
            long actualTimeMillis = System.currentTimeMillis();
            long timestamp = actualTimeMillis / 1000;

            //Parse received message
            try {
                JSONObject json = new JSONObject(stringMsg);
                String user = json.getString("senderName");
                String msg = json.getString("msg");
                String hora = json.getString("ts");
                String media = "";
                if (msg.contains("Image"))
                        media = json.getString("mediaFileName");
                bfWriter.write(" " + timestamp + " ; " + time + " ; " + user + " ; " + msg + " ; " + media + " ; " + hora + " ; " + separator + separator);
                bfWriter.close();
                Log.v("log", "Writing text message data");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.v("log", "Error extrayendo el JSON: " + e.toString());
            }

        } catch (IOException e) {
            Log.v("log", "Error writing text data in file: " + e.toString());
        }
    }

    @Override
    public void sendingMsg(HeliosMessage message, Context context) {
        Log.v("cv", "MESSAGE SENT BY USER TO NEUROBEHAVIOUR MODULE: " + message.getMessage());
        Log.v("cv", "File: " + message.getMediaFileName());
    }

    //Calculating the Ego - Alter sentimental result for egoAlterTrust method
    //Arousal
    //Valence
    //Attention


    public String getArousal (String alterUser) {

        RepositoryConversation repositoryConversation = new RepositoryConversation(app);
        List<ModelConversation> conversationList = repositoryConversation.getAllConversationsWithUser(alterUser);

        RepositoryMessage repositoryMessage = new RepositoryMessage(app);

        int totalConversations = 0;
        int highArousalConversations = 0;
        int imageInConversations = 0;
        int facesInImages = 0;
        int happyFaces = 0;

        for (int i=0; i < conversationList.size(); i++) {
            ModelConversation conversation = conversationList.get(i);
            double startTime = conversation.getStartTime();
            Log.v("db", "Conversation for Arousal: " + startTime);

            List<ModelMessage> list = repositoryMessage.getMessagesOfConversation(startTime);

            int totalMessages = list.size();

            //Conversations with more than 2.5 messages per minute
            double timeFirstMessage = repositoryMessage.getTimeOfFirstMessage(startTime);
            double timeLastMessage = repositoryMessage.getTimeOfLastMessage(startTime);

            float minutes = (float)(timeLastMessage - timeFirstMessage) / 60;
            Log.v("db", "Minutes of conversation: " + minutes);

            float messagesPerMinute = totalMessages / minutes;
            if (messagesPerMinute > 2.5f) highArousalConversations++;

            boolean image = false;
            boolean face = false;
            boolean happy = false;

            for (int j=0; j < list.size(); j++) {
                ModelMessage message = list.get(j);
                if (message.getType().equals("image")) {
                    image = true;
                    if (message.getNumOfFaces() > 0) {
                        face = true;
                        if (message.getHappyFaces() > 0) happy = true;
                    }
                }
            }
            totalConversations++;
            if (image) imageInConversations++;
            if (face) facesInImages++;
            if (happy) happyFaces++;
        }

        float a = 0.6f;
        float b = 0.075f;
        float c = 0.125f;
        float d = 0.2f;

        float percentageHighArousalConversations = highArousalConversations / totalConversations;
        float percentageImageInConversations = imageInConversations / totalConversations;
        float percentageFacesInImages = facesInImages / totalConversations;
        float percentageWithHappyFaces = happyFaces / totalConversations;

        float arousal = (a*percentageHighArousalConversations) + (b*percentageImageInConversations) + (c*percentageFacesInImages) + (d*percentageWithHappyFaces);

        if (arousal >= 0.5f) {
            return "high";
        } else {
            return "low";
        }
    }



    public String getValence ( String alterUser ) {

        RepositoryConversation repositoryConversation = new RepositoryConversation(app);
        List<ModelConversation> conversationList = repositoryConversation.getAllConversationsWithUser(alterUser);

        RepositoryMessage repositoryMessage = new RepositoryMessage(app);

        int totalConversations = 0;
        int longMessageConversations = 0;
        int highValenceConversations = 0;

        for (int i=0; i < conversationList.size(); i++) {
            ModelConversation conversation = conversationList.get(i);
            double startTime = conversation.getStartTime();
            Log.v("db", "Conversation for Valence: " + startTime);

            List<ModelMessage> list = repositoryMessage.getMessagesOfConversation(startTime);

            int totalWordsOfConversation = 0;
            float messagesValence = 0;
            int totalMessages = 0;
            float valenceAverage = 0;

            for (int j=0; j < list.size(); j++) {
                ModelMessage message = list.get(j);
                if (message.getType().equals("text")) {
                    totalWordsOfConversation += list.get(j).getTotalWords();
                    messagesValence += list.get(j).getTextArousal();
                    totalMessages++;
                }
            }
            totalConversations++;
            if (totalWordsOfConversation > 27) longMessageConversations++;
            valenceAverage = messagesValence / totalMessages;
            if (valenceAverage > 0.5f) highValenceConversations++;
        }

        Log.v("db", "Valence - Long conversations higher than 27 words: " + longMessageConversations + " over " + totalConversations + " conversations.");
        Log.v("db", "Valence - Conversations with high valence: " + highValenceConversations + " over " + totalConversations + " conversations.");

        float a = 0.3f;
        float b = 0.7f;

        float percentageLongConversations = longMessageConversations / totalConversations;
        float percentageHighValenceConversations = highValenceConversations / totalConversations;

        float valence = (a*percentageLongConversations) + (b*percentageHighValenceConversations);

        if (valence >= 0.5f) {
            return "high";
        } else {
            return "low";
        }
    }



    public String getAttention (String alterUser, String context) {

        RepositoryConversation repositoryConversation = new RepositoryConversation(app);
        List<ModelConversation> conversationList = repositoryConversation.getAllConversationsWithUser(alterUser);

        double firstTime = repositoryConversation.getFirstConversation(alterUser);
        double lastTime = repositoryConversation.getLastConversation(alterUser);

        //timestamp is defined in seconds
        float days = (float)(lastTime - firstTime)/(60*60*24);
        int totalDays = (int)days + 1;
        Log.v("db", "Total days: " + totalDays);

        int lastDay = 0;
        int currentDay = 0;
        int daysWithConversation = 0;
        Log.v("db", "Conversation list size: " + conversationList.size());
        for (int i=0; i<conversationList.size(); i++) {
            ModelConversation conversation =  conversationList.get(i);
            currentDay = getDay(conversation.getStartTime());
            Log.v("db", "Current day: " + currentDay);
            if (currentDay != lastDay) daysWithConversation++;
            lastDay = currentDay;
        }

        Log.v("db", "Days with conversation: " + daysWithConversation);

        float percentageOfDays = daysWithConversation / totalDays;

        if (percentageOfDays < 0.3333f) {
            return "low";
        } else if (percentageOfDays < 0.6666) {
            return "medium";
        } else return "high";

    }

    private int getDay (double time) {
        Date date = new Date((long)time);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return day;
    }

    private String[] getContexts(String alterUser) {
        String[] contexts = new String[] {"atWork"};

        return contexts;
    }

    private String[][] insertRow(String[][] m, int r, String[] data) {
        String[][] out = new String[m.length + 1][];
        for (int i = 0; i < r; i++) {
            out[i] = m[i];
        }
        out[r] = data;
        for (int i = r + 1; i < out.length; i++) {
            out[i] = m[i - 1];
        }
        return out;
    }

    @Override
    public String[][] egoAlterTrust(String alterUser) {

        Log.v("db", "EGO-ALTER TRUST - Alter: " + alterUser);

        String[][] sentimentalAnalysisMatrix = {
                {"Context", "Valence", "Arousal", "Attention"},
                            };

        String[] contexts = getContexts(alterUser);

        for (int i=0; i<contexts.length; i++) {

            Log.v("db", "Context: " + contexts[i]);

            String valence = getValence(alterUser);
            Log.v("db", "Valence: " + valence);

            String arousal = getArousal(alterUser);
            Log.v("db", "Arousal: " + arousal);

            String attention = getAttention(alterUser, contexts[i]);
            Log.v("db", "Attention: " + attention);

            sentimentalAnalysisMatrix = insertRow(sentimentalAnalysisMatrix, 1, new String[]{ contexts[i], valence, arousal, attention });
            Log.v("db", "Matrix row: " + contexts[i] + " - " + valence + " - " + arousal + " - " + attention);

        }

        //Delete messages and conversations data from tables after sending results
        RepositoryConversation repositoryConversation = new RepositoryConversation(app);
        RepositoryMessage repositoryMessage = new RepositoryMessage(app);
        //repositoryConversation.deleteAll();
        //repositoryMessage.deleteAll();

        return sentimentalAnalysisMatrix;
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
                        Log.v("log", "Error creating csv file: " + e.toString());
                    }
                    Log.v("log", "Creating Acceleration.csv file in " + path);

                    String header = "Start time: ;" + time + separator;
                    header += "User: ;" + userName + separator;
                    header += " ;" + separator;
                    header += " OFFSET ; TIMESTAMP ; TIME ; ACCELERATION ; ACCEL AVERAGE " + separator;
                    header += " ms offset in Sensor of device; seconds Unix time; App time;m/s2;m/s2" + separator;
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
                        Log.v("log", "Error writing Header: " + e.toString());
                    }

                    break;

                case "Image":
                    imageFile = new File(context.getExternalFilesDir(null), userName + "-" + time + "-ImageAnalysis.csv");
                    String imagePath = context.getExternalFilesDir(null).getAbsolutePath().toString();
                    try {
                        imageFile.createNewFile();
                    } catch (IOException e) {
                        Log.v("log", "Error creating csv file for Image Analysis: " + e.toString());
                    }
                    Log.v("log", "Creating ImageAnalysis.csv file in " + imagePath);

                    String imageHeader = "Sending images start: ;" + time + separator;
                    imageHeader += "User: ;" + userName + separator;
                    imageHeader += " ;" + separator;
                    imageHeader += " TIMESTAMP ; TIME ; SENDER ; FILE ; NUM OF FACES ; EMOTIONS ; SCORES " + separator;
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
                        Log.v("log", "Error writing images Header: " + e.toString());
                    }

                    break;

                case "Text":
                    textFile = new File(context.getExternalFilesDir(null), userName + "-" + time + "-TextAnalysis.csv");
                    String textPath = context.getExternalFilesDir(null).getAbsolutePath().toString();
                    try {
                        textFile.createNewFile();
                    } catch (IOException e) {
                        Log.v("log", "Error creating csv file for Text Analysis: " + e.toString());
                    }
                    Log.v("log", "Creating TextAnalysis.csv file in " + textPath);

                    String textHeader = "Sending text message start: ;" + time + separator;
                    textHeader += "User: ;" + userName + separator;
                    textHeader += " ;" + separator;
                    textHeader += " TIMESTAMP ; TIME ; SENDER ; MESSAGE ; TRANSLATED MSG ; TAGS ; CLASSIFICATION ; POSITIVE SCORE ; NEGATIVE SCORE " + separator;
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
                        Log.v("log", "Error writing text file Header: " + e.toString());
                    }

                    break;

                case "Audio":
                    break;
            }
        } else {
            // External storage is not usable
            Log.v("log", "ERROR: Unable to mount external memory.");
            Toast.makeText(context, "SDCard no disponible", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void writeData(String data) {

        if (csvReady) {
            Log.v("accel", "csvReady = TRUE");
            try {
                //LAB - Append data to file > append = true
                fileWriter  = new FileWriter(accelFile, true);
                bfWriter = new BufferedWriter(fileWriter);
                bfWriter.write(data);
                bfWriter.close();
                Log.v("accel", "Writing data");
            } catch (IOException e) {
                Log.v("accel", "Error writing data in file: " + e.toString());
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
                Log.v("log", "Writing image data");
            } catch (IOException e) {
                Log.v("log", "Error writing image data in file: " + e.toString());
            }
        }

    }

    public void writeTextData(String data) {

        Log.v("log", "Método log de Textos");
        boolean fileReady = GetCsvTextReady();
        if (fileReady) {
            try {
                //LAB - Append data to file > append = true
                fileWriter  = new FileWriter(textFile, true);
                bfWriter = new BufferedWriter(fileWriter);
                bfWriter.write(data);
                bfWriter.close();
                Log.v("log", "Writing text message data");
            } catch (IOException e) {
                Log.v("log", "Error writing text data in file: " + e.toString());
            }
        } else {
            Log.v("log", "El archivo CSV no está listo");
        }

    }

    public void writeAudioData(String data) {
        Log.v("audio", "Método log de Audio. Se guarda junto con los mensajes de texto.");
        boolean fileReady = GetCsvTextReady();
        if (fileReady) {
            try {
                //LAB - Append data to file > append = true
                fileWriter  = new FileWriter(textFile, true);
                bfWriter = new BufferedWriter(fileWriter);
                bfWriter.write(data);
                bfWriter.close();
                Log.v("audio", "Writing text message data");
            } catch (IOException e) {
                Log.v("audio", "Error writing AUDIO data in file: " + e.toString());
            }
        } else {
            Log.v("audio", "El archivo CSV no está listo. Creando archivo log de texto para el audio.");
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


    //Data manager for database with results
    NeurobehaviourDatabase db;
    RepositoryMessage messageRepository;
    Application app = new Application();

    //Timestamp to store start conversation time and the time of the last message
    private static double startConversationTime = 0;

    public void DatabaseInstance(Context context, Application application) {
        app = application;
        db = NeurobehaviourDatabase.getDatabase(context);
        Log.v("db", "Database instance");
    }

    public void InsertMessage(double time, boolean sent, String type, String msgContent, int totalWords, float textArousal, int faces, int happyFaces, String alterUser) {

        Log.v("db", "InsertMessage listener method");
        ModelMessage message = new ModelMessage(time, sent, type, msgContent);

        switch (type) {
            case "text":
                message.setTotalWords(totalWords);
                message.setTextArousal(textArousal);
                break;
            case "image":
                message.setNumOfFaces(faces);
                message.setHappyFaces(happyFaces);
        }

        //Insert in DB
        messageRepository = new RepositoryMessage(app);
        Log.v("db", "Creating repository...");

        messageRepository.insert(message);
        Log.v("db", "Database insertion OK!");

        //Is this message contained in current conversation?
        RepositoryConversation repositoryConversation = new RepositoryConversation(app);
        double conversationTime = repositoryConversation.getAlterUserConversation(alterUser);
        Log.v("db", "Conversation time: " + conversationTime);
        double lastMessage;
        if (conversationTime != 0) {
            //Get timestamp of last message of conversation
            lastMessage = messageRepository.getTimeOfLastMessage(conversationTime);
            Log.v("db", "Last message time: " + lastMessage);
        } else {
            lastMessage = 0;
        }

        if ((lastMessage == 0) || ((time - lastMessage) > 300)) {
            //More than 5 min. between messages > New conversation
            Log.v("db", "Creating new conversation");
            //This is the first message of the conversation
            //Create a new conversation and add message
            startConversationTime = time;
            ModelConversation conversation = new ModelConversation(startConversationTime, alterUser, getCurrentContext());

            //Insert new conversation in DB
            repositoryConversation.insert(conversation);
            Log.v("db", "New conversation. Start time: " + time);
            Log.v("db", "Helios Context: " + getCurrentContext());

            //set conversation of message
            message.setConversationTime(time);
            //update message in database
            messageRepository.insert(message);

        } else {
            //Insert message in current conversation
            message.setConversationTime(conversationTime);
            messageRepository.insert(message);
            Log.v("db", "Message inserted in current conversation!");
        }
    }

    private String getCurrentContext() {
        return heliosContext.getName();
    }

}
