import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class serves as a frontend implementation, that provides HTML for dealing with input from a
 * user and feeding that input to a backend implementation to compute user's questions
 * 
 * @author Christian Mendoza
 */
public class Frontend implements FrontendInterface {

  // stores a backend implementation to calculate requested paths
  BackendInterface backend = null;

  /**
   * Constructs a frontend instance given a backend implementation
   * 
   * @param backend is used for shortest path computations
   */
  public Frontend(BackendInterface backend) {
    this.backend = backend;
  }

  /**
   * Returns an HTML fragment that can be embedded within the body of a larger html page. This HTML
   * output should include:
   * 
   * - a text input field with the id="start", for the start location <br>
   * - a text input field with the id="end", for the destination <br>
   * - a button labelled "Find Shortest Path" to request this computation <br>
   * 
   * Ensure that these text fields are clearly labelled, so that the user can understand how to use
   * them.
   * 
   * @return an HTML string that contains input controls that the user can make use of to request a
   *         shortest path computation
   */
  @Override
  public String generateShortestPathPromptHTML() {
    return "<label>Enter Starting Location: </label><input type = \"text\" id = \"start\" placeholder = \"Start\"/><br>"
        + "<label>Enter Destination: </label><input type = \"text\" id = \"end\" placeholder = \"Destination\"/><br>"
        + "<label>Find Shortest Path</label><input type = \"button\" value = \"Go!\"/><br>";
  }

  /**
   * Returns an HTML fragment that can be embedded within the body of a larger html page. This HTML
   * output should include:
   * 
   * - a paragraph (p) that describes the path's start and end locations <br>
   * - an ordered list (ol) of locations along that shortest path <br>
   * - a paragraph (p) that includes the total travel time along this path <br>
   * 
   * Or if there is no such path, the HTML returned should instead indicate the kind of problem
   * encountered.
   * 
   * @param start is the starting location to find a shortest path from
   * @param end   is the destination that this shortest path should end at
   * @return an HTML string that describes the shortest path between these two locations
   */
  @Override
  public String generateShortestPathResponseHTML(String start, String end) {
    if (start == null || end == null) {
      return "<p>Start and End location cannot be null</p>";
    }
    List<String> pathLocations = this.backend.findLocationsOnShortestPath(start, end);
    String html = "";

    // locations empty, no path exists
    if (pathLocations.isEmpty()) {
      html = "<p>No path exists from " + start + " to " + end + "</p>";
    }

    // path does exist, output locations and walk time
    else {
      html = "<p>Here is the shortest path from " + start + " to " + end + "</p><ol>";
      for (String location : pathLocations) {
        html += "<li>" + location + "</li>";
      }
      html += "</ol>";

      List<Double> pathTimes = this.backend.findTimesOnShortestPath(start, end);
      double totalTime = 0.0;
      for (Double time : pathTimes) {
        totalTime += time;
      }
      html += "<p>The total walking time along this path is " + totalTime + " seconds</p>";
    }

    return html;
  }

  /**
   * Returns an HTML fragment that can be embedded within the body of a larger html page. This HTML
   * output should include:
   * 
   * - a text input field with the id="from", for the start location <br>
   * - a button labelled "Furthest Destination From" to submit this request <br>
   * 
   * Ensure that this text field is clearly labelled, so that the user can understand how to use it.
   * 
   * @return an HTML string that contains input controls that the user can make use of to request a
   *         furthest destination calculation
   */
  @Override
  public String generateFurthestDestinationFromPromptHTML() {
    return "<input type = \"text\" id = \"from\" placeholder = \"Enter Starting Location\"/>"
        + "<input type = \"button\" value = \"Furthest Destination From\"/>";
  }

  /**
   * Returns an HTML fragment that can be embedded within the body of a larger html page. This HTML
   * output should include:
   * 
   * - a paragraph (p) that describes the starting point being searched from <br>
   * - a paragraph (p) that describes the furthest destination found <br>
   * - an ordered list (ol) of locations on the path between these locations <br>
   * 
   * Or if there is no such destination, the HTML returned should instead indicate the kind of
   * problem encountered.
   * 
   * @param start is the starting location to find the furthest dest from
   * @return an HTML string that describes the furthest destination from the specified start
   *         location
   */
  @Override
  public String generateFurthestDestinationFromResponseHTML(String start) {
    if (start == null) {
      return "<p>Start and End location cannot be null</p>";
    }
    String html = "";
    try {
      String furthest = this.backend.getFurthestDestinationFrom(start);
      html = "<p>Starting Location: " + start + "</p><p>Furthest Destination from Start: "
          + furthest + "</p><p>Here is how to get there:</p><ol>";
      List<String> pathLocations = this.backend.findLocationsOnShortestPath(start, furthest);
      for (String location : pathLocations) {
        html += "<li>" + location + "</li>";
      }
      html += "</ol>";
    } catch (NoSuchElementException e) {
      // backend threw an exception, meaning invalid start or no path from that start
      html = "<p>" + start + " does not exist or no other locations can be reached</p><p>Error: " + e.getMessage() + "</p>";
    }

    return html;
  }
}
