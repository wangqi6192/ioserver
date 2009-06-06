package example.chat;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.yz.net.IoSession;
import com.yz.net.NetMessage;
import com.yz.net.ProtocolHandler;

/**
 * <p>
 * CMNET协议解析
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class CmNetProtocolHandler implements ProtocolHandler {

	@Override
	public List<NetMessage> onData(ByteBuffer data, IoSession session) {
		
		session.addAttribute("TYPE", ProtocolType.CMNET);
		
		ArrayList<NetMessage> list = null;
		while(true) {
			if(data.remaining() < 2) {
				break;
			}
			
			byte[] tag = new byte[2];
			data.get(tag);
			if(tag[0] != 'O' || tag[1] != 'K') {
				break;
			}
			
			if(data.remaining() < 2) {
				break;
			}
			
			//内容长度
			short contentlen = data.getShort();
			
			if(data.remaining() < contentlen) {
				break;
			}
			
			
			byte[] content = new byte[contentlen];
			
			ByteBuffer contentBuffer = ByteBuffer.wrap(content);
			
			byte cmdtype = contentBuffer.get();
			long playerId = contentBuffer.getLong();
			int validateCode = contentBuffer.getInt();
			
			byte[] msgbody = new byte[contentBuffer.remaining()];
			
			contentBuffer.get(msgbody);
			
			InputMessage msg = new InputMessage(cmdtype,playerId, validateCode, msgbody); 
			
			if(list == null) {
				list = new ArrayList<NetMessage>();
			}
	
			list.add(msg);
			
		}
		
		return list;
	}

}
