import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class represents the Backend of a Graph 
 */
public class Backend implements BackendInterface {
    private GraphADT<String, Double> graph;

    /*
     * Constructor 
     */
    public Backend(GraphADT<String, Double> graph) {
        this.graph = graph;
    }
    
    
    /**
     * Loads graph data from a dot file.  If a graph was previously loaded, this
     * method should first delete the contents (nodes and edges) of the existing 
     * graph before loading a new one.
     * @param filename the path to a dot file to read graph data from
     * @throws IOException if there was any problem reading from this file
     */
    @Override
    public void loadGraphData(String filename) throws IOException {
        // Clear the existing graph
        List<String> allNodes = new ArrayList<>(graph.getAllNodes());
        if (graph.getNodeCount() !=0 || graph.getEdgeCount() !=0) {
            for (String node : allNodes) {
                graph.removeNode(node);
            }
        }
        
        
        
        // Load new data
        List<String> lines = Files.readAllLines(new File(filename).toPath());
        for (String line : lines) {
            if (line.contains("->")) {
                String[] parts = line.split("->|\\[label=\"|\"\\]");
                String start = parts[0].trim();
                String end = parts[1].trim();
                double weight = Double.parseDouble(parts[2].trim());
                graph.insertNode(start);
                graph.insertNode(end);
                graph.insertEdge(start, end, weight);
            }
        }
    }
    
    
    /**
     * Returns a list of all locations (node data) available in the graph.
     * @return list of all location names
     */
    @Override
    public List<String> getListOfAllLocations() {
        return graph.getAllNodes(); // Calls GraphADT method which obtains all node data
    }

    /**
     * Return the sequence of locations along the shortest path from 
     * startLocation to endLocation, or an empty list if no such path exists.
     * @param startLocation the start location of the path
     * @param endLocation the end location of the path
     * @return a list with the nodes along the shortest path from startLocation 
     *         to endLocation, or an empty list if no such path exists
     */
    @Override
    public List<String> findLocationsOnShortestPath(String startLocation, String endLocation) {
        // Check if both locations exist in the graph
        if (!graph.containsNode(startLocation) || !graph.containsNode(endLocation)) {
            return new ArrayList<>();
        }
        
        // If start and end are the same location, return just that location
        if (startLocation.equals(endLocation)) {
            List<String> sameLocation = new ArrayList<>();
            sameLocation.add(startLocation);
            return sameLocation;
        }
        
        // Attempt to retrieve the shortest path using the graph's method
        try {
            List<String> path = graph.shortestPathData(startLocation, endLocation);
            // Additional validation to make sure the path is valid
            if (path != null && !path.isEmpty() && 
                path.get(0).equals(startLocation) && 
                path.get(path.size() - 1).equals(endLocation)) {
                return path;
            } else {
                // If the path returned is invalid, return an empty list
                return new ArrayList<>();
            }
        } catch (NoSuchElementException e) {
            // If no path exists, return an empty list
            return new ArrayList<>();
        } catch (Exception e) {
            // Handle any other unexpected exceptions
            // Log the exception if logging is available
            // System.err.println("Error finding shortest path: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Return the walking times in seconds between each two nodes on the 
     * shortest path from startLocation to endLocation, or an empty list of no 
     * such path exists.
     * @param startLocation the start location of the path
     * @param endLocation the end location of the path
     * @return a list with the walking times in seconds between two nodes along 
     *         the shortest path from startLocation to endLocation, or an empty 
     *         list if no such path exists
     */
    @Override
    public List<Double> findTimesOnShortestPath(String startLocation, String endLocation) {
        // Check if both locations exist in the graph
        if (!graph.containsNode(startLocation) || !graph.containsNode(endLocation)) {
            return new ArrayList<>();
        }
        
        // If start and end are the same location, return an empty list (no edges)
        if (startLocation.equals(endLocation)) {
            return new ArrayList<>();
        }
        
        try {
            // Get the nodes on the shortest path
            List<String> pathLocations = graph.shortestPathData(startLocation, endLocation);
            
            // Validate the returned path
            if (pathLocations == null || pathLocations.isEmpty() || 
                !pathLocations.get(0).equals(startLocation) || 
                !pathLocations.get(pathLocations.size() - 1).equals(endLocation)) {
                return new ArrayList<>();
            }
            
            // If we have fewer than 2 locations, there are no edges between them
            if (pathLocations.size() < 2) {
                return new ArrayList<>();
            }
            
            // Calculate the edge weights (times) between consecutive locations
            List<Double> walkingTimes = new ArrayList<>();
            for (int i = 0; i < pathLocations.size() - 1; i++) {
                String currentLocation = pathLocations.get(i);
                String nextLocation = pathLocations.get(i + 1);
                
                try {
                    // Get the edge weight (walking time) between these locations
                    Double walkingTime = graph.getEdge(currentLocation, nextLocation);
                    walkingTimes.add(walkingTime);
                } catch (NoSuchElementException e) {
                    // This shouldn't happen with a valid path from shortestPathData,
                    // but we'll handle it just in case
                    return new ArrayList<>();
                }
            }
            
            return walkingTimes;
        } catch (NoSuchElementException e) {
            // If no path exists, return an empty list
            return new ArrayList<>();
        } catch (Exception e) {
            // Handle any other unexpected exceptions
            // Possible to add logging here: System.err.println("Error: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    /**
     * Returns the most distant location (the one that takes the longest time to 
     * reach) when comparing all shortest paths that begin from the provided 
     * startLocation.
     * @param startLocation the location to find the most distant location from
     * @return the most distant location (the one that takes the longest time to 
     *         reach which following the shortest path)
     * @throws NoSuchElementException if startLocation does not exist, or if
     *         there are no other locations that can be reached from there
     */
    @Override
    public String getFurthestDestinationFrom(String startLocation) throws NoSuchElementException {
        // Check if the start location exists in the graph
        if (!getListOfAllLocations().contains(startLocation)) {
            throw new NoSuchElementException("Start location not found in graph: " + startLocation);
        }

        
        // Get all nodes in the graph
        List<String> allLocations = graph.getAllNodes();
        
        // Variables to track the furthest destination and its cost
        String furthestDestination = null;
        double maxPathCost = -1.0;
        
        // Check the path cost to each possible destination
        for (String destination : allLocations) {
            // Skip the start location itself
            if (destination.equals(startLocation)) {
                continue;
            }
            
            try {
                // Try to find the shortest path cost to this destination
                double pathCost = graph.shortestPathCost(startLocation, destination);
                
                // Update if this is the furthest destination so far
                if (pathCost > maxPathCost) {
                    maxPathCost = pathCost;
                    furthestDestination = destination;
                }
            } catch (NoSuchElementException e) {
                // No path to this destination, skip it
                continue;
            }
        }
        
        // If no reachable destinations were found
        if (furthestDestination == null) {
            throw new NoSuchElementException("No destinations reachable from: " + startLocation);
        }
        
        return furthestDestination;
    }


    @Override
    public List<String> getAllNodeDescriptions() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public String shortestPathData(String string, String string2) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public String furthestDestination(String string) {
        // TODO Auto-generated method stub
        return null;
    }

} // end of Backend Class
