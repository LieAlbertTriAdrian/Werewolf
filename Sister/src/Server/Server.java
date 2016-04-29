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
    
    public Server (int port) throws IOException {
        this.listenPort = port;
        serverSocket = new ServerSocket(listenPort);
    }
    
    public void start () throws IOException, ParseException {
        while(true) {             
            connectionSocket = serverSocket.accept();
            JSONObject jsonRequest = receive();
            JSONObject jsonResponse = new JSONObject();
            System.out.println(jsonRequest);
            String method = (String) jsonRequest.get("method");
            if (method.equals("join")){
                String username = (String) jsonRequest.get("username");
                jsonResponse.put("status","ok");
                jsonResponse.put("player_id","3");
            } else if(method.equals("leave")){
                jsonResponse.put("status", "ok");
            } else if (method.equals("ready")){
                jsonResponse.put("status","ok");
                jsonResponse.put("description","waiting for other player to start");
            } else if (method.equals("client_address")){
                JSONObject clients = new JSONObject();
                clients.put("player_id","0");
                clients.put("is_alive","1");
                clients.put("address","192.168.1.1");
                clients.put("port","9999");
                clients.put("username","sister");
                jsonResponse.put("status","ok");
                jsonResponse.put("clients",clients);
            } else {
                jsonResponse.put("status", "wrong request");
            }
            send(jsonResponse);
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
        return jsonRequest;
    }
}
