package edu.murraystate.httpserver;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import edu.murraystate.httpserver.HTTPRequest.Method;
import edu.murraystate.httpserver.RequestHandler.Code;
import java.util.HashMap;
import org.junit.Test;

public class RequestHandlerTest {
  @Test
  public void handleGetNotFound(){
    final Data mocked = mock(Data.class);
    final HTTPRequest request = new HTTPRequest(Method.GET, "/some/path", new HashMap<>());
    final RequestHandler handler = new RequestHandler(mocked);
    final String response = handler.handle(request);
    final String expected = "HTTP/1.1 404 NOT FOUND\ncontent-type: text/plain\n\n";
    assertEquals(expected, response);
    verify(mocked, times(1)).get("/some/path");
    verifyNoMoreInteractions(mocked);
  }

  @Test
  public void handleGetOk(){
    final Data mocked = mock(Data.class);
    when(mocked.get("/some/other/path")).thenReturn("some fake document");
    final HTTPRequest request = new HTTPRequest(Method.GET, "/some/other/path", new HashMap<>());
    final RequestHandler handler = new RequestHandler(mocked);
    final String response = handler.handle(request);
    final String expected = "HTTP/1.1 200 OK\ncontent-type: text/plain\n\nsome fake document";
    assertEquals(expected, response);
    verify(mocked, times(1)).get("/some/other/path");
    verifyNoMoreInteractions(mocked);
  }

  @Test
  public void handlePostOk(){
    final Data mocked = mock(Data.class);
    final HTTPRequest request = new HTTPRequest(Method.POST, "/some/post/path", new HashMap<>(),"posted doc");
    final RequestHandler handler = new RequestHandler(mocked);
    final String response = handler.handle(request);
    final String expected = "HTTP/1.1 200 OK\ncontent-type: text/plain\n\n";
    assertEquals(expected, response);
    verify(mocked, times(1)).post("/some/post/path", "posted doc");
    verifyNoMoreInteractions(mocked);
  }

  @Test
  public void handlePutOk(){
    final Data mocked = mock(Data.class);
    final HTTPRequest request = new HTTPRequest(Method.PUT, "/another/path", new HashMap<>(),"thing we put");
    final RequestHandler handler = new RequestHandler(mocked);
    final String response = handler.handle(request);
    final String expected = "HTTP/1.1 200 OK\ncontent-type: text/plain\n\n";
    assertEquals(expected, response);
    verify(mocked, times(1)).put("/another/path", "thing we put");
    verifyNoMoreInteractions(mocked);
  }

  @Test
  public void handleDelete(){
    final Data mocked = mock(Data.class);
    final HTTPRequest request = new HTTPRequest(Method.DELETE, "/path", new HashMap<>());
    final RequestHandler handler = new RequestHandler(mocked);
    final String response = handler.handle(request);
    final String expected = "HTTP/1.1 200 OK\ncontent-type: text/plain\n\n";
    assertEquals(expected, response);
    verify(mocked, times(1)).delete("/path");
    verifyNoMoreInteractions(mocked);
  }

  @Test
  public void generate404StatusLineTest(){
    final Data mocked = mock(Data.class);
    final RequestHandler handler = new RequestHandler(mocked);
    final String output = handler.generateStatusLine(Code.NOT_FOUND);
    final String expected = "HTTP/1.1 404 NOT FOUND\n";
    assertEquals(expected, output);
  }

  @Test
  public void generate200StatusLineTest(){
    final Data mocked = mock(Data.class);
    final RequestHandler handler = new RequestHandler(mocked);
    final String output = handler.generateStatusLine(Code.OK);
    final String expected = "HTTP/1.1 200 OK\n";
    assertEquals(expected, output);
  }

  @Test
  public void generate404ResponseNoBodyTest(){
    final Data mocked = mock(Data.class);
    final RequestHandler handler = new RequestHandler(mocked);
    final String output = handler.generateResponse(Code.NOT_FOUND);
    final String expected = "HTTP/1.1 404 NOT FOUND\ncontent-type: text/plain\n\n";
    assertEquals(expected, output);
  }

  @Test
  public void generate200ResponseNoBodyTest(){
    final Data mocked = mock(Data.class);
    final RequestHandler handler = new RequestHandler(mocked);
    final String output = handler.generateResponse(Code.OK);
    final String expected = "HTTP/1.1 200 OK\ncontent-type: text/plain\n\n";
    assertEquals(expected, output);
  }

  @Test
  public void generate200ResponseWithBodyTest(){
    final Data mocked = mock(Data.class);
    final RequestHandler handler = new RequestHandler(mocked);
    final String output = handler.generateResponse(Code.OK, "document body");
    final String expected = "HTTP/1.1 200 OK\ncontent-type: text/plain\n\ndocument body";
    assertEquals(expected, output);
  }

}
