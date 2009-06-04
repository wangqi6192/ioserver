package example.chat;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import com.yz.net.NetMessage;

public class ChatMessage implements NetMessage {
	
	private byte cmdtype;
	
	private long playerId;
	
	private int validateCode; 
	
	private ProtocolType type ;
	
	private byte[] body;  

	private DataInputStream dis;

	public ChatMessage(byte cmdtype, long playerId, int validateCode , ProtocolType protocolType, byte[] body) {
		this.cmdtype = cmdtype;
		this.playerId = playerId;
		this.validateCode = validateCode;
		this.type = protocolType;
		this.body = body;
	}
	
	
	public byte getCmdType() {
		return cmdtype;
	}
	
	public long getPlayerId() {
		return playerId;
	}
	
	public int getValidateCode() {
		return validateCode;
	}
	
	public DataInputStream getStream() {
		if(dis == null) {
			ByteArrayInputStream bis = new ByteArrayInputStream(body);
			DataInputStream dis = new DataInputStream(bis);
		}
		return dis;
	}
	
	
	public ProtocolType getProtocolType() {
		return type;
	}
	
	@Override
	public byte[] getContent() {
		// TODO Auto-generated method stub
		return null;
	}
}
