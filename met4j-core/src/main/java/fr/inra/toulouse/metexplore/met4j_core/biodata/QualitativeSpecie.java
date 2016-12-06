/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_core.biodata;

public class QualitativeSpecie extends BioEntity {
	
	private int initLevel;
	private int maxLevel;

	public QualitativeSpecie(String id) {
		super(id);
	}
	
	public QualitativeSpecie(String id,String name) {
		super(id,name);
	}
	
	public QualitativeSpecie(String id,String name,int init) {
		super(id,name);
		this.setInitLevel(init);
	}
	
	public QualitativeSpecie(String id,String name,int init,int max) {
		super(id,name);
		this.setInitLevel(init);
		this.setMaxLevel(max);
	}

	public int getInitLevel() {
		return initLevel;
	}

	public void setInitLevel(int level) {
		this.initLevel = level;
	}
	public int getMaxLevel() {
		return maxLevel;
	}
	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

}
