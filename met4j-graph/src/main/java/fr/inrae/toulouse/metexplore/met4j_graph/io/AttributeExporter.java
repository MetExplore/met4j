package fr.inrae.toulouse.metexplore.met4j_graph.io;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.reaction.CompoundEdge;
import lombok.Getter;
import lombok.Setter;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * This class is used to define the attributes that will be exported with the graph.
 */
public class AttributeExporter {

    private Boolean exportMass = false; // Boolean variable to check if the COMPOUND mass is exported
    private Boolean exportFormula = false; // Boolean variable to check if the COMPOUND formula is exported
    private Boolean exportEC = false; // Boolean variable to check if the REACTION EC number is exported
    private Boolean exportReversible = false; // Boolean variable to check if the REACTION reversibility is exported
    private Boolean exportName = false; // Boolean variable to check if the name is exported
    private Boolean exportType = false; // Boolean variable to check if the bioentity type is exported
    private Boolean exportTransport = false; // Boolean variable to check if the REACTION transport flag is exported
    private Boolean exportCompartment = false; // Boolean variable to check if the COMPARTMENT is exported. Requires original BioNetwork.
    private TreeMap<String,Function<BioEntity,Object>> nodeExtraAtt = new TreeMap<>(); // TreeMap to store the node attributes
    private TreeMap<String,Function<Edge<?extends BioEntity>,Object>> edgeExtraAtt = new TreeMap<>(); // TreeMap to store the edge attributes
    private BioNetwork network = null; // The BioNetwork from which some attributes are exported

    /**
     * Gets the list of node attribute labels
     * @return the list of node attribute labels
     */
    public List<String> getNodeAttributeLabels(){
        return new ArrayList<>(nodeExtraAtt.keySet());
    }

    /**
     * Gets the list of edge attribute labels
     * @return the list of edge attribute labels
     */
    public List<String> getEdgeAttributeLabels(){
        return new ArrayList<>(edgeExtraAtt.keySet());
    }

    /**
     *  Exports compounds' mass
      */
    public AttributeExporter exportMass() {
        this.exportMass = true;
        return this;
    }
    /**
     *  Exports compounds' chemical formula
     */
    public AttributeExporter exportFormula() {
        this.exportFormula = true;
        return this;
    }
    /**
     *  Exports reactions' EC number
     */
    public AttributeExporter exportEC() {
        this.exportEC = true;
        return this;
    }
    /**
     *  Exports reactions' reversibility
     */
    public AttributeExporter exportReversible() {
        this.exportReversible = true;
        return this;
    }
    /**
     *  Exports reactions transport flag
     */
    public AttributeExporter exportTransportFlag() {
        this.exportTransport = true;
        return this;
    }
    /**
     *  Exports bio-entities' name
     */
    public AttributeExporter exportName() {
        this.exportName = true;
        return this;
    }
    /**
     *  Exports bio-entities' type
     */
    public AttributeExporter exportType() {
        this.exportType = true;
        return this;
    }
    /**
     *  Exports bio-entities' compartment
     */
    public AttributeExporter exportCompartment() {
        this.exportCompartment = true;
        return this;
    }

    /**
     * Exports a node attribute
     * @param attributeName the name of the attribute
     * @param values the function that computes the attribute value
     * @return an instance of the AttributeExporter
     */
    public AttributeExporter exportNodeAttribute(String attributeName, Function<BioEntity,Object> values) {
        nodeExtraAtt.put(attributeName, values);
        return this;
    }
    /**
     * Exports an edge attribute
     * @param attributeName the name of the attribute
     * @param values the function that computes the attribute value
     * @return an instance of the AttributeExporter
     */
    public AttributeExporter exportEdgeAttribute(String attributeName, Function<Edge<?extends BioEntity>,Object> values) {
        edgeExtraAtt.put(attributeName, values);
        return this;
    }


    public AttributeExporter() {
        // Default constructor
    }
    public AttributeExporter(BioNetwork network){
        this.network=network;
    }

    /*
     * Creates an attribute object from a value
     */
    private Attribute createAttribute(Object value){
        if(value.getClass().isPrimitive()){
            if(value instanceof Boolean) return DefaultAttribute.createAttribute((Boolean) value);
            else if(value instanceof Double) return DefaultAttribute.createAttribute((Double)value);
            else if(value instanceof Float) return DefaultAttribute.createAttribute((Float)value);
            else if(value instanceof Integer) return DefaultAttribute.createAttribute((Integer)value);
            else if(value instanceof Long) return DefaultAttribute.createAttribute((Long)value);
            else return DefaultAttribute.createAttribute(value.toString());
        }else{
            return DefaultAttribute.createAttribute(value.toString());
        }
    }
    /*
     * Initializes the map to store the node attributes
     */
    private Map<String, Attribute> initNodeAtt(BioEntity v){
        Map<String, Attribute> map = new TreeMap<>();
        for(Map.Entry<String,Function<BioEntity,Object>> entry : nodeExtraAtt.entrySet()){
            try{
                Object value = entry.getValue().apply(v);
                if(value!=null) map.put(entry.getKey(), createAttribute(value));
            }catch (Exception e){
                System.err.println("Error while computing attribute "+entry.getKey()+" for node "+v.getId());
                e.printStackTrace();
            }
        }
        return map;
    }
    /*
     * Initializes the map to store the edge attributes
     */
    private Map<String, Attribute> initEdgeAtt(Edge<? extends BioEntity> e){
        Map<String, Attribute> map = new TreeMap<>();
        for(Map.Entry<String,Function<Edge<? extends BioEntity>,Object>> entry : edgeExtraAtt.entrySet()){
            try{
                Object value = entry.getValue().apply(e);
                if(value!=null) map.put(entry.getKey(), createAttribute(value));
            }catch (Exception ex){
                System.err.println("Error while computing attribute "+entry.getKey()+" for edge "+e.getV1().getId()+"->"+e.getV2().getId());
                ex.printStackTrace();
            }
        }
        return map;
    }

    /**
     * Function to provide attributes for a compound node
     */
    @Setter
    @Getter
    public Function<BioMetabolite, Map<String, Attribute>> compoundAttProvider = (v -> {
        Map<String, Attribute> att = initNodeAtt(v);
        if(exportName && v.getName()!=null) att.put("Name", DefaultAttribute.createAttribute(v.getName()));
        if(exportType) att.put("Type",DefaultAttribute.createAttribute("Compound"));
        if(exportFormula && v.getChemicalFormula()!=null) att.put("Formula", DefaultAttribute.createAttribute(v.getChemicalFormula()));
        if(exportMass && v.getMolecularWeight()!=null) att.put("Mass", DefaultAttribute.createAttribute(v.getMolecularWeight()));
        if(exportCompartment && network!=null){
            att.put("Compartment", DefaultAttribute.createAttribute(
                    network.getCompartmentsOf(v).stream().map(BioEntity::getId).reduce((a,b) -> a + "," + b).orElse("None"))
            );
        } else if (exportCompartment) {
            System.err.println("Warning: Compartment export is enabled but no BioNetwork is provided. Compartment information will not be exported.");
        }
        return att;
    });

    /**
     * Function to provide attributes for a reaction node
     */
    @Setter
    @Getter
    public Function<BioReaction, Map<String, Attribute>> reactionAttProvider = (v -> {
        Map<String, Attribute> att = initNodeAtt(v);
        if(exportName && v.getName()!=null) att.put("Name", DefaultAttribute.createAttribute(v.getName()));
        if(exportType) att.put("Type",DefaultAttribute.createAttribute("Reaction"));
        if(exportReversible && v.isReversible()!=null) att.put("Reversible", DefaultAttribute.createAttribute(v.isReversible()));
        if(exportEC && v.getEcNumber()!=null) att.put("EC", DefaultAttribute.createAttribute(v.getEcNumber()));
        if(exportTransport && v.isTransportReaction()!=null) att.put("Transport", DefaultAttribute.createAttribute(v.isTransportReaction()));
        return att;
    });

    /**
     * Function to provide attributes for a bipartite node
     */
    @Setter
    @Getter
    public Function<BioEntity, Map<String, Attribute>> bipNodeAttProvider = (v -> {
        Map<String, Attribute> att = initNodeAtt(v);
        if (v instanceof BioMetabolite) return compoundAttProvider.apply((BioMetabolite) v);
        if (v instanceof BioReaction) return reactionAttProvider.apply((BioReaction) v);
        return att;
    });

    /**
     * Function to provide a label for a reaction edge (compound graph)
     */
    @Setter
    @Getter
    public Function<ReactionEdge, String> reactionEdgeLabelProvider = (e -> {
        if(e.getReaction()!=null) return e.getReaction().getId(); return "NA";
    });

    /**
     * Function to provide a label for a compound edge (reaction graph)
     */
    @Setter
    @Getter
    public Function<CompoundEdge, String> compoundEdgeLabelProvider = (e -> {
        if(e.getCompound()!=null) return e.getCompound().getId(); return "NA";
    });

    /**
     * Function to provide a label for an edge in a bipartite graph
     */
    @Setter
    @Getter
    public Function<BipartiteEdge, String> bipEdgeLabelProvider = (e -> e.getV1() instanceof BioReaction ? "product" : "substrate of");

    /**
     * Function to provide attributes for a reaction edge (compound graph)
     */
    @Setter
    @Getter
    public Function<ReactionEdge, Map<String, Attribute>> reactionEdgeAttProvider = (e -> {
        Map<String, Attribute> att  = initEdgeAtt(e);
        att.put("Name", DefaultAttribute.createAttribute(reactionEdgeLabelProvider.apply(e)));
        if(e.getReaction()!=null) att.putAll(reactionAttProvider.apply(e.getReaction()));
        return att;
    });

    /**
     * Function to provide attributes for a compound edge (reaction graph)
     */
    @Setter
    @Getter
    public Function<CompoundEdge, Map<String, Attribute>>compoundEdgeAttProvider = (e -> {
        Map<String, Attribute> att = initEdgeAtt(e);
        att.put("Name", DefaultAttribute.createAttribute(compoundEdgeLabelProvider.apply(e)));
        if(e.getCompound()!=null) att.putAll(compoundAttProvider.apply(e.getCompound()));
        return att;
    });

    /**
     * Function to provide attributes for an edge in a bipartite graph
     */
    @Setter
    @Getter
    public Function<BipartiteEdge, Map<String, Attribute>>bipEdgeAttProvider = (e -> {
        Map<String, Attribute> att = initEdgeAtt(e);
        att.put("Name", DefaultAttribute.createAttribute(bipEdgeLabelProvider.apply(e)));
        return att;
    });


    /**
     * Function to provide attributes for a generic node
     */
    @Setter
    @Getter
    public Function<BioEntity, Map<String, Attribute>> defaultNodeAttProvider =
         (v -> {
            Map<String, Attribute> att = initNodeAtt(v);
            if(v.getName()!=null) att.put("Name", DefaultAttribute.createAttribute(v.getName()));
            att.put("Type", DefaultAttribute.createAttribute(v.getClass().getSimpleName()));
            return att;
        });


    /**
     * Function to provide attributes for a generic edge
     */
    @Setter
    @Getter
    public Function<Edge<BioEntity>, Map<String, Attribute>> defaultEdgeAttProvider =
        (e -> {
            Map<String, Attribute> att = initEdgeAtt(e);
            att.put("Name", DefaultAttribute.createAttribute(e.toString()));
            return att;
        });

    /**
     * Exports the default attributes (name and type for bioentity as nodes and/or edge attribute, and reversibility for reactions)
     * @return an instance of the AttributeExporter
     */
    public static AttributeExporter minimal(){
        return new AttributeExporter().exportName().exportType().exportReversible();
    }

    /**
     * Exports all attributes from model (name, type, reversibility, transport flag, compartment, mass, formula, EC number)
     * @return an instance of the AttributeExporter
     */
    public static AttributeExporter full(BioNetwork network){
        return new AttributeExporter(network)
                .exportName()
                .exportType()
                .exportReversible()
                .exportCompartment()
                .exportEC()
                .exportFormula()
                .exportMass()
                .exportTransportFlag();
    }

}
