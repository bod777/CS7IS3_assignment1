package ie.tcd.odonneb4;

import java.io.IOException;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.apache.lucene.analysis.Analyzer;
// import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;

import org.apache.lucene.search.similarities.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.index.DirectoryReader;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;

import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
// import org.apache.lucene.store.RAMDirectory;

public class SearchQuery
{
    // Directory where the search index will be saved
    private static String INDEX_DIRECTORY = "index";

    public static void search_queries(String scoring) throws ParseException, IOException{
        final Path cranQueries = Paths.get("cran/cran.qry");

        // Open the folder that contains our search index
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

        // create objects to read and search across the index
        DirectoryReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        Analyzer analyzer = new EnglishAnalyzer();
        switch (scoring) {
                case "BM25":
                        searcher.setSimilarity(new BM25Similarity());
                        break;
                case "Classic":
                        searcher.setSimilarity(new ClassicSimilarity());
                        break;
                case "LMDirichlet":
                        searcher.setSimilarity(new LMDirichletSimilarity());
                        break;
                case "Boolean":
                        searcher.setSimilarity(new BooleanSimilarity());
                        break;
                case "BM25_Classic":
                        searcher.setSimilarity(new MultiSimilarity(new Similarity[]{new BM25Similarity(), new ClassicSimilarity()}));
                        break;
                case "Classic_LMDirichlet":
                        searcher.setSimilarity(new MultiSimilarity(new Similarity[]{new ClassicSimilarity(), new LMDirichletSimilarity()}));
                        break;
                case "BM25_LMDirichlet":
                        searcher.setSimilarity(new MultiSimilarity(new Similarity[]{new BM25Similarity(), new LMDirichletSimilarity()}));
                        break;
        }


        String result_file_path = "results/query_results_"+scoring+".txt";
        PrintWriter iwriter = new PrintWriter(result_file_path, "UTF-8");

        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"title", "author", "bibliography", "content"}, analyzer);
        //QueryParser qparser = new QueryParser("content",analyzer);

        try(InputStream stream = Files.newInputStream(cranQueries)){
                BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                System.out.println("Reading Queries...");

                String line = br.readLine();

                String id = ""; // Creating our own ID because the IDs in cran.qry are not in order.
                int n = 0;
                String queryString = "";

                while(line!=null) {
                        n = n + 1;
                        if (line.startsWith(".I")) { //ID no.
                                id = Integer.toString(n);
                                line = br.readLine();
                        }
                        if (line.startsWith(".W")) { // Content of query.
                                line = br.readLine();
                                while (line!=null && !line.startsWith(".I")) { //Read till the next ID or till end of file (for last query).
                                        queryString += line + " ";
                                        line = br.readLine();
                                }
                        }
                        queryString = queryString.trim(); // Remove spaces in beg and end.
                        queryString = queryString.replace("?", "");  // Remove '?' marks as Lucene threw an error because it's a WildcardQuery character.

                        Query query = parser.parse(QueryParser.escape(queryString));

                        // Searching.

                        // Supplying the query to the searcher.
                        TopDocs query_results = searcher.search(query,1000);      // Returning 1000 hits.
                        ScoreDoc[] hits = query_results.scoreDocs; // All relevant documents.

                        // Writing into the results.txt file.
                        // This needs to be in the format for trec_eval
                        // query_id, Q0, document_id, rank, score, STANDARD
                        // System.out.println(hits.length);
                        for (int i = 0; i < hits.length; ++i) { // 225 queries => 1000 hits. Results file - 225*1000
                                Document doc = searcher.doc(hits[i].doc);
                                iwriter.println(Integer.parseInt(id) + " 0 " + doc.get("id") + " " + i + " " + hits[i].score + " "+scoring);
                        }
                        queryString = "";
                }
        } finally {
                 // Commit changes and close everything
                iwriter.close();
                directory.close();
        }
    }
}

