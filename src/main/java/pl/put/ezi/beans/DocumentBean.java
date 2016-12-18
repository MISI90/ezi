package pl.put.ezi.beans;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import pl.put.ezi.model.Document;

/**
 *
 * @author Michal
 */
@Named
@SessionScoped
public class DocumentBean implements Serializable {

    private Document document;

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public String getTfidf(Document document) {

        return document.getKeywords().toString();

    }

}
