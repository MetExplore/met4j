/*
 * Copyright INRAE (2020)
 *
 * contact-metexplore@inrae.fr
 *
 * This software is a computer program whose purpose is to [describe
 * functionalities and technical features of your software].
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "https://cecill.info/licences/Licence_CeCILL_V2.1-en.html".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 *
 */
package fr.inrae.toulouse.metexplore.met4j_graph.core.pathway;


import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;

/**
 * The Class PathwayGraph allow to build a directed graph representing connections between pathways in a bionetwork.
 * It first identify the sources and inner compounds of a pathways.
 * A source is a compounds which are consumed by a reaction of the pathways and not produced by any reaction of the pathways
 * An inner compounds is any compound that can be produced by a reaction of the pathways.
 * Two pathways are connected by an edge if they as an inner compound that is a source of the other.
 * Side compounds have to be defined to avoid erroneous connections.
 *
 * @author clement
 * @version $Id: $Id
 */
public class PathwayGraph extends BioGraph<BioPathway,PathwayGraphEdge> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8034821357586840871L;

	/**
	 * Instantiates a new pathway graph.
	 */
	public PathwayGraph() {
		super();
	}
	
	
	/** {@inheritDoc} */
	@Override
	public PathwayGraphEdge copyEdge(PathwayGraphEdge edge) {
		PathwayGraphEdge copy = new PathwayGraphEdge(edge.getV1(), edge.getV2(), edge.getConnectingCompounds());
		return copy;
	}
	
	@Override
	public BioPathway createVertex(String id) {
		return new BioPathway(id);
	}

	@Override
	public PathwayGraphEdge createEdge(BioPathway v1, BioPathway v2) {
		return new PathwayGraphEdge(v1,v2, new BioCollection<>());
	}

	@Override
	public PathwayGraphEdge createEdgeFromModel(BioPathway v1, BioPathway v2, PathwayGraphEdge edge) {
		return new PathwayGraphEdge(v1, v2, edge.getConnectingCompounds());
	}

	/** {@inheritDoc} */
	@Override
	public PathwayGraphEdge reverseEdge(PathwayGraphEdge edge) {
		PathwayGraphEdge reversed = new PathwayGraphEdge(edge.getV2(), edge.getV1(), edge.getConnectingCompounds());
		return reversed;
	}

}

