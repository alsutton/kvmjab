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

package com.alsutton.jabber;

/**
 * The dispatcher for blocks that have arrived. Adds new blocks to the
 * dispatch queue, and then dispatches waiting blocks in their own thread to
 * avoid holding up the stream reader.
 */

import java.io.*;
import java.util.*;

public class JabberDataBlockDispatcher extends Thread
{
  /**
   * The recipient waiting on this stream
   */

  private JabberListener listener = null;

  /**
   * The list of messages waiting to be dispatched
   */

  private Vector waitingQueue = new Vector();

  /**
   * Flag to watch the dispatching loop
   */

  private boolean dispatcherActive;

  /**
   * Constructor to start the dispatcher in a thread.
   */

  public JabberDataBlockDispatcher()
  {
    start();
  }

  /**
   * Set the listener that we are dispatching to. Allows for switching
   * of clients in mid stream.
   *
   * @param _listener The listener to dispatch to.
   */

  public void setJabberListener( JabberListener _listener )
  {
    listener = _listener;
  }

  /**
   * Method to add a datablock to the dispatch queue
   *
   * @param datablock The block to add
   */

  public void broadcastJabberDataBlock( JabberDataBlock dataBlock )
  {
    waitingQueue.addElement( dataBlock );
  }

  /**
   * The thread loop that handles dispatching any waiting datablocks
   */

  public void run()
  {
    dispatcherActive = true;
    while( dispatcherActive )
    {
      while( waitingQueue.size() == 0 )
      {
        try
        {
          Thread.sleep( 100L );
        }
        catch( InterruptedException e )
        {
        }
      }

      JabberDataBlock dataBlock = (JabberDataBlock) waitingQueue.elementAt(0);
      waitingQueue.removeElementAt( 0 );
      if( listener != null )
        listener.blockArrived( dataBlock );
    }
  }

  /**
   * Method to stop the dispatcher
   */

  public void halt()
  {
    dispatcherActive = false;
  }

  /**
   * Method to tell the listener the connection has been terminated
   *
   * @param exception The exception that caused the termination. This may be
   * null for the situtations where the connection has terminated without an
   * exception.
   */

  public void broadcastTerminatedConnection( Exception exception )
  {
    halt();
    if( listener != null )
      listener.connectionTerminated( exception );
  }

  /**
   * Method to tell the listener the stream is ready for talking to.
   */

  public void broadcastBeginConversation( )
  {
    if( listener != null )
      listener.beginConversation();
  }
}