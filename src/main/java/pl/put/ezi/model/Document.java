package pl.put.ezi.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dokument.
 *
 * @author Michal
 */
public class Document {

    private final String name;
    private final String content;
    private final List<String> terms;
    private final Map<String, Double> keywords;
    private Double similarity;

    public Document(String name, String content) {
        this.name = name;
        this.content = content;
        this.terms = new ArrayList<>();
        this.keywords = new HashMap<>();
        this.similarity = 0d;
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

    public Map<String, Double> getKeywords() {
        return keywords;
    }

    public Double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Double similarity) {
        this.similarity = similarity;
    }

    /**
     * Dodanie termu do dokumentu.
     *
     * @param term
     */
    public void addTerm(String term) {
        this.terms.add(term);
    }

    /**
     * Dodanie keyworda.
     *
     * @param keyword
     * @param count
     */
    public void addKeyword(String keyword, Double count) {
        this.keywords.put(keyword, count);
    }

}
