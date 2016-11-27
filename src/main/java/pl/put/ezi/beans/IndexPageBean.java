package pl.put.ezi.beans;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.put.ezi.data.DataProcessor;
import pl.put.ezi.model.Document;

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
    private List<String> keywordsList = new ArrayList<>();

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

    public List<String> getKeywordsList() {
        return keywordsList;
    }

    public void setKeywordsList(List<String> keywordsList) {
        this.keywordsList = keywordsList;
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
        keywordsList = DataProcessor.processKeywordsFile(keywords);

        DataProcessor.processDocument(documentList.get(0));
    }
}
