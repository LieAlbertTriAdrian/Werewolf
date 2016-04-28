/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author alberttriadrian
 */
public class Server {
    private int listenPort;
    private ServerSocket serverSocket;
    private Socket connectionSocket;
    
    public Server (int port) throws IOException {
        this.listenPort = port;
        serverSocket = new ServerSocket(listenPort);
    }
    
    public void start () throws IOException {
        connectionSocket = serverSocket.accept();
        while(true) {             
            String sentence = this.receive();
            this.send(sentence.toUpperCase());
        }  
    }

    public void send (String sentence) throws IOException {
        DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());             
        outToClient.writeBytes(sentence);
    }
    
    public String receive () throws IOException {
        BufferedReader inFromClient =  new BufferedReader(new InputStreamReader(this.connectionSocket.getInputStream()));
        String response = inFromClient.readLine();
        System.out.println("Received: " + response);
        return response;
    }
}
