package cs425_fall18_tm07;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ServerThread extends Thread {
	private Socket clientSocket;
	private File serverThroughput;
	private int servedRequests;
	private int client_ID;
	private int counter;

	public ServerThread(Socket socket, File ThroughputFile) {
		this.clientSocket = socket;
		this.serverThroughput = ThroughputFile;
		this.servedRequests = 0;
		this.counter = 0;
	}

	public int getRandomPayload() {
		int payloadSize = 0;
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

	public void printThroughput() throws IOException {
		String text = this.counter + ") Throughput of client " + this.client_ID + ": " + this.servedRequests + "\n";
		Files.write(Paths.get(this.serverThroughput.getName()), text.getBytes(), StandardOpenOption.APPEND);
		this.counter++;
	}

	public void run() {
		try {
			Timer timer = new Timer(true);
			timer.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					try {
						printThroughput();
					} catch (IOException e) {
						System.out.println("Server exception: " + e.getMessage());
						e.printStackTrace();
					}
				}
			}, 0, 10000);

			InputStreamReader input = new InputStreamReader(clientSocket.getInputStream());
			BufferedReader in = new BufferedReader(input);

			OutputStream output = clientSocket.getOutputStream();
			PrintWriter out = new PrintWriter(output, true);

			String clientInput = in.readLine();
			String clientInfo[] = clientInput.split(" ");
			String clientID = clientInfo[clientInfo.length - 1];
			this.client_ID = Integer.parseInt(clientID);

			while (clientInput != null) {
				clientInfo = clientInput.split(" ");
				clientID = clientInfo[clientInfo.length - 1];
				this.client_ID = Integer.parseInt(clientID);
				int randomPayload = getRandomPayload();
				out.println("WELCOME Client " + clientID);
				for (int i = 0; i < randomPayload; i++) {
					if ((i % 100) == 0) {
						out.println();
					} else {
						out.print(getRandomAsciiByte());
					}
				}
				out.println();
				out.println("END");
				this.servedRequests++;
				clientInput = in.readLine();
			}

			System.out.println("Client " + this.client_ID + " has finished");
			timer.cancel();
			clientSocket.close();
		} catch (IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}
