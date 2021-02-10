package eu.h2020.helios_social.modules.neurobehaviour;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.*;

//UPV - Unit tests for NeurobehaviourListener class calling functions
//see Neurobehaviour module callings in:
// https://scm.atosresearch.eu/ari/helios_group/neurobehaviouralclassifier/blob/master/doc/contents-analysis.md
//
//for Ego-Alter Trust analysis in:
//https://scm.atosresearch.eu/ari/helios_group/neurobehaviouralclassifier/blob/master/doc/ego-alter-analysis.md

@RunWith(AndroidJUnit4.class)
public class NeurobehaviourListenerTest {

    private NeurobehaviourListener listener = new NeurobehaviourListener();
    private Context applicationContext;

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("eu.h2020.helios_social.modules.neurobehaviour.test", appContext.getPackageName());
        applicationContext = appContext;
    }

    @Test
    public void writingMsg() {
        //Testing writingMsg calling
        listener.writingMsg("exampleUUID", applicationContext);
    }

    @Test
    public void inboxMsg() {
        //Testing inboxMsg calling
        //listener.inboxMsg("messageUUID", applicationContext);
    }

    @Test
    public void sendingMsg() {
        //Testing sendingMsg calling
        //listener.sendingMsg("messageUUID", "Audio", "AlterUserName", "hello.wav");
    }

    @Test
    public void egoAlterTrust() {
        //Testing egoAlterTrust calling and result value
        String[][] result = listener.egoAlterTrust("AlterUserName");
        Assert.assertTrue(result.length > 0);
    }

    @Test
    public void createCsv() {
        File createdFile = null;

        //Testing createCsv function of listener
        //With any type of file:
        listener.createCsv("any", applicationContext, "dummyName");

    }

    @Test
    public void getCsvReady() {
        boolean ready = true;
        ready = listener.GetCsvReady();
        //Result should be false:
        Assert.assertEquals(false, ready);
    }
}