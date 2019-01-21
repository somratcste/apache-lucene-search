#!/bin/bash

# Run the dropSpatialIndex.sql batch file to drop existing geo table.
# Inside the dropSpatialIndex.sql, you should check whether the table exists. Drop ONLY if they exist.
mysql < dropSpatialIndex.sql

# Run the createSpatialIndex.sql batch file to create the database (if it does not exist) and the tables.
mysql < createSpatialIndex.sql

# Compile and create lucene index
javac -cp /usr/share/java/mysql-connector-java-5.1.28.jar:/usr/share/java/lucene-core-5.4.0.jar:/usr/share/java/lucene-analyzers-common-5.4.0.jar:/usr/share/java/lucene-queryparser-5.4.0.jar:/usr/share/java/lucene-queries-5.4.0.jar:. Indexer.java
java -cp /usr/share/java/mysql-connector-java-5.1.28.jar:/usr/share/java/lucene-core-5.4.0.jar:/usr/share/java/lucene-analyzers-common-5.4.0.jar:/usr/share/java/lucene-queryparser-5.4.0.jar:/usr/share/java/lucene-queries-5.4.0.jar:. Indexer


# Compile and create search
javac -cp /usr/share/java/mysql-connector-java-5.1.28.jar:/usr/share/java/lucene-core-5.4.0.jar:/usr/share/java/lucene-analyzers-common-5.4.0.jar:/usr/share/java/lucene-queryparser-5.4.0.jar:/usr/share/java/lucene-queries-5.4.0.jar:. Searcher.java
echo "Using: java Searcher superman"
java -cp /usr/share/java/mysql-connector-java-5.1.28.jar:/usr/share/java/lucene-core-5.4.0.jar:/usr/share/java/lucene-analyzers-common-5.4.0.jar:/usr/share/java/lucene-queryparser-5.4.0.jar:/usr/share/java/lucene-queries-5.4.0.jar:. Searcher "superman"
echo "Using: java Searcher star trek -x -73.997255 -y 40.732371 -w 10"
java -cp /usr/share/java/mysql-connector-java-5.1.28.jar:/usr/share/java/lucene-core-5.4.0.jar:/usr/share/java/lucene-analyzers-common-5.4.0.jar:/usr/share/java/lucene-queryparser-5.4.0.jar:/usr/share/java/lucene-queries-5.4.0.jar:. Searcher "star trek" -x -73.997255 -y 40.732371 -w 10
