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

/**
 *
 * @author alberttriadrian
 */
public class UDPClient {
    private InetAddress IPAddress;
    private int port;
    private InetAddress targetIPAddress;
    private int targetPort;
    private DatagramSocket datagramSocket;
    
    public UDPClient (String _IPAddress, int _port) throws UnknownHostException {
        this.port = _port;
        this.IPAddress = InetAddress.getByName(_IPAddress);
        this.targetPort = _port;
        this.targetIPAddress = InetAddress.getByName(_IPAddress);
    }
    
    public void setTargetIPAddress (String _IPAddress) throws UnknownHostException {
        this.targetIPAddress = InetAddress.getByName(_IPAddress);
    }
    
    public void setTargetPort (int _port) {
        this.targetPort = _port;
    }
    
    public void start () throws SocketException, IOException {
        this.datagramSocket = new DatagramSocket();

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        UnreliableSender unreliableSender = new UnreliableSender(this.datagramSocket);

        while (true)
        {
            String sentence = inFromUser.readLine();
            if (sentence.equals("quit"))
            {
                    break;
            }

            this.send(sentence, targetIPAddress, targetPort, unreliableSender);
        }
        this.datagramSocket.close();
    }
    
    public void send (String sentence, InetAddress targetAddress, int targetPort, UnreliableSender unreliableSender) throws IOException {
        byte[] sendData = sentence.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, targetAddress, targetPort);
        unreliableSender.send(sendPacket);        
    }
    
    public void receive (int listenPort) throws IOException {
        DatagramSocket serverSocket = new DatagramSocket(listenPort);
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
