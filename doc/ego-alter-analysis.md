<h2>Ego - Alter relationship sentimental analysis</h2>

<p>This function re-calculates sentimental metrics for one period of time according to:</p>

<ul>
	<li>Previous values</li>
	<li>User's context</li>
	<li>New metadata from Ego - Alter communications</li>
</ul>

<h3>Calling from Trust module</h3>

```java
//Using Neurobehaviour module listener class to send info about chat communications
import eu.h2020.helios_social.modules.neurobehaviour.NeurobehaviourListener;
//Listener init
private NeurobehaviourListener listen;
```

<p> <br><b>onCreate method</b></p>

```java
listen = new NeurobehaviourListener();
```

<p> <br><b>Updating Ego - Alter Trust value</b></p>

```java
String[][] newTrustValue = listen.egoAlterTrust(String alterUser);
```
<br>
<br>
<p><b>egoAlterTrust</b> function returns a matrix with values to calculate <b>new Trust value</b> in Trust module according to each context:</p>
<ul>
	<li>Arousal: positive / negative</li>
	<li>Valence: positive / negative</li>
	<li>Attention: low / medium / high</li>
</ul>
<p><b>Matrix of values to return:</b></p>

```java
String[][] dummyMatrix = {
   {"Context", "Valence", "Arousal", "Attention"},
   {"Home", "Positive", "Positive", "Medium"},
   {"Work", "Positive", "Negative", "High"}
};
```
