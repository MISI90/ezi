package pl.put.ezi.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.put.ezi.model.Document;

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
    public static List<String> processKeywordsFile(InputStream data) throws IOException {
        String fileData = IOUtils.toString(data);
        String[] fileKeywords = fileData.split("\\r?\\n");
        return Arrays.asList(fileKeywords);
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

        LOGGER.info("Standarized tokens: {}", tokens);
    }

    /**
     * Tokenizacja linii.
     *
     * @param string linia do tokenizacji.
     * @return
     */
    private static List<String> tokenize(String string) {
        List<String> tokens = new ArrayList<>();
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
}
