import java.io.*;
import java.net.*;

public class Server{
	
	public static void main(String[] args) {
		if(args.length < 2) { 
			System.err.println("Wrong Arguments");
			return;
		}

		int port = Integer.parseInt(args[0]);
		int clients = Integer.parseInt(args[1]);
		
		try(ServerSocket serverSocket= new ServerSocket(port)) {
			System.out.println("Server is listening on port "+ port);
			int i=0;
			while(i<clients) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("Client " + (i+1) + " is connected");
				new ServerThread(clientSocket).start();
				i++;
			}
		} catch(IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}
