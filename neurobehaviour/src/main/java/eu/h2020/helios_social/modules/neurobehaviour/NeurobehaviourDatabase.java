package eu.h2020.helios_social.modules.neurobehaviour;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ModelMessage.class, ModelConversation.class}, version = 1, exportSchema = false)
public abstract class NeurobehaviourDatabase extends RoomDatabase {

    public abstract InterfaceMessage messageDao();
    public abstract InterfaceConversation conversationDao();

    private static volatile NeurobehaviourDatabase INSTANCE; //Singleton
    private static final  int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriterExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    //Returns a singleton of database. It'll create the database the first time it's accessed.
    static NeurobehaviourDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (NeurobehaviourDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), NeurobehaviourDatabase.class, "neurobehaviour_database.db").build();
                    Log.v("db","Creating db Instance");
                }
            }
        }
        return INSTANCE;
    }

    //Populate the database
    //Override onCreate method to execute a lambda on a background thread
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };

}
