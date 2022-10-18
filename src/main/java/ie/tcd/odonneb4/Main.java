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
}

