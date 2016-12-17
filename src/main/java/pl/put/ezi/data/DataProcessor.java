package pl.put.ezi.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.put.ezi.model.Document;
import pl.put.ezi.utils.EntrySetComparator;

/**
 * Procesor przetwarzania danych.
 *
 * @author Michal
 */
public class DataProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataProcessor.class);

    /**
     * Odczyt danych z pliku z dokumentami.
     *
     * @param data dane z pliku
     * @return zwraca listę dokumentów
     * @throws IOException
     */
    public static List<Document> processDocumentsFile(InputStream data) throws IOException {
        String fileData = IOUtils.toString(data);
        String[] fileDocuments = fileData.split("\\r?\\n\\r?\\n");
        List<Document> documents = new ArrayList<>();
        for (String fileDocument : fileDocuments) {
            String[] lines = fileDocument.split("\\r?\\n", 2);
            Document d = new Document(lines[0], lines[1]);
            documents.add(d);
        }
        return documents;
    }

    /**
     * Odczyt danych z pliku z keywordami.
     *
     * @param data dane z pliku
     * @return zwraca listę koeywordów
     * @throws IOException
     */
    public static Map<String, Double> processKeywordsFile(InputStream data) throws IOException {
        String fileData = IOUtils.toString(data);
        String[] fileKeywords = fileData.split("\\r?\\n");
        Map<String, Double> keywords = new HashMap<>();
        Arrays.asList(fileKeywords).stream().forEach(r -> keywords.put(r, Double.NaN));
        return keywords;
    }

    /**
     * Przetwarzanie dokumentu.
     *
     * @param document dokument do przetworzenia
     */
    public static void processDocument(Document document) {
        List<String> tokens = new ArrayList<>();

        //tokenizacja tytułu
        tokens.addAll(tokenize(document.getName()));

        //tokenizacja treści
        tokens.addAll(tokenize(document.getContent()));

        LOGGER.info("Tokenized document: {}", tokens);

        //standaryzacja
        tokens = standarize(tokens);

        //stemowanie
        tokens = stemming(tokens);

        tokens.stream().forEach(r -> document.addTerm(r));

        LOGGER.info("Document tokens: {}", tokens);
    }

    /**
     * Przetwarzanie keywordów.
     *
     */
    public static Map<String, Double> processKeywords(Set<String> keywords) {
        List<String> tokens;

        //standaryzacja
        tokens = standarize(new ArrayList<>(keywords));

        //stemowanie
        tokens = stemming(tokens);

        LOGGER.info("Standarized tokens: {}", tokens);

        Map<String, Double> m = new HashMap<>();
        tokens.stream().forEach(r -> m.put(r, Double.NaN));
        return m;
    }

    /**
     * Przeliczenie keywordów w dokumencie.
     *
     * @param document
     * @param keywords
     */
    public static void countKeywords(List<Document> documents, Map<String, Double> keywords) {
        for (Document document : documents) {
            for (String keyword : keywords.keySet()) {
                Long count = document.getTerms().stream().filter(r -> r.equals(keyword)).count();
                if (count > 0) {
                    document.addKeyword(keyword, count.doubleValue());
                    if (Double.isNaN(keywords.get(keyword))) {
                        keywords.put(keyword, 1d);
                    } else {
                        keywords.put(keyword, keywords.get(keyword) + 1);
                    }
                }
            }
        }

        //IDF
        for (Map.Entry<String, Double> e : keywords.entrySet()) {
            keywords.put(e.getKey(), Math.log10(documents.size() / e.getValue()));
        }

        //TF-IDF
        for (Document document : documents) {
            Double maxCount = document.getKeywords().entrySet().stream().max(new EntrySetComparator()).get().getValue();
            for (Map.Entry<String, Double> entry : document.getKeywords().entrySet()) {
                document.addKeyword(entry.getKey(), entry.getValue() / maxCount * keywords.get(entry.getKey()));
            }
        }
    }

    /**
     * Przeliczenie keywordów w dokumencie.
     *
     * @param document
     * @param keywords
     */
    public static void countKeywordsInSearchDocument(Document document, Map<String, Double> keywords) {
        for (String keyword : keywords.keySet()) {
            Long count = document.getTerms().stream().filter(r -> r.equals(keyword)).count();
            if (count > 0) {
                document.addKeyword(keyword, count.doubleValue());
            }
        }

        Double maxCount = document.getKeywords().entrySet().stream().max(new EntrySetComparator()).get().getValue();
        for (Map.Entry<String, Double> entry : document.getKeywords().entrySet()) {
            document.addKeyword(entry.getKey(), entry.getValue() / maxCount * keywords.get(entry.getKey()));
        }

    }

    /**
     * Stemowanie tokenów.
     *
     * @param tokens
     * @return
     */
    private static List<String> stemming(List<String> tokens) {
        List<String> stemmedTokens = new ArrayList<>();
        Stemmer s = new Stemmer();
        for (String token : tokens) {
            s.add(token.toCharArray(), token.length());
            s.stem();
            stemmedTokens.add(s.toString());
        }
        return stemmedTokens;
    }

    /**
     * Tokenizacja linii.
     *
     * @param string linia do tokenizacji.
     * @return
     */
    private static List<String> tokenize(String string) {
        List<String> tokens = new ArrayList<>();
        if (string == null) {
            return tokens;
        }
        Pattern p = Pattern.compile("([a-zA-Z]{2,})");
        Matcher m = p.matcher(string);
        while (m.find()) {
            tokens.add(m.group());
        }
        return tokens;
    }

    private static List<String> standarize(List<String> tokens) {
        List<String> standarizedTokens = new ArrayList<>();
        for (String token : tokens) {
            standarizedTokens.add(token.toLowerCase());
        }
        return standarizedTokens;
    }

    public static void searchDocuments(List<Document> documents, Document searchDocument) {
        Map<Document, Double> numerators = new HashMap<>();
        double mianownik = 0d;
        double mianownik2 = 0d;
        for (Document document : documents) {
            for (Map.Entry<String, Double> entry : document.getKeywords().entrySet()) {
                double docVal = entry.getValue();
                double searchVal;
                if (searchDocument.getKeywords().containsKey(entry.getKey())) {
                    searchVal = searchDocument.getKeywords().get(entry.getKey());
                } else {
                    searchVal = 0d;
                }
                if (numerators.containsKey(document)) {
                    numerators.put(document, numerators.get(document) + (docVal * searchVal));
                } else {
                    numerators.put(document, (docVal * searchVal));
                }
                mianownik += docVal * docVal;
                mianownik2 += searchVal * searchVal;
            }
            mianownik = Math.sqrt(mianownik);
            mianownik2 = Math.sqrt(mianownik2);
            mianownik = mianownik * mianownik2;
            document.setSimilarity(numerators.get(document) / mianownik);
            mianownik = 0d;
            mianownik2 = 0d;
        }
    }
}
