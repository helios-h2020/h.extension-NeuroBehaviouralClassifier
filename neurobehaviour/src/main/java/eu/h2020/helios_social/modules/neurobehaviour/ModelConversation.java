package eu.h2020.helios_social.modules.neurobehaviour;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "conversation_table")
public class ModelConversation {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo( name = "start_time")
    private double startTime;

    @ColumnInfo( name = "alter_user")
    private String alterUser;

    @ColumnInfo( name = "helios_context")
    private String heliosContext;


    //Constructor to use by controller
    public ModelConversation(@NonNull double startTime, String alterUser, String heliosContext) {
        this.startTime = startTime;
        this.alterUser = alterUser;
        this.heliosContext = heliosContext;
    }

    public int getId() {return id; }

    public void setId(int id) {this.id = id;}

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double time) {
        this.startTime = time;
    }

    public String getAlterUser() {
        return alterUser;
    }

    public void setAlterUser(String name) {
        this.alterUser = name;
    }

    public String getHeliosContext() {
        return heliosContext;
    }

    public void setHeliosContext(String context) {
        this.heliosContext = context;
    }

}
