package com.example.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CollectionHelper {
    public static boolean equalLists(Collection a, Collection b) {
        if(a == null && b == null) {
            return true;
        }

        if(a == null || b == null) {
            return false;
        }

        if (a.size() != b.size()) {
            return false;
        }

        return a.containsAll(b) && b.containsAll(a);
    }

    public static <T> boolean equalLists(Collection<T> a, Collection<T> b, EqualityComparer comparer) {
        if(a == null && b == null) {
            return true;
        }

        if(a == null || b == null) {
            return false;
        }

        if (a.size() != b.size()) {
            return false;
        }

        Map<Key, Count> counts = new HashMap<>();

        // Count the items in list1
        for (T item : a) {
            Key keyItem = new Key(item, comparer);

            if (!counts.containsKey(keyItem)) counts.put(new Key(item, comparer), new Count());
            counts.get(keyItem).count += 1;
        }

        // Subtract the count of items in list2
        for (T item : b) {
            Key keyItem = new Key(item, comparer);

            // If the map doesn't contain the item here, then this item wasn't in list1
            if (!counts.containsKey(keyItem)) {
                return false;
            }
            counts.get(keyItem).count -= 1;
        }

        // If any count is nonzero at this point, then the two lists don't match
        for (Map.Entry<Key, Count> entry : counts.entrySet()) {
            if (entry.getValue().count != 0) {
                return false;
            }
        }

        return true;
    }

    private static class Count {
        int count = 0;
    }

    public interface EqualityComparer<T> {
        boolean equals(T a, T b);
    }

    private static class Key {
        private Object obj;
        private EqualityComparer comparer;

        Key(Object obj, EqualityComparer comparer) {
            this.obj = obj;
            this.comparer = comparer;
        }

        @Override
        public boolean equals(Object o) {
            return comparer.equals(this.obj, ((Key)o).obj);

        }

        @Override
        public int hashCode() {
            return obj != null ? obj.hashCode() : 0;
        }
    }
}
