package edu.murraystate.httpserver;

import java.util.HashMap;
import java.util.Map;

/**
 * A very simple data store which will be used to "persist" the "documents" on
 * our server.
 */
public class Data {
  private final Map<String, String> data = new HashMap<>();

  /**
   * A method for implementing the behavior of a GET request. If there is a
   * document in the underlying map for the given location, returns that document.
   * Otherwise, returns null.
   *
   * @param location the path of the desired resource
   * @return the resource, if it exists
   */
  public String get(final String location) {
    return this.data.getOrDefault(location, null);
  }

  /**
   * A method for implementing the behavior of a POST request. If there is already
   * a document at the given location, this operation should have no effect.
   * Otherwise, this should add the given document at the given location.
   *
   * @param location the path where the document should be stored
   * @param document the document that should be stored
   */
  public void post(final String location, final String document) {
    if (this.data.containsKey(location))
      return;
    this.data.put(location, document);
  }

  /**
   * A method for implementing the behavior of a DELETE request. If there is no
   * document at the given location, this will have no effect. Otherwise, the
   * document should be removed from the underlying map.
   *
   * @param location the location of the resource to delete
   */
  public void delete(final String location) {
    if (this.data.containsKey(location))
      this.data.remove(location);
  }

  /**
   * A method for implementing the behavior of a PUT request. If there is no
   * document at the given location, then the given document should be added at
   * the location. If there is an existing document at the given location, it
   * should be replaced with the given document.
   *
   * @param location the location of the document to add/update
   * @param document the updated document
   */
  public void put(final String location, final String document) {
    this.data.put(location, document);
  }
}
