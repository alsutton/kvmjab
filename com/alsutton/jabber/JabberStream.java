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

package com.alsutton.jabber;

/**
 * Title:        JabberStream.java
 * Description:  The stream to a jabber server
 */

import java.io.*;
import java.util.*;

import com.alsutton.jabber.datablocks.*;

import com.alsutton.xmlparser.*;

public class JabberStream implements XMLEventListener, Runnable
{
  /**
   * The Output stream to the server
   */

  private OutputStream outStream;

  /**
   * The input stream from the server
   */

  private InputStream inpStream;

  /**
   * The dispatcher thread
   */

  private JabberDataBlockDispatcher dispatcher;

  /**
   * Constructor
   *
   * @param connectorInterface The connector which establishes the socket for the connection
   */
  public JabberStream( ConnectorInterface connectorInterface ) throws IOException
  {
    outStream = connectorInterface.openOutputStream();
    inpStream = connectorInterface.openInputStream();

    StringBuffer streamInitiator = new StringBuffer( "<stream:stream to=\"" );
    String hostname = connectorInterface.getHostname();
    streamInitiator.append( hostname );
    streamInitiator.append( "\" xmlns=\"jabber:client\" xmlns:stream=\"http://etherx.jabber.org/streams\">" );
    outStream.write( streamInitiator.toString().getBytes() );
    outStream.flush();

    dispatcher = new JabberDataBlockDispatcher();

    Thread newThread = new Thread( this );
    newThread.start();
  }

  /**
   * The threads run method. Handles the parsing of incomming data in its own thread.
   */

  public void run()
  {
    try
    {
      InputStreamReader inSource = new InputStreamReader( inpStream );
      XMLParser parser = new XMLParser( this );
      parser.parse( inSource );
      dispatcher.broadcastTerminatedConnection( null );
    }
    catch( Exception e )
    {
      dispatcher.broadcastTerminatedConnection(e);
    }

  }

  /**
   * Method to close the stream
   */

  public void close()
  {
    dispatcher.setJabberListener( null );
    try
    {
      send( "</stream:stream>" );
      inpStream.close();
      outStream.close();
    }
    catch( IOException e )
    {
    }
    finally
    {
      dispatcher.halt();
    }
  }

  /**
   * Method of sending data to the server
   *
   * @param data The data to send
   */

  public void send( byte[] data ) throws IOException
  {
    outStream.write( data );
    outStream.flush();
  }

  /**
   * Method of sending data to the server
   *
   * @param The data to send to the server
   */

  public void send( String data ) throws IOException
  {
    send( data.getBytes() );
  }

  /**
   * Method of sending a Jabber datablock to the server
   *
   * @param block The data block to send to the server
   */

  public void send( JabberDataBlock block ) throws IOException
  {
    send( block.getBytes() );
  }

  /**
   * Set the listener to this stream
   */

  public void setJabberListener( JabberListener listener )
  {
    dispatcher.setJabberListener( listener );
  }

  /**
   * The current class being constructed
   */

  private JabberDataBlock currentBlock;

  /**
   * Method called when an XML tag is started
   *
   * @param name Tag name
   * @param attributes The tags attributes
   */
  public void tagStarted( String name, Hashtable attributes )
  {
    if ( name.equals( "stream:stream" ) )
      dispatcher.broadcastBeginConversation();
    else if ( name.equals( "message" ) )
      currentBlock = new Message( currentBlock, attributes );
    else if ( name.equals("iq") )
      currentBlock = new Iq( currentBlock, attributes );
    else
      currentBlock = new JabberDataBlock( name, currentBlock, attributes );
  }

  /**
   * Method called when some plain text is encountered
   *
   * @param text The plain text in question
   */
  public void plaintextEncountered( String text )
  {
    if( currentBlock != null )
    {
      currentBlock.addText( text );
    }
  }

  /**
   *  The method called when a tag is ended
   *
   * @param name The name of the tag that has just ended
   */
  public void tagEnded( String name )
  {
    if( currentBlock == null )
      return;

    JabberDataBlock parent = currentBlock.getParent();
    if( parent == null )
      dispatcher.broadcastJabberDataBlock( currentBlock );
    else
      parent.addChild( currentBlock);
    currentBlock = parent;
  }
}