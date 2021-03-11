<h1>Calls to Neurobehaviour module</h1>

<h2>Contents and sensors analysis</h2>

<p>This interface implements the same function for each type of content (text, images and audio)</p>

<h3>Calling from TestClient MainActivity class</h3>

```java
//Using Neurobehaviour module listener class to send info about chat communications
import eu.h2020.helios_social.modules.neurobehaviour.NeurobehaviourListener;
//Using SentimentalAnalysis Class
import eu.h2020.helios_social.modules.neurobehaviour.SentimentalAnalysis;

//Listener init
private NeurobehaviourListener neuroListener;
//Sentimental analysis instance
private SentimentalAnalysis sentimentalAnalysis;
private android.content.Context context;
```

<p> <br><b>onCreate method</b></p>

```java
    //UPV - Setup listener for Neurobehaviour module
    neuroListener = new NeurobehaviourListener();
    context = getApplicationContext();

    //UPV - Start acceleration measurement
    neuroListener.startAccel("start_session", context);

    //UPV - Setting storage vars
    neuroListener.SetCsvReady(false);
    neuroListener.SetCsvImageReady(false);

    //UPV - Sentimental analysis instance
    sentimentalAnalysis = new SentimentalAnalysis();

    //UPV - Neurobehaviour database for analysis results
    neuroListener.DatabaseInstance(context, getApplication());
```

<h3>Start sentimental analysis</h3>

<p> <br><b>From ShowMessage function in MainActivity class:</b></p>

```java
        //UPV - Extracting user name from message
        String senderName = "";
        String msgType = "";
        try {
            JSONObject json = new JSONObject(message.getMessage());
            senderName = json.getString("senderName");
            msgType = json.getString("messageType");
            Log.v("text", "Sender name: " + senderName);
            Log.v("text", "Type of message: " + msgType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //UPV - Don't analyze "JOIN" messages
        if (msgType.equals("MESSAGE")) {
            //UPV - sending message to SentimentalAnalysis class to analize
            sentimentalAnalysis.runThread(this.getApplicationContext(), message.getMediaFileName(), listener, topic, message, senderName);
            //UPV - sending message to Neurobehaviour module
            neuroListener.sendingMsg(message, this.getApplicationContext());
        }
```

<h3>Stop accelerometer measurement</h3>

<p><b>onDestroy method</b></p>

```java
//Stop accelerometer
neuroListener.stopAccel();
```

<h3>Calling from MySettingsFragment class</h3>

If user's name is changed, files for analysis results are created and we start to write metrics:

```java
    //Neurobehaviour listener instance
    private NeurobehaviourListener listener = new NeurobehaviourListener();

    private void changeTextPreference(String key, String value) {
        EditTextPreference pref = (EditTextPreference) findPreference(key);
        pref.setSummary(value);
        HeliosUserData.getInstance().setValue(key, value);

        //LAB - When user name is changed, we start to write metrics
        if (pref.getKey().equals("username")) {
            //LAB - User name changed - new session
            Context context = this.getContext();
            //LAB - Start to write metrics - new file
            //Sending new user name to Neurobehaviour listener to start writing results
            listener.createCsv("Acceleration", context, value);
            listener.createCsv("Image", context, value);
            listener.createCsv("Text", context, value);
            listener.createCsv("Audio", context, value);
        }
    }
```
