package eu.h2020.helios_social.modules.neurobehaviour;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface InterfaceMessage {

    @Query("Select * from message_table")
    List<ModelMessage> getAllMessages();

    @Query("Select * from message_table where timestamp IN (:time) LIMIT 1")
    ModelMessage getMessageByTimestamp(double time);

    //Query to get the time of first message of a conversation
    @Query("Select timestamp from message_table where conversation_time = :conversation_time ORDER BY timestamp ASC LIMIT 1")
    double getTimeOfFirstMessage(double conversation_time);

    //Query to get the time of last message of a conversation
    @Query("Select timestamp from message_table where conversation_time = :conversation_time ORDER BY timestamp DESC LIMIT 1")
    double getTimeOfLastMessage(double conversation_time);

    //Get messages of a conversation
    @Query("Select * from message_table where conversation_time = :conversation_time ORDER BY timestamp ASC")
    List<ModelMessage> getMessagesofConversation(double conversation_time);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMessage(ModelMessage message);

    @Update
    void updateMessage(ModelMessage message);

    @Query("delete from message_table")
    void deleteAll();

}
