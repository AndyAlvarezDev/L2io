package l2.client.util;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;

public class CollectionsMethods {
    public static <T> int indexIf(List<T> list, Predicate<T> condition) {
        ListIterator<T> it = list.listIterator();
        while (it.hasNext())
            if (condition.test(it.next()))
                return it.previousIndex();
        return -1;
    }
}
