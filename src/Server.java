package cs425_fall18_tm07;

import java.io.*;
import java.net.*;

public class Server{
	public static void main(String[] args) {
		if(args.length < 1) { 
			System.err.println("Wrong Arguments");
			return;
		}
		int port = Integer.parseInt(args[0]);
		try(ServerSocket serverSocket= new ServerSocket(port)) {
			System.out.println("Server is listening on port "+ port);
			int i=0;
			File serverThroughput = new File("serverThroughput.txt");
			if (serverThroughput.exists() != true)
				serverThroughput.createNewFile();
			else{
				serverThroughput.delete();
				serverThroughput.createNewFile();
			}
			while(true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("Client " + i + " is connected");
				new ServerThread(clientSocket,serverThroughput).start();;
				i++;
			}
		} catch(IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}
