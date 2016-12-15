# Web-Search-Engine-Project
Project for WSE Fall 2016

**Important:**
- Download the dictionary for Chinese Segmentation from: 
- Unzip seg.zip and put it under /data

**Commands:**
<pre>javac -cp lib/jsoup-1.10.1.jar:lib/json-simple-1.1.1.jar:lib/slf4j-api.jar:lib/slf4j-simple.jar:lib/stanford-segmenter-3.6.0.jar src/edu/nyu/cs/cs2580/*.java
java -cp src:lib/jsoup-1.10.1.jar:lib/json-simple-1.1.1.jar edu.nyu.cs.cs2580.SearchEngine --mode=crawl --options=conf/engine.conf
java -cp src:lib/jsoup-1.10.1.jar:lib/json-simple-1.1.1.jar:lib/slf4j-api.jar:lib/slf4j-simple.jar:lib/stanford-segmenter-3.6.0.jar edu.nyu.cs.cs2580.SearchEngine --mode=index --options=conf/engine.conf
java -cp src:lib/jsoup-1.10.1.jar:lib/json-simple-1.1.1.jar:lib/slf4j-api.jar:lib/slf4j-simple.jar:lib/stanford-segmenter-3.6.0.jar edu.nyu.cs.cs2580.SearchEngine --mode=serve --port=25801 --options=conf/engine.conf
curl 'localhost:25801/search?query=%E6%9D%83%E5%88%A9'</pre> 
