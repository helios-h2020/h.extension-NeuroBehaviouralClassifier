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
```

<p> <br><b>onCreate method</b></p>

```java
neuroListener = new NeurobehaviourListener();
sentimentalAnalysis = new SentimentalAnalysis();
context = getApplicationContext();

//start acceleration measurement
neuroListener.startAccel("start_session", context);

//Setting storage vars
neuroListener.SetCsvReady(false);
neuroListener.SetCsvImageReady(false);
```

<h3>Start sentimental analysis</h3>

<p> <br><b>From ShowMessage function in MainActivity class:</b></p>

```java
//Extracting user name from message
        String senderName = "";
        try {
            JSONObject json = new JSONObject(message.getMessage());
            senderName = json.getString("senderName");
            Log.v("cv", "Sender name: " + senderName);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//Sending message to SentimentalAnalysis class to analize
    sentimentalAnalysis.runThread(this.getApplicationContext(), message.getMediaFileName(), listener, topic, message, senderName);
//Sending message to Neurobehaviour module
    neuroListener.sendingMsg(message, this.getApplicationContext());
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
