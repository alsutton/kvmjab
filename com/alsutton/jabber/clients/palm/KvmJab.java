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

package com.alsutton.jabber.clients.palm;

/**
 * Title:        KvmJab.java
 * Description:  Demo client using Palm Pilot classes
 */

import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;

import com.sun.kjava.*;

import java.io.*;
import java.util.*;

public class KvmJab extends Spotlet implements JabberListener, DialogOwner
{
  /**
   * Text field for the username
   */

  private static TextField tfUsername;

  /**
   * Text field for the password
   */

  private static TextField tfPassword;

  /**
   * Text field for the server name
   */

  private static TextField tfServer;

  /**
   * Text field for the server port
   */

  private static TextField tfServerPort;

  /**
   * Text field currently focused
   */

  private static TextField tfFocused;

  /**
   * The text box to display messages when they arrive
   */

  private static ScrollTextBox messageTextBox;

  /**
   * The connect button
   */

  public static Button connectButton;

  /**
   * The "prev" button
   */

  public static Button prevButton;

  /**
   * The "next" button
   */

  public static Button nextButton;

  /**
   * The quit button
   */

  public static Button quitButton;

  /**
   * The connection stream to the jabber server
   */

  private static JabberStream theStream;

  /**
   * The vector of messages available
   */

  private static Vector messageList = new Vector();

  /**
   * The graphics context
   */

  public static Graphics gc = Graphics.getGraphics();

  /**
   * The current state of the display
   */

  public static int state;

  public static int INIT_DIALOG_STATE = 0,
                    LOGIN_SCREEN_STATE = 1,
                    MESSAGE_STATE = 2;

  /**
   * The current message number being viewed
   */

  private static int messageNumber = 0;

  /**
   * The title for the initial dialog
   */

  private static final String INIT_DIALOG_TITLE = "KvmJab";

  /**
   * The main routine to start everything
   */
  public final static void main( String args[] )
  {
    new KvmJab();
  }

  /**
   * The constructor to display the initial dialog
   */

  public KvmJab()
  {
    Dialog d = new Dialog(this, INIT_DIALOG_TITLE, "KvmJab\n\n(C)Copyright 2000\nAl Sutton (al@alsutton.com).\n\nSponsored by BuddySites.net (http://www.buddysites.net/).", "OK");
    unregister();
    d.showDialog();
    state = INIT_DIALOG_STATE;
  }

  /**
   * Method to handle penMove events
   */

  public void penMove( int x, int y )
  {
    if( state == MESSAGE_STATE )
    {
      messageTextBox.handlePenMove( x, y );
    }
  }

  /**
   * Method to handle key down events
   */

  public void keyDown(int key)
  {
    if( state == LOGIN_SCREEN_STATE )
    {
      tfFocused.handleKeyDown(key);
    }
    else if( state == MESSAGE_STATE )
    {
      messageTextBox.handleKeyDown( key );
    }
  }

  /**
   * Method to handle pen down events
   */

  public void penDown(int x, int y)
  {
    if(state == LOGIN_SCREEN_STATE)
    {
      if( connectButton.pressed(x, y))
      {
        connectToServer();
      }
      else if(tfUsername.pressed(x,y) && (tfUsername != tfFocused))
      {
        tfFocused.loseFocus();
        tfUsername.setFocus();
        tfFocused = tfUsername;
      }
      else if(tfPassword.pressed(x,y) && (tfPassword != tfFocused))
      {
        tfFocused.loseFocus();
        tfPassword.setFocus();
        tfFocused = tfPassword;
      }
      else if(tfServer.pressed(x,y) && (tfServer != tfFocused))
      {
        tfFocused.loseFocus();
        tfServer.setFocus();
        tfFocused = tfServer;
      }
      else if(tfServerPort.pressed(x,y) && (tfServerPort != tfFocused))
      {
        tfFocused.loseFocus();
        tfServerPort.setFocus();
        tfFocused = tfServerPort;
      }
    }
    else if( state == MESSAGE_STATE )
    {
      messageTextBox.handlePenDown( x, y );
      if( quitButton.pressed( x, y ))
      {
        theStream.close();
      }
      else if( nextButton.pressed( x, y ) )
      {
        gc.clearScreen();
        messageNumber++;
        String messageText = (String) messageList.elementAt( messageNumber );
        messageTextBox.setText( messageText );
        paint();
      }
      else if( prevButton.pressed( x, y ) )
      {
        gc.clearScreen();
        messageNumber--;
        String messageText = (String) messageList.elementAt( messageNumber );
        messageTextBox.setText( messageText );
        paint();
      }
    }
  }

  /**
   * The method to handle the dismissing of dialogs. Only used when the initial
   * dialog is dismissed.
   */

  public void dialogDismissed(String title)
  {
    if( title.equals(INIT_DIALOG_TITLE) )
    {
      register(NO_EVENT_OPTIONS);
      state = LOGIN_SCREEN_STATE;
      gc.clearScreen();
      tfUsername = new TextField("Username", 5, 10, 130, 20);
      tfPassword = new TextField("Password", 5, 30, 130, 20);
      tfServer = new TextField("Server", 5, 50, 130, 20);
      tfServerPort = new TextField("Port Number", 5, 70, 130, 20);
      tfServerPort.setText( "5222" );
      connectButton = new Button("Connect",115,145);
      tfUsername.setFocus();
      tfFocused = tfUsername;
      paint();
    }
  }

  /**
   * Method to paint the display
   */

  public void paint()
  {
    if(state == LOGIN_SCREEN_STATE)
    {
      connectButton.paint();
      tfUsername.paint();
      tfPassword.paint();
      tfServer.paint();
      tfServerPort.paint();
    }
    else if( state == MESSAGE_STATE )
    {
      messageTextBox.paint();
      if( messageNumber < messageList.size()-1 )
        nextButton.paint();
      if( messageNumber > 0 )
        prevButton.paint();
      quitButton.paint();
    }
  }

  /**
   * Method to connect to the jabber server and prepare the message
   * display screen
   */

  private void connectToServer()
  {
    try
    {
      tfFocused.loseFocus();
      String serverName = tfServer.getText();
      String portNumberString = tfServerPort.getText();
      int portNumber = Integer.parseInt( portNumberString );

      ConnectorInterface connector = new meConnector( serverName, portNumber );
      theStream= new JabberStream( connector );
      theStream.setJabberListener( this );
      state = MESSAGE_STATE;
      gc.clearScreen();
      messageTextBox = new ScrollTextBox("Waiting for messages....", 5, 10, 130, 130 );
      prevButton = new Button("Prev",5,145);
      nextButton = new Button("Next",70,145);
      quitButton = new Button("Quit",115,145);
      paint();
    }
    catch( Exception e )
    {
      e.printStackTrace();
      System.exit( 0 );
    }
  }

  /**
   * Method to send the on-line presence message to the server
   */

  private void sendPresence() throws IOException
  {
      Presence presence = new Presence();
      theStream.send( presence );
  }

  /**
   * Method to handle incomming blocks
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
        }
      }
      if( data instanceof Message )
      {
        Message message = (Message) data;
        String messageText = message.getBody();

        Message reply = message.constructReply();
        reply.setBodyText( "Message recieved:"+messageText );
        theStream.send( reply );

        String from = message.getAttribute( "from" );
        String messageBody = message.getBody();

        StringBuffer messageStringBuffer = new StringBuffer( "From: " );
        messageStringBuffer.append( from );
        messageStringBuffer.append( "\n\n* Message Text *\n\n" );
        if( messageBody != null )
          messageStringBuffer.append( messageBody );
        else
          messageStringBuffer.append( "<<MESSAGE WAS EMPTY>>" );

        messageList.addElement( messageStringBuffer.toString() );
        messageNumber = messageList.size()-1;
        messageTextBox.setText( messageStringBuffer.toString() );
        paint();
      }
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }

  /**
   * Method called once a connection to the server has been established
   */

  public void beginConversation()
  {
    try
    {
      String username = tfUsername.getText();
      String password = tfPassword.getText();
      Login login = new Login( username, password, "kvmjab" );
      theStream.send( login );
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }

  /**
   * Method called when the connection to the server is terminated
   *
   * @param e Any exception thrown (possibly null).
   */
  public void connectionTerminated( Exception e )
  {
    System.exit(0);
  }
}