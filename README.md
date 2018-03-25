**GraphViewer**

This is a repository for GraphViewer Code Sample to plot graphs.

Try it

git clone https://github.com/herevrush/GraphViewer.git

cd GraphViewer

mvn install

java -jar target/GraphViewer-1.0-SNAPSHOT-jar-with-dependencies.jar 

![Alt text](images/screen.png?raw=true "GraphViwer Screenshot")


Steps to generate the above graph:-

1) Load the node csv file

2) Load the edges csv file

3) Generate graph (it is bit slow, it helps if it is run on GPU)

4) Select node by clicking the node ( the node color will change to yellow)

5) Node properties will be displayed on the lower panel

6) Click the beautify graph button to change the graph layout 

Prerequisites :-

Oracle Java 8

Maven
