/*
  Copyright (c) 2000,2001 Al Sutton (al@alsutton.com)
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
 * Class to allow paging messages to be sent via a jabber server.
 */

import java.io.*;
import java.util.*;

import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;

public class Pager implements JabberListener
{
  /**
   * The name of the jabber server to connect to.
   */

  private static final String JABBER_SERVER = "jabber.com";

  /**
   * The port on which the jabber server runs on the machine named in
   * JABBER_SERVER.
   */

  private static final int JABBER_SERVER_PORT = 5222;

  /**
   * The username to log into the jabber server with.
   */

  private static final String JABBER_USERID = "personalbuddy";

  /**
   * The password to log into the jabber server with.
   */

  private static final String JABBER_PASSWORD = "557235v";

  /**
   * The resource name to present to the server.
   */

  private static final String JABBER_RESOURCE = "AlSutton.com-Pager";

  /**
   * The stream to the jabber server
   */

  private JabberStream jabberStream = null;

  /**
   * The single instance that all other objects should use.
   */

  private static Pager instance = null;

  /**
   * Flag to say whether or not we are connected to the server, and have been
   * authenticated.
   */

  private boolean connectionOpen;

  /**
   * Method to create a new instance.
   */

  private static synchronized void createInstance()
  {
    if( instance != null )
      return;

    instance = new Pager();
  }

  /**
   * Method to get the instance.
   */

  public static Pager getInstance()
  {
    if( instance == null )
      createInstance();

    return instance;
  }

  /**
   * Constructor. Sets up connection to the jabber server
   */

   public Pager()
   {
        connectToJabberServer();
   }

  /**
   * Send a message to another user.
   *
   * @param to The user to send the message to.
   * @param message The message to send.
   */

  public void send( String to, String message )
    throws IOException
  {
    // This next section could be written by waiting for a monitor on
    // an object, it is written this way for clarity.

    // This code checks if the connection to the server is available.
    // It checks every 10 seconds, and if after 10 checks the connection is
    // still not open it tries to re-establish the connection. If after
    // 10 restablish attempts it fails then it gives up.

    int reconnectAttempts = 10;
    while( connectionOpen == false )
    {
      int waitAttempts = 10;
      while( connectionOpen == false && waitAttempts > 0 )
      {
        try
        {
          Thread.sleep( 10000 );
        }
        catch( InterruptedException e )
        {
        }
        waitAttempts--;
      }
      if( connectionOpen == false )
      {
        connectToJabberServer();
      }
      reconnectAttempts--;
    }

    if( connectionOpen == false )
      throw new IOException( "Jabber Server unavailable" );

    try
    {
      if( jabberStream == null )
        connectToJabberServer();

      if( to == null || to.length() == 0 )
        throw new RuntimeException( "No recipient has been specified" );

      if( message == null || message.length() == 0 )
        throw new RuntimeException( "No message text has been entered" );

      Message outgoingMessage = new Message( to, message );
      jabberStream.send( outgoingMessage );
    }
    catch( Exception e )
    {
      if( jabberStream != null )
      {
        jabberStream.close();
        jabberStream = null;
      }
    }
  }

  /**
   * Method to connect to the jabber server
   */

  public synchronized void connectToJabberServer()
  {
    connectionOpen = false;
    if( jabberStream != null )
    {
      jabberStream.close();
    }

    jabberStream = null;

    try
    {
      ConnectorInterface connector = new seConnector( JABBER_SERVER, JABBER_SERVER_PORT );
      jabberStream= new JabberStream( connector );
      jabberStream.setJabberListener( this );
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }

  /**
   * Method to respond to the arrival of Jabber data block
   *
   * @param data The data block comming from the server
   */

  public void blockArrived( JabberDataBlock data )
  {
    try
    {
      if( data instanceof Iq )
      {
        String result = (String) data.getAttribute ( "type" );
        if( result.equals( "result" ) )
        {
          sendPresence();
          connectionOpen = true;
        }
      }
      if( data instanceof Message )
      {
        Message message = (Message) data;
        Message reply = message.constructReply();
        reply.setBodyText( "I'm sorrry, this Jabber ID is used by an "+
                           "automated system that does not accept messages." );
        jabberStream.send( reply );
      }
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }

  /**
   * Method to being the conversation with the
   */

  public void beginConversation()
  {
    try
    {
      Login login = new Login( JABBER_USERID, JABBER_PASSWORD, JABBER_RESOURCE );
      jabberStream.send( login );
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }

  /**
   * Method to handle the termination of a connection.
   *
   * @param e The exception that caused the connection to be terminated.
   */

  public void connectionTerminated( Exception e )
  {
    connectToJabberServer();
  }

  /**
   * Method to inform the jabber server that we are on-line.
   */

  private void sendPresence()
    throws IOException
  {
    Presence presence = new Presence();
    jabberStream.send( presence );
  }

  /**
   * The main method, executed by the user from the command line.
   * <p>
   * The execution should take the form;<br>
   * java com.alsutton.jabber.examples.Pager send_to_user message
   */

  public static void main (String args[])
  {
    System.out.println( "KVMjab Pager example" );
    System.out.println( "(c)Copyright 2000,2001 Al Sutton, See source code for licence details." );
    System.out.println( );

    if( args.length != 2 )
    {
      System.out.println( "Usage: java com.alsutton.jabber.examples.Pager send_to_user message" );
      System.exit( -1 );
    }

    System.out.print( "Sending message to " + args[0] +"..." );

    try
    {
      Pager testPager = Pager.getInstance();
      testPager.send( args[0], args[1] );
      System.out.println( "Message sent" );
    }
    catch( Exception e )
    {
      System.out.println( "Sending failed." );
      e.printStackTrace();
    }
  }
}