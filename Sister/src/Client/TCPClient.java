import java.io.*; 
import java.net.*; 
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class TCPClient {  
    public static void main(String argv[]) throws Exception {   
            String sentence;   
            String modifiedSentence;   
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("method","join");
            jsonRequest.put("username","sister");
            System.out.print(jsonRequest);
            BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));   
            Socket clientSocket = new Socket("localhost", 6789);   
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());   
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));   
            //sentence = inFromUser.readLine();  
            outToServer.writeBytes(jsonRequest.toString() + "\n"); 
            
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(inFromServer.readLine());
            JSONObject jsonResponse = (JSONObject) obj;
            String status = (String) jsonResponse.get("status");
            System.out.println(status);
            String player_id = (String) jsonResponse.get("player_id");
            System.out.println(player_id); 
            clientSocket.close();  
    }
    
    void leaveGame(){
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("method", "leave");
    }
}