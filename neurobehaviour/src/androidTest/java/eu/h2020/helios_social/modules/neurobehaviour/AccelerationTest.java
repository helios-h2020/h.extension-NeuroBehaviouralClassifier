package eu.h2020.helios_social.modules.neurobehaviour;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AccelerationTest {

    private Acceleration accel = new Acceleration();
    private Context applicationContext;

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("eu.h2020.helios_social.modules.neurobehaviour.test", appContext.getPackageName());
        applicationContext = appContext;
    }

    @Test
    public void getAcceleration() {
        //Testing getAcceleration function
        float testAcceleration = 3.0f;
        testAcceleration = accel.getAcceleration();
        // testAcceleration value should be 0 (initial value):
        Assert.assertTrue(testAcceleration == 0f);
    }

    @Test
    public void getDate() {
        //Testing getDate function
        long actualTimeMillis = System.currentTimeMillis();
        String time = accel.getDate(actualTimeMillis);
        //time should contains an string value:
        Assert.assertFalse(time == "");
    }
}