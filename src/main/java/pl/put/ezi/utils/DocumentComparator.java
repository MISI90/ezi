package pl.put.ezi.utils;

import java.util.Comparator;
import pl.put.ezi.model.Document;

/**
 *
 * @author Michal
 */
public class DocumentComparator implements Comparator<Document> {

    @Override
    public int compare(Document o1, Document o2) {
        return o2.getSimilarity().compareTo(o1.getSimilarity());
    }

}
