package UDP;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Sender.UnreliableSender;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alberttriadrian
 */
public class UDPClient extends Thread{
    private InetAddress IPAddress;
    private int port;
    private InetAddress targetIPAddress;
    private int targetPort;
    private DatagramSocket datagramSocket;
    private Runnable sender;
    private Runnable receiver;
    
    public UDPClient (String _IPAddress, int _port) throws UnknownHostException, SocketException {
        this.port = _port;
        this.IPAddress = InetAddress.getByName(_IPAddress);
        this.targetPort = _port;
        this.targetIPAddress = InetAddress.getByName(_IPAddress);
        this.datagramSocket = new DatagramSocket();
//        this.datagramSocket = new DatagramSocket();
//        DatagramSocket serverSocket = new DatagramSocket(port);
//        sender = new Runnable(){
//            public void run(){
//                try {
//                    call();
//                } catch (IOException ex) {
//                    Logger.getLogger(UDPClient.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//            public void call() throws SocketException, IOException{
//                datagramSocket = new DatagramSocket();
//                DatagramSocket serverSocket = new DatagramSocket(port-1);
//                byte[] receiveData = new byte[1024];
//
//                BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
//                UnreliableSender unreliableSender = new UnreliableSender(datagramSocket);
//
//                while (true)
//               {    
//                    String sentence = inFromUser.readLine();
//                    String target = sentence.split(" ")[0];
//                    if (target.equals("toOthers")){
//                        if (sentence.split(" ")[1].equals("quit"))
//                        {
//                                break;
//                        }
//
//                        send(sentence, targetIPAddress, targetPort, unreliableSender);
//                    }
//                }
//                datagramSocket.close();
//            }
//        };
//        
        receiver = new Runnable(){
            public void run(){
                try {
                    receive();
                } catch (IOException ex) {
                    Logger.getLogger(UDPClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            public void receive() throws IOException{
               DatagramSocket serverSocket = new DatagramSocket(port);
                byte[] receiveData = new byte[1024];

                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);

                    String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("RECEIVED: " + sentence);

                }     
           } 
        };
    }
    
    public void setTargetIPAddress (String _IPAddress) throws UnknownHostException {
        this.targetIPAddress = InetAddress.getByName(_IPAddress);
    }
    
    public void setTargetPort (int _port) {
        this.targetPort = _port;
    }
    
    public void run () {
        new Thread(receiver).start();
//        new Thread(sender).start();
    }
    
    public void call (String sentence) throws IOException {
//        this.datagramSocket = new DatagramSocket();
//        DatagramSocket serverSocket = new DatagramSocket(targetPort);
//        byte[] receiveData = new byte[1024];
//
////        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        UnreliableSender unreliableSender = new UnreliableSender(this.datagramSocket);

//        while (true)
//        {
//            sentence = inFromUser.readLine();
//            String target = sentence.split(" ")[0];
//            if (target.equals("toOthers")){
                if (sentence.equals("quit"))
                {
//                        break;
                    this.datagramSocket.close();
                }

                this.send(sentence, targetIPAddress, targetPort, unreliableSender);
//            }
//        }
//        this.datagramSocket.close();
    }
    
    public void send (String sentence, InetAddress targetAddress, int targetPort, UnreliableSender unreliableSender) throws IOException {
        byte[] sendData = sentence.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, targetAddress, targetPort);
        unreliableSender.send(sendPacket);        
    }
    
    public void receive () throws IOException {
        DatagramSocket serverSocket = new DatagramSocket(port);
        byte[] receiveData = new byte[1024];
        
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);

            String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("RECEIVED: " + sentence);
        
        }       
    }
    
    public void close () {
        this.datagramSocket.close();
    }

//    public static void main(String args[]) throws Exception
//    {
//        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
//
//        String targetAddress = "localhost";
//        InetAddress IPAddress = InetAddress.getByName(targetAddress);
//        int targetPort = 9876;
//
//        DatagramSocket datagramSocket = new DatagramSocket();
//        UnreliableSender unreliableSender = new UnreliableSender(datagramSocket);
//
//        while (true)
//        {
//                String sentence = inFromUser.readLine();
//                if (sentence.equals("quit"))
//                {
//                        break;
//                }
//
//                byte[] sendData = sentence.getBytes();
//                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, targetPort);
//                unreliableSender.send(sendPacket);
//        }
//        datagramSocket.close();
//    }
    
}
