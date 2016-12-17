# Web-Search-Engine-Project
Project for WSE Fall 2016

**Important:**
- Download the dictionary for Chinese Segmentation from: 
- Unzip seg.zip and put it under /data

**Commands:**
<pre>mkdir class
javac -d class -cp .:./lib/* ./src/edu/nyu/cs/cs2580/*.java
java -cp ./class/:./lib/*:. edu.nyu.cs.cs2580.SearchEngine --mode=crawl --options=conf/engine.conf
java -cp ./class/:./lib/*:. edu.nyu.cs.cs2580.SearchEngine --mode=index --options=conf/engine.conf
java -cp ./class/:./lib/*:. edu.nyu.cs.cs2580.SearchEngine --mode=serve --port=25801 --options=conf/engine.conf
curl 'localhost:25801/search?query=%E6%9D%83%E5%88%A9'
java -cp ./class/:./lib/*:. edu.nyu.cs.cs2580.Evaluator judgement.txt rankdedResultsFor艾滋病.tsv 2
</pre>

When running search, ranked result for query, in this example, 艾滋病, is saved as a tsv file in root directory, in the
format of <query><tab><url>. We also have judgement file, judgement.txt in root file. Therefore, by running the last
command line we can evaluate our search engine using different metrics which is provided as the third argument.

10 queries are used for producing judgement file:
中国
教育
环境
艾滋病
民主
创新
恐怖主义
机器人
犯罪
数据
