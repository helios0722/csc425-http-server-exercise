package edu.murraystate.httpserver;

import java.util.Objects;

/**
 * A class which will be used to process parsed HTTPRequest instances.
 */
public class RequestHandler {
  private final Data data;

  /**
   * A simple constructor for creating a RequestHandler instance
   * 
   * @param data the Data object backing this server instance
   */
  public RequestHandler(final Data data) {
    this.data = data;
  }

  /**
   * This method will perform the actual processing of the HTTPRequests. You will
   * need to use the given object to determine the method being requested, the
   * resource being referenced, and, if available, the contents of the request.
   * According to the method and location of the request, you will need to make
   * the proper calls to the underlying Data instance. Finally, you must generate
   * an appropriate response to send back to the client.
   *
   * @param request the parsed request
   * @return the response which should be sent to the client
   */
  public String handle(final HTTPRequest request) {
    if (request.getMethod() == HTTPRequest.Method.DELETE)
      this.data.delete(request.getPath());
    else if (request.getMethod() == HTTPRequest.Method.PUT)
      this.data.put(request.getPath(), request.getBody());
    else if (request.getMethod() == HTTPRequest.Method.POST)
      this.data.post(request.getPath(), request.getBody());
    else if (request.getMethod() == HTTPRequest.Method.GET) {
      String document = this.data.get(request.getPath());
      if (document == null)
        return generateResponse(Code.NOT_FOUND);
      return generateResponse(Code.OK, document);
    }
    return generateResponse(Code.OK);
  }

  /**
   * A helper method which will generate a full response to return to the client.
   *
   * @param responseCode the Code value for this response
   * @return the response to send to the client
   */
  protected String generateResponse(final Code responseCode) {
    String status = generateStatusLine(responseCode);
    String content = "content-type: text/plain\n\n";
    return status + content;
  }

  /**
   * A helper method which will generate a full response to return to the client.
   *
   * @param responseCode the Code value for this response
   * @param body         the body of the response
   * @return the complete response to send to the client
   */
  protected String generateResponse(final Code responseCode, final String body) {
    String status = generateStatusLine(responseCode);
    String content = "content-type: text/plain\n\n";
    return status + content + body;
  }

  /**
   * Helper method which will take a given Code value and turn it into a valid
   * status line for the client response.
   *
   * @param responseCode the Code value for the status line
   * @return the status line for the response
   */
  protected String generateStatusLine(final Code responseCode) {
    return String.format("HTTP/1.1 %d %s\n", responseCode.code, responseCode.message);
  }

  /**
   * Convenience enumeration for the response codes we support. Each value stores
   * its corresponding integer code and message.
   */
  protected enum Code {
    NOT_FOUND(404, "NOT FOUND"), OK(200, "OK");

    public final int code;
    public final String message;

    Code(final int code, final String message) {
      this.code = code;
      this.message = Objects.requireNonNull(message);
    }
  }
}
