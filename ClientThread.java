import java.net.*;
import java.io.*;

public class ClientThread extends Thread {
	private String ip_address_server;
	private int port_server;
	private int client_id;
	
	public ClientThread(String[] args, int id) {
		this.ip_address_server = args[0]; // server ip address
		this.port_server = Integer.parseInt(args[1]); // the port of the server
		this.client_id = id;
	}
	
	public void run() {
		// find the client ip address
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			String ip_address = localHost.getHostAddress().trim();

			Socket socket = new Socket(this.ip_address_server, this.port_server);

			//while (socket.isClosed() == false) {
			//	socket = new Socket(this.ip_address_server, this.port_server);
			//}
			
			String request = "HELLO " + ip_address + " " + socket 
					+ " " + this.client_id;
			
			OutputStream output = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(output, true);
				
			// while <300 send request
			int requests = 0;
			while (requests < 300) {
				if((requests%25)==0) 
					System.out.println("Request "+requests+" from client "+this.client_id);
				writer.println(request);
				InputStream input = socket.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				String reply = reader.readLine();
				while (reply != null) {
					//System.out.println(reply);
					if(reply.compareTo("END")==0) {
						break;
					}
					else
						reply = reader.readLine();
				}
				requests++;
			}
			
			System.out.println("Client " + this.client_id + " has finished after "
					+ requests + " requests");
			socket.close();
			
		} catch (UnknownHostException ex) {
			System.out.println("Server not found: " + ex.getMessage());
		} catch (IOException ex) {
			System.out.println("I/O error: " + ex.getMessage());
		}
	}
}
