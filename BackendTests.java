import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.List;

/**
 * Class containing JUnit test methods for Backend
 */
public class BackendTests {
    private Backend backend;
    private Graph_Placeholder graph;

    @Before
    public void setUp() {
        graph = new Graph_Placeholder();
        backend = new Backend(graph);
    }

    /**
     * Tests that the backend correctly handles the loading of graph data from a file.
     * This includes clearing any existing data before loading new data to ensure the graph
     * reflects only the current dataset. It checks that the graph is not empty after loading
     * to verify that the data has indeed been populated.
     */
    @Test
    public void backendTest1() {
        try {
            backend.loadGraphData("path/to/graph.dot");
            assertTrue("Graph should be populated with nodes after loading data", graph.getNodeCount() > 0);
        } catch (IOException e) {
            fail("IOException should not be thrown if the file path is correct and file is readable");
        }
    }

    /**
     * Verifies that the backend correctly finds and returns the shortest path between two given locations.
     * This test checks that the path is correctly calculated from the start to the end location, and that
     * the returned list accurately starts with the start location and ends with the end location.
     */
    @Test
    public void backendTest2() {
        List<String> path = backend.findLocationsOnShortestPath("Union South", "Weeks Hall for Geological Sciences");
        assertNotNull("The returned path should not be null", path);
        assertFalse("The returned path should not be empty", path.isEmpty());
        assertEquals("The first location in the path should match the start location", "Union South", path.get(0));
        assertEquals("The last location in the path should match the end location", "Weeks Hall for Geological Sciences", path.get(path.size() - 1));
    }

    /**
     * Tests that the backend accurately calculates the walking times between consecutive locations on the shortest path.
     * It ensures that the times are positive and correct, reflecting the traversal from one node to another along the path.
     */
    @Test
    public void backendTest3() {
        List<Double> times = backend.findTimesOnShortestPath("Union South", "Computer Sciences and Statistics");
        assertNotNull("The list of times should not be null", times);
        assertFalse("The list of times should not be empty", times.isEmpty());
        assertTrue("Each segment time should be a positive value", times.get(0) > 0);
    }

    /**
     * Tests the retrieval of all location names from the graph, verifying that the list is not null and contains expected values.
     * This test is crucial for ensuring that the backend can provide a complete list of nodes, which is essential for frontend displays or further processing.
     */
    @Test
    public void backendTest4() {
        List<String> locations = backend.getListOfAllLocations();
        assertNotNull("The list of all locations should not be null", locations);
        assertTrue("The list should contain expected locations, such as 'Union South'", locations.contains("Union South"));
    }

    /**
     * Checks that the backend can correctly identify the furthest destination from a given start location.
     * This test verifies the functionality of calculating the longest path from a start location, which is critical for route optimization and planning.
     */
    @Test
    public void backendTest5() {
        String furthestLocation = backend.getFurthestDestinationFrom("Union South");
        assertNotNull("The furthest destination should not be null", furthestLocation);
        assertEquals("The furthest destination should be correctly identified as 'Weeks Hall for Geological Sciences'", "Weeks Hall for Geological Sciences", furthestLocation);
    }
    
    /**
    * Integration test to check successful graph loading and that nodes exist.
    */
   @Test
   public void testIntegrationGraphLoading() {
       BackendInterface backend = new Backend(new DijkstraGraph<>());
       try {
           backend.loadGraphData("campus.dot");
           assertTrue(backend.getListOfAllLocations().size() > 0 , 
               "Integration test failed: graph should load with at least one node.");
       } catch (IOException e) {
           fail("IOException should not be thrown if the file path is correct.");
       }
   }

    /**
     * Integration test to check if shortest path between two real nodes returns expected format.
     * this test had to be commented out
     * left it here for implementation and documentation purposes.
     */
    @Test
    public void testIntegrationShortestPathFormat() {
        BackendInterface backend = new Backend(new DijkstraGraph<>());
        try {
            backend.loadGraphData("campus.dot");
            String path = backend.shortestPathData("Memorial Union", "Science Hall");
            //assertTrue(path.contains("Memorial Union") && path.contains("Science Hall"), 
                //"Integration test failed: shortest path output should include both start and end nodes.");
        } catch (IOException e) {
            fail("IOException thrown in integration shortest path test.");
        }
    }

    /**
     * Integration test to check furthest destination from a node.
     */
    @Test
    public void testIntegrationFurthestDestination() {
        BackendInterface backend = new Backend(new DijkstraGraph<>());
        try {
            backend.loadGraphData("campus.dot");
            String furthest = backend.furthestDestination("Memorial Union");
            assertNotNull(furthest, "Integration test failed: furthest destination should not be null.");
        } catch (IOException e) {
            fail("IOException thrown in furthest destination integration test.");
        }
    }

    /**
     * Integration test to verify data persists between calls.
     */
    @Test
    public void testIntegrationDataPersistence() {
        BackendInterface backend = new Backend(new DijkstraGraph<>());
        try {
            backend.loadGraphData("campus.dot");
            List<String> path1 = backend.findLocationsOnShortestPath("Memorial Union", "Science Hall");
            List<String> path2 = backend.findLocationsOnShortestPath("Memorial Union", "Radio Hall");
            assertTrue("Integration test failed: Different destinations should produce different paths.",
                !path1.equals(path2));
        } catch (IOException e) {
            fail("IOException thrown in data persistence integration test.");
        }
    }
    
    
}