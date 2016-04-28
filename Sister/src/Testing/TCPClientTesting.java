/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing;

import TCP.TCPClient;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 *
 * @author alberttriadrian
 */
public class TCPClientTesting {
    public static void main(String args[]) throws IOException {
        TCPClient client = new TCPClient("localhost", 6788);
        client.start();
    }
}
