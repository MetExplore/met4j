package fr.inrae.toulouse.metexplore.met4j_io.tabulated.attributes;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <p>Abstract AbstractSetMultiAttributesFromFile class.</p>
 *
 * allow importing attributes where multiple values can be affected to a single entity
 * @author lcottret
 */
public abstract class AbstractSetMultiAttributesFromFile extends AbstractSetAttributesFromFile{


    /**
     * <p>Constructor for AbstractSetMultiAttributesFromFilez.</p>
     *
     * @param colId      a int.
     * @param colAttr    : number of the attribute column
     * @param bn         : {@link BioNetwork}
     * @param fileIn     : tabulated file containing the ids and the attributes
     * @param c          : comment character
     * @param nSkip      a int. Number of lines to skip
     * @param entityType a {@link EntityType}
     * @param p          a {@link Boolean} object : To match the objects in the sbml file, adds the prefix R_ to reactions and M_ to metabolites
     * @param s          a {@link Boolean} object : To match the objects in the sbml file, adds the suffix _comparmentID to metabolite
     */
    public AbstractSetMultiAttributesFromFile(int colId, int colAttr, BioNetwork bn, String fileIn, String c, int nSkip, EntityType entityType, Boolean p, Boolean s) {
        super(colId, colAttr, bn, fileIn, c, nSkip, entityType, p, s);
        allowDuplicates();
    }

    protected HashMap<String, Set<String>> idMultiAttributeMap= new HashMap<>();

    @Override
    protected void addValue(String id, String attribute){
        this.getIdAttributeMap().put(id, attribute);
        this.getIdMultiAttributeMap().computeIfAbsent(id, k -> new LinkedHashSet<>())
                .add(attribute);
    }

    /**
     * <p>Getter for the field <code>idAttributeMap</code>.</p>
     *
     * @return the idMultiAttributeMap
     */
    public HashMap<String, Set<String>> getIdMultiAttributeMap() {
        if(idMultiAttributeMap == null) {
            idMultiAttributeMap = new HashMap<>();
        }
        return idMultiAttributeMap;
    }
}

