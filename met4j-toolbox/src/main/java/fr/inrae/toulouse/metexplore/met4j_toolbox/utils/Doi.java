/*
 * Copyright INRAE (2024)
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * The Doi class represents a Digital Object Identifier (DOI) and fetches
 * publication details associated with the DOI from the CrossRef API.
 */
public class Doi {

    private final String doi;
    private String title;
    private String journal;
    private String date;
    private String authors;
    private ArrayList<String> authorList;
    private final String url;

    private boolean detailsRetrieved = false;

    /**
     * Constructs a Doi object and extracts the DOI from the provided URL.
     *
     * @param doi The DOI or URL containing the DOI.
     */
    public Doi(String doi) {
        String normalizedDoi = StringUtils.replaceIgnoreCase(doi,"https://doi.org/", "",1);
        normalizedDoi = StringUtils.replaceIgnoreCase(normalizedDoi,"http:/dx.doi.org/", "",1);
        normalizedDoi = StringUtils.replaceIgnoreCase(normalizedDoi,"doi:", "",1);
        this.doi=normalizedDoi;
        if(!this.doi.matches("10\\.[\\d\\.]+/.+")) throw new IllegalArgumentException("invalid DOI : "+this.doi);
        this.url = "https://doi.org/"+this.getDoi();
        this.fetchPublicationDetails();
    }

    /**
     * Fetches publication details from the CrossRef API and populates the class fields.
     * Throws RuntimeException if the API response code is not 200.
     * Logs other exceptions as "Doi not reachable."
     */
    public void fetchPublicationDetails() {
        String apiUrl = "https://api.crossref.org/works/" + doi;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            conn.disconnect();

            JsonObject json = JsonParser.parseString(sb.toString()).getAsJsonObject();
            JsonObject message = json.getAsJsonObject("message");

            String title = message.getAsJsonArray("title").get(0).getAsString();
            String journal = message.getAsJsonArray("container-title").get(0).getAsString();
            String date = message.getAsJsonObject("published-print").getAsJsonArray("date-parts").get(0).getAsJsonArray().get(0).getAsString();
            JsonArray authors = message.getAsJsonArray("author");

            ArrayList<String> authorList = new ArrayList<>();

            int index = 0;

            for (JsonElement authorElement : authors) {
                JsonObject author = authorElement.getAsJsonObject();
                String authorEntry = author.get("given") + " " + author.get("family");
                authorList.add(authorEntry.replace("\"", ""));
                index++;
            }

            this.title = title;
            this.journal = journal;
            this.date = date;
            this.authors = String.join(", ", authorList);
            this.authorList = authorList;
            this.detailsRetrieved = true;

        } catch (Exception e) {
            System.err.println("Doi "+doi+" not reachable");
        }
    }

    /**
     * Returns the title of the publication.
     *
     * @return The title of the publication.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the journal name of the publication.
     *
     * @return The journal name of the publication.
     */
    public String getJournal() {
        return journal;
    }

    /**
     * Returns the publication date.
     *
     * @return The publication date.
     */
    public String getDate() {
        return date;
    }

    /**
     * Returns the authors of the publication as a single string.
     *
     * @return The authors of the publication.
     */
    public String getAuthors() {
        return authors;
    }

    /**
     * Returns the list of authors.
     *
     * @return The list of authors.
     */
    public ArrayList<String> getAuthorList() {
        return authorList;
    }

    /**
     * Returns a complete reference of the publication including authors, title, journal, and date.
     * If no publication details has been successfully fetched from DOI, returns url
     * @return The complete reference of the publication, or the url.
     */
    public String getCompleteReference() {
        if(detailsRetrieved) return this.getAuthors() + "; " + this.getTitle() + "; " + this.getJournal() + "; " + this.getDate();
        return this.url;
    }

    /**
     * Returns an abbreviated reference with the first author's last name, "et al.",
     * title, journal, and date.
     * If no publication details has been successfully fetched from DOI, returns url
     * @return The abbreviated reference of the publication, or the url.
     */
    public String getAbbreviatedReference() {
        if(detailsRetrieved){
            String firstAuthor = this.getAuthorList().get(0);
            String[] detailsFirstAuthor = firstAuthor.split("\s");
            String lastName = detailsFirstAuthor[detailsFirstAuthor.length - 1];

            return lastName + " et al.; " + this.getTitle() + "; " + this.getJournal() + "; " + this.getDate();
        }
        return this.url;
    }

    /**
     * Returns the DOI of the publication.
     *
     * @return The DOI of the publication.
     */
    public String getDoi() {
        return doi;
    }

    /**
     * Returns if the publication details have been successfully retrieved
     * @return if details have been retrieved
     */
    public boolean isDetailsRetrieved() {
        return detailsRetrieved;
    }
}
