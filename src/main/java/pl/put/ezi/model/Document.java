package pl.put.ezi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Dokument.
 *
 * @author Michal
 */
public class Document {

    private final String name;
    private final String content;
    private final List<String> terms;

    public Document(String name, String content) {
        this.name = name;
        this.content = content;
        this.terms = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public List<String> getTerms() {
        return terms;
    }

    /**
     * Dodanie termu do dokumentu.
     *
     * @param term
     */
    public void addTerm(String term) {
        this.terms.add(term);
    }

}
