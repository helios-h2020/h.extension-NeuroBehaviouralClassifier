<h2> </h2>
<h2>TestClient app changes</h2>

<p>Neurobehaviour module uses deep learning models to extract a sentimental analysis of images and texts using a Python scripts wrapper.</p>

<p>There is a branch in TestClient repository with modifications to include calls to Neurobehaviour module and image sentimental analysis features:</p>

<p><a href="https://scm.atosresearch.eu/ari/helios_group/testclient/tree/Neurobehaviour_module_integration-UPV" target="_blank" title="Neurobehaviour module integration branch">https://scm.atosresearch.eu/ari/helios_group/testclient/tree/Neurobehaviour_module_integration-UPV</a></p>

<h2>Cloning TestClient branch</h2>
<p>git clone --single-branch --branch  Neurobehaviour_module_integration-UPV https://scm.atosresearch.eu/ari/helios_group/testclient.git</p>

<br>
 <p>This version contains:</p>
    <ul style="margin-left:30px">
        <li>Permission to use system sensors in AndroidManifest.xml</li>
        <li>Calls to Neurobehaviour module into TestClient activities</li>
        <li>Modification in project's top-level build.gradle file to add Python wrapper repository and dependencies</li>
    </ul>
    <br>    
<ul>
    <li><b>build.gradle file (Project: testclient)</b> modifications:</li>
</ul>
    <ul style="margin-left:30px">
        <li>repositories -> <code>maven { url 'https://chaquo.com/maven' } </code></li>
        <li>dependencies -> <code>classpath 'com.chaquo.python:gradle:8.0.0' </code></li>
    </ul>
<br>
<ul>
    <li>In order to use Python wrapper feature, please edit your <b>local.properties</b> file and add:</li>
</ul>
    <p style="margin-left:30px">
        > chaquopy.license=ADCOZh9lSwXa7BArj2j1gni5JznpJJWNZ6nbNhR9FTLx
		> chaquopy.applicationId=eu.h2020.helios_social.heliostestclient
    </p>
    <br>
<ul>
    <li>Finally, rebuild your project in Android Studio.</li>
</ul>
    
    

