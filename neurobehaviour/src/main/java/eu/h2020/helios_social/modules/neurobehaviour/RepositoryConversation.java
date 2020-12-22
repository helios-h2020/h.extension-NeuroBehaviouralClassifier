package eu.h2020.helios_social.modules.neurobehaviour;

import android.app.Application;

import java.util.List;

public class RepositoryConversation {

    private InterfaceConversation conversationDao;

    RepositoryConversation(Application application) {
        NeurobehaviourDatabase db = NeurobehaviourDatabase.getDatabase(application);
        conversationDao = db.conversationDao();
    }

    void insert(final ModelConversation conversation) {
        NeurobehaviourDatabase.databaseWriterExecutor.execute(() -> {
            conversationDao.insertConversation(conversation);
        });
    }

    double getAlterUserConversation(final String alter) {
        double time = conversationDao.getAlterUserConversationTime(alter);
        return time;
    }

    List<ModelConversation> getAllConversationsWithUser(final String user) {
        List<ModelConversation> list = conversationDao.getAllConversationsWithAlterUser(user);
        return list;
    }

    double getFirstConversation(final String user) {
        double time = conversationDao.getFirstConversation(user);
        return time;
    }

    double getLastConversation(final String user) {
        double time = conversationDao.getLastConversation(user);
        return time;
    }

    void deleteAll() {
        NeurobehaviourDatabase.databaseWriterExecutor.execute(() -> {
            conversationDao.deleteAll();
        });
    }

}
