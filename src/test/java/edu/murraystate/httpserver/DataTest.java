package edu.murraystate.httpserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class DataTest {
  @Test
  public void getNonexistentDoc(){
    final Data data = new Data();
    assertNull(data.get("/doesn't/exist"));
  }

  @Test
  public void putGetDoc(){
    final Data data = new Data();
    data.put("/here", "a doc");
    assertEquals("a doc", data.get("/here"));
  }

  @Test
  public void postGetDoc(){
    final Data data = new Data();
    data.post("/here", "a doc");
    assertEquals("a doc", data.get("/here"));
  }

  @Test
  public void postPostTest(){
    final Data data = new Data();
    data.post("/here", "a doc");
    data.post("/here", "another doc");
    assertEquals("a doc", data.get("/here"));
  }

  @Test
  public void putPutTest(){
    final Data data = new Data();
    data.put("/here", "a doc");
    data.put("/here", "another doc");
    assertEquals("another doc", data.get("/here"));
  }

  @Test
  public void postDeleteTest(){
    final Data data = new Data();
    data.post("/something", "data");
    data.delete("/something");
    assertNull(data.get("/something"));
  }
}
