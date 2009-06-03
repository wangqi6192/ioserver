package example.chat;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import com.yz.net.NetMessage;

public class ChatMessage implements NetMessage {
	
	private byte cmdtype;
	
	private long playerId;
	
	private byte protocolType;
	
	private byte[] body;  


	public ChatMessage(byte cmdtype, long playerId, byte protocolType, byte[] body) {
		this.cmdtype = cmdtype;
		this.playerId = playerId;
		this.protocolType = protocolType;
		this.body = body;
	}
	
	public DataInputStream getStream() {
		ByteArrayInputStream bis = new ByteArrayInputStream(body);
		DataInputStream dis = new DataInputStream(bis);
		return dis;
	}
	
	@Override
	public byte[] getContent() {
		// TODO Auto-generated method stub
		return null;
	}
}
