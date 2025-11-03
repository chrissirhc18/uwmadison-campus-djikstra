// Name: <Christian Mendoza>
// Email: <camendoza@wisc.edu>

import java.util.PriorityQueue;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * This class extends the BaseGraph data structure with additional methods for
 * computing the total cost and list of node data along the shortest path
 * connecting a provided starting to ending nodes. This class makes use of
 * Dijkstra's shortest path algorithm.
 */
public class DijkstraGraph<NodeType, EdgeType extends Number>
        extends BaseGraph<NodeType, EdgeType>
        implements GraphADT<NodeType, EdgeType> {

    /**
     * While searching for the shortest path between two nodes, a SearchNode
     * contains data about one specific path between the start node and another
     * node in the graph. The final node in this path is stored in its node
     * field. The total cost of this path is stored in its cost field. And the
     * predecessor SearchNode within this path is referened by the predecessor
     * field (this field is null within the SearchNode containing the starting
     * node in its node field).
     *
     * SearchNodes are Comparable and are sorted by cost so that the lowest cost
     * SearchNode has the highest priority within a java.util.PriorityQueue.
     */
    protected class SearchNode implements Comparable<SearchNode> {
        public Node node;
        public double cost;
        public SearchNode predecessor;

        public SearchNode(Node node, double cost, SearchNode predecessor) {
            this.node = node;
            this.cost = cost;
            this.predecessor = predecessor;
        }

        public int compareTo(SearchNode other) {
            if (cost > other.cost)
                return +1;
            if (cost < other.cost)
                return -1;
            return 0;
        }
    }

    /**
     * Constructor that sets the map that the graph uses.
     */
    public DijkstraGraph() {
        super(new PlaceholderMap<>());
    }

    /**
     * This helper method creates a network of SearchNodes while computing the
     * shortest path between the provided start and end locations. The
     * SearchNode that is returned by this method is represents the end of the
     * shortest path that is found: it's cost is the cost of that shortest path,
     * and the nodes linked together through predecessor references represent
     * all of the nodes along that shortest path (ordered from end to start).
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return SearchNode for the final end node within the shortest path
     * @throws NoSuchElementException when no path from start to end is found
     *                                or when either start or end data do not
     *                                correspond to a graph node
     */
    protected SearchNode computeShortestPath(NodeType start, NodeType end) {
        // Check if start and end nodes exist in the graph
        if (!this.containsNode(start) || !this.containsNode(end)) {
            throw new NoSuchElementException("Start or end node not found in the graph");
        }
        
        // If start and end are the same node, return a SearchNode with zero cost
        if (start.equals(end)) {
            return new SearchNode(nodes.get(start), 0.0, null);
        }
        
        // Initialize priority queue for exploring nodes
        PriorityQueue<SearchNode> pq = new PriorityQueue<>();
        
        // Using PlaceholderMap to keep track of visited nodes
        PlaceholderMap<Node, SearchNode> visited = new PlaceholderMap<>();
        
        // Start with the initial node
        Node startNode = nodes.get(start);
        SearchNode initialNode = new SearchNode(startNode, 0.0, null);
        pq.add(initialNode);
        
        // Dijkstra's algorithm main loop
        while (!pq.isEmpty()) {
            // Get the node with the smallest cost
            SearchNode current = pq.poll();
            
            // If we've found the end node, return the current SearchNode
            if (current.node.data.equals(end)) {
                return current;
            }
            
            // Skip if we've already visited this node with a lower cost
            if (visited.containsKey(current.node) && 
                visited.get(current.node).cost <= current.cost) {
                continue;
            }
            
            // Mark the current node as visited
            visited.put(current.node, current);
            
            // Explore all edges leaving the current node
            for (Edge edge : current.node.edgesLeaving) {
                // Calculate new cost to this neighbor
                double newCost = current.cost + edge.data.doubleValue();
                
                // Skip if we've already found a better path to this node
                if (visited.containsKey(edge.successor) && 
                    visited.get(edge.successor).cost <= newCost) {
                    continue;
                }
                
                // Create a new search node for this neighbor
                SearchNode neighborNode = new SearchNode(edge.successor, newCost, current);
                pq.add(neighborNode);
            }
        }
        
        // If we've explored all reachable nodes and haven't found the end node,
        // there's no path from start to end
        throw new NoSuchElementException("No path exists from " + start + " to " + end);
    }

    /**
     * Returns the list of data values from nodes along the shortest path
     * from the node with the provided start value through the node with the
     * provided end value. This list of data values starts with the start
     * value, ends with the end value, and contains intermediary values in the
     * order they are encountered while traversing this shorteset path. This
     * method uses Dijkstra's shortest path algorithm to find this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return list of data item from node along this shortest path
     */
    public List<NodeType> shortestPathData(NodeType start, NodeType end) {
        // Use computeShortestPath to find the path
        SearchNode endNode = computeShortestPath(start, end);
        
        // Create a linked list to store the path
        LinkedList<NodeType> path = new LinkedList<>();
        
        // Traverse from end to start using predecessor references
        // and add each node's data to the beginning of our list
        SearchNode current = endNode;
        while (current != null) {
            path.addFirst(current.node.data);
            current = current.predecessor;
        }
        
        return path;
    }

    /**
     * Returns the cost of the path (sum over edge weights) of the shortest
     * path from the node containing the start data to the node containing the
     * end data. This method uses Dijkstra's shortest path algorithm to find
     * this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return the cost of the shortest path between these nodes
     */
    public double shortestPathCost(NodeType start, NodeType end) {
        // implement in step 6.7 HAHAH 67 
        return computeShortestPath(start, end).cost;
    }
}
