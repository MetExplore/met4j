package fr.inrae.toulouse.metexplore.met4j_mapping;
import fr.inrae.toulouse.metexplore.met4j_mapping.fuzzyMatching.ChemicalAliasCreator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChemicalAliasCreatorTest {

    @Test
    public void testMapper1(){
        ChemicalAliasCreator alias = new ChemicalAliasCreator(ChemicalAliasCreator.all());
        assertEquals("αdglucose",alias.createAlias("alpha-D-Glucopyranose"));
        assertEquals("ebutyrate",alias.createAlias("trans-Butyric Acid"));
        assertEquals("linoleol",alias.createAlias("linoleol"));
    }
}
