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
package fr.inrae.toulouse.metexplore.met4j_graph.computation.transform;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Perform vertex contraction, replacing two or more nodes by a single one, keeping edges.
 * The neighborhood of the resulting node is therefore the union of the former nodes neighborhood
 * @author clement
 */
public class VertexContraction<V extends BioEntity,E extends Edge<V>, G extends BioGraph<V,E>>  {


    /**
     * Contract a set of vertices into a single vertex
     * @param vertexSet the set of nodes that will be contracted in a single node
     * @param v the substitute node that will replace the set
     * @param g the graph that will be modified
     */
    public static <V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> void contract(Set<V> vertexSet, V v, G g){
        vertexSet.remove(v);
        if(!g.containsVertex(v)) g.addVertex(v);

        for(V old : vertexSet){
            Set<E> oldEdges = g.edgesOf(old);
            for(E e : oldEdges){
                V v1 = e.getV1();
                V v2 = e.getV2();
                if(v1!=v && v2!=v){ //skip edges involving the substitute to avoid loop
                    if(v1==old){ //update edge sources
                        v1=v;
                    }else{
                        v2=v;
                    }
                    E e2 = g.createEdgeFromModel(v1, v2, e);
                    g.addEdge(v1, v2, e2);
                    g.setEdgeWeight(e2, g.getEdgeWeight(e));
                }
            }
            g.removeAllEdges(oldEdges);
        }
        g.removeAllVertices(vertexSet);

    }

    /**
     * Contract all nodes in graph from an aggregation function, which provide a common group id for each member of a set to contract
     * @param g the graph
     * @param l the aggregation function
     * @param <V> the node class
     * @param <E> the edge class
     * @param <G> the graph class
     * @return a graph with contracted vertices
     */
    public static <V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> G contractBy(G g, Function<V,String> l){
        G g2 = (G) g.clone();
        Map<String, List<V>> groupedNodes = g.vertexSet().stream().collect(Collectors.groupingBy(l));
        for(List<V> toContract : groupedNodes.values()){
            V v = toContract.get(0);
            toContract.remove(v);
            VertexContraction.contract(new HashSet<>(toContract), v, g2);
        }
        return g2;
    }

    /**
     * Remove compartment in a compound graph by contracting all nodes sharing a given attribute provided by the mapper
     * @param g the graph to decompartmentalize
     * @param m a Mapper function that return, for each compound, a String attribute used for grouping prior to merging
     * @return a graph with a single node for each set of nodes sharing the same attribute in g
     */
    public CompoundGraph decompartmentalize(CompoundGraph g, Mapper m){
        return VertexContraction.contractBy(g, m::commonField);
    }

    /**
     * Remove compartment in a compound graph by contracting all nodes sharing the same name
     * @param g the graph to decompartmentalize
     * @return a graph with a single node for each set of nodes sharing the same name in g
     */
    public CompoundGraph decompartmentalize(CompoundGraph g){
        return  decompartmentalize(g, BioMetabolite::getName);
    }

    /**
     * Provide, for a compound, a String attribute shared between compounds to merge
     */
    @FunctionalInterface
    public interface Mapper{
        /**
         * return a String attribute shared among compounds to merge
         * @param v a compound
         * @return a String attribute
         */
        String commonField(BioMetabolite v);
    }

    /**
     * Mapper used to decomparmentalize by merging compounds sharing the same name
     */
    public static class MapByName implements Mapper{
        @Override
        public String commonField(BioMetabolite v) {
            return v.getName();
        }
    }
    /**
     * Mapper used to decomparmentalize by merging compounds sharing the same inchi
     * WARNING: compound without InChI will all be merged into a single node.
     */
    public static class MapByInChI implements Mapper{
        @Override
        public String commonField(BioMetabolite v) {
            return v.getInchi();
        }
    }

    /**
     * Mapper used to decomparmentalize by merging compounds sharing part of their identifier.
     * SBML often use an identifer system with a compartment suffix, for example M_xxx_c where _c refer to a compartment.
     * This mapper needs a regex to extract the identifier subpart shared between compounds.
     */
    public static class MapByIdSubString implements Mapper{
        final String regex;
        public MapByIdSubString(String regex){
            this.regex=regex;
        }

        @Override
        public String commonField(BioMetabolite v) {
            String id = v.getId();
            Matcher m = Pattern.compile(regex).matcher(id);
            if(m.matches()) id=m.group(1);
            return id;
        }
    }

}
