package Server;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class Receiver
{
	public static void main(String args[]) throws Exception
	{
		int listenPort = 9876;
		DatagramSocket serverSocket = new DatagramSocket(listenPort);

		byte[] receiveData = new byte[1024];
		while(true)
		{
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);

			String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
			System.out.println("RECEIVED: " + sentence);
		}
	}
}
