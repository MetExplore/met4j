package parsebionet.io.jsbml.writer.plugin;

import java.util.HashMap;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Annotation;
import org.sbml.jsbml.CVTerm;
import org.sbml.jsbml.CVTerm.Qualifier;
import org.sbml.jsbml.CVTerm.Type;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.UniqueNamedSBase;

import parsebionet.biodata.BioChemicalReaction;
import parsebionet.biodata.BioCompartment;
import parsebionet.biodata.BioEntity;
import parsebionet.biodata.BioGene;
import parsebionet.biodata.BioNetwork;
import parsebionet.biodata.BioPhysicalEntity;
import parsebionet.biodata.BioRef;
import parsebionet.io.jsbml.dataTags.AdditionalDataTag;
import parsebionet.io.jsbml.writer.plugin.tags.WriterSBML2Compatible;
import parsebionet.io.jsbml.writer.plugin.tags.WriterSBML3Compatible;
import parsebionet.utils.StringUtils;

/**
 * Creates MIRIAM annotation for the SBML entities of the model created by
 * {@link parsebionet.io.jsbml.writer.BionetworkToJsbml}
 * 
 * @author Benjamin
 * @since 3.0
 */
public class AnnotationWriter implements PackageWriter, WriterSBML2Compatible,
		WriterSBML3Compatible, AdditionalDataTag {

	/**
	 * The SBML model
	 */
	public Model model;
	/**
	 * the {@link BioNetwork}
	 */
	public BioNetwork bionetwork;

	/**
	 * The default URL pattern for the annotations
	 */
	public static final String defaluftURLpattern = "http://identifiers.org/";

	/**
	 * The user defined URL pattern for the annotations
	 */
	public String usedPattern;
	/**
	 * The separator between database and identifier in the annotation URL
	 */
	public char separator;

	/**
	 * Instantiate this Annotation Writer with {@link #defaluftURLpattern}:
	 * <ul>
	 * <li>{@value #defaluftURLpattern}
	 * </ul>
	 */
	public AnnotationWriter() {
		this.setSeparator('/');
		this.usedPattern = defaluftURLpattern;
	}

	/**
	 * Constructor
	 * 
	 * @param pattern
	 *            the user defined url pattern
	 */
	public AnnotationWriter(String pattern) {
		this.setUsedPattern(pattern);
		this.setSeparator('/');
	}

	/**
	 * Constructor
	 * 
	 * @param pattern
	 *            the user defined url pattern
	 * @param separator
	 *            the user defined separator
	 */
	public AnnotationWriter(String pattern, char separator) {
		this.setUsedPattern(pattern);
		this.setSeparator(separator);
	}

	@Override
	public String getAssociatedPackageName() {
		return "annot";
	}

	@Override
	public boolean isPackageUseableOnLvl(int lvl) {
		return true;
	}

	/**
	 * Parse the different HashMaps present in the BioNetwork and create
	 * annotations
	 */
	@Override
	public void parseBionetwork(Model model, BioNetwork bionetwork) {
		System.err.println("Generating Model Annotations...");

		this.setBionetwork(bionetwork);
		this.setModel(model);

		this.createModelAnnotation();
		this.createCompartAnnotations();
		this.createAnnotationFromBioEntities(this.getBionetwork()
				.getPhysicalEntityList());
		this.createAnnotationFromBioEntities(this.getBionetwork()
				.getBiochemicalReactionList());
		this.createAnnotationFromBioEntities(this.getBionetwork().getGeneList());
		this.createAnnotationFromBioEntities(this.getBionetwork()
				.getComplexList());
		this.createAnnotationFromBioEntities(this.getBionetwork()
				.getProteinList());
	}

	/**
	 * Create the model's annotation from the saved annotation if it exists
	 */
	private void createModelAnnotation() {
		if (this.getBionetwork().getModelAnnot() != null
				&& this.getBionetwork().getModelAnnot().getMetaId() != null) {

			try {
				this.getModel().setMetaId(
						this.getBionetwork().getModelAnnot().getMetaId());
				this.getModel().setAnnotation(
						new Annotation(this.getBionetwork().getModelAnnot()
								.getXMLasString()));
			} catch (XMLStreamException e) {

				AnnotationWriter.errorsAndWarnings.add("Unable to create Model annotation form the saved annotations");
			}
		}

	}

	/**
	 * Creates the compartments' annotations from their references
	 */
	private void createCompartAnnotations() {
		for (BioCompartment c : this.getBionetwork().getCompartments().values()) {
			Annotation annot = createAnnotationsFromRefs(c.getRefs());

			UniqueNamedSBase sbase = this.getModel().findUniqueNamedSBase(
					StringUtils.convertToSID(c.getId()));
			if (!annot.isEmpty() && sbase != null) {

				String metaid = model.getSBMLDocument().nextMetaId();
				sbase.setMetaId(metaid);
				annot.setAbout(metaid);

				sbase.setAnnotation(annot);
			}
		}

	}

	/**
	 * Loop through the list of entities and creates annotations from their set
	 * of references and, depending on the type of the entity, from several
	 * other attributes
	 * 
	 * @param entityList
	 *            the list of {@link BioEntity}
	 */
	private void createAnnotationFromBioEntities(
			HashMap<String, ? extends BioEntity> entityList) {
		for (BioEntity ent : entityList.values()) {

			UniqueNamedSBase sbase = this.getModel().findUniqueNamedSBase(
					StringUtils.convertToSID(ent.getId()));

			if (sbase != null) {

				Annotation annot = createAnnotationsFromRefs(ent.getRefs());

				switch (ent.getClass().getSimpleName()) {
				case "BioPhysicalEntity":
					this.getAdditionnalAnnotation((BioPhysicalEntity) ent,
							annot);
					break;
				case "BioChemicalReaction":
					this.getAdditionnalAnnotation((BioChemicalReaction) ent,
							annot);
					break;
				case "BioGene":
					this.getAdditionnalAnnotation((BioGene) ent, annot);
					break;
				}

				if (!annot.isEmpty()) {
					String metaid = model.getSBMLDocument().nextMetaId();
					sbase.setMetaId(metaid);
					annot.setAbout(metaid);

					sbase.setAnnotation(annot);
				}

			}

		}
	}

	/**
	 * Creates the appropriate biological annotations from an entity's external
	 * references.
	 * 
	 * @param setOfRef
	 *            a set of extenal references coming from an object of the
	 *            {@link BioNetwork}
	 * @return The SBML Annotation object
	 */
	private Annotation createAnnotationsFromRefs(
			HashMap<String, Set<BioRef>> setOfRef) {
		Annotation annot = new Annotation();

		for (Set<BioRef> refs : setOfRef.values()) {
			addingLoop: for (BioRef r : refs) {

				for (CVTerm innerCV : annot.getListOfCVTerms()) {
					if (innerCV.getBiologicalQualifierType().compareTo(
							Qualifier.valueOf("BQB_"
									+ r.logicallink.toUpperCase())) == 0) {
						innerCV.addResource(usedPattern + r.getDbName()
								+ separator + r.id);
						continue addingLoop;
					}
				}

				CVTerm cvTerm = new CVTerm();
				cvTerm.setQualifierType(Type.BIOLOGICAL_QUALIFIER);
				cvTerm.setBiologicalQualifierType(Qualifier.valueOf("BQB_"
						+ r.logicallink.toUpperCase()));
				cvTerm.addResource(usedPattern + r.getDbName() + separator
						+ r.id);
				annot.addCVTerm(cvTerm);
			}
		}

		return annot;
	}

	/**
	 * Add additional annotation CV Terms from attributes of the input
	 * {@link BioPhysicalEntity}
	 * 
	 * @param ent
	 *            the entity
	 * @param annot
	 *            The SBMl annotation object
	 */
	private void getAdditionnalAnnotation(BioPhysicalEntity ent,
			Annotation annot) {

		CVTerm cvIsTerm = new CVTerm();
		boolean newCV = false;

		if (annot.filterCVTerms(Qualifier.BQB_IS).isEmpty()) {
			cvIsTerm.setQualifierType(Type.BIOLOGICAL_QUALIFIER);
			cvIsTerm.setBiologicalQualifierType(Qualifier.BQB_IS);
			newCV = true;
		} else {
			cvIsTerm = annot.filterCVTerms(Qualifier.BQB_IS).get(0);
		}

		if (ent.getInchi() != null && !ent.getInchi().isEmpty()
				&& !ent.getInchi().equals("NA")) {
			if (annot.filterCVTerms(Qualifier.BQB_IS, "inchi").isEmpty()) {
				cvIsTerm.addResource(usedPattern + "inchi" + separator
						+ ent.getInchi());
			}
		}

		if (ent.getPubchemCID() != null && !ent.getPubchemCID().isEmpty()
				&& !ent.getPubchemCID().equals("NA")) {
			if (annot.filterCVTerms(Qualifier.BQB_IS, "pubchem.compound")
					.isEmpty()) {
				cvIsTerm.addResource(usedPattern + "pubchem.compound"
						+ separator + ent.getPubchemCID());
			}
		}

		if (newCV && cvIsTerm.getNumResources() > 0) {
			annot.addCVTerm(cvIsTerm);
		}

		/**
		 * Same method for the "isDecribedBy" term
		 */
		CVTerm cvIsDescByTerm = new CVTerm();
		newCV = false;
		if (annot.filterCVTerms(Qualifier.BQB_IS_DESCRIBED_BY).isEmpty()) {
			cvIsDescByTerm.setQualifierType(Type.BIOLOGICAL_QUALIFIER);
			cvIsDescByTerm
					.setBiologicalQualifierType(Qualifier.BQB_IS_DESCRIBED_BY);
			newCV = true;
		} else {
			cvIsDescByTerm = annot.filterCVTerms(Qualifier.BQB_IS_DESCRIBED_BY)
					.get(0);
		}
		for (String pmid : ent.getPmids()) {
			cvIsDescByTerm.addResource(usedPattern + "pubmed" + separator
					+ pmid);
		}
		if (newCV && cvIsDescByTerm.getNumResources() > 0) {
			annot.addCVTerm(cvIsDescByTerm);
		}
	}

	/**
	 * Add additional annotation CV Terms from attributes of the input
	 * {@link BioChemicalReaction}
	 * 
	 * @param rxn
	 *            the reaction
	 * @param annot
	 *            The SBMl annotation object
	 */
	private void getAdditionnalAnnotation(BioChemicalReaction rxn,
			Annotation annot) {

		CVTerm cvIsTerm = new CVTerm();
		boolean newCV = false;

		if (annot.filterCVTerms(Qualifier.BQB_IS).isEmpty()) {
			cvIsTerm.setQualifierType(Type.BIOLOGICAL_QUALIFIER);
			cvIsTerm.setBiologicalQualifierType(Qualifier.BQB_IS);
			newCV = true;
		} else {
			cvIsTerm = annot.filterCVTerms(Qualifier.BQB_IS).get(0);
		}

		if (rxn.getEcNumber() != null && !rxn.getEcNumber().isEmpty()) {

			if (annot.filterCVTerms(Qualifier.BQB_IS, "ec-code").isEmpty()) {
				cvIsTerm.addResource(usedPattern + "ec-code" + separator
						+ rxn.getEcNumber());
			}
		}
		if (newCV && cvIsTerm.getNumResources() > 0) {
			annot.addCVTerm(cvIsTerm);
		}

		/**
		 * Same method for the "isDecribedBy" term
		 */
		CVTerm cvIsDescByTerm = new CVTerm();
		newCV = false;
		if (annot.filterCVTerms(Qualifier.BQB_IS_DESCRIBED_BY).isEmpty()) {
			cvIsDescByTerm.setQualifierType(Type.BIOLOGICAL_QUALIFIER);
			cvIsDescByTerm
					.setBiologicalQualifierType(Qualifier.BQB_IS_DESCRIBED_BY);
			newCV = true;
		} else {
			cvIsDescByTerm = annot.filterCVTerms(Qualifier.BQB_IS_DESCRIBED_BY)
					.get(0);
		}
		for (String pmid : rxn.getPmids()) {
			cvIsDescByTerm.addResource(usedPattern + "pubmed" + separator
					+ pmid);
		}
		if (newCV && cvIsDescByTerm.getNumResources() > 0) {
			annot.addCVTerm(cvIsDescByTerm);
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
	 * @return the usedPattern
	 */
	public String getUsedPattern() {
		return usedPattern;
	}

	/**
	 * @param usedPattern
	 *            the usedPattern to set
	 */
	public void setUsedPattern(String usedPattern) {
		this.usedPattern = usedPattern;
	}

	/**
	 * @return the separator
	 */
	public char getSeparator() {
		return separator;
	}

	/**
	 * @param separator
	 *            the separator to set
	 */
	public void setSeparator(char separator) {
		this.separator = separator;
	}

	// private void getAdditionnalAnnotation(BioGene gene, Annotation annot) {
	//
	// CVTerm cvIsTerm=new CVTerm();
	// boolean newCV=false;
	//
	// /**
	// * Same method for the "isDecribedBy" term
	// */
	// CVTerm cvIsDescByTerm=new CVTerm();
	// newCV=false;
	// if(annot.filterCVTerms(Qualifier.BQB_IS_DESCRIBED_BY).isEmpty()){
	// cvIsDescByTerm.setQualifierType(Type.BIOLOGICAL_QUALIFIER);
	// cvIsDescByTerm.setBiologicalQualifierType(Qualifier.BQB_IS_DESCRIBED_BY);
	// newCV=true;
	// }else{
	// cvIsDescByTerm=annot.filterCVTerms(Qualifier.BQB_IS_DESCRIBED_BY).get(0);
	// }
	// for(String pmid:gene.getPmids()){
	// cvIsDescByTerm.addResource(URLpattern+"pubmed"+"/"+pmid);
	// }
	// if(newCV && cvIsDescByTerm.getNumResources()>0){
	// annot.addCVTerm(cvIsDescByTerm);
	// }
	// }

}
