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
 * Title:        Login.java
 * Description:  The class representing the login message
 *
 * Note: This has no incomming constructor as login messages are never
 * received from the server.
 */

import com.alsutton.jabber.*;

public class Login extends JabberDataBlock
{
  /**
   * The data representing the block
   */

  private String completeBlock;

  /**
   * Constructor. Builds the string ready for sending to the server.
   *
   * @param username The username to log in with
   * @param password The password to log in with
   * @param resource The resource name to use
   */

  public Login( String username, String password, String resource )
  {
    super( );

    StringBuffer blockBuffer = new StringBuffer( "<iq id=\"1001\" type=\"set\"><query xmlns=\"jabber:iq:auth\">");

    if( username != null )
    {
      blockBuffer.append( "<username>" );
      blockBuffer.append( username );
      blockBuffer.append( "</username>" );
    }

    if( password != null )
    {
      blockBuffer.append( "<password>" );
      blockBuffer.append( password );
      blockBuffer.append( "</password>" );
    }

    if( resource != null )
    {
      blockBuffer.append( "<resource>" );
      blockBuffer.append( resource );
      blockBuffer.append( "</resource>" );
    }

    blockBuffer.append( "</query></iq>" );
    completeBlock = blockBuffer.toString();
  }

  /**
   * Return this block as a byte sequence
   *
   * @return The byte array representing this block
   */

  public byte[] getBytes()
  {
    return completeBlock.getBytes();
  }

  /**
   * Return this block as a byte sequence
   *
   * @return The string representing this block
   */

  public String toString()
  {
    return completeBlock;
  }

  /**
   * Method to return the start of tag string as a string
   *
   * @return Always returns "<login>" as this block needs no parameters
   */

  public String getTagStart()
  {
    return "<login>";
  }

  /**
   * Method to return the end of tag string as a string
   *
   * @return Always returns "</login>" as this block needs no parameters
   */

  public String getTagEnd()
  {
    return "</login>";
  }

}