<h1>Calls to Neurobehaviour module</h1>

<h2>Contents and sensors analysis</h2>

<p>This interface implements the same function for each type of content (text, images and audio)</p>

<h3>Calling from TestClient MainActivity class</h3>

> //Using Neurobehaviour module listener class to send info about chat communications
> import eu.h2020.helios_social.modules.neurobehaviour.NeurobehaviourListener;
> //Listener init
> private NeurobehaviourListener neuroListener;

<p> <br><b>onCreate method</b></p>

> neuroListener = new NeurobehaviourListener();


<h3>Start sentimental analysis</h3>

<p> <br><b>From ShowMessage function in MainActivity class:</b></p>

> //Sending message to Neurobehaviour module to analyze
> neuroListener.sendingMsg(message, this.getApplicationContext());

<h3>Start sensors measurement</h3>

<p> <br><b>User starts to read a message</b></p>

> neuroListener.readingChat (String alterUser, Context context);

<p><b>User starts to write a message</b></p>

> neuroListener.writingMsg(String alterUser, Context context);

<h3>Stop accelerometer measurement</h3>

<p><b>User sends the message</b></p>

> neuroListener.sendingMsg((message, this.getApplicationContext());

<p> <br><b>User closes the chat</b></p>

> neuroListener.chatClosed(String alterUser);