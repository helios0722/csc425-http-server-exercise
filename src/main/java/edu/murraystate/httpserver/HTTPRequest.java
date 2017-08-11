package edu.murraystate.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A class for representing a client request which our server should be able to
 * handle.
 */
public class HTTPRequest {
  private final Map<String, String> headers;
  private final Method method;
  private final String path;
  private final String body;

  /**
   * A simple constructor which assigns the fields as appropriate
   *
   * @param method  the method of the request
   * @param path    the path of th resource for this request
   * @param headers the headers of the request
   * @param body    the body of the request
   */
  protected HTTPRequest(final Method method, final String path, final Map<String, String> headers, final String body) {
    this.headers = headers;
    this.method = method;
    this.path = path;
    this.body = body;
  }

  /**
   * A convenience constructor for cases where there is no body in the request
   *
   * @param method  the method of the request
   * @param path    the path of th resource for this request
   * @param headers the headers of the request
   */
  protected HTTPRequest(final Method method, final String path, final Map<String, String> headers) {
    this.headers = headers;
    this.method = method;
    this.path = path;
    this.body = "";
  }

  /**
   * A simple getter for the request path
   * 
   * @return the resource path from the request line
   */
  public String getPath() {
    return this.path;
  }

  /**
   * A simple getter for the request method
   *
   * @return one of the values defined in the Method enum
   */
  public Method getMethod() {
    return this.method;
  }

  /**
   * A simple getter for the body of the request
   *
   * @return the request body
   */
  public String getBody() {
    return this.body;
  }

  /**
   * A simple method for indicating if there is a body or not.
   *
   * @return true if the request had a body
   */
  public boolean hasBody() {
    return !this.body.isEmpty();
  }

  /**
   * A simple getter for returning a map of the headers from the request.
   *
   * @return the request headers as a map
   */
  public Map<String, String> getHeaders() {
    return this.headers;
  }

  /**
   * A parsing method which will take a given input stream and parse its contents
   * in order to create a complete HTTPRequest object.
   *
   * @param inputStream an input stream for the raw request received by the server
   * @return an HTTPRequest object representing the received request
   * @throws IOException
   */
  public static HTTPRequest parse(final InputStream inputStream) throws IOException {
    String result = new String(inputStream.readAllBytes());
    String[] items = result.split(" ");
    Method method = Method.valueOf(items[0]);
    String path = items[1];
    String next = String.join(" ", Arrays.copyOfRange(items, 2, items.length));
    items = next.split("\n\n");
    Map<String, String> headers = loadHeaders(new BufferedReader(new StringReader(items[0])));
    String body = items[1];
    return body.isEmpty() ? new HTTPRequest(method, path, headers) : new HTTPRequest(method, path, headers, body);
  }

  /**
   * A helper method for retrieving the body of a given request.
   *
   * Note: this should not be called until the request line and headers have been
   * extracted from the input stream.
   *
   * @param headers the headers for the request
   * @param reader  the reader for the request
   * @return the body of the request as a String
   * @throws IOException
   */
  protected static String getBody(final Map<String, String> headers, final BufferedReader reader) throws IOException {
    if (headers.isEmpty())
      return "";
    String body = reader.lines().collect(Collectors.joining("\n"));
    return body;
  }

  /**
   * A helper method which breaks the request line down into method, path and HTTP
   * version strings.
   *
   * @param line the request line
   * @return the three parts of the request line in an array
   */
  protected static String[] processRequestLine(final String line) {
    String[] ret = line.split(" ");
    return ret;
  }

  /**
   * A helper method which will extract the headers from the request and return
   * them in a map.
   *
   * Note: this should not be called until after the request line has been read
   * from the request!
   *
   * @param reader the reader for the request
   * @return a map containing all of the key/value pairs for the headers
   * @throws IOException
   */
  protected static Map<String, String> loadHeaders(final BufferedReader reader) throws IOException {
    Map<String, String> headers = new HashMap<>();
    String item = null;
    while ((item = reader.readLine()) != null) {
      String[] var = item.trim().split(":");
      if (var.length < 2)
        continue;
      headers.put(var[0].trim(), var[1].trim());
    }
    return headers;
  }

  /**
   * An enumeration representing all of the supported HTTP methods
   */
  public enum Method {
    GET, PUT, POST, DELETE;
  }

}
