package com.github.marcoral.versatia.core.impl.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CollectionsUtils {
    public static Collection<String> collectionMinus(Collection<String> firstCollection, Collection<String> secondCollection) {
        List<String> result = new ArrayList<>();
        for (Iterator<String> iterator = firstCollection.iterator(); iterator.hasNext(); ) {
            String element = iterator.next();
            if(!secondCollection.contains(element))
                result.add(element);
        }
        return result;
    }
}
