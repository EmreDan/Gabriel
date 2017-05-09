package org.organet.commons.gabriel;

import org.organet.commons.gabriel.Model.Connection;
import org.organet.commons.inofy.Index;
import org.organet.commons.inofy.Model.SharedFileHeader;

import java.io.*;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionManager {
  private static ArrayList<Connection> connections = new ArrayList<>();
  private final static Integer PORT_NO = 5001;

  static Index networkIndex = new Index(false);
  Map<String, Index> remoteIndeces = new HashMap<>(); // TODO Emre will implement this
  //  connectionId, (de-serialized) remote Index class

  public Integer getPORT_NO() {
    return PORT_NO;
  }

  public static void startServer() {
    try {
      //Listening for a connection to be made
      System.out.println("server started");
      ServerSocket serverSocket = new ServerSocket(PORT_NO);
      System.out.println("TCPServer Waiting for client on port " + PORT_NO);
      while (true) {
        Socket neighbourConnectionSocket = serverSocket.accept();

        Connection newIncomingConnection = new Connection(neighbourConnectionSocket);

        Runnable r = new Runnable() {
          @Override
          public void run() {
            getRemoteIndex(newIncomingConnection);
          }
        };
        r.run();
        Runnable r2 = new Runnable() {
          @Override
          public void run() {
            sendIndex(newIncomingConnection, App.localIndex);
          }
        };
        r2.run();

        connections.add(newIncomingConnection);

        App.mainForm.getConnectionListModel().addElement(newIncomingConnection.getConnectionIp().toString());
      }
    } catch (IOException ex) {
      System.out.println("Input Output Exception on Listen Connection Action Performed");
      Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
    }
  }


  public static void sendIndex(Connection connection, Index myIndex) {
    ObjectOutputStream objectOS = null;
    try {
      objectOS = new ObjectOutputStream(new BufferedOutputStream(connection.getOutputStream()));
      objectOS.writeObject(myIndex);
      objectOS.flush();

    } catch (IOException e) {
      e.printStackTrace();
    }
//        objectOS.close();

  }

  public static void getRemoteIndex(Connection conn) {
    try {
      ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(conn.getInputStream()));
      Index remoteIndex = new Index(false);
      boolean flag = true;
      while (flag) {
        try {
          remoteIndex = (Index) in.readObject();
          flag = false;
        } catch (EOFException ex) {
          flag = true;
        }
      }
      System.out.println("Index read successfully: " + remoteIndex.toString());
      conn.setConnectionIndex(remoteIndex);
      networkIndex.addAllSharedFiles(remoteIndex);
      for (SharedFileHeader sh : networkIndex.getSharedFileHeaders()) {
        conn.getConnectionIndex().add(sh);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }



  public static void broadcastLocalIndex() {
    //TODO send local index to all connections
  }


  public Index getNetworkIndex() {
    return networkIndex;
  }

  public void setNetworkIndex(Index networkIndex) {
    this.networkIndex = networkIndex;
  }

  public static Connection createConnection(Inet4Address connectionIp) {
    Connection newConnection = null;
    try {

      newConnection = new Connection(new Socket(connectionIp.getHostAddress(), PORT_NO));

      sendIndex(newConnection, App.localIndex);

      getRemoteIndex(newConnection);

      connections.add(newConnection);
      App.mainForm.getConnectionListModel().addElement(newConnection.getConnectionIp().toString());
      return newConnection;
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Couldn't create connection");
      return null;
    }
  }

  public static void downloadFile() {
    String selectedFileScreenName = App.mainForm.getNetworkIndexListBox().getSelectedValue();

    if (selectedFileScreenName == null) {
      System.out.println("Error file not specified");
      return;
    }

    SharedFileHeader networkSharedFileHeader = networkIndex.findIndex(selectedFileScreenName);

    System.out.println(networkSharedFileHeader.toString());

    //connections.get(0).requestFile(networkSharedFileHeader.getNDNID());


  }
}
