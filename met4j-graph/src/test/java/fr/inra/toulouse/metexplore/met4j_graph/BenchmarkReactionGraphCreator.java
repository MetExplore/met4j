package fr.inra.toulouse.metexplore.met4j_graph;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Test;

import com.google.common.base.CaseFormat;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntityParticipant;
import fr.inra.toulouse.metexplore.met4j_graph.core.reaction.ReactionGraph;
import fr.inra.toulouse.metexplore.met4j_graph.io.ReactionGraphCreator;

public class BenchmarkReactionGraphCreator {

	/** The graph. */
	public BioNetwork bn;
	
	/** The path. */
	public ReactionGraphCreator builder;
	
	/** The nodes. */
	public BioPhysicalEntity a,b,c,d,e,f,h;
	
	/** The edges. */
	public BioChemicalReaction r1,r2,r3,r4,r5,r6;	

	public BenchmarkReactionGraphCreator() {
		this.init();
	}
	  long start = System.nanoTime();
      
	public void init(){
		bn = new BioNetwork();
		a = new BioPhysicalEntity("a"); bn.addPhysicalEntity(a);
		b = new BioPhysicalEntity("b"); bn.addPhysicalEntity(b);
		c = new BioPhysicalEntity("c"); bn.addPhysicalEntity(c);
		d = new BioPhysicalEntity("d"); bn.addPhysicalEntity(d);
		e = new BioPhysicalEntity("e"); bn.addPhysicalEntity(e);
		f = new BioPhysicalEntity("f"); bn.addPhysicalEntity(f);
		h = new BioPhysicalEntity("h"); bn.addPhysicalEntity(h);
		r1 = new BioChemicalReaction("r1");
		r1.addLeftParticipant(new BioPhysicalEntityParticipant(a));
		r1.addRightParticipant(new BioPhysicalEntityParticipant(b));
		r1.addRightParticipant(new BioPhysicalEntityParticipant(h));
		r2 = new BioChemicalReaction("r2");
		r2.addLeftParticipant(new BioPhysicalEntityParticipant(b));
		r2.addLeftParticipant(new BioPhysicalEntityParticipant(d));
		r2.addLeftParticipant(new BioPhysicalEntityParticipant(h));
		r2.addRightParticipant(new BioPhysicalEntityParticipant(c));
		r3 = new BioChemicalReaction("r3");
		r3.addLeftParticipant(new BioPhysicalEntityParticipant(e));
		r3.addRightParticipant(new BioPhysicalEntityParticipant(b));
		r3.setReversibility(true);
		r4 = new BioChemicalReaction("r4");
		r4.addLeftParticipant(new BioPhysicalEntityParticipant(e));
		r4.addRightParticipant(new BioPhysicalEntityParticipant(c));
		r4.addRightParticipant(new BioPhysicalEntityParticipant(f));
		r5 = new BioChemicalReaction("r5");
		r5.addLeftParticipant(new BioPhysicalEntityParticipant(a));
		r5.addRightParticipant(new BioPhysicalEntityParticipant(e));
		r5.setReversibility(true);
		r6 = new BioChemicalReaction("r6");
		r6.addLeftParticipant(new BioPhysicalEntityParticipant(d));
		r6.addRightParticipant(new BioPhysicalEntityParticipant(f));
		bn.addBiochemicalReaction(r1);
		bn.addBiochemicalReaction(r2);
		bn.addBiochemicalReaction(r3);
		bn.addBiochemicalReaction(r4);
		bn.addBiochemicalReaction(r5);
		bn.addBiochemicalReaction(r6);
		
		builder = new ReactionGraphCreator(bn);
	}
	
	public static void main(String[] args) {
		BenchmarkReactionGraphCreator bm = new BenchmarkReactionGraphCreator();
		ReactionGraphCreator builder = bm.builder;
		ReactionGraph r;
		int i = 10000;
		long start;
		long time;
		DescriptiveStatistics meanR0 = new DescriptiveStatistics();
		DescriptiveStatistics meanR1 = new DescriptiveStatistics();
		DescriptiveStatistics meanR2= new DescriptiveStatistics();
		DescriptiveStatistics meanR3 = new DescriptiveStatistics();
		DescriptiveStatistics meanR4 = new DescriptiveStatistics();
		DescriptiveStatistics meanR5 = new DescriptiveStatistics();
		DescriptiveStatistics meanR6 = new DescriptiveStatistics();
		
		for(int j = 0; j<=i; j++){
			List<Integer> order = new ArrayList<Integer>();
			order.add(0);order.add(1);order.add(2);order.add(3);order.add(4);order.add(5);order.add(6);
			Collections.shuffle(order);
			for(Integer x : order){
				switch (x.intValue()) {
				case 0:
					start = System.nanoTime();
						r = builder.getReactionGraph0();
			        time = System.nanoTime() - start;
			        meanR0.addValue(time);
					break;
				case 1:
					start = System.nanoTime();
						r = builder.getReactionGraph1();
			        time = System.nanoTime() - start;
			        meanR1.addValue(time);
					break;
				case 2:
					start = System.nanoTime();
						r = builder.getReactionGraph2();
			        time = System.nanoTime() - start;
			        meanR2.addValue(time);
					break;
				case 3:
					start = System.nanoTime();
						r = builder.getReactionGraph3();
			        time = System.nanoTime() - start;
			        meanR3.addValue(time);
					break;
				case 4:
					start = System.nanoTime();
						r = builder.getReactionGraph4();
			        time = System.nanoTime() - start;
			        meanR4.addValue(time);
					break;
				case 5:
					start = System.nanoTime();
						r = builder.getReactionGraph5();
			        time = System.nanoTime() - start;
			        meanR5.addValue(time);
				break;
				case 6:
					start = System.nanoTime();
						r = builder.getReactionGraph5();
			        time = System.nanoTime() - start;
			        meanR6.addValue(time);
				break;
				default:
					break;
				}
			}
		}
		
		System.out.println("getReactionGraph0 : "+meanR0.getPercentile(50)+" - "+meanR0.getMean()+"ns (min:"+meanR0.getMin()+"ns, max:"+meanR0.getMax()+"ns)");
		System.out.println("getReactionGraph1 : "+meanR1.getPercentile(50)+" - "+meanR1.getMean()+"ns (min:"+meanR1.getMin()+"ns, max:"+meanR1.getMax()+"ns)");
		System.out.println("getReactionGraph2 : "+meanR2.getPercentile(50)+" - "+meanR2.getMean()+"ns (min:"+meanR2.getMin()+"ns, max:"+meanR2.getMax()+"ns)");
		System.out.println("getReactionGraph3 : "+meanR3.getPercentile(50)+" - "+meanR3.getMean()+"ns (min:"+meanR3.getMin()+"ns, max:"+meanR3.getMax()+"ns)");
		System.out.println("getReactionGraph4 : "+meanR4.getPercentile(50)+" - "+meanR4.getMean()+"ns (min:"+meanR4.getMin()+"ns, max:"+meanR4.getMax()+"ns)");
		System.out.println("getReactionGraph5 : "+meanR5.getPercentile(50)+" - "+meanR5.getMean()+"ns (min:"+meanR5.getMin()+"ns, max:"+meanR5.getMax()+"ns)");
		System.out.println("getReactionGraph5 : "+meanR6.getPercentile(50)+" - "+meanR6.getMean()+"ns (min:"+meanR6.getMin()+"ns, max:"+meanR6.getMax()+"ns)");
	}
}
