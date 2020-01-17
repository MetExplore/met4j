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

package fr.inra.toulouse.metexplore.met4j_chemUtils.chemicalStructures;

/**
 * Used to test the Inchi and InchiKey objects. The main purpose was to test the <em>equals</em> function of the objects, ie the three properties of equality:
 * <ul>
 * <li>Self equality: a = a
 * <li>Reversability: a = b <=> b = a
 * <li>Reflexivity: { a = b & b = c } => a = c
 * </ul> </br>This could be turned into
 * one or more JUnit Test Cases
 * 
 * @author Benjamin
 * @since 2.0
 */
public class InChITester {

//	public boolean onlyStandard = true;
//
//	public boolean useStereo;
//	public boolean usesIsotopic;

	/**
	 * The main method used to launch the tests
	 * @param args
	 * args (null)
	 */
	public static void main(String[] args) {

		// InChI inc=new
		// InChI("InChI=1S/C6H12O6/c7-1-2-3(8)4(9)5(10)6(11)12-2/h2-11H,1H2/t2-,3-,4+,5-,6?/m1/s1");
		// System.out.println(inc.getFormulaLayer());
		// System.out.println(inc.getRealFormula());
		// System.out.println(inc.getAbsoluteCharge());
		// System.out.println(inc.computeChargedExactMass());
		// System.out.println(inc.computeAverageMass());

		// System.out.println("####");
		// System.out.println("Setting charge and protonation layers to null");
		// inc.setChargeLayer(null);
		// inc.setProtonationLayer(null);
		//
		// System.out.println(inc.getFormulaLayer());
		// System.out.println(inc.getRealFormula());
		// System.out.println(inc.getAbsoluteCharge());
		// System.out.println(inc.computeChargedExactMass());

		InChIKey incK1 = new InChIKey("SHGAZHPCJJPHSC-YCNIQYBTSA-M");
		// InChIKey incK2=new InChIKey("SHGAZHPCJJPHSC-ONEGZZNKSA-P");
		InChIKey incK2 = new InChIKey("SHGAZHPCJJPHSC-ONEGZZNKSA-M");

		System.out.println(incK1.equals(incK2) + " , should be true");
		System.out.println(incK1.equals(incK2, "12") + " , should be false");
		System.out.println(incK1.equals(incK2, "13") + " , should be true");
		System.out.println(incK1.equals(incK2, "123") + " , should be false");

		// InChIKey incK3=new InChIKey("IAQRGUVFOMOMEM-ONEGZZNKSA-N");
		// InChIKey incK4=new InChIKey("SHGAZHPCJJPHSC-MNOVXSKESA-M");
		//
		// System.out.println(incK1.equals(incK2)+" , should be true");
		// System.out.println(incK1.equals(incK3)+" , should be false");
		// System.out.println(incK1.equals(incK2, "123")+" , should be false");
		//
		// System.out.println("self equality: "+incK1.equals(incK1)+" , should be true");
		// System.out.println("self equality: "+incK1.equals(incK1,"123")+" , should be true");
		//
		// System.out.println("reversibility: "+incK2.equals(incK1)+" , should be true");
		// System.out.println("reversibility: "+incK2.equals(incK1,"123")+" , should be false");
		//
		// System.out.println("testing reflexivity: ");
		// System.out.println("1=2?: "+incK1.equals(incK2)+" , should be true");
		// System.out.println("2=4?: "+incK2.equals(incK4)+" , should be true");
		// System.out.println("1=4?: "+incK1.equals(incK4)+" , should be true");

		// InChI inc=new InChI("InChI=1S/C4H8/c1-3-4-2/h3-4H,1-2H3/b4-3-");
		// inc.displayLayers();

		// System.out.println("self equality: "+inc.equals(inc, "sifr"));
		//
		// System.out.println("##########");
		//
		// inc=new
		// InChI("InChI=1/2CH2O2/c2*2-1-3/h2*1H,(H,2,3)/i2+1;2-1/f/h2*2H/i3-1;2+1/o(1,2)");
		// inc.displayLayers();
		// System.out.println("self equality: "+inc.equals(inc, "sifr"));
		//
		// InChI incNoISo=new
		// InChI("InChI=1/2CH2O2/c2*2-1-3/h2*1H,(H,2,3)/f/h2*2H/i3-1;2+1/o(1,2)");
		// incNoISo.displayLayers();
		//
		// InChI incNoFi=new
		// InChI("InChI=1/2CH2O2/c2*2-1-3/h2*1H,(H,2,3)/i2+1;2-1");
		// InChI incNoIsoFix=new InChI("InChI=1/2CH2O2/c2*2-1-3/h2*1H,(H,2,3)");
		//
		// System.out.println("equality with istopic: "+inc.equals(incNoISo,
		// "sifr"));
		// System.out.println("equality without istopic: "+inc.equals(incNoISo,
		// "sfr"));
		//
		// System.out.println("equality with fixed: "+inc.equals(incNoFi,
		// "sifr"));
		// System.out.println("equality without fixed: "+inc.equals(incNoFi,
		// "sir"));
		//
		// System.out.println("equality with both: "+inc.equals(incNoIsoFix,
		// "sifr"));
		// System.out.println("equality with fixed: "+inc.equals(incNoIsoFix,
		// "sfr"));
		// System.out.println("equality with istopic: "+inc.equals(incNoIsoFix,
		// "sir"));
		// System.out.println("equality without both: "+inc.equals(incNoIsoFix,
		// "sr"));
		//
		// System.out.println("##########");
		//
		// InChI inc=new
		// InChI("InChI=1S/C45H74N7O19P3S/c1-24(28-9-10-29-27-8-7-25-18-26(53)12-14-44(25,4)30(27)19-32(54)45(28,29)5)6-11-34(56)75-17-16-47-33(55)13-15-48-41(59)38(58)43(2,3)21-68-74(65,66)71-73(63,64)67-20-31-37(70-72(60,61)62)36(57)42(69-31)52-23-51-35-39(46)49-22-50-40(35)52/h22-32,36-38,42,53-54,57-58H,6-21H2,1-5H3,(H,47,55)(H,48,59)(H,63,64)(H,65,66)(H2,46,49,50)(H2,60,61,62)/p-4/t24-,25-,26-,27+,28-,29+,30+,31-,32+,36-,37-,38+,42-,44+,45-/m1/s1");
		// System.out.println(inc.getFormulaLayer());
		// System.out.println(inc.getRealFormula());
		// System.out.println(inc.getAbsoluteCharge());
		// System.out.println(inc.computeExactMass());
		//
		// System.out.println("#######");
		//
		// inc=new
		// InChI("InChI=1S/C45H74N7O19P3S/c1-24(28-9-10-29-27-8-7-25-18-26(53)12-14-44(25,4)30(27)19-32(54)45(28,29)5)6-11-34(56)75-17-16-47-33(55)13-15-48-41(59)38(58)43(2,3)21-68-74(65,66)71-73(63,64)67-20-31-37(70-72(60,61)62)36(57)42(69-31)52-23-51-35-39(46)49-22-50-40(35)52/h22-32,36-38,42,53-54,57-58H,6-21H2,1-5H3,(H,47,55)(H,48,59)(H,63,64)(H,65,66)(H2,46,49,50)(H2,60,61,62)/t24-,25-,26-,27+,28-,29+,30+,31-,32+,36-,37-,38+,42-,44+,45-/m1/s1");
		// System.out.println(inc.getFormulaLayer());
		// System.out.println(inc.getRealFormula());
		// System.out.println(inc.getAbsoluteCharge());
		// System.out.println(inc.computeExactMass());

		//
		// inc=new InChI("InChI=1S/CO2/c2-1-3");
		// System.out.println(inc.getFormulaLayer());
		// System.out.println(inc.getRealFormula());
		// System.out.println(inc.getAbsoluteCharge());
		// System.out.println(inc.computeExactMass());
		//
		// inc=new InChI("InChI=1S/H3N/h1H3/p+1");
		// System.out.println(inc.getFormulaLayer());
		// System.out.println(inc.getRealFormula());
		// System.out.println(inc.getAbsoluteCharge());
		// System.out.println(inc.computeExactMass());
		//
		// inc=new InChI("InChI=1S/HNO2/c2-1-3/h(H,2,3)/p-1");
		// System.out.println(inc.getFormulaLayer());
		// System.out.println(inc.getRealFormula());
		// System.out.println(inc.getAbsoluteCharge());
		// System.out.println(inc.computeExactMass());
		//
		// inc=new
		// InChI("InChI=1S/C5H9NO4/c6-3(5(9)10)1-2-4(7)8/h3H,1-2,6H2,(H,7,8)(H,9,10)/p-1/t3-/m0/s1");
		// System.out.println(inc.getFormulaLayer());
		// System.out.println(inc.getRealFormula());
		// System.out.println(inc.getAbsoluteCharge());
		//
		// inc=new
		// InChI("InChI=1S/C37H58N7O18P3S/c1-4-5-7-11-24-23(13-14-25(24)45)10-8-6-9-12-28(47)66-18-17-39-27(46)15-16-40-35(50)32(49)37(2,3)20-59-65(56,57)62-64(54,55)58-19-26-31(61-63(51,52)53)30(48)36(60-26)44-22-43-29-33(38)41-21-42-34(29)44/h5,7,9,12,21-24,26,30-32,36,48-49H,4,6,8,10-11,13-20H2,1-3H3,(H,39,46)(H,40,50)(H,54,55)(H,56,57)(H2,38,41,42)(H2,51,52,53)/p-4");
		// System.out.println(inc.getFormulaLayer());
		// System.out.println(inc.getRealFormula());
		// System.out.println(inc.getAbsoluteCharge());
		// System.out.println(inc.computeExactMass());
		//

		// inc.displayLayers();
		//
		// System.out.println("self equality: "+inc.equals(inc, "sifr"));
		//
		// System.out.println("##########");
		//
		// inc=new InChI("InChI=1S/CO2/c2-1-3");
		// inc.displayLayers();
		//
		// System.out.println("self equality: "+inc.equals(inc, "sifr"));
		//
		// System.out.println("##########");
		//
		// inc=new
		// InChI("InChI=1S/C6H8O6/c7-1-2(8)5-3(9)4(10)6(11)12-5/h2,5,7-8,10-11H,1H2/t2-,5+/m0/s1");
		// inc.displayLayers();
		//
		// System.out.println("self equality: "+inc.equals(inc, "sifr"));
		//
		// System.out.println("##########");
		//
		// inc=new
		// InChI("InChI=1S/C5H9NO4/c6-3(5(9)10)1-2-4(7)8/h3H,1-2,6H2,(H,7,8)(H,9,10)/p-1/t3-/m0/s1");
		// inc.displayLayers();
		//
		// System.out.println("self equality: "+inc.equals(inc, "sifr"));
		//
		// System.out.println("##########");
		//
		// inc=new
		// InChI("InChI=1S/C37H58N7O18P3S/c1-4-5-7-11-24-23(13-14-25(24)45)10-8-6-9-12-28(47)66-18-17-39-27(46)15-16-40-35(50)32(49)37(2,3)20-59-65(56,57)62-64(54,55)58-19-26-31(61-63(51,52)53)30(48)36(60-26)44-22-43-29-33(38)41-21-42-34(29)44/h5,7,9,12,21-24,26,30-32,36,48-49H,4,6,8,10-11,13-20H2,1-3H3,(H,39,46)(H,40,50)(H,54,55)(H,56,57)(H2,38,41,42)(H2,51,52,53)/p-4/b7-5-,12-9+/t23?,24?,26-,30-,31-,32+,36-/m1/s1");
		// inc.displayLayers();
		//
		// System.out.println("self equality: "+inc.equals(inc, "sifr"));
		//
		// InChI inc2=new
		// InChI("InChI=1S/C37H58N7O18P3S/c1-4-5-7-11-24-23(13-14-25(24)45)10-8-6-9-12-28(47)66-18-17-39-27(46)15-16-40-35(50)32(49)37(2,3)20-59-65(56,57)62-64(54,55)58-19-26-31(61-63(51,52)53)30(48)36(60-26)44-22-43-29-33(38)41-21-42-34(29)44/h5,7,9,12,21-24,26,30-32,36,48-49H,4,6,8,10-11,13-20H2,1-3H3,(H,39,46)(H,40,50)(H,54,55)(H,56,57)(H2,38,41,42)(H2,51,52,53)/p-4");
		//
		// System.out.println("equality with stereo: "+inc.equals(inc2,
		// "sifr"));
		// System.out.println("testing reversibility: "+inc2.equals(inc,"sifr"));
		//
		// System.out.println("equality without stereo: "+inc.equals(inc2,
		// "ifr"));
		// System.out.println("testing reversibility: "+inc2.equals(inc,"ifr"));
		//
		// InChI inc3=new
		// InChI("InChI=1S/C37H58N7O18P3S/c1-4-5-7-11-24-23(13-14-25(24)45)10-8-6-9-12-28(47)66-18-17-39-27(46)15-16-40-35(50)32(49)37(2,3)20-59-65(56,57)62-64(54,55)58-19-26-31(61-63(51,52)53)30(48)36(60-26)44-22-43-29-33(38)41-21-42-34(29)44/h5,7,9,12,21-24,26,30-32,36,48-49H,4,6,8,10-11,13-20H2,1-3H3,(H,39,46)(H,40,50)(H,54,55)(H,56,57)(H2,38,41,42)(H2,51,52,53)/p-4");
		//
		// System.out.println("testing reflexivity: ");
		// System.out.println("1=2?: "+inc.equals(inc2, "ifr"));
		// System.out.println("2=3?: "+inc2.equals(inc3, "ifr"));
		// System.out.println("1=3?: "+inc.equals(inc3, "ifr"));
		// System.out.println("####");
		// System.out.println("1=2?: "+inc.equals(inc2, "sifr"));
		// System.out.println("2=3?: "+inc2.equals(inc3, "sifr"));
		// System.out.println("1=3?: "+inc.equals(inc3, "sifr"));
		//
		// inc=new
		// InChI("InChI=1S/C17H19NO3/c1-18-7-6-17-10-3-5-13(20)16(17)21-15-12(19)4-2-9(14(15)17)8-11(10)18/h2-5,10-11,13,16,19-20H,6-8H2,1H3/t10-,11+,13-,16-,17-/m0/s1");
		//
		// inc.displayLayers();

	}

}
