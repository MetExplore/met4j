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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class KeggServices {

    private final WebResource webResource;

    public KeggServices() {
        DefaultClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        URI baseUri = UriBuilder.fromUri("http://rest.kegg.jp/").build();
        this.webResource = client.resource(baseUri);
    }

    /**
     * Gets the links between EC and genes via Kegg api
     * The api (ex : http://rest.kegg.jp/link/hsa/ec)
     * returns a multi-line tabulated String:
     * ec:2.7.11.1	hsa:9344
     * ec:2.7.11.1	hsa:5894
     * ec:2.7.11.1	hsa:673
     * ec:2.7.12.2	hsa:5607
     * ec:2.7.11.24	hsa:5598
     * ec:2.7.11.25	hsa:9020
     * ec:2.7.11.24	hsa:51701
     * ec:2.3.1.250	hsa:64840
     * ec:7.2.2.19	hsa:495
     * ec:7.2.2.19	hsa:496
     *
     * @param orgId : a kegg organism id in three letters
     * @return a {@link String}, the result of the query
     */
    public String getKeggEcGeneEntries(String orgId) {

        if(orgId.length() != 3)
        {
            throw new IllegalArgumentException("[met4j-io][KeggServices] Length of the organism id must contain only 3 letters");
        }

        return this.webResource.path("link").path("ec").path(orgId.toLowerCase()).get(String.class);
    }

    /**
     * Gets the list of genes via The api (ex : http://rest.kegg.jp/link/genome/hsa)
     * returns a multi line tabulated String.
     * Ex :
     * hsa:102725023	gn:hsa
     * hsa:102724788	gn:hsa
     * hsa:112268355	gn:hsa
     * hsa:112268354	gn:hsa
     * hsa:109864273	gn:hsa
     * hsa:109864274	gn:hsa
     * hsa:109864271	gn:hsa
     * hsa:109864272	gn:hsa
     * hsa:504191	gn:hsa
     * hsa:391322	gn:hsa
     * hsa:100008587	gn:hsa
     *
     * @param orgId : a kegg organism id in three letters
     * @return a {@link String}, the result of the query
     **/
    public String getKeggGeneEntries(String orgId) {

        if(orgId.length() != 3)
        {
            throw new IllegalArgumentException("[met4j-io][KeggServices] Length of the organism id must contain only 3 letters");
        }

        return this.webResource.path("link").path("genome").path(orgId.toLowerCase()).get(String.class);
    }

    /**
     * Get the KGML string for a pathway
     *
     * @param id the id of a pathway
     * @return a {@link String}, the result of the query in kgml format
     */
    public String getKgml(String id) {
        return this.webResource.path("get").path(id).path("kgml").accept(MediaType.APPLICATION_XML).get(String.class);
    }

    /**
     * Get Kegg pathway entries via Kegg API
     * The api returns a multi-line tabulated String. Ex :
     * path:hsa00010	Glycolysis / Gluconeogenesis - Homo sapiens (human)
     * path:hsa00020	Citrate cycle (TCA cycle) - Homo sapiens (human)
     * path:hsa00030	Pentose phosphate pathway - Homo sapiens (human)
     * path:hsa00040	Pentose and glucuronate interconversions - Homo sapiens (human)
     * path:hsa00051	Fructose and mannose metabolism - Homo sapiens (human)
     *
     * @param orgId : a kegg organism id in three letters
     * @return a {@link String}, the result of the query
     */
    public String getKeggPathwayEntries(String orgId) {

        if(orgId.length() != 3)
        {
            throw new IllegalArgumentException("[met4j-io][KeggServices] Length of the organism id must contain only 3 letters");
        }

        return this.webResource.path("list").path("pathway").path(orgId.toLowerCase()).get(String.class);
    }

    /**
     * Get organism info of a kegg organism id via kegg api
     * <p>
     * Example :
     * T00820           Buchnera aphidicola 5A (Acyrthosiphon pisum) KEGG Genes Database
     * bap              Release 100.0+/12-16, Dec 21
     * Kanehisa Laboratories
     * 599 entries
     * <p>
     * linked db        pathway
     * brite
     * module
     * ko
     * genome
     * enzyme
     * ncbi-proteinid
     * uniprot
     *
     * @param orgId : a kegg organism id in three letters
     * @return a {@link String}, the result of the query
     */
    public String getKeggOrganismInfo(String orgId) {

        if(orgId.length() != 3)
        {
            throw new IllegalArgumentException("[met4j-io][KeggServices] Length of the organism id must contain only 3 letters");
        }

        return this.webResource.path("info").path(orgId.toLowerCase()).get(String.class);
    }

    /**
     * Get Kegg entries for several ids separated by +
     * @param query a String containing several ids separated by +
     * @return a {@link String}, the result of the query
     */
    public String getKeggEntities(String query) {
        return this.webResource.path("get").path(query).get(String.class);
    }

    /**
     * @param orgId a {@link java.lang.String} : kegg organism code in three letters
     * @return a {@link Boolean} : true if the code is in the list of the kegg organims, false otherwise.
     */
    public boolean checkKeggOrgId(String orgId) {

        if(orgId.length() != 3)
        {
            throw new IllegalArgumentException("[met4j-io][KeggServices] Length of the organism id must contain only 3 letters");
        }

        String[] Data = this.webResource.path("list").path("genome").get(String.class).split("\\n");

        for (String genome : Data) {
            String[] tab = genome.split("\\t");
            if (tab.length < 2) {
                System.err.println("Fatal Error : kegg api does not return the good format");
                return false;
            }
            if (tab[1].split(";")[0].equalsIgnoreCase(orgId)) {
                return true;
            }
        }
        return false;

    }

}
