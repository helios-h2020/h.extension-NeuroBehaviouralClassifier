package eu.h2020.helios_social.modules.neurobehaviour;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "message_table")
public class ModelMessage {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo( name = "timestamp")
    private double timestamp;

    @ColumnInfo( name = "sent")
    private boolean sent;

    @ColumnInfo( name = "type")
    private String type;

    @ColumnInfo( name = "msg_content")
    private String msgContent;

    @ColumnInfo( name = "total_words")
    private int totalWords;

    @ColumnInfo( name = "text_arousal")
    private float textArousal;

    @ColumnInfo( name = "num_of_faces")
    private int numOfFaces;

    @ColumnInfo( name = "happy_faces")
    private int happyFaces;

    //Foreing key - Conversation start time
    @ColumnInfo( name = "conversation_time")
    private double conversationTime;

    //Constructor to use by controller
    public ModelMessage(@NonNull double timestamp, boolean sent, String type, String msgContent) {
        this.timestamp = timestamp;
        this.sent = sent;
        this.type = type;
        this.msgContent = msgContent;
    }

    public int getId() { return id; }

    public void setId(int id) {this.id = id; }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double time) {
        this.timestamp = time;
    }

    public boolean getSent() {
        return sent;
    }

    public void setSent(boolean bool) {
        this.sent = bool;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String content) {
        this.msgContent = content;
    }

    public int getTotalWords() {
        return totalWords;
    }

    public void setTotalWords(int num) {
        this.totalWords = num;
    }

    public float getTextArousal() {
        return textArousal;
    }

    public void setTextArousal(float arousal) {
        this.textArousal = arousal;
    }

    public int getNumOfFaces() {
        return numOfFaces;
    }

    public void setNumOfFaces(int num) {
        this.numOfFaces = num;
    }

    public int getHappyFaces() {
        return happyFaces;
    }

    public void setHappyFaces(int num) {
        this.happyFaces = num;
    }

    public double getConversationTime() {
        return conversationTime;
    }

    public void setConversationTime(double time) {
        this.conversationTime = time;
    }
}
