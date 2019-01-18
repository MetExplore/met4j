package fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Annotation;
import org.sbml.jsbml.CVTerm;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.UniqueNamedSBase;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioRef;
import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEnzyme;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.dataTags.AdditionalDataTag;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML1Compatible;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML2Compatible;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML3Compatible;
import static fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils.isVoid;


/**
 * This class is used to parse the annotation of every SBML element.
 * {@link BioChemicalReaction} are treated separately because they have
 * specific of annotations linked to them. {@link BioCompartment} are also
 * separated because they do not extend the {@link BioPhysicalEntity} class
 * 
 * @author Benjamin
 * @since 3.0
 */
public class AnnotationParser implements PackageParser, AdditionalDataTag,
		ReaderSBML1Compatible, ReaderSBML2Compatible, ReaderSBML3Compatible {
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
		System.err.println("Starting " + this.getAssociatedPackageName()
				+ " plugin...");

		this.setBionetwork(bionetwork);
		this.setModel(model);

		this.getModelData(model, bionetwork);

		this.getSBMLAnnotations(bionetwork.getReactionsView());
		this.getSBMLAnnotations(bionetwork.getMetabolitesView());
		this.getSBMLAnnotations(bionetwork.getProteinsView());
		this.getSBMLAnnotations(bionetwork.getEnzymesView());
		this.getSBMLAnnotations(bionetwork.getCompartmentsView());
	}

	/**
	 * 
	 * @param list
	 *            One of the different lists present in the {@link BioNetwork}
	 *            class
	 */
	public void getSBMLAnnotations(BioCollection<?> collection) {
		
		for (BioEntity entry : collection) {
			
			
			UniqueNamedSBase sbase = this.getModel().findUniqueNamedSBase(
					entry.getId());

			if (sbase != null && !sbase.getAnnotation().isEmpty()
					&& sbase.hasValidAnnotation()) {

				switch (entry.getClass().getSimpleName()) {
				case "BioCompartment":
					this.parseAnnotation((BioCompartment) entry,
							sbase.getAnnotation());
					break;
				case "BioReaction":
					this.parseAnnotation(
							(BioReaction) entry,
							sbase.getAnnotation());
					break;
				case "BioEnzyme":
					this.parseAnnotation((BioEnzyme) entry,
							sbase.getAnnotation());
					break;
				case "BioProtein":
					this.parseAnnotation((BioProtein) entry,
							sbase.getAnnotation());
					break;
				case "BioMetabolite":
					this.parseAnnotation((BioMetabolite) entry,
							sbase.getAnnotation());
					break;
				}
			}
		}
	}

	/**
	 * Parse the compartment's annotation to extract external identifiers
	 * 
	 * @param comp
	 *            the compartment
	 * @param annot
	 *            the SBML annotation element
	 */
	private void parseAnnotation(BioCompartment comp, Annotation annot) {

		Matcher m;

		for (CVTerm cv : annot.getListOfCVTerms()) {
			String bioQual = cv.getBiologicalQualifierType()
					.getElementNameEquivalent();

			for (String ress : cv.getResources()) {
				if (this.getAnnotationPattern() != null
						&& (m = Pattern.compile(this.getAnnotationPattern())
								.matcher(ress)).matches()) {
					comp.addRef(m.group(1), m.group(2), 1, bioQual, "SBML File");
				}
			}
		}
	}

	/**
	 * Parse the reaction's annotation to extract related pubmed references, EC
	 * number and external identifiers
	 * 
	 * @param rxn
	 *            The reaction
	 * @param annot
	 *            the SBML annotation element
	 */
	private void parseAnnotation(BioReaction rxn, Annotation annot) {

		Matcher m;
		for (CVTerm cv : annot.getListOfCVTerms()) {
			String bioQual = cv.getBiologicalQualifierType()
					.getElementNameEquivalent();

			for (String ress : cv.getResources()) {
				if (this.getAnnotationPattern() != null
						&& (m = Pattern.compile(this.getAnnotationPattern())
								.matcher(ress)).matches()) {

					switch (bioQual) {
					case "isDescribedBy":
						if (m.group(1).equalsIgnoreCase("pubmed")) {
							BioRef ref = new BioRef("pubmed", "pubmed", m.group(2), 1);
							rxn.addRef(ref);
						}
						break;
					// case "isPartOf":
					// // TODO See if this can contain pathway annotation
					//
					// break;
					default:
						if (m.group(1).equalsIgnoreCase("ec-code")) {
							rxn.setEcNumber(m.group(2));
						} else if (!rxn.hasRef(m.group(1), m.group(2))) {
							rxn.addRef(m.group(1), m.group(2), 1, bioQual,
									"SBML File");
						}
						break;
					}
				}
			}
		}
	}

	/**
	 * Retrieves all identifiers of BioPysical entities (metabolite, protein and
	 * complex) when they respect RDF and MIRIAM specifications. The only
	 * non-rdf annotations retrieved are the metabolites' inchis when they are
	 * in an 'in:inchi' tag. This tag has to have "http://biomodels.net/inchi"
	 * namespace to be valid
	 * 
	 * @param entity
	 *            the physical entity
	 * @param annot
	 *            the SBML annotation element
	 */
	private void parseAnnotation(BioPhysicalEntity entity, Annotation annot) {

		Matcher m;

		for (CVTerm cv : annot.getListOfCVTerms()) {
			String bioQual = cv.getBiologicalQualifierType()
					.getElementNameEquivalent();

			for (String ress : cv.getResources()) {

				if (this.getAnnotationPattern() != null
						&& (m = Pattern.compile(this.getAnnotationPattern())
								.matcher(ress)).matches()) {
					if (!entity.hasRef(m.group(1), m.group(2))) {
						entity.addRef(m.group(1), m.group(2), 1, bioQual,
								"SBML File");
					}
				}
			}
		}

		String nonrdfAnnot = annot.getNonRDFannotationAsString();
		if (!nonrdfAnnot.isEmpty()) {

			String specialInchiPattern = ".*?<in:inchi xmlns:in=\"([^\"]+)\">([^<]+)</in:inchi>.*";
			if ((m = Pattern.compile(specialInchiPattern).matcher(nonrdfAnnot))
					.matches()) {
				if (m.group(1).equalsIgnoreCase("http://biomodels.net/inchi")) {
					if (!entity.hasRef("inchi", m.group(2))) {
						entity.addRef("inchi", m.group(2), 1, "is", "SBML File");
					}
				}
			}
		}
	}

	/**
	 * Link the {@link #model}'s annotation to the bionetwork
	 * 
	 * @param model
	 *            the SBML model
	 * @param bionet
	 *            the Bionetwork
	 */
	public void getModelData(Model model, BioNetwork bionet) {
		if (!isVoid(model.getMetaId())) {
			try {
				
				BioRef ref = new BioRef("sbml", "sbmlAnnotation", model.getAnnotationString(), 1);
				bionet.addRef(ref);
				
//				bionet.setModelAnnot(new BioAnnotation(model.getMetaId(), model
//						.getAnnotationString()));
			} catch (XMLStreamException e) {
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