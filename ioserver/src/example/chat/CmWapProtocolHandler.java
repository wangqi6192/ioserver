package example.chat;

import java.nio.ByteBuffer;
import java.util.List;

import com.yz.net.IoSession;
import com.yz.net.NetMessage;
import com.yz.net.ProtocolHandler;

public class CmWapProtocolHandler implements ProtocolHandler {

	@Override
	public List<NetMessage> onData(ByteBuffer data, IoSession session) {
		// TODO Auto-generated method stub
		return null;
	}

}
