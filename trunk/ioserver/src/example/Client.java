package example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Random;

public class Client extends Thread {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Client client = new Client("127.0.0.1", 8899);
			client.start();
			
			
			while(true) {
				Thread.sleep(10100);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	Socket socket;
	
	DataInputStream dis;
	
	DataOutputStream dos;
	
	boolean isRuning = false;
	
	public Client(String host, int port) throws IOException {
		socket = new Socket(host, port);
		
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
	}
	
	public void run() {
		isRuning = true;
		Random rand = new Random();
		while(isRuning) {
			int temp = rand.nextInt(5000);
			
			try {
				dos.writeInt(temp);
				
				int checkInt = dis.readInt();
				
				if(checkInt != (temp + 1)) {
					System.out.println("存在问题，存在问题");
				}
				else {
					System.out.println(checkInt);
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
