package example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(8899);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(true) {
			Socket socket = null;
			try {
				socket = ss.accept();
				System.out.println("接入....");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Work work = new Work();
			work.socket = socket;
			Thread t = new Thread(work);
			t.start();
		}
		
	}

	
	public static class Work implements Runnable {
		
		Socket socket = null;

		@Override
		public void run() {
			try {
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				
				while(true) {
					int num = dis.readInt();
					
					dos.writeInt(num + 1);
				}
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
	}
}
