package fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.sbml.jsbml.Annotation;
import org.sbml.jsbml.CVTerm;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.UniqueNamedSBase;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.dataTags.AdditionalDataTag;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML1Compatible;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML2Compatible;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML3Compatible;

/**
 * This class is used to parse the annotation of every SBML element.
 * {@link BioReaction} are treated separately because they have specific
 * of annotations linked to them. {@link BioCompartment} are also separated
 * because they do not extend the {@link BioPhysicalEntity} class
 * 
 * @author Benjamin
 * @since 3.0
 */
public class AnnotationParser implements PackageParser, AdditionalDataTag, ReaderSBML1Compatible, ReaderSBML2Compatible,
		ReaderSBML3Compatible {

	public static final String ORIGIN = "SBML";

	/**
	 * The Jsbml Model
	 */
	public Model model;
	/**
	 * The BioNetwork
	 */
	public BioNetwork bionetwork;

	/**
	 * The user defined annotation pattern used in the regular expression of
	 * this class
	 */
	public String annotationPattern;

	/**
	 * The default annotation pattern:
	 * <ul>
	 * <li>http://identifiers.org/([^/]+)/(.*)
	 * </ul>
	 * The first parenthesis group is
	 */
	public static final String defaultAnnotationPattern = "http://identifiers.org/([^/]+)/(.*)";

	/**
	 * Constructor
	 * 
	 * @param useDefault
	 *            true to use the {@link #defaultAnnotationPattern}
	 */
	public AnnotationParser(boolean useDefault) {
		if (useDefault)
			this.setAnnotationPattern(defaultAnnotationPattern);
	}

	/**
	 * Constructor
	 * 
	 * @param pattern
	 *            the user defined pattern
	 */
	public AnnotationParser(String pattern) {
		this.annotationPattern = pattern;
	}

	public String getAssociatedPackageName() {
		return "annot";
	}

	public boolean isPackageUseableOnModel(Model model) {
		return true;
	}

	/**
	 * Parse all model entities to retrieve their annotations and extract the
	 * desired information
	 */
	public void parseModel(Model model, BioNetwork bionetwork) {
		System.err.println("Starting " + this.getAssociatedPackageName() + " plugin...");

		this.setBionetwork(bionetwork);
		this.setModel(model);

		this.parseAnnotation(bionetwork,model.getAnnotation());
		
		this.parseSbmlAnnotations(bionetwork.getReactionsView());
		this.parseSbmlAnnotations(bionetwork.getMetabolitesView());
		this.parseSbmlAnnotations(bionetwork.getProteinsView());
		this.parseSbmlAnnotations(bionetwork.getEnzymesView());
		this.parseSbmlAnnotations(bionetwork.getCompartmentsView());
	}

	/**
	 * 
	 *            One of the different lists present in the {@link BioNetwork}
	 *            class
	 */
	private void parseSbmlAnnotations(BioCollection<?> collection) {

		for (BioEntity entry : collection) {

			UniqueNamedSBase sbase = this.getModel().findUniqueNamedSBase(entry.getId());

			if (sbase != null && !sbase.getAnnotation().isEmpty() && sbase.hasValidAnnotation()) {

				this.parseAnnotation(entry, sbase.getAnnotation());

			}
		}
	}

	/**
	 * Parse entity's annotation to extract external identifiers
	 * 
	 * @param annot
	 *            the SBML annotation element
	 */
	private void parseAnnotation(BioEntity ent, Annotation annot) {

		Matcher m;
		for (CVTerm cv : annot.getListOfCVTerms()) {
			
			String qual = "NA";
			
			if(cv.isBiologicalQualifier()) {
			
				qual = cv.getBiologicalQualifierType().getElementNameEquivalent();
			}
			else {
				qual = cv.getModelQualifierType().getElementNameEquivalent();
			}
			
			for (String ress : cv.getResources()) {
				if (this.getAnnotationPattern() != null
						&& (m = Pattern.compile(this.getAnnotationPattern()).matcher(ress)).matches()) {

					if (m.group(1).equalsIgnoreCase("ec-code") && ent instanceof BioReaction) {
						((BioReaction) ent).setEcNumber(m.group(2));
					}
					ent.addRef(m.group(1), m.group(2), 1, qual, ORIGIN);
				}
			}
		}

		String nonrdfAnnot = annot.getNonRDFannotationAsString();
		if (ent instanceof BioMetabolite && nonrdfAnnot != null && !nonrdfAnnot.isEmpty()) {

			String specialInchiPattern = "<in:inchi xmlns:in=\"([^\"]+)\">InChI=([^<]+)</in:inchi>";

			m = Pattern.compile(specialInchiPattern, Pattern.DOTALL).matcher(nonrdfAnnot);

			while (m.find()) {

				if (m.group(1).equalsIgnoreCase("http://biomodels.net/inchi")) {
					if (!ent.hasRef("inchi", m.group(2))) {
						ent.addRef("inchi", m.group(2), 1, "is", ORIGIN);
					}
				}
			}
		}
	}

	/**
	 * @return the model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * @param model
	 *            the model to set
	 */
	public void setModel(Model model) {
		this.model = model;
	}

	/**
	 * @return the bionetwork
	 */
	public BioNetwork getBionetwork() {
		return bionetwork;
	}

	/**
	 * @param bionetwork
	 *            the bionetwork to set
	 */
	public void setBionetwork(BioNetwork bionetwork) {
		this.bionetwork = bionetwork;
	}

	/**
	 * @return the annotationPattern
	 */
	public String getAnnotationPattern() {
		return annotationPattern;
	}

	/**
	 * @param annotationPattern
	 *            the annotationPattern to set
	 */
	public void setAnnotationPattern(String annotationPattern) {
		this.annotationPattern = annotationPattern;
	}

}