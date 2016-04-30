import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    // ///----------------------------------------Instance Variable Fields
    ServerSocket ss = null;
    Socket incoming = null;

    // ///----------------------------------------Instance Variable Fields

    // ///---------------------------------------- static Variable Fields
    public static ArrayList<Socket> socList = new ArrayList<Socket>();

    // ///---------------------------------------- static Variable Fields

    public void go() {

        try {

            ss = new ServerSocket(25005);

            while (true) {

                incoming = ss.accept();
                socList.add(incoming);
                System.out.println("Incoming: " + incoming);
                new Thread(new ClientHandleKaro(incoming)).start();

            }

        } catch (IOException e) {

            e.printStackTrace();
        } finally {

            try {
                ss.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    class ClientHandleKaro implements Runnable {

        InputStream is = null;
        OutputStream os = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        PrintWriter pw = null;
        boolean isDone = false;

        Socket sInThread = null;

        public ClientHandleKaro(Socket sxxx) {

            this.sInThread = sxxx;

        }

        @Override
        public void run() {

            if (sInThread.isConnected()) {

                System.out.println("Welcamu Clienta");
                System.out.println(socList);
            }

            try {
                is = sInThread.getInputStream();
                System.out.println("IS: " + is);
                isr = new InputStreamReader(is);
                br = new BufferedReader(isr);

                os = sInThread.getOutputStream();
                pw = new PrintWriter(os, true);

                String s = new String();

                while ((!isDone) && (s = br.readLine()) != null) {

                    String[] asx = s.split("-");
                    System.out.println("On Console: " + s);

                    // pw.println(s);

                    Thread tx = new Thread(new ReplyKaroToClient(s,
                            this.sInThread));
                    tx.start();

                    if (asx[1].trim().equalsIgnoreCase("BYE")) {

                        System.out.println("I am inside Bye");
                        isDone = true;

                    }
                }
            } catch (IOException e) {

                System.out.println("Thanks for Chatting.....");
            } finally {

                try {
                    Thread tiku = new Thread(new ByeByeKarDo(sInThread));
                    tiku.start();
                    try {
                        tiku.join();
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }

                    System.out.println("Accha to hum Chalte hain !!!");
                    System.out.println(socList);

                    br.close();
                    pw.close();
                    sInThread.close();

                } catch (IOException e) {

                }
            }

        }

    }

    class ReplyKaroToClient implements Runnable {

        public String mString;
        public Socket mSocket;

        public ReplyKaroToClient(String s, Socket sIn) {

            this.mString = s;
            this.mSocket = sIn;
        }

        @Override
        public void run() {

            for (Socket sRaW : socList) {

                if (mSocket.equals(sRaW)) {
                    System.out.println("Mai same hun");
                    continue;

                } else {
                    try {
                        new PrintWriter(sRaW.getOutputStream(), true)
                                .println(mString);
                    } catch (IOException e) {

                        System.out.println("Its in Catch");

                    }
                }
            }

        }

    }

    class ByeByeKarDo implements Runnable {

        Socket inCom;

        public ByeByeKarDo(Socket si) {

            this.inCom = si;
        }

        @Override
        public void run() {

            try {
                new PrintWriter(inCom.getOutputStream(), true)
                        .println("You have Logged Out of Server... Thanks for ur Visit");
            } catch (IOException e) {

                e.printStackTrace();
            }

        }

    }

    public static void main(String[] args) {

        new Server().go();
    }
}