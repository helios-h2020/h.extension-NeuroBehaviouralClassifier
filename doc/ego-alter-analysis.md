<h2>Ego - Alter relationship sentimental analysis</h2>

<p>This function re-calculates sentimental metrics for one period of time according to:</p>

<ul>
	<li>Previous Trust metrics</li>
	<li>New metadata from Ego - Alter communications</li>
</ul>

<h3>Calling from Trust module</h3>

> //Using Neurobehaviour module listener class to send info about chat communications
> import eu.h2020.helios_social.modules.neurobehaviour.NeurobehaviourListener;
> //Listener init
> private NeurobehaviourListener listen;

<p> <br><b>onCreate method</b></p>

> listen = new NeurobehaviourListener();

<p> <br><b>Updating Ego - Alter Trust value</b></p>

> int[] newTrustValue = listen.egoAlterTrust(String alterUser);
<br>
<br>
<p><b>egoAlterTrust</b> function returns an array with values to calculate <b>new Trust value</b> in Trust module:</p>
<ul>
	<li>Attention</li>
	<li>Arousal</li>
	<li>Valence</li>
	<li>Personality</li>
</ul>
