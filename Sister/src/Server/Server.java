/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Client.Client;
import Config.Config;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Thread.sleep;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;
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
    private ArrayList<Socket> connectionSocket;
    private static ArrayList<DatagramSocket> datagramSockets = new ArrayList<DatagramSocket>();
    private ArrayList<JSONObject> Clients;
    private Runnable receiver;
    private Runnable serverCron;
    private String round;
    private int day;
    private boolean isGameRunning;
    private boolean isKPUElected;
    private int kpuElected;
    private ArrayList<Integer> kpuIds;
    private ArrayList<Integer> votes;
    private ArrayList<Boolean> readyStates;

    public static final void addDatagramSockets (DatagramSocket socket) {
        datagramSockets.add(socket);
    }

    public static ArrayList<DatagramSocket> getDatagramSockets () {
        return datagramSockets;
    }
            
    public Server (int port) throws IOException {
        this.listenPort = port;
        this.serverSocket = new ServerSocket(listenPort);
        this.Clients = new ArrayList<JSONObject>();
        this.isGameRunning = false;
        this.isKPUElected = false;
        this.connectionSocket = new ArrayList<Socket>();
        datagramSockets = new ArrayList<DatagramSocket>();
        this.kpuIds = new ArrayList<Integer>();
        this.votes = new ArrayList<Integer>();
        this.readyStates = new ArrayList<Boolean>();
        serverCron = new Runnable(){
            public void run(){
                try {
                    serverCronChecking();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            public void serverCronChecking() throws IOException, InterruptedException{
                while(true){
                    if (isKPUElected){
                        kpuSelected();
                        isKPUElected = false;
                    }
                    int i = 0;
                    while (i <= readyStates.size() && readyStates.get(i)){
                        i++;
                    }
                    if (i == readyStates.size()){
                        //startGame();
                    }
                    sleep(5000);
                }
            }
        };
        receiver = new Runnable() {
            int index;
            public void run(){
                try {
//                    s = serverSocket.accept();
//                    System.out.println(s);
                    index = connectionSocket.size()-1;
                    System.out.println("Thread baru!"+index);
                    call();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            public void call() throws IOException, ParseException {
                int i = this.index;
                int counterClient = 0;
                while(true) {             
                    System.out.println(i+": "+ connectionSocket.get(i));
                    JSONObject jsonRequest = receive(i);
                    JSONObject jsonResponse = new JSONObject();
                    System.out.println(jsonRequest);
                    String method = (String) jsonRequest.get("method");
                    System.out.println("Method : " + method);
                    if (method.equals("join"))
                        jsonResponse = joinGameResponse(jsonRequest);
                    else if(method.equals("leave"))
                        jsonResponse = leaveGameResponse(jsonRequest);
                    else if (method.equals("ready")){
                        jsonResponse = readyUpResponse(jsonRequest);
                        readyStates.set(i, true);
                    }
                    else if (method.equals("client_address"))
                        jsonResponse = listClient(jsonRequest);
                    else if (method.equals("vote_result_werewolf"))
                        jsonResponse = infoWerewolfKilledResponse(jsonRequest);
                    else if (method.equals("accepted_proposal")){
                        jsonResponse = clientAcceptedResponse(jsonRequest);
                        counterClient++;
                        if (counterClient >= Clients.size()-2)
                            kpuElected = recapitulateVote();
                            if (kpuElected != -1){
                                isKPUElected = true;
                            }
                    }
                    else if (method.equals("get_other"))
                        jsonResponse = getClient(Integer.parseInt((String) jsonRequest.get("playerId")));
                    else {
                        jsonResponse.put("status", "wrong request");
                    }
                    send(jsonResponse,i);
                }
            }
        };

    }
    
    public JSONObject getClient(int playerId){
        return Clients.get(playerId);
    }

    public void addClient (JSONObject client) {
        Clients.add(client);
    }
    
    public void startServer () throws IOException{
        while(true){
            Socket s = serverSocket.accept();
            connectionSocket.add(s);
            new Thread(receiver).start();
            new Thread(serverCron).start();
        }
    }
    
    public void broadcastServer(JSONObject jsonResponse) throws IOException{
        for (int i = 0; i < Clients.size(); i++){
            send(jsonResponse, i);
        }
    }

    
    public void stopServer () throws IOException {
        serverSocket.close();
    }

    public void send (JSONObject jsonResponse, int index) throws IOException {
        DataOutputStream outToClient = new DataOutputStream(connectionSocket.get(index).getOutputStream());             
        outToClient.writeBytes(jsonResponse.toString() + "\n");
    }
    
    public JSONObject receive (int index) throws IOException, ParseException {
        System.out.println("Masuk Receive");
        BufferedReader inFromClient =  new BufferedReader(new InputStreamReader(this.connectionSocket.get(index).getInputStream()));
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(inFromClient.readLine());
        JSONObject jsonRequest = new JSONObject(obj.toString());
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
                int playerId = this.getClients().size();
                jsonResponse.put("status", status);
                jsonResponse.put("player_id", playerId);
                
                JSONObject newClient = new JSONObject();
                newClient.put("player_id", playerId);
                newClient.put("is_alive", 1);
                newClient.put("address", request.get("udp_address"));
                newClient.put("port", request.get("udp_port"));
                newClient.put("username", request.get("username"));
         
                addClient(newClient);
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
        System.out.println(jsonResponse);
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
    
    public JSONObject clientAcceptedResponse (JSONObject request) {
        JSONObject jsonResponse = new JSONObject();  
        System.out.println(jsonResponse);
        String status;
        String message;
        int playerId, vote = 0;
                
        if (request.has("method")) {
            int retrieveKpuId = request.getInt("kpu_id");
            int occurences = Collections.frequency(kpuIds,retrieveKpuId);
            if (occurences == 0){
                kpuIds.add(retrieveKpuId);
                votes.add(vote++);
            } else {
                int indexKpuId = kpuIds.indexOf(retrieveKpuId);
                votes.set(indexKpuId, vote++);
            }
            status = "ok";
            message = "we have received your vote";
        } else {
            status = "error";
            message = "wrong request";
        }
        jsonResponse.put("status", status);            
        jsonResponse.put("description", message);            
        return jsonResponse;
    }
    
    public JSONObject infoWerewolfKilledResponse (JSONObject request) {
        JSONObject jsonResponse = new JSONObject();        
        String status;
        String message;
        int playerKilled = 0;
        
        /* Error Handling */        
        if (request.has("method")) {
            if (request.getInt("vote_status") == 1){
                playerKilled = request.getInt("player_killed");
            }
            for (JSONObject client : Clients){
                if (playerKilled == client.getInt("player_id")){
                    client.put("is_alive",0);
                }
            }
            status = "ok";
            message = "";
        } else {
            status = "error";
            message = "wrong request";
        }
        jsonResponse.put("status", status);            
        jsonResponse.put("description", message);            
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
    
    public int recapitulateVote(){
        int occurences = Collections.frequency(votes, Collections.max(votes));
        double totalCount = 0;
        for (Integer count : votes){
            totalCount += count;
        }
        if (totalCount % 2 == 0 && Collections.max(votes) == totalCount / 2){
            
        } else {
            if (occurences >= (Math.ceil(totalCount / 2.0) + 1)){
                int index = votes.indexOf(Collections.max(votes));
                return kpuIds.get(index);
            }
        }
        return -1;
    }
    
   public void startGame(int index) throws IOException{
       JSONObject player = Clients.get(index);
       JSONObject jsonRequest = new JSONObject();
       jsonRequest.put("method","start");
       jsonRequest.put("time","day");
       jsonRequest.put("role",player.getString("role"));
       String friends = "";
       for(int i = 0; i < Clients.size(); i++){
           if (i != index){
               if(Clients.get(i).getString("role").equals(player.getString("role"))){
                   if(friends.equals("")){
                       friends = Clients.get(i).getString("role");
                   }else{
                       friends = friends + ", " + Clients.get(i).getString("role");
                   }
               }
           }
       }
       jsonRequest.put("friend",friends);
       jsonRequest.put("description","game is started");
       send(jsonRequest, index);
       System.out.println(jsonRequest);
   }
   
   public void changePhase(int index) throws IOException{
       JSONObject jsonRequest = new JSONObject();
       jsonRequest.put("method","change_phase");
       jsonRequest.put("time","day");
       jsonRequest.put("days","3");       
       jsonRequest.put("description","PUT NARRATION HERE");
       send(jsonRequest, index);
       System.out.println(jsonRequest);
   }
   
   public void gameOver(int index) throws IOException{
       JSONObject jsonRequest = new JSONObject();
       jsonRequest.put("method","game_over");
       jsonRequest.put("winner","werewolf");
       jsonRequest.put("description","PUT NARRATION HERE");
       send(jsonRequest, index);
       System.out.println(jsonRequest);
   }
   
   public void kpuSelected() throws IOException{
       JSONObject jsonRequest = new JSONObject();
       jsonRequest.put("method","kpu_selected");
       jsonRequest.put("kpu_id", kpuElected);
       broadcastServer(jsonRequest);
       System.out.println(jsonRequest);
   }
    
   public static void main(String args[]) throws IOException, ParseException {
        Config config = new Config();
        int serverPort = config.serverPort;
        
        Server server = new Server(serverPort);
        server.startServer();
   }
}
