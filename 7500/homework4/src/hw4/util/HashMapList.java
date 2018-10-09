package hw4.util;

import java.util.*;

public class HashMapList<K,V> {

	private Map<K,List<V>> map = new HashMap();
	
	public HashMapList() {
		
	}

	public int size() {
		return map.size();
	}

	public Set<Map.Entry<K,List<V>>> entrySet() {
		return map.entrySet();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Collection<V> valuesFlatten() {
		List<V> res = new ArrayList();
		for (List<V> l : map.values()) {
			res.addAll(l);
		}
		return res;
	}

	public Collection<List<V>> values() {
		return map.values();
	}

	public Set<K> keySet() {
		return map.keySet();
	}
	
	public List<V> get(K k) {
		List<V> l = map.get(k);
		return l == null ? Collections.EMPTY_LIST : l;
	}
	
	public void put(K k, V v) {
		List<V> l = map.get(k);
		if (l == null) {
			l = new ArrayList();
			map.put(k, l);
		}
		l.add(v);		
	}

	public void putAll(K k, Collection<V> vcoll) {
		List<V> l = map.get(k);
		if (l == null) {
			l = new ArrayList();
			map.put(k, l);
		}
		l.addAll(vcoll);
	}

	public void putAll(HashMapList<K,V> m) {
		for (K k : m.keySet()) {
			putAll(k, m.get(k));
		}
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
