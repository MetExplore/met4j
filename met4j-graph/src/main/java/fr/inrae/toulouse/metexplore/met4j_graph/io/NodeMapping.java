package fr.inrae.toulouse.metexplore.met4j_graph.io;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_core.utils.StringUtils;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Pattern;

public class NodeMapping<V extends BioEntity,E extends Edge<V>, G extends BioGraph<V ,E>> {

    enum parameter{
        THROWERROR,
        SKIP,
        ADD
    }
    private final G graph;
    private parameter notFoundHandling = parameter.THROWERROR;
    private String sep = "\t";
    private Boolean skipHeader = false;
    private int col=1;

    public  NodeMapping(G graph){
        this.graph=graph;
    }

    private V get(String id){
        V v = graph.getVertex(id);
        if(v!=null){
            return v;
        }else{
            switch (notFoundHandling) {
                case THROWERROR:
                    throw new IllegalArgumentException("Node " + id + " not found in graph");
                case SKIP:
                    System.err.println("Node " + id + " not found in graph");
                    return null;
                case ADD:
                    if(!StringUtils.isVoid(id)){
                        v = graph.createVertex(id);
                        graph.addVertex(v);
                        return v;
                    }else{
                        System.err.println("Id \"" + id + "\" is not valid");
                        return null;
                    }

            }
        }
        return null;
    }

    public BioCollection<V> map(Collection<String> nodesId){
        BioCollection<V> nodes = new BioCollection<>();
        for(String id : nodesId){
            V v = this.get(id);
            if(v!=null){
                nodes.add(v);
            }
        }
        return nodes;
    }

    public BioCollection<V> map(String nodeFilePath) throws IOException {
        BufferedReader fr = new BufferedReader(new FileReader(nodeFilePath));
        BioCollection<V> nodes = new BioCollection<>();
        String line;
        if(skipHeader) fr.readLine();
        while ((line = fr.readLine()) != null) {
            String id = line.trim().split(sep)[col-1];
            V v = this.get(id);
            if(v!=null){
                nodes.add(v);
            }
        }
        fr.close();
        return nodes;
    }

    public NodeMapping<V,E,G> throwErrorIfNotFound(){
        this.notFoundHandling=parameter.THROWERROR;
        return this;
    }
    public NodeMapping<V,E,G> skipIfNotFound(){
        this.notFoundHandling=parameter.SKIP;
        return this;
    }
    public NodeMapping<V,E,G> createIfNotFound(){
        this.notFoundHandling=parameter.ADD;
        return this;
    }
    public NodeMapping<V,E,G> skipHeader(){
        this.skipHeader=true;
        return this;
    }
    public NodeMapping<V,E,G> idColumn(int i){
        this.col=i;
        return this;
    }
    public NodeMapping<V,E,G> columnSeparator(String sep){
        this.sep= Pattern.quote(sep);
        return this;
    }

}
