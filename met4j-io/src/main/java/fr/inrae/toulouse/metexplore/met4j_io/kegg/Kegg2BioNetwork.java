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

package fr.inrae.toulouse.metexplore.met4j_io.kegg;


import java.io.StringReader;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import com.google.common.collect.Lists;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.metabolite.MetaboliteAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * <p>Kegg2BioNetwork class.</p>
 *
 * @author lcottret
 */
public class Kegg2BioNetwork {


    KeggServices keggServices;
    public String origin = "map";
    public BioNetwork network;
    public String keggOrgId;

    private final HashSet<String> ecList = new HashSet<>();
    private final HashSet<String> geneList = new HashSet<>();
    private final HashMap<String, String> pathwayList = new HashMap<>();

    /**
     * <p>Constructor for Kegg2BioNetwork.</p>
     *
     * @param idOrg a {@link java.lang.String} object.
     */
    public Kegg2BioNetwork(String idOrg) throws Exception {

        if (idOrg.length() != 3) {
            throw new Exception("[met4j-io][Kegg2BioNetwork] The organism id must have 3 letters");
        }

        this.keggOrgId = idOrg.toLowerCase();

        this.network = new BioNetwork(idOrg);
        this.keggServices = new KeggServices();
    }

    /**
     * <p>Constructor for Kegg2BioNetwork.</p>
     *
     * @param idOrg a {@link java.lang.String} object.
     * @param ori   a {@link java.lang.String} object.
     */
    public Kegg2BioNetwork(String idOrg, String ori) throws Exception {
        this(idOrg);
        this.origin = ori;
    }

    /**
     * <p>createBionetworkFromKegg.</p>
     */
    public void createBionetworkFromKegg() {
        System.err.println("Start : " + (new Date()));
        try {
            this.setBionetworkDefaultValue();

            System.err.println("Gets EC numbers...");
            this.setECList();

            System.err.println("Gets pathways...");
            this.createNetworkPathways();

            System.err.println("Gets reaction data...");

            List<List<String>> queryReactions = this.patitionIdsByTen(new ArrayList<>(this.network.getReactionsView().getIds()));

            for (List<String> tenReactions : queryReactions) {
                TimeUnit.MILLISECONDS.sleep(100);

                this.getReactionData(tenReactions);
            }


            System.err.println("Get metabolite data...");

            List<List<String>> queryMetabolites = this.patitionIdsByTen(new ArrayList<>(this.network.getMetabolitesView().getIds()));

            for (List<String> tenMetabolites : queryMetabolites) {
                TimeUnit.MILLISECONDS.sleep(100);

                this.getCompoundData(tenMetabolites);
            }

            System.err.println("Done !\n");
            System.err.println("Number of reactions: " + this.getNetwork().getReactionsView().size());
            System.err.println("Number of metabolites: " + this.getNetwork().getMetabolitesView().size());
            System.err.println("Number of pathways: " + this.getNetwork().getPathwaysView().size());
            System.err.println("Number of genes: " + this.getNetwork().getGenesView().size());
        } catch (Exception e) {
            System.err.println("[met4j-io][Kegg2BioNetwork] Unable to create a network from KEGG." +
                    " KEGG API may be down, please try later.");
            e.printStackTrace();
            this.network = null;
        }
        System.err.println("End : " + (new Date()));
    }

    /**
     * Set the default value for the compartment and the unitdefinition used in the model,
     * also retrieves the name of the network
     */
    public void setBionetworkDefaultValue() throws Exception {

        this.setNetWorkName();

        BioCompartment defaultCompartment = new BioCompartment("default", "default");
        this.network.add(defaultCompartment);

        BioUnitDefinition unitDef = new BioUnitDefinition();

        NetworkAttributes.addUnitDefinition(this.network, unitDef);

    }


    /**
     * Gets all the gene ids of the organism.
     *
     * @throws java.lang.Exception if any.
     */
    public void setGeneList() throws Exception {

        String geneString = this.keggServices.getKeggGeneEntries(this.keggOrgId);

        String[] genes = geneString.split("\\n");

        for (String line : genes) {
            String[] geneData = line.split("\\t");
            if (geneData.length != 2) {
                throw new Exception("[met4j-io][Kegg2BioNetwork] Gene list badly formatted for the organism "
                        + this.keggOrgId + " (" + line + ")");
            }
            this.geneList.add(geneData[0]);
        }
    }

    /**
     * Retrieves all pathways associated to the organism using the Kegg API.
     * First, get pathway list : the api (ex : http://rest.kegg.jp/list/pathway/hsa)
     * returns a tabulated file. Ex :
     * path:hsa00010	Glycolysis / Gluconeogenesis - Homo sapiens (human)
     * path:hsa00020	Citrate cycle (TCA cycle) - Homo sapiens (human)
     * path:hsa00030	Pentose phosphate pathway - Homo sapiens (human)
     * path:hsa00040	Pentose and glucuronate interconversions - Homo sapiens (human)
     * path:hsa00051	Fructose and mannose metabolism - Homo sapiens (human)
     * <p>
     * Then,
     *
     * @throws java.lang.Exception if any.
     */
    public void createNetworkPathways() throws Exception {

        this.setPathwayList();

        for (String dbId : this.pathwayList.keySet()) {

            String name = this.pathwayList.get(dbId);
            BioPathway path = new BioPathway(dbId, name);

            this.network.add(path);

            this.getPathwayComponents(path);

            if (this.network.getReactionsFromPathways(path).size() == 0) {
                this.network.removeOnCascade(path);
            }
        }
    }

    /**
     * Set Pathway list
     */
    public void setPathwayList() throws Exception {

        String pathwayEntries = this.keggServices.getKeggPathwayEntries(this.keggOrgId);

        String[] pathList = pathwayEntries.split("\\n");

        for (String line : pathList) {
            String[] pathwayData = line.split("\\t");

            if (pathwayData.length != 2) {
                throw new Exception("[met4j-io][Kegg2BioNetwork] Invalid format in pathway data : " + line);
            }

            //String dbId = this.simplifyId(pathwayData[0].substring(5));
            String dbId = pathwayData[0];
            String name = pathwayData[1];
            this.pathwayList.put(dbId, name);
        }

    }


    /**
     * For each pathway, dowload the kgml via api (ex : http://rest.kegg.jp/get/hsa05130/kgml)
     * Adds the reactions in the network.
     * If origin = map, adds also the primary compounds.
     * <p>
     * Sets the links between reactions and genes.
     *
     * @param pathway a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway} object.
     * @throws java.lang.Exception if any.
     */
    public void getPathwayComponents(BioPathway pathway) throws Exception {

        String xml = this.keggServices.getKgml(pathway.getId());

        Document doc;
        try {
            doc = this.loadXMLFromString(xml);
        } catch (Exception e) {
            System.err.println("[met4j-io][Kegg2BioNetwork] Badly formatted KGML for pathway " + pathway.getId());
            throw e;
        }

        doc.getDocumentElement().normalize();

        HashMap<String, String> reactionInPath = new HashMap<>();

        NodeList entryList = doc.getElementsByTagName("entry");
        for (int i = 0, c = entryList.getLength(); i < c; i++) {
            Node entry = entryList.item(i);
            if (((Element) entry).getAttribute("type").equalsIgnoreCase("GENE")) {
                /*
                 * get all reaction that have known genes associated to it
                 */
                reactionInPath.put(((Element) entry).getAttribute("reaction"), ((Element) entry).getAttribute("name"));
            }
        }

        NodeList reactionList = doc.getElementsByTagName("reaction");
        for (int i = 0, c = reactionList.getLength(); i < c; i++) {
            //get the reaction node
            Node entry = reactionList.item(i);
            Element rxn = (Element) entry;

            if (reactionInPath.containsKey(rxn.getAttribute("name"))) {
                String[] reactionIds = rxn.getAttribute("name").split(" ");

                for (String longId : reactionIds) {

                    String id = this.simplifyId(longId).replace("rn_", "");
                    BioReaction reaction;

                    if (this.network.containsReaction(id)) {
                        reaction = this.network.getReaction(id);
                        if (rxn.getAttribute("type").equalsIgnoreCase("reversible")) {
                            reaction.setReversible(true);
                        }
                    } else {
                        reaction = new BioReaction(id);

                        this.network.add(reaction);

                        reaction.setReversible(rxn.getAttribute("type").equalsIgnoreCase("reversible"));

                        /*
                         * Set the GPR for this reaction
                         */
                        String[] genes = reactionInPath.get(rxn.getAttribute("name")).split(" ");
                        for (String longGeneId : genes) {

                            String geneId = this.simplifyId(longGeneId);

                            BioGene gene = network.getGene(geneId);
                            if(gene == null)
                            {
                                gene = new BioGene(geneId);
                                network.add(gene);
                            }

                            BioProtein prot = network.getProtein(geneId);
                            if(prot == null)
                            {
                                prot = new BioProtein(geneId);
                                network.add(prot);
                            }

                            BioEnzyme enz = network.getEnzyme(geneId);
                            if(enz == null) {
                                enz = new BioEnzyme(geneId);
                                network.add(enz);
                            }

                            network.affectGeneProduct(prot, gene);
                            network.affectSubUnit(enz, 1.0, prot);
                            network.affectEnzyme(reaction, enz);
                        }
                        if (this.origin.equals("map")) {
                            NodeList childs = rxn.getChildNodes();
                            int d = childs.getLength();
                            for (int j = 1; j < d; j += 2) {
                                BioMetabolite cpd;
                                Node child = childs.item(j);
                                Element Compound = (Element) child;
                                BioCompartment cpt = this.network.getCompartment("default");

                                String metaboliteId = this.simplifyMetaboliteId(Compound.getAttribute("name"));

                                if (this.network.containsMetabolite(metaboliteId)) {
                                    cpd = this.network.getMetabolite(metaboliteId);
                                } else {
                                    cpd = new BioMetabolite(metaboliteId);
                                    this.network.add(cpd);
                                    this.network.affectToCompartment(cpt, cpd);
                                }
                                if (child.getNodeName().equalsIgnoreCase("substrate")) {
                                    network.affectLeft(reaction, 1.0, cpt, cpd);
                                    continue;
                                }
                                network.affectRight(reaction, 1.0, cpt, cpd);
                            }
                        }

                    }
                    network.affectToPathway(pathway, reaction);
                }
            }
        }
    }

    public void setNetWorkName() throws Exception {

        String info = this.keggServices.getKeggOrganismInfo(this.keggOrgId);

        String[] data = info.split("\\n");

        String firstLine = data[0];
        String[] fields = firstLine.split("\\s{2,}");
        if (fields.length != 2) {
            throw new Exception("[met4j-io][Kegg2BioNetwork] Impossible to get organism name for " + this.keggOrgId +
                    " : info badly formatted (" + firstLine + ")");
        }

        String name = fields[1].replaceAll(" KEGG.*", "");

        this.network.setName(name);

    }


    /**
     * Create reactant from string
     * <p>
     * Reformat these patterns :
     * 2n C1 -> 2 C1
     * n C1 -> 1 C1
     * C1 -> 1 C1
     * C1(n+1) -> C1
     *
     * @param substrateAndStoichio a String containing the coefficient (optional) and the cpd id
     * @return a {@link BioReactant}
     */
    private void createReactant(String substrateAndStoichio, BioReaction reaction, Boolean rightSide) {
        String stoichio;
        String longCpdId;
        BioMetabolite cpd;

        BioCompartment cpt = this.network.getCompartment("default");

        String[] tmp = substrateAndStoichio.split("[ ]+");
        if (tmp.length == 2) {
            stoichio = tmp[0];

            Pattern p = Pattern.compile("^(\\d+)[\\D+]+.*");
            Matcher m = p.matcher(stoichio);
            if (m.find()) {
                // The stoichio is in the form 2n, we keep only 2.
                stoichio = m.group(1);
                System.err.println("[met4j-io][Kegg2BioNetwork] Warning: in reaction " + reaction.getId() +
                        " : changes the stoichiometric coefficient from " + tmp[0] + " to " + stoichio);
            }
            longCpdId = tmp[1];

        } else {
            stoichio = "1.0";
            longCpdId = substrateAndStoichio;
        }

        // Here we remove patterns like (n), (n +1)
        if (longCpdId.matches(".*\\([^)]+\\)$")) {
            String id2 = longCpdId.replaceAll("\\([^)]+\\)", "");
            System.err.println("[met4j-io][Kegg2BioNetwork] Warning: in reaction " + reaction.getId() +
                    " : changes the id from " + longCpdId + " to " + id2);
            longCpdId = id2;
        }

        String cpdId = this.simplifyMetaboliteId(longCpdId);

        if (this.network.containsMetabolite(cpdId)) {
            cpd = this.network.getMetabolite(cpdId);
        } else {
            cpd = new BioMetabolite(cpdId);
            this.network.add(cpd);
            this.network.affectToCompartment(cpt, cpd);
        }

        double coeff = 1.0;
        try {
            coeff = Double.parseDouble(stoichio);
        } catch (NumberFormatException e) {
            System.err.println("[met4j-io][Kegg2BioNetwork] Warning :  The stoechiometry "
                    + stoichio + " in the reaction " + reaction.getId() + " is not a number, it is left as 1.0");
        }

        if(! rightSide){
            this.network.affectLeft(reaction, coeff, cpt, cpd);
        }
        else {
            this.network.affectRight(reaction, coeff, cpt, cpd);
        }

        return;
    }

    /**
     * Retrieve Reaction data from the Kegg database and add the other attributes by using default values
     * from the sbml specifications (2.4)
     * <p>
     * Get the info of a reaction by the api (ex : http://rest.kegg.jp/get/rn:R07618)
     * ENTRY       R07618                      Reaction
     * NAME        enzyme N6-(dihydrolipoyl)lysine:NAD+ oxidoreductase
     * DEFINITION  Enzyme N6-(dihydrolipoyl)lysine + NAD+ <=> Enzyme N6-(lipoyl)lysine + NADH + H+
     * EQUATION    C15973 + C00003 <=> C15972 + C00004 + C00080
     * COMMENT     Oxo-acid dehydrogenase complexes, dihydrolipoyl dehydrogenase
     * RCLASS      RC00001  C00003_C00004
     * RC00583  C15972_C15973
     * ENZYME      1.8.1.4
     * PATHWAY     rn00010  Glycolysis / Gluconeogenesis
     * rn00020  Citrate cycle (TCA cycle)
     * rn00280  Valine, leucine and isoleucine degradation
     * rn00620  Pyruvate metabolism
     * rn00640  Propanoate metabolism
     * rn01100  Metabolic pathways
     * rn01110  Biosynthesis of secondary metabolites
     * rn01240  Biosynthesis of cofactors
     * MODULE      M00009  Citrate cycle (TCA cycle, Krebs cycle)
     * M00011  Citrate cycle, second carbon oxidation, 2-oxoglutarate => oxaloacetate
     * M00036  Leucine degradation, leucine => acetoacetate + acetyl-CoA
     * M00307  Pyruvate oxidation, pyruvate => acetyl-CoA
     * ORTHOLOGY   K00382  dihydrolipoamide dehydrogenase [EC:1.8.1.4]
     * DBLINKS     RHEA: 15048
     * ///
     * <p>
     * Sets the formula if origin != map
     * <p>
     * Sets the EC number
     *
     * @param reactions a {@link List} of reaction ids
     */
    protected void getReactionData(List<String> reactions) throws Exception {

        HashMap<String, HashMap<String, ArrayList<String>>> allData = this.getEntitiesData(reactions);

        for (String id : allData.keySet()) {

            /*
             * Reactions must be first created in createNetworksForPathways
             */
            BioReaction rxn = this.getNetwork().getReaction(id);

            if (rxn == null) {
                throw new Exception("[met4j-io][Kegg2BioNetwork] Problem while setting reaction " + id + "");
            }

            HashMap<String, ArrayList<String>> Data = allData.get(id);

            if (Data.get("NAME") != null) {
                rxn.setName(Data.get("NAME").get(0));
            }
            if (Data.get("COMMENT") != null) {
                rxn.setComment(Data.get("COMMENT").get(0));
            }
            if (this.origin.equals("reaction") && Data.get("EQUATION") != null && Data.get("EQUATION").get(0).contains("<=>")) {


                String[] eq = Data.get("EQUATION").get(0).split(" <=> ");
                String[] subs = eq[0].split(" \\+ ");
                String[] prod = eq[1].split(" \\+ ");

                for (String substrateAndStoichio : subs) {
                    createReactant(substrateAndStoichio, rxn, false);
                }

                for (String productAndStoechio : prod) {
                    createReactant(productAndStoechio, rxn, true);
                }
            }
            if (Data.get("ENZYME") != null) {
                for (String ecNum : Data.get("ENZYME")) {
                    if (this.ecList.contains(ecNum)) {
                        if (rxn.getEcNumber() == null || rxn.getEcNumber().isEmpty() || rxn.getEcNumber().equalsIgnoreCase(" ")) {
                            rxn.setEcNumber(ecNum);
                        } else {
                            rxn.setEcNumber(rxn.getEcNumber() + " / " + ecNum);
                        }
                    }
                }
            }
        }
    }

    /**
     * Retrieve data for each compound from the Kegg database and add the other attributes by using default values
     * from the sbml specifications (2.4)
     * Note that those compounds can come from Kegg Compound Database or from Kegg Glycan Database
     * <p>
     * Get the metabolite data from kegg api (ex : http://rest.kegg.jp/get/C15973)
     * ENTRY       C15973                      Compound
     * NAME        Enzyme N6-(dihydrolipoyl)lysine;
     * Dihydrolipoamide-E;
     * [E2 protein]-N6-[(R)-dihydrolipoyl]-L-lysine;
     * [Lipoyl-carrier protein E2]-N6-[(R)-dihydrolipoyl]-L-lysine
     * FORMULA     C8H16NOS2R
     * COMMENT     Generic compound in reaction hierarchy
     * The reduced lipoyllysine residue in EC 2.3.1.12, dihydrolipoyllysine-residue acetyltransferase ( EC 2.3.1.61, dihydrolipoyllysine-residue succinyltransferase or EC 2.3.1.168, dihydrolipoyllysine-residue (2-methylpropanoyl)transferase ).
     * REACTION    R02569 R02570 R02571 R02662 R03174 R04097 R07618 R10998
     * R12423 R12432 R12603
     * PATHWAY     map00010  Glycolysis / Gluconeogenesis
     * map00020  Citrate cycle (TCA cycle)
     * map00280  Valine, leucine and isoleucine degradation
     * map00620  Pyruvate metabolism
     * map00640  Propanoate metabolism
     * map00785  Lipoic acid metabolism
     * map01100  Metabolic pathways
     * map01240  Biosynthesis of cofactors
     * MODULE      M00009  Citrate cycle (TCA cycle, Krebs cycle)
     * M00011  Citrate cycle, second carbon oxidation, 2-oxoglutarate => oxaloacetate
     * M00036  Leucine degradation, leucine => acetoacetate + acetyl-CoA
     * M00307  Pyruvate oxidation, pyruvate => acetyl-CoA
     * M00881  Lipoic acid biosynthesis, plants and bacteria, octanoyl-ACP => dihydrolipoyl-E2/H
     * M00883  Lipoic acid biosynthesis, animals and bacteria, octanoyl-ACP => dihydrolipoyl-H => dihydrolipoyl-E2
     * M00884  Lipoic acid biosynthesis, octanoyl-CoA => dihydrolipoyl-E2
     * ENZYME      1.8.1.4         1.11.1.28       2.3.1.12        2.3.1.61
     * 2.3.1.168       2.8.1.8
     * DBLINKS     PubChem: 47205286
     * ChEBI: 80219
     * ATOM        13
     * 1   C1c C    15.4683  -15.8087
     * 2   C1b C    16.6705  -16.5090
     * 3   C1b C    14.2660  -16.5090
     * 4   S1a S    15.4683  -14.4255
     * 5   C1b C    17.8728  -15.8087
     * 6   C1b C    13.0639  -15.8087
     * 7   C1b C    19.0808  -16.5090
     * 8   S1a S    13.0639  -14.4255
     * 9   C1b C    20.2771  -15.8087
     * 10  C5a C    21.4794  -16.5090
     * 11  O5a O    21.4794  -17.8921
     * 12  N1b N    22.8334  -15.8379
     * 13  R   R    24.2335  -15.8379
     * BOND        12
     * 1     1   2 1 #Down
     * 2     1   3 1
     * 3     1   4 1
     * 4     2   5 1
     * 5     3   6 1
     * 6     5   7 1
     * 7     6   8 1
     * 8     7   9 1
     * 9     9  10 1
     * 10   10  11 2
     * 11   10  12 1
     * 12   12  13 1
     * ///
     * <p>
     * Adds name, mass, formula information and db links.
     *
     * @param metabolites : a {@link List} of metabolite ids
     */
    protected void getCompoundData(List<String> metabolites) throws Exception {

        HashMap<String, HashMap<String, ArrayList<String>>> dataMap = this.getEntitiesData(metabolites);

        for (String id : dataMap.keySet()) {

            BioMetabolite metabolite = this.getNetwork().getMetabolite(id);

            HashMap<String, ArrayList<String>> Data = dataMap.get(id);

            MetaboliteAttributes.setBoundaryCondition(metabolite, false);
            MetaboliteAttributes.setConstant(metabolite, false);
            MetaboliteAttributes.setHasOnlySubstanceUnits(metabolite, false);

            if (Data.get("NAME") != null) { //kegg glycans entries do not always have names or chemical formulas
                metabolite.setName(Data.get("NAME").get(0));
            }
            if (Data.get("FORMULA") != null) {
                metabolite.setChemicalFormula(Data.get("FORMULA").get(0));
            }
            if (Data.get("MOL_WEIGHT") != null) {
                double mass = 0.0;
                try {
                    mass = Double.parseDouble(Data.get("MOL_WEIGHT").get(0));
                } catch (NumberFormatException e) {
                    System.err.println("The mass " + Data.get("MOL_WEIGHT").get(0) + " is not a number, it is left as 0.0");
                }
                metabolite.setMolecularWeight(mass);
            } else if (Data.get("EXACT_MASS") != null) {
                double mass = 0.0;
                try {
                    mass = Double.parseDouble(Data.get("EXACT_MASS").get(0));
                } catch (NumberFormatException e) {
                    System.err.println("The mass " + Data.get("EXACT_MASS").get(0) + " is not a number, it is left as 0.0");
                }
                metabolite.setMolecularWeight(mass);
            }

            metabolite.addRef(new BioRef("kegg", "kegg.compound", metabolite.getId(), 1));

            if (Data.get("DBLINKS") != null) {
                for (String links : Data.get("DBLINKS")) {
                    String[] tab = links.split(": ");
                    metabolite.addRef(new BioRef("kegg", tab[0], tab[1], 1));
                }
            }
        }
    }


    private String simplifyMetaboliteId(String id) {
        String[] tempArray = id.split("[: ]");
        if (tempArray.length > 1) {
            return tempArray[1];
        } else {
            return id;
        }
    }

    private String simplifyId(String id) {
        return id.replaceAll("[: ]", "_");
    }


    /**
     * @param xml a String
     * @return a {@link Document}
     */
    protected Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = getFactory();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

    protected DocumentBuilderFactory getFactory() {
        return DocumentBuilderFactory.newInstance();
    }

    /**
     * Gets the data of a list of entities from the Kegg api
     * <p>
     * The list of ids (no more than 10) are concatened in one query.
     *
     * @param list {@link List} of ids
     * @return a map whose primary keys are entity ids and the second keys are the data fields
     */
    public HashMap<String, HashMap<String, ArrayList<String>>> getEntitiesData(List<String> list) throws Exception {

        if (list.size() > 10) {
            throw new IllegalArgumentException("[FATAL][met4j-io][Kegg2BioNetwork] query apis must not contain more than 10 ids");
        }

        String ids = String.join("+", list);

        String[] Data = this.keggServices.getKeggEntities(ids).split("\\n");

        String lastKey = null, id = null;
        HashMap<String, ArrayList<String>> output = null;

        HashMap<String, HashMap<String, ArrayList<String>>> res = new HashMap<>();

        for (String line : Data) {

            String[] linedata = line.split("[ ]{2,}");

            if (linedata[0].length() != 0) {
                lastKey = linedata[0];
            }

            if (lastKey != null && lastKey.equalsIgnoreCase("///")) {
                res.put(id, output);
                continue;
            }

            if (lastKey != null && lastKey.equalsIgnoreCase("ENTRY")) {
                id = linedata[1];
                output = new HashMap<>();
                continue;
            }

            if (lastKey != null) {
                String valuePart = line.replace(lastKey, "").trim();

                String[] values = valuePart.split("[ ]{3,}");
                if (id != null) {
                    ArrayList<String> tempData;

                    if (output.containsKey(lastKey)) {
                        tempData = output.get(lastKey);

                    } else {
                        tempData = new ArrayList<>();
                        output.put(lastKey, tempData);
                    }

                    for (String s : values) {
                        String value = s.trim();
                        if (!value.isEmpty()) {
                            tempData.add(s.replace(";", ""));
                        }
                    }
                }
            }
        }

        if (res.size() != list.size()) {
            throw new Exception("[met4j-io][Kegg2BioNetwork] Problem while loading entities "
                    + ids + " : the number of results is different than the number of ids");
        }

        return res;
    }

    /**
     * <p>Getter for the field <code>network</code>.</p>
     *
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork} object.
     */
    public BioNetwork getNetwork() {
        return network;
    }

    /**
     * Sets the links between ecs and genes.
     */
    public void setECList() throws Exception {

        String ecEntries;

        ecEntries = keggServices.getKeggEcGeneEntries(this.keggOrgId);

        String[] Data = ecEntries.split("\\n");

        for (String line : Data) {
            String[] tmp = line.split("\t");
            if (tmp.length != 2) {
                throw new Exception("[met4j-io][Kegg2BioNetwork] Problem while loading EC numbers: api result badly formatted : " + line);
            }
            ecList.add(tmp[1].substring(3));
        }

    }

    /**
     * Partition a list of ids by lists of 10 elements and concatenate them into
     * strings ready for api
     * For instance : [A,B,C,D,E,F,G,H,I,L], [M,N,O]
     * will return :
     * ["A+B+C+D+E+F+G+H+I+L"], ["M+N+O"]
     *
     * @param ids a {@link List} of ids
     * @return a {@link Set} of {@link String}
     */
    private List<List<String>> patitionIdsByTen(List<String> ids) {
        return Lists.partition(ids, 10);
    }

    public HashSet<String> getGeneList() {
        return geneList;
    }

    public HashSet<String> getEcList() {
        return ecList;
    }

    public HashMap<String, String> getPathwayList() {
        return pathwayList;
    }

    /**
     * Main for testing class
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) throws Exception {
        Kegg2BioNetwork ktbn = new Kegg2BioNetwork("hsa", "reaction");
        ktbn.keggServices.checkKeggOrgId("bap");
        try {
            ktbn.createBionetworkFromKegg();
        } catch (Exception e) {
            ktbn.network = null;
            e.printStackTrace();
        }
    }
}
