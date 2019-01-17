package fr.inra.toulouse.metexplore.met4j_io.annotations;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

public class BioAnnotationCollection {

	BioCollection<BioAnnotation> annotations;

	public int hashCode() {
		return annotations.hashCode();
	}

	public String toString() {
		return annotations.toString();
	}

	public Boolean containsId(String id) {
		return annotations.containsId(id);
	}

	public Boolean containsName(String name) {
		return annotations.containsName(name);
	}

	public BioAnnotation getEntityFromId(String id) {
		return annotations.getEntityFromId(id);
	}

	public Set<String> getIds() {
		return annotations.getIds();
	}

	public BioCollection<BioAnnotation> getEntitiesFromName(String name) {
		return annotations.getEntitiesFromName(name);
	}

	public boolean equals(Object obj) {
		return annotations.equals(obj);
	}

	public int size() {
		return annotations.size();
	}

	public boolean isEmpty() {
		return annotations.isEmpty();
	}

	public Iterator<BioAnnotation> iterator() {
		return annotations.iterator();
	}

	public Object[] toArray() {
		return annotations.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return annotations.toArray(a);
	}

	public boolean retainAll(Collection<?> c) {
		return annotations.retainAll(c);
	}

	public void clear() {
		annotations.clear();
	}

	public boolean removeAll(Collection<?> c) {
		return annotations.removeAll(c);
	}

	public boolean contains(Object o) {
		return annotations.contains(o);
	}

	public boolean add(BioAnnotation e) {
		return annotations.add(e);
	}

	public boolean remove(Object o) {
		return annotations.remove(o);
	}

	public boolean containsAll(Collection<?> c) {
		return annotations.containsAll(c);
	}

	public boolean addAll(Collection<? extends BioAnnotation> c) {
		return annotations.addAll(c);
	}

	public BioCollection<BioAnnotation> getView() {
		return annotations.getView();
	}
	
}
