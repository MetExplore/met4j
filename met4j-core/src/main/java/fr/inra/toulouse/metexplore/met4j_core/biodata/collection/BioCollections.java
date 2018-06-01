package fr.inra.toulouse.metexplore.met4j_core.biodata.collection;


import java.util.HashSet;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;

public class BioCollections{
	
	public static <E extends BioEntity> BioCollection<E> interesct(BioCollection<E>... collections){
		
		BioCollection<E> interesct = new BioCollection<E>(collections[0]);
		for(int i=1; i<collections.length; i++){
			interesct.retainAll(collections[i]);
			if(interesct.isEmpty()) return interesct;
		}
		return interesct;
	}
	
	public static <E extends BioEntity> BioCollection<E> union(BioCollection<E>... collections){
		
		HashSet<E> union = new HashSet<E>();
		for(BioCollection<E> collection : collections){
			union.addAll(collection);
		}
		return new BioCollection<E>(union);
	}

	
//	public static void main(String[] args) {
//		BioCollection<BioMetabolite> c1 = new BioCollection<BioMetabolite>();
//		BioCollection<BioMetabolite> c2 = new BioCollection<BioMetabolite>();
//		BioCollection<BioMetabolite> c3 = new BioCollection<BioMetabolite>();
//		
//		BioMetabolite m1 = new BioMetabolite("1");
//		BioMetabolite m2 = new BioMetabolite("2");
//		BioMetabolite m3 = new BioMetabolite("3");
//		BioMetabolite m4 = new BioMetabolite("4");
//		BioMetabolite m5 = new BioMetabolite("5");
//		BioMetabolite m6 = new BioMetabolite("5");
//		
//		c1.add(m1);
//		c2.add(m1);
//		c3.add(m1);
//		
//		c1.add(m5);
//		c2.add(m5);
//		c3.add(m5);
//		
//		c1.add(m2);
//		c2.add(m3);
//		c3.add(m4);
//		
//		System.out.println("interesct");
//		BioCollection<BioMetabolite> c4 = BioCollections.union(c1,c2,c3);
//		for(BioMetabolite m : c4){
//			System.out.println(m.getId());
//		}
//		
//		System.out.println("union");
//		BioCollection<BioMetabolite> c5 = BioCollections.interesct(c1,c2,c3);
//		for(BioMetabolite m : c5){
//			System.out.println(m.getId());
//		}
//		
//		c3.remove(m5);
//		c3.add(m6);
//		try {
//			c5 = BioCollections.union(c1,c2,c3);
//			for(BioMetabolite m : c5){
//				System.out.println(m.getId());
//			}
//		} catch (IllegalArgumentException e) {
//			System.out.println("bad id");
//		}
//		
//		BioCollection<BioMetabolite> c6 = BioCollections.union(c1);
//		for(BioMetabolite m : c6){
//			System.out.println(m.getId());
//		}
//		
//	}
	
}
