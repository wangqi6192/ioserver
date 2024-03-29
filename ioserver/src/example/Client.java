﻿package example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class Client extends Thread {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			for(int i=0; i<10; i++) {
				Client client = new Client("127.0.0.1", 8899);
				client.flag = i+1;
				client.start();
				Thread.sleep(500);
			}
			
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
	
	int count = 0;
	
	int flag = 0;
	
	public Client(String host, int port) throws IOException {
		//socket = new Socket(host, port);
		
		//dis = new DataInputStream(socket.getInputStream());
		//dos = new DataOutputStream(socket.getOutputStream());
	}
	
	
	public void longConnection() {
		Random rand = new Random();
		try {
			socket = new Socket("127.0.0.1", 8899);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(isRuning) {
			
			int temp = rand.nextInt(5000);
			
			try {
				
				long startTime = System.currentTimeMillis();
				dos.writeInt(temp);
				
				int checkInt = dis.readInt();
				
				long endTime = System.currentTimeMillis();
				
				System.out.println("TIME("+flag+") = " + (endTime - startTime));
				
				if(checkInt != (temp + 1)) {
					System.out.println("存在问题，存在问题");
				}
				else {
					//System.out.println(checkInt);
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
	
	/**
	 * 短连接测试
	 */
	public void shortConnection() {
		Random rand = new Random();
		while(isRuning) {
			try {
				socket = new Socket("127.0.0.1", 8899);
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			
			
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
				
				socket.close();
				
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
	
	public void run() {
		isRuning = true;
		//shortConnection();
		longConnection();
	}
}
