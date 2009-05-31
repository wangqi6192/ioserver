import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * <p>
 * 无任何意义，完全验证用的 
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class ExampleServerSocket {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			ServerSocket sSocket = new ServerSocket(8899);
			
			while(true) {
				Socket socket = sSocket.accept();
				
				System.out.println("连接成功.....");
				
				Work work = new Work(socket);
				
				Thread t = new Thread(work);
				t.start();
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	static class Work implements Runnable {
		
		Socket wSocket;
		
		DataInputStream dis;
		
		DataOutputStream dos;
		
		Work(Socket _socket) throws IOException {
			wSocket = _socket;
			dis = new DataInputStream(wSocket.getInputStream());
			dos = new DataOutputStream(wSocket.getOutputStream());
		}

		@Override
		public void run() {
			
			while(true) {
				try {
					int x = dis.readInt();
					
					x = x + 1;
					
					dos.writeInt(x);
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

}
