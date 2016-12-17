package pl.put.ezi.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Helper do wyświetlania mapy w tabeli.
 *
 * @author Michal
 */
public class KeywordsHelper {

    private String name;
    private Double idf;

    public KeywordsHelper(String name, Double idf) {
        this.name = name;
        this.idf = idf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getIdf() {
        return idf;
    }

    public void setIdf(Double idf) {
        this.idf = idf;
    }

    public static List<KeywordsHelper> getInstances(Map<String, Double> keywords) {
        List<KeywordsHelper> list = new ArrayList<>();

        for (Map.Entry<String, Double> entry : keywords.entrySet()) {
            list.add(new KeywordsHelper(entry.getKey(), entry.getValue()));
        }

        return list;
    }

}
