package cs425_fall18_tm07;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Client {
	
	public static void main(String[] args) {
		if(args.length< 3) {
			System.err.println("Wrong Arguments");
			return;
		}
		try {
			int repetitions = Integer.parseInt(args[2]);
			
			File latencyTimes = new File("clientLatencies.txt");
			if (latencyTimes.exists() != true)
				latencyTimes.createNewFile();
			else{
				latencyTimes.delete();
				latencyTimes.createNewFile();
			}

			for (int j = 0; j < 1; j++) {
				ClientThread [] threads = new ClientThread [10];
				for (int i = 0; i < 10; i++) {
					threads[i] = new ClientThread(args, i, latencyTimes);
					threads[i].start();
				}
				for(int i = 0; i < threads.length; i++)
					  threads[i].join();
				String newline = "\n";
				Files.write(Paths.get(latencyTimes.getName()), newline.getBytes(), StandardOpenOption.APPEND);
			}
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("InterruptedException: " + e.getMessage());
			e.printStackTrace();
		}
	}
}