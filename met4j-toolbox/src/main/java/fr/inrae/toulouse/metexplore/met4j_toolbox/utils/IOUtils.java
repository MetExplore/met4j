/*
 * Copyright INRAE (2025)
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

package fr.inrae.toulouse.metexplore.met4j_toolbox.utils;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.*;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.JsbmlWriter;
import fr.inrae.toulouse.metexplore.met4j_mapping.Mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class IOUtils {

    /**
     * Reads an SBML file and returns a BioNetwork object.
     *
     * @param sbmlPath the path to the SBML file
     * @return the BioNetwork object read from the SBML file
     */
    public static BioNetwork readSbml(String sbmlPath, SbmlPackage... packages) {
        JsbmlReader reader = new JsbmlReader(sbmlPath);

        ArrayList<PackageParser> pkgs = new ArrayList<>();

        for(SbmlPackage pkg : packages) {
            switch (pkg) {
                case FBC:
                    pkgs.add(new FBCParser());
                    break;
                case GROUPS:
                    pkgs.add(new GroupPathwayParser());
                    break;
                case ANNOTATIONS:
                    pkgs.add(new AnnotationParser(true));
                    break;
                case NOTES:
                    pkgs.add(new NotesParser(true));
                    break;
                case ALL:
                    pkgs.addAll(Arrays.asList(new FBCParser(), new GroupPathwayParser(), new AnnotationParser(true), new NotesParser(true)));
                    break;
            }
        }

        BioNetwork bn = null;
        try {
            bn = reader.read(pkgs);
        } catch (Met4jSbmlReaderException e) {
            e.printStackTrace();
            System.err.println("Problem while reading the sbml file " + sbmlPath);
            System.exit(1);
        }

        return bn;

    }

    public enum SbmlPackage {
        FBC, GROUPS, ANNOTATIONS, NOTES, ALL
    }

    /**
     * Writes a BioNetwork object to an SBML file.
     *
     * @param network the BioNetwork object to be written
     * @param pathOut the path to the output SBML file
     */
    public static void writeSbml(BioNetwork network, String pathOut) {
        JsbmlWriter writer = new JsbmlWriter(pathOut, network);

        try {
            writer.write();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in writing the sbml file");
            System.exit(1);
        }
    }

    /**
     * Reads a metabolite file and maps its contents to a BioCollection of BioMetabolite objects.
     *
     * @param path the path to the metabolite file
     * @param network the BioNetwork object containing the metabolites
     * @param label the label of the metabolite file
     * @return a BioCollection of BioMetabolite objects read from the metabolite file
     */
    public static BioCollection<BioMetabolite> getMetabolitesFromFile(String path, BioNetwork network, String label) {
        Mapper<BioMetabolite> metMapper = new Mapper<>(network, BioNetwork::getMetabolitesView).skipIfNotFound();
        System.out.println("Read file containing "+label+"...");
        BioCollection<BioMetabolite> metabolites = null;
        try {
            metabolites = metMapper.map(path);
        } catch (IOException e) {
            System.err.println("Error while reading the file containing "+label);
            System.err.println(e.getMessage());
            System.exit(1);
        }
        if (metMapper.getNumberOfSkippedEntries() > 0)
            System.err.println(metMapper.getNumberOfSkippedEntries() + " "+label+" not found in network.");

        return metabolites;
    }


}
