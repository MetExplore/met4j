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

package fr.inrae.toulouse.metexplore.met4j_io.tabulated.attributes;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * <p>Abstract AbstractSetAttributesFromFile class.</p>
 *
 * @author lcottret
 * @version $Id: $Id
 */
public abstract class AbstractSetAttributesFromFile {

    protected int colId = 0;
    protected int colAttr = 1;
    protected BioNetwork bn;
    protected String fileIn;
    protected String commentCharacter = "#";
    protected int nSkip = 0;
    protected EntityType entityType = EntityType.REACTION;
    protected Boolean addPrefix = false;
    protected Boolean addSuffix = false;

    protected Set<String> ids;

    protected Set<String> objectIds;

    protected HashMap<String, String> idAttributeMap;

    /**
     * <p>Constructor for AbstractSetAttributesFromFile.</p>
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
    public AbstractSetAttributesFromFile(int colId, int colAttr, BioNetwork bn, String fileIn, String c, int nSkip, EntityType entityType,
                                         Boolean p, Boolean s) {

        this.colId = colId;
        this.colAttr = colAttr;
        this.bn = bn;
        this.fileIn = fileIn;
        this.commentCharacter = c;
        this.nSkip = nSkip;
        this.addPrefix = p;
        this.addSuffix = s;
        this.idAttributeMap = new HashMap<>();
        this.ids = new HashSet<>();

        this.entityType = entityType;

        switch (entityType) {
            case REACTION: {
                this.objectIds = bn.getReactionsView().getIds();
                break;
            }
            case METABOLITE: {
                this.objectIds = bn.getMetabolitesView().getIds();
                break;
            }
            case PROTEIN: {
                this.objectIds = bn.getProteinsView().getIds();
                break;
            }
            case GENE: {
                this.objectIds = bn.getGenesView().getIds();
                break;
            }
            case COMPARTMENT: {
                this.objectIds = bn.getCompartmentsView().getIds();
                break;
            }
            case PATHWAY: {
                this.objectIds = bn.getPathwaysView().getIds();
                break;
            }
            default: {
                throw new EntityTypeException("Entity "+entityType+" not recognized");
            }
        }

        if (this.colId < 0) {
            throw new IllegalArgumentException("The id column number must be positive");
        }

        if (this.colAttr < 0) {
            throw new IllegalArgumentException("The attribute column number must be positive");
        }

    }

    /**
     * Test the attribute file and other things
     *
     * @return a {@link java.lang.Boolean} object.
     * @throws java.io.IOException if any.
     */
    public Boolean parseAttributeFile() throws IOException {

        Boolean flag = true;

        FileInputStream in;
        BufferedReader br;
        try {
            in = new FileInputStream(this.fileIn);
            InputStreamReader ipsr = new InputStreamReader(in);
            br = new BufferedReader(ipsr);
        } catch (Exception e) {
            System.err.println("Impossible to read the input file " + fileIn);
            return false;
        }

        String ligne;

        int nLines = 0;

        while ((ligne = br.readLine()) != null) {

            nLines++;

            flag = parseLine(ligne, nLines);
        }
        return flag;
    }

    protected Boolean parseLine(String line, int nLines) {

        Boolean flag = true;

        line = line.replace("\n", "").replace("\r", "");

        if ((nLines > this.nSkip) && (!line.isEmpty())
                && (this.commentCharacter.equals("")
                || !line.matches("^" + this.commentCharacter + ".*"))) {

            String[] tab = line.split("\\t");

            // We do not return an error if the length of the tab
            // is less than the column index because when the final columns are empty,
            // the split does not take into account them

            Pattern pattern = Pattern.compile("\\t");
            Matcher matcher = pattern.matcher(line);
            long nbColumns = matcher.results().count() + 1;

            if (nbColumns <= this.colId || nbColumns <= this.colAttr) {
                System.err.println("********\n[Warning] Bad number of columns line " + nLines + "(" + nbColumns + ")\n********");
                return false;
            }

            if (tab.length > this.colId && tab.length > this.colAttr) {

                String attribute = tab[this.colAttr];

                if (!this.testAttribute(attribute)) {
                    System.err.println("********\n[Warning]Attribute \"" + attribute
                            + "\" not well formatted\n********");
                    flag = false;
                }

                String id = tab[this.colId];
                // remove spaces
                id = id.trim();

                if (this.entityType.equals(EntityType.METABOLITE)) {
                    // To transform metabolite id like this cpd[c] in
                    // cpd_c
                    Pattern cpd_Pattern = Pattern.compile("^.*(\\[([^\\]]*)\\])$");

                    Matcher matcherCpd = cpd_Pattern.matcher(id);

                    // cpd(e) becomes cpd_e
                    if (matcherCpd.matches()) {
                        id = id.replace(matcherCpd.group(1), "_" + matcherCpd.group(2));
                    }
                }

                if (this.entityType.equals(EntityType.METABOLITE) && this.addSuffix) {
                    Boolean presence = false;

                    BioCollection<BioCompartment> compartmentsView = this.bn.getCompartmentsView();
                    // Checks if the suffix corresponds to a compartment
                    for (String compartmentId : compartmentsView.getIds()) {

                        String metaboliteId = id;

                        // // To see if the id ends with _compartmentId
                        // Pattern cpt_Pattern =
                        // Pattern.compile("^.*_([^_])"+compartmentId+"$");
                        //
                        // metaboliteId =
                        // metaboliteId.replaceAll("[^A-Za-z0-9_]",
                        // "_");
                        //
                        // System.err.println(metaboliteId);
                        //
                        // Matcher matcherCpt =
                        // cpt_Pattern.matcher(metaboliteId);

                        // if(! matcherCpt.matches()) {
                        metaboliteId = (this.addPrefix ? "M_" : "") + metaboliteId + "_" + compartmentId;
                        // }
                        // else {
                        // metaboliteId = (this.addPrefix ? "M_" : "")
                        // +metaboliteId;
                        // }

                        if (this.bn.containsMetabolite(metaboliteId)) {
                            this.getIdAttributeMap().put(metaboliteId, attribute);
                            presence = true;
                        }
                    }

                    if (presence && ids.contains(id)) {
                        System.err.println("[Warning] Duplicated id : " + id + " line " + nLines);
                        flag = false;
                    }

                    if (presence) {
                        ids.add(id);
                    }

                    if (!presence) {
                        System.err.println("[Warning] Metabolite " + id
                                + " does not correspond to any metabolite in the network line " + nLines);
                        // flag = false;
                    }
                } else {
//							id = id.replaceAll("[^A-Za-z0-9_]", "_");

                    if (this.addPrefix && this.entityType.equals(EntityType.REACTION)) {
                        if (!id.startsWith("R_"))
                            id = "R_" + id;
                    } else if (this.addPrefix && this.entityType.equals(EntityType.METABOLITE)) {
                        if (!id.startsWith("M_"))
                            id = "M_" + id;
                    }

                    if (id.equals("")) {
                        System.err.println(
                                "********\n[Fatal Error]Empty object id empty column " + nLines + "\n********");
                        flag = false;
                    }

                    if (ids.contains(id)) {
                        System.err.println("[Warning] Duplicated id : " + id + " line " + nLines);
                        flag = false;
                    } else {
                        if (!this.objectIds.contains(id)) {
                            System.err
                                    .println("[Warning] " + id + " not present in the network line " + nLines);
                            // flag = false;
                        } else {
                            this.getIdAttributeMap().put(id, attribute);
                            ids.add(id);
                        }
                    }
                }
            }
        }
        return flag;
    }

    /**
     * Abstract function to validate the attribute
     *
     * @param attribute a {@link java.lang.String} object.
     * @return true if attribute well formatted
     */
    public abstract Boolean testAttribute(String attribute);

    /**
     * Reads the file and sets the attributes
     *
     * @return a {@link java.lang.Boolean} object.
     * @throws java.io.IOException if any.
     */
    public abstract Boolean setAttributes() throws IOException;

    /**
     * <p>Getter for the field <code>idAttributeMap</code>.</p>
     *
     * @return the idAttributeMap
     */
    public HashMap<String, String> getIdAttributeMap() {
        return idAttributeMap;
    }

}
