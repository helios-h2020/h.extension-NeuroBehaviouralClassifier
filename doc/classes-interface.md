<h3>NeurobehaviourInterface class</h3>

<p>This interface class define functions used in the listener and its parameters</p>

> import eu.h2020.helios_social.core.messaging.HeliosMessage;
> public interface NeurobehaviourInterface {
>> //Accelerometer - Events to call when user open a message to write or read it
>> void writingMsg (String alterUser, Context context);
>> void readingChat (String alterUser, Context context);
>> void chatClosed (String alterUser);<br>
>> //Sentimental analysis for media chat content
>> void inboxMsg (HeliosMessage message, Context context);
>> void sendingMsg (HeliosMessage message, Context context);<br>
>> //Sentimental analysis of Ego - Alter relationship
>> //based on previous Trust value and new communications analysis
>> int[] egoAlterTrust (String alterUser);<br>
>> //External data storage to save neurobehavioural metrics used by the module
>> void createCsv(String file, Context context, String userName);
>> void writeData(String data);
> }
