package eu.h2020.helios_social.modules.neurobehaviour;

import android.app.Application;
import android.util.Log;

import java.util.List;

public class RepositoryMessage {

    private InterfaceMessage messageDao;

    RepositoryMessage(Application application) {
        NeurobehaviourDatabase db = NeurobehaviourDatabase.getDatabase(application);
        messageDao = db.messageDao();
    }

    void insert(final ModelMessage message) {
        NeurobehaviourDatabase.databaseWriterExecutor.execute(() -> {
            messageDao.insertMessage(message);
        });
    }

    double getTimeOfFirstMessage(final double conversationTime) {
        double time = messageDao.getTimeOfFirstMessage(conversationTime);
        Log.v("db", "Getting time of first message: " + time);
        return time;
    }

    double getTimeOfLastMessage(final double conversationTime) {
        double time = messageDao.getTimeOfLastMessage(conversationTime);
        Log.v("db", "Getting time of last message: " + time);
        return time;
    }

    List<ModelMessage> getMessagesOfConversation(final double conversationTime) {
        List<ModelMessage> list = messageDao.getMessagesofConversation(conversationTime);
        return list;
    }

    void deleteAll() {
        NeurobehaviourDatabase.databaseWriterExecutor.execute(() -> {
            messageDao.deleteAll();
        });
    }

}
