#!/bin/bash

# Run the dropSpatialIndex.sql batch file to drop existing geo table.
# Inside the dropSpatialIndex.sql, you should check whether the table exists. Drop ONLY if they exist.
mysql < dropSpatialIndex.sql

# Run the createSpatialIndex.sql batch file to create the database (if it does not exist) and the tables.
mysql < createSpatialIndex.sql

# Compile and create lucene index
javac Indexer.java

# Compile and create search
javac Searcher.java