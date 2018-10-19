import java.io.*;
import java.net.*;
import java.util.Random;

public class ServerThread extends Thread{
	private Socket clientSocket;
	
	public ServerThread(Socket socket) {
		this.clientSocket = socket;
	}
	
	public int getRandomPayload() {
		int payloadSize=0;
		Random rand = new Random(System.currentTimeMillis());
		payloadSize = (rand.nextInt((2000 - 300) + 1) + 300) * 1024;
		return payloadSize;
	}
	
	public char getRandomAsciiByte() {
		Random r = new Random();
		int randomChar = r.nextInt(('~' - ' ') + 1) + ' ';
		char character = (char) randomChar;
		return character;
	}
	
	public void run() {
		try{
			InputStreamReader input  = new InputStreamReader(clientSocket.getInputStream());
	        BufferedReader in = new BufferedReader(input);
	        
	        OutputStream output = clientSocket.getOutputStream();
	        PrintWriter out = new PrintWriter(output, true);
	        
	        String clientInput= in.readLine();
	        String clientInfo [] = clientInput.split(" ");
        	String clientID = clientInfo[clientInfo.length - 1];
        	int client_ID = Integer.parseInt(clientID);
        	
	        System.out.println("Talking with Client " + client_ID);
	        while (clientInput  != null) {
	        	clientInfo = clientInput.split(" ");
	        	clientID = clientInfo[clientInfo.length - 1];
	        	client_ID = Integer.parseInt(clientID);
	        	int randomPayload = getRandomPayload();
	        	out.println("WELCOME Client " + clientID);
	        	for(int i=0; i<randomPayload; i++) {
	        		if((i%100) == 0) {
	        			out.println();
	        		}
	        		else {
	        			out.print(getRandomAsciiByte());
	        		}
	        	}
	        	out.println();
	        	out.println("END");
	        	clientInput= in.readLine();
	        }
	        
	        System.out.println("Client " + client_ID + " has finished");
	        clientSocket.close();        
		} catch(IOException ex) {
			System.out.println("Server exception: "+ ex.getMessage());
			ex.printStackTrace();
		}
	}
}
