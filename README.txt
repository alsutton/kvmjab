Documentation for Java Jabber library
=====================================

Requirments
-----------

To use this system you must have a working Java 2 virtual machine (either the JVM or the 
KVM).



Whats Included
--------------

Included in this release (1.0.1) is;

- The source code for the Jabber library and XML parser
- The source code for two example jabber clients (j2se & j2me)
- The source code for a Palm Pilot KVM based echoing client
- A MS-DOS batch file and file list which builds the Palm Pilot client (appologies to the 
UNIX users, The batch file is a very simple conversion job to whichever shell you use)
- This Readme file


Example code
------------

Two examples of how to use this code are located in the directory 
com\alsutton\jabber\examples, the Java2se.java example is for use on the Java 2 Standard 
Edition virtual machine (JVM), and the Java2me.java example is for use on the Java 2 Micro 
Edition virtual machine (JVM or KVM).



How to implement your own client
--------------------------------

* Listening class *

To implement your own client your class which handles incomming blocks must implement the 
interface  com.alsutton.jabber.JabberListener which has the following methods;


  public void beginConversation();

(Which is called once the stream has been set up. It is recommended that this method
sends the users login details)

  public void blockArrived( JabberDataBlock data );

(Which is called when a block of data arrives)

  public void connectionTerminated( Exception e );

(Which is called when the connection is terminated abnormally)

Examples of how to use these methods are shown in the two example programs.

* Sending class *

The class which will send messages needs to establish a connection to open a stream 
to the server and set the listener class thus;

      JabberStream theStream = new JabberStream( new seConnector( SERVER_NAME, SERVER_PORT ) );
      theStream.setJabberListener( this );

[This example is for the j2se, for the j2me you should instanciate an meConnector object 
using the same parameters as the seConnector object above]

Once this has been done the class is free to send data to the server using the following
method defined in the JabberStream class;

  public void send( JabberDataBlock block ) throws IOException




Do you think this code is useful?
---------------------------------

If so please make a donation to my PayPal account called "al@alsutton.com".

More info about PayPal can be found at http://www.paypal.com/
