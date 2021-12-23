/*
 * Copyright INRAE (2021)
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

public class KeggApiMock {

    public static String errorResult = "ERROR";

    public static String orgInfo = "T00820           Buchnera aphidicola 5A (Acyrthosiphon pisum) KEGG Genes Database\n" +
            "bap              Release 100.0+/12-16, Dec 21\n" +
            "                 Kanehisa Laboratories\n" +
            "                 599 entries\n" +
            "\n" +
            "linked db        pathway\n" +
            "                 brite\n" +
            "                 module\n" +
            "                 ko\n" +
            "                 genome\n" +
            "                 enzyme\n" +
            "                 ncbi-proteinid\n" +
            "                 uniprot";

    public static String reactionInfo = "ENTRY       R00109                      Reaction\n" +
            "NAME        NADPH:ferrileghemoglobin oxidoreductase\n" +
            "DEFINITION  NADPH + 2 Ferrileghemoglobin <=> NADP+ + 2 Ferroleghemoglobin + H+\n" +
            "EQUATION    2n C00005 + 2 C02683 <=> C00006(n+1) + 2 C02685 + dd C00080\n" +
            "COMMENT     NADH (see R00101)\n" +
            "RCLASS      RC00001  C00005_C00006\n" +
            "ENZYME      3.4.21.92\n" +
            "            1.19.1.1\n"+
            "DBLINKS     RHEA: 16160\n" +
            "///\n" +
            "ENTRY       R00209                      Reaction\n" +
            "NAME        pyruvate:NAD+ 2-oxidoreductase (CoA-acetylating);\n" +
            "            pyruvate dehydrogenase complex\n" +
            "DEFINITION  Pyruvate + CoA + NAD+ <=> Acetyl-CoA + CO2 + NADH + H+\n" +
            "EQUATION    C00022 + C00010 + C00003 <=> C00024 + C00011 + C00004 + C00080\n" +
            "COMMENT     multienzyme system\n" +
            "            multi-step reaction (see R01699+R02569+R07618)\n" +
            "RCLASS      RC00001  C00003_C00004\n" +
            "            RC00004  C00010_C00024\n" +
            "            RC02742  C00022_C00024\n" +
            "ENZYME      1.2.1.104\n" +
            "PATHWAY     rn01100  Metabolic pathways\n" +
            "            rn01110  Biosynthesis of secondary metabolites\n" +
            "            rn01120  Microbial metabolism in diverse environments\n" +
            "            rn01200  Carbon metabolism\n" +
            "MODULE      M00307  Pyruvate oxidation, pyruvate => acetyl-CoA\n" +
            "ORTHOLOGY   K00161  pyruvate dehydrogenase E1 component alpha subunit [EC:1.2.4.1]\n" +
            "            K00162  pyruvate dehydrogenase E1 component beta subunit [EC:1.2.4.1]\n" +
            "            K00163  pyruvate dehydrogenase E1 component [EC:1.2.4.1]\n" +
            "            K00382  dihydrolipoamide dehydrogenase [EC:1.8.1.4]\n" +
            "            K00627  pyruvate dehydrogenase E2 component (dihydrolipoamide acetyltransferase) [EC:2.3.1.12]\n" +
            "DBLINKS     RHEA: 28045\n" +
            "///";

    public static String metaboliteInfo = "ENTRY       C00005                      Compound\n" +
            "NAME        NADPH;\n" +
            "            TPNH;\n" +
            "            Reduced nicotinamide adenine dinucleotide phosphate\n" +
            "FORMULA     C21H30N7O17P3\n" +
            "EXACT_MASS  745.0911\n" +
            "MOL_WEIGHT  745.4209\n" +
            "DBLINKS     CAS: 2646-71-1\n" +
            "            PubChem: 3307\n" +
            "            ChEBI: 16474\n" +
            "            ChEMBL: CHEMBL1594246 CHEMBL407009\n" +
            "            PDB-CCD: NDP\n" +
            "            3DMET: B01128\n" +
            "            NIKKAJI: J208.978E\n" +
            "///\n" +
            "ENTRY       C02683                      Compound\n" +
            "NAME        Ferrileghemoglobin\n" +
            "REACTION    R00101 R00109\n" +
            "ENZYME      1.6.2.6\n" +
            "DBLINKS     PubChem: 5652\n" +
            "EXACT_MASS  3.0\n" +
            "///";

    public static String geneList = "bap:BUAP5A_001\tgn:bap\n" +
            "bap:BUAP5A_002\tgn:bap\n" +
            "bap:BUAP5A_003\tgn:bap\n" +
            "bap:BUAP5A_004\tgn:bap\n" +
            "bap:BUAP5A_005\tgn:bap\n" +
            "bap:BUAP5A_006\tgn:bap\n" +
            "bap:BUAP5A_007\tgn:bap\n" +
            "bap:BUAP5A_008\tgn:bap\n" +
            "bap:BUAP5A_009\tgn:bap\n" +
            "bap:BUAP5A_010\tgn:bap\n" +
            "bap:BUAP5A_011\tgn:bap\n" +
            "bap:BUAP5A_012\tgn:bap\n" +
            "bap:BUAP5A_013\tgn:bap\n" +
            "bap:BUAP5A_014\tgn:bap\n" +
            "bap:BUAP5A_015\tgn:bap\n" +
            "bap:BUAP5A_016\tgn:bap\n" +
            "bap:BUAP5A_017\tgn:bap\n" +
            "bap:BUAP5A_018\tgn:bap\n" +
            "bap:BUAP5A_019\tgn:bap\n" +
            "bap:BUAP5A_020\tgn:bap\n" +
            "bap:BUAP5A_021\tgn:bap\n" +
            "bap:BUAP5A_022\tgn:bap\n" +
            "bap:BUAP5A_023\tgn:bap\n" +
            "bap:BUAP5A_024\tgn:bap\n" +
            "bap:BUAP5A_025\tgn:bap\n" +
            "bap:BUAP5A_026\tgn:bap\n" +
            "bap:BUAP5A_027\tgn:bap\n" +
            "bap:BUAP5A_028\tgn:bap\n" +
            "bap:BUAP5A_029\tgn:bap\n" +
            "bap:BUAP5A_030\tgn:bap\n" +
            "bap:BUAP5A_031\tgn:bap\n" +
            "bap:BUAP5A_032\tgn:bap\n" +
            "bap:BUAP5A_033\tgn:bap\n" +
            "bap:BUAP5A_034\tgn:bap\n" +
            "bap:BUAP5A_035\tgn:bap\n" +
            "bap:BUAP5A_036\tgn:bap\n" +
            "bap:BUAP5A_037\tgn:bap\n" +
            "bap:BUAP5A_038\tgn:bap\n" +
            "bap:BUAP5A_039\tgn:bap\n" +
            "bap:BUAP5A_040\tgn:bap\n" +
            "bap:BUAP5A_041\tgn:bap\n" +
            "bap:BUAP5A_042\tgn:bap\n" +
            "bap:BUAP5A_043\tgn:bap\n" +
            "bap:BUAP5A_044\tgn:bap\n" +
            "bap:BUAP5A_045\tgn:bap\n" +
            "bap:BUAP5A_046\tgn:bap\n" +
            "bap:BUAP5A_047\tgn:bap\n" +
            "bap:BUAP5A_048\tgn:bap\n" +
            "bap:BUAP5A_049\tgn:bap\n" +
            "bap:BUAP5A_050\tgn:bap\n" +
            "bap:BUAP5A_051\tgn:bap\n" +
            "bap:BUAP5A_052\tgn:bap\n" +
            "bap:BUAP5A_053\tgn:bap\n" +
            "bap:BUAP5A_054\tgn:bap\n" +
            "bap:BUAP5A_055\tgn:bap\n" +
            "bap:BUAP5A_056\tgn:bap\n" +
            "bap:BUAP5A_057\tgn:bap\n" +
            "bap:BUAP5A_058\tgn:bap\n" +
            "bap:BUAP5A_059\tgn:bap\n" +
            "bap:BUAP5A_060\tgn:bap\n" +
            "bap:BUAP5A_061\tgn:bap\n" +
            "bap:BUAP5A_062\tgn:bap\n" +
            "bap:BUAP5A_063\tgn:bap\n" +
            "bap:BUAP5A_064\tgn:bap\n" +
            "bap:BUAP5A_065\tgn:bap\n" +
            "bap:BUAP5A_066\tgn:bap\n" +
            "bap:BUAP5A_067\tgn:bap\n" +
            "bap:BUAP5A_068\tgn:bap\n" +
            "bap:BUAP5A_069\tgn:bap\n" +
            "bap:BUAP5A_070\tgn:bap\n" +
            "bap:BUAP5A_071\tgn:bap\n" +
            "bap:BUAP5A_072\tgn:bap\n" +
            "bap:BUAP5A_073\tgn:bap\n" +
            "bap:BUAP5A_074\tgn:bap\n" +
            "bap:BUAP5A_075\tgn:bap\n" +
            "bap:BUAP5A_076\tgn:bap\n" +
            "bap:BUAP5A_078\tgn:bap\n" +
            "bap:BUAP5A_079\tgn:bap\n" +
            "bap:BUAP5A_080\tgn:bap\n" +
            "bap:BUAP5A_081\tgn:bap\n" +
            "bap:BUAP5A_082\tgn:bap\n" +
            "bap:BUAP5A_083\tgn:bap\n" +
            "bap:BUAP5A_084\tgn:bap\n" +
            "bap:BUAP5A_085\tgn:bap\n" +
            "bap:BUAP5A_086\tgn:bap\n" +
            "bap:BUAP5A_087\tgn:bap\n" +
            "bap:BUAP5A_088\tgn:bap\n" +
            "bap:BUAP5A_089\tgn:bap\n" +
            "bap:BUAP5A_090\tgn:bap\n" +
            "bap:BUAP5A_091\tgn:bap\n" +
            "bap:BUAP5A_092\tgn:bap\n" +
            "bap:BUAP5A_093\tgn:bap\n" +
            "bap:BUAP5A_094\tgn:bap\n" +
            "bap:BUAP5A_095\tgn:bap\n" +
            "bap:BUAP5A_096\tgn:bap\n" +
            "bap:BUAP5A_097\tgn:bap\n" +
            "bap:BUAP5A_098\tgn:bap\n" +
            "bap:BUAP5A_099\tgn:bap\n" +
            "bap:BUAP5A_100\tgn:bap\n" +
            "bap:BUAP5A_101\tgn:bap\n" +
            "bap:BUAP5A_102\tgn:bap\n" +
            "bap:BUAP5A_103\tgn:bap\n" +
            "bap:BUAP5A_104\tgn:bap\n" +
            "bap:BUAP5A_105\tgn:bap\n" +
            "bap:BUAP5A_106\tgn:bap\n" +
            "bap:BUAP5A_107\tgn:bap\n" +
            "bap:BUAP5A_108\tgn:bap\n" +
            "bap:BUAP5A_109\tgn:bap\n" +
            "bap:BUAP5A_110\tgn:bap\n" +
            "bap:BUAP5A_111\tgn:bap\n" +
            "bap:BUAP5A_112\tgn:bap\n" +
            "bap:BUAP5A_113\tgn:bap\n" +
            "bap:BUAP5A_114\tgn:bap\n" +
            "bap:BUAP5A_115\tgn:bap\n" +
            "bap:BUAP5A_116\tgn:bap\n" +
            "bap:BUAP5A_117\tgn:bap\n" +
            "bap:BUAP5A_118\tgn:bap\n" +
            "bap:BUAP5A_119\tgn:bap\n" +
            "bap:BUAP5A_120\tgn:bap\n" +
            "bap:BUAP5A_121\tgn:bap\n" +
            "bap:BUAP5A_122\tgn:bap\n" +
            "bap:BUAP5A_123\tgn:bap\n" +
            "bap:BUAP5A_124\tgn:bap\n" +
            "bap:BUAP5A_125\tgn:bap\n" +
            "bap:BUAP5A_126\tgn:bap\n" +
            "bap:BUAP5A_127\tgn:bap\n" +
            "bap:BUAP5A_128\tgn:bap\n" +
            "bap:BUAP5A_129\tgn:bap\n" +
            "bap:BUAP5A_130\tgn:bap\n" +
            "bap:BUAP5A_131\tgn:bap\n" +
            "bap:BUAP5A_132\tgn:bap\n" +
            "bap:BUAP5A_133\tgn:bap\n" +
            "bap:BUAP5A_134\tgn:bap\n" +
            "bap:BUAP5A_135\tgn:bap\n" +
            "bap:BUAP5A_136\tgn:bap\n" +
            "bap:BUAP5A_137\tgn:bap\n" +
            "bap:BUAP5A_138\tgn:bap\n" +
            "bap:BUAP5A_139\tgn:bap\n" +
            "bap:BUAP5A_140\tgn:bap\n" +
            "bap:BUAP5A_141\tgn:bap\n" +
            "bap:BUAP5A_142\tgn:bap\n" +
            "bap:BUAP5A_143\tgn:bap\n" +
            "bap:BUAP5A_144\tgn:bap\n" +
            "bap:BUAP5A_145\tgn:bap\n" +
            "bap:BUAP5A_146\tgn:bap\n" +
            "bap:BUAP5A_147\tgn:bap\n" +
            "bap:BUAP5A_148\tgn:bap\n" +
            "bap:BUAP5A_149\tgn:bap\n" +
            "bap:BUAP5A_150\tgn:bap\n" +
            "bap:BUAP5A_151\tgn:bap\n" +
            "bap:BUAP5A_152\tgn:bap\n" +
            "bap:BUAP5A_153\tgn:bap\n" +
            "bap:BUAP5A_154\tgn:bap\n" +
            "bap:BUAP5A_155\tgn:bap\n" +
            "bap:BUAP5A_156\tgn:bap\n" +
            "bap:BUAP5A_157\tgn:bap\n" +
            "bap:BUAP5A_158\tgn:bap\n" +
            "bap:BUAP5A_159\tgn:bap\n" +
            "bap:BUAP5A_160\tgn:bap\n" +
            "bap:BUAP5A_161\tgn:bap\n" +
            "bap:BUAP5A_162\tgn:bap\n" +
            "bap:BUAP5A_163\tgn:bap\n" +
            "bap:BUAP5A_164\tgn:bap\n" +
            "bap:BUAP5A_165\tgn:bap\n" +
            "bap:BUAP5A_166\tgn:bap\n" +
            "bap:BUAP5A_167\tgn:bap\n" +
            "bap:BUAP5A_168\tgn:bap\n" +
            "bap:BUAP5A_169\tgn:bap\n" +
            "bap:BUAP5A_170\tgn:bap\n" +
            "bap:BUAP5A_171\tgn:bap\n" +
            "bap:BUAP5A_172\tgn:bap\n" +
            "bap:BUAP5A_173\tgn:bap\n" +
            "bap:BUAP5A_174\tgn:bap\n" +
            "bap:BUAP5A_175\tgn:bap\n" +
            "bap:BUAP5A_176\tgn:bap\n" +
            "bap:BUAP5A_177\tgn:bap\n" +
            "bap:BUAP5A_178\tgn:bap\n" +
            "bap:BUAP5A_179\tgn:bap\n" +
            "bap:BUAP5A_180\tgn:bap\n" +
            "bap:BUAP5A_181\tgn:bap\n" +
            "bap:BUAP5A_182\tgn:bap\n" +
            "bap:BUAP5A_183\tgn:bap\n" +
            "bap:BUAP5A_184\tgn:bap\n" +
            "bap:BUAP5A_601\tgn:bap\n" +
            "bap:BUAP5A_185\tgn:bap\n" +
            "bap:BUAP5A_186\tgn:bap\n" +
            "bap:BUAP5A_187\tgn:bap\n" +
            "bap:BUAP5A_188\tgn:bap\n" +
            "bap:BUAP5A_189\tgn:bap\n" +
            "bap:BUAP5A_190\tgn:bap\n" +
            "bap:BUAP5A_191\tgn:bap\n" +
            "bap:BUAP5A_192\tgn:bap\n" +
            "bap:BUAP5A_193\tgn:bap\n" +
            "bap:BUAP5A_194\tgn:bap\n" +
            "bap:BUAP5A_195\tgn:bap\n" +
            "bap:BUAP5A_196\tgn:bap\n" +
            "bap:BUAP5A_197\tgn:bap\n" +
            "bap:BUAP5A_198\tgn:bap\n" +
            "bap:BUAP5A_199\tgn:bap\n" +
            "bap:BUAP5A_200\tgn:bap\n" +
            "bap:BUAP5A_201\tgn:bap\n" +
            "bap:BUAP5A_202\tgn:bap\n" +
            "bap:BUAP5A_203\tgn:bap\n" +
            "bap:BUAP5A_204\tgn:bap\n" +
            "bap:BUAP5A_205\tgn:bap\n" +
            "bap:BUAP5A_206\tgn:bap\n" +
            "bap:BUAP5A_207\tgn:bap\n" +
            "bap:BUAP5A_208\tgn:bap\n" +
            "bap:BUAP5A_209\tgn:bap\n" +
            "bap:BUAP5A_210\tgn:bap\n" +
            "bap:BUAP5A_211\tgn:bap\n" +
            "bap:BUAP5A_212\tgn:bap\n" +
            "bap:BUAP5A_213\tgn:bap\n" +
            "bap:BUAP5A_214\tgn:bap\n" +
            "bap:BUAP5A_215\tgn:bap\n" +
            "bap:BUAP5A_216\tgn:bap\n" +
            "bap:BUAP5A_217\tgn:bap\n" +
            "bap:BUAP5A_218\tgn:bap\n" +
            "bap:BUAP5A_219\tgn:bap\n" +
            "bap:BUAP5A_220\tgn:bap\n" +
            "bap:BUAP5A_221\tgn:bap\n" +
            "bap:BUAP5A_222\tgn:bap\n" +
            "bap:BUAP5A_223\tgn:bap\n" +
            "bap:BUAP5A_224\tgn:bap\n" +
            "bap:BUAP5A_225\tgn:bap\n" +
            "bap:BUAP5A_226\tgn:bap\n" +
            "bap:BUAP5A_227\tgn:bap\n" +
            "bap:BUAP5A_228\tgn:bap\n" +
            "bap:BUAP5A_229\tgn:bap\n" +
            "bap:BUAP5A_230\tgn:bap\n" +
            "bap:BUAP5A_231\tgn:bap\n" +
            "bap:BUAP5A_232\tgn:bap\n" +
            "bap:BUAP5A_233\tgn:bap\n" +
            "bap:BUAP5A_234\tgn:bap\n" +
            "bap:BUAP5A_235\tgn:bap\n" +
            "bap:BUAP5A_236\tgn:bap\n" +
            "bap:BUAP5A_237\tgn:bap\n" +
            "bap:BUAP5A_238\tgn:bap\n" +
            "bap:BUAP5A_239\tgn:bap\n" +
            "bap:BUAP5A_240\tgn:bap\n" +
            "bap:BUAP5A_241\tgn:bap\n" +
            "bap:BUAP5A_242\tgn:bap\n" +
            "bap:BUAP5A_243\tgn:bap\n" +
            "bap:BUAP5A_244\tgn:bap\n" +
            "bap:BUAP5A_245\tgn:bap\n" +
            "bap:BUAP5A_246\tgn:bap\n" +
            "bap:BUAP5A_247\tgn:bap\n" +
            "bap:BUAP5A_248\tgn:bap\n" +
            "bap:BUAP5A_249\tgn:bap\n" +
            "bap:BUAP5A_250\tgn:bap\n" +
            "bap:BUAP5A_251\tgn:bap\n" +
            "bap:BUAP5A_252\tgn:bap\n" +
            "bap:BUAP5A_253\tgn:bap\n" +
            "bap:BUAP5A_254\tgn:bap\n" +
            "bap:BUAP5A_255\tgn:bap\n" +
            "bap:BUAP5A_256\tgn:bap\n" +
            "bap:BUAP5A_257\tgn:bap\n" +
            "bap:BUAP5A_258\tgn:bap\n" +
            "bap:BUAP5A_259\tgn:bap\n" +
            "bap:BUAP5A_260\tgn:bap\n" +
            "bap:BUAP5A_261\tgn:bap\n" +
            "bap:BUAP5A_262\tgn:bap\n" +
            "bap:BUAP5A_263\tgn:bap\n" +
            "bap:BUAP5A_264\tgn:bap\n" +
            "bap:BUAP5A_265\tgn:bap\n" +
            "bap:BUAP5A_266\tgn:bap\n" +
            "bap:BUAP5A_267\tgn:bap\n" +
            "bap:BUAP5A_268\tgn:bap\n" +
            "bap:BUAP5A_269\tgn:bap\n" +
            "bap:BUAP5A_270\tgn:bap\n" +
            "bap:BUAP5A_271\tgn:bap\n" +
            "bap:BUAP5A_272\tgn:bap\n" +
            "bap:BUAP5A_273\tgn:bap\n" +
            "bap:BUAP5A_274\tgn:bap\n" +
            "bap:BUAP5A_275\tgn:bap\n" +
            "bap:BUAP5A_276\tgn:bap\n" +
            "bap:BUAP5A_277\tgn:bap\n" +
            "bap:BUAP5A_278\tgn:bap\n" +
            "bap:BUAP5A_279\tgn:bap\n" +
            "bap:BUAP5A_280\tgn:bap\n" +
            "bap:BUAP5A_281\tgn:bap\n" +
            "bap:BUAP5A_282\tgn:bap\n" +
            "bap:BUAP5A_283\tgn:bap\n" +
            "bap:BUAP5A_284\tgn:bap\n" +
            "bap:BUAP5A_285\tgn:bap\n" +
            "bap:BUAP5A_286\tgn:bap\n" +
            "bap:BUAP5A_287\tgn:bap\n" +
            "bap:BUAP5A_288\tgn:bap\n" +
            "bap:BUAP5A_289\tgn:bap\n" +
            "bap:BUAP5A_290\tgn:bap\n" +
            "bap:BUAP5A_291\tgn:bap\n" +
            "bap:BUAP5A_292\tgn:bap\n" +
            "bap:BUAP5A_293\tgn:bap\n" +
            "bap:BUAP5A_294\tgn:bap\n" +
            "bap:BUAP5A_295\tgn:bap\n" +
            "bap:BUAP5A_296\tgn:bap\n" +
            "bap:BUAP5A_297\tgn:bap\n" +
            "bap:BUAP5A_298\tgn:bap\n" +
            "bap:BUAP5A_299\tgn:bap\n" +
            "bap:BUAP5A_300\tgn:bap\n" +
            "bap:BUAP5A_301\tgn:bap\n" +
            "bap:BUAP5A_302\tgn:bap\n" +
            "bap:BUAP5A_303\tgn:bap\n" +
            "bap:BUAP5A_304\tgn:bap\n" +
            "bap:BUAP5A_305\tgn:bap\n" +
            "bap:BUAP5A_306\tgn:bap\n" +
            "bap:BUAP5A_307\tgn:bap\n" +
            "bap:BUAP5A_308\tgn:bap\n" +
            "bap:BUAP5A_309\tgn:bap\n" +
            "bap:BUAP5A_310\tgn:bap\n" +
            "bap:BUAP5A_311\tgn:bap\n" +
            "bap:BUAP5A_312\tgn:bap\n" +
            "bap:BUAP5A_313\tgn:bap\n" +
            "bap:BUAP5A_314\tgn:bap\n" +
            "bap:BUAP5A_315\tgn:bap\n" +
            "bap:BUAP5A_316\tgn:bap\n" +
            "bap:BUAP5A_317\tgn:bap\n" +
            "bap:BUAP5A_318\tgn:bap\n" +
            "bap:BUAP5A_319\tgn:bap\n" +
            "bap:BUAP5A_320\tgn:bap\n" +
            "bap:BUAP5A_321\tgn:bap\n" +
            "bap:BUAP5A_322\tgn:bap\n" +
            "bap:BUAP5A_323\tgn:bap\n" +
            "bap:BUAP5A_325\tgn:bap\n" +
            "bap:BUAP5A_326\tgn:bap\n" +
            "bap:BUAP5A_327\tgn:bap\n" +
            "bap:BUAP5A_328\tgn:bap\n" +
            "bap:BUAP5A_329\tgn:bap\n" +
            "bap:BUAP5A_330\tgn:bap\n" +
            "bap:BUAP5A_331\tgn:bap\n" +
            "bap:BUAP5A_332\tgn:bap\n" +
            "bap:BUAP5A_333\tgn:bap\n" +
            "bap:BUAP5A_334\tgn:bap\n" +
            "bap:BUAP5A_335\tgn:bap\n" +
            "bap:BUAP5A_336\tgn:bap\n" +
            "bap:BUAP5A_337\tgn:bap\n" +
            "bap:BUAP5A_338\tgn:bap\n" +
            "bap:BUAP5A_339\tgn:bap\n" +
            "bap:BUAP5A_340\tgn:bap\n" +
            "bap:BUAP5A_341\tgn:bap\n" +
            "bap:BUAP5A_342\tgn:bap\n" +
            "bap:BUAP5A_343\tgn:bap\n" +
            "bap:BUAP5A_344\tgn:bap\n" +
            "bap:BUAP5A_345\tgn:bap\n" +
            "bap:BUAP5A_346\tgn:bap\n" +
            "bap:BUAP5A_347\tgn:bap\n" +
            "bap:BUAP5A_348\tgn:bap\n" +
            "bap:BUAP5A_349\tgn:bap\n" +
            "bap:BUAP5A_350\tgn:bap\n" +
            "bap:BUAP5A_351\tgn:bap\n" +
            "bap:BUAP5A_352\tgn:bap\n" +
            "bap:BUAP5A_353\tgn:bap\n" +
            "bap:BUAP5A_354\tgn:bap\n" +
            "bap:BUAP5A_355\tgn:bap\n" +
            "bap:BUAP5A_356\tgn:bap\n" +
            "bap:BUAP5A_357\tgn:bap\n" +
            "bap:BUAP5A_358\tgn:bap\n" +
            "bap:BUAP5A_359\tgn:bap\n" +
            "bap:BUAP5A_360\tgn:bap\n" +
            "bap:BUAP5A_361\tgn:bap\n" +
            "bap:BUAP5A_362\tgn:bap\n" +
            "bap:BUAP5A_363\tgn:bap\n" +
            "bap:BUAP5A_364\tgn:bap\n" +
            "bap:BUAP5A_365\tgn:bap\n" +
            "bap:BUAP5A_366\tgn:bap\n" +
            "bap:BUAP5A_367\tgn:bap\n" +
            "bap:BUAP5A_368\tgn:bap\n" +
            "bap:BUAP5A_369\tgn:bap\n" +
            "bap:BUAP5A_370\tgn:bap\n" +
            "bap:BUAP5A_371\tgn:bap\n" +
            "bap:BUAP5A_372\tgn:bap\n" +
            "bap:BUAP5A_373\tgn:bap\n" +
            "bap:BUAP5A_374\tgn:bap\n" +
            "bap:BUAP5A_375\tgn:bap\n" +
            "bap:BUAP5A_376\tgn:bap\n" +
            "bap:BUAP5A_377\tgn:bap\n" +
            "bap:BUAP5A_378\tgn:bap\n" +
            "bap:BUAP5A_379\tgn:bap\n" +
            "bap:BUAP5A_380\tgn:bap\n" +
            "bap:BUAP5A_381\tgn:bap\n" +
            "bap:BUAP5A_382\tgn:bap\n" +
            "bap:BUAP5A_383\tgn:bap\n" +
            "bap:BUAP5A_384\tgn:bap\n" +
            "bap:BUAP5A_385\tgn:bap\n" +
            "bap:BUAP5A_386\tgn:bap\n" +
            "bap:BUAP5A_387\tgn:bap\n" +
            "bap:BUAP5A_388\tgn:bap\n" +
            "bap:BUAP5A_389\tgn:bap\n" +
            "bap:BUAP5A_390\tgn:bap\n" +
            "bap:BUAP5A_391\tgn:bap\n" +
            "bap:BUAP5A_392\tgn:bap\n" +
            "bap:BUAP5A_393\tgn:bap\n" +
            "bap:BUAP5A_394\tgn:bap\n" +
            "bap:BUAP5A_395\tgn:bap\n" +
            "bap:BUAP5A_396\tgn:bap\n" +
            "bap:BUAP5A_397\tgn:bap\n" +
            "bap:BUAP5A_398\tgn:bap\n" +
            "bap:BUAP5A_399\tgn:bap\n" +
            "bap:BUAP5A_400\tgn:bap\n" +
            "bap:BUAP5A_401\tgn:bap\n" +
            "bap:BUAP5A_402\tgn:bap\n" +
            "bap:BUAP5A_403\tgn:bap\n" +
            "bap:BUAP5A_404\tgn:bap\n" +
            "bap:BUAP5A_405\tgn:bap\n" +
            "bap:BUAP5A_406\tgn:bap\n" +
            "bap:BUAP5A_407\tgn:bap\n" +
            "bap:BUAP5A_408\tgn:bap\n" +
            "bap:BUAP5A_409\tgn:bap\n" +
            "bap:BUAP5A_410\tgn:bap\n" +
            "bap:BUAP5A_411\tgn:bap\n" +
            "bap:BUAP5A_412\tgn:bap\n" +
            "bap:BUAP5A_413\tgn:bap\n" +
            "bap:BUAP5A_414\tgn:bap\n" +
            "bap:BUAP5A_415\tgn:bap\n" +
            "bap:BUAP5A_416\tgn:bap\n" +
            "bap:BUAP5A_417\tgn:bap\n" +
            "bap:BUAP5A_418\tgn:bap\n" +
            "bap:BUAP5A_419\tgn:bap\n" +
            "bap:BUAP5A_420\tgn:bap\n" +
            "bap:BUAP5A_421\tgn:bap\n" +
            "bap:BUAP5A_422\tgn:bap\n" +
            "bap:BUAP5A_423\tgn:bap\n" +
            "bap:BUAP5A_424\tgn:bap\n" +
            "bap:BUAP5A_425\tgn:bap\n" +
            "bap:BUAP5A_426\tgn:bap\n" +
            "bap:BUAP5A_427\tgn:bap\n" +
            "bap:BUAP5A_428\tgn:bap\n" +
            "bap:BUAP5A_429\tgn:bap\n" +
            "bap:BUAP5A_430\tgn:bap\n" +
            "bap:BUAP5A_431\tgn:bap\n" +
            "bap:BUAP5A_432\tgn:bap\n" +
            "bap:BUAP5A_433\tgn:bap\n" +
            "bap:BUAP5A_434\tgn:bap\n" +
            "bap:BUAP5A_435\tgn:bap\n" +
            "bap:BUAP5A_436\tgn:bap\n" +
            "bap:BUAP5A_437\tgn:bap\n" +
            "bap:BUAP5A_438\tgn:bap\n" +
            "bap:BUAP5A_439\tgn:bap\n" +
            "bap:BUAP5A_440\tgn:bap\n" +
            "bap:BUAP5A_441\tgn:bap\n" +
            "bap:BUAP5A_442\tgn:bap\n" +
            "bap:BUAP5A_443\tgn:bap\n" +
            "bap:BUAP5A_444\tgn:bap\n" +
            "bap:BUAP5A_445\tgn:bap\n" +
            "bap:BUAP5A_446\tgn:bap\n" +
            "bap:BUAP5A_447\tgn:bap\n" +
            "bap:BUAP5A_448\tgn:bap\n" +
            "bap:BUAP5A_449\tgn:bap\n" +
            "bap:BUAP5A_450\tgn:bap\n" +
            "bap:BUAP5A_451\tgn:bap\n" +
            "bap:BUAP5A_452\tgn:bap\n" +
            "bap:BUAP5A_453\tgn:bap\n" +
            "bap:BUAP5A_454\tgn:bap\n" +
            "bap:BUAP5A_455\tgn:bap\n" +
            "bap:BUAP5A_456\tgn:bap\n" +
            "bap:BUAP5A_457\tgn:bap\n" +
            "bap:BUAP5A_458\tgn:bap\n" +
            "bap:BUAP5A_459\tgn:bap\n" +
            "bap:BUAP5A_460\tgn:bap\n" +
            "bap:BUAP5A_461\tgn:bap\n" +
            "bap:BUAP5A_462\tgn:bap\n" +
            "bap:BUAP5A_463\tgn:bap\n" +
            "bap:BUAP5A_464\tgn:bap\n" +
            "bap:BUAP5A_465\tgn:bap\n" +
            "bap:BUAP5A_466\tgn:bap\n" +
            "bap:BUAP5A_467\tgn:bap\n" +
            "bap:BUAP5A_468\tgn:bap\n" +
            "bap:BUAP5A_469\tgn:bap\n" +
            "bap:BUAP5A_470\tgn:bap\n" +
            "bap:BUAP5A_471\tgn:bap\n" +
            "bap:BUAP5A_472\tgn:bap\n" +
            "bap:BUAP5A_473\tgn:bap\n" +
            "bap:BUAP5A_474\tgn:bap\n" +
            "bap:BUAP5A_475\tgn:bap\n" +
            "bap:BUAP5A_476\tgn:bap\n" +
            "bap:BUAP5A_477\tgn:bap\n" +
            "bap:BUAP5A_478\tgn:bap\n" +
            "bap:BUAP5A_479\tgn:bap\n" +
            "bap:BUAP5A_480\tgn:bap\n" +
            "bap:BUAP5A_481\tgn:bap\n" +
            "bap:BUAP5A_482\tgn:bap\n" +
            "bap:BUAP5A_483\tgn:bap\n" +
            "bap:BUAP5A_484\tgn:bap\n" +
            "bap:BUAP5A_485\tgn:bap\n" +
            "bap:BUAP5A_486\tgn:bap\n" +
            "bap:BUAP5A_487\tgn:bap\n" +
            "bap:BUAP5A_488\tgn:bap\n" +
            "bap:BUAP5A_489\tgn:bap\n" +
            "bap:BUAP5A_490\tgn:bap\n" +
            "bap:BUAP5A_491\tgn:bap\n" +
            "bap:BUAP5A_492\tgn:bap\n" +
            "bap:BUAP5A_493\tgn:bap\n" +
            "bap:BUAP5A_494\tgn:bap\n" +
            "bap:BUAP5A_495\tgn:bap\n" +
            "bap:BUAP5A_496\tgn:bap\n" +
            "bap:BUAP5A_497\tgn:bap\n" +
            "bap:BUAP5A_498\tgn:bap\n" +
            "bap:BUAP5A_499\tgn:bap\n" +
            "bap:BUAP5A_500\tgn:bap\n" +
            "bap:BUAP5A_501\tgn:bap\n" +
            "bap:BUAP5A_502\tgn:bap\n" +
            "bap:BUAP5A_503\tgn:bap\n" +
            "bap:BUAP5A_504\tgn:bap\n" +
            "bap:BUAP5A_505\tgn:bap\n" +
            "bap:BUAP5A_506\tgn:bap\n" +
            "bap:BUAP5A_507\tgn:bap\n" +
            "bap:BUAP5A_508\tgn:bap\n" +
            "bap:BUAP5A_509\tgn:bap\n" +
            "bap:BUAP5A_510\tgn:bap\n" +
            "bap:BUAP5A_511\tgn:bap\n" +
            "bap:BUAP5A_512\tgn:bap\n" +
            "bap:BUAP5A_513\tgn:bap\n" +
            "bap:BUAP5A_514\tgn:bap\n" +
            "bap:BUAP5A_515\tgn:bap\n" +
            "bap:BUAP5A_516\tgn:bap\n" +
            "bap:BUAP5A_517\tgn:bap\n" +
            "bap:BUAP5A_518\tgn:bap\n" +
            "bap:BUAP5A_519\tgn:bap\n" +
            "bap:BUAP5A_520\tgn:bap\n" +
            "bap:BUAP5A_521\tgn:bap\n" +
            "bap:BUAP5A_522\tgn:bap\n" +
            "bap:BUAP5A_523\tgn:bap\n" +
            "bap:BUAP5A_524\tgn:bap\n" +
            "bap:BUAP5A_525\tgn:bap\n" +
            "bap:BUAP5A_526\tgn:bap\n" +
            "bap:BUAP5A_527\tgn:bap\n" +
            "bap:BUAP5A_528\tgn:bap\n" +
            "bap:BUAP5A_529\tgn:bap\n" +
            "bap:BUAP5A_530\tgn:bap\n" +
            "bap:BUAP5A_531\tgn:bap\n" +
            "bap:BUAP5A_532\tgn:bap\n" +
            "bap:BUAP5A_533\tgn:bap\n" +
            "bap:BUAP5A_534\tgn:bap\n" +
            "bap:BUAP5A_535\tgn:bap\n" +
            "bap:BUAP5A_536\tgn:bap\n" +
            "bap:BUAP5A_537\tgn:bap\n" +
            "bap:BUAP5A_538\tgn:bap\n" +
            "bap:BUAP5A_539\tgn:bap\n" +
            "bap:BUAP5A_540\tgn:bap\n" +
            "bap:BUAP5A_541\tgn:bap\n" +
            "bap:BUAP5A_542\tgn:bap\n" +
            "bap:BUAP5A_543\tgn:bap\n" +
            "bap:BUAP5A_544\tgn:bap\n" +
            "bap:BUAP5A_545\tgn:bap\n" +
            "bap:BUAP5A_546\tgn:bap\n" +
            "bap:BUAP5A_547\tgn:bap\n" +
            "bap:BUAP5A_548\tgn:bap\n" +
            "bap:BUAP5A_549\tgn:bap\n" +
            "bap:BUAP5A_550\tgn:bap\n" +
            "bap:BUAP5A_551\tgn:bap\n" +
            "bap:BUAP5A_552\tgn:bap\n" +
            "bap:BUAP5A_553\tgn:bap\n" +
            "bap:BUAP5A_554\tgn:bap\n" +
            "bap:BUAP5A_555\tgn:bap\n" +
            "bap:BUAP5A_556\tgn:bap\n" +
            "bap:BUAP5A_557\tgn:bap\n" +
            "bap:BUAP5A_558\tgn:bap\n" +
            "bap:BUAP5A_559\tgn:bap\n" +
            "bap:BUAP5A_560\tgn:bap\n" +
            "bap:BUAP5A_561\tgn:bap\n" +
            "bap:BUAP5A_562\tgn:bap\n" +
            "bap:BUAP5A_563\tgn:bap\n" +
            "bap:BUAP5A_564\tgn:bap\n" +
            "bap:BUAP5A_565\tgn:bap\n" +
            "bap:BUAP5A_566\tgn:bap\n" +
            "bap:BUAP5A_567\tgn:bap\n" +
            "bap:BUAP5A_568\tgn:bap\n" +
            "bap:BUAP5A_569\tgn:bap\n" +
            "bap:BUAP5A_570\tgn:bap\n" +
            "bap:BUAP5A_571\tgn:bap\n" +
            "bap:BUAP5A_572\tgn:bap\n" +
            "bap:BUAP5A_573\tgn:bap\n" +
            "bap:BUAP5A_574\tgn:bap\n" +
            "bap:BUAP5A_575\tgn:bap\n" +
            "bap:BUAP5A_576\tgn:bap\n" +
            "bap:BUAP5A_577\tgn:bap\n" +
            "bap:BUAP5A_578\tgn:bap\n" +
            "bap:BUAP5A_579\tgn:bap\n" +
            "bap:BUAP5A_580\tgn:bap\n" +
            "bap:BUAP5A_581\tgn:bap\n" +
            "bap:BUAP5A_582\tgn:bap\n" +
            "bap:BUAP5A_583\tgn:bap\n" +
            "bap:BUAP5A_584\tgn:bap\n" +
            "bap:BUAP5A_585\tgn:bap\n" +
            "bap:BUAP5A_586\tgn:bap\n" +
            "bap:BUAP5A_587\tgn:bap\n" +
            "bap:BUAP5A_588\tgn:bap\n" +
            "bap:BUAP5A_589\tgn:bap\n" +
            "bap:BUAP5A_590\tgn:bap\n" +
            "bap:BUAP5A_591\tgn:bap\n" +
            "bap:BUAP5A_592\tgn:bap\n" +
            "bap:BUAP5A_593\tgn:bap\n" +
            "bap:BUAP5A_594\tgn:bap\n" +
            "bap:BUAP5A_595\tgn:bap\n" +
            "bap:BUAP5A_596\tgn:bap\n" +
            "bap:BUAP5A_597\tgn:bap\n" +
            "bap:BUAP5A_598\tgn:bap\n" +
            "bap:BUAP5A_599\tgn:bap\n" +
            "bap:BUAP5A_600\tgn:bap";

    public static String geneEcLinks = "bpa:BPP1507\tec:7.4.2.8\n" +
            "bpa:BPP1523\tec:1.8.5.9\n" +
            "bpa:BPP1537\tec:2.3.1.191\n" +
            "bpa:BPP1538\tec:4.2.1.59\n" +
            "bpa:BPP1541\tec:3.1.26.4\n" +
            "bpa:BPP1560\tec:2.7.7.7\n" +
            "bpa:BPP1571\tec:2.2.1.6\n" +
            "bpa:BPP1635\tec:2.7.7.7\n" +
            "bpa:BPP2069\tec:5.4.2.10\n" +
            "bpa:BPP3390\tec:7.1.1.2\n" +
            "bpa:BPP1392\tec:1.1.1.100\n" +
            "bpa:BPP1442\tec:2.3.2.2\n" +
            "bpa:BPP1442\tec:3.4.19.13\n" +
            "bpa:BPP1462\tec:1.2.4.1\n" +
            "bpa:BPP1163\tec:2.1.1.193\n" +
            "bpa:BPP0243\tec:3.5.1.88\n" +
            "bpa:BPP0177\tec:3.4.25.2\n" +
            "bpa:BPP0015\tec:2.7.7.6\n" +
            "bpa:BPP2472\tec:3.6.4.12\n" +
            "bpa:BPP4216\tec:2.3.1.9\n" +
            "bpa:BPP4235\tec:1.17.99.9\n" +
            "bpa:BPP4239\tec:7.1.1.9\n" +
            "bpa:BPP4253\tec:1.4.1.13\n" +
            "bpa:BPP4254\tec:1.4.1.13\n" +
            "bpa:BPP4270\tec:4.3.2.10\n" +
            "bpa:BPP4272\tec:4.3.2.10\n" +
            "bpa:BPP4332\tec:1.16.3.1\n" +
            "bpa:BPP4352\tec:6.1.1.19\n" +
            "bpa:BPP4362\tec:6.4.1.4\n" +
            "bpa:BPP4363\tec:6.4.1.4\n" +
            "bpa:BPP4374\tec:5.6.2.1\n" +
            "bpa:BPP0275\tec:1.18.1.3\n" +
            "bpa:BPP2166\tec:1.2.1.3\n" +
            "bpa:BPP1673\tec:1.14.13.149\n" +
            "bpa:BPP1675\tec:1.14.13.149\n" +
            "bpa:BPP1700\tec:5.3.1.6\n" +
            "bpa:BPP1702\tec:3.1.4.3\n" +
            "bpa:BPP1705\tec:3.6.4.13\n" +
            "bpa:BPP1707\tec:2.7.7.7\n" +
            "bpa:BPP4379\tec:1.2.1.3\n" +
            "bpa:BPP4381\tec:2.2.1.6\n" +
            "bpa:BPP4385\tec:4.1.99.12\n" +
            "bpa:BPP4395\tec:3.6.1.7\n" +
            "bpa:BPP4399\tec:5.6.2.2\n" +
            "bpa:BPP4400\tec:2.7.7.7\n" +
            "bpa:BPP4403\tec:3.1.26.5\n" +
            "bpa:BPP3738\tec:1.4.5.1\n" +
            "bpa:BPP3739\tec:1.1.1.100\n" +
            "bpa:BPP3805\tec:2.3.1.9\n" +
            "bpa:BPP1729\tec:2.7.7.7\n" +
            "bpa:BPP1739\tec:2.1.1.79\n" +
            "bpa:BPP1744\tec:2.3.1.9\n" +
            "bpa:BPP1762\tec:2.6.99.2\n" +
            "bpa:BPP4246\tec:3.1.4.53\n" +
            "bpa:BPP1775\tec:2.3.1.47\n" +
            "bpa:BPP1823\tec:1.8.1.2\n" +
            "bpa:BPP1833\tec:2.5.1.32\n" +
            "bpa:BPP1869\tec:2.7.7.7\n" +
            "bpa:BPP1879\tec:5.6.2.2\n" +
            "bpa:BPP1881\tec:5.6.2.2\n" +
            "bpa:BPP1910\tec:7.4.2.8\n" +
            "bpa:BPP1926\tec:2.10.1.1\n" +
            "bpa:BPP1927\tec:2.7.7.77\n" +
            "bpa:BPP1933\tec:1.17.1.9\n" +
            "bpa:BPP1942\tec:4.2.1.33\n" +
            "bpa:BPP1942\tec:4.2.1.35\n" +
            "bpa:BPP1943\tec:4.2.1.33\n" +
            "bpa:BPP1943\tec:4.2.1.35\n" +
            "bpa:BPP1969\tec:1.11.1.28\n" +
            "bpa:BPP1975\tec:7.4.2.1\n" +
            "bpa:BPP1983\tec:3.4.23.36\n" +
            "bpa:BPP1990\tec:3.6.4.12\n" +
            "bpa:BPP2019\tec:2.8.3.6\n" +
            "bpa:BPP2020\tec:2.8.3.6\n" +
            "bpa:BPP2042\tec:6.2.1.3\n" +
            "bpa:BPP2066\tec:2.1.1.166\n" +
            "bpa:BPP2076\tec:7.3.2.1\n" +
            "bpa:BPP2082\tec:4.2.1.1\n" +
            "bpa:BPP2100\tec:1.1.1.100\n" +
            "bpa:BPP2104\tec:1.1.1.157\n" +
            "bpa:BPP2716\tec:1.20.4.1\n" +
            "bpa:BPP0096\tec:2.3.3.9\n" +
            "bpa:BPP1351\tec:1.3.99.16\n" +
            "bpa:BPP0871\tec:4.1.1.76\n" +
            "bpa:BPP3995\tec:2.1.1.163\n" +
            "bpa:BPP3995\tec:2.1.1.201\n" +
            "bpa:BPP3286\tec:2.7.7.7\n" +
            "bpa:BPP2642\tec:2.5.1.61\n" +
            "bpa:BPP1068\tec:7.1.1.3\n" +
            "bpa:BPP1093\tec:7.1.1.3\n" +
            "bpa:BPP1067\tec:7.1.1.3\n" +
            "bpa:BPP3463\tec:1.8.1.9\n" +
            "bpa:BPP4043\tec:5.3.1.28\n" +
            "bpa:BPP4057\tec:6.3.5.6\n" +
            "bpa:BPP4057\tec:6.3.5.7\n" +
            "bpa:BPP4058\tec:6.3.5.6\n" +
            "bpa:BPP4058\tec:6.3.5.7\n" +
            "bpa:BPP4059\tec:6.3.5.6\n" +
            "bpa:BPP4059\tec:6.3.5.7\n" +
            "bpa:BPP4061\tec:3.1.11.2\n" +
            "bpa:BPP4129\tec:3.4.17.11\n" +
            "bpa:BPP4130\tec:3.6.4.12\n" +
            "bpa:BPP4156\tec:4.1.3.27\n" +
            "bpa:BPP4157\tec:4.1.3.27\n" +
            "bpa:BPP3381\tec:7.1.1.2\n" +
            "bpa:BPP3380\tec:7.1.1.2\n" +
            "bpa:BPP3379\tec:7.1.1.2\n" +
            "bpa:BPP1475\tec:2.1.1.80\n" +
            "bpa:BPP1476\tec:3.1.1.61\n" +
            "bpa:BPP1476\tec:3.5.1.44\n" +
            "bpa:BPP1658\tec:7.3.2.3\n" +
            "bpa:BPP1660\tec:2.7.7.4\n" +
            "bpa:BPP1661\tec:2.7.7.4\n" +
            "bpa:BPP4137\tec:7.1.2.2\n" +
            "bpa:BPP4137\tec:7.2.2.1\n" +
            "bpa:BPP0816\tec:2.7.1.148\n" +
            "bpa:BPP1472\tec:2.7.13.3\n" +
            "bpa:BPP1024\tec:7.1.1.7\n" +
            "bpa:BPP3577\tec:7.1.1.7\n" +
            "bpa:BPP4025\tec:7.1.1.7\n" +
            "bpa:BPP1025\tec:7.1.1.7\n" +
            "bpa:BPP3576\tec:7.1.1.7\n" +
            "bpa:BPP4024\tec:7.1.1.7\n" +
            "bpa:BPP3383\tec:7.1.1.2\n" +
            "bpa:BPP2028\tec:2.8.1.7\n" +
            "bpa:BPP0002\tec:2.1.1.170\n" +
            "bpa:BPP1156\tec:4.1.1.98\n" +
            "bpa:BPP0819\tec:3.1.1.29\n" +
            "bpa:BPP3387\tec:7.1.1.2\n" +
            "bpa:BPP3227\tec:1.3.5.1\n" +
            "bpa:BPP3227\tec:1.3.5.4\n" +
            "bpa:BPP3780\tec:1.7.1.17\n" +
            "bpa:BPP1779\tec:7.1.1.9\n" +
            "bpa:BPP0350\tec:4.1.3.34\n" +
            "bpa:BPP0352\tec:4.2.1.2\n" +
            "bpa:BPP0353\tec:4.2.1.2\n" +
            "bpa:BPP0359\tec:2.8.3.5\n" +
            "bpa:BPP0057\tec:2.7.7.6\n" +
            "bpa:BPP3317\tec:5.4.99.24\n" +
            "bpa:BPP4150\tec:5.3.4.1\n" +
            "bpa:BPP2482\tec:3.1.3.11\n" +
            "bpa:BPP2982\tec:7.2.2.7\n" +
            "bpa:BPP2921\tec:6.3.1.20\n" +
            "bpa:BPP2970\tec:1.2.1.18\n" +
            "bpa:BPP2970\tec:1.2.1.27\n" +
            "bpa:BPP2973\tec:3.6.4.12\n" +
            "bpa:BPP1947\tec:5.4.99.12\n" +
            "bpa:BPP3980\tec:3.2.2.31\n" +
            "bpa:BPP2045\tec:6.1.1.6\n" +
            "bpa:BPP0246\tec:3.1.3.18\n" +
            "bpa:BPP0249\tec:1.16.3.1\n" +
            "bpa:BPP0259\tec:2.1.1.176\n" +
            "bpa:BPP0251\tec:5.3.2.6\n" +
            "bpa:BPP0278\tec:1.2.5.3\n" +
            "bpa:BPP0279\tec:1.2.5.3\n" +
            "bpa:BPP0287\tec:2.1.1.197\n" +
            "bpa:BPP0289\tec:2.1.1.207\n" +
            "bpa:BPP0301\tec:4.1.99.17\n" +
            "bpa:BPP0313\tec:2.5.1.90\n" +
            "bpa:BPP0316\tec:3.5.2.14\n" +
            "bpa:BPP0330\tec:2.6.1.21\n" +
            "bpa:BPP0334\tec:2.2.1.6\n" +
            "bpa:BPP2628\tec:7.2.2.7\n" +
            "bpa:BPP2286\tec:6.3.4.19\n" +
            "bpa:BPP0297\tec:3.4.21.102\n" +
            "bpa:BPP4192\tec:1.14.11.33\n" +
            "bpa:BPP2285\tec:6.4.1.2\n" +
            "bpa:BPP2285\tec:2.1.3.15\n" +
            "bpa:BPP3391\tec:7.1.1.2\n" +
            "bpa:BPP4135\tec:7.1.2.2\n" +
            "bpa:BPP4135\tec:7.2.2.1\n" +
            "bpa:BPP3382\tec:7.1.1.2\n" +
            "bpa:BPP3743\tec:7.4.2.8\n" +
            "bpa:BPP3296\tec:3.4.21.89\n" +
            "bpa:BPP1180\tec:2.7.7.75\n" +
            "bpa:BPP1192\tec:4.1.2.13\n" +
            "bpa:BPP1198\tec:5.4.99.18\n" +
            "bpa:BPP1199\tec:6.3.4.18\n" +
            "bpa:BPP1210\tec:2.2.1.6\n" +
            "bpa:BPP1221\tec:2.7.7.7\n" +
            "bpa:BPP1229\tec:6.3.5.5\n" +
            "bpa:BPP1230\tec:6.3.5.5\n" +
            "bpa:BPP4242\tec:7.1.1.9\n" +
            "bpa:BPP4243\tec:7.1.1.9\n" +
            "bpa:BPP0014\tec:2.7.7.6\n" +
            "bpa:BPP0206\tec:3.2.2.27\n" +
            "bpa:BPP0229\tec:6.2.1.3\n" +
            "bpa:BPP2058\tec:7.2.2.7\n" +
            "bpa:BPP3389\tec:7.1.1.2\n" +
            "bpa:BPP3388\tec:7.1.1.2\n" +
            "bpa:BPP3385\tec:7.1.1.2\n" +
            "bpa:BPP3384\tec:7.1.1.2\n" +
            "bpa:BPP0585\tec:1.3.8.7\n" +
            "bpa:BPP3321\tec:6.4.1.2\n" +
            "bpa:BPP3321\tec:2.1.3.15\n" +
            "bpa:BPP2529\tec:2.5.1.25\n" +
            "bpa:BPP4406\tec:1.11.1.6\n" +
            "bpa:BPP0475\tec:3.1.6.6\n" +
            "bpa:BPP0498\tec:3.5.1.28\n" +
            "bpa:BPP0501\tec:3.6.5.4\n" +
            "bpa:BPP0575\tec:1.3.8.7\n" +
            "bpa:BPP0607\tec:1.1.1.100\n" +
            "bpa:BPP0608\tec:2.3.1.9\n" +
            "bpa:BPP0609\tec:6.2.1.1\n" +
            "bpa:BPP0613\tec:1.3.8.7\n" +
            "bpa:BPP0636\tec:1.5.1.38\n" +
            "bpa:BPP0670\tec:1.2.1.3\n" +
            "bpa:BPP0737\tec:7.3.2.5\n" +
            "bpa:BPP0765\tec:4.1.1.19\n" +
            "bpa:BPP3378\tec:7.1.1.2\n" +
            "bpa:BPP0864\tec:7.1.1.2\n" +
            "bpa:BPP3228\tec:1.3.5.1\n" +
            "bpa:BPP3228\tec:1.3.5.4\n" +
            "bpa:BPP3986\tec:2.8.1.10\n" +
            "bpa:BPP0787\tec:2.1.1.45\n" +
            "bpa:BPP0795\tec:6.3.5.4\n" +
            "bpa:BPP0829\tec:2.2.1.6\n" +
            "bpa:BPP0853\tec:7.3.2.2\n" +
            "bpa:BPP0861\tec:3.1.3.27\n" +
            "bpa:BPP0955\tec:2.5.1.9\n" +
            "bpa:BPP1011\tec:6.3.5.6\n" +
            "bpa:BPP1011\tec:6.3.5.7\n" +
            "bpa:BPP2810\tec:1.3.8.7\n" +
            "bpa:BPP2841\tec:2.7.7.101\n" +
            "bpa:BPP2854\tec:6.1.1.21\n" +
            "bpa:BPP3006\tec:2.7.7.6\n" +
            "bpa:BPP3032\tec:6.3.5.6\n" +
            "bpa:BPP3032\tec:6.3.5.7\n" +
            "bpa:BPP3044\tec:5.2.1.8\n" +
            "bpa:BPP3047\tec:1.8.1.4\n" +
            "bpa:BPP3051\tec:2.1.1.190\n" +
            "bpa:BPP3057\tec:3.1.3.5\n" +
            "bpa:BPP2684\tec:4.2.1.33\n" +
            "bpa:BPP2684\tec:4.2.1.35\n" +
            "bpa:BPP3097\tec:1.1.5.3\n" +
            "bpa:BPP3123\tec:5.1.3.20\n" +
            "bpa:BPP3134\tec:5.6.2.2\n" +
            "bpa:BPP3941\tec:1.3.8.7\n" +
            "bpa:BPP3907\tec:6.1.1.13\n" +
            "bpa:BPP3599\tec:4.1.3.38\n" +
            "bpa:BPP3333\tec:7.1.1.11\n" +
            "bpa:BPP3333\tec:7.2.1.2\n" +
            "bpa:BPP1751\tec:1.15.1.1\n" +
            "bpa:BPP4354\tec:1.8.4.15\n" +
            "bpa:BPP2490\tec:1.1.2.4\n" +
            "bpa:BPP2491\tec:1.1.3.15\n" +
            "bpa:BPP2497\tec:1.6.1.2\n" +
            "bpa:BPP2497\tec:7.1.1.1\n" +
            "bpa:BPP2498\tec:1.6.1.2\n" +
            "bpa:BPP2498\tec:7.1.1.1\n" +
            "bpa:BPP2499\tec:1.6.1.2\n" +
            "bpa:BPP2499\tec:7.1.1.1\n" +
            "bpa:BPP2531\tec:2.1.1.185\n" +
            "bpa:BPP2555\tec:4.1.2.20\n" +
            "bpa:BPP2566\tec:3.1.11.6\n" +
            "bpa:BPP2569\tec:2.3.1.16\n" +
            "bpa:BPP2598\tec:6.1.1.20\n" +
            "bpa:BPP2613\tec:3.1.21.1\n" +
            "bpa:BPP2617\tec:2.1.3.2\n" +
            "bpa:BPP2638\tec:6.2.1.5\n" +
            "bpa:BPP2639\tec:6.2.1.5\n" +
            "bpa:BPP2643\tec:4.2.1.75\n" +
            "bpa:BPP2644\tec:2.1.1.107\n" +
            "bpa:BPP3962\tec:3.4.23.43\n" +
            "bpa:BPP3978\tec:3.5.1.44\n" +
            "bpa:BPP4204\tec:7.1.1.9\n" +
            "bpa:BPP1572\tec:4.1.3.34\n" +
            "bpa:BPP3258\tec:3.4.11.19\n" +
            "bpa:BPP3728\tec:3.1.3.25\n" +
            "bpa:BPP2201\tec:2.3.2.2\n" +
            "bpa:BPP2201\tec:3.4.19.13\n" +
            "bpa:BPP2231\tec:7.4.2.8\n" +
            "bpa:BPP2260\tec:4.3.3.7\n" +
            "bpa:BPP2280\tec:3.6.1.54\n" +
            "bpa:BPP2283\tec:6.1.1.16\n" +
            "bpa:BPP2378\tec:7.4.2.8\n" +
            "bpa:BPP2394\tec:4.2.1.3\n" +
            "bpa:BPP2400\tec:4.2.1.3\n" +
            "bpa:BPP2400\tec:4.2.1.99\n" +
            "bpa:BPP0505\tec:2.1.1.33\n" +
            "bpa:BPP2699\tec:7.6.2.5\n" +
            "bpa:BPP2703\tec:7.4.2.1\n" +
            "bpa:BPP3513\tec:4.1.3.34\n" +
            "bpa:BPP3529\tec:2.3.2.2\n" +
            "bpa:BPP3529\tec:3.4.19.13\n" +
            "bpa:BPP3538\tec:2.3.2.6\n" +
            "bpa:BPP3556\tec:2.7.7.9\n" +
            "bpa:BPP3561\tec:3.4.11.9\n" +
            "bpa:BPP3567\tec:3.1.21.10\n" +
            "bpa:BPP3568\tec:3.6.4.12\n" +
            "bpa:BPP3571\tec:3.6.4.12\n" +
            "bpa:BPP3572\tec:3.6.4.12\n" +
            "bpa:BPP3610\tec:3.5.1.10\n" +
            "bpa:BPP3619\tec:4.2.1.2\n" +
            "bpa:BPP3621\tec:3.5.1.28\n" +
            "bpa:BPP3646\tec:4.2.1.2\n" +
            "bpa:BPP3650\tec:4.2.1.33\n" +
            "bpa:BPP3650\tec:4.2.1.35\n" +
            "bpa:BPP3651\tec:4.2.1.33\n" +
            "bpa:BPP3651\tec:4.2.1.35\n" +
            "bpa:BPP3654\tec:6.1.1.14\n" +
            "bpa:BPP3655\tec:6.1.1.14\n" +
            "bpa:BPP3657\tec:2.3.1.51\n" +
            "bpa:BPP3660\tec:3.5.1.88\n" +
            "bpa:BPP1864\tec:5.4.99.25\n" +
            "bpa:BPP4283\tec:7.1.1.8\n" +
            "bpa:BPP2451\tec:7.2.2.7\n" +
            "bpa:BPP2462\tec:3.1.11.6\n" +
            "bpa:BPP2463\tec:2.5.1.1\n" +
            "bpa:BPP2463\tec:2.5.1.10\n" +
            "bpa:BPP2464\tec:2.2.1.7\n" +
            "bpa:BPP2476\tec:1.11.1.24\n" +
            "bpa:BPP3362\tec:1.11.1.28\n" +
            "bpa:BPP3184\tec:6.3.2.29\n" +
            "bpa:BPP3184\tec:6.3.2.30\n" +
            "bpa:BPP3198\tec:5.4.99.23\n" +
            "bpa:BPP3220\tec:1.2.4.1\n" +
            "bpa:BPP3243\tec:6.2.1.1\n" +
            "bpa:BPP3265\tec:2.8.3.5\n" +
            "bpa:BPP3266\tec:2.8.3.5\n" +
            "bpa:BPP3268\tec:1.5.5.1\n" +
            "bpa:BPP3295\tec:3.1.26.3\n" +
            "bpa:BPP3307\tec:2.3.1.180\n" +
            "bpa:BPP3308\tec:2.3.1.274\n" +
            "bpa:BPP3316\tec:3.1.3.18\n" +
            "bpa:BPP3322\tec:4.2.1.20\n" +
            "bpa:BPP0383\tec:2.1.1.297\n" +
            "bpa:BPP0388\tec:1.2.5.3\n" +
            "bpa:BPP0389\tec:1.2.5.3\n" +
            "bpa:BPP0403\tec:6.3.5.6\n" +
            "bpa:BPP0403\tec:6.3.5.7\n" +
            "bpa:BPP0404\tec:4.2.1.1\n" +
            "bpa:BPP0411\tec:2.8.3.6\n" +
            "bpa:BPP0412\tec:2.8.3.6\n" +
            "bpa:BPP0432\tec:3.1.4.58\n" +
            "bpa:BPP3598\tec:2.6.1.85\n" +
            "bpa:BPP3323\tec:4.2.1.20\n" +
            "bpa:BPP3665\tec:2.1.1.182\n" +
            "bpa:BPP3666\tec:5.2.1.8\n" +
            "bpa:BPP3707\tec:3.2.2.20\n" +
            "bpa:BPP2281\tec:5.2.1.8\n" +
            "bpa:BPP3746\tec:3.5.1.108\n" +
            "bpa:BPP3752\tec:2.4.1.227\n" +
            "bpa:BPP3754\tec:6.3.2.9\n" +
            "bpa:BPP3757\tec:3.4.16.4\n" +
            "bpa:BPP3759\tec:2.1.1.199\n" +
            "bpa:BPP3767\tec:1.13.11.27\n" +
            "bpa:BPP3779\tec:1.1.1.31\n" +
            "bpa:BPP3831\tec:2.7.7.7\n" +
            "bpa:BPP3834\tec:3.1.26.4\n" +
            "bpa:BPP3848\tec:1.1.99.1\n" +
            "bpa:BPP3855\tec:3.5.1.5\n" +
            "bpa:BPP3856\tec:3.5.1.5\n" +
            "bpa:BPP3858\tec:3.5.1.5\n" +
            "bpa:BPP3903\tec:1.17.4.1\n" +
            "bpa:BPP3904\tec:1.17.4.1\n" +
            "bpa:BPP3918\tec:4.2.1.10\n" +
            "bpa:BPP3920\tec:6.3.2.45\n" +
            "bpa:BPP3925\tec:2.4.1.129\n" +
            "bpa:BPP3929\tec:3.5.2.3\n" +
            "bpa:BPP3930\tec:2.1.3.2\n" +
            "bpa:BPP3939\tec:3.1.2.23\n" +
            "bpa:BPP0073\tec:3.1.5.1\n" +
            "bpa:BPP0100\tec:2.5.1.39\n" +
            "bpa:BPP0111\tec:1.1.99.1\n" +
            "bpa:BPP0121\tec:5.1.3.2\n" +
            "bpa:BPP0123\tec:6.3.5.4\n" +
            "bpa:BPP0136\tec:6.3.5.4\n" +
            "bpa:BPP0157\tec:2.4.99.12\n" +
            "bpa:BPP0157\tec:2.4.99.13\n" +
            "bpa:BPP0157\tec:2.4.99.14\n" +
            "bpa:BPP0157\tec:2.4.99.15\n" +
            "bpa:BPP0167\tec:2.6.1.21\n" +
            "bpa:BPP0169\tec:2.3.1.181\n" +
            "bpa:BPP0170\tec:2.8.1.8\n" +
            "bpa:BPP0190\tec:2.3.1.241\n" +
            "bpa:BPP0191\tec:2.3.1.241\n" +
            "bpa:BPP1810\tec:3.6.1.13\n" +
            "bpa:BPP3386\tec:7.1.1.2\n" +
            "bpa:BPP3331\tec:1.18.1.2\n" +
            "bpa:BPP3331\tec:1.19.1.1\n" +
            "bpa:BPP3366\tec:2.7.7.60\n" +
            "bpa:BPP3371\tec:2.2.1.6\n" +
            "bpa:BPP3408\tec:3.5.2.14\n" +
            "bpa:BPP3417\tec:1.1.1.100\n" +
            "bpa:BPP3436\tec:2.2.1.6\n" +
            "bpa:BPP3437\tec:2.2.1.6\n" +
            "bpa:BPP3441\tec:1.1.1.30\n" +
            "bpa:BPP3469\tec:7.2.2.6\n" +
            "bpa:BPP3365\tec:4.6.1.12\n" +
            "bpa:BPP0385\tec:2.5.1.129\n" +
            "bpa:BPP1094\tec:7.1.1.3\n" +
            "bpa:BPP1103\tec:1.17.1.9\n" +
            "bpa:BPP1105\tec:1.17.1.9\n" +
            "bpa:BPP1109\tec:4.1.99.22\n" +
            "bpa:BPP1110\tec:2.10.1.1\n" +
            "bpa:BPP1111\tec:2.7.7.75\n" +
            "bpa:BPP1112\tec:2.8.1.12\n" +
            "bpa:BPP1114\tec:4.6.1.17\n" +
            "bpa:BPP1116\tec:3.1.1.45\n" +
            "bpa:BPP1121\tec:2.8.1.6\n" +
            "bpa:BPP1126\tec:4.2.1.3\n" +
            "bpa:BPP3324\tec:3.5.1.16\n" +
            "bpa:BPP0767\tec:1.15.1.1\n" +
            "bpa:BPP2574\tec:2.4.1.129\n" +
            "bpa:BPP3200\tec:3.6.4.12\n" +
            "bpa:BPP1637\tec:1.14.12.17\n" +
            "bpa:BPP4029\tec:3.1.3.45\n" +
            "bpa:BPP0070\tec:2.4.1.129\n" +
            "bpa:BPP0070\tec:3.4.16.4\n" +
            "bpa:BPP3196\tec:2.3.1.304\n" +
            "bpa:BPP0862\tec:3.5.1.42\n" +
            "bpa:BPP1859\tec:5.4.99.22\n" +
            "bpa:BPP4053\tec:3.4.16.4\n" +
            "bpa:BPP2567\tec:1.15.1.1\n" +
            "bpa:BPP3894\tec:3.6.4.13\n" +
            "bpa:BPP2279\tec:3.6.1.27\n" +
            "bpa:BPP1141\tec:2.8.4.3\n" +
            "bpa:BPP1764\tec:3.2.1.52\n" +
            "bpa:BPP2912\tec:1.20.4.1\n" +
            "bpa:BPP3697\tec:1.13.11.16\n" +
            "bpa:BPP1652\tec:4.2.3.12\n" +
            "bpa:BPP1652\tec:4.1.2.50\n" +
            "bpa:BPP0791\tec:1.1.1.136\n" +
            "bpa:BPP2957\tec:1.1.1.136\n" +
            "bpa:BPP1852\tec:1.17.7.4\n" +
            "bpa:BPP0789\tec:1.14.99.60\n" +
            "bpa:BPP2080\tec:1.1.99.3\n" +
            "bpa:BPP2079\tec:1.1.99.3\n" +
            "bpa:BPP2133\tec:3.5.1.6\n" +
            "bpa:BPP2133\tec:3.5.1.87\n" +
            "bpa:BPP2132\tec:1.1.1.95\n" +
            "bpa:BPP2132\tec:1.1.1.399\n" +
            "bpa:BPP0811\tec:1.14.13.127\n" +
            "bpa:BPP0847\tec:2.7.7.3\n" +
            "bpa:BPP1888\tec:2.3.1.178\n" +
            "bpa:BPP1890\tec:4.2.1.108\n" +
            "bpa:BPP2420\tec:1.3.3.3\n" +
            "bpa:BPP1940\tec:7.3.2.6\n" +
            "bpa:BPP3997\tec:2.7.13.3\n" +
            "bpa:BPP1889\tec:2.6.1.76\n" +
            "bpa:BPP1914\tec:2.7.1.39\n" +
            "bpa:BPP4032\tec:7.5.2.5\n" +
            "bpa:BPP3471\tec:2.7.13.3\n" +
            "bpa:BPP0267\tec:2.7.7.76\n" +
            "bpa:BPP4420\tec:2.7.13.3\n" +
            "bpa:BPP0197\tec:1.5.1.54\n" +
            "bpa:BPP3360\tec:2.7.13.3\n" +
            "bpa:BPP3575\tec:2.7.13.3\n" +
            "bpa:BPP4251\tec:1.1.1.284\n" +
            "bpa:BPP4251\tec:1.1.1.1\n" +
            "bpa:BPP4006\tec:3.5.2.9\n" +
            "bpa:BPP3606\tec:4.3.2.7\n" +
            "bpa:BPP1237\tec:3.1.3.97\n" +
            "bpa:BPP1148\tec:3.4.16.4\n" +
            "bpa:BPP3209\tec:1.14.11.17\n" +
            "bpa:BPP4410\tec:1.14.11.17\n" +
            "bpa:BPP1668\tec:3.1.3.100\n" +
            "bpa:BPP2987\tec:2.7.13.3\n" +
            "bpa:BPP3668\tec:2.7.1.221\n" +
            "bpa:BPP3281\tec:1.2.1.18\n" +
            "bpa:BPP3281\tec:1.2.1.27\n" +
            "bpa:BPP2245\tec:3.1.3.3\n" +
            "bpa:BPP3029\tec:2.7.13.3\n" +
            "bpa:BPP0704\tec:1.3.99.16\n" +
            "bpa:BPP0703\tec:1.3.99.16\n" +
            "bpa:BPP3768\tec:1.8.4.11\n" +
            "bpa:BPP1010\tec:2.7.7.47\n" +
            "bpa:BPP2857\tec:2.1.1.192\n" +
            "bpa:BPP0524\tec:2.7.11.1\n" +
            "bpa:BPP2765\tec:2.7.11.1\n" +
            "bpa:BPP4041\tec:2.1.1.198\n" +
            "bpa:BPP3312\tec:2.1.1.198\n" +
            "bpa:BPP0419\tec:6.3.4.20\n" +
            "bpa:BPP1993\tec:2.1.1.298\n" +
            "bpa:BPP1630\tec:3.1.3.16\n" +
            "bpa:BPP3349\tec:1.8.4.12\n" +
            "bpa:BPP0203\tec:2.7.7.72\n" +
            "bpa:BPP3046\tec:2.1.1.266\n" +
            "bpa:BPP0166\tec:3.4.16.4\n" +
            "bpa:BPP1851\tec:5.2.1.8\n" +
            "bpa:BPP1529\tec:2.7.4.22\n" +
            "bpa:BPP3303\tec:2.3.1.179\n" +
            "bpa:BPP2702\tec:1.9.6.1\n" +
            "bpa:BPP1848\tec:1.13.11.11\n" +
            "bpa:BPP1543\tec:2.7.4.28\n" +
            "bpa:BPP1543\tec:2.7.11.33\n" +
            "bpa:BPP2417\tec:2.1.1.177\n" +
            "bpa:BPP2465\tec:3.5.4.16\n" +
            "bpa:BPP3879\tec:2.7.1.170\n" +
            "bpa:BPP4027\tec:6.3.1.21\n" +
            "bpa:BPP0915\tec:6.2.1.3\n" +
            "bpa:BPP3318\tec:3.1.26.12\n" +
            "bpa:BPP0381\tec:1.2.1.70\n" +
            "bpa:BPP1989\tec:1.14.19.1\n" +
            "bpa:BPP1451\tec:5.4.99.21\n" +
            "bpa:BPP3058\tec:2.3.1.275\n" +
            "bpa:BPP0858\tec:2.5.1.78\n" +
            "bpa:BPP1450\tec:4.1.3.40\n" +
            "bpa:BPP0813\tec:3.2.2.23\n" +
            "bpa:BPP0813\tec:4.2.99.18\n" +
            "bpa:BPP2295\tec:1.4.1.21\n" +
            "bpa:BPP3256\tec:3.4.11.9\n" +
            "bpa:BPP3008\tec:7.4.2.1\n" +
            "bpa:BPP0268\tec:4.1.1.46\n" +
            "bpa:BPP3972\tec:7.4.2.1\n" +
            "bpa:BPP4078\tec:7.4.2.1\n" +
            "bpa:BPP0223\tec:6.2.1.30\n" +
            "bpa:BPP3999\tec:2.7.13.3\n" +
            "bpa:BPP3334\tec:4.2.99.18\n" +
            "bpa:BPP0507\tec:2.8.3.16\n" +
            "bpa:BPP3838\tec:1.3.1.9\n" +
            "bpa:BPP3838\tec:1.3.1.10\n" +
            "bpa:BPP1433\tec:4.3.2.1\n" +
            "bpa:BPP1458\tec:3.4.24.70\n" +
            "bpa:BPP1463\tec:2.3.1.12\n" +
            "bpa:BPP1464\tec:1.8.1.4\n" +
            "bpa:BPP1522\tec:2.4.2.14\n" +
            "bpa:BPP1525\tec:2.7.7.59\n" +
            "bpa:BPP1526\tec:3.4.11.18\n" +
            "bpa:BPP1532\tec:2.7.7.41\n" +
            "bpa:BPP1533\tec:1.1.1.267\n" +
            "bpa:BPP1539\tec:2.3.1.129\n" +
            "bpa:BPP1540\tec:2.4.1.182\n" +
            "bpa:BPP1544\tec:2.7.9.2\n" +
            "bpa:BPP1554\tec:1.1.1.31\n" +
            "bpa:BPP1559\tec:2.7.4.9\n" +
            "bpa:BPP1600\tec:3.4.17.13\n" +
            "bpa:BPP1659\tec:1.8.4.8\n" +
            "bpa:BPP1659\tec:1.8.4.10\n" +
            "bpa:BPP1681\tec:6.2.1.30\n" +
            "bpa:BPP1726\tec:3.5.1.4\n" +
            "bpa:BPP1728\tec:1.2.1.41\n" +
            "bpa:BPP1731\tec:6.1.1.4\n" +
            "bpa:BPP1763\tec:2.7.8.7\n" +
            "bpa:BPP1769\tec:4.1.3.1\n" +
            "bpa:BPP1776\tec:2.6.1.62\n" +
            "bpa:BPP1835\tec:5.1.1.1\n" +
            "bpa:BPP1871\tec:3.1.1.45\n" +
            "bpa:BPP1873\tec:4.3.1.17\n" +
            "bpa:BPP1875\tec:2.7.7.42\n" +
            "bpa:BPP1875\tec:2.7.7.89\n" +
            "bpa:BPP1899\tec:4.1.1.11\n" +
            "bpa:BPP1900\tec:1.17.1.1\n" +
            "bpa:BPP1901\tec:1.4.5.1\n" +
            "bpa:BPP1923\tec:4.2.3.5\n" +
            "bpa:BPP1945\tec:1.2.1.11\n" +
            "bpa:BPP1951\tec:5.3.1.24\n" +
            "bpa:BPP1963\tec:6.1.1.3\n" +
            "bpa:BPP1965\tec:6.3.2.3\n" +
            "bpa:BPP1976\tec:3.6.1.23\n" +
            "bpa:BPP1984\tec:6.1.1.5\n" +
            "bpa:BPP1994\tec:3.5.1.18\n" +
            "bpa:BPP1995\tec:2.3.1.117\n" +
            "bpa:BPP2006\tec:3.4.21.92\n" +
            "bpa:BPP2008\tec:3.4.21.53\n" +
            "bpa:BPP2016\tec:1.1.1.100\n" +
            "bpa:BPP2023\tec:3.4.21.88\n" +
            "bpa:BPP2068\tec:2.5.1.15\n" +
            "bpa:BPP2072\tec:2.7.4.1\n" +
            "bpa:BPP2084\tec:1.1.1.61\n" +
            "bpa:BPP2124\tec:4.3.1.15\n" +
            "bpa:BPP2157\tec:2.5.1.7\n" +
            "bpa:BPP2160\tec:2.1.1.67\n" +
            "bpa:BPP2247\tec:5.1.1.1\n" +
            "bpa:BPP2277\tec:3.1.3.25\n" +
            "bpa:BPP2287\tec:2.7.2.4\n" +
            "bpa:BPP2338\tec:6.2.1.3\n" +
            "bpa:BPP2344\tec:2.3.1.9\n" +
            "bpa:BPP2350\tec:5.3.1.9\n" +
            "bpa:BPP2351\tec:2.3.1.30\n" +
            "bpa:BPP2356\tec:1.2.1.16\n" +
            "bpa:BPP2356\tec:1.2.1.79\n" +
            "bpa:BPP2356\tec:1.2.1.20\n" +
            "bpa:BPP2419\tec:2.7.7.18\n" +
            "bpa:BPP2421\tec:6.3.4.13\n" +
            "bpa:BPP2445\tec:1.6.5.5\n" +
            "bpa:BPP2479\tec:1.1.1.3\n" +
            "bpa:BPP2480\tec:4.2.3.1\n" +
            "bpa:BPP2483\tec:3.4.11.2\n" +
            "bpa:BPP2488\tec:2.5.1.54\n" +
            "bpa:BPP2503\tec:2.8.1.13\n" +
            "bpa:BPP2506\tec:4.3.2.2\n" +
            "bpa:BPP2541\tec:6.3.4.5\n" +
            "bpa:BPP2560\tec:2.7.4.3\n" +
            "bpa:BPP2561\tec:2.7.7.38\n" +
            "bpa:BPP2563\tec:2.7.1.130\n" +
            "bpa:BPP2625\tec:1.1.1.100\n" +
            "bpa:BPP2636\tec:2.1.1.14\n" +
            "bpa:BPP2646\tec:3.6.1.1\n" +
            "bpa:BPP2652\tec:1.2.5.1\n" +
            "bpa:BPP2721\tec:1.4.5.1\n" +
            "bpa:BPP0006\tec:3.5.1.1\n" +
            "bpa:BPP0063\tec:4.2.1.24\n" +
            "bpa:BPP0068\tec:4.1.1.20\n" +
            "bpa:BPP0071\tec:2.7.1.71\n" +
            "bpa:BPP0072\tec:4.2.3.4\n" +
            "bpa:BPP0115\tec:6.1.1.12\n" +
            "bpa:BPP0171\tec:5.1.3.13\n" +
            "bpa:BPP0172\tec:1.1.1.133\n" +
            "bpa:BPP0173\tec:4.2.1.46\n" +
            "bpa:BPP0189\tec:5.1.1.7\n" +
            "bpa:BPP0192\tec:2.5.1.6\n" +
            "bpa:BPP0195\tec:3.3.1.1\n" +
            "bpa:BPP0239\tec:4.1.3.4\n" +
            "bpa:BPP0244\tec:2.1.2.9\n" +
            "bpa:BPP0257\tec:1.4.1.21\n" +
            "bpa:BPP0291\tec:1.1.1.94\n" +
            "bpa:BPP0295\tec:5.4.2.11\n" +
            "bpa:BPP0308\tec:2.7.2.11\n" +
            "bpa:BPP0319\tec:1.2.1.16\n" +
            "bpa:BPP0319\tec:1.2.1.79\n" +
            "bpa:BPP0319\tec:1.2.1.20\n" +
            "bpa:BPP0408\tec:3.1.1.24\n" +
            "bpa:BPP0410\tec:5.3.3.4\n" +
            "bpa:BPP0418\tec:4.1.1.31\n" +
            "bpa:BPP0427\tec:2.3.3.13\n" +
            "bpa:BPP0429\tec:4.2.1.9\n" +
            "bpa:BPP0431\tec:3.1.3.5\n" +
            "bpa:BPP0504\tec:3.4.11.5\n" +
            "bpa:BPP0508\tec:4.1.3.4\n" +
            "bpa:BPP0541\tec:2.3.1.9\n" +
            "bpa:BPP0656\tec:1.3.8.6\n" +
            "bpa:BPP0666\tec:2.5.1.18\n" +
            "bpa:BPP0679\tec:4.3.3.7\n" +
            "bpa:BPP0689\tec:4.3.2.3\n" +
            "bpa:BPP0701\tec:2.1.2.1\n" +
            "bpa:BPP0748\tec:2.6.1.13\n" +
            "bpa:BPP0749\tec:3.5.3.1\n" +
            "bpa:BPP0760\tec:3.5.4.13\n" +
            "bpa:BPP0769\tec:2.1.2.10\n" +
            "bpa:BPP0783\tec:1.2.1.18\n" +
            "bpa:BPP0783\tec:1.2.1.27\n" +
            "bpa:BPP0784\tec:2.6.1.18\n" +
            "bpa:BPP0786\tec:1.5.1.3\n" +
            "bpa:BPP0799\tec:5.3.1.9\n" +
            "bpa:BPP0806\tec:3.7.1.2\n" +
            "bpa:BPP0807\tec:1.13.11.5\n" +
            "bpa:BPP0817\tec:2.7.6.1\n" +
            "bpa:BPP0832\tec:1.1.1.40\n" +
            "bpa:BPP0860\tec:2.7.4.16\n" +
            "bpa:BPP0863\tec:4.1.1.23\n" +
            "bpa:BPP0865\tec:2.7.1.107\n" +
            "bpa:BPP0880\tec:4.1.1.44\n" +
            "bpa:BPP1033\tec:3.4.21.53\n" +
            "bpa:BPP1043\tec:1.13.12.16\n" +
            "bpa:BPP1052\tec:4.3.1.19\n" +
            "bpa:BPP1146\tec:2.4.2.29\n" +
            "bpa:BPP1149\tec:2.4.2.9\n" +
            "bpa:BPP1164\tec:2.2.1.1\n" +
            "bpa:BPP1165\tec:1.2.1.12\n" +
            "bpa:BPP1166\tec:2.7.2.3\n" +
            "bpa:BPP1168\tec:2.7.2.1\n" +
            "bpa:BPP1169\tec:2.3.1.8\n" +
            "bpa:BPP1189\tec:3.5.1.49\n" +
            "bpa:BPP1194\tec:6.3.2.6\n" +
            "bpa:BPP1209\tec:1.3.8.6\n" +
            "bpa:BPP1228\tec:2.2.1.2\n" +
            "bpa:BPP1242\tec:6.3.5.3\n" +
            "bpa:BPP1258\tec:1.1.1.205\n" +
            "bpa:BPP1259\tec:6.3.5.2\n" +
            "bpa:BPP1273\tec:2.5.1.18\n" +
            "bpa:BPP1286\tec:1.2.1.16\n" +
            "bpa:BPP1286\tec:1.2.1.79\n" +
            "bpa:BPP1286\tec:1.2.1.20\n" +
            "bpa:BPP1311\tec:1.5.1.2\n" +
            "bpa:BPP1356\tec:2.5.1.54\n" +
            "bpa:BPP1358\tec:1.8.1.7\n" +
            "bpa:BPP1368\tec:4.1.1.32\n" +
            "bpa:BPP1380\tec:4.2.1.9\n" +
            "bpa:BPP1422\tec:3.1.11.2\n" +
            "bpa:BPP1696\tec:3.2.2.21\n" +
            "bpa:BPP2742\tec:2.5.1.18\n" +
            "bpa:BPP2783\tec:3.1.1.45\n" +
            "bpa:BPP2803\tec:4.1.3.4\n" +
            "bpa:BPP2844\tec:6.3.4.4\n" +
            "bpa:BPP2850\tec:2.6.1.9\n" +
            "bpa:BPP2858\tec:2.7.4.6\n" +
            "bpa:BPP2859\tec:6.1.1.9\n" +
            "bpa:BPP2865\tec:2.1.1.228\n" +
            "bpa:BPP2870\tec:6.1.1.7\n" +
            "bpa:BPP2889\tec:2.4.1.21\n" +
            "bpa:BPP2892\tec:2.4.1.18\n" +
            "bpa:BPP2897\tec:2.4.1.25\n" +
            "bpa:BPP2900\tec:2.7.1.35\n" +
            "bpa:BPP2917\tec:1.11.1.9\n" +
            "bpa:BPP2924\tec:4.1.1.65\n" +
            "bpa:BPP2925\tec:1.1.2.3\n" +
            "bpa:BPP2935\tec:2.5.1.18\n" +
            "bpa:BPP2943\tec:1.5.1.2\n" +
            "bpa:BPP2995\tec:2.7.7.56\n" +
            "bpa:BPP3000\tec:3.2.2.4\n" +
            "bpa:BPP3005\tec:2.7.4.8\n" +
            "bpa:BPP3007\tec:2.7.6.5\n" +
            "bpa:BPP3007\tec:3.1.7.2\n" +
            "bpa:BPP3012\tec:3.5.2.9\n" +
            "bpa:BPP3045\tec:3.1.4.46\n" +
            "bpa:BPP3056\tec:2.1.1.77\n" +
            "bpa:BPP3059\tec:2.3.1.234\n" +
            "bpa:BPP3073\tec:3.4.17.13\n" +
            "bpa:BPP3086\tec:1.4.5.1\n" +
            "bpa:BPP3129\tec:2.7.4.25\n" +
            "bpa:BPP3130\tec:2.5.1.19\n" +
            "bpa:BPP3133\tec:2.6.1.52\n" +
            "bpa:BPP3136\tec:2.1.1.222\n" +
            "bpa:BPP3136\tec:2.1.1.64\n" +
            "bpa:BPP3146\tec:2.3.1.9\n" +
            "bpa:BPP3160\tec:1.2.1.16\n" +
            "bpa:BPP3160\tec:1.2.1.79\n" +
            "bpa:BPP3160\tec:1.2.1.20\n" +
            "bpa:BPP3202\tec:6.1.1.2\n" +
            "bpa:BPP3213\tec:1.1.1.100\n" +
            "bpa:BPP3215\tec:1.8.1.4\n" +
            "bpa:BPP3216\tec:2.3.1.61\n" +
            "bpa:BPP3217\tec:1.2.4.2\n" +
            "bpa:BPP3221\tec:1.1.1.40\n" +
            "bpa:BPP3225\tec:2.3.3.1\n" +
            "bpa:BPP3233\tec:4.1.3.30\n" +
            "bpa:BPP3234\tec:2.3.3.5\n" +
            "bpa:BPP3237\tec:4.2.1.79\n" +
            "bpa:BPP3252\tec:4.2.1.11\n" +
            "bpa:BPP3254\tec:2.5.1.55\n" +
            "bpa:BPP3255\tec:6.3.4.2\n" +
            "bpa:BPP3274\tec:6.2.1.1\n" +
            "bpa:BPP3277\tec:6.2.1.17\n" +
            "bpa:BPP3285\tec:3.4.11.1\n" +
            "bpa:BPP3305\tec:1.1.1.100\n" +
            "bpa:BPP3306\tec:2.3.1.39\n" +
            "bpa:BPP3329\tec:6.3.4.21\n" +
            "bpa:BPP3341\tec:2.6.1.42\n" +
            "bpa:BPP3353\tec:6.5.1.2\n" +
            "bpa:BPP3368\tec:3.1.3.3\n" +
            "bpa:BPP3419\tec:4.3.2.1\n" +
            "bpa:BPP3422\tec:4.3.1.2\n" +
            "bpa:BPP3426\tec:5.3.1.1\n" +
            "bpa:BPP3429\tec:4.3.1.19\n" +
            "bpa:BPP3431\tec:2.7.7.8\n" +
            "bpa:BPP3435\tec:1.1.1.86\n" +
            "bpa:BPP3459\tec:6.1.1.11\n" +
            "bpa:BPP3489\tec:4.99.1.1\n" +
            "bpa:BPP3489\tec:4.99.1.9\n" +
            "bpa:BPP3491\tec:2.7.1.23\n" +
            "bpa:BPP3496\tec:1.17.1.8\n" +
            "bpa:BPP3497\tec:1.3.1.98\n" +
            "bpa:BPP3501\tec:3.5.1.10\n" +
            "bpa:BPP3551\tec:2.6.1.42\n" +
            "bpa:BPP3578\tec:6.1.1.18\n" +
            "bpa:BPP3607\tec:1.4.3.5\n" +
            "bpa:BPP3624\tec:2.5.1.75\n" +
            "bpa:BPP3625\tec:6.3.3.1\n" +
            "bpa:BPP3628\tec:2.7.7.19\n" +
            "bpa:BPP3629\tec:2.7.6.3\n" +
            "bpa:BPP3636\tec:4.1.1.44\n" +
            "bpa:BPP3638\tec:1.1.1.30\n" +
            "bpa:BPP3659\tec:4.4.1.5\n" +
            "bpa:BPP3663\tec:2.7.1.40\n" +
            "bpa:BPP3676\tec:3.5.1.4\n" +
            "bpa:BPP3729\tec:6.2.1.3\n" +
            "bpa:BPP3750\tec:6.3.2.4\n" +
            "bpa:BPP3751\tec:6.3.2.8\n" +
            "bpa:BPP3755\tec:2.7.8.13\n" +
            "bpa:BPP3762\tec:3.5.2.3\n" +
            "bpa:BPP3770\tec:4.2.1.9\n" +
            "bpa:BPP3787\tec:6.1.1.15\n" +
            "bpa:BPP3836\tec:3.1.2.6\n" +
            "bpa:BPP3842\tec:2.4.2.19\n" +
            "bpa:BPP3864\tec:4.3.1.2\n" +
            "bpa:BPP3875\tec:2.1.2.1\n" +
            "bpa:BPP3877\tec:6.1.1.1\n" +
            "bpa:BPP3882\tec:1.2.1.38\n" +
            "bpa:BPP3924\tec:1.1.1.25\n" +
            "bpa:BPP3927\tec:3.6.1.41\n" +
            "bpa:BPP3935\tec:2.5.1.3\n" +
            "bpa:BPP3936\tec:5.4.3.8\n" +
            "bpa:BPP3961\tec:2.7.1.24\n" +
            "bpa:BPP3965\tec:6.3.2.1\n" +
            "bpa:BPP3969\tec:2.7.1.30\n" +
            "bpa:BPP3988\tec:2.1.1.77\n" +
            "bpa:BPP4016\tec:4.3.1.19\n" +
            "bpa:BPP4020\tec:2.4.2.7\n" +
            "bpa:BPP4040\tec:2.1.1.63\n" +
            "bpa:BPP4047\tec:2.7.2.8\n" +
            "bpa:BPP4051\tec:1.1.1.27\n" +
            "bpa:BPP4060\tec:2.4.2.10\n" +
            "bpa:BPP4089\tec:6.3.2.2\n" +
            "bpa:BPP4094\tec:2.1.2.11\n" +
            "bpa:BPP4147\tec:4.1.2.25\n" +
            "bpa:BPP4147\tec:5.1.99.8\n" +
            "bpa:BPP4147\tec:1.13.11.81\n" +
            "bpa:BPP4154\tec:5.1.3.1\n" +
            "bpa:BPP4155\tec:3.1.3.18\n" +
            "bpa:BPP4158\tec:2.4.2.18\n" +
            "bpa:BPP4159\tec:4.1.1.48\n" +
            "bpa:BPP4171\tec:1.17.1.1\n" +
            "bpa:BPP4193\tec:2.1.1.63\n" +
            "bpa:BPP4214\tec:2.6.1.16\n" +
            "bpa:BPP0616\tec:4.2.1.153\n" +
            "bpa:BPP2453\tec:3.11.1.1\n" +
            "bpa:BPP1021\tec:3.2.2.21\n" +
            "bpa:BPP0286\tec:6.3.2.2\n" +
            "bpa:BPP0329\tec:7.4.2.1\n" +
            "bpa:BPP3940\tec:6.2.1.32\n" +
            "bpa:BPP1874\tec:1.17.99.6\n" +
            "bpa:BPP4389\tec:1.1.1.169\n" +
            "bpa:BPP1569\tec:1.2.1.99\n" +
            "bpa:BPP2397\tec:3.8.1.2\n" +
            "bpa:BPP0107\tec:2.3.1.180\n" +
            "bpa:BPP4207\tec:3.8.1.2\n" +
            "bpa:BPP1200\tec:2.7.7.87\n" +
            "bpa:BPP3352\tec:5.2.1.8\n" +
            "bpa:BPP1147\tec:2.4.99.17\n" +
            "bpa:BPP3671\tec:4.2.1.96\n" +
            "bpa:BPP1631\tec:2.4.1.129\n" +
            "bpa:BPP1631\tec:3.4.16.4\n" +
            "bpa:BPP0174\tec:2.5.1.17\n" +
            "bpa:BPP3922\tec:3.1.13.1\n" +
            "bpa:BPP2976\tec:1.1.1.346\n" +
            "bpa:BPP1151\tec:2.1.1.107\n" +
            "bpa:BPP1151\tec:1.3.1.76\n" +
            "bpa:BPP1151\tec:4.99.1.4\n" +
            "bpa:BPP1891\tec:1.14.11.55\n" +
            "bpa:BPP0153\tec:2.6.1.98\n" +
            "bpa:BPP0155\tec:1.1.1.335\n" +
            "bpa:BPP0755\tec:6.1.1.10\n" +
            "bpa:BPP0358\tec:2.8.3.5\n" +
            "bpa:BPP3043\tec:3.1.2.2\n" +
            "bpa:BPP3043\tec:3.1.1.2\n" +
            "bpa:BPP3043\tec:3.1.1.5\n" +
            "bpa:BPP4227\tec:1.1.1.22\n" +
            "bpa:BPP4265\tec:2.5.1.7\n" +
            "bpa:BPP4266\tec:2.4.2.17\n" +
            "bpa:BPP4267\tec:1.1.1.23\n" +
            "bpa:BPP4268\tec:2.6.1.9\n" +
            "bpa:BPP4269\tec:4.2.1.19\n" +
            "bpa:BPP4271\tec:5.3.1.16\n" +
            "bpa:BPP4273\tec:3.5.4.19\n" +
            "bpa:BPP4274\tec:3.6.1.31\n" +
            "bpa:BPP4293\tec:1.8.1.9\n" +
            "bpa:BPP4338\tec:2.3.3.13\n" +
            "bpa:BPP4359\tec:1.3.8.4\n" +
            "bpa:BPP4361\tec:2.3.1.9\n" +
            "bpa:BPP4380\tec:1.1.1.100\n" +
            "bpa:BPP4394\tec:1.1.2.3\n" +
            "bpa:BPP2284\tec:3.2.2.21\n" +
            "bpa:BPP2962\tec:7.6.2.12\n" +
            "bpa:BPP2947\tec:7.6.2.10\n" +
            "bpa:BPP4346\tec:7.6.2.10\n" +
            "bpa:BPP3684\tec:4.1.1.96\n" +
            "bpa:BPP4360\tec:2.7.11.5\n" +
            "bpa:BPP1692\tec:7.6.2.16\n" +
            "bpa:BPP2599\tec:6.1.1.20\n" +
            "bpa:BPP3372\tec:1.1.1.100\n" +
            "bpa:BPP3169\tec:2.6.1.1\n" +
            "bpa:BPP3902\tec:1.2.7.8\n" +
            "bpa:BPP3979\tec:1.2.7.8\n" +
            "bpa:BPP0142\tec:6.3.5.4\n" +
            "bpa:BPP1704\tec:2.3.1.1\n" +
            "bpa:BPP4174\tec:1.3.3.11\n" +
            "bpa:BPP1519\tec:6.3.2.12\n" +
            "bpa:BPP1519\tec:6.3.2.17\n" +
            "bpa:BPP4311\tec:7.4.2.8\n" +
            "bpa:BPP1597\tec:1.1.1.31\n" +
            "bpa:BPP0242\tec:3.7.1.3\n" +
            "bpa:BPP1203\tec:3.5.3.11\n" +
            "bpa:BPP4328\tec:3.6.4.13\n" +
            "bpa:BPP4365\tec:2.6.1.11\n" +
            "bpa:BPP4365\tec:2.6.1.17\n" +
            "bpa:BPP1690\tec:2.7.1.166\n" +
            "bpa:BPP2401\tec:2.7.7.7\n" +
            "bpa:BPP1131\tec:1.3.5.4\n" +
            "bpa:BPP1718\tec:7.5.2.6\n" +
            "bpa:BPP4316\tec:7.4.2.8\n" +
            "bpa:BPP1986\tec:2.1.2.2\n" +
            "bpa:BPP1968\tec:2.7.3.9\n" +
            "bpa:BPP3656\tec:3.1.3.82\n" +
            "bpa:BPP3656\tec:3.1.3.83\n" +
            "bpa:BPP3313\tec:3.5.1.28\n" +
            "bpa:BPP3332\tec:3.1.1.75\n" +
            "bpa:BPP3913\tec:2.7.1.20\n" +
            "bpa:BPP3475\tec:1.1.1.42\n" +
            "bpa:BPP2341\tec:1.1.1.35\n" +
            "bpa:BPP2936\tec:5.1.1.13\n" +
            "bpa:BPP4028\tec:5.3.1.13\n" +
            "bpa:BPP3953\tec:2.3.1.35\n" +
            "bpa:BPP3953\tec:2.3.1.1\n" +
            "bpa:BPP4229\tec:2.7.7.23\n" +
            "bpa:BPP4229\tec:2.3.1.157\n" +
            "bpa:BPP3566\tec:2.1.2.3\n" +
            "bpa:BPP3566\tec:3.5.4.10\n" +
            "bpa:BPP3291\tec:4.2.3.1\n" +
            "bpa:BPP1459\tec:1.5.1.5\n" +
            "bpa:BPP1459\tec:3.5.4.9\n" +
            "bpa:BPP3263\tec:3.5.1.1\n" +
            "bpa:BPP3263\tec:3.4.19.5\n" +
            "bpa:BPP3771\tec:2.5.1.145\n" +
            "bpa:BPP0152\tec:5.1.3.23\n" +
            "bpa:BPP3928\tec:2.3.1.51\n" +
            "bpa:BPP0583\tec:4.1.3.34\n" +
            "bpa:BPP3735\tec:2.3.1.251\n" +
            "bpa:BPP3170\tec:3.5.2.14\n" +
            "bpa:BPP1435\tec:6.3.5.1\n" +
            "bpa:BPP1985\tec:2.7.1.26\n" +
            "bpa:BPP1985\tec:2.7.7.2\n" +
            "bpa:BPP0160\tec:6.3.4.15\n" +
            "bpa:BPP3195\tec:1.1.1.36\n" +
            "bpa:BPP0208\tec:3.2.2.9\n" +
            "bpa:BPP0944\tec:6.2.1.1\n" +
            "bpa:BPP3871\tec:3.5.4.26\n" +
            "bpa:BPP3871\tec:1.1.1.193\n" +
            "bpa:BPP2372\tec:3.4.23.43\n" +
            "bpa:BPP1801\tec:2.6.1.1\n" +
            "bpa:BPP2668\tec:5.6.2.1\n" +
            "bpa:BPP4079\tec:5.1.1.13\n" +
            "bpa:BPP3509\tec:1.5.1.5\n" +
            "bpa:BPP3509\tec:3.5.4.9\n" +
            "bpa:BPP2425\tec:1.1.1.79\n" +
            "bpa:BPP2425\tec:1.1.1.81\n" +
            "bpa:BPP4066\tec:1.1.1.93\n" +
            "bpa:BPP4066\tec:4.1.1.73\n" +
            "bpa:BPP4066\tec:1.1.1.83\n" +
            "bpa:BPP2437\tec:1.1.1.93\n" +
            "bpa:BPP2437\tec:4.1.1.73\n" +
            "bpa:BPP2437\tec:1.1.1.83\n" +
            "bpa:BPP1430\tec:7.2.2.12\n" +
            "bpa:BPP1430\tec:7.2.2.21\n" +
            "bpa:BPP3916\tec:6.4.1.2\n" +
            "bpa:BPP3916\tec:6.3.4.14\n" +
            "bpa:BPP3120\tec:2.5.1.144\n" +
            "bpa:BPP3171\tec:3.5.2.14\n" +
            "bpa:BPP3955\tec:3.6.1.55\n" +
            "bpa:BPP1982\tec:4.1.1.36\n" +
            "bpa:BPP1982\tec:6.3.2.5\n" +
            "bpa:BPP2530\tec:3.1.13.1\n" +
            "bpa:BPP2184\tec:1.18.1.3\n" +
            "bpa:BPP2819\tec:2.3.1.9\n" +
            "bpa:BPP1773\tec:2.1.1.197\n" +
            "bpa:BPP1773\tec:6.3.3.3\n" +
            "bpa:BPP4217\tec:1.1.1.35\n" +
            "bpa:BPP0573\tec:5.2.1.1\n" +
            "bpa:BPP0577\tec:6.2.1.48\n" +
            "bpa:BPP3804\tec:6.2.1.48\n" +
            "bpa:BPP1167\tec:1.3.1.9\n" +
            "bpa:BPP1167\tec:1.3.1.10\n" +
            "bpa:BPP2343\tec:1.1.1.35\n" +
            "bpa:BPP2343\tec:4.2.1.17\n" +
            "bpa:BPP2343\tec:5.1.2.3\n" +
            "bpa:BPP1768\tec:1.7.1.13\n" +
            "bpa:BPP0771\tec:1.4.4.2\n" +
            "bpa:BPP2431\tec:2.6.1.19\n" +
            "bpa:BPP2431\tec:2.6.1.22\n" +
            "bpa:BPP2431\tec:2.6.1.48\n" +
            "bpa:BPP2514\tec:2.7.7.65\n" +
            "bpa:BPP0154\tec:2.3.1.201\n" +
            "bpa:BPP0333\tec:1.1.1.100\n" +
            "bpa:BPP0604\tec:6.4.1.1\n" +
            "bpa:BPP1797\tec:7.4.2.1\n" +
            "bpa:BPP1214\tec:3.5.1.124\n" +
            "bpa:BPP1020\tec:2.1.1.63\n" +
            "bpa:BPP3232\tec:1.1.1.37\n" +
            "bpa:BPP2513\tec:3.5.1.19\n" +
            "bpa:BPP0645\tec:2.7.11.1\n" +
            "bpa:BPP2579\tec:1.5.5.2\n" +
            "bpa:BPP2579\tec:1.2.1.88\n" +
            "bpa:BPP1366\tec:3.5.1.6\n" +
            "bpa:BPP1366\tec:3.5.1.87\n" +
            "bpa:BPP2175\tec:6.4.1.2\n" +
            "bpa:BPP2175\tec:6.3.4.14\n" +
            "bpa:BPP1669\tec:3.4.24.84\n" +
            "bpa:BPP4074\tec:4.4.1.25\n" +
            "bpa:BPP1640\tec:2.7.7.7\n" +
            "bpa:BPP4250\tec:3.1.2.12\n" +
            "bpa:BPP2505\tec:2.5.1.18\n" +
            "bpa:BPP0103\tec:4.3.3.7\n" +
            "bpa:BPP3072\tec:4.2.1.18\n" +
            "bpa:BPP2340\tec:2.3.1.16\n" +
            "bpa:BPP3934\tec:2.7.1.49\n" +
            "bpa:BPP3934\tec:2.7.4.7\n" +
            "bpa:BPP3987\tec:2.7.1.49\n" +
            "bpa:BPP3987\tec:2.7.4.7\n" +
            "bpa:BPP3944\tec:1.14.13.40\n" +
            "bpa:BPP0441\tec:2.5.1.18\n" +
            "bpa:BPP3911\tec:6.1.1.13\n" +
            "bpa:BPP3675\tec:2.6.1.77\n" +
            "bpa:BPP2834\tec:1.18.1.3\n" +
            "bpa:BPP3131\tec:1.3.1.12\n" +
            "bpa:BPP1996\tec:2.6.1.17\n" +
            "bpa:BPP4201\tec:2.6.1.44\n" +
            "bpa:BPP4201\tec:2.6.1.45\n" +
            "bpa:BPP4201\tec:2.6.1.51\n" +
            "bpa:BPP0250\tec:2.8.4.4\n" +
            "bpa:BPP2071\tec:3.6.1.11\n" +
            "bpa:BPP2071\tec:3.6.1.40\n" +
            "bpa:BPP0857\tec:4.1.99.12\n" +
            "bpa:BPP0857\tec:3.5.4.25\n" +
            "bpa:BPP4424\tec:2.6.1.88\n" +
            "bpa:BPP2136\tec:3.5.2.10\n" +
            "bpa:BPP2448\tec:2.8.1.1\n" +
            "bpa:BPP2448\tec:2.8.1.2\n" +
            "bpa:BPP3132\tec:5.4.99.5\n" +
            "bpa:BPP3132\tec:4.2.1.51\n" +
            "bpa:BPP0548\tec:4.1.1.44\n" +
            "bpa:BPP2327\tec:1.14.13.114\n" +
            "bpa:BPP2720\tec:1.14.13.1\n" +
            "bpa:BPP4009\tec:1.14.13.1\n" +
            "bpa:BPP0686\tec:3.5.1.6\n" +
            "bpa:BPP0686\tec:3.5.1.87\n" +
            "bpa:BPP0464\tec:5.4.1.3\n" +
            "bpa:BPP1065\tec:2.7.13.3\n" +
            "bpa:BPP4397\tec:3.5.1.4\n" +
            "bpa:BPP1427\tec:7.4.2.1\n" +
            "bpa:BPP0438\tec:1.14.11.18\n" +
            "bpa:BPP1987\tec:2.1.1.176\n" +
            "bpa:BPP4117\tec:1.13.12.16\n" +
            "bpa:BPP2990\tec:3.6.1.66\n" +
            "bpa:BPP2134\tec:4.1.1.76\n" +
            "bpa:BPP3727\tec:2.1.1.61\n" +
            "bpa:BPP2056\tec:5.4.2.11\n" +
            "bpa:BPP3756\tec:6.3.2.13\n" +
            "bpa:BPP3756\tec:6.3.2.10\n" +
            "bpa:BPP0914\tec:4.2.1.17\n" +
            "bpa:BPP4119\tec:4.2.1.17\n" +
            "bpa:BPP0270\tec:3.5.1.106\n" +
            "bpa:BPP3647\tec:4.2.1.17\n" +
            "bpa:BPP1747\tec:4.1.1.9\n" +
            "bpa:BPP1186\tec:1.17.1.9\n" +
            "bpa:BPP0040\tec:1.2.1.16\n" +
            "bpa:BPP0040\tec:1.2.1.79\n" +
            "bpa:BPP0040\tec:1.2.1.20\n" +
            "bpa:BPP3950\tec:3.5.1.9\n" +
            "bpa:BPP0800\tec:5.4.2.8\n" +
            "bpa:BPP0800\tec:5.4.2.2\n" +
            "bpa:BPP0395\tec:1.8.1.2\n" +
            "bpa:BPP1679\tec:5.3.3.18\n" +
            "bpa:BPP2329\tec:3.5.1.106\n" +
            "bpa:BPP2129\tec:7.4.2.1\n" +
            "bpa:BPP3361\tec:1.11.1.28\n" +
            "bpa:BPP2193\tec:1.14.12.1\n" +
            "bpa:BPP1853\tec:5.2.1.4\n" +
            "bpa:BPP1742\tec:7.6.2.9\n" +
            "bpa:BPP1041\tec:4.2.1.83\n" +
            "bpa:BPP2426\tec:1.2.1.99\n" +
            "bpa:BPP2647\tec:3.7.1.20\n" +
            "bpa:BPP0274\tec:1.14.12.1\n" +
            "bpa:BPP0792\tec:5.1.3.7\n" +
            "bpa:BPP2771\tec:7.5.2.7\n" +
            "bpa:BPP0683\tec:7.4.2.1\n" +
            "bpa:BPP0512\tec:1.14.13.111\n" +
            "bpa:BPP0513\tec:1.14.13.111\n" +
            "bpa:BPP3074\tec:3.5.4.33\n" +
            "bpa:BPP1774\tec:3.1.1.85\n" +
            "bpa:BPP2424\tec:2.6.1.96\n" +
            "bpa:BPP4084\tec:1.1.1.338\n" +
            "bpa:BPP0517\tec:1.18.1.3\n" +
            "bpa:BPP1566\tec:7.4.2.1\n" +
            "bpa:BPP3434\tec:2.7.8.8\n" +
            "bpa:BPP3784\tec:2.7.13.3\n" +
            "bpa:BPP3062\tec:1.8.1.8\n" +
            "bpa:BPP2543\tec:2.6.1.11\n" +
            "bpa:BPP2543\tec:2.6.1.17\n" +
            "bpa:BPP2606\tec:3.5.2.6\n" +
            "bpa:BPP3540\tec:1.3.5.2\n" +
            "bpa:BPP2326\tec:1.17.2.1\n" +
            "bpa:BPP0271\tec:1.13.11.9\n" +
            "bpa:BPP0272\tec:3.5.1.107\n" +
            "bpa:BPP1002\tec:2.2.1.6\n" +
            "bpa:BPP3570\tec:4.3.1.16\n" +
            "bpa:BPP2896\tec:3.2.1.141\n" +
            "bpa:BPP2956\tec:5.1.3.7\n" +
            "bpa:BPP2411\tec:2.8.3.18\n" +
            "bpa:BPP3645\tec:4.2.1.56\n" +
            "bpa:BPP3644\tec:2.8.3.22\n" +
            "bpa:BPP3990\tec:2.7.7.70\n" +
            "bpa:BPP3643\tec:4.1.3.25\n" +
            "bpa:BPP2331\tec:3.5.1.107\n" +
            "bpa:BPP2330\tec:1.13.11.9\n" +
            "bpa:BPP2264\tec:1.14.11.47\n" +
            "bpa:BPP2893\tec:3.2.1.68\n" +
            "bpa:BPP2855\tec:1.17.7.1\n" +
            "bpa:BPP2855\tec:1.17.7.3\n" +
            "bpa:BPP0873\tec:1.3.99.4\n" +
            "bpa:BPP1049\tec:2.7.4.34\n" +
            "bpa:BPP1097\tec:4.3.1.19\n" +
            "bpa:BPP3298\tec:3.4.21.107\n" +
            "bpa:BPP3124\tec:2.7.1.167\n" +
            "bpa:BPP1417\tec:3.5.1.104\n" +
            "bpa:BPP0968\tec:7.2.2.8\n" +
            "bpa:BPP3004\tec:1.8.2.2\n" +
            "bpa:BPP3669\tec:2.7.7.99\n" +
            "bpa:BPP3508\tec:1.5.1.21\n" +
            "bpa:BPP2170\tec:7.2.4.2\n" +
            "bpa:BPP0497\tec:2.5.1.18\n" +
            "bpa:BPP1171\tec:2.5.1.18\n" +
            "bpa:BPP1570\tec:3.5.1.6\n" +
            "bpa:BPP1570\tec:3.5.1.87\n" +
            "bpa:BPP2428\tec:3.5.1.6\n" +
            "bpa:BPP2428\tec:3.5.1.87\n" +
            "bpa:BPP0387\tec:4.1.1.55\n" +
            "bpa:BPP0273\tec:1.14.12.1\n" +
            "bpa:BPP3235\tec:4.2.1.117\n" +
            "bpa:BPP1038\tec:3.5.4.2\n" +
            "bpa:BPP1790\tec:2.7.7.65\n" +
            "bpa:BPP0102\tec:1.8.4.14\n" +
            "bpa:BPP2085\tec:1.2.1.26\n" +
            "bpa:BPP1055\tec:1.3.8.7\n" +
            "bpa:BPP0298\tec:2.7.7.80\n" +
            "bpa:BPP0321\tec:4.6.1.1\n" +
            "bpa:BPP4345\tec:3.1.4.53\n" +
            "bpa:BPP3539\tec:2.3.2.29\n" +
            "bpa:BPP1653\tec:4.3.99.3\n" +
            "bpa:BPP3357\tec:3.2.2.27\n" +
            "bpa:BPP1894\tec:1.1.1.408\n" +
            "bpa:BPP1894\tec:1.1.1.409\n" +
            "bpa:BPP2474\tec:1.1.1.262\n" +
            "bpa:BPP4144\tec:4.2.1.17\n" +
            "bpa:BPP1893\tec:2.7.1.219\n" +
            "bpa:BPP1893\tec:2.7.1.220\n" +
            "bpa:BPP2687\tec:4.2.1.81\n" +
            "bpa:BPP1671\tec:5.1.99.6\n" +
            "bpa:BPP3137\tec:3.1.3.105\n" +
            "bpa:BPP1960\tec:1.3.98.3\n" +
            "bpa:BPP4120\tec:4.2.1.136\n" +
            "bpa:BPP4234\tec:2.5.1.141\n" +
            "bpa:BPP2542\tec:2.1.3.3\n" +
            "bpa:BPP2486\tec:3.5.1.128\n" +
            "bpa:BPP4023\tec:7.1.1.7\n" +
            "bpa:BPP3736\tec:4.3.2.7\n" +
            "bpa:BPP4083\tec:2.3.1.31\n" +
            "bpa:BPP4083\tec:2.3.1.46\n" +
            "bpa:BPP1206\tec:7.4.2.1\n" +
            "bpa:BPP0633\tec:3.5.1.68\n" +
            "bpa:BPP1088\tec:3.5.1.68\n" +
            "bpa:BPP2758\tec:1.8.3.7\n" +
            "bpa:BPP2026\tec:3.1.3.48\n" +
            "bpa:BPP0218\tec:2.7.4.33\n" +
            "bpa:BPP1531\tec:2.5.1.31\n" +
            "bpa:BPP3505\tec:1.5.99.5\n" +
            "bpa:BPP3504\tec:1.5.99.5\n" +
            "bpa:BPP3503\tec:1.5.99.5\n" +
            "bpa:BPP3502\tec:1.5.99.5\n" +
            "bpa:BPP1966\tec:2.7.1.191\n" +
            "bpa:BPP2984\tec:1.13.11.24\n" +
            "bpa:BPP1390\tec:5.5.1.27\n" +
            "bpa:BPP1414\tec:3.5.1.107\n" +
            "bpa:BPP1371\tec:2.3.1.247\n" +
            "bpa:BPP1378\tec:5.3.1.35\n" +
            "bpa:BPP3210\tec:1.1.1.100\n" +
            "bpa:BPP0848\tec:2.1.1.171\n" +
            "bpa:BPP0902\tec:3.4.13.19\n" +
            "bpa:BPP0988\tec:1.1.1.100\n" +
            "bpa:BPP0885\tec:3.5.1.126\n" +
            "bpa:BPP0888\tec:1.3.8.7\n" +
            "bpa:BPP0939\tec:3.5.2.5\n" +
            "bpa:BPP0940\tec:1.1.1.100\n" +
            "bpa:BPP0943\tec:4.2.1.55\n" +
            "bpa:BPP0959\tec:6.2.1.3\n" +
            "bpa:BPP1007\tec:2.7.7.65\n" +
            "bpa:BPP1626\tec:1.1.1.346\n" +
            "bpa:BPP1650\tec:4.1.2.17\n" +
            "bpa:BPP1766\tec:2.7.8.41\n" +
            "bpa:BPP2307\tec:6.2.1.13\n" +
            "bpa:BPP2176\tec:7.2.4.3\n" +
            "bpa:BPP2178\tec:6.2.1.13\n" +
            "bpa:BPP1832\tec:1.17.8.1\n" +
            "bpa:BPP1836\tec:1.1.1.100\n" +
            "bpa:BPP1922\tec:2.7.7.65\n" +
            "bpa:BPP2087\tec:1.3.3.11\n" +
            "bpa:BPP2105\tec:4.2.1.17\n" +
            "bpa:BPP2349\tec:5.4.2.8\n" +
            "bpa:BPP2349\tec:5.4.2.2\n" +
            "bpa:BPP2332\tec:1.17.2.1\n" +
            "bpa:BPP2046\tec:1.1.1.320\n" +
            "bpa:BPP2296\tec:1.1.1.76\n" +
            "bpa:BPP2296\tec:1.1.1.304\n" +
            "bpa:BPP2822\tec:5.1.99.4\n" +
            "bpa:BPP2784\tec:1.1.1.76\n" +
            "bpa:BPP2784\tec:1.1.1.304\n" +
            "bpa:BPP2793\tec:6.4.1.2\n" +
            "bpa:BPP2793\tec:6.3.4.14\n" +
            "bpa:BPP2458\tec:1.14.15.7\n" +
            "bpa:BPP2532\tec:2.5.1.49\n" +
            "bpa:BPP0080\tec:1.13.11.41\n" +
            "bpa:BPP0085\tec:3.5.4.31\n" +
            "bpa:BPP0085\tec:3.5.4.28\n" +
            "bpa:BPP0105\tec:1.14.19.20\n" +
            "bpa:BPP1059\tec:5.1.99.4\n" +
            "bpa:BPP1284\tec:3.1.1.57\n" +
            "bpa:BPP1295\tec:3.5.1.18\n" +
            "bpa:BPP1304\tec:2.8.3.19\n" +
            "bpa:BPP1309\tec:1.5.1.21\n" +
            "bpa:BPP1040\tec:4.1.3.17\n" +
            "bpa:BPP1127\tec:3.5.2.2\n" +
            "bpa:BPP1128\tec:3.5.1.107\n" +
            "bpa:BPP1418\tec:3.1.1.24\n" +
            "bpa:BPP2620\tec:2.8.3.19\n" +
            "bpa:BPP2669\tec:3.5.1.42\n" +
            "bpa:BPP2690\tec:3.5.99.10\n" +
            "bpa:BPP2736\tec:2.8.3.19\n" +
            "bpa:BPP2911\tec:1.11.1.24\n" +
            "bpa:BPP2500\tec:3.5.1.68\n" +
            "bpa:BPP0124\tec:2.1.2.9\n" +
            "bpa:BPP0127\tec:2.1.2.9\n" +
            "bpa:BPP0128\tec:2.1.2.9\n" +
            "bpa:BPP0137\tec:5.1.3.2\n" +
            "bpa:BPP0138\tec:5.1.3.2\n" +
            "bpa:BPP0201\tec:6.3.3.2\n" +
            "bpa:BPP0423\tec:1.1.1.79\n" +
            "bpa:BPP0423\tec:1.1.1.81\n" +
            "bpa:BPP0463\tec:4.2.1.56\n" +
            "bpa:BPP0340\tec:6.2.1.3\n" +
            "bpa:BPP0349\tec:2.8.3.22\n" +
            "bpa:BPP0362\tec:1.1.1.31\n" +
            "bpa:BPP3108\tec:4.3.3.7\n" +
            "bpa:BPP2950\tec:2.3.1.292\n" +
            "bpa:BPP0658\tec:2.3.1.9\n" +
            "bpa:BPP0661\tec:6.2.1.13\n" +
            "bpa:BPP0528\tec:4.2.1.17\n" +
            "bpa:BPP0626\tec:4.1.3.4\n" +
            "bpa:BPP0627\tec:4.2.1.17\n" +
            "bpa:BPP0684\tec:3.5.1.32\n" +
            "bpa:BPP0688\tec:3.5.2.5\n" +
            "bpa:BPP0552\tec:5.1.99.4\n" +
            "bpa:BPP0559\tec:4.2.1.33\n" +
            "bpa:BPP0559\tec:4.2.1.35\n" +
            "bpa:BPP0560\tec:4.2.1.33\n" +
            "bpa:BPP0560\tec:4.2.1.35\n" +
            "bpa:BPP0562\tec:1.3.5.4\n" +
            "bpa:BPP0564\tec:3.1.1.24\n" +
            "bpa:BPP0572\tec:2.3.1.9\n" +
            "bpa:BPP0632\tec:3.5.1.4\n" +
            "bpa:BPP0649\tec:6.2.1.3\n" +
            "bpa:BPP0414\tec:1.1.1.100\n" +
            "bpa:BPP0480\tec:2.4.1.173\n" +
            "bpa:BPP0489\tec:3.1.3.16\n" +
            "bpa:BPP0490\tec:2.7.7.101\n" +
            "bpa:BPP0519\tec:3.5.1.124\n" +
            "bpa:BPP0587\tec:5.1.99.4\n" +
            "bpa:BPP3612\tec:1.3.8.7\n" +
            "bpa:BPP3642\tec:2.7.1.184\n" +
            "bpa:BPP3523\tec:4.1.2.52\n" +
            "bpa:BPP3524\tec:5.1.3.2\n" +
            "bpa:BPP3548\tec:3.6.4.13\n" +
            "bpa:BPP3704\tec:1.1.1.100\n" +
            "bpa:BPP3806\tec:1.3.8.7\n" +
            "bpa:BPP3847\tec:3.1.6.6\n" +
            "bpa:BPP3851\tec:1.2.1.16\n" +
            "bpa:BPP3851\tec:1.2.1.79\n" +
            "bpa:BPP3851\tec:1.2.1.20\n" +
            "bpa:BPP3695\tec:3.7.1.9\n" +
            "bpa:BPP3726\tec:3.2.1.177\n" +
            "bpa:BPP3427\tec:1.6.5.5\n" +
            "bpa:BPP3443\tec:1.14.13.59\n" +
            "bpa:BPP3447\tec:1.14.15.7\n" +
            "bpa:BPP1118\tec:1.11.1.25\n" +
            "bpa:BPP1118\tec:1.11.1.27\n" +
            "bpa:BPP3048\tec:1.11.1.27\n" +
            "bpa:BPP1419\tec:3.5.1.4\n" +
            "bpa:BPP1437\tec:3.1.25.1\n" +
            "bpa:BPP4220\tec:6.2.1.13\n" +
            "bpa:BPP4011\tec:3.1.1.83\n" +
            "bpa:BPP4146\tec:1.8.1.9\n" +
            "bpa:BPP4151\tec:1.14.13.240\n" +
            "bpa:BPP4199\tec:3.1.26.11\n" +
            "bpa:BPP3892\tec:3.5.1.110\n" +
            "bpa:BPP1568\tec:1.4.1.3\n" +
            "bpa:BPP2024\tec:2.6.1.57\n" +
            "bpa:BPP1944\tec:1.1.1.85\n" +
            "bpa:BPP2357\tec:2.6.1.19\n" +
            "bpa:BPP2778\tec:3.5.3.11\n" +
            "bpa:BPP2988\tec:6.3.1.2\n" +
            "bpa:BPP2979\tec:1.18.1.2\n" +
            "bpa:BPP2979\tec:1.19.1.1\n" +
            "bpa:BPP0416\tec:1.1.1.157\n" +
            "bpa:BPP0671\tec:4.4.1.13\n" +
            "bpa:BPP1372\tec:1.1.1.157\n" +
            "bpa:BPP3272\tec:4.1.2.48\n" +
            "bpa:BPP3415\tec:4.4.1.13\n" +
            "bpa:BPP3615\tec:2.3.2.2\n" +
            "bpa:BPP3615\tec:3.4.19.13\n" +
            "bpa:BPP3778\tec:2.3.2.2\n" +
            "bpa:BPP3778\tec:3.4.19.13\n" +
            "bpa:BPP4132\tec:4.1.1.37\n" +
            "bpa:BPP3983\tec:2.1.1.13\n" +
            "bpa:BPP4001\tec:1.1.1.95\n" +
            "bpa:BPP4001\tec:1.1.1.399\n" +
            "bpa:BPP4355\tec:4.4.1.13\n" +
            "bpa:BPP4378\tec:1.1.1.69\n" +
            "bpa:BPP3898\tec:6.1.1.17\n" +
            "bpa:BPP1821\tec:3.5.1.82\n" +
            "bpa:BPP3114\tec:1.11.1.24\n" +
            "bpa:BPP0061\tec:1.8.4.16\n" +
            "bpa:BPP4189\tec:7.6.2.5\n" +
            "bpa:BPP4408\tec:7.6.2.14\n" +
            "bpa:BPP1387\tec:7.6.2.14\n" +
            "bpa:BPP4376\tec:7.6.2.14\n" +
            "bpa:BPP1226\tec:7.6.2.14";

    public static String pathwayList = "path:bpa00010\tGlycolysis / Gluconeogenesis - Bordetella parapertussis 12822\n" +
            "path:bpa00020\tCitrate cycle (TCA cycle) - Bordetella parapertussis 12822\n" +
            "path:bpa00030\tPentose phosphate pathway - Bordetella parapertussis 12822\n" +
            "path:bpa00040\tPentose and glucuronate interconversions - Bordetella parapertussis 12822\n" +
            "path:bpa00051\tFructose and mannose metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00052\tGalactose metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00053\tAscorbate and aldarate metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00061\tFatty acid biosynthesis - Bordetella parapertussis 12822\n" +
            "path:bpa00071\tFatty acid degradation - Bordetella parapertussis 12822\n" +
            "path:bpa00100\tSteroid biosynthesis - Bordetella parapertussis 12822\n" +
            "path:bpa00130\tUbiquinone and other terpenoid-quinone biosynthesis - Bordetella parapertussis 12822\n" +
            "path:bpa00190\tOxidative phosphorylation - Bordetella parapertussis 12822\n" +
            "path:bpa00220\tArginine biosynthesis - Bordetella parapertussis 12822\n" +
            "path:bpa00230\tPurine metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00240\tPyrimidine metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00250\tAlanine, aspartate and glutamate metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00260\tGlycine, serine and threonine metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00261\tMonobactam biosynthesis - Bordetella parapertussis 12822\n" +
            "path:bpa00270\tCysteine and methionine metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00280\tValine, leucine and isoleucine degradation - Bordetella parapertussis 12822\n" +
            "path:bpa00281\tGeraniol degradation - Bordetella parapertussis 12822\n" +
            "path:bpa00290\tValine, leucine and isoleucine biosynthesis - Bordetella parapertussis 12822\n" +
            "path:bpa00300\tLysine biosynthesis - Bordetella parapertussis 12822\n" +
            "path:bpa00310\tLysine degradation - Bordetella parapertussis 12822\n" +
            "path:bpa00330\tArginine and proline metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00332\tCarbapenem biosynthesis - Bordetella parapertussis 12822\n" +
            "path:bpa00340\tHistidine metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00350\tTyrosine metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00360\tPhenylalanine metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00361\tChlorocyclohexane and chlorobenzene degradation - Bordetella parapertussis 12822\n" +
            "path:bpa00362\tBenzoate degradation - Bordetella parapertussis 12822\n" +
            "path:bpa00364\tFluorobenzoate degradation - Bordetella parapertussis 12822\n" +
            "path:bpa00380\tTryptophan metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00400\tPhenylalanine, tyrosine and tryptophan biosynthesis - Bordetella parapertussis 12822\n" +
            "path:bpa00401\tNovobiocin biosynthesis - Bordetella parapertussis 12822\n" +
            "path:bpa00410\tbeta-Alanine metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00430\tTaurine and hypotaurine metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00450\tSelenocompound metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00460\tCyanoamino acid metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00470\tD-Amino acid metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00480\tGlutathione metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00500\tStarch and sucrose metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00520\tAmino sugar and nucleotide sugar metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00521\tStreptomycin biosynthesis - Bordetella parapertussis 12822\n" +
            "path:bpa00523\tPolyketide sugar unit biosynthesis - Bordetella parapertussis 12822\n" +
            "path:bpa00540\tLipopolysaccharide biosynthesis - Bordetella parapertussis 12822\n" +
            "path:bpa00541\tO-Antigen nucleotide sugar biosynthesis - Bordetella parapertussis 12822\n" +
            "path:bpa00550\tPeptidoglycan biosynthesis - Bordetella parapertussis 12822\n" +
            "path:bpa00561\tGlycerolipid metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00562\tInositol phosphate metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00564\tGlycerophospholipid metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00590\tArachidonic acid metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00592\talpha-Linolenic acid metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00620\tPyruvate metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00621\tDioxin degradation - Bordetella parapertussis 12822\n" +
            "path:bpa00622\tXylene degradation - Bordetella parapertussis 12822\n" +
            "path:bpa00623\tToluene degradation - Bordetella parapertussis 12822\n" +
            "path:bpa00624\tPolycyclic aromatic hydrocarbon degradation - Bordetella parapertussis 12822\n" +
            "path:bpa00625\tChloroalkane and chloroalkene degradation - Bordetella parapertussis 12822\n" +
            "path:bpa00626\tNaphthalene degradation - Bordetella parapertussis 12822\n" +
            "path:bpa00627\tAminobenzoate degradation - Bordetella parapertussis 12822\n" +
            "path:bpa00630\tGlyoxylate and dicarboxylate metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00633\tNitrotoluene degradation - Bordetella parapertussis 12822\n" +
            "path:bpa00640\tPropanoate metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00642\tEthylbenzene degradation - Bordetella parapertussis 12822\n" +
            "path:bpa00643\tStyrene degradation - Bordetella parapertussis 12822\n" +
            "path:bpa00650\tButanoate metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00660\tC5-Branched dibasic acid metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00670\tOne carbon pool by folate - Bordetella parapertussis 12822\n" +
            "path:bpa00680\tMethane metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00730\tThiamine metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00740\tRiboflavin metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00750\tVitamin B6 metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00760\tNicotinate and nicotinamide metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00770\tPantothenate and CoA biosynthesis - Bordetella parapertussis 12822\n" +
            "path:bpa00780\tBiotin metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00785\tLipoic acid metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00790\tFolate biosynthesis - Bordetella parapertussis 12822\n" +
            "path:bpa00860\tPorphyrin metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00900\tTerpenoid backbone biosynthesis - Bordetella parapertussis 12822\n" +
            "path:bpa00903\tLimonene and pinene degradation - Bordetella parapertussis 12822\n" +
            "path:bpa00906\tCarotenoid biosynthesis - Bordetella parapertussis 12822\n" +
            "path:bpa00910\tNitrogen metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00920\tSulfur metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa00930\tCaprolactam degradation - Bordetella parapertussis 12822\n" +
            "path:bpa00970\tAminoacyl-tRNA biosynthesis - Bordetella parapertussis 12822\n" +
            "path:bpa00997\tBiosynthesis of various secondary metabolites - part 3 - Bordetella parapertussis 12822\n" +
            "path:bpa01040\tBiosynthesis of unsaturated fatty acids - Bordetella parapertussis 12822\n" +
            "path:bpa01054\tNonribosomal peptide structures - Bordetella parapertussis 12822\n" +
            "path:bpa01100\tMetabolic pathways - Bordetella parapertussis 12822\n" +
            "path:bpa01110\tBiosynthesis of secondary metabolites - Bordetella parapertussis 12822\n" +
            "path:bpa01120\tMicrobial metabolism in diverse environments - Bordetella parapertussis 12822\n" +
            "path:bpa01200\tCarbon metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa01210\t2-Oxocarboxylic acid metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa01212\tFatty acid metabolism - Bordetella parapertussis 12822\n" +
            "path:bpa01220\tDegradation of aromatic compounds - Bordetella parapertussis 12822\n" +
            "path:bpa01230\tBiosynthesis of amino acids - Bordetella parapertussis 12822\n" +
            "path:bpa01240\tBiosynthesis of cofactors - Bordetella parapertussis 12822\n" +
            "path:bpa01250\tBiosynthesis of nucleotide sugars - Bordetella parapertussis 12822\n" +
            "path:bpa01501\tbeta-Lactam resistance - Bordetella parapertussis 12822\n" +
            "path:bpa01502\tVancomycin resistance - Bordetella parapertussis 12822\n" +
            "path:bpa01503\tCationic antimicrobial peptide (CAMP) resistance - Bordetella parapertussis 12822\n" +
            "path:bpa02010\tABC transporters - Bordetella parapertussis 12822\n" +
            "path:bpa02020\tTwo-component system - Bordetella parapertussis 12822\n" +
            "path:bpa02024\tQuorum sensing - Bordetella parapertussis 12822\n" +
            "path:bpa02030\tBacterial chemotaxis - Bordetella parapertussis 12822\n" +
            "path:bpa02040\tFlagellar assembly - Bordetella parapertussis 12822\n" +
            "path:bpa02060\tPhosphotransferase system (PTS) - Bordetella parapertussis 12822\n" +
            "path:bpa03010\tRibosome - Bordetella parapertussis 12822\n" +
            "path:bpa03018\tRNA degradation - Bordetella parapertussis 12822\n" +
            "path:bpa03020\tRNA polymerase - Bordetella parapertussis 12822\n" +
            "path:bpa03030\tDNA replication - Bordetella parapertussis 12822\n" +
            "path:bpa03060\tProtein export - Bordetella parapertussis 12822\n" +
            "path:bpa03070\tBacterial secretion system - Bordetella parapertussis 12822\n" +
            "path:bpa03410\tBase excision repair - Bordetella parapertussis 12822\n" +
            "path:bpa03420\tNucleotide excision repair - Bordetella parapertussis 12822\n" +
            "path:bpa03430\tMismatch repair - Bordetella parapertussis 12822\n" +
            "path:bpa03440\tHomologous recombination - Bordetella parapertussis 12822\n" +
            "path:bpa04122\tSulfur relay system - Bordetella parapertussis 12822\n" +
            "path:bpa05133\tPertussis - Bordetella parapertussis 12822";


    public static String tcaKgml = "<?xml version=\"1.0\"?>\n" +
            "<!DOCTYPE pathway SYSTEM \"https://www.kegg.jp/kegg/xml/KGML_v0.7.2_.dtd\">\n" +
            "<!-- Creation date: Sep 22, 2021 10:21:10 +0900 (GMT+9) -->\n" +
            "<pathway name=\"path:bpa00020\" org=\"bpa\" number=\"00020\"\n" +
            "         title=\"Citrate cycle (TCA cycle)\"\n" +
            "         image=\"https://www.kegg.jp/kegg/pathway/bpa/bpa00020.png\"\n" +
            "         link=\"https://www.kegg.jp/kegg-bin/show_pathway?bpa00020\">\n" +
            "    <entry id=\"33\" name=\"bpa:BPP1464 bpa:BPP3047 bpa:BPP3215\" type=\"gene\" reaction=\"rn:R07618\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP1464+bpa:BPP3047+bpa:BPP3215\">\n" +
            "        <graphics name=\"lpdA...\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"467\" y=\"623\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"34\" name=\"bpa:BPP3217\" type=\"gene\" reaction=\"rn:R00621\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP3217\">\n" +
            "        <graphics name=\"odhA\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"661\" y=\"579\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"35\" name=\"bpa:BPP3217\" type=\"gene\" reaction=\"rn:R03316\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP3217\">\n" +
            "        <graphics name=\"odhA\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"530\" y=\"580\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"36\" name=\"bpa:BPP3216\" type=\"gene\" reaction=\"rn:R02570\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP3216\">\n" +
            "        <graphics name=\"odhB\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"403\" y=\"579\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"37\" name=\"bpa:BPP2638 bpa:BPP2639\" type=\"gene\" reaction=\"rn:R00405\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP2638+bpa:BPP2639\">\n" +
            "        <graphics name=\"sucC...\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"260\" y=\"579\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"38\" name=\"ko:K01899 ko:K01900\" type=\"ortholog\" reaction=\"rn:R00432 rn:R00727\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?K01899+K01900\">\n" +
            "        <graphics name=\"K01899...\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"rectangle\" x=\"260\" y=\"560\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"39\" name=\"bpa:BPP3475\" type=\"gene\" reaction=\"rn:R00268\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP3475\">\n" +
            "        <graphics name=\"icd\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"718\" y=\"510\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"40\" name=\"ko:K00030\" type=\"ortholog\" reaction=\"rn:R00709\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?K00030\">\n" +
            "        <graphics name=\"K00030\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"rectangle\" x=\"766\" y=\"463\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"41\" name=\"bpa:BPP3475\" type=\"gene\" reaction=\"rn:R01899\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP3475\">\n" +
            "        <graphics name=\"icd\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"718\" y=\"405\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"42\" name=\"ko:K01648 ko:K15230 ko:K15231\" type=\"ortholog\" reaction=\"rn:R00352\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?K01648+K15230+K15231\">\n" +
            "        <graphics name=\"K01648...\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"rectangle\" x=\"436\" y=\"350\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"43\" name=\"bpa:BPP0352 bpa:BPP0353 bpa:BPP3619 bpa:BPP3646\" type=\"gene\" reaction=\"rn:R01082\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP0352+bpa:BPP0353+bpa:BPP3619+bpa:BPP3646\">\n" +
            "        <graphics name=\"BPP0352...\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"191\" y=\"441\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"44\" name=\"bpa:BPP1126 bpa:BPP2394 bpa:BPP2400\" type=\"gene\" reaction=\"rn:R01900\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP1126+bpa:BPP2394+bpa:BPP2400\">\n" +
            "        <graphics name=\"acnA...\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"670\" y=\"350\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"45\" name=\"bpa:BPP1126 bpa:BPP2394 bpa:BPP2400\" type=\"gene\" reaction=\"rn:R01325\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP1126+bpa:BPP2394+bpa:BPP2400\">\n" +
            "        <graphics name=\"acnA...\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"571\" y=\"350\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"46\" name=\"bpa:BPP3225\" type=\"gene\" reaction=\"rn:R00351\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP3225\">\n" +
            "        <graphics name=\"gltA\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"436\" y=\"331\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"47\" name=\"bpa:BPP3232\" type=\"gene\" reaction=\"rn:R00342\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP3232\">\n" +
            "        <graphics name=\"mdH\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"253\" y=\"349\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"48\" name=\"bpa:BPP0604\" type=\"gene\" reaction=\"rn:R00344\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP0604\">\n" +
            "        <graphics name=\"BPP0604\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"736\" y=\"278\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"49\" name=\"path:bpa00630\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00630\">\n" +
            "        <graphics name=\"Glyoxylate and dicarboxylate metabolism\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"149\" y=\"312\" width=\"158\" height=\"34\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"50\" name=\"path:bpa00010\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00010\">\n" +
            "        <graphics name=\"Glycolysis / Gluconeogenesis\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"692\" y=\"116\" width=\"216\" height=\"46\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"51\" name=\"path:bpa00061\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00061\">\n" +
            "        <graphics name=\"Fatty acid biosynthesis\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"163\" y=\"153\" width=\"126\" height=\"25\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"52\" name=\"path:map00062\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?map00062\">\n" +
            "        <graphics name=\"Fatty acid elongation\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"129\" y=\"181\" width=\"194\" height=\"25\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"53\" name=\"path:bpa00071\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00071\">\n" +
            "        <graphics name=\"Fatty acid degradation\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"166\" y=\"239\" width=\"121\" height=\"25\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"54\" name=\"path:bpa00053\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00053\">\n" +
            "        <graphics name=\"Ascorbate and aldarate metabolism\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"864\" y=\"615\" width=\"126\" height=\"34\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"55\" name=\"path:bpa00250\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00250\">\n" +
            "        <graphics name=\"Alanine, aspartate and glutamate metabolism\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"862\" y=\"652\" width=\"121\" height=\"34\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"56\" name=\"path:bpa00020\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00020\">\n" +
            "        <graphics name=\"TITLE:Citrate cycle (TCA cycle)\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"154\" y=\"58\" width=\"227\" height=\"25\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"58\" name=\"path:bpa00280\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00280\">\n" +
            "        <graphics name=\"Valine, leucine and isoleucine degradation\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"266\" y=\"641\" width=\"85\" height=\"34\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"59\" name=\"path:bpa00250\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00250\">\n" +
            "        <graphics name=\"Alanine, aspartate and glutamate metabolism\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"167\" y=\"275\" width=\"121\" height=\"34\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"60\" name=\"path:bpa00470\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00470\">\n" +
            "        <graphics name=\"D-Amino acid metabolism\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"872\" y=\"685\" width=\"141\" height=\"25\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"61\" name=\"cpd:C00022\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00022\">\n" +
            "        <graphics name=\"C00022\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"737\" y=\"241\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"62\" name=\"cpd:C00122\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00122\">\n" +
            "        <graphics name=\"C00122\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"190\" y=\"486\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"63\" name=\"cpd:C00036\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00036\">\n" +
            "        <graphics name=\"C00036\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"320\" y=\"349\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"64\" name=\"cpd:C05379\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C05379\">\n" +
            "        <graphics name=\"C05379\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"718\" y=\"458\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"65\" name=\"cpd:C00024\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00024\">\n" +
            "        <graphics name=\"C00024\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"400\" y=\"241\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"66\" name=\"cpd:C00149\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00149\">\n" +
            "        <graphics name=\"C00149\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"190\" y=\"398\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"67\" name=\"cpd:C00311\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00311\">\n" +
            "        <graphics name=\"C00311\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"718\" y=\"349\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"68\" name=\"cpd:C00417\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00417\">\n" +
            "        <graphics name=\"C00417\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"621\" y=\"349\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"69\" name=\"cpd:C00042\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00042\">\n" +
            "        <graphics name=\"C00042\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"190\" y=\"579\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"70\" name=\"cpd:C00158\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00158\">\n" +
            "        <graphics name=\"C00158\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"522\" y=\"349\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"71\" name=\"cpd:C15972\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C15972\">\n" +
            "        <graphics name=\"C15972\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"522\" y=\"622\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"72\" name=\"cpd:C00068\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00068\">\n" +
            "        <graphics name=\"C00068\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"589\" y=\"535\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"73\" name=\"cpd:C16254\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C16254\">\n" +
            "        <graphics name=\"C16254\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"462\" y=\"578\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"74\" name=\"cpd:C15973\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C15973\">\n" +
            "        <graphics name=\"C15973\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"408\" y=\"622\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"75\" name=\"cpd:C00091\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00091\">\n" +
            "        <graphics name=\"C00091\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"334\" y=\"579\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"76\" name=\"cpd:C00026\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00026\">\n" +
            "        <graphics name=\"C00026\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"718\" y=\"578\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"77\" name=\"cpd:C05381\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C05381\">\n" +
            "        <graphics name=\"C05381\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"593\" y=\"578\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"83\" name=\"cpd:C05125\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C05125\">\n" +
            "        <graphics name=\"C05125\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"631\" y=\"241\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"84\" name=\"bpa:BPP1462 bpa:BPP3220\" type=\"gene\" reaction=\"rn:R00014\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP1462+bpa:BPP3220\">\n" +
            "        <graphics name=\"aceE...\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"686\" y=\"241\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"85\" name=\"cpd:C00068\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00068\">\n" +
            "        <graphics name=\"C00068\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"631\" y=\"198\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"86\" name=\"bpa:BPP1462 bpa:BPP3220\" type=\"gene\" reaction=\"rn:R03270\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP1462+bpa:BPP3220\">\n" +
            "        <graphics name=\"aceE...\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"582\" y=\"241\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"87\" name=\"cpd:C15972\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C15972\">\n" +
            "        <graphics name=\"C15972\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"580\" y=\"284\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"89\" name=\"bpa:BPP1464 bpa:BPP3047 bpa:BPP3215\" type=\"gene\" reaction=\"rn:R07618\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP1464+bpa:BPP3047+bpa:BPP3215\">\n" +
            "        <graphics name=\"lpdA...\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"529\" y=\"285\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"90\" name=\"cpd:C16255\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C16255\">\n" +
            "        <graphics name=\"C16255\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"517\" y=\"241\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"91\" name=\"bpa:BPP1463\" type=\"gene\" reaction=\"rn:R02569\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP1463\">\n" +
            "        <graphics name=\"aceF\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"464\" y=\"242\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"94\" name=\"path:bpa00280\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00280\">\n" +
            "        <graphics name=\"Valine, leucine and isoleucine degradation\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"154\" y=\"211\" width=\"144\" height=\"25\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"96\" name=\"ko:K01610\" type=\"ortholog\" reaction=\"rn:R00341\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?K01610\">\n" +
            "        <graphics name=\"K01610\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"rectangle\" x=\"371\" y=\"126\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"97\" name=\"bpa:BPP1368\" type=\"gene\" reaction=\"rn:R00431 rn:R00726\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP1368\">\n" +
            "        <graphics name=\"pckG\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"371\" y=\"105\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"101\" name=\"cpd:C15973\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C15973\">\n" +
            "        <graphics name=\"C15973\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"478\" y=\"284\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"102\" name=\"cpd:C00074\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00074\">\n" +
            "        <graphics name=\"C00074\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"471\" y=\"116\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"105\" name=\"ko:K17753\" type=\"ortholog\" reaction=\"rn:R00709\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?K17753\">\n" +
            "        <graphics name=\"K17753\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"rectangle\" x=\"816\" y=\"463\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"106\" name=\"bpa:BPP2411\" type=\"gene\" reaction=\"rn:R10343\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP2411\">\n" +
            "        <graphics name=\"BPP2411\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"260\" y=\"598\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"107\" name=\"bpa:BPP3227 bpa:BPP3228 bpa:BPP3229 bpa:BPP3230\" type=\"gene\" reaction=\"rn:R02164\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP3227+bpa:BPP3228+bpa:BPP3229+bpa:BPP3230\">\n" +
            "        <graphics name=\"sdhB...\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"215\" y=\"535\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"109\" name=\"ko:K00116\" type=\"ortholog\" reaction=\"rn:R00361\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?K00116\">\n" +
            "        <graphics name=\"K00116\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"rectangle\" x=\"253\" y=\"398\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"110\" name=\"bpa:BPP0562 bpa:BPP1131 bpa:BPP3227 bpa:BPP3228 bpa:BPP3229 bpa:BPP3230\" type=\"gene\" reaction=\"rn:R02164\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP0562+bpa:BPP1131+bpa:BPP3227+bpa:BPP3228+bpa:BPP3229+bpa:BPP3230\">\n" +
            "        <graphics name=\"BPP0562...\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"165\" y=\"535\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"111\" name=\"path:bpa00220\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00220\">\n" +
            "        <graphics name=\"Arginine biosynthesis\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"80\" y=\"486\" width=\"120\" height=\"25\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"113\" name=\"path:bpa00220\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00220\">\n" +
            "        <graphics name=\"Arginine biosynthesis\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"861\" y=\"583\" width=\"119\" height=\"25\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"115\" name=\"ko:K00174 ko:K00175\" type=\"ortholog\" reaction=\"rn:R01197\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?K00174+K00175\">\n" +
            "        <graphics name=\"K00174...\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"rectangle\" x=\"530\" y=\"656\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"117\" name=\"ko:K00169 ko:K00170 ko:K00172 ko:K00189 ko:K00171 ko:K03737\" type=\"ortholog\" reaction=\"rn:R01196\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?K00169+K00170+K00172+K00189+K00171+K03737\">\n" +
            "        <graphics name=\"K00169...\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"rectangle\" x=\"535\" y=\"157\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"118\" name=\"ko:K00174 ko:K00175\" type=\"ortholog\" reaction=\"rn:R01196\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?K00174+K00175\">\n" +
            "        <graphics name=\"K00174...\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"rectangle\" x=\"535\" y=\"179\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"119\" name=\"ko:K00174 ko:K00175 ko:K00177 ko:K00176\" type=\"ortholog\" reaction=\"rn:R01197\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?K00174+K00175+K00177+K00176\">\n" +
            "        <graphics name=\"K00174...\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"rectangle\" x=\"530\" y=\"678\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"120\" name=\"ko:K05942\" type=\"ortholog\" reaction=\"rn:R00351\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?K05942\">\n" +
            "        <graphics name=\"K05942\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"rectangle\" x=\"436\" y=\"369\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"122\" name=\"path:bpa00190\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00190\">\n" +
            "        <graphics name=\"Oxidative phosphorylation\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"73\" y=\"579\" width=\"99\" height=\"34\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"57\" name=\"path:bpa00350\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00350\">\n" +
            "        <graphics name=\"Tyrosine metabolism\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"102\" y=\"448\" width=\"76\" height=\"34\"/>\n" +
            "    </entry>\n" +
            "    <relation entry1=\"84\" entry2=\"48\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"61\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"84\" entry2=\"50\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"61\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"48\" entry2=\"50\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"61\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"51\" entry2=\"52\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"51\" entry2=\"94\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"51\" entry2=\"53\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"51\" entry2=\"91\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"51\" entry2=\"46\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"52\" entry2=\"94\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"52\" entry2=\"53\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"52\" entry2=\"91\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"52\" entry2=\"46\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"94\" entry2=\"53\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"94\" entry2=\"91\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"94\" entry2=\"46\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"53\" entry2=\"91\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"53\" entry2=\"46\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"91\" entry2=\"46\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"49\" entry2=\"59\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"49\" entry2=\"47\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"49\" entry2=\"46\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"49\" entry2=\"48\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"49\" entry2=\"97\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"59\" entry2=\"47\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"59\" entry2=\"46\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"59\" entry2=\"48\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"59\" entry2=\"97\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"47\" entry2=\"46\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"47\" entry2=\"48\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"47\" entry2=\"97\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"46\" entry2=\"48\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"46\" entry2=\"97\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"48\" entry2=\"97\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"47\" entry2=\"43\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"66\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"43\" entry2=\"57\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"62\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"43\" entry2=\"111\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"62\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"43\" entry2=\"107\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"62\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"57\" entry2=\"111\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"62\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"57\" entry2=\"107\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"62\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"111\" entry2=\"107\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"62\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"107\" entry2=\"37\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"69\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"37\" entry2=\"58\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"75\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"37\" entry2=\"36\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"75\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"58\" entry2=\"36\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"75\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"36\" entry2=\"35\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"73\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"36\" entry2=\"33\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"74\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"33\" entry2=\"35\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"71\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"34\" entry2=\"60\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"34\" entry2=\"55\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"34\" entry2=\"54\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"34\" entry2=\"39\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"60\" entry2=\"55\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"60\" entry2=\"54\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"60\" entry2=\"39\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"55\" entry2=\"54\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"55\" entry2=\"39\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"54\" entry2=\"39\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"44\" entry2=\"41\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"67\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"46\" entry2=\"45\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"70\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"89\" entry2=\"86\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"87\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"86\" entry2=\"84\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"83\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"91\" entry2=\"86\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"90\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"89\" entry2=\"91\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"101\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"97\" entry2=\"50\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"102\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"107\" entry2=\"106\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"69\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"106\" entry2=\"36\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"75\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"106\" entry2=\"58\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"75\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"43\" entry2=\"110\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"62\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"110\" entry2=\"111\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"62\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"110\" entry2=\"57\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"62\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"110\" entry2=\"37\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"69\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"110\" entry2=\"106\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"69\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"34\" entry2=\"113\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"60\" entry2=\"113\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"55\" entry2=\"113\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"54\" entry2=\"113\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"113\" entry2=\"39\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"122\" entry2=\"110\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"69\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"122\" entry2=\"107\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"69\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"122\" entry2=\"37\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"69\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"122\" entry2=\"106\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"69\"/>\n" +
            "    </relation>\n" +
            "    <reaction id=\"33\" name=\"rn:R07618\" type=\"reversible\">\n" +
            "        <substrate id=\"74\" name=\"cpd:C15973\"/>\n" +
            "        <product id=\"71\" name=\"cpd:C15972\"/>\n" +
            "    </reaction>\n" +
            "    <reaction id=\"34\" name=\"rn:R00621\" type=\"irreversible\">\n" +
            "        <substrate id=\"72\" name=\"cpd:C00068\"/>\n" +
            "        <substrate id=\"76\" name=\"cpd:C00026\"/>\n" +
            "        <product id=\"77\" name=\"cpd:C05381\"/>\n" +
            "    </reaction>\n" +
            "    <reaction id=\"35\" name=\"rn:R03316\" type=\"irreversible\">\n" +
            "        <substrate id=\"77\" name=\"cpd:C05381\"/>\n" +
            "        <substrate id=\"71\" name=\"cpd:C15972\"/>\n" +
            "        <product id=\"72\" name=\"cpd:C00068\"/>\n" +
            "        <product id=\"73\" name=\"cpd:C16254\"/>\n" +
            "    </reaction>\n" +
            "    <reaction id=\"36\" name=\"rn:R02570\" type=\"reversible\">\n" +
            "        <substrate id=\"75\" name=\"cpd:C00091\"/>\n" +
            "        <substrate id=\"74\" name=\"cpd:C15973\"/>\n" +
            "        <product id=\"73\" name=\"cpd:C16254\"/>\n" +
            "    </reaction>\n" +
            "    <reaction id=\"37\" name=\"rn:R00405\" type=\"reversible\">\n" +
            "        <substrate id=\"69\" name=\"cpd:C00042\"/>\n" +
            "        <product id=\"75\" name=\"cpd:C00091\"/>\n" +
            "    </reaction>\n" +
            "    <reaction id=\"39\" name=\"rn:R00268\" type=\"reversible\">\n" +
            "        <substrate id=\"64\" name=\"cpd:C05379\"/>\n" +
            "        <product id=\"76\" name=\"cpd:C00026\"/>\n" +
            "    </reaction>\n" +
            "    <reaction id=\"41\" name=\"rn:R01899\" type=\"reversible\">\n" +
            "        <substrate id=\"67\" name=\"cpd:C00311\"/>\n" +
            "        <product id=\"64\" name=\"cpd:C05379\"/>\n" +
            "    </reaction>\n" +
            "    <reaction id=\"107\" name=\"rn:R02164\" type=\"reversible\">\n" +
            "        <substrate id=\"69\" name=\"cpd:C00042\"/>\n" +
            "        <product id=\"62\" name=\"cpd:C00122\"/>\n" +
            "    </reaction>\n" +
            "    <reaction id=\"43\" name=\"rn:R01082\" type=\"reversible\">\n" +
            "        <substrate id=\"66\" name=\"cpd:C00149\"/>\n" +
            "        <product id=\"62\" name=\"cpd:C00122\"/>\n" +
            "    </reaction>\n" +
            "    <reaction id=\"44\" name=\"rn:R01900\" type=\"reversible\">\n" +
            "        <substrate id=\"68\" name=\"cpd:C00417\"/>\n" +
            "        <product id=\"67\" name=\"cpd:C00311\"/>\n" +
            "    </reaction>\n" +
            "    <reaction id=\"45\" name=\"rn:R01325\" type=\"reversible\">\n" +
            "        <substrate id=\"70\" name=\"cpd:C00158\"/>\n" +
            "        <product id=\"68\" name=\"cpd:C00417\"/>\n" +
            "    </reaction>\n" +
            "    <reaction id=\"46\" name=\"rn:R00351\" type=\"reversible\">\n" +
            "        <substrate id=\"63\" name=\"cpd:C00036\"/>\n" +
            "        <substrate id=\"65\" name=\"cpd:C00024\"/>\n" +
            "        <product id=\"70\" name=\"cpd:C00158\"/>\n" +
            "    </reaction>\n" +
            "    <reaction id=\"47\" name=\"rn:R00342\" type=\"reversible\">\n" +
            "        <substrate id=\"66\" name=\"cpd:C00149\"/>\n" +
            "        <product id=\"63\" name=\"cpd:C00036\"/>\n" +
            "    </reaction>\n" +
            "    <reaction id=\"48\" name=\"rn:R00344\" type=\"irreversible\">\n" +
            "        <substrate id=\"61\" name=\"cpd:C00022\"/>\n" +
            "        <product id=\"63\" name=\"cpd:C00036\"/>\n" +
            "    </reaction>\n" +
            "    <reaction id=\"84\" name=\"rn:R00014\" type=\"irreversible\">\n" +
            "        <substrate id=\"85\" name=\"cpd:C00068\"/>\n" +
            "        <substrate id=\"61\" name=\"cpd:C00022\"/>\n" +
            "        <product id=\"83\" name=\"cpd:C05125\"/>\n" +
            "    </reaction>\n" +
            "    <reaction id=\"86\" name=\"rn:R03270\" type=\"irreversible\">\n" +
            "        <substrate id=\"87\" name=\"cpd:C15972\"/>\n" +
            "        <substrate id=\"83\" name=\"cpd:C05125\"/>\n" +
            "        <product id=\"85\" name=\"cpd:C00068\"/>\n" +
            "        <product id=\"90\" name=\"cpd:C16255\"/>\n" +
            "    </reaction>\n" +
            "    <reaction id=\"89\" name=\"rn:R07618\" type=\"reversible\">\n" +
            "        <substrate id=\"101\" name=\"cpd:C15973\"/>\n" +
            "        <product id=\"87\" name=\"cpd:C15972\"/>\n" +
            "    </reaction>\n" +
            "    <reaction id=\"91\" name=\"rn:R02569\" type=\"reversible\">\n" +
            "        <substrate id=\"65\" name=\"cpd:C00024\"/>\n" +
            "        <substrate id=\"101\" name=\"cpd:C15973\"/>\n" +
            "        <product id=\"90\" name=\"cpd:C16255\"/>\n" +
            "    </reaction>\n" +
            "    <reaction id=\"97\" name=\"rn:R00431 rn:R00726\" type=\"irreversible\">\n" +
            "        <substrate id=\"63\" name=\"cpd:C00036\"/>\n" +
            "        <product id=\"102\" name=\"cpd:C00074\"/>\n" +
            "    </reaction>\n" +
            "    <reaction id=\"106\" name=\"rn:R10343\" type=\"reversible\">\n" +
            "        <substrate id=\"75\" name=\"cpd:C00091\"/>\n" +
            "        <product id=\"69\" name=\"cpd:C00042\"/>\n" +
            "    </reaction>\n" +
            "    <reaction id=\"110\" name=\"rn:R02164\" type=\"reversible\">\n" +
            "        <substrate id=\"69\" name=\"cpd:C00042\"/>\n" +
            "        <product id=\"62\" name=\"cpd:C00122\"/>\n" +
            "    </reaction>\n" +
            "</pathway>\n";

    public static String fakeKgml = "<?xml version=\"1.0\"?>\n" +
            "<!DOCTYPE pathway SYSTEM \"https://www.kegg.jp/kegg/xml/KGML_v0.7.2_.dtd\">\n" +
            "<!-- Creation date: Sep 22, 2021 10:21:10 +0900 (GMT+9) -->\n" +
            "<pathway name=\"path:bpa0\" org=\"bpa\" number=\"0\"\n" +
            "         title=\"Fake pathway\"\n" +
            "         image=\"https://www.kegg.jp/kegg/pathway/bpa/bpa00020.png\"\n" +
            "         link=\"https://www.kegg.jp/kegg-bin/show_pathway?bpa00020\">\n" +
            "<entry id=\"1\" name=\"bpa:BPP1464 bpa:BPP3047 bpa:BPP3215\" type=\"gene\" reaction=\"rn:R00109\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP1464+bpa:BPP3047+bpa:BPP3215\">\n" +
            "        <graphics name=\"lpdA...\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"467\" y=\"623\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "<entry id=\"1\" name=\"bpa:BPP1464 bpa:BPP3047 bpa:BPP3215\" type=\"gene\" reaction=\"rn:R00209\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP1464+bpa:BPP3047+bpa:BPP3215\">\n" +
            "        <graphics name=\"lpdA...\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"467\" y=\"623\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <reaction id=\"33\" name=\"rn:R00109\" type=\"reversible\">\n" +
            "        <substrate id=\"74\" name=\"cpd:C15973\"/>\n" +
            "        <product id=\"71\" name=\"cpd:C15972\"/>\n" +
            "    </reaction>\n" +
            "    <reaction id=\"34\" name=\"rn:R00209\" type=\"irreversible\">\n" +
            "        <substrate id=\"72\" name=\"cpd:C00068\"/>\n" +
            "        <substrate id=\"76\" name=\"cpd:C00026\"/>\n" +
            "        <product id=\"77\" name=\"cpd:C05381\"/>\n" +
            "    </reaction>\n" +
            "</pathway>\n";

    public static String noReactionPathwayKgml = "<?xml version=\"1.0\"?>\n" +
            "<!DOCTYPE pathway SYSTEM \"https://www.kegg.jp/kegg/xml/KGML_v0.7.2_.dtd\">\n" +
            "<!-- Creation date: Sep 22, 2021 10:21:10 +0900 (GMT+9) -->\n" +
            "<pathway name=\"path:bpa00000\" org=\"bpa\" number=\"00000\"\n" +
            "         title=\"Fake pathway\"\n" +
            "         image=\"https://www.kegg.jp/kegg/pathway/bpa/bpa00020.png\"\n" +
            "         link=\"https://www.kegg.jp/kegg-bin/show_pathway?bpa00020\">\n" +
            "    <entry id=\"33\" name=\"bpa:BPP1464 bpa:BPP3047 bpa:BPP3215\" type=\"gene\" reaction=\"rn:R07618\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP1464+bpa:BPP3047+bpa:BPP3215\">\n" +
            "        <graphics name=\"lpdA...\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"467\" y=\"623\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"34\" name=\"bpa:BPP3217\" type=\"gene\" reaction=\"rn:R00621\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP3217\">\n" +
            "        <graphics name=\"odhA\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"661\" y=\"579\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"35\" name=\"bpa:BPP3217\" type=\"gene\" reaction=\"rn:R03316\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP3217\">\n" +
            "        <graphics name=\"odhA\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"530\" y=\"580\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"36\" name=\"bpa:BPP3216\" type=\"gene\" reaction=\"rn:R02570\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP3216\">\n" +
            "        <graphics name=\"odhB\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"403\" y=\"579\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"37\" name=\"bpa:BPP2638 bpa:BPP2639\" type=\"gene\" reaction=\"rn:R00405\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP2638+bpa:BPP2639\">\n" +
            "        <graphics name=\"sucC...\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"260\" y=\"579\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"38\" name=\"ko:K01899 ko:K01900\" type=\"ortholog\" reaction=\"rn:R00432 rn:R00727\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?K01899+K01900\">\n" +
            "        <graphics name=\"K01899...\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"rectangle\" x=\"260\" y=\"560\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"39\" name=\"bpa:BPP3475\" type=\"gene\" reaction=\"rn:R00268\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP3475\">\n" +
            "        <graphics name=\"icd\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"718\" y=\"510\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"40\" name=\"ko:K00030\" type=\"ortholog\" reaction=\"rn:R00709\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?K00030\">\n" +
            "        <graphics name=\"K00030\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"rectangle\" x=\"766\" y=\"463\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"41\" name=\"bpa:BPP3475\" type=\"gene\" reaction=\"rn:R01899\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP3475\">\n" +
            "        <graphics name=\"icd\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"718\" y=\"405\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"42\" name=\"ko:K01648 ko:K15230 ko:K15231\" type=\"ortholog\" reaction=\"rn:R00352\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?K01648+K15230+K15231\">\n" +
            "        <graphics name=\"K01648...\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"rectangle\" x=\"436\" y=\"350\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"43\" name=\"bpa:BPP0352 bpa:BPP0353 bpa:BPP3619 bpa:BPP3646\" type=\"gene\" reaction=\"rn:R01082\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP0352+bpa:BPP0353+bpa:BPP3619+bpa:BPP3646\">\n" +
            "        <graphics name=\"BPP0352...\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"191\" y=\"441\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"44\" name=\"bpa:BPP1126 bpa:BPP2394 bpa:BPP2400\" type=\"gene\" reaction=\"rn:R01900\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP1126+bpa:BPP2394+bpa:BPP2400\">\n" +
            "        <graphics name=\"acnA...\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"670\" y=\"350\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"45\" name=\"bpa:BPP1126 bpa:BPP2394 bpa:BPP2400\" type=\"gene\" reaction=\"rn:R01325\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP1126+bpa:BPP2394+bpa:BPP2400\">\n" +
            "        <graphics name=\"acnA...\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"571\" y=\"350\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"46\" name=\"bpa:BPP3225\" type=\"gene\" reaction=\"rn:R00351\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP3225\">\n" +
            "        <graphics name=\"gltA\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"436\" y=\"331\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"47\" name=\"bpa:BPP3232\" type=\"gene\" reaction=\"rn:R00342\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP3232\">\n" +
            "        <graphics name=\"mdH\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"253\" y=\"349\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"48\" name=\"bpa:BPP0604\" type=\"gene\" reaction=\"rn:R00344\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP0604\">\n" +
            "        <graphics name=\"BPP0604\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"736\" y=\"278\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"49\" name=\"path:bpa00630\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00630\">\n" +
            "        <graphics name=\"Glyoxylate and dicarboxylate metabolism\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"149\" y=\"312\" width=\"158\" height=\"34\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"50\" name=\"path:bpa00010\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00010\">\n" +
            "        <graphics name=\"Glycolysis / Gluconeogenesis\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"692\" y=\"116\" width=\"216\" height=\"46\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"51\" name=\"path:bpa00061\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00061\">\n" +
            "        <graphics name=\"Fatty acid biosynthesis\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"163\" y=\"153\" width=\"126\" height=\"25\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"52\" name=\"path:map00062\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?map00062\">\n" +
            "        <graphics name=\"Fatty acid elongation\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"129\" y=\"181\" width=\"194\" height=\"25\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"53\" name=\"path:bpa00071\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00071\">\n" +
            "        <graphics name=\"Fatty acid degradation\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"166\" y=\"239\" width=\"121\" height=\"25\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"54\" name=\"path:bpa00053\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00053\">\n" +
            "        <graphics name=\"Ascorbate and aldarate metabolism\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"864\" y=\"615\" width=\"126\" height=\"34\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"55\" name=\"path:bpa00250\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00250\">\n" +
            "        <graphics name=\"Alanine, aspartate and glutamate metabolism\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"862\" y=\"652\" width=\"121\" height=\"34\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"56\" name=\"path:bpa00020\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00020\">\n" +
            "        <graphics name=\"TITLE:Citrate cycle (TCA cycle)\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"154\" y=\"58\" width=\"227\" height=\"25\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"58\" name=\"path:bpa00280\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00280\">\n" +
            "        <graphics name=\"Valine, leucine and isoleucine degradation\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"266\" y=\"641\" width=\"85\" height=\"34\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"59\" name=\"path:bpa00250\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00250\">\n" +
            "        <graphics name=\"Alanine, aspartate and glutamate metabolism\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"167\" y=\"275\" width=\"121\" height=\"34\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"60\" name=\"path:bpa00470\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00470\">\n" +
            "        <graphics name=\"D-Amino acid metabolism\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"872\" y=\"685\" width=\"141\" height=\"25\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"61\" name=\"cpd:C00022\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00022\">\n" +
            "        <graphics name=\"C00022\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"737\" y=\"241\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"62\" name=\"cpd:C00122\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00122\">\n" +
            "        <graphics name=\"C00122\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"190\" y=\"486\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"63\" name=\"cpd:C00036\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00036\">\n" +
            "        <graphics name=\"C00036\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"320\" y=\"349\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"64\" name=\"cpd:C05379\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C05379\">\n" +
            "        <graphics name=\"C05379\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"718\" y=\"458\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"65\" name=\"cpd:C00024\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00024\">\n" +
            "        <graphics name=\"C00024\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"400\" y=\"241\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"66\" name=\"cpd:C00149\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00149\">\n" +
            "        <graphics name=\"C00149\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"190\" y=\"398\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"67\" name=\"cpd:C00311\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00311\">\n" +
            "        <graphics name=\"C00311\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"718\" y=\"349\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"68\" name=\"cpd:C00417\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00417\">\n" +
            "        <graphics name=\"C00417\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"621\" y=\"349\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"69\" name=\"cpd:C00042\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00042\">\n" +
            "        <graphics name=\"C00042\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"190\" y=\"579\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"70\" name=\"cpd:C00158\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00158\">\n" +
            "        <graphics name=\"C00158\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"522\" y=\"349\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"71\" name=\"cpd:C15972\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C15972\">\n" +
            "        <graphics name=\"C15972\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"522\" y=\"622\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"72\" name=\"cpd:C00068\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00068\">\n" +
            "        <graphics name=\"C00068\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"589\" y=\"535\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"73\" name=\"cpd:C16254\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C16254\">\n" +
            "        <graphics name=\"C16254\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"462\" y=\"578\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"74\" name=\"cpd:C15973\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C15973\">\n" +
            "        <graphics name=\"C15973\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"408\" y=\"622\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"75\" name=\"cpd:C00091\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00091\">\n" +
            "        <graphics name=\"C00091\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"334\" y=\"579\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"76\" name=\"cpd:C00026\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00026\">\n" +
            "        <graphics name=\"C00026\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"718\" y=\"578\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"77\" name=\"cpd:C05381\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C05381\">\n" +
            "        <graphics name=\"C05381\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"593\" y=\"578\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"83\" name=\"cpd:C05125\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C05125\">\n" +
            "        <graphics name=\"C05125\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"631\" y=\"241\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"84\" name=\"bpa:BPP1462 bpa:BPP3220\" type=\"gene\" reaction=\"rn:R00014\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP1462+bpa:BPP3220\">\n" +
            "        <graphics name=\"aceE...\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"686\" y=\"241\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"85\" name=\"cpd:C00068\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00068\">\n" +
            "        <graphics name=\"C00068\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"631\" y=\"198\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"86\" name=\"bpa:BPP1462 bpa:BPP3220\" type=\"gene\" reaction=\"rn:R03270\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP1462+bpa:BPP3220\">\n" +
            "        <graphics name=\"aceE...\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"582\" y=\"241\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"87\" name=\"cpd:C15972\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C15972\">\n" +
            "        <graphics name=\"C15972\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"580\" y=\"284\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"89\" name=\"bpa:BPP1464 bpa:BPP3047 bpa:BPP3215\" type=\"gene\" reaction=\"rn:R07618\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP1464+bpa:BPP3047+bpa:BPP3215\">\n" +
            "        <graphics name=\"lpdA...\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"529\" y=\"285\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"90\" name=\"cpd:C16255\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C16255\">\n" +
            "        <graphics name=\"C16255\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"517\" y=\"241\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"91\" name=\"bpa:BPP1463\" type=\"gene\" reaction=\"rn:R02569\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP1463\">\n" +
            "        <graphics name=\"aceF\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"464\" y=\"242\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"94\" name=\"path:bpa00280\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00280\">\n" +
            "        <graphics name=\"Valine, leucine and isoleucine degradation\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"154\" y=\"211\" width=\"144\" height=\"25\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"96\" name=\"ko:K01610\" type=\"ortholog\" reaction=\"rn:R00341\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?K01610\">\n" +
            "        <graphics name=\"K01610\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"rectangle\" x=\"371\" y=\"126\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"97\" name=\"bpa:BPP1368\" type=\"gene\" reaction=\"rn:R00431 rn:R00726\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP1368\">\n" +
            "        <graphics name=\"pckG\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"371\" y=\"105\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"101\" name=\"cpd:C15973\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C15973\">\n" +
            "        <graphics name=\"C15973\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"478\" y=\"284\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"102\" name=\"cpd:C00074\" type=\"compound\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?C00074\">\n" +
            "        <graphics name=\"C00074\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"circle\" x=\"471\" y=\"116\" width=\"8\" height=\"8\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"105\" name=\"ko:K17753\" type=\"ortholog\" reaction=\"rn:R00709\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?K17753\">\n" +
            "        <graphics name=\"K17753\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"rectangle\" x=\"816\" y=\"463\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"106\" name=\"bpa:BPP2411\" type=\"gene\" reaction=\"rn:R10343\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP2411\">\n" +
            "        <graphics name=\"BPP2411\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"260\" y=\"598\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"107\" name=\"bpa:BPP3227 bpa:BPP3228 bpa:BPP3229 bpa:BPP3230\" type=\"gene\" reaction=\"rn:R02164\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP3227+bpa:BPP3228+bpa:BPP3229+bpa:BPP3230\">\n" +
            "        <graphics name=\"sdhB...\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"215\" y=\"535\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"109\" name=\"ko:K00116\" type=\"ortholog\" reaction=\"rn:R00361\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?K00116\">\n" +
            "        <graphics name=\"K00116\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"rectangle\" x=\"253\" y=\"398\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"110\" name=\"bpa:BPP0562 bpa:BPP1131 bpa:BPP3227 bpa:BPP3228 bpa:BPP3229 bpa:BPP3230\" type=\"gene\" reaction=\"rn:R02164\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa:BPP0562+bpa:BPP1131+bpa:BPP3227+bpa:BPP3228+bpa:BPP3229+bpa:BPP3230\">\n" +
            "        <graphics name=\"BPP0562...\" fgcolor=\"#000000\" bgcolor=\"#BFFFBF\"\n" +
            "             type=\"rectangle\" x=\"165\" y=\"535\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"111\" name=\"path:bpa00220\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00220\">\n" +
            "        <graphics name=\"Arginine biosynthesis\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"80\" y=\"486\" width=\"120\" height=\"25\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"113\" name=\"path:bpa00220\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00220\">\n" +
            "        <graphics name=\"Arginine biosynthesis\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"861\" y=\"583\" width=\"119\" height=\"25\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"115\" name=\"ko:K00174 ko:K00175\" type=\"ortholog\" reaction=\"rn:R01197\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?K00174+K00175\">\n" +
            "        <graphics name=\"K00174...\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"rectangle\" x=\"530\" y=\"656\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"117\" name=\"ko:K00169 ko:K00170 ko:K00172 ko:K00189 ko:K00171 ko:K03737\" type=\"ortholog\" reaction=\"rn:R01196\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?K00169+K00170+K00172+K00189+K00171+K03737\">\n" +
            "        <graphics name=\"K00169...\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"rectangle\" x=\"535\" y=\"157\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"118\" name=\"ko:K00174 ko:K00175\" type=\"ortholog\" reaction=\"rn:R01196\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?K00174+K00175\">\n" +
            "        <graphics name=\"K00174...\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"rectangle\" x=\"535\" y=\"179\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"119\" name=\"ko:K00174 ko:K00175 ko:K00177 ko:K00176\" type=\"ortholog\" reaction=\"rn:R01197\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?K00174+K00175+K00177+K00176\">\n" +
            "        <graphics name=\"K00174...\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"rectangle\" x=\"530\" y=\"678\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"120\" name=\"ko:K05942\" type=\"ortholog\" reaction=\"rn:R00351\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?K05942\">\n" +
            "        <graphics name=\"K05942\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"rectangle\" x=\"436\" y=\"369\" width=\"46\" height=\"17\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"122\" name=\"path:bpa00190\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00190\">\n" +
            "        <graphics name=\"Oxidative phosphorylation\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"73\" y=\"579\" width=\"99\" height=\"34\"/>\n" +
            "    </entry>\n" +
            "    <entry id=\"57\" name=\"path:bpa00350\" type=\"map\"\n" +
            "        link=\"https://www.kegg.jp/dbget-bin/www_bget?bpa00350\">\n" +
            "        <graphics name=\"Tyrosine metabolism\" fgcolor=\"#000000\" bgcolor=\"#FFFFFF\"\n" +
            "             type=\"roundrectangle\" x=\"102\" y=\"448\" width=\"76\" height=\"34\"/>\n" +
            "    </entry>\n" +
            "    <relation entry1=\"84\" entry2=\"48\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"61\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"84\" entry2=\"50\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"61\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"48\" entry2=\"50\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"61\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"51\" entry2=\"52\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"51\" entry2=\"94\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"51\" entry2=\"53\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"51\" entry2=\"91\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"51\" entry2=\"46\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"52\" entry2=\"94\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"52\" entry2=\"53\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"52\" entry2=\"91\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"52\" entry2=\"46\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"94\" entry2=\"53\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"94\" entry2=\"91\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"94\" entry2=\"46\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"53\" entry2=\"91\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"53\" entry2=\"46\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"91\" entry2=\"46\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"65\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"49\" entry2=\"59\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"49\" entry2=\"47\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"49\" entry2=\"46\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"49\" entry2=\"48\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"49\" entry2=\"97\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"59\" entry2=\"47\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"59\" entry2=\"46\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"59\" entry2=\"48\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"59\" entry2=\"97\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"47\" entry2=\"46\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"47\" entry2=\"48\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"47\" entry2=\"97\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"46\" entry2=\"48\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"46\" entry2=\"97\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"48\" entry2=\"97\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"63\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"47\" entry2=\"43\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"66\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"43\" entry2=\"57\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"62\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"43\" entry2=\"111\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"62\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"43\" entry2=\"107\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"62\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"57\" entry2=\"111\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"62\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"57\" entry2=\"107\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"62\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"111\" entry2=\"107\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"62\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"107\" entry2=\"37\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"69\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"37\" entry2=\"58\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"75\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"37\" entry2=\"36\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"75\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"58\" entry2=\"36\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"75\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"36\" entry2=\"35\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"73\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"36\" entry2=\"33\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"74\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"33\" entry2=\"35\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"71\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"34\" entry2=\"60\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"34\" entry2=\"55\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"34\" entry2=\"54\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"34\" entry2=\"39\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"60\" entry2=\"55\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"60\" entry2=\"54\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"60\" entry2=\"39\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"55\" entry2=\"54\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"55\" entry2=\"39\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"54\" entry2=\"39\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"44\" entry2=\"41\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"67\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"46\" entry2=\"45\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"70\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"89\" entry2=\"86\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"87\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"86\" entry2=\"84\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"83\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"91\" entry2=\"86\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"90\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"89\" entry2=\"91\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"101\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"97\" entry2=\"50\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"102\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"107\" entry2=\"106\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"69\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"106\" entry2=\"36\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"75\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"106\" entry2=\"58\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"75\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"43\" entry2=\"110\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"62\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"110\" entry2=\"111\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"62\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"110\" entry2=\"57\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"62\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"110\" entry2=\"37\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"69\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"110\" entry2=\"106\" type=\"ECrel\">\n" +
            "        <subtype name=\"compound\" value=\"69\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"34\" entry2=\"113\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"60\" entry2=\"113\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"55\" entry2=\"113\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"54\" entry2=\"113\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"113\" entry2=\"39\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"76\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"122\" entry2=\"110\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"69\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"122\" entry2=\"107\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"69\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"122\" entry2=\"37\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"69\"/>\n" +
            "    </relation>\n" +
            "    <relation entry1=\"122\" entry2=\"106\" type=\"maplink\">\n" +
            "        <subtype name=\"compound\" value=\"69\"/>\n" +
            "    </relation>\n" +
            "</pathway>\n";

}
