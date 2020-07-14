package eu.h2020.helios_social.modules.neurobehaviour;

import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

//Class to define result data with acceleration average
import eu.h2020.helios_social.modules.neurobehaviour.AccelerationAverage;

//Sensor calling in Context module
import eu.h2020.helios_social.core.sensor.Sensor;

public class DataHandler extends Handler {

    private final WeakReference<Acceleration> acceleration;

    private int count = 0;

    //Object to send acceleration value to Context Module, with msg Id
    AccelerationAverage obj = new AccelerationAverage();

    //Object to send average to Context module
    Sensor contextSensor = new Sensor() {
        @Override
        public void startUpdates() {

        }

        @Override
        public void stopUpdates() {

        }
    };


    public DataHandler(Acceleration activity) {
        acceleration = new WeakReference<Acceleration>(activity);
    }

    public void handlerMessage(Message msg) {
        Acceleration activity = acceleration.get();
        count++;
        //calculating acceleration average after 10 iterations
        if ((activity != null) && (count > 10)) {
            //Pass accel value to Acceleration class to calculate average
            activity.getAverage(msg.getData().getFloat("value"));
        }
    }

    public void sendAverage(Message msg) {
        float averageAccel = msg.getData().getFloat("value");
        String msgId = msg.getData().getString("id");
        Log.v("listen", "Msg UUID: " + msgId + " - Acceleration AVERAGE: " + averageAccel);

        //Sending Average value with msg Id to Sensor.registerValueListener (Context Module)
        obj.msgId = msgId;
        obj.accelAverage = averageAccel;
        contextSensor.receiveValue(obj);
    }

    @Override
    public void publish(LogRecord record) {

    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}
