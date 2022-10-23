# CS7IS3 - Assignment 1

This project implements and studies the performance of the Apache Lucene search engine library against the Cranfield collection. It was created on a Linux virtual machine through Microsoft Azure. Lucene is an open-source, free-to-download and full-featured text search engine library written entirely in Java. The project uses Apache Lucene version 8.10.0, along with OpenJDK 1.9.0_292. 

Additionally, the Cranfield Collection was downloaded from http://ir.dcs.gla.ac.uk/resources/test_collections/cran/ and consists of 1400 abstracts along with 225 queries and relevance judgements. This collection was then indexed and its queries were searched to test the performance of Apache Lucene. 
## Structure
The folder structure of the repository, along with their description, is as follows:

* cran - contains documents from cranfield collections, including the corrected_qrel.txt file
* index - contains the index folder
* results - contains the txt files produced from search_queries method
* src - contains source code to be executed
* target - contains maven compiled jar files
* trec_eval-9.0.7 - contains trec eval files (downloadable from https://trec.nist.gov/trec_eval/trec_eval-9.0.7.tar.gz)

The java scripts can be found in the src/main/java/ie/tcd/odonneb4 folder and consist of three files:

* Main.java - contains the main() method which implements tests for 7 different scoring approaches. Additionally, it contains the correct_qrel() and run_test() methods. The latter method takes the scoring String argument and runs it through the create_index() and search_queries() methods. The correct_qrel() method creates a new corrected_cranqrel.txt file of relevance judgements which is compatible with trec_eval.
* CreateIndex.java - contains the create_index() method which parses the cran.all.1400 file, and indexes the documents based on the similarity score of the method’s scoring argument. 
* SearchQuery.java - contains the search_queries() method which takes the cran.qrl file and searches each 225 queries against the existing index. It similarly takes a scoring argument to set the same similarity score as the index it is searching. This method produces a query_results file within the results folder in a suitable trec_eval format, (ie. query_id, Q0, document_id, rank, scoring approach).

## Instructions
To index the cran files and search the queries, clone the repository and run the following commands:
```
cd /assignment1
java -jar target/assignment1-1.0.jar
```
For Mean Average Precision (MAP) score, run the following command for each scoring approach:
```
trec_eval-9.0.7/trec_eval -m map cran/cranqrel_corrected.txt results/query_results_[scoring].txt
```
For recall score, run the following command for each scoring approach:
```
trec_eval-9.0.7/trec_eval -m recall cran/cranqrel_corrected.txt results/query_results_[scoring].txt
```
## Scoring
7 different scoring approaches for selected for testing, including:
* BM25
* Classic
* LMDirichlet
* Boolean
* M25_Classic (a combination of BM25 and Classic similarity values)
* Classic_LMDirichlet (a combination of Classic and LMDirichlet similarity values)
* BM25_LMDirichlet (a combination of BM25 and LMDirichlet similarity values)
