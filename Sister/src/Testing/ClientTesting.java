/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing;

import Client.Client;
import java.io.IOException;
import org.json.simple.parser.ParseException;
/**
 *
 * @author alberttriadrian
 */
public class ClientTesting {
    public static void main(String args[]) throws IOException, ParseException {
        Client client = new Client("localhost", 6788, 6789);
        client.start();
    }
}