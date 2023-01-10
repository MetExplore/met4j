package fr.inrae.toulouse.metexplore.met4j_chemUtils.chemicalSimilarity;

import fr.inrae.toulouse.metexplore.met4j_chemUtils.FormulaParser;
import org.junit.Assert;
import org.junit.Test;

public class FormulaParserTest {

    public String f1 = "NaHCO3";
    public String f2 = "RCO2H";
    public String f3 = "*OH";
    public String f4 = "C8H10N4O2";
    public String f5 = "Ca(NO3)2";
    public String f6 = "Cz(NJ3)2";

    public String f7 = "lardonsfumes";
    public String f8 = "f02mu14$$$";
    public String f9 = "Ca(N O3)2";
    public String f10 = " ";
    public String f11 = "CO2";
    public String f12 = "C1O2";
    public String f13 = "C15H21N5O14P2";
    public String f14 = "C2H7N1O2S1";

    @Test
    public void testFormulaParser(){
        try{FormulaParser fp = new FormulaParser(f1);}
        catch (IllegalArgumentException e){Assert.fail("Valid formula structure rejected");}
        try{FormulaParser fp = new FormulaParser(f2);}
        catch (IllegalArgumentException e){Assert.fail("Valid formula structure rejected");}
        try{FormulaParser fp = new FormulaParser(f3);}
        catch (IllegalArgumentException e){Assert.fail("Valid formula structure rejected");}
        try{FormulaParser fp = new FormulaParser(f4);}
        catch (IllegalArgumentException e){Assert.fail("Valid formula structure rejected");}
        try{FormulaParser fp = new FormulaParser(f5);}
        catch (IllegalArgumentException e){Assert.fail("Valid formula structure rejected");}
        try{FormulaParser fp = new FormulaParser(f7);Assert.fail("Malformed formula passed");}
        catch (IllegalArgumentException e){}
        try{FormulaParser fp = new FormulaParser(f8);Assert.fail("Malformed formula passed");}
        catch (IllegalArgumentException e){}
        try{FormulaParser fp = new FormulaParser(f9);Assert.fail("Malformed formula passed");}
        catch (IllegalArgumentException e){}
        try{FormulaParser fp = new FormulaParser(f10);Assert.fail("Malformed formula passed");}
        catch (IllegalArgumentException e){}
    }

    @Test
    public void testHasUndefinedPart(){
        Assert.assertFalse((new FormulaParser(f1)).hasUndefinedPart());
        Assert.assertTrue((new FormulaParser(f2)).hasUndefinedPart());
        Assert.assertTrue((new FormulaParser(f3)).hasUndefinedPart());
        Assert.assertFalse((new FormulaParser(f4)).hasUndefinedPart());
        Assert.assertFalse((new FormulaParser(f5)).hasUndefinedPart());
        Assert.assertFalse((new FormulaParser(f6)).hasUndefinedPart());
        Assert.assertFalse((new FormulaParser(f11)).hasUndefinedPart());
        Assert.assertFalse((new FormulaParser(f12)).hasUndefinedPart());
        Assert.assertFalse((new FormulaParser(f13)).hasUndefinedPart());
        Assert.assertFalse((new FormulaParser(f14)).hasUndefinedPart());
    }

    @Test
    public void testHasValidAtomSymbols(){
        Assert.assertTrue((new FormulaParser(f1)).hasValidAtomSymbols());
        Assert.assertFalse((new FormulaParser(f2)).hasValidAtomSymbols());
        Assert.assertFalse((new FormulaParser(f3)).hasValidAtomSymbols());
        Assert.assertTrue((new FormulaParser(f4)).hasValidAtomSymbols());
        Assert.assertTrue((new FormulaParser(f5)).hasValidAtomSymbols());
        Assert.assertFalse((new FormulaParser(f6)).hasValidAtomSymbols());
        Assert.assertTrue((new FormulaParser(f11)).hasValidAtomSymbols());
        Assert.assertTrue((new FormulaParser(f12)).hasValidAtomSymbols());
        Assert.assertTrue((new FormulaParser(f13)).hasValidAtomSymbols());
        Assert.assertTrue((new FormulaParser(f14)).hasValidAtomSymbols());
    }

    @Test
    public void testIsExpectedInorganic(){
        Assert.assertTrue((new FormulaParser(f1)).isExpectedInorganic());
        Assert.assertFalse((new FormulaParser(f2)).isExpectedInorganic());
        Assert.assertFalse((new FormulaParser(f3)).isExpectedInorganic());
        Assert.assertFalse((new FormulaParser(f4)).isExpectedInorganic());
        Assert.assertTrue((new FormulaParser(f5)).isExpectedInorganic());
        Assert.assertTrue((new FormulaParser(f6)).isExpectedInorganic());
        Assert.assertTrue((new FormulaParser(f11)).isExpectedInorganic());
        Assert.assertTrue((new FormulaParser(f12)).isExpectedInorganic());
        Assert.assertFalse((new FormulaParser(f13)).isExpectedInorganic());
        Assert.assertFalse((new FormulaParser(f14)).isExpectedInorganic());
    }

}
