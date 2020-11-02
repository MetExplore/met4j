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

package fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction;

import java.util.HashSet;
import java.util.Set;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.GenericAttributes;


public class ReactionAttributes extends GenericAttributes {

    public static final String LOWER_BOUND = "flux_lower_bound";
    public static final String UPPER_BOUND = "flux_upper_bound";
    public static final String SCORE = "score";
    public static final String STATUS = "status";
    public static final String FAST = "fast";
    public static final String KINETIC_FORMULA = "kinetic_formula";
    public static final String FLUX_PARAMS = "flux_params";
    public static final String ADDITIONAL_FLUX_PARAMS = "additional_flux_params";
    public static final String SUBSYSTEM = "SUBSYSTEM";
    public static final String EC_NUMBER = "EC_NUMBER";
    public static final String GENE_ASSOCIATION = "GENE_ASSOCIATION";
    public static final String HOLE = "hole";
    public static final String SIDE_COMPOUNDS = "side_compounds";
    public static final String SPONTANEOUS = "spontaneous";


    public static final double MIN_LOWER_BOUND = Flux.FLUXMIN;
    public static final double MAX_UPPER_BOUND = Flux.FLUXMAX;

    /**
     * get lower bound
     *
     * @param r
     * @return
     */
    public static Flux getLowerBound(BioReaction r) {

        return (Flux) r.getAttribute(LOWER_BOUND);

    }

    /**
     * get upper bound
     *
     * @param r
     * @return
     */
    public static Flux getUpperBound(BioReaction r) {

        return (Flux) r.getAttribute(UPPER_BOUND);
    }

    /**
     * set lower bound
     *
     * @param r
     * @param val
     */
    public static void setLowerBound(BioReaction r, Flux val) {
        r.setAttribute(LOWER_BOUND, val);
    }

    /**
     * set upper bound
     *
     * @param r
     * @param val
     */
    public static void setUpperBound(BioReaction r, Flux val) {
        r.setAttribute(UPPER_BOUND, val);
    }


    /**
     * Get Score
     *
     * @param r
     * @return
     */
    public static Double getScore(BioReaction r) {

        return (Double) r.getAttribute(SCORE);
    }

    /**
     * Set score
     *
     * @param r
     * @param score
     */
    public static void setScore(BioReaction r, Double score) {

        r.setAttribute(SCORE, score);
    }

    /**
     * Get status
     *
     * @param r
     * @return
     */
    public static String getStatus(BioReaction r) {

        return (String) r.getAttribute(STATUS);
    }

    /**
     * Set status
     *
     * @param r
     * @param status
     */
    public static void setStatus(BioReaction r, String status) {

        r.setAttribute(STATUS, status);
    }


    /**
     * get fast attribute
     *
     * @param r
     * @return
     */
    public static boolean getFast(BioReaction r) {
        return r.getAttribute(FAST) != null ? (Boolean) r.getAttribute(FAST) : false;
    }

    /**
     * Set spontaneous attribute
     *
     * @param r
     * @param flag
     */
    public static void setSpontaneous(BioReaction r, boolean flag) {
        r.setAttribute(SPONTANEOUS, flag);
    }

    /**
     * get spontaneous attribute
     *
     * @param r
     * @return
     */
    public static boolean getSpontaneous(BioReaction r) {
        return r.getAttribute(SPONTANEOUS) != null ? (Boolean) r.getAttribute(SPONTANEOUS) : false;
    }

    /**
     * Set fast attribute
     *
     * @param r
     * @param flag
     */
    public static void setFast(BioReaction r, boolean flag) {
        r.setAttribute(FAST, flag);
    }

    /**
     * get kinetic formula
     *
     * @param r
     * @return
     */
    public static String getKineticFormula(BioReaction r) {
        return (String) r.getAttribute(KINETIC_FORMULA);
    }

    /**
     * Set fast attribute
     */
    public static void setKineticFormula(BioReaction r, String k) {
        r.setAttribute(KINETIC_FORMULA, k);
    }

    /**
     * Get flux params
     *
     * @param r
     * @return
     */
    public static FluxCollection getFluxParams(BioReaction r) {
        return (FluxCollection) r.getAttribute(FLUX_PARAMS);
    }

    /**
     * Set Flux params
     *
     * @param r
     * @param c
     */
    public static void setFluxParams(BioReaction r, FluxCollection c) {
        r.setAttribute(FLUX_PARAMS, c);
    }


    /**
     * Get addtional flux params
     *
     * @param r
     * @return
     */
    public static FluxCollection getAdditionalFluxParams(BioReaction r) {
        return (FluxCollection) r.getAttribute(ADDITIONAL_FLUX_PARAMS);
    }

    /**
     * Set additional Flux params
     *
     * @param r
     * @param c
     */
    public static void setAdditionalFluxParams(BioReaction r, FluxCollection c) {
        r.setAttribute(ADDITIONAL_FLUX_PARAMS, c);
    }


    /**
     * Add a flux
     *
     * @param r
     * @param f
     */
    public static void addFlux(BioReaction r, Flux f) {

        if (getFluxParams(r) == null) {
            setFluxParams(r, new FluxCollection());
        }

        getFluxParams(r).add(f);

    }

    /**
     * get flux from its id
     *
     * @param r
     * @param id
     * @return
     */
    public static Flux getFlux(BioReaction r, String id) {

        if (getFluxParams(r) == null) {
            return null;
        }

        return getFluxParams(r).get(id);


    }

    /**
     * get the hole attribute of a metabolite
     *
     * @param r
     * @return
     */
    public static Boolean getHole(BioReaction r) {

        Boolean flag = false;

        if (r.getAttribute(HOLE) != null) {
            flag = (Boolean) r.getAttribute(HOLE);
        }

        return flag;
    }

    /**
     * Set hole attribute
     */
    public static void setHole(BioReaction r, Boolean flag) {
        r.setAttribute(HOLE, flag);
    }

    /**
     * set side compounds
     */
    public static void setSideCompounds(BioReaction e, Set<String> sideCompounds) {

        e.setAttribute(SIDE_COMPOUNDS, sideCompounds);

    }

    /**
     * add side compound
     */
    public static void addSideCompound(BioReaction e, String id) {

        if (getSideCompounds(e) == null) {
            setSideCompounds(e, new HashSet<String>());
        }

        getSideCompounds(e).add(id);

    }

    /**
     * get side compounds
     *
     * @param e
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Set<String> getSideCompounds(BioReaction e) {

        return (Set<String>) e.getAttribute(SIDE_COMPOUNDS);

    }

}
