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

package fr.inrae.toulouse.metexplore.met4j_io.metexplorexml.reader;

import java.io.IOException;
import java.util.HashMap;

import fr.inrae.toulouse.metexplore.met4j_io.annotations.GenericAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.metabolite.MetaboliteAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.UnitSbml;
import org.sbml.jsbml.text.parser.ParseException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEnzyme;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.AnnotatorComment;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.Flux;
import fr.inrae.toulouse.metexplore.met4j_io.utils.StringUtils;
import fr.inrae.toulouse.metexplore.met4j_io.utils.XmlUtils;

import static fr.inrae.toulouse.metexplore.met4j_core.utils.StringUtils.isVoid;

public class MetexploreXmlReader {

    private BioNetwork network;
    private String xmlFile;
    private Document document;

    private HashMap<String, String> compartmentMetabolites;

    public MetexploreXmlReader(String xmlFile) {
        this.xmlFile = xmlFile;
        compartmentMetabolites = new HashMap<String, String>();
    }

    /**
     * Read the xml file and create the network
     *
     * @throws ParseException
     */
    public void read() throws ParseException {

        try {
            document = XmlUtils.open(xmlFile);

            Element model = (Element) document.getElementsByTagName("model").item(0);

            network = new BioNetwork(model.getAttribute("id"));
            network.setName(model.getAttribute("name"));

            this.readUnitDefinitions();
            this.readCompartments();
            this.readMetabolites();
            this.readReactions();
        } catch (IOException e) {
            System.err.println("Error while reading Xml file");
            e.printStackTrace();
        } catch (SAXException e) {
            System.err.println("Xml file badly formatted");
            e.printStackTrace();
        }

    }

    /**
     * Read unit definitions
     *
     * @throws ParseException
     */
    public void readUnitDefinitions() throws ParseException {
        NodeList listOfUnitDefinitions = document.getElementsByTagName("listOfUnitDefinitions");
        if (listOfUnitDefinitions != null && listOfUnitDefinitions.getLength() > 0) {

                NodeList unitDefinitions = ((Element) (listOfUnitDefinitions.item(0)))
                        .getElementsByTagName("unitDefinition");

                int nbUnitDefinitions = unitDefinitions.getLength();

                // IMPORTANT: The loop adds 2 at each iteration... I don't know
                // why, but there are

                for (int i = 0; i < nbUnitDefinitions; i++) {
                    Element unitDefinitionElt = (Element) unitDefinitions.item(i);
                    String unitDefinitionId = unitDefinitionElt.getAttribute("id");
                    String unitDefinitionName = unitDefinitionElt.getAttribute("name");

                    if (unitDefinitionName == null) {
                        unitDefinitionName = "";
                    }

                    BioUnitDefinition unitDefinition = new BioUnitDefinition(unitDefinitionId, unitDefinitionName);

                    NodeList listOfUnits = unitDefinitionElt.getElementsByTagName("listOfUnits");

                    if (listOfUnits != null && listOfUnits.getLength() > 0) {

                        NodeList units = listOfUnits.item(0).getChildNodes();
                        int nbUnits = units.getLength();

                        for (int j = 1; j < nbUnits; j = j + 2) {
                            Element unitElt = (Element) units.item(j);

                            String kind = unitElt.getAttribute("kind");
                            if (kind == null) {
                                throw new ParseException("Unit kind badly formatted");
                            }

                            Double multiplier = null;
                            String attr = unitElt.getAttribute("multiplier");

                            if (attr != null) {

                                try {
                                    multiplier = Double.valueOf(attr);
                                } catch (NumberFormatException e) {
                                    System.err.println("Multiplier badly formatted, must be a double");
                                    e.printStackTrace();
                                }
                            }

                            Integer scale = null;
                            attr = unitElt.getAttribute("scale");
                            if (attr != null) {

                                try {
                                    scale = Integer.valueOf(attr);
                                } catch (NumberFormatException e) {
                                    System.err.println("scale badly formatted, must be an integer");
                                    e.printStackTrace();
                                }
                            }

                            Double exponent = null;
                            attr = unitElt.getAttribute("exponent");

                            if (attr != null) {

                                try {
                                    exponent = Double.valueOf(attr);
                                } catch (NumberFormatException e) {
                                    System.err.println("scale badly formatted, must be an integer");
                                    e.printStackTrace();
                                }
                            }

                            UnitSbml unit = new UnitSbml(kind, exponent, scale, multiplier);

                            unitDefinition.addUnit(unit);

                        }

                    }

                    NetworkAttributes.addUnitDefinition(network, unitDefinition);

                }
        } else {
            System.err.println("[WARNING] No unit definition, set default");
            BioUnitDefinition unitDefinition = new BioUnitDefinition();
            NetworkAttributes.addUnitDefinition(network, unitDefinition);
        }
    }

    /**
     * Read compartments
     */
    public void readCompartments() {
        NodeList listOfCompartments = document.getElementsByTagName("listOfCompartments");

        // Check if everything is OK.
        if (listOfCompartments == null)
            throw new RuntimeException("Incorrect Format: Can't find node [listOfCompartments]");

        if (listOfCompartments.getLength() > 1)
            throw new RuntimeException("Incorrect Format: More than one node [listOfCompartments]");

        NodeList compartments = listOfCompartments.item(0).getChildNodes();

        int compartmentCount = compartments.getLength();

        // IMPORTANT: The loop adds 2 at each iteration... I don't know why, but
        // there are
        // some
        for (int i = 1; i < compartmentCount; i = i + 2) {
            Element compartment = (Element) compartments.item(i);
            String compartmentId = compartment.getAttribute("id");
            String compartmentName = compartment.getAttribute("name");

            if (compartmentId == null) {
                compartmentId = "NA";
            }
            if (compartmentName == null) {
                compartmentName = compartmentId;
            }

            BioCompartment comp = new BioCompartment(compartmentId, compartmentName);

            this.network.add(comp);

        }
    }

    /**
     * @throws ParseException
     * @author LC from Paulo Milreu Read metabolites
     */
    public void readMetabolites() throws ParseException {
        // get the element "listOfSpecies"
        NodeList listOfSpecies = document.getElementsByTagName("listOfSpecies");

        // Check if everything is OK.
        if (listOfSpecies == null)
            throw new RuntimeException("Incorrect Format: Can't find node [listOfSpecies]");

        if (listOfSpecies.getLength() > 1)
            throw new RuntimeException("Incorrect Format: More than one node [listOfSpecies]");

        // print all child elements from listOfSpecies
        NodeList listOfCompounds = listOfSpecies.item(0).getChildNodes();
        int compoundCount = listOfCompounds.getLength();
        // IMPORTANT: The loop adds 2 at each iteration... I don't know why, but
        // there are
        // some
        for (int i = 1; i < compoundCount; i = i + 2) {
            Element compound = (Element) listOfCompounds.item(i);
            String cpdId = compound.getAttribute("id");
            String cpdName = compound.getAttribute("name");

            BioMetabolite cpd = new BioMetabolite(cpdId, cpdName);

            String compartmentId = compound.getAttribute("compartment");

            compartmentMetabolites.put(cpdId, compartmentId);

            Integer charge = null;
            if (compound.hasAttribute("charge")) {
                charge = Integer.valueOf(compound.getAttribute("charge"));
            }

            String boundaryConditionStr = compound.getAttribute("boundaryCondition");

            Boolean boundaryCondition = false;

            if (boundaryConditionStr != null && boundaryConditionStr.compareToIgnoreCase("true") == 0) {
                boundaryCondition = true;
            }

            String formula = compound.getAttribute("formula");

            String massAttr = compound.getAttribute("mass");


            Double mass = null;
            if (massAttr != null) {
                // Remove the unit
                massAttr = massAttr.replaceAll("d0", "");
                try {
                    mass = Double.valueOf(massAttr);
                } catch (NumberFormatException e) {
                    System.err.println("Mass of " + cpd.getId() + " badly formatted");
                }
            }

            Boolean generic = false;

            String genericStr = compound.getAttribute("generic");

            if (genericStr != null && genericStr.compareToIgnoreCase("true") == 0) {
                generic = true;
            }

            if (!this.network.getCompartmentsView().containsId(compartmentId)) {
                throw new ParseException("Compartment of " + cpd.getId() + " not declared");
            }

            if (charge != null)
                cpd.setCharge(charge);

            if (boundaryCondition != null)
                MetaboliteAttributes.setBoundaryCondition(cpd, boundaryCondition);

            if (formula != null)
                cpd.setChemicalFormula(formula);

            if (generic != null)
                GenericAttributes.setGeneric(cpd, generic);

            if (mass != null)
                cpd.setMolecularWeight(mass);

            network.add(cpd);

            BioCompartment bioCompartment = this.network.getCompartmentsView().get(compartmentId);
            network.affectToCompartment(bioCompartment, cpd);

            NodeList listsFromMetabolite = compound.getChildNodes();

            int listsCount = listsFromMetabolite.getLength();
            for (int j = 0; j < listsCount; j++) {
                if ("notes".equals(listsFromMetabolite.item(j).getNodeName())) {

                    Node noteNode = listsFromMetabolite.item(j);

                    NodeList notesNodes = noteNode.getChildNodes();

                    for (int k = 0; k < notesNodes.getLength(); k++) {

                        Node x = notesNodes.item(k);

                        if ("body".equals(x.getNodeName())) {

                            NodeList bodyNodes = x.getChildNodes();

                            for (int iterBody = 0; iterBody < bodyNodes.getLength(); iterBody++) {

                                Node y = bodyNodes.item(iterBody);

                                String valInBody = y.getTextContent();

                                String REGEX_inchi = ".*INCHI:\\s(\\S+).*";
                                String REGEX_smiles = ".*SMILES:\\s(\\S+).*";

                                if (valInBody.matches(REGEX_inchi)) {
                                    String value = valInBody.replaceAll(REGEX_inchi, "$1");
                                    cpd.setInchi(value);
                                } else if (valInBody.matches(REGEX_smiles)) {
                                    String value = valInBody.replaceAll(REGEX_smiles, "$1");
                                    cpd.setSmiles(value);
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    /**
     * @throws ParseException
     */
    public void readReactions() throws ParseException {
        // get the element "listOfReactions"
        NodeList listOfReactions = document.getElementsByTagName("listOfReactions");

        // Check if everything is OK.
        if (listOfReactions == null) {
            throw new ParseException("Incorrect Format: Can't find node [listOfReactions]");
        }

        if (listOfReactions.getLength() > 1) {
            throw new ParseException("Incorrect Format: More than one node [listOfReactions]");
        }

        // print all child elements from listOfReactions
        NodeList reactionsList = listOfReactions.item(0).getChildNodes();
        int reactionCount = reactionsList.getLength();

        // IMPORTANT: The loop adds 2 at each iteration... I don't know why, but
        // there are
        // some
        for (int i = 1; i < reactionCount; i = i + 2) {
            Element reaction = (Element) reactionsList.item(i);

            BioReaction rxn = new BioReaction(reaction.getAttribute("id"),
                    reaction.getAttribute("name"));

            network.add(rxn);

            Boolean rev = true;

            String revAttr = reaction.getAttribute("reversible");

            if (revAttr != null && !revAttr.equalsIgnoreCase("false") && !revAttr.equalsIgnoreCase("true")) {
                throw new ParseException("Reversibility of the reaction " + rxn.getId()
                        + " badly formatted, must be equal to true or false");
            }

            if (revAttr != null && revAttr.equalsIgnoreCase("false")) {
                rev = false;
            }

            rxn.setReversible(rev);

            String ec = reaction.getAttribute("ec");

            if (ec != null) {
                rxn.setEcNumber(ec);
            }

            String holeStr = reaction.getAttribute("hole");

            Boolean hole = false;

            if (holeStr != null && !holeStr.equalsIgnoreCase("false") && !holeStr.equalsIgnoreCase("true")) {
                throw new ParseException("Hole attribute of the reaction " + rxn.getId()
                        + " badly formatted, must be equal to true or false");
            }

            if (holeStr != null && holeStr.compareToIgnoreCase("true") == 0) {
                hole = true;
            }

            ReactionAttributes.setHole(rxn, hole);

            String genericStr = reaction.getAttribute("generic");

            Boolean generic = false;

            if (genericStr != null && genericStr.compareToIgnoreCase("true") == 0) {
                generic = true;
            }

            GenericAttributes.setGeneric(rxn, generic);

            String type = reaction.getAttribute("type");

            if (type != null) {
                GenericAttributes.setType(rxn, type);
            }

            this.readReaction(reaction, rxn);

        }
    }

    /**
     * Parse a reaction
     *
     * @param reaction
     * @param rxn
     * @throws ParseException
     */
    public void readReaction(Element reaction, BioReaction rxn) throws ParseException {
        NodeList enzymeNodes = reaction.getElementsByTagName("enzyme");

        int nbEnzymes = enzymeNodes.getLength();

        for (int i = 0; i < nbEnzymes; i++) {

            Element enzymeNode = (Element) enzymeNodes.item(i);
            String enzymeId = enzymeNode.getAttribute("id");
            String enzymeName = enzymeNode.getAttribute("name");

            BioEnzyme enzyme;

            if (!network.getEnzymesView().containsId(enzymeId)) {
                enzyme = new BioEnzyme(enzymeId, enzymeName);

                network.add(enzyme);
            }

            enzyme = network.getEnzymesView().get(enzymeId);

            network.affectEnzyme(rxn, enzyme);

            NodeList proteinNodes = enzymeNode.getElementsByTagName("protein");

            for (int j = 0; j < proteinNodes.getLength(); j++) {

                Element proteinNode = (Element) proteinNodes.item(j);
                String proteinId = proteinNode.getAttribute("id");
                String proteinName = proteinNode.getAttribute("name");

                BioProtein protein;

                if (!network.getProteinsView().containsId(proteinId)) {
                    protein = new BioProtein(proteinId, proteinName);

                    network.add(protein);
                }

                protein = network.getProteinsView().get(proteinId);

                network.affectSubUnit(enzyme, 1.0, protein);

                NodeList listOfGenes = proteinNode.getElementsByTagName("gene");

                for (int k = 0; k < listOfGenes.getLength(); k++) {

                    Element geneNode = (Element) listOfGenes.item(k);
                    String geneId = geneNode.getAttribute("id");
                    String geneName = geneNode.getAttribute("name");

                    BioGene gene;
                    if (!network.getGenesView().containsId(geneId)) {
                        gene = new BioGene(geneId, geneName);

                        network.add(gene);
                    }

                    gene = network.getGenesView().get(geneId);

                    network.affectGeneProduct(protein, gene);

                }
            }
        }

        // Get score
        NodeList scoreNodes = reaction.getElementsByTagName("score");
        if (scoreNodes.getLength() > 1) {
            throw new ParseException("More than one SCORE tag in the reaction " + rxn.getId());
        }
        if (scoreNodes.getLength() == 1) {
            Element scoreNode = (Element) scoreNodes.item(0);

            String scoreTxt = scoreNode.getTextContent();

            Double score = null;

            if (!isVoid(scoreTxt)) {
                try {
                    score = Double.valueOf(scoreNode.getTextContent());
                } catch (NumberFormatException e) {
                    System.err.println("Score of the reaction " + rxn.getId() + " badly formatted, must be a double");
                    e.printStackTrace();
                }

                if (score != null) {
                    ReactionAttributes.setScore(rxn, score);
                }
            }
        }

        // Get status
        NodeList statusNodes = reaction.getElementsByTagName("status");
        if (statusNodes.getLength() > 1) {
            throw new ParseException("More than one STATUS tag in the reaction " + rxn.getId());
        }
        if (statusNodes.getLength() == 1) {
            Element statusNode = (Element) statusNodes.item(0);

            String status = statusNode.getTextContent();

            if (status != null) {
                ReactionAttributes.setStatus(rxn, status);
            }
        }

        // Get pmid
        NodeList pmidNodes = reaction.getElementsByTagName("pmid");
        if (statusNodes.getLength() > 0) {
            for (int i = 0; i < pmidNodes.getLength(); i++) {
                Element pmidNode = (Element) pmidNodes.item(i);

                Integer pmid = null;

                try {
                    pmid = Integer.valueOf(pmidNode.getTextContent());
                } catch (NumberFormatException e) {
                    System.err
                            .println("PMID badly formatted for the reaction " + rxn.getId() + ",  must be an integer");
                    e.printStackTrace();
                } catch (DOMException e) {
                    e.printStackTrace();
                }

                if (pmid != null)
                    ReactionAttributes.addPmid(rxn, pmid);

            }
        }

        // Get pathways
        NodeList pathwayNodes = reaction.getElementsByTagName("pathway");

        for (int i = 0; i < pathwayNodes.getLength(); i++) {

            Element pathwayNode = (Element) pathwayNodes.item(i);

            String pathwayId = pathwayNode.getAttribute("id");
            String pathwayName = pathwayNode.getAttribute("name");

            BioPathway pathway;

            if (!network.getPathwaysView().containsId(pathwayId)) {

                pathway = new BioPathway(pathwayId, pathwayName);

                network.add(pathway);

            }

            pathway = network.getPathwaysView().get(pathwayId);

            network.affectToPathway(pathway, rxn);
        }

        // Get comments
        NodeList commentNodes = reaction.getElementsByTagName("comment");

        for (int i = 0; i < commentNodes.getLength(); i++) {

            Element commentNode = (Element) commentNodes.item(i);

            String annotator = "NA";

            NodeList annotatorNodes = commentNode.getElementsByTagName("annotator");

            if (annotatorNodes.getLength() > 1) {
                throw new RuntimeException(
                        "More than one annotator tag in the comment " + i + " in the reaction " + rxn.getId());
            }
            if (annotatorNodes.getLength() == 1) {
                Element annotatorNode = (Element) annotatorNodes.item(0);
                annotator = annotatorNode.getTextContent();
            }

            NodeList textNodes = commentNode.getElementsByTagName("text");

            if (textNodes.getLength() > 1) {
                throw new RuntimeException(
                        "More than one text tag in the comment " + i + " in the reaction " + rxn.getId());
            }
            if (textNodes.getLength() == 0) {
                throw new RuntimeException("No text tag in the comment " + i + " in the reaction " + rxn.getId());
            }

            Element textNode = (Element) textNodes.item(0);
            String text = textNode.getTextContent();

            text = text.replaceAll("\\s+", " ");

            AnnotatorComment comment = new AnnotatorComment(text, annotator);

            GenericAttributes.addAnnotatorComment(rxn, comment);

        }

        // print all child elements from listOfReactions
        NodeList listsFromReaction = reaction.getChildNodes();

        int listsCount = listsFromReaction.getLength();
        for (int j = 0; j < listsCount; j++) {

            NodeList listOfReactants, listOfProducts;

            if ("listOfReactants".equals(listsFromReaction.item(j).getNodeName())) {
                listOfReactants = listsFromReaction.item(j).getChildNodes();
                int reactantsCount = listOfReactants.getLength();

                for (int i = 1; i < reactantsCount; i = i + 2) {
                    Element reactant = (Element) listOfReactants.item(i);

                    this.addReactant(reactant, rxn, true);
                }
            }

            if ("listOfProducts".equals(listsFromReaction.item(j).getNodeName())) {
                listOfProducts = listsFromReaction.item(j).getChildNodes();
                int productsCount = listOfProducts.getLength();

                for (int i = 1; i < productsCount; i = i + 2) {
                    Element product = (Element) listOfProducts.item(i);

                    this.addReactant(product, rxn, false);
                }
            }

            if ("kineticLaw".equals(listsFromReaction.item(j).getNodeName())) {

                Node kineticLaw = listsFromReaction.item(j);

                NodeList kineticLawNodes = kineticLaw.getChildNodes();

                for (int i = 0; i < kineticLawNodes.getLength(); i++) {

                    Node x = kineticLawNodes.item(i);

                    if ("listOfParameters".equals(x.getNodeName())) {

                        NodeList parameters = x.getChildNodes();

                        for (int k = 1; k < parameters.getLength(); k = k + 2) {

                            Element parameter = (Element) parameters.item(k);

                            String parameterId = parameter.getAttribute("id");

                            if (parameterId.compareToIgnoreCase("LOWER_BOUND") == 0
                                    || parameterId.compareToIgnoreCase("UPPER_BOUND") == 0) {
                                String valueAttr = parameter.getAttribute("value");

                                Double value = null;
                                try {
                                    value = Double.parseDouble(valueAttr);
                                } catch (NumberFormatException e) {
                                    System.err.println("Flux bound value badly formatted for reaction " + rxn.getId());
                                    e.printStackTrace();
                                }

                                String units = parameter.getAttribute("units");
                                if (units == null || units.equals("")) {
                                    units = parameter.getAttribute("name");
                                }

                                BioUnitDefinition ud;
                                if (units != null && !units.equals("")) {
                                    ud = NetworkAttributes.getUnitDefinition(network, units);
                                } else {
                                    throw new ParseException("Invalid unit definition for reaction " + rxn.getId());
                                }

                                Flux lb = new Flux(parameterId, value, ud);

                                if (parameterId.compareToIgnoreCase("LOWER_BOUND") == 0)
                                    ReactionAttributes.setLowerBound(rxn, lb);
                                else
                                    ReactionAttributes.setUpperBound(rxn, lb);
                            }
                        }

                    }

                }

            }

        }

        // It's important to do it after indicating the left and right
        // participants !
        // Get side-compounds
        NodeList sideCompoundNodes = reaction.getElementsByTagName("side-compounds");

        for (int i = 0; i < sideCompoundNodes.getLength(); i++) {

            Element sideCompoundNode = (Element) sideCompoundNodes.item(i);

            NodeList speciesReferences = sideCompoundNode.getElementsByTagName("speciesReference");

            for (int j = 0; j < speciesReferences.getLength(); j++) {
                Element speciesReference = (Element) speciesReferences.item(j);

                String idSpecies = speciesReference.getAttribute("species");
                ReactionAttributes.addSideCompound(rxn, idSpecies);
            }
        }

    }

    private void addReactant(Element reactant, BioReaction rxn, Boolean left) throws ParseException {
        // Finds the compound
        BioMetabolite c = network.getMetabolitesView()
                .get(reactant.getAttribute("species"));

        if (c != null) {
            String coeffAttr = reactant.getAttribute("stoichiometry");

            Double coeff = 1.0;

            if (coeffAttr != null) {
                try {
                    coeff = Double.valueOf(coeffAttr);
                } catch (NumberFormatException e) {
                    System.err.println("Stoichiometric coeff badly formatted in the reaction " + rxn.getId());
                    e.printStackTrace();
                }
            }

            String compartmentId = compartmentMetabolites.get(c.getId());

            BioCompartment compartment = network.getCompartmentsView().get(compartmentId);

            if (left) {
                network.affectLeft(rxn, coeff, compartment, c);
            } else {
                network.affectRight(rxn, coeff, compartment, c);
            }
        } else {
            throw new ParseException(
                    "Reactant " + reactant.getAttribute("species") + " not found in the reaction " + rxn.getId());
        }
    }

    public BioNetwork getNetwork() {
        return network;
    }


}