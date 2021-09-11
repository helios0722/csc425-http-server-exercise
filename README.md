# HTTP Server

The purpose of this project is to help you understand how an HTTP server works. While this
implementation will only support a subset of the HTTP protocol, you should at least be able to
develop a basic understanding of how a real implementation works at a high level. You will also
gain experience working with Java sockets, which allow for the creation of network applications
using TCP/IP connections.

**Note**: The assignment must be completed in the specific manner described in the instructions. 
Any deviations from the instructions will possibly cause the automated grading to fail and will be 
considered incorrect. "Following the instructions" includes, but is not limited to:

* Do not change the test code in any way. If the test code does not compile, change your implementation 
  of the classes being tested, not the tests! (you may add additional tests)
* Do not modify interfaces and abstract classes provided to you in any way unless specifically 
  instructed to do so.
* Do not modify class constructors, method signatures, or local field names unless specifically 
  instructed to do so.
* Do not add fields to any classes unless specifically instructed to do so.

## Note About Academic Honesty

You should keep your code and repositories private and should not be sharing code under *any* 
circumstances. **When forking your projects, you should make sure the repository is set to 
private, and you should immediately remove the class's team, **CSC425**, from your project**. Then,
add me (**pwright4**) to your project so that I will be the only person who can access your code 
other than you! If you do not do this and check in large amounts of code, I will consider this a 
violation of the academic honesty policies of MSU.

## Read Instructions First Before Starting

For best results, read the following instructions once, completely, before you attempt to write
any code. Do not dive in with no clue about what your goals are. Prepare, then program!

## Overview

For this project, you will be implementing an HTTP server which can be used to store, update,
retrieve and delete plain text documents (your implementation will only support the
*GET*, *POST*, *PUT*, and *DELETE* methods). For example, if the following request is sent to
your HTTP server:

```http request
POST /path/to/document HTTP/1.1
Content-Length: 12

Hello World!
```

it will store the document

```
Hello World!
```

at the path

```
/path/to/document
```

in the local, in memory, data store.

If you were then sent the following request:

```http request
GET /path/to/document HTTP/1.1

```

The previously POSTed document would be retrieved from the data store and returned to
the user in the following response:

```http_request
HTTP/1.1 200 OK
content-type: text/plain

Hello World!
```

### A Quick Note About URLs

The complete structure of a URL is covered elsewhere, but we should note that, when you enter a
URL in your browser, it will, more or less, be broken into a *host* and a resource *path*. The host
will be included in a header in the HTTP request, and the path will be part of the request line.

For example, given the URL **127.0.0.1/path/to/document**, the *host* will be *127.0.0.1* and the
path for the request line will be */path/to/document*.

**Note**: when you enter a URL into the browser, all it is doing is generating a *GET* request,
similar to what we have shown above, for whatever resource the entered URL represents. In other words,
we can test the *GET* functionality of our HTTP server. Unfortunately, we can not easily use
a browser to test the rest of the functionality. For this, we will use [Postman](https://www.postman.com/)
(see below).

### Server Structure

The server you will implement is broken down into the following individual parts:

* The server code which handle TCP/IP connections
* Code for parsing an HTTP request so that it will be easier to work with
* Code for processing the HTTP request after it has been parsed
* A backend storage mechanism for our documents

Roughly speaking, your HTTP server will function as follows:

* Initialize the server
* Wait for a client to connect, then
  + Obtain the *InputStream* from the client in order to read its request
  + Parse the request to create an *HTTPRequest* instance
  + Determine the *method* of the request and use that (plus any additional data needed) to determine
    the proper way to interact with the data store
  + Perform the necessary actions to manipulate the data store
  + Generate a response
  + Write the response to the client's *OutputStream*
  + Wait for the next client and do it all again!  

## Instructions

The following instructions are presented in, roughly, the ideal order in which you should complete
the project. After each step, you should use the provided unit tests to check and see if your
code is working properly before moving on to the next step.

### Compilation

First and foremost, you should ensure that your code compiles! If your code does not compile:

* You will get a zero on the assignment
* It will make debugging your code impossible

By ensuring that, first, your code compiles, you will ensure that you have a clear starting point
for completing your work, and you will be able to incrementally make changes and test them to
determine your progress. **Do not attempt to simply sit down and write the code in one go,
then debug. You will only be making your life unnecessarily difficult!**

Once you believe your code is compiling, execute your unit tests and make sure that the entire
build and test process work. **You will have nothing but test failures, but this will at least
ensure that the code can be built and tested**

### Data

Once you have a clean base to start with, you should proceed to the *Data* class, which will store
our documents in a *Map<String,String>* instance where the *key* will be a resource path and
the value will be the document. You should initialize this map, then use it in methods
in the *Data* class as necessary.

Simply provide the method bodies for the given method signatures then, then make sure that all tests
in the class *DataTest* are passing. 

#### Notes

Consider the following when implementing your data store code:

* "getting" a document should never change the data store
* "getting" a document which does not exist should return a null value
* "deleting" a document should never fail, even if the document doesn't exist
* "posting" a document should not change the data store if there is already a document at that location
* "putting" a document will be equivalent to "posting" if there is no document at the given path.
* ...otherwise, "putting" should cause the old document to be overwritten by the new document

### HTTPRequest

Once you are certain your data store is working as expected, move on to the *HTTPRequest* class, which
is a nice, object-oriented representation of the request sent by the client. 

#### Basic Setup

First, make sure that your *HTTPRequest* has proper constructors and can be instantiated with all
fields initialized to the proper values. Next, implement the simple "getter" methods
which will do nothing more than return the fields of the class (more or less).

#### Parsing

After you have the basics of the class worked out, you need to provide the necessary logic for
parsing (in the *parse* method) a given request and generating an instance of *HTTPRequest*. The rough algorithm for this
is:

* Process the request line (using *processRequestLine*)
* Read and load the headers (using *loadHeaders*)
* Extract the body of the request (using *getBody*)
* Use the above data to create an instance of *HTTPRequest*

##### processRequestLine

This method will simply take the first line of the request and split it into three separate *String*
objects representing the *method*, *path* and *http version*, respectively.

##### loadHeaders

This method will iterate through the header lines in the request and, from each line, extract
the *key* and *value* and add them to a *Map<String,String>* instance which will be returned at
the end of the method's execution. For example, given the following headers:

```http request
...
Accept: text/plain
Content-Length: 10

...
```

this method will return a map containing:

|  key             |     value          |
|:----------------:|:------------------:|
| Accept           |   text/plain       |
| Content-Length   |      10            |

##### getBody

In this method, you will need to read the body of the request, which is, basically, our "document"
(or empty!).

**Note**: After *loadHeaders* has executed, the body should be the next *n* bytes in the *InputStream*, where
*n* is the value of the *Content-Length* header. If, however, there is no body, you might find that
things behave a bit differently. If there is *no* content length header, then you should assume
the body is the empty *String*.



Once you have completed these helper methods and the *parse* method, double check that all tests
in the *HTTPRequestTest* class are passing.

### RequestHandler

Now that you can parse a request and obtain an *HTTPRequest* instance, we are ready to implement
the *RequestHandler* which will use an *HTTPRequest* and a given *Data* class to properly execute
the desired actions.

#### Basic Setup

As with the *HTTPRequest* class, first make sure your constructor and fields are all set up properly.

#### generateResponse

For every request, we must generate a response which can be sent to the client. This will be broken
down to the following steps:

* Generate the appropriate status line
* Add a header for the *content-type* (the value should be "text/plain")
* Append the body of the response (if it needs one!)

**Note**: you are given two method signatures for *generateResponse*. One accepts a *Code*
and the other accepts a *Code* and a *String* for the body. You should focus on implementing the
second one, first, then implement the first one by simply calling the second one and providing
and empty body.

#### generateStatusLine

Remember that a status line will be something like:

```http request
HTTP/1.1 200 OK
```

Using the given *Code* instance, constructing such a *String* should be a fairly straightforward
exercise.

#### handle

The *handle* method is the core of the *RequestHandler* class. You will be given an *HTTPRequest*
and must use the data it contains to properly determine how you should interact with the *Data*
instance that was passed into the constructor. For each request, you must:

* Determine the *method*
* Perform the necessary interactions with the *Data* instance
* Generate and return the appropriate response for the client

After you have finished the implementation of this class, be sure to double check that all tests
in *RequestHandlerTest* are passing.

### Server

Now that the supporting classes have been completed, you should tie everything together in the
*Server* class. In this class, you only have a single *main* method. As described previously,
this method should:

* Initialize the server (creating *Data* and *RequestHandler* instances)
* Open a socket on port 9999
* Until the program is terminated...
  + wait for a client to connect
  + parse their request
  + handle the parsed request
  + write a response back to the client  
    
**Note**: your server should be able to handle multiple requests! If execution terminates after
a single connection, you should rethink your design!

#### Testing

At this point, we can't really do any unit testing, because now we have a complete application.
Accordingly, we will need to do some *integration tests* using [Postman](https://www.postman.com/).
In the root directory of your project, you will find a configuration file for Postman which you
can load and execute. This will trigger a series requests to be sent to your server and the responses
received will be compared to an expected  response.

To use Postman, you will need to do the following:
* Import the collection into Postman
* Click the |-> arrow to expand the menu, then click run
* In the popup menu, make sure all tests are selected, then click "Run Basic HTTP Server"

##### Restart Before Re-testing

A common problem students run into is executing the tests, fixing failures, then running the
tests again and not seeing any changes. The issue is that your changes won't be reflected in the
*currently running* server process. You must terminate the server and relaunch to see the effect
of your changes.

Also, it may be helpful to do a complete restart of your server to debug, because if you had a
failed execution, you may not really understand what the current state is and it would be better
to simply start fresh and try one request at a time.

Once you are confident you have fixed everything, I would recommend double checking by killing the
current server process, making sure all files are saved and closed, then restarting the server
and executing a full test run.

**Note**: Postman tests are *not* executed on Travis. So, please be sure that you actually
uploaded everything you think. Travis will help, but it is not a *guarantee* that everything will
work when I download your code!

## Submission

To "submit" your code, just ensure that I am added to your project and that *all* changes that
you made are reflected on GitHub. At the deadline, I will run an automated process to download
and execute your code.
