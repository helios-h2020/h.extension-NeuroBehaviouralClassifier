<h3>DataHandler class</h3>

<p>It implements a handler to share data between classes. This class extends from Handler class.</p>

<code>
	public class DataHandler extends Handler {
        private final WeakReference<Acceleration> acceleration;
        private SensorValueListener valueListener
        public DataHandler(Acceleration activity)
        public void handlerMessage(Message msg)
        public void sendAverage(Message msg)
        @Override
        public void publish(LogRecord record)
        @Override
        public void flush()
        @Override
        public void close() throws SecurityException
	}
</code>