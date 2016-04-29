/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.net.Socket;

/**
 *
 * @author alberttriadrian
 */
public class ServerThread extends Thread {
    private Socket socket;
    private int clientId;
    private Server server;
    
    public ServerThread (Socket socket, int clientId, Server server) {
        this.socket = socket;
        this.clientId = clientId;
        this.server = server;
    }

    public void run () {
               
    }
    
}
