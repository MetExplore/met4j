package fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Select nodes according to their neighborhood status, as sinks (no successors) or sources (no predecessor).
 * Metabolic sources and sinks are useful for identifying medium requirements and metabolic capability. However,
 * for metabolic networks, relevant sources and sinks may be relevant only if present in an extracellular compartment,
 * and by transposing neighborhood status of their intracellular counterparts. This class provides means to account for such specificities.
 */
public class SourcesAndSinks {

    boolean useInternal = false;
    boolean keepIsolated = false;
    boolean source = false;
    boolean notsource = false;
    boolean sink = false;
    boolean notsink = false;
    boolean notany = false;

    boolean useBorensteinAlgorithm = false;

    private BioCollection<BioMetabolite> candidates;
    private CompoundGraph graph;

    /**
     * Setting to use if sources & sinks are relevant only if accessible from extracellular compartments.
     * Since "real" sinks and sources in intracellular compartment(s) may be involved in transport/exchange reactions
     * reversible by default, thus not allowing extracellular source or sink, an option allows to take
     * the degree (minus extracellular neighbors) of intracellular counterparts.
     *
     * @param externals set of compounds in compartment of interest.
     * @param useInternalsForDegree select nodes according to the degree of their counterparts in other compartments.
     * @return
     */
    public SourcesAndSinks fromExternalCompartment(BioCollection<BioMetabolite> externals, boolean useInternalsForDegree){
        this.useInternal = useInternalsForDegree;
        this.candidates=externals;
        return this;
    }

    /**
     * Select all compound with no producing reaction for export
     * @param select
     * @return
     */
    public SourcesAndSinks selectSources(boolean select) {
        this.source = select;
        return this;
    }

    /**
     * Select all compound with no consuming reaction for export
     * @param select
     * @return
     */
    public SourcesAndSinks selectSinks(boolean select) {
        this.sink = select;
        return this;
    }

    /**
     * Select all compound with at least one consuming reaction for export
     * @param select
     * @return
     */
    public SourcesAndSinks selectNonSinks(boolean select) {
        this.notsink = select;
        return this;
    }

    /**
     * Define seeds and targets (outputs) using the Borenstein et al. algorithm (see Borenstein et al. 2008 Large-scale reconstruction and phylogenetic analysis of metabolic environments https://doi.org/10.1073/pnas.0806162105)
     * This method consider strongly connected components rather than individual nodes, thus, members of cycles can be considered as seed.
     * A sink from an external compartment can however be connected to a non sink internal counterpart, thus highlighting what could end up in the external compartment rather than what must be exported.
     * This option will ignore the useInternalsForDegree option.
     * @param use
     * @return
     */
    public SourcesAndSinks useBorensteinAlgorithm(boolean use) {
        this.useBorensteinAlgorithm = use;
        return this;
    }

    /**
     * Select all compound with at least one producing reaction for export
     * @param select
     * @return
     */
    public SourcesAndSinks selectNonSources(boolean select) {
        this.notsource = select;
        return this;
    }

    /**
     * Select all compounds with at least one producing reaction and one consuming reaction for export
     * @param select
     * @return
     */
    public SourcesAndSinks selectIntermediaries(boolean select) {
        this.notany = select;
        return this;
    }

    /**
     * Consider isolated count both as sinks and sources. If set to false, isolated compounds are ignored.
     * Default set to false;
     * @param select
     * @return
     */
    public SourcesAndSinks keepIsolated(boolean select) {
        this.keepIsolated = select;
        return this;
    }

    public SourcesAndSinks(CompoundGraph graph){
        this.graph=graph;
    }

    /**
     * get all compounds that match the selected neighborhood status
     * @return selected compounds
     */
    public BioCollection<BioMetabolite> getSelection(){
        if(!(source||sink||notsink||notsource||notany)){
            System.err.println("[Warn] Sources and Sinks: no type selected, will return empty list");
            return new BioCollection<>();
        }
        if(candidates==null || candidates.isEmpty()) candidates = new BioCollection<>(graph.vertexSet());

        if((source&&notsource)||(sink&&notsink)){
            System.err.println("[Warn] Sources and Sinks: complementary types selected (such as source and not sources), will return all evaluated nodes");
            return candidates;
        }

        if(useBorensteinAlgorithm){
            return computeBorensteinAlgorithm();
        }else{
            return compute();
        }
    }


    private BioCollection<BioMetabolite> compute(){

        //Evaluate Candidates
        BioCollection<BioMetabolite> res = new BioCollection<>();
        for(BioMetabolite v : candidates){
            int inDegree = useInternal ? getInternalDegree(graph,v, candidates, false) : graph.inDegreeOf(v);
            int outDegree = useInternal ? getInternalDegree(graph,v, candidates, true) : graph.outDegreeOf(v);
            //ignore external only
            if(inDegree!=-1 && outDegree!=-1 &&
                    //ignore isolated
                    !(!keepIsolated && (inDegree+outDegree==0))){
                if(inDegree==0){
                    if(source) res.add(v);
                } else if (notsource) {
                    res.add(v);
                }

                if(outDegree==0){
                    if(sink) res.add(v);
                } else if (notsink) {
                    res.add(v);
                }

                if(notany && outDegree>0 && inDegree>0){
                    res.add(v);
                }
            }
        }

        return res;
    }

    private BioCollection<BioMetabolite> computeBorensteinAlgorithm(){

        //Evaluate Candidates
        BioCollection<BioMetabolite> res = new BioCollection<>();
        KosarajuStrongConnectivityInspector<BioMetabolite, ReactionEdge> sccComputor = new KosarajuStrongConnectivityInspector<>(graph);
        List<Set<BioMetabolite>> scc = sccComputor.stronglyConnectedSets();

        for(Set<BioMetabolite> cc : scc){
            Set<BioMetabolite> sccCandidates = new HashSet<>(cc);
            sccCandidates.retainAll(candidates);
            if(!sccCandidates.isEmpty()) {
                int inDegree = 0;
                int outDegree = 0;
                //Same as considering a condensation graph, where a whole strongly connected component is condensed into a single node
                //This will sum up, for each component's element, all successors and predecessors outside the component
                for (BioMetabolite v : cc) {
                    Set<BioMetabolite> predecessors = graph.predecessorListOf(v);
                    predecessors.removeAll(cc);
                    inDegree += predecessors.size();
                    Set<BioMetabolite> successors = graph.successorListOf(v);
                    successors.removeAll(cc);
                    outDegree += successors.size();
                }
                if (!(!keepIsolated && (inDegree + outDegree == 0) && cc.size()==1)) {
                    if (inDegree == 0) {
                        if (source) res.addAll(sccCandidates);
                    } else if (notsource) {
                        res.addAll(sccCandidates);
                    }

                    if (outDegree == 0) {
                        if (sink) res.addAll(sccCandidates);
                    } else if (notsink) {
                        res.addAll(sccCandidates);
                    }

                    if (notany && outDegree > 0 && inDegree > 0) {
                        res.addAll(sccCandidates);
                    }
                }
            }
        }
        return res;
    }


    private int getInternalDegree(CompoundGraph g, BioMetabolite v, BioCollection<BioMetabolite> externalComp , Boolean out){
        //get internal neighbor counterpart
        Set<BioMetabolite> internal = new HashSet<>();
        internal.addAll(g.neighborListOf(v));
        //remove all neighbors that are not internal
        internal.removeAll(externalComp);

        //ignore compounds with no internal counterparts
        if(internal.isEmpty()) return -1;

        //compute degree of internal counterpart
        int degree = 0;
        for(BioMetabolite neighbor : internal){
            if(out){
                Set<BioMetabolite> n = g.successorListOf(neighbor);
                n.removeAll(externalComp);
                degree+= n.size();
            }else{
                Set<BioMetabolite> n = g.predecessorListOf(neighbor);
                n.removeAll(externalComp);
                degree+= n.size();
            }
        }
        return degree;
    }
}
