<h3>Sentiment analysis class</h3>

<p>This class performs a sentimental analysis of each image sent by user using Helios chat. Using deep learning models, this module extracts:</p>

<ul>
    <li>Number of faces</li>
    <li>Sentiment of each face: happy, fear, anger...</li>
    <li>Score of each face sentimental analysis</li>
</ul>

<p>Results are saved in a text file and are drawn over the image:</p>

<p><img src="./image-analysis.jpg" alt="image analysis" /></p>

<h3>Text analysis</h3>

<p>This class performs too a sentimental analysis of each text sent by user in Helios chat. Using deep learning models, this function extracts:</p>

<ul>
	<li>Text translation from any language to english using Google API</li>
	<li>Tags for each word of text</li>
	<li>Result of sentiment analysis of text. Positive / negative and score</li>
</ul>

<code>
    public class SentimentalAnalysis extends AppCompatActivity {
        public void runThread(final Context context, final String fileName, final HeliosMessageListener messageListener, final HeliosTopic topic, final HeliosMessage message)
        public int realImageWidth(String resource)
        private String getExtension(String word)
		private void textAnalysis (Context context, String message, HeliosMessageListener messageListener, HeliosTopic topic)
        private void pythonAnalysis (Context context, String script, String picture, Float scale, ImageView imageView, HeliosMessageListener messageListener, HeliosTopic topic)
        private void paintingImage(PyObject squares, PyObject emotions, PyObject scores, String imageResource, Float scale, ImageView imageView, Context context, HeliosMessageListener messageListener, HeliosTopic topic)
        public String getDate(long time)
		private void saveImageData (Context context, int numFaces, String emotionsData, String scoreData)
		private void saveTextData (Context context, PyObject origText, PyObject engText, PyObject tags, PyObject emotions) 
    }
</code>