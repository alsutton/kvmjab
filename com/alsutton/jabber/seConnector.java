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
 * Title:        seConnector.java
 * Description:  Class for getting a connection to the server for j2se
 */

import java.io.*;
import java.net.*;

public class seConnector extends ConnectorInterface
{
  /**
   * The socket connected to the Jabber server
   */

  private Socket serverConnection;

  /**
   * Constructor
   *
   * @param hostname The host to connect to
   * @param port The port to connect to
   */

  public seConnector( String hostname, int port ) throws IOException
  {
    super( hostname );
    serverConnection = new Socket( hostname, port );
  }

  /**
   * Method to return the input stream of the connection
   *
   * @return The input stream
   */

  public InputStream openInputStream() throws IOException
  {
    return serverConnection.getInputStream();
  }

  /**
   * Method to return the output stream of the connection
   *
   * @return The output stream
   */

  public OutputStream openOutputStream()  throws IOException
  {
    return serverConnection.getOutputStream();
  }
}