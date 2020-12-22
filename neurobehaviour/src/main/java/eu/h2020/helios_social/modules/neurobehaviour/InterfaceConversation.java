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
public interface InterfaceConversation {

    @Query("Select * from conversation_table")
    List<ModelConversation> getAllConversations();

    @Query("Select * from conversation_table where alter_user=:user")
    List<ModelConversation> getAllConversationsWithAlterUser(String user);

    @Query("Select * from conversation_table where start_time=:time LIMIT 1")
    ModelConversation getConversationByStartTime(double time);

    @Query("Select start_time from conversation_table where alter_user=:alter ORDER BY start_time DESC LIMIT 1")
    double getAlterUserConversationTime(String alter);

    @Query("Select start_time from conversation_table where alter_user=:alter ORDER BY start_time ASC LIMIT 1")
    double getFirstConversation(String alter);

    @Query("Select start_time from conversation_table where alter_user=:alter ORDER BY start_time DESC LIMIT 1")
    double getLastConversation(String alter);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertConversation(ModelConversation conversation);

    @Update
    void updateConversation(ModelConversation conversation);

    @Query("delete from conversation_table")
    void deleteAll();

}
