package eu.h2020.helios_social.modules.neurobehaviour;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;

import eu.h2020.helios_social.core.messaging.HeliosMessage;

public interface NeurobehaviourInterface {

    //Accelerometer - Events to call when user open a message to write or read it
    void writingMsg (String alterUser, Context context);
    void readingChat (String alterUser, Context context);
    void chatClosed (String alterUser);

    //Sentimental analysis for media chat content
    void inboxMsg (HeliosMessage message, Context context);
    void sendingMsg (HeliosMessage message, Context context);

    //Sentimental analysis of Ego - Alter relationship
    //based on previous values and new communications analysis
    String[][] egoAlterTrust (String alterUser);

    //External data storage to save neurobehavioural metrics
    void createCsv(String file, Context context, String userName);
    void writeData(String data);
    }
