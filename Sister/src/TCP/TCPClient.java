/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TCP;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author alberttriadrian
 */
public class TCPClient {
    private String IPAddress;
    private int port;
    private Socket socket;
    
    public TCPClient (String _IPAddress, int _port) throws IOException {
        this.IPAddress = _IPAddress;
        this.port = _port;
        this.socket = new Socket(this.IPAddress, this.port); 
    }
    
    public void start () throws IOException {
        BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));   
        
        String sentence = inFromUser.readLine();
        this.send(sentence);
        String modifiedSentence = this.receive();
        System.out.println("FROM SERVER: " + modifiedSentence);   
    }
    
    public void send (String sentence) throws IOException {
        DataOutputStream outToServer = new DataOutputStream(this.socket.getOutputStream());   
        outToServer.writeBytes(sentence + "\n");        
    }
    
    public String receive () throws IOException {
        System.out.println("Masuk Stream");
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));   
        String response = inFromServer.readLine();
        System.out.println("Debug : " + response);
        return response;
    }

    public void close() throws IOException {
        this.socket.close();
    }
    
    public static void main(String argv[]) throws Exception {   
            String sentence;   
            String modifiedSentence;    
            BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));   
            Socket clientSocket = new Socket("localhost", 6789);   
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());   
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));   
            sentence = inFromUser.readLine();   outToServer.writeBytes(sentence + '\n');   
            modifiedSentence = inFromServer.readLine();   
            System.out.println("FROM SERVER: " + modifiedSentence);   
            clientSocket.close();  
    } 
}
