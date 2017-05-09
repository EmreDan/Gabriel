package org.organet.commons.gabriel;

import org.organet.commons.gabriel.Controller.Introducer;
import org.organet.commons.gabriel.Model.Connection;
import org.organet.commons.gabriel.Model.Node;
import org.organet.commons.inofy.Model.SharedFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class MainForm extends JFrame {
  private JList<String> IpListBox;
  private JButton ScanNetworkButton;
  private JButton ConnectButton;
  private JList<String> ConnectionListBox;
  private JButton listenConnection;
  private JList<String> LocalIndexListBox;

  public JList<String> getNetworkIndexListBox() {
    return NetworkIndexListBox;
  }

  public void setNetworkIndexListBox(JList<String> networkIndexListBox) {
    NetworkIndexListBox = networkIndexListBox;
  }

  private JList<String> NetworkIndexListBox;
  private JPanel panelMain;
  private JButton downloadButton;

  private DefaultListModel<String> IpListModel = new DefaultListModel<>();
  private DefaultListModel<String> ConnectionListModel = new DefaultListModel<>();
  public DefaultListModel<String> LocalIndexListModel = new DefaultListModel<>();
  private DefaultListModel<String> NetworkIndexListModel = new DefaultListModel<>();

  public DefaultListModel<String> getConnectionListModel() {
    return ConnectionListModel;
  }

  public void setConnectionListModel(DefaultListModel<String> connectionListModel) {
    ConnectionListModel = connectionListModel;
  }

  public DefaultListModel<String> getNetworkIndexListModel() {
    return NetworkIndexListModel;
  }

  // TODO Move these to App
  private Introducer introducer;

  MainForm() {
    IpListModel.setSize(10);
    IpListBox.setModel(IpListModel);
    IpListBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//    IpListBox.setMaximumSize(new Dimension(200,700));
    IpListBox.setPreferredSize(new Dimension(10,700));


   IpListBox.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    IpListBox.setVisibleRowCount(10);

    IpListBox.revalidate();
    IpListBox.repaint();

    ConnectionListBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    ConnectionListBox.setLayoutOrientation(JList.VERTICAL);
    ConnectionListBox.setModel(ConnectionListModel);

    LocalIndexListBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    LocalIndexListBox.setLayoutOrientation(JList.VERTICAL);
    LocalIndexListBox.setModel(LocalIndexListModel);

    NetworkIndexListBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    NetworkIndexListBox.setLayoutOrientation(JList.VERTICAL);
    NetworkIndexListBox.setModel(NetworkIndexListModel);

    ScanNetworkButton.addActionListener(this::ScanNetworkButtonActionPerformed);
    ConnectButton.addActionListener(this::ConnectButtonActionPerformed);
    downloadButton.addActionListener(this::downloadButtonActionPerformed);


    introducer = App.getIntroducer();

    panelMain.setPreferredSize(new Dimension(900, 650));
    panelMain.repaint();
    revalidate();

    getAndListHosts();
    setContentPane(panelMain);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    pack();

  }

  private void downloadButtonActionPerformed(ActionEvent evt) {
      ConnectionManager.downloadFile();
  }

  private void ScanNetworkButtonActionPerformed(ActionEvent evt) {
    getAndListHosts();
  }

  private void getAndListHosts() {
    IpListModel.removeAllElements();
    introducer.checkHostsBruteForce(App.SUBNET);
    //introducer.getHostCheckerAll().run();
    introducer.getHostCheckerAll().getHostIps().forEach((node) -> IpListModel.addElement(node));
  }

  private void ConnectButtonActionPerformed(ActionEvent evt) {
    String selectedIp = IpListModel.getElementAt(IpListBox.getSelectedIndex());

    // TODO If there is no selection then disable the button before here
    if (selectedIp == null) {
      JOptionPane.showMessageDialog(null, "Please choose 1 IP to connect to.");
    } else {
      try {
        String ip = selectedIp.split(" -")[0].replaceAll("/", "");
        Node selectedNode = App.getNode(ip);
        Connection newConnection = ConnectionManager.createConnection(selectedNode.getConnectionIp());

      } catch (HeadlessException ex) {
        Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
}
