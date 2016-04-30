/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import UDP.UDPClient;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 *
 * @author alberttriadrian
 */
public class UDPClientSendTesting {
    public static void main (String[] args) throws UnknownHostException, IOException {
        UDPClient client = new UDPClient("localhost", 9876);
        Scanner sc = new Scanner(System.in);
        
        client.setTargetIPAddress("localhost");
        client.setTargetPort(9876);
        
        while (true) {
            String method = sc.next();
            System.out.println("Method : " + method);
            client.call(method);
        }
    }
}
