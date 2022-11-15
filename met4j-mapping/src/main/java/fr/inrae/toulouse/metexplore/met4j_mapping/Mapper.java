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
package fr.inrae.toulouse.metexplore.met4j_mapping;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 *  @author clement
 * A class to easily retrieve network elements from their identifiers
 * @param <E> the type of BioEntity that will be searched
 */
public class Mapper<E extends BioEntity> {

    enum parameter{
        THROWERROR,
        SKIP
    }

    private final BioNetwork bn;
    private final Function<BioNetwork, BioCollection<E>> getEntities;
    private parameter notFoundHandling = parameter.THROWERROR;
    private String sep = "\t";
    private Boolean skipHeader = false;
    private int col=1;
    private int skipped =0;

    /**
     * A class to easily retrieve network elements from their identifiers
     * @param bioNetwork a bionetwork
     * @param getCollectionToMap a function that takes the bionetwork and return the collection where the entities will
     *                           be searched
     */
    public  Mapper(BioNetwork bioNetwork, Function<BioNetwork, BioCollection<E>> getCollectionToMap){
        this.bn=bioNetwork;
        this.getEntities =getCollectionToMap;
    }

    private E get(String id){
        BioCollection<E> coll = getEntities.apply(bn);
        E e = coll.get(id);
        if(e!=null){
            return e;
        }else{
            switch (notFoundHandling) {
                case THROWERROR:
                    throw new IllegalArgumentException("Element " + id + " not found in network");
                case SKIP:
                    System.err.println("Element " + id + " not found in network");
                    this.skipped +=1;
                    return null;
            }
        }
        return null;
    }

    /**
     * From a list of entities identifiers, return the corresponding entities instances found in the network
     * @param entityIds a list of identifiers
     * @return a collection of matching entities
     */
    public BioCollection<E> map(Collection<String> entityIds){
        this.skipped =0;
        BioCollection<E> mapping = new BioCollection<>();
        for(String id : entityIds){
            E e = this.get(id);
            if(e!=null){
                mapping.add(e);
            }
        }
        return mapping;
    }
    /**
     * From a file with entities identifiers, return the corresponding entities instances found in the network
     * @param filePath the path to the file holding identifiers
     * @return a collection of matching entities
     * @throws IOException
     */
    public BioCollection<E> map(String filePath) throws IOException {
        return map(new FileReader(filePath));
    }

    /**
     * From a file with entities identifiers, return the corresponding entities instances found in the network
     * @param reader the input stream holding identifiers
     * @return a collection of matching entities
     * @throws IOException
     */
    public BioCollection<E> map(Reader reader) throws IOException {
        this.skipped =0;
        BufferedReader breader = new BufferedReader(reader);
        BioCollection<E> mapping = new BioCollection<>();
        String line;
        if(skipHeader) breader.readLine();
        while ((line = breader.readLine()) != null) {
            String id = line.trim().split(sep)[col-1];
            E e = this.get(id);
            if(e!=null){
                mapping.add(e);
            }
        }
        breader.close();
        return mapping;
    }

    /**
     * From a tabulated file with one entity identifiers column and attributes columns, return a map with the corresponding entities instances found in the network as value
     * and a list of attributes as value.
     * @param reader the input stream holding identifiers
     * @return a collection of matching entities
     * @throws IOException
     */
    public Map<E, List<String>> mapAttributes(Reader reader) throws IOException {
        this.skipped =0;
        BufferedReader breader = new BufferedReader(reader);
        HashMap<E, List<String>> mapping = new HashMap<>();
        String line;
        if(skipHeader) breader.readLine();
        while ((line = breader.readLine()) != null) {
            ArrayList<String> parsedLine = new ArrayList<String>(Arrays.asList(line.trim().split(sep)));
            String id = parsedLine.get(col-1);
            parsedLine.remove(col-1);
            E e = this.get(id);
            if(e!=null){
                mapping.put(e,parsedLine);
            }
        }
        breader.close();
        return mapping;
    }

    public int getNumberOfSkippedEntries(){
        return this.skipped;
    }

    /**
     * set that if an entity is not found, an error should be raised
     * @return a mapper
     */
    public Mapper<E> throwErrorIfNotFound(){
        this.notFoundHandling=parameter.THROWERROR;
        return this;
    }

    /**
     * set that if an entity is not found, no error should be raised
     * @return a mapper
     */
    public Mapper<E> skipIfNotFound(){
        this.notFoundHandling=parameter.SKIP;
        return this;
    }
    /**
     * set that the first column should be skiped during file parsing
     * @return a mapper
     */
    public Mapper<E> skipHeader(){
        this.skipHeader=true;
        return this;
    }

    /**
     * set the index of the column holding entities identifiers
     * @param i the index
     * @return a mapper
     */
    public Mapper<E> idColumn(int i){
        this.col=i;
        return this;
    }

    /**
     * set column separator
     * @param sep the separator as String
     * @return a mapper
     */
    public Mapper<E> columnSeparator(String sep){
        this.sep= Pattern.quote(sep);
        return this;
    }

}
