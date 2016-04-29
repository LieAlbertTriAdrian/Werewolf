/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Client.Client;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author alberttriadrian
 */
public class Server {
    private int listenPort;
    private ServerSocket serverSocket;
    private Socket connectionSocket;
    private ArrayList<Client> Clients;
    
    public Server (int port) throws IOException {
        this.listenPort = port;
        this.serverSocket = new ServerSocket(listenPort);
        this.Clients = new ArrayList<Client>();
    }

    public void addClient (Client client) {
        Clients.add(client);
    }
    
    public void start () throws IOException, ParseException {
        while(true) {             
            connectionSocket = serverSocket.accept();
            System.out.println("Server looping");
            JSONObject jsonRequest = this.receive();
            JSONObject jsonResponse = new JSONObject();
            String method = jsonRequest.get("method").toString();
            if(method.equals("leave")){
                jsonResponse.put("status", "ok");
            } else {
                jsonResponse.put("status", "wrong request");
            }
            this.send(jsonResponse);
        }
    }

    public void send (JSONObject jsonResponse) throws IOException {
        DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());             
        outToClient.writeBytes(jsonResponse.toString() + "\n");
    }
    
    public JSONObject receive () throws IOException, ParseException {
        BufferedReader inFromClient =  new BufferedReader(new InputStreamReader(this.connectionSocket.getInputStream()));
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(inFromClient.readLine());
        JSONObject jsonRequest = (JSONObject) obj;
        System.out.println("Before Return");
        return jsonRequest;
    }
    
    public ArrayList<Client> getClients () {
        return this.Clients;
    }
    
}
