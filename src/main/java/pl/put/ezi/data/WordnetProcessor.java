package pl.put.ezi.data;

import edu.mit.jwi.Dictionary;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.wordnet.SynExpand;
import org.apache.lucene.wordnet.SynonymMap;
import org.apache.lucene.wordnet.Syns2Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Klasa do obsługi danych Wordnetu.
 *
 * @author Michal
 */
public class WordnetProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(WordnetProcessor.class);
    private static final String PATH = "D:\\Projekty\\Studia_PUT\\EZI\\wordnet";
    private static final String LUCENE_PATH = PATH + "\\lucene\\";
    private Dictionary dictionary;
    private SynonymMap synonymMap;

    public WordnetProcessor() throws FileNotFoundException, IOException {
    }

    public Dictionary getDictionary() throws Throwable {
//        File file = new File(PATH);
//        FileInputStream fileInputStream = new FileInputStream(file);
//        this.synonymMap = new SynonymMap(fileInputStream);
        initDictionary();
        return dictionary;
    }

    private void initDictionary() throws Throwable {
        String[] args = new String[2];
        args[0] = PATH + "\\prolog\\wn_s.pl";
        args[1] = LUCENE_PATH;
        LOGGER.info("Begin index");
        Syns2Index.main(args);
        LOGGER.info("End index");
//        File file = new File(PATH);
//        LOGGER.info(file.getAbsolutePath());
//        dictionary = new Dictionary(file);
    }
    
    public static List<String> expandQuery(String query) throws IOException {
        List<String> expandedQueries = new ArrayList<>();
        Query extendedQuery;
        Directory directory = FSDirectory.open(new File(LUCENE_PATH));
                 
        extendedQuery = SynExpand.expand(query, new IndexSearcher(directory), null, null, 0.9f);
        directory.close();
        Set<Term> terms = new HashSet<>();
        extendedQuery.extractTerms(terms);
        terms.stream().forEach((t) -> {
            LOGGER.info("F={}, T={}", t.field(), t.text());
            if (!query.contains(t.text()) && expandedQueries.size() < 5) {
                expandedQueries.add(query + " " + t.text());
            }
        });
        LOGGER.info("{}={}", query, extendedQuery);
        return expandedQueries;
    }

}
