package cs425_fall18_tm07;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class ServerThread extends Thread {
	private Socket clientSocket;
	private File serverThroughput;
	private int servedRequests;
	private int client_ID;
	private int counter;
	private static long previousMemoryUsed;

	public ServerThread(Socket socket, File ThroughputFile) {
		this.clientSocket = socket;
		this.serverThroughput = ThroughputFile;
		this.servedRequests = 0;
		this.counter = 0;
		this.previousMemoryUsed=0;
	}

	public int getRandomPayload() {
		int payloadSize = 0;
		Random rand = new Random(System.currentTimeMillis());
		payloadSize = (rand.nextInt((2000 - 300) + 1) + 300) * 1024;
		return payloadSize;
	}
	
	public void printStatusInfo() throws Exception {
		String text = this.counter + ") Throughput of client " + this.client_ID + ": " + this.servedRequests 
				+ " - MemoryUsageUtilization: " + getMemoryUsageUtilization() + " - ProcessCpuLoad: " +
				getProcessCpuLoad() + "\n";
		Files.write(Paths.get(this.serverThroughput.getName()), text.getBytes(), StandardOpenOption.APPEND);
		this.counter++;
	}
	
	public static double getMemoryUsageUtilization() {
		long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long actualMemUsed = afterUsedMem - previousMemoryUsed;
		double percentage = (100.0 * actualMemUsed) / (Runtime.getRuntime().totalMemory() * 1.0);
		return ((int) (percentage * 10) / 10.0);
	}
	
	public static double getProcessCpuLoad() throws Exception {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
		AttributeList list = mbs.getAttributes(name, new String[] { "ProcessCpuLoad" });
		if (list.isEmpty())
			return Double.NaN;
		Attribute att = (Attribute) list.get(0);
		Double value = (Double) att.getValue();
		if (value == -1.0)
			return Double.NaN;
		return ((int) (value * 10000) / 100.0);
	}

	public void run() {
		try {
			previousMemoryUsed = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			Timer timer = new Timer(true);
			timer.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					try {
						printStatusInfo();
					} catch (IOException e) {
						System.out.println("IOException: " + e.getMessage());
						e.printStackTrace();
					} catch (Exception e) {
						System.out.println("Exception: " + e.getMessage());
						e.printStackTrace();
					}
				}
			}, 1000, 20000);

			InputStreamReader input = new InputStreamReader(clientSocket.getInputStream());
			BufferedReader in = new BufferedReader(input);

			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

			String clientInput = in.readLine();
			String clientInfo[] = clientInput.split(" ");
			String clientID = clientInfo[clientInfo.length - 1];
			this.client_ID = Integer.parseInt(clientID);

			while (clientInput != null) {
				clientInfo = clientInput.split(" ");
				clientID = clientInfo[clientInfo.length - 1];
				this.client_ID = Integer.parseInt(clientID);
				
				String message = "WELCOME Client " + clientID + "\n";
				out.writeUTF(message);		// write the message
				
				int randomPayload = getRandomPayload();
				byte[] payload = new byte [randomPayload];
				new Random().nextBytes(payload);
				out.writeInt(payload.length); // write length of the message
				out.write(payload);           // write the message
				
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