/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing;

import Server.Server;
import java.io.IOException;

/**
 *
 * @author alberttriadrian
 */
public class ServerTesting {
    public static void main(String args[]) throws IOException {
        Server server = new Server(6788);
        server.start();
    }
}