<h1>Calls to Neurobehaviour module</h1>

<h2>Contents and sensors analysis</h2>

<p>This interface implements the same function for each type of content (text, images and audio)</p>

<h3>Calling from TestClient MainActivity class</h3>
<code>
    //Using Neurobehaviour module listener class to send info about chat communications
    import eu.h2020.helios_social.modules.neurobehaviour.NeurobehaviourListener;
    //Listener init
    private NeurobehaviourListener neuroListener;
</code>

<p> <br><b>onCreate method</b></p>

<code>
    neuroListener = new NeurobehaviourListener();
</code>


<h3>Start sentimental analysis</h3>

<p> <br><b>From ShowMessage function in MainActivity class:</b></p>
<code>
    //Sending message to Neurobehaviour module to analyze
    neuroListener.sendingMsg(message, this.getApplicationContext());
</code>

<h3>Start sensors measurement</h3>

<p> <br><b>User starts to read a message</b></p>

<code>neuroListener.readingChat (String alterUser, Context context);</code>

<p><b>User starts to write a message</b></p>

<code>neuroListener.writingMsg(String alterUser, Context context);</code>

<h3>Stop accelerometer measurement</h3>

<p><b>User sends the message</b></p>

<code>neuroListener.sendingMsg((message, this.getApplicationContext());</code>

<p> <br><b>User closes the chat</b></p>

<code>neuroListener.chatClosed(String alterUser);</code>