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

package com.alsutton.jabber.datablocks;

/**
 * Title:        Message.java
 * Description:  The class representing a Jabber message object
 */

import com.alsutton.jabber.*;

import java.util.*;

public class Message extends JabberDataBlock
{
  /**
   * Constructor. Prepares the message destination and body
   *
   * @param to The destination of the message
   * @param message The message text
   */

  public Message( String to, String message )
  {
    super();

    setAttribute( "to", to );
    if( message != null )
      setBodyText( message );
  }

  /**
   * Constructor. Prepares the message destination
   *
   * @param to The destination of the message
   */

  public Message( String to )
  {
    this( to, null );
  }

  /**
   * Constructor for incomming messages
   *
   * @param _parent The parent of this datablock
   * @param _attributes The list of element attributes
   */

  public Message( JabberDataBlock _parent, Hashtable _attributes )
  {
    super( _parent, _attributes );
  }

  /**
   * Method to set the body text. Creates a block with body as it's tag name
   * and inserts the text into it.
   *
   * @param bodyText The string to go in the message body
   */

  public void setBodyText( String text )
  {
    JabberDataBlock body = new JabberDataBlock( "body", null, null );
    body.addText( text );
    addChild( body );
  }

  /**
   * Method to set the subject text. Creates a subject block and inserts the text into it.
   *
   * @param text The string to go in the message subject
   */

  public void setSubject( String text )
  {
    JabberDataBlock subject = new JabberDataBlock( "subject", null, null );
    subject.addText( text );
    addChild( subject );
  }

  /**
   * Method to return the text for a given child block
   */

  private String getTextForChildBlock( String blockname )
  {
    if( childBlocks == null )
      return "";

    for( int i = 0 ; i < childBlocks.size() ; i++ )
    {
      JabberDataBlock thisBlock = (JabberDataBlock) childBlocks.elementAt( i );
      if( thisBlock.getTagName().equals( blockname ) )
      {
        return thisBlock.getText();
      }
    }

    return "";
  }

  /**
   * Method to get the message subject
   *
   * @return A string representing the message subject
   */

  public String getSubject()
  {
    return getTextForChildBlock( "subject" );
  }

  /**
   * Method to get the message body
   *
   * @return The message body as a string
   */

  public String getBody()
  {
    return getTextForChildBlock( "body" );
  }

  /**
   * Construct a reply message
   *
   * @return A message object destined for the sender of this message with no subject or body
   */

  public Message constructReply()
  {
    if( attributes == null )
      return null;

    String to = (String) attributes.get( "from" );
    if( to == null )
      return null;

    Message reply = new Message( to );

    String from = (String) attributes.get( "to" );
    if( from != null )
      reply.setAttribute( "from", from );

    String messageType = getAttribute( "type" );
    reply.setAttribute( "type", messageType );

    String thread = getTextForChildBlock( "thread" );
    if( thread.length() > 0 )
    {
        JabberDataBlock threadBlock = new JabberDataBlock( "thread", null, null );
        threadBlock.addText( thread );
        reply.addChild( threadBlock );
    }

    return reply;
  }

  /**
   * Get the tag start marker
   *
   * @return The block start tag
   */

  public String getTagName()
  {
    return "message";
  }
}