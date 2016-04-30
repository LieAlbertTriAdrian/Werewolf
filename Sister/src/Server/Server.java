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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
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
    private ArrayList<JSONObject> Clients;
    private Runnable receiver;
    private String round;
    private int day;
    private boolean isGameRunning;
    
    public Server (int port) throws IOException {
        this.listenPort = port;
        this.serverSocket = new ServerSocket(listenPort);
        this.Clients = new ArrayList<JSONObject>();
        this.isGameRunning = false;
        receiver = new Runnable() {
            public void run(){
                try {
                    call();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            public void call() throws IOException, ParseException {
                while(true) {             
                    JSONObject jsonRequest = receive();
                    JSONObject jsonResponse = new JSONObject();
                    System.out.println(jsonRequest);
                    String method = (String) jsonRequest.get("method");
                    System.out.println("Method : " + method);
                    if (method.equals("join"))
                        jsonResponse = joinGameResponse(jsonRequest);
                    else if(method.equals("leave"))
                        jsonResponse = leaveGameResponse(jsonRequest);
                    else if (method.equals("ready"))
                        jsonResponse = readyUpResponse(jsonRequest);
                    else if (method.equals("client_address"))
                        jsonResponse = listClient(jsonRequest);
                    else {
                        jsonResponse.put("status", "wrong request");
                    }
                    send(jsonResponse);
                }
            }
        };
    }

    public void addClient (JSONObject client) {
        Clients.add(client);
    }
    
    public void start () throws IOException{
        connectionSocket = serverSocket.accept();
        new Thread(receiver).start();
    }

    public void send (JSONObject jsonResponse) throws IOException {
        DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());             
        outToClient.writeBytes(jsonResponse.toString() + "\n");
    }
    
    public JSONObject receive () throws IOException, ParseException {
        System.out.println("Masuk Receive");
        BufferedReader inFromClient =  new BufferedReader(new InputStreamReader(this.connectionSocket.getInputStream()));
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(inFromClient.readLine());
        JSONObject jsonRequest = (JSONObject) obj;
        return jsonRequest;
    }
    
    public ArrayList<JSONObject> getClients () {
        return this.Clients;
    }

    /******************** Protocol Function ********************/
    public JSONObject joinGameResponse (JSONObject request) {
        JSONObject jsonResponse = new JSONObject();        
        String status;
        
        /* Error Handling */        
        if (request.has("username") && request.has("method") && request.has("udp_address") && request.has("udp_port")) {
            /* Failure Handling */            
            if ( isUsernameExist(request.get("username").toString()) ) {
                status = "fail";
                String message = "user exists";
                jsonResponse.put("status", status);
                jsonResponse.put("description", message);
            } else if ( this.isGameRunning ) {
                status = "fail";
                String message = "please wait, game is currently running";
                jsonResponse.put("status", status);
                jsonResponse.put("description", message);
            } else {
                status = "ok";            
                jsonResponse.put("status", status);
                jsonResponse.put("player_id", this.getClients().size());
                addClient(request);
            }
        } else {
            status = "error";
            String message = "wrong request";
            jsonResponse.put("status", status);
            jsonResponse.put("description", message);
        }
        return jsonResponse;
    }

    public JSONObject leaveGameResponse (JSONObject request) {
        JSONObject jsonResponse = new JSONObject();        
        String status;
        
        /* Error Handling */        
        if (request.has("method")) {
            status = "ok";
            jsonResponse.put("status", status);            
        } else {
            status = "error";
            String message = "wrong request";
            jsonResponse.put("status", status);
            jsonResponse.put("description", message);
        }
        return jsonResponse;
    }

    public JSONObject readyUpResponse (JSONObject request) {
        JSONObject jsonResponse = new JSONObject();        
        String status;
        String message;
        
        /* Error Handling */        
        if (request.has("method")) {
            status = "ok";
            message = "waiting for other player to start";
        } else {
            status = "error";
            message = "wrong request";
        }
        jsonResponse.put("status", status);            
        jsonResponse.put("description", message);            
        return jsonResponse;
    }
    
    public JSONObject listClient (JSONObject request) {
        JSONObject jsonResponse = new JSONObject();        
        String status;
        String message;
        
        /* Error Handling */        
        if (request.has("method")) {
            status = "ok";
            message = "list of clients retrieved";
            jsonResponse.put("status", status);          
            jsonResponse.put("clients", this.Clients);
            jsonResponse.put("description", message);
        } else {
            status = "error";
            message = "wrong request";
            jsonResponse.put("status", status);
            jsonResponse.put("description", message);
        }         
        return jsonResponse;
    }


    /******************** Checking Function ********************/
    public boolean isUsernameExist (String username) {
        for (JSONObject Client: Clients) {
            if (Client.get("username").equals(username)) {
                return true;
            }
        }
        return false; 
    }
    
   public void startGame() throws IOException{
       JSONObject jsonRequest = new JSONObject();
       jsonRequest.put("method","start");
       jsonRequest.put("time","day");
       jsonRequest.put("role","werewolf");
       jsonRequest.put("friend","ahmad, dariel");
       jsonRequest.put("description","game is started");
       send(jsonRequest);
       System.out.println(jsonRequest);
   }
   
   public void changePhase() throws IOException{
       JSONObject jsonRequest = new JSONObject();
       jsonRequest.put("method","change_phase");
       jsonRequest.put("time","day");
       jsonRequest.put("days","3");       
       jsonRequest.put("description","PUT NARRATION HERE");
       send(jsonRequest);
       System.out.println(jsonRequest);
   }
   
   public void gameOver() throws IOException{
       JSONObject jsonRequest = new JSONObject();
       jsonRequest.put("method","game_over");
       jsonRequest.put("winner","werewolf");
       jsonRequest.put("description","PUT NARRATION HERE");
       send(jsonRequest);
       System.out.println(jsonRequest);
   }
    
}
