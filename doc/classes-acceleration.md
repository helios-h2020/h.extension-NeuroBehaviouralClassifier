<h3>Acceleration class</h3>

<p>Acceleration class calculates device linear acceleration in real time and acceleration average. It setup, starts and stop accelerometer sensor too when functions are called by listener.</p>

> public class Acceleration implements SensorEventListener {
>   public void accelerationInit(Context activityContext)
>   public void onSensorChanged(SensorEvent event)
>   private void accelValues(SensorEvent event)
>   private void writingValues(final long st, final String t , final float a)
>   public static float getAcceleration()
>   public static void getAverage(Float value)
>   public void accelerometerOff()
>   public String getDate(long time)
> }


