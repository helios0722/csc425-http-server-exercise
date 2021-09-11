package edu.murraystate.httpserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import edu.murraystate.httpserver.HTTPRequest.Method;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class HTTPRequestTest {
  @Test
  public void constructionTestNoBody(){
    final Map<String,String> headers = new HashMap<>();
    headers.put("dummy", "value");
    final HTTPRequest test = new HTTPRequest(Method.GET, "test", headers);
    assertEquals(Method.GET, test.getMethod());
    assertEquals(headers, test.getHeaders());
    assertEquals("test", test.getPath());
    assertEquals(1, test.getHeaders().size());
    assertEquals("value", test.getHeaders().get("dummy"));
    assertFalse(test.hasBody());
  }

  @Test
  public void constructionTestWithBody(){
    final Map<String,String> headers = new HashMap<>();
    headers.put("dummy", "value");
    final HTTPRequest test = new HTTPRequest(Method.GET, "test", headers, "test body");
    assertEquals(Method.GET, test.getMethod());
    assertEquals(headers, test.getHeaders());
    assertEquals("test", test.getPath());
    assertEquals("test body", test.getBody());
    assertEquals(1, test.getHeaders().size());
    assertEquals("value", test.getHeaders().get("dummy"));
    assertTrue(test.hasBody());
  }

  @Test
  public void simpleGet() throws IOException {
    final String rawRequest = "GET /test/path HTTP/1.1\nContent-Length: 8\n\nthe body";
    final InputStream is = createInputStream(rawRequest);
    final HTTPRequest request = HTTPRequest.parse(is);
    final Map<String,String> expectedHeaders = new HashMap<>();
    expectedHeaders.put("Content-Length", "8");
    final HTTPRequest expected = new HTTPRequest(Method.GET, "/test/path", expectedHeaders, "the body");
    testResult(expected, request);
  }

  @Test
  public void simplePost() throws IOException {
    final String rawRequest = "POST /a/b/c HTTP/1.1\nContent-Length: 9\n\nthe body!";
    final InputStream is = createInputStream(rawRequest);
    final HTTPRequest request = HTTPRequest.parse(is);
    final Map<String,String> expectedHeaders = new HashMap<>();
    expectedHeaders.put("Content-Length", "9");
    final HTTPRequest expected = new HTTPRequest(Method.POST, "/a/b/c", expectedHeaders, "the body!");
    testResult(expected, request);
  }

  @Test
  public void simplePut() throws IOException {
    final String rawRequest = "PUT /test/path/put HTTP/1.1\nContent-Length: 7\n\nthe bod";
    final InputStream is = createInputStream(rawRequest);
    final HTTPRequest request = HTTPRequest.parse(is);
    final Map<String,String> expectedHeaders = new HashMap<>();
    expectedHeaders.put("Content-Length", "7");
    final HTTPRequest expected = new HTTPRequest(Method.PUT, "/test/path/put", expectedHeaders, "the bod");
    testResult(expected, request);
  }

  @Test
  public void simpleDelete() throws IOException {
    final String rawRequest = "DELETE /test/this/too HTTP/1.1\nContent-Length: 10\n\nthe body x";
    final InputStream is = createInputStream(rawRequest);
    final HTTPRequest request = HTTPRequest.parse(is);
    final Map<String,String> expectedHeaders = new HashMap<>();
    expectedHeaders.put("Content-Length", "10");
    final HTTPRequest expected = new HTTPRequest(Method.DELETE, "/test/this/too", expectedHeaders, "the body x");
    testResult(expected, request);
  }

  @Test
  public void extraHeaders() throws Exception {
    final Map<String,String> expectedHeaders = new HashMap<>();
    expectedHeaders.put("Accept","text/plain");
    expectedHeaders.put("Content-Length", "4");
    final HTTPRequest expected = new HTTPRequest(Method.GET, "/another/path", expectedHeaders, "abcd");
    final String rawRequest = "GET /another/path HTTP/1.1\nAccept: text/plain\nContent-Length: 4\n\nabcd";
    final HTTPRequest actual = HTTPRequest.parse(createInputStream(rawRequest));
    testResult(expected, actual);
  }

  @Test
  public void noHeadersLoadingTest() throws IOException {
    final BufferedReader reader = createBufferedReader("");
    final Map<String,String> result = HTTPRequest.loadHeaders(reader);
    assertTrue(result.isEmpty());
  }

  @Test
  public void noHeadersLoadingTestTwo() throws IOException {
    final BufferedReader reader = createBufferedReader("    ");
    final Map<String,String> result = HTTPRequest.loadHeaders(reader);
    assertTrue(result.isEmpty());
  }

  @Test
  public void twoHeadersLoadTest() throws IOException {
    final BufferedReader reader = createBufferedReader("x:    y     \nz:a\n");
    final Map<String,String> result = HTTPRequest.loadHeaders(reader);
    assertEquals(2, result.size());
    assertEquals("y", result.get("x"));
    assertEquals("a", result.get("z"));
  }

  @Test
  public void oneHeaderLoadTest() throws IOException {
    final BufferedReader reader = createBufferedReader("x:    y     \n");
    final Map<String,String> result = HTTPRequest.loadHeaders(reader);
    assertEquals(1, result.size());
    assertEquals("y", result.get("x"));
  }

  @Test
  public void requestLineProcessingTest(){
    final String requestLine = "HEAD /some/test/path HTTP/2.0";
    final String[] requestLineChunks = HTTPRequest.processRequestLine(requestLine);
    assertEquals(3, requestLineChunks.length);
    assertEquals("HEAD", requestLineChunks[0]);
    assertEquals("/some/test/path", requestLineChunks[1]);
    assertEquals("HTTP/2.0", requestLineChunks[2]);
  }

  @Test
  public void getBodyTest() throws IOException {
    final BufferedReader reader = createBufferedReader("a document to read");
    final Map<String,String> headers = new HashMap<>();
    headers.put("Content-Length", "18");
    final String body = HTTPRequest.getBody(headers, reader);
    assertEquals("a document to read", body);
  }

  @Test
  public void getBiggerBody() throws IOException {
    final BufferedReader reader = createBufferedReader("some\ndoc   ");
    final Map<String,String> headers = new HashMap<>();
    headers.put("Content-Length", "11");
    final String body = HTTPRequest.getBody(headers, reader);
    assertEquals("some\ndoc   ", body);
  }

  @Test
  public void getNoBodyIfNoHeader() throws IOException {
    final BufferedReader reader = createBufferedReader("asdasd");
    final Map<String,String> headers = new HashMap<>();
    final String body = HTTPRequest.getBody(headers, reader);
    assertEquals("", body);
  }

  private void testResult(final HTTPRequest expected, HTTPRequest actual){
    assertEquals(expected.getPath(), actual.getPath());
    assertEquals(expected.getMethod(), actual.getMethod());
    assertEquals(expected.getBody(), actual.getBody());
    assertEquals(expected.getHeaders(), actual.getHeaders());
  }

  private BufferedReader createBufferedReader(final String content){
    return new BufferedReader(new InputStreamReader(createInputStream(content)));
  }

  private InputStream createInputStream(final String content){
    return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
  }
}
