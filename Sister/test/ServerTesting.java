/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import Config.Config;
import Server.Server;
import java.io.IOException;
import org.json.simple.parser.ParseException;

/**
 *
 * @author alberttriadrian
 */
public class ServerTesting {
    public static void main(String args[]) throws IOException, ParseException {
        Config config = new Config();
        int serverPort = config.serverPort;
        
        Server server = new Server(serverPort);
        server.startServer();
    }
}
