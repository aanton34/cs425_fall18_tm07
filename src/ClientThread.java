package cs425_fall18_tm07;

import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.*;

public class ClientThread extends Thread {
	private String ip_address_server;
	private int port_server;
	private int client_id;
	private File latencyTimes;
	
	public ClientThread(String[] args, int id, File latencyFile) {
		this.ip_address_server = args[0]; // server ip address
		this.port_server = Integer.parseInt(args[1]); // the port of the server
		this.client_id = id;
		this.latencyTimes = latencyFile;
	}

	public void run() {
		// find the client ip address
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			String ip_address = localHost.getHostAddress().trim();
			Socket socket = new Socket(this.ip_address_server, this.port_server);
			while (socket.isClosed() == true) {
				socket = new Socket(this.ip_address_server, this.port_server);
			}
			String request = "HELLO " + ip_address + " " + socket 
					+ " " + this.client_id;
			OutputStream output = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(output, true);	
			// while <300 send request
			int requests = 0;
			long sum=0;
			while (requests < 300) {
				long startTime = System.currentTimeMillis();
				writer.println(request);
				DataInputStream reader = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				reader.readUTF();
				int length = reader.readInt();
				if(length>0){
					byte[] payload = new byte[length];
				    reader.readFully(payload, 0, payload.length); // read the message
				}
				requests++;
				long endTime = System.currentTimeMillis();
				long time = endTime - startTime;
				sum+=time;
			}
			System.out.println("Client " + this.client_id + " has finished after "
					+ requests + " requests");
			double averTime = sum / 300;
			String text = "Average Communication Latency (" + this.client_id + "): " + averTime + "\n";
			Files.write(Paths.get(this.latencyTimes.getName()), text.getBytes(), StandardOpenOption.APPEND);
			socket.close();	
		} catch (UnknownHostException ex) {
			System.out.println("Server not found: " + ex.getMessage());
			ex.printStackTrace();
		} catch (IOException ex) {
			System.out.println("I/O error: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}
