package ie.tcd.odonneb4;

  import java.io.BufferedReader;
  import java.io.IOException;
  import java.io.InputStream;
  import java.io.InputStreamReader;
  import java.nio.charset.StandardCharsets;
  import java.nio.file.Files;
  import java.nio.file.Path;
  import java.nio.file.Paths;

  import org.apache.lucene.analysis.Analyzer;
  import org.apache.lucene.analysis.en.EnglishAnalyzer;
 // import org.apache.lucene.analysis.standard.StandardAnalyzer;
  import org.apache.lucene.document.Document;
  import org.apache.lucene.document.Field;
  import org.apache.lucene.document.TextField;
  import org.apache.lucene.document.StringField;
  import org.apache.lucene.index.IndexWriter;
  import org.apache.lucene.index.IndexWriterConfig;
  import org.apache.lucene.search.similarities.*;
  import org.apache.lucene.store.Directory;
  import org.apache.lucene.store.FSDirectory;

  public class CreateIndex
  {

      // Directory where the search index will be saved
      private static String INDEX_DIRECTORY = "index";

      public static void create_index(String scoring) throws IOException{
          final Path cranInput = Paths.get("cran/cran.all.1400");

          // Analyzer that is used to process TextField
          Analyzer analyzer = new EnglishAnalyzer();

          // To store an index in memory
          // Directory directory = new RAMDirectory();
          // To store an index on disk
          Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
          IndexWriterConfig config = new IndexWriterConfig(analyzer);

          // Index opening mode
          // IndexWriterConfig.OpenMode.CREATE = create a new index
          // IndexWriterConfig.OpenMode.APPEND = open an existing index
          // IndexWriterConfig.OpenMode.CREATE_OR_APPEND = create an index if it
          // does not exist, otherwise it opens it
          config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

          switch (scoring) {
                  case "BM25":
                          config.setSimilarity(new BM25Similarity());
                          break;
                  case "Classic":
                          config.setSimilarity(new ClassicSimilarity());
                          break;
                  case "LMDirichlet":
                          config.setSimilarity(new LMDirichletSimilarity());
                          break;
                  case "Boolean":
                          config.setSimilarity(new BooleanSimilarity());
                          break;
                  case "BM25_Classic":
                          config.setSimilarity(new MultiSimilarity(new Similarity[]{new BM25Similarity(), new ClassicSimilarity()}));
                          break;
                  case "Classic_LMDirichlet":
                          config.setSimilarity(new MultiSimilarity(new Similarity[]{new ClassicSimilarity(), new LMDirichletSimilarity()}));
                          break;
                  case "BM25_LMDirichlet":
                          config.setSimilarity(new MultiSimilarity(new Similarity[]{new BM25Similarity(), new LMDirichletSimilarity()}));
                          break;
          }

          IndexWriter writer = new IndexWriter(directory, config);

          try(InputStream stream = Files.newInputStream(cranInput)){
                  BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                  System.out.println("Starting indexing...");

                  String line = br.readLine();
                  String field = "";



                  while(line != null){
                          Document doc = new Document();
                          if(line.startsWith(".I")){
                                  doc.add(new StringField("id", line.substring(3,line.length()),Field.Store.YES));
                                  line = br.readLine();
                                  while (!(line.startsWith(".I"))){
                                          if(line.startsWith(".T")){
                                                  line = br.readLine();
                                                  while(!line.startsWith(".A")){
                                                          field += line +" ";
                                                          line = br.readLine();
                                                  }
                                                  doc.add(new TextField("title", field, Field.Store.YES));
                                                  field = "";
                                          }
                                          if(line.startsWith(".A")){
                                                  line = br.readLine();
                                                  while(!line.startsWith(".B")){
                                                          field += line + " ";
                                                          line = br.readLine();
                                                  }
                                                  doc.add(new TextField("author", field, Field.Store.YES));
                                                  field = "";
                                          }
                                          if(line.startsWith(".B")){
                                                  line = br.readLine();
                                                  while(!line.startsWith(".W")){
                                                          field += line + " ";
                                                          line = br.readLine();
                                                  }
                                                  doc.add(new TextField("bibliography", field, Field.Store.YES));
                                                  field = "";
                                          }
                                          if(line.startsWith(".W")){
                                                  line = br.readLine();
                                                  while ((line != null) && (!line.startsWith(".I"))){
                                                          field += line + " ";
                                                          line = br.readLine();
                                                  }
                                                  doc.add(new TextField("content", field, Field.Store.YES));
                                                  field = "";
                                          }
                                          if(line == null){
                                                  break;
                                          }
                                  }
                                   // Save the document to the index
                                  writer.addDocument(doc);
                          }
                  }
          } finally {
                   // Commit changes and close everything
                  writer.close();
                  directory.close();
          }
      }
  }
