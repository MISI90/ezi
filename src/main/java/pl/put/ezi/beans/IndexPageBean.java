package pl.put.ezi.beans;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.put.ezi.data.DataProcessor;
import pl.put.ezi.model.Document;
import pl.put.ezi.utils.DocumentComparator;
import pl.put.ezi.utils.KeywordsHelper;

/**
 *
 * @author Michal
 */
@Named(value = "indexPageBean")
@SessionScoped
public class IndexPageBean implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexPageBean.class);

    private InputStream documents;
    private InputStream keywords;
    private List<Document> documentList = new ArrayList<>();
    private Map<String, Double> keywordsMap = new HashMap<>();
    private String searchTerm;
    private List<Document> sortedDocuments = new ArrayList<>();
    private List<KeywordsHelper> keywordsHelpers = new ArrayList<>();

    /**
     * Creates a new instance of IndexPageBean
     */
    public IndexPageBean() {
    }

    public InputStream getDocuments() {
        return documents;
    }

    public void setDocuments(InputStream documents) {
        this.documents = documents;
    }

    public InputStream getKeywords() {
        return keywords;
    }

    public void setKeywords(InputStream keywords) {
        this.keywords = keywords;
    }

    public List<Document> getDocumentList() {
        return documentList;
    }

    public void setDocumentList(List<Document> documentList) {
        this.documentList = documentList;
    }

    public Map<String, Double> getKeywordsMap() {
        return keywordsMap;
    }

    public void setKeywordsMap(Map<String, Double> keywordsMap) {
        this.keywordsMap = keywordsMap;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public List<Document> getSortedDocuments() {
        return sortedDocuments;
    }

    public void setSortedDocuments(List<Document> sortedDocuments) {
        this.sortedDocuments = sortedDocuments;
    }

    public List<KeywordsHelper> getKeywordsHelpers() {
        return KeywordsHelper.getInstances(keywordsMap);
    }

    /**
     * Handler dla akcji wczytania dokumentów.
     *
     * @param event
     */
    public void documentFileUploadHandler(FileUploadEvent event) throws IOException {
        LOGGER.info("Document file uploaded: {}", event.getFile().getFileName());
        this.documents = event.getFile().getInputstream();
    }

    /**
     * Handler dla akcji wczytania termów.
     *
     * @param event
     */
    public void keywordFileUploadHandler(FileUploadEvent event) throws IOException {
        LOGGER.info("Keywords file uploaded: {}", event.getFile().getFileName());
        this.keywords = event.getFile().getInputstream();
    }

    /**
     * Przetwarzaj dane pobrane z plików.
     */
    public void processData() throws IOException {
        LOGGER.info("Process data");
        documentList = DataProcessor.processDocumentsFile(documents);
        keywordsMap = DataProcessor.processKeywordsFile(keywords);

        for (Document document : documentList) {
            DataProcessor.processDocument(document);
        }

        keywordsMap = DataProcessor.processKeywords(keywordsMap.keySet());

        DataProcessor.countKeywords(documentList, keywordsMap);
    }

    public void searchAction() {
        sortedDocuments.clear();
        Document searchDocument = new Document(null, this.searchTerm);
        DataProcessor.processDocument(searchDocument);
        DataProcessor.countKeywordsInSearchDocument(searchDocument, keywordsMap);
        DataProcessor.searchDocuments(documentList, searchDocument);
        sortedDocuments.addAll(documentList.stream().filter(r -> r.getSimilarity() > 0).collect(Collectors.toList()));
        Collections.sort(sortedDocuments, new DocumentComparator());

    }
}
