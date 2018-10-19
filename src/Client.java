public class Client {
	
	public static void main(String[] args) {
		if(args.length< 2) {
			System.err.println("Wrong Arguments");
			return;
		}
		for (int i=0; i<10; i++) {
			new ClientThread(args,i).start();
		}
	}
}
