
package fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents the full gene association as they are described in SBML
 * files. </br>
 * </br>
 * This class consists of a list of {@link GeneSet}s. Each of them being one
 * possible "AND" gene association that can activate a given
 * {@link BioReaction}.
 * 
 * 
 * @author Benjamin mainly modified by LC
 * @since 3.0
 * 
 */
public class GeneAssociation implements Set<GeneSet> {

	public ArrayList<GeneSet> geneSets;

	public GeneAssociation() {
		geneSets = new ArrayList<GeneSet>();
	}

	/**
	 * Returns the string representation of this GPR as a fully developed AND/OR
	 * logical expression.
	 */
	@Override
	public String toString() {

		return this.stream().map(x -> x.size() > 1 && this.size() > 1 ? "( " + x.toString() + " )" : x.toString())
				.sorted().collect(Collectors.joining(" OR "));

	}

	@Override
	public int size() {
		return geneSets.size();
	}

	@Override
	public boolean isEmpty() {
		return geneSets.isEmpty();
	}

	@Override
	public boolean contains(Object o) {

		for (GeneSet g : geneSets) {
			if (g.equals(o)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Iterator<GeneSet> iterator() {
		return geneSets.iterator();
	}

	@Override
	public Object[] toArray() {
		return geneSets.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return geneSets.toArray(a);
	}

	@Override
	public boolean add(GeneSet e) {

		for (GeneSet g : this.geneSets) {

			if (e.equals(g)) {
				return false;
			}
		}

		return geneSets.add(e);
	}

	@Override
	public boolean remove(Object o) {

		ArrayList<GeneSet> genes = new ArrayList<GeneSet>(this.geneSets);

		for (GeneSet g : genes) {
			if (o.equals(g)) {
				
				return geneSets.remove(g);
			}
		}

		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {

		for (GeneSet g : (GeneAssociation) c) {
			if (!this.contains(g)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean addAll(Collection<? extends GeneSet> c) {

		Boolean flag = false;

		for (GeneSet g : (GeneAssociation) c) {
			Boolean f = this.add(g);
			if (f) {
				flag = true;
			}
		}

		return flag;
	}

	@Override
	public boolean retainAll(Collection<?> c) {

		Boolean flag = false;

		HashSet<GeneSet> genes = new HashSet<GeneSet>(this.geneSets);

		for (GeneSet g : genes) {
			if (!c.contains(g)) {
				flag = true;
				this.remove(g);
			}
		}

		return flag;
	}

	@Override
	public boolean removeAll(Collection<?> c) {

		Boolean flag = false;

		HashSet<GeneSet> genes = new HashSet<GeneSet>(this.geneSets);

		for (GeneSet g : genes) {
			if (c.contains(g)) {
				flag = true;
				this.remove(g);
			}
		}

		return flag;
	}

	@Override
	public void clear() {
		geneSets.clear();
	}

}