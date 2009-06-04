package example.chat;

import com.yz.net.NetMessage;

public class CmWapBindMessage implements NetMessage {
	private byte cmdtype;
	
	private ChatMessage[] msgs;

	public CmWapBindMessage(byte cmdtype, ChatMessage[] msgs) {
		this.cmdtype = cmdtype;
		this.msgs = msgs;
	}
	
	@Override
	public byte[] getContent() {
		// TODO Auto-generated method stub
		return null;
	}

}
