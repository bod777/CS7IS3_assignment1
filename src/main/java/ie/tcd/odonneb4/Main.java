package ie.tcd.odonneb4;


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
  public static void correct_qrel() {
	  System.out.println("Correcting cranqrel file for trec_eval process...");
	  final Path qrelFile = Paths.get("cran/cranqrel");
	  String correctedFile = "cran/cranqrels_corrected.txt";
      PrintWriter iwriter = new PrintWriter(correctedFile, "UTF-8");4
      
      try(InputStream stream = Files.newInputStream(qrelFile)){
          BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
          System.out.println("Reading Relevant Scores...");

          String line = br.readLine();

          while(line!=null) {
              String[] entry = line.split(" ");
              switch(entry[2]){
                  case "1 ":
                      entry[2]="4 ";
                  case "2 ":
                      entry[2]="3 ";
                  case "3 ":
                      entry[2]="2 ";
                  case "4 ":
                      entry[2]="1 ";
                  case "-1 ":
                      entry[2]="5 ";
              }
        	  iwriter.println(entry[0]+" 0 "+entry[1]+" "+entry[2]);
        	  String line = br.readLine();
          }
          System.out.println("Correction completed.");
      }
  }
}

