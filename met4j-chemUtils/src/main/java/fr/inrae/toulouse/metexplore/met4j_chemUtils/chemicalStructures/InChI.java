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

package fr.inrae.toulouse.metexplore.met4j_chemUtils.chemicalStructures;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class converts an inchi Strings into objects that can be manipulated and
 * compared between them with a custom logic
 *
 * @author Benjamin
 * @since 2.0
 * @version $Id: $Id
 */
public class InChI extends ChemicalStructure {

    /**
     * The original Inchi String
     */
    public String inchiString;
    /**
     * The version of the Inchi Program
     */
    public int version;
    /**
     * if the Inchi is Stanadard or not
     */
    public boolean standard;
    /**
     * The neutral chemical formula of the molecule
     */
    public String formulaLayer;

    /**
     * The connectivity layer. It describes how non hydrogen atoms are connected
     * to each other
     */
    public InchiLayer connectivity;
    /**
     * The Hydrogen Layer. It describes how and where the hydrogen atoms are
     * connected
     */
    public InchiLayer hLayer;
    /**
     * The charge Layer, indicate the ccharge of the molecule
     */
    public InchiLayer chargeLayer;
    /**
     * The protonation layer, indicate the protonation state of the molecule. It
     * must be taken into account when extracting the effective chemical formula
     * of the molecule
     */
    public InchiLayer protonationLayer;

    /**
     * The double-bond Stereochemistry layer. Indicates the double-bond
     * Stereochemistry (Z/E) of carbon double bonds
     */
    public InchiLayer dbStereoLayer;

    /**
     * The asymmetric carbon Stereo chemistry layer. Indicates the asymmetric
     * carbon Stereochemistry (L/R)
     */
    public InchiLayer tetraStereoLayer;

    /**
     * The Isotopic Layer. Indicates the position and nature (13C or 14C) of
     * isotopes in the molecule
     */
    public InchiLayer isotopicLayer;
    /**
     * The fixed atoms Layer. Indicates the position of non mobile atoms in the
     * molecule.<br>This layer can contains any of the above layer as sub-layer
     */
    public InchiLayer fixedLayer;
    /**
     * The reconnected Layer. Can be used for molecules that are connected
     * through a metallic bond
     */
    public InchiLayer reconnectedLayer;

    /**
     * Empty constructor. Set the Validity to false
     */
    public InChI() {
        this.validity = false;
    }

    /**
     * Constructor. Uses {@link #setLayers()} to create all the sub-layers of
     * the inchi.
     *
     * @param inchiStringValue the inchi String
     */
    public InChI(String inchiStringValue) {

        this.inchiString = inchiStringValue;

        this.setLayers();
    }

    /**
     * Parse the {@link #inchiString} to create the different {@link fr.inrae.toulouse.metexplore.met4j_chemUtils.chemicalStructures.InchiLayer}
     * . If any of the created layer is invalid, the all InChI is set as invalid
     * <br>Use an algorithm described in the InChI specifications
     */
    public void setLayers() {
        String trunckedInchi = this.getInchiString();

        if (!trunckedInchi.startsWith("InChI=")) {
            this.setValidity(false);
            return;
        }

        trunckedInchi = trunckedInchi.substring(6);
        try {
            this.setVersion(Integer.parseInt(trunckedInchi.substring(0, 1)));
        } catch (Exception e) {
            this.setValidity(false);
            return;
        }
        if (trunckedInchi.charAt(1) == 'S') {
            this.setStandard(true);
            trunckedInchi = trunckedInchi.substring(2);
        } else {
            this.setStandard(false);
            trunckedInchi = trunckedInchi.substring(1);
        }

        if (trunckedInchi.contains("/r")) {
            String[] tmp = trunckedInchi.split("/r");

            if (tmp.length != 2) {
                this.setValidity(false);
                return;
            }

            InchiLayer reconnect = new InchiLayer('r', tmp[1]);
            this.setReconnectedLayer(reconnect);

            trunckedInchi = tmp[0];
        }

        if (trunckedInchi.contains("/f")) {
            String[] tmp = trunckedInchi.split("/f");

            if (tmp.length != 2) {
                this.setValidity(false);
                return;
            }

            InchiLayer fixed = new InchiLayer('f', tmp[1]);
            this.setFixedLayer(fixed);

            trunckedInchi = tmp[0];
        }

        if (trunckedInchi.contains("/i")) {
            String[] tmp = trunckedInchi.split("/i");

            if (tmp.length != 2) {
                this.setValidity(false);
                return;
            }

            InchiLayer isotopiclay = new InchiLayer('i', tmp[1]);
            this.setIsotopicLayer(isotopiclay);

            trunckedInchi = tmp[0];
        }

        if (trunckedInchi.contains("/t")) {
            String[] tmp = trunckedInchi.split("/t");

            if (tmp.length != 2) {
                this.setValidity(false);
                return;
            }

            InchiLayer tetlay = new InchiLayer('t', tmp[1]);
            this.setTetraStereoLayer(tetlay);

            trunckedInchi = tmp[0];
        }

        if (trunckedInchi.contains("/b")) {
            String[] tmp = trunckedInchi.split("/b");

            if (tmp.length != 2) {
                this.setValidity(false);
                return;
            }

            InchiLayer dblay = new InchiLayer('b', tmp[1]);
            this.setDbStereoLayer(dblay);

            trunckedInchi = tmp[0];
        }

        if (trunckedInchi.contains("/p")) {
            String[] tmp = trunckedInchi.split("/p");

            if (tmp.length != 2 || tmp[1].contains("/")) {
                this.setValidity(false);
                return;
            }

            InchiLayer protonlay = new InchiLayer('p', tmp[1]);
            this.setProtonationLayer(protonlay);

            trunckedInchi = tmp[0];
        }

        if (trunckedInchi.contains("/q")) {
            String[] tmp = trunckedInchi.split("/q");

            if (tmp.length != 2 || tmp[1].contains("/")) {
                this.setValidity(false);
                return;
            }

            InchiLayer chargeLay = new InchiLayer('q', tmp[1]);
            this.setChargeLayer(chargeLay);

            trunckedInchi = tmp[0];
        }

        if (trunckedInchi.contains("/h")) {
            String[] tmp = trunckedInchi.split("/h");

            if (tmp.length != 2 || tmp[1].contains("/")) {
                this.setValidity(false);
                return;
            }

            InchiLayer hlay = new InchiLayer('h', tmp[1]);
            this.sethLayer(hlay);

            trunckedInchi = tmp[0];
        }

        if (trunckedInchi.contains("/c")) {
            String[] tmp = trunckedInchi.split("/c");

            if (tmp.length != 2 || tmp[1].contains("/")) {
                this.setValidity(false);
                return;
            }

            InchiLayer connect = new InchiLayer('c', tmp[1]);
            this.setConnectivity(connect);

            trunckedInchi = tmp[0];
        }

        if (!trunckedInchi.equals("/")) {
            String formula = trunckedInchi.replaceAll("/", "");

            this.setFormulaLayer(formula);
        }

    }

    /**
     * Retrieves the real formula of the compound using formula and protonation
     * layers
     *
     * @return the real chemical formula
     */
    public String getRealFormula() {
        if (this.getProtonationLayer() != null) {

            String protonState = this.getProtonationLayer().getValue();

            String inchiFormula = this.formulaLayer, realFormula = inchiFormula;
            String sign = "", value = "0";

            Matcher m;
            m = Pattern.compile("^([+-])(\\d+)$").matcher(protonState);
            if (m.matches()) {
                sign = m.group(1);
                value = m.group(2);
            }

            m = Pattern.compile(".*H(\\d*).*").matcher(inchiFormula);
            if (m.matches()) {
                int realHNumber;
                String hnumber;
                if (m.group(1).isEmpty()) {
                    hnumber = "1";
                } else {
                    hnumber = m.group(1);
                }

                if (sign.equals("+")) {
                    realHNumber = Integer.parseInt(hnumber)
                            + Integer.parseInt(value);
                } else {
                    realHNumber = Integer.parseInt(hnumber)
                            - Integer.parseInt(value);
                }

                if (realHNumber == 0) {
                    realFormula = realFormula.replaceAll("H" + m.group(1), "");
                } else {
                    realFormula = realFormula.replaceAll("H" + m.group(1), "H"
                            + realHNumber);
                }

            } else {
                if (sign.equals("+") && !value.equals("1")) {
                    realFormula = realFormula + "H" + value;
                } else if (sign.equals("+")) {
                    realFormula = realFormula + "H";
                }
            }

            return realFormula;

        } else {
            return this.getFormulaLayer();
        }
    }

    /**
     * computes the real charge of the compounds using the charge and
     * protonation layers
     *
     * @return the charge as a positive or negative integer
     */
    public int getAbsoluteCharge() {

        Matcher m;
        int charge = 0, protonation = 0;

        if (this.getProtonationLayer() != null) {
            String protonState = this.getProtonationLayer().getValue();
            String protonationSign = "+", protonationValue = "0";

            m = Pattern.compile("^([+-])(\\d+)$").matcher(protonState);
            if (m.matches()) {
                protonationSign = m.group(1);
                protonationValue = m.group(2);
            }

            if (protonationSign.equals("+")) {
                protonation = Integer.parseInt(protonationValue);
            } else {
                protonation = -Integer.parseInt(protonationValue);
            }

        }

        if (this.getChargeLayer() != null) {

            String chargeState = this.getChargeLayer().getValue();
            String chargeSign = "+", chargeValue = "0";

            m = Pattern.compile("^([+-])(\\d+)$").matcher(chargeState);
            if (m.matches()) {
                chargeSign = m.group(1);
                chargeValue = m.group(2);
            }

            if (chargeSign.equals("+")) {
                charge = Integer.parseInt(chargeValue);
            } else {
                charge = -Integer.parseInt(chargeValue);
            }

        }
        return charge + protonation;

    }

    /**
     * Computes the exact mass of the compound using the real formula and the
     * calculated charge
     *
     * @return the Exact mass of the molecule as a string
     */
    public String computeChargedExactMass() {

        IMolecularFormula molecularFormula = MolecularFormulaManipulator.
				getMolecularFormula(this.getRealFormula(), DefaultChemObjectBuilder.getInstance());
        int charge = getAbsoluteCharge();


        if (molecularFormula != null) {
            double mass = MolecularFormulaManipulator.getMajorIsotopeMass(molecularFormula);
            mass = mass * 10000000;
            mass = Math.round(mass);
            mass = mass / 10000000;

            // System.out.println("mass sans electron: "+mass );

            double e = 0.0005486;
            double exact = mass - charge * e;

            return (Double.toString(exact));

        } else {

            return "0.0000000";
        }

    }

    /**
     * Computes the Average (Molecular) mass of the compound using the real
     * formula
     *
     * @return the Average (Molecular) mass of the molecule as a string
     */
    public String computeAverageMass() {

        IMolecularFormula molecularFormula = MolecularFormulaManipulator.
				getMolecularFormula(this.getRealFormula(), DefaultChemObjectBuilder.getInstance());


        if (molecularFormula != null) {
            double mass = MolecularFormulaManipulator.getNaturalExactMass(molecularFormula);
            mass = mass * 10000000;
            mass = Math.round(mass);
            mass = mass / 10000000;

            return (Double.toString(mass));

        } else {

            return "0.0000000";
        }
    }

    /**
     * Display in System.out the description of all the layer contained in this
     * {@link fr.inrae.toulouse.metexplore.met4j_chemUtils.chemicalStructures.InChI}
     */
    public void displayLayers() {
        System.out.println("initial String: " + this.inchiString);
        System.out.println("Version: " + this.getVersion());
        if (this.isStandard()) {
            System.out.println("Standard: True");
        } else {
            System.out.println("Standard: False");
        }
        System.out.println("Formula: " + this.getFormulaLayer());
        if (this.getConnectivity() != null) {
            System.out.println("connectivity: "
                    + this.getConnectivity().toString());
        }
        if (this.gethLayer() != null) {
            System.out
                    .println("hydrogen layer: " + this.gethLayer().toString());
        }
        if (this.getChargeLayer() != null) {
            System.out.println("charge layer: "
                    + this.getChargeLayer().toString());
        }
        if (this.getProtonationLayer() != null) {
            System.out.println("protonation layer: "
                    + this.getProtonationLayer().toString());
        }
        if (this.getDbStereoLayer() != null) {
            System.out.println("double bond layer: "
                    + this.getDbStereoLayer().toString());
        }
        if (this.getTetraStereoLayer() != null) {
            System.out.println("tetrahedral stereo: "
                    + this.getTetraStereoLayer().toString());
        }
        if (this.getIsotopicLayer() != null) {
            System.out.println("isotopic: "
                    + this.getIsotopicLayer().toString());
        }
        if (this.getFixedLayer() != null) {
            System.out.println("fixed H: " + this.getFixedLayer().toString());
        }
        if (this.getReconnectedLayer() != null) {
            System.out.println("Reconnected Layer: "
                    + this.getReconnectedLayer().toString());
        }
    }

    /**
     * Default InChI comparison. By default this compares:
     * <ul>
     * <li>{@link #version}
     * <li>{@link #formulaLayer}
     * <li>{@link #connectivity}
     * <li>{@link #hLayer}
     * <li>{@link #chargeLayer}
     * <li>{@link #protonationLayer}
     * <li>{@link #dbStereoLayer}
     * <li>{@link #tetraStereoLayer}
     * </ul>
     *
     * @param compared the compared Inchi
     * @return true if the two {@link fr.inrae.toulouse.metexplore.met4j_chemUtils.chemicalStructures.InChI} are the same considering their
     * {@link #dbStereoLayer} and {@link #tetraStereoLayer}
     */
    public boolean equals(InChI compared) {
        return this.equals(compared, "s");
    }

    /**
     * Test if two {@link fr.inrae.toulouse.metexplore.met4j_chemUtils.chemicalStructures.InChI} are equals, considering the passed layer
     * identifier
     *
     * @param compared the compared Inchi
     * @param Layers   the layers to compare. Can be a combination of these layer
     *                 identifiers:
     *                 <ul>
     *                 <li>s : compare the Stereochemistry layers:
     *                 {@link #dbStereoLayer} and {@link #tetraStereoLayer}
     *                 <li>i : compare the isotopic Layer :{@link #isotopicLayer}
     *                 <li>f : compare the fixed layer: {@link #fixedLayer}
     *                 </ul>
     * @return true if the compared layers are the same, false otherwise
     */
    public boolean equals(InChI compared, String Layers) {

        if (this.getVersion() != compared.getVersion()) {
            return false;
        }

        if (!this.getFormulaLayer().equals(compared.getFormulaLayer())) {
            return false;
        }

        // test si un seul des deux est null
        if ((this.getConnectivity() == null && compared.getConnectivity() != null)
                || (this.getConnectivity() != null && compared
                .getConnectivity() == null)) {
            return false;
            // test si les deux ne sont pas null simultanément et test fction
            // equals
        } else if (!(this.getConnectivity() == null && compared
                .getConnectivity() == null)
                && !this.getConnectivity().equals(compared.getConnectivity())) {
            return false;
        }

        if ((this.gethLayer() == null && compared.gethLayer() != null)
                || (this.gethLayer() != null && compared.gethLayer() == null)) {
            return false;
            // test si les deux ne sont pas null simultanément et test fction
            // equals
        } else if (!(this.gethLayer() == null && compared.gethLayer() == null)
                && !this.gethLayer().equals(compared.gethLayer())) {
            return false;
        }

        if ((this.getProtonationLayer() == null && compared
                .getProtonationLayer() != null)
                || (this.getProtonationLayer() != null && compared
                .getProtonationLayer() == null)) {
            return false;
            // test si les deux ne sont pas null simultanément et test fction
            // equals
        } else if (!(this.getProtonationLayer() == null && compared
                .getProtonationLayer() == null)
                && !this.getProtonationLayer().equals(
                compared.getProtonationLayer())) {
            return false;
        }

        if ((this.getChargeLayer() == null && compared.getChargeLayer() != null)
                || (this.getChargeLayer() != null && compared.getChargeLayer() == null)) {
            return false;
            // test si les deux ne sont pas null simultanément et test fction
            // equals
        } else if (!(this.getChargeLayer() == null && compared.getChargeLayer() == null)
                && !this.getChargeLayer().equals(compared.getChargeLayer())) {
            return false;
        }

        if (!Layers.isEmpty()) {

            Set<Character> mySet = new HashSet<Character>();

            for (char c : Layers.toCharArray()) {
                mySet.add(c);
            }

            if (mySet.contains('s')) {

                if ((this.getDbStereoLayer() == null && compared
                        .getDbStereoLayer() != null)
                        || (this.getDbStereoLayer() != null && compared
                        .getDbStereoLayer() == null)) {
                    return false;
                    // test si les deux ne sont pas null simultanément et test
                    // fction equals
                } else if (!(this.getDbStereoLayer() == null && compared
                        .getDbStereoLayer() == null)
                        && !this.getDbStereoLayer().equals(
                        compared.getDbStereoLayer())) {
                    return false;
                }

                if ((this.getTetraStereoLayer() == null && compared
                        .getTetraStereoLayer() != null)
                        || (this.getTetraStereoLayer() != null && compared
                        .getTetraStereoLayer() == null)) {
                    return false;
                    // test si les deux ne sont pas null simultanément et test
                    // fction equals
                } else if (!(this.getTetraStereoLayer() == null && compared
                        .getTetraStereoLayer() == null)
                        && !this.getTetraStereoLayer().equals(
                        compared.getTetraStereoLayer())) {
                    return false;
                }
            }

            if (mySet.contains('i')) {

                if ((this.getIsotopicLayer() == null && compared
                        .getIsotopicLayer() != null)
                        || (this.getIsotopicLayer() != null && compared
                        .getIsotopicLayer() == null)) {
                    return false;
                    // test si les deux ne sont pas null simultanément et test
                    // fction equals
                } else if (!(this.getIsotopicLayer() == null && compared
                        .getIsotopicLayer() == null)
                        && !this.getIsotopicLayer().equals(
                        compared.getIsotopicLayer())) {
                    return false;
                }
            }

            if (mySet.contains('f')) {

                if ((this.getFixedLayer() == null && compared.getFixedLayer() != null)
                        || (this.getFixedLayer() != null && compared
                        .getFixedLayer() == null)) {
                    return false;
                    // test si les deux ne sont pas null simultanément et test
                    // fction equals
                } else if (!(this.getFixedLayer() == null && compared
                        .getFixedLayer() == null)
                        && !this.getFixedLayer().equals(
                        compared.getFixedLayer())) {
                    return false;
                }
            }

        }

        return true;
    }

    /*
     * Getters
     */

    /** {@inheritDoc} */
    @Override
    public boolean isValid() {

        return this.validity;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return this.inchiString;
    }

    /**
     * <p>Getter for the field <code>inchiString</code>.</p>
     *
     * @return the inchiString
     */
    public String getInchiString() {
        return inchiString;
    }

    /**
     * <p>Setter for the field <code>inchiString</code>.</p>
     *
     * @param inchiString the inchiString to set
     */
    public void setInchiString(String inchiString) {
        this.inchiString = inchiString;
    }

    /**
     * <p>Getter for the field <code>version</code>.</p>
     *
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * <p>Setter for the field <code>version</code>.</p>
     *
     * @param version the version to set
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * <p>isStandard.</p>
     *
     * @return the standard
     */
    public boolean isStandard() {
        return standard;
    }

    /**
     * <p>Setter for the field <code>standard</code>.</p>
     *
     * @param standard the standard to set
     */
    public void setStandard(boolean standard) {
        this.standard = standard;
    }

    /**
     * <p>Getter for the field <code>formulaLayer</code>.</p>
     *
     * @return the formulaLayer
     */
    public String getFormulaLayer() {
        return formulaLayer;
    }

    /**
     * <p>Setter for the field <code>formulaLayer</code>.</p>
     *
     * @param formulaLayer the formulaLayer to set
     */
    public void setFormulaLayer(String formulaLayer) {
        this.formulaLayer = formulaLayer;
    }

    /**
     * <p>Getter for the field <code>connectivity</code>.</p>
     *
     * @return the connectivity
     */
    public InchiLayer getConnectivity() {
        return connectivity;
    }

    /**
     * <p>Setter for the field <code>connectivity</code>.</p>
     *
     * @param connectivity the connectivity to set
     */
    public void setConnectivity(InchiLayer connectivity) {
        this.connectivity = connectivity;
    }

    /**
     * <p>Getter for the field <code>hLayer</code>.</p>
     *
     * @return the hLayer
     */
    public InchiLayer gethLayer() {
        return hLayer;
    }

    /**
     * <p>Setter for the field <code>hLayer</code>.</p>
     *
     * @param hLayer the hLayer to set
     */
    public void sethLayer(InchiLayer hLayer) {
        this.hLayer = hLayer;
    }

    /**
     * <p>Getter for the field <code>chargeLayer</code>.</p>
     *
     * @return the chargeLayer
     */
    public InchiLayer getChargeLayer() {
        return chargeLayer;
    }

    /**
     * <p>Setter for the field <code>chargeLayer</code>.</p>
     *
     * @param chargeLayer the chargeLayer to set
     */
    public void setChargeLayer(InchiLayer chargeLayer) {
        this.chargeLayer = chargeLayer;
    }

    /**
     * <p>Getter for the field <code>protonationLayer</code>.</p>
     *
     * @return the protonationLayer
     */
    public InchiLayer getProtonationLayer() {
        return protonationLayer;
    }

    /**
     * <p>Setter for the field <code>protonationLayer</code>.</p>
     *
     * @param protonationLayer the protonationLayer to set
     */
    public void setProtonationLayer(InchiLayer protonationLayer) {
        this.protonationLayer = protonationLayer;
    }

    /**
     * <p>Getter for the field <code>dbStereoLayer</code>.</p>
     *
     * @return the dbStereoLayer
     */
    public InchiLayer getDbStereoLayer() {
        return dbStereoLayer;
    }

    /**
     * <p>Setter for the field <code>dbStereoLayer</code>.</p>
     *
     * @param dbStereoLayer the dbStereoLayer to set
     */
    public void setDbStereoLayer(InchiLayer dbStereoLayer) {
        this.dbStereoLayer = dbStereoLayer;
    }

    /**
     * <p>Getter for the field <code>tetraStereoLayer</code>.</p>
     *
     * @return the tetraStereoLayer
     */
    public InchiLayer getTetraStereoLayer() {
        return tetraStereoLayer;
    }

    /**
     * <p>Setter for the field <code>tetraStereoLayer</code>.</p>
     *
     * @param tetraStereoLayer the tetraStereoLayer to set
     */
    public void setTetraStereoLayer(InchiLayer tetraStereoLayer) {
        this.tetraStereoLayer = tetraStereoLayer;
    }

    /**
     * <p>Getter for the field <code>isotopicLayer</code>.</p>
     *
     * @return the isotopicLayer
     */
    public InchiLayer getIsotopicLayer() {
        return isotopicLayer;
    }

    /**
     * <p>Setter for the field <code>isotopicLayer</code>.</p>
     *
     * @param isotopicLayer the isotopicLayer to set
     */
    public void setIsotopicLayer(InchiLayer isotopicLayer) {
        this.isotopicLayer = isotopicLayer;
    }

    /**
     * <p>Getter for the field <code>fixedLayer</code>.</p>
     *
     * @return the fixedLayer
     */
    public InchiLayer getFixedLayer() {
        return fixedLayer;
    }

    /**
     * <p>Setter for the field <code>fixedLayer</code>.</p>
     *
     * @param fixedLayer the fixedLayer to set
     */
    public void setFixedLayer(InchiLayer fixedLayer) {
        this.fixedLayer = fixedLayer;
    }

    /**
     * <p>Getter for the field <code>reconnectedLayer</code>.</p>
     *
     * @return the reconnectedLayer
     */
    public InchiLayer getReconnectedLayer() {
        return reconnectedLayer;
    }

    /**
     * <p>Setter for the field <code>reconnectedLayer</code>.</p>
     *
     * @param reconnectedLayer the reconnectedLayer to set
     */
    public void setReconnectedLayer(InchiLayer reconnectedLayer) {
        this.reconnectedLayer = reconnectedLayer;
    }

}
