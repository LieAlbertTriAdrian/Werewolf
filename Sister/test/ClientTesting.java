/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import Client.Client;
import Config.Config;
import java.io.IOException;
import java.util.Scanner;
import org.json.simple.parser.ParseException;
/**
 *
 * @author alberttriadrian
 */
public class ClientTesting {
    public static void main(String args[]) throws IOException, ParseException {
        Config config = new Config();
        int serverPort = config.serverPort;
        
        System.out.println("Enter your port: ");
        Scanner sc = new Scanner(System.in);
        int clientPort = sc.nextInt();
        
        Client client = new Client("localhost", clientPort,"localhost", serverPort);
        client.start();
    }
}
