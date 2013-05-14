package gr.plato.dashboard.service.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class CompareUtil {

	public static boolean equalsIgnoreNull(Object a, Object b) {
		return a == null ? (b == null ? true : false) : (b == null ? false : a
			.equals(b));
	}

	public static <T> boolean compareCollections(Collection<T> collection1, Collection<T> collection2, Comparator<T> comparator) {
		if (collection1.size() != collection2.size()) {
			return false;
		}
		Set<T> set1 = new TreeSet<T>(comparator);
		set1.addAll(collection1);
		Set<T> set2 = new TreeSet<T>(comparator);
		set2.addAll(collection2);
		return set1.containsAll(set2);
	}
}
