/*
  Copyright (c) 2000, Al Sutton (al@alsutton.com)
  All rights reserved.
  Redistribution and use in source and binary forms, with or without modification, are permitted
  provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice, this list of conditions
  and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright notice, this list of
  conditions and the following disclaimer in the documentation and/or other materials provided with
  the distribution.

  Neither the name of Al Sutton nor the names of its contributors may be used to endorse or promote
  products derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS ``AS IS'' AND ANY EXPRESS OR
  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
  THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.alsutton.jabber.examples;

/**
 * Title:        Java2se.java
 * Description:  Example for Java2se. Connects to a server,
 */

import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;

import java.io.*;

public class Java2se implements JabberListener
{
  /**
   * The name of the server to connect to
   */

  private static final String SERVER_NAME = "jabber.com";

  /**
   * The port on the server to bind to
   */

  private static final int SERVER_PORT = 5222;

  /**
   * The username to log in via
   */

  private static final String USERNAME = /*"[insert-valid-username]"*/;

  /**
   * The password to use
   */

  private static final String PASSWORD = /*"[insert-valid-password]"*/;

  /**
   * The resource to log in as
   */

  private static final String RESOURCE = "J2SE Test client";

  /**
   * The name of the recipient for the test message
   */

  private static final String TEST_MESSAGE_RECIPIENT = "alsutton@jabber.com";

  /**
   * The body of the test message
   */

  private static final String TEST_MESSAGE_BODY = "Test message";

  /**
   * The stream representing the connection to ther server
   */
  private JabberStream theStream ;

  /**
   * Main method, simple instanciates the main client class.
   */

  public final static void main( String args[] )
  {
    new Java2se();
  }

  /**
   * Constructor. Sets up the stream to the server and adds this class as a listener
   */

  public Java2se()
  {
    try
    {
      theStream= new JabberStream( new seConnector( SERVER_NAME, SERVER_PORT ) );
      theStream.setJabberListener( this );
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }

  /**
   * Method to inform the server we are now online
   */

  private void sendPresence() throws IOException
  {
      Presence presence = new Presence();
      theStream.send( presence );
  }

  /**
   * Method to send a message to the specified recipient
   */

  private void sendMessage() throws IOException
  {
      Message simpleMessage = new Message( TEST_MESSAGE_RECIPIENT, TEST_MESSAGE_BODY );
      theStream.send( simpleMessage );
  }

  /**
   * Method to handle an incomming datablock.
   *
   * @param data The incomming data
   */
  public void blockArrived( JabberDataBlock data )
  {
    try
    {

      // If an IQ message has been returned that has the type equal to result
      // then we'll assume we have logged in successfully and so we'll tell the
      // server we're online, and send the initial test message.

      if( data instanceof Iq )
      {
        String result = (String) data.getAttribute ( "type" );
        if( result.equals( "result" ) )
        {
          sendPresence();
          sendMessage();
        }
      }

      // If we've received a message, we'll construct the automatic reply and send it back
      // If the message contains purely the word stop we'll end the program.

      else if( data instanceof Message )
      {
        Message message = (Message) data;
        String messageText = message.getBody();

        Message reply = message.constructReply();
        reply.setBodyText( "Message recieved:"+messageText );
        theStream.send( reply );

        if( messageText.equals( "stop" ) )
        {
          // For the stop message tell the server we are now unavailable

          Presence unavailablePresence = new Presence();

          JabberDataBlock statusBlock = new JabberDataBlock( "status", null, null );
          statusBlock.addText( "unavailable" );

          unavailablePresence.addChild( statusBlock );

          theStream.send( unavailablePresence );

          // Then close the stream
          theStream.close();
        }
      }
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }

  /**
   * Method to begin talking to the server (i.e. send a login message)
   */

  public void beginConversation()
  {
    try
    {
      Login login = new Login( USERNAME, PASSWORD, RESOURCE );
      theStream.send( login );
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }

  /**
   * If the connection is terminated then print a message
   *
   * @e The exception that caused the connection to be terminated, Note that
   *  receiving a SocketException is normal when the client closes the stream.
   */
  public void connectionTerminated( Exception e )
  {
    System.out.println( "Connection terminated" );
    if( e != null )
      e.printStackTrace();
  }
}