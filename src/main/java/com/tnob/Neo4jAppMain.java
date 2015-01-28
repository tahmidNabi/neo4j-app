package com.tnob;

import com.tnob.domain.TutorialRelationships;
import com.tnob.domain.Tutorials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestAPIFacade;
import org.neo4j.rest.graphdb.query.QueryEngine;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;

import java.util.Collections;
import java.util.Map;

/**
 * Various approaches for interfacing with Neo4j using java!
 */
public class Neo4jAppMain {
    public static final String SERVER_ROOT_URI = "http://localhost:7474";
    public static final String NEO4J_HOME = "/usr/local/neo4j-community-2.1.6";
    public static final String REST_URI = "http://localhost:7474/db/data";

    public static void main(String[] args) {

        Neo4jAppMain neo4jAppMain = new Neo4jAppMain();
        //neo4jAppMain.getServerStatus();

        //embeddedExample();

        restAPIFacadeExample();
    }

    public static void restAPIFacadeExample() {
        RestAPI graphDb = new RestAPIFacade(REST_URI);

        QueryEngine engine=new RestCypherQueryEngine(graphDb);
        QueryResult<Map<String,Object>> result=
                engine.query("match (n:Person) return n, n.name limit 5", Collections.EMPTY_MAP);

        for (Map<String, Object> row : result) {
            for (String key : row.keySet()) {
                System.out.println("Key: " + key + " , value: " + row.get(key));
            }
        }

        result = engine.query("create (java:Language{name:\"Java\", type:\"procedural OOP\"}) return java", Collections.EMPTY_MAP);
        for (Map<String, Object> row : result) {
            for (String key : row.keySet()) {
                System.out.println("Key: " + key + " , value: " + row.get(key));
            }
        }
        graphDb.close();
        System.out.println("Closed");
    }

    public static void embeddedExample() {
        GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
        GraphDatabaseService db = dbFactory.newEmbeddedDatabase("data/dbName");
        System.out.println(db);
        ExecutionEngine engine = new ExecutionEngine( db );
        ExecutionResult result;
        String rows = "";

        try (Transaction tx = db.beginTx()) {
            // Perform DB operations
            Node javaNode = db.createNode(Tutorials.JAVA);
            javaNode.setProperty("TutorialID", "JAVA001");
            javaNode.setProperty("Title", "Learn Java");
            javaNode.setProperty("NoOfChapters", "25");
            javaNode.setProperty("Status", "Completed");

            Node scalaNode = db.createNode(Tutorials.SCALA);
            scalaNode.setProperty("TutorialID", "SCALA001");
            scalaNode.setProperty("Title", "Learn Scala");
            scalaNode.setProperty("NoOfChapters", "20");
            scalaNode.setProperty("Status", "Completed");

            Relationship relationship = javaNode.createRelationshipTo(scalaNode,
                    TutorialRelationships.JVM_LANGUAGES);

            relationship.setProperty("Id","1234");
            relationship.setProperty("OOPS","YES");
            relationship.setProperty("FP","YES");

            System.out.println(javaNode.getLabels());

            result = engine.execute( "match (n) return n, n.Title limit 5" );
            System.out.println(result);

            for ( Map<String, Object> row : result )
            {
                for ( Map.Entry<String, Object> column : row.entrySet() )
                {
                    rows += column.getKey() + ": " + column.getValue() + "; ";
                }
                rows += "\n";
            }
            tx.success();
            //tx.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        System.out.println(rows);
    }

    public int getServerStatus() {
        int status = 500;
        try {
            String url = SERVER_ROOT_URI;
            HttpClient client = new HttpClient();
            GetMethod mGet = new GetMethod(url);
            status = client.executeMethod(mGet);
            System.out.println(status);
            mGet.releaseConnection();
        } catch (Exception e) {
            System.out.println("Exception in connecting to neo4j : " + e);
        }

        return status;
    }


}
