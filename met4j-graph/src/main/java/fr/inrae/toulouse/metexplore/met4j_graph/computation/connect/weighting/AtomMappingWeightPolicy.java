package fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AtomMappingWeightPolicy extends WeightingPolicy<BioMetabolite, ReactionEdge, CompoundGraph> {
    private Map<BioMetabolite,Map<BioMetabolite, Integer>> conservedCarbons;
    private WeightsFromFile importer;
    Boolean removeNotFound = false;
    Boolean binarize = false;
    Boolean removeNoCC = false;

    public AtomMappingWeightPolicy removeEdgeWithoutMapping() {
        this.removeNotFound = true;
        return this;
    }
    public AtomMappingWeightPolicy removeEdgesWithoutConservedCarbon() {
        this.removeNoCC = true;
        return this;
    }
    public AtomMappingWeightPolicy binarize() {
        this.binarize = true;
        return this;
    }

    public AtomMappingWeightPolicy fromAAMRxnSmiles(Map<BioMetabolite, Map<BioMetabolite, String>> transitionRxnSmiles){
        this.conservedCarbons = new HashMap<>();
        for(Map.Entry<BioMetabolite,Map<BioMetabolite, String>> e : transitionRxnSmiles.entrySet()){
            BioMetabolite key = e.getKey();
            Map<BioMetabolite, Integer> value = e.getValue().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e2 -> parseRxnSmile(e2.getValue())));
            conservedCarbons.put(key,value);
        }
        return this;
    }

    private int parseRxnSmile(String AAMSmilesRxn) {

        if(AAMSmilesRxn.contains("\\.")) throw new IllegalArgumentException(AAMSmilesRxn+" not valid, only two compounds must be mapped");
        if(!AAMSmilesRxn.matches(".*\\[[A-Za-z2-9]+:\\d+\\].*")) throw new IllegalArgumentException(AAMSmilesRxn+" not valid, SMILES must contain atom mapping");

        //separate reactant list from products list
        String[] split = AAMSmilesRxn.split(">>");
        if(split.length!=2) throw new IllegalArgumentException(AAMSmilesRxn+" not valid, not a reaction SMILES");

        ArrayList<String> atomLabels1 = new ArrayList<>();
        ArrayList<String> atomLabels2 = new ArrayList<>();
        //separate atoms
        Pattern p = Pattern.compile("\\[C[^lardonsfume]?H?\\d*[+-]?:(\\d+)\\]");
        Matcher m1 = p.matcher(split[0]);
        while(m1.find()){
            atomLabels1.add(m1.group(1));
        }
        Matcher m2 = p.matcher(split[1]);
        while(m2.find()){
            atomLabels2.add(m2.group(1));
        }

        atomLabels1.retainAll(atomLabels2);
        return atomLabels1.size();
    }

    public AtomMappingWeightPolicy fromConservedCarbonIndexes(Map<BioMetabolite,Map<BioMetabolite, Collection<Integer>>> transitionConservedCarbonIndex){
        this.conservedCarbons = new HashMap<>();
        for(Map.Entry<BioMetabolite,Map<BioMetabolite, Collection<Integer>>> e : transitionConservedCarbonIndex.entrySet()){
            BioMetabolite key = e.getKey();
            Map<BioMetabolite, Integer> value = e.getValue().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e2 -> e2.getValue().size()));
            conservedCarbons.put(key,value);
        }
        return this;
    }

    public AtomMappingWeightPolicy fromNumberOfConservedCarbons(Map<BioMetabolite,Map<BioMetabolite, Integer>> transitionNumberOfConservedCarbon){
        this.conservedCarbons = transitionNumberOfConservedCarbon;
        return this;
    }

    public AtomMappingWeightPolicy fromNumberOfConservedCarbons(WeightsFromFile importer){
        this.conservedCarbons = new HashMap<>();
        this.importer = importer;
        return this;
    }

    public AtomMappingWeightPolicy fromNumberOfConservedCarbons(String GSAMoutputFile){
        return this.fromNumberOfConservedCarbons(new WeightsFromFile(GSAMoutputFile, false)
                .weightCol(8)
                .edgeLabelCol(6)
                .sourceCol(0)
                .targetCol(3)
                .sep("\t"));
    }

    @Override
    public void setWeight(CompoundGraph compoundGraph) {
        if(conservedCarbons==null) throw new IllegalArgumentException("an atom mapping must be provided");
        if(importer !=null) importer.setWeight(compoundGraph);

        ArrayList<ReactionEdge> toRemove = new ArrayList<>();
        for(ReactionEdge e : compoundGraph.edgeSet()){
            Integer cc = null;
            if(importer !=null){
                cc=(int) compoundGraph.getEdgeWeight(e);
            }else{
                Map<BioMetabolite,Integer> mapping = conservedCarbons.get(e.getV1());
                if(mapping!=null) cc = conservedCarbons.get(e.getV1()).get(e.getV2());
            }

            if(cc != null){
                if(binarize && cc>0) cc=1 ;
                if(removeNoCC && cc==0) toRemove.add(e);
                compoundGraph.setEdgeWeight(e, Double.valueOf(cc));
            }else{
                compoundGraph.setEdgeWeight(e, Double.NaN);
                if(removeNotFound) toRemove.add(e);
            }

        }
        if(removeNotFound || removeNoCC) compoundGraph.removeAllEdges(toRemove);
    }
}
