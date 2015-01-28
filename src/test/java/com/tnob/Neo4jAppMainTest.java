package com.tnob;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Unit test for simple Neo4jAppMain.
 */

@Test
public class Neo4jAppMainTest

{
    Neo4jAppMain neo4jAppMain;

    @BeforeTest
    public void setup() {
        neo4jAppMain = new Neo4jAppMain();
    }
    /**
     * Rigourous Test :-)
     */
    @Test
    public void testApp() {
        assert (true);
    }

    @Test
    public void testServerStatus() {
        int status = neo4jAppMain.getServerStatus();
        Assert.assertEquals(status, 200);
    }
}
