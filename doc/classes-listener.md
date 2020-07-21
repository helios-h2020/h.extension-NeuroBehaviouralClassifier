<h3>NeurobehaviourListener class</h3>

<p>This class implements each function defined in the interface.</p>

```java
import eu.h2020.helios_social.core.messaging.HeliosMessage;
public class NeurobehaviourListener implements NeurobehaviourInterface {
   @Override
   public void writingMsg(String alterUser, Context context) {
   @Override
   public void readingChat (String alterUser, Context context)
   //LAB - Start accelerometer when session begins
   public void startAccel(String uuid, Context context
   @Override
   public void chatClosed (String alterUser)
   public void stopAccel( )
   @Override
   public void inboxMsg(HeliosMessage message, Context context)
   @Override
   public void sendingMsg(HeliosMessage message, Context context)
   @Override
   public int[] egoAlterTrust(String alterUser)
   @Override
   public void createCsv(String fileType, Context context, String userName)
   @Override
   public void writeData(String data)
   public void writeImageData(String data)
   public Boolean GetCsvReady()
   public void SetCsvReady(Boolean value)
   public void setUserName (String name)
   public String getUserName()
   public Boolean GetCsvImageReady()
   public void SetCsvImageReady(Boolean value)
}
```
