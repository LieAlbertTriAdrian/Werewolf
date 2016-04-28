/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing;

import UDP.UDPClient;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 *
 * @author alberttriadrian
 */
public class UDPClientSendTesting {
    public static void main (String[] args) throws UnknownHostException, IOException {
        UDPClient client = new UDPClient("localhost", 9876);
        client.setTargetIPAddress("localhost");
        client.setTargetPort(9876);
        client.start();
    }
}
