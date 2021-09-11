package edu.murraystate.httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

/**
 * The main executable class for our server. Executing this main method should
 * "launch" our server such that it will be waiting for any incoming requests.
 */
public class Server {
  private static final Data data = new Data();
  private static final RequestHandler handler = new RequestHandler(data);

  /**
   * The core logic of our server. It should: - create a server socket, then,
   * repeatedly + accept a client socket connection + parse its requests + handle
   * the request + write a response to the client
   *
   * @param args
   * @throws IOException
   */
  public static void main(final String... args) throws IOException {
    int port = args.length < 1 ? 999 : Integer.parseInt(args[0]);
    System.out.format("Server started. Listening on Port %d\n", port);

    ServerSocket serverSocket = null;
    Socket socket = null;

    try {
      serverSocket = ServerSocketFactory.getDefault().createServerSocket(port);
    } catch (IOException e) {
      System.err.format("Exception caught when trying to listen on port %d or listening for a connection\n");
    }

    while (true) {
      try {
        socket = serverSocket.accept();
      } catch (IOException e) {
        System.out.println("I/O error: " + e);
      }
      // new thread for a client
      new EchoThread(socket, handler).start();
    }
  }

  public static class EchoThread extends Thread {
    protected Socket socket;
    protected RequestHandler handler;

    public EchoThread(Socket clientSocket, RequestHandler handler) {
      this.socket = clientSocket;
      this.handler = handler;
    }

    public void run() {
      try {
        HTTPRequest request = HTTPRequest.parse(socket.getInputStream());
        this.handler.handle(request);
      } catch (IOException e) {
        return;
      }
    }
  }
}
