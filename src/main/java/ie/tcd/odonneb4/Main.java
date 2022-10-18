package ie.tcd.odonneb4;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.apache.lucene.queryparser.classic.ParseException;
import java.io.IOException;

public class Main {

  public static void main(String[] args) {
    try {
      run_test("BM25");
      run_test("Classic");
      run_test("LMDirichlet");
      run_test("Boolean");
      run_test("BM25_Classic");
      run_test("Classic_LMDirichlet");
      run_test("BM25_LMDirichlet");
      correct_qrel();
    } catch (Exception e) {
      System.out.println(e.getClass());
    }
  }
  public static void run_test(String scoring) throws ParseException, IOException {
	System.out.println("Starting process for "+scoring+" test");
    CreateIndex indexer = new CreateIndex();
    indexer.create_index(scoring);
    System.out.println("Finished indexing process for "+scoring+" test");

    SearchQuery searcher = new SearchQuery();
    searcher.search_queries(scoring);
    System.out.println("Finished querying process for "+scoring+" test");
  }
  public static void correct_qrel() throws IOException {
    System.out.println("Correcting cranqrel file for trec_eval process...");
    final Path qrelFile = Paths.get("cran/cranqrel");
    String correctedFile = "cran/cranqrel_corrected.txt";
    PrintWriter iwriter = new PrintWriter(correctedFile, "UTF-8");

    InputStream stream = Files.newInputStream(qrelFile);
    BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
    System.out.println("Reading Relevant Scores...");

    String line = br.readLine();

    for (int i = 0; i < 1837; i++) {
  	  String[] score = line.split(" ");
	  switch(score[2]){
	    case "1":
		  score[2]="4";
		  break;
	    case "2":
		  score[2]="3";
		  break;
	    case "3":
		  score[2]="2";
		  break;
	    case "4":
		  score[2]="1";
		  break;
	    case "-1":
		  score[2]="5";
		  break;
	  }
	  iwriter.println(score[0]+" 0 "+score[1]+" "+score[2]);
	  line = br.readLine();
    }
    System.out.println("Correction completed.");
    iwriter.close();
    br.close();
  }
}

