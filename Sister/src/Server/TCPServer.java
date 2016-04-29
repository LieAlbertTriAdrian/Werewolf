import java.io.*; 
import java.net.*; 
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class TCPServer {    
	public static void main(String argv[]) throws Exception {          
		String clientSentence;          
		String capitalizedSentence;          
		ServerSocket welcomeSocket = new ServerSocket(6789);          
		while(true) {             
			                 System.out.println("Looping");
                        Socket connectionSocket = welcomeSocket.accept();             
			BufferedReader inFromClient =  new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));             
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());             
			//clientSentence = inFromClient.readLine();             
                        JSONParser parser = new JSONParser();
                        Object obj = parser.parse(inFromClient.readLine());
                        JSONObject jsonRequest = (JSONObject) obj;
                        String method = (String) jsonRequest.get("method");
                        System.out.println(method);
                        String username = (String) jsonRequest.get("username");
                        System.out.println(username);             
                        JSONObject jsonResponse = new JSONObject();
                        jsonResponse.put("status", "OK");
                        jsonResponse.put("player_id", "1");
                        outToClient.writeBytes(jsonResponse.toString() + "\n");         
		}
	} 
}