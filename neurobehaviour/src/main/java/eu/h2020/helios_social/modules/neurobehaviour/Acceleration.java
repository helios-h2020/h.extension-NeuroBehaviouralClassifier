package eu.h2020.helios_social.modules.neurobehaviour;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Debug;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Time;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.SENSOR_SERVICE;

//This class implements a listener for sensor events
public class Acceleration implements SensorEventListener {

    //Linear acceleration sensor provides a tridimensional array to represent acceleration
    //by each device axis (without gravity)
    //linear acceleration = acceleration - acceleration due to gravity


    private Sensor accelerometer;

    private static float[] gravity = new float[3];
    private static float[] acelByAxis = new float[3];
    private static float acceleration;

    private static float x;
    private static float y;
    private static float z;

    private static long lastUpdate = 0;

    private static Float average;
    private static int samples;

    private DataHandler resultHandler;

    //Default linear acceleration sensor instance
    private SensorManager sensorManager;

    //message Id from listener
    private static String id;

    //LAB - Var to save Acceleration file
    private File accelFile;
    private FileOutputStream fOut;
    private OutputStreamWriter streamWriter;

    //LAB - Listener init
    private NeurobehaviourListener listen;
    private android.content.Context context;
    private String separator = System.getProperty("line.separator");

    //LAB - Vars to manage csv list
    private int rows = 0;
    private String csvRows = "";

    //LAB - Time variables
    private long sensorTimeMillis = 0;
    private String time = "";

    public void accelerationInit(Context activityContext){

        sensorManager = (SensorManager) activityContext.getSystemService(SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelerometer != null) Log.v("accel", "Accelerometer ready");
        else Log.v("accel", "There is not accelerometer sensor");

        acceleration = 0.00f;

        gravity[0] = 0;
        gravity[1] = 0;
        gravity[2] = 0;

        x = 0;
        y = 0;
        z = 0;

        average = 0f;
        samples = 0;

        //Accelerometer listener
        //sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, accelerometer, sensorManager.SENSOR_DELAY_FASTEST);

        //Handler instance
        resultHandler = new DataHandler(this);

        //LAB - Setup listener to write data to file
        listen = new NeurobehaviourListener();
        context = activityContext;

        //LAB - create results file for acceleration metrics
        //only when user writes userName
        //accelFile = listen.createCsv("Acceleration", context);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelValues(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //VIB - It's called with a SensorEvent > onSensorChanged
    private void accelValues(SensorEvent event) {

        final SensorEvent ev = event;
        //Creates a new Thread and uses a Handler to return result to Acceleration class

        new Thread(new Runnable() {
            @Override
            public void run() {
                //Log.v("thread", "Thread id: " + Thread.currentThread().getId());

                //Acceleration values
                //Alpha is calculated as t/(t+dT)
                //with t, the low-pass filter's time-constant
                //and dT, the event delivery rate

                final float alpha = 0.25f;

                gravity[0] = alpha * gravity[0] + (1 - alpha) * ev.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * ev.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * ev.values[2];

                //Acceleration by axis without gravity:
                acelByAxis[0] = ev.values[0] - gravity[0];
                acelByAxis[1] = ev.values[1] - gravity[1];
                acelByAxis[2] = ev.values[2] - gravity[2];

                x = acelByAxis[0];
                y = acelByAxis[1];
                z = acelByAxis[2];

                //total acceleration from 3 axes
                acceleration = (float)Math.sqrt(x*x + y*y + z*z);

                //VIB - Event timestamp in nanoseconds
                //the awake time in nanoseconds SINCE the last boot
                long sensorTime = ev.timestamp;
                sensorTimeMillis = sensorTime / 1000000;

                // Current time in Milliseconds
                long actualTimeMillis = System.currentTimeMillis();

                // Time with format hh:mm:ss:mmm
                time = getDate(actualTimeMillis);

                //Filtering to 30Hz sampling rate (one sample each 33 ms)
                //VIB - Android Studio device emulator shows a result each 66 ms -> frecuency = 15Hz -> From sensor Timestamp
                if (sensorTimeMillis - lastUpdate > 33) {

                    Log.v("thread", sensorTimeMillis + " --- Acceleration: " + Float.toString(acceleration) + " m/s2");
                    lastUpdate = sensorTimeMillis;

                    //write a row in file with this time values
                    writingValues(sensorTimeMillis, time, acceleration);
                }

            }
        }).start();

    }

    private void writingValues(final long st, final String t , final float a) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                //Message to return acceleration value with a handler
                Bundle msgBundle = new Bundle();
                msgBundle.putFloat("value", a);
                Message msg = new Message();
                msg.setData(msgBundle);
                //Sending acceleration value to calculate accel average
                resultHandler.handlerMessage(msg);

                //LAB - Save data to file if CSV file is ready
                if (listen.GetCsvReady()) {
                    rows = rows + 1;
                    csvRows = csvRows + st + ";" + t + ";" + Float.toString(a) + ";" + average + separator;
                    //we save csv rows to file each 100 rows
                    if (rows >= 100) {
                        Log.v("accel", "Rows counter OK. writeData calling...");
                        listen.writeData(csvRows);
                        rows = 0;
                        csvRows = "";
                    }

                }

            }}).start();
    }

    public static float[] getAccelAxisValues() {

        return acelByAxis;
    }

    public static float getAcceleration() {

        return acceleration;
    }

    public static void getAverage(Float value) {

        samples++;
        average = (((average * (samples-1)) + value) / samples);
        Log.v("thread", "Acceleration average: " + average + " --- Current Accel value: " + value);

        //LAB - Updating current acceleration value
        acceleration = value;
    }

    public void accelerometerOff() {

        //send average acceleration to Sensor.registerValueListener through DataHandler
        //Message to return average value with a handler
        Bundle msgBundle = new Bundle();
        msgBundle.putFloat("value", average);
        msgBundle.putString("id", id);
        Message msg = new Message();
        msg.setData(msgBundle);
        //Sending acceleration average to Context module
        resultHandler.sendAverage(msg);
        Log.v("accel", "Sending acceleration average to Context Module");

        //write last rows in csvRows
        Log.v("storage", "Writing last data rows.");
        listen.writeData(csvRows);

        //write acceleration average in csv file
        Log.v("storage", "Writing acceleration average. Value: " + Float.toString(average));
        listen.writeData("Final acceleration average: " + ";" + Float.toString(average) + separator);

        listen.SetCsvReady(false);
        sensorManager.unregisterListener(this);
    }

    public String getDate(long time) {
        long mil = time % 1000;
        long s = (time / 1000) % 60;
        long m = (time / (1000 * 60)) % 60;
        long h = (time / (1000 * 60 * 60)) % 24;
        //UTC + 2
        h = h + 2;
        return String.format("%d:%02d:%02d:%03d", h,m,s,mil);
    }

}
