package example.chat;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.yz.net.IoSession;
import com.yz.net.NetMessage;
import com.yz.net.ProtocolHandler;

public class CmWapProtocolHandler implements ProtocolHandler {

	@Override
	public List<NetMessage> onData(ByteBuffer data, IoSession session) {
		session.addAttribute("TYPE", ProtocolType.CMWAP);
		ArrayList<NetMessage> list = null;
		while(true) {
			byte[] headdata = new byte[data.remaining()];
			data.get(headdata);
			
			
			int contentlen = 0;
			
			try {
				String headstr = new String(headdata, "ISO-8859-1");
				
				int index = headstr.indexOf("\r\n\r\n");
				if(index < 0) { //没有找到
					break;
				}
				
				//寻找长度字段
				int idx = headstr.indexOf("Content-Length");
				idx = idx + "Content-Length: ".length();
				
				String lenstr = "";
				while(true){
					char c = headstr.charAt(idx++);
					if(c == '\r') {
						break;
					}
					lenstr += c;
				}
				
				contentlen = Integer.parseInt(lenstr);
				String substr = headstr.substring(index + 4);
				
				data.position(data.position() - substr.length());
					
			} catch (Exception e) {
				if(list == null) {
					list = new ArrayList<NetMessage>();
				}
				list.add(NetMessage.ERROR_MSG);
				return list;
			}
			
			//找到头的处理
			if(data.remaining() < contentlen) {
				break;
			}
			
			byte[] content = new byte[contentlen];
			data.get(content);
			
			ByteBuffer contentbuf = ByteBuffer.wrap(content);
			
			byte cmdtype = contentbuf.get();
			long playerId = contentbuf.getLong();
			int validateCode = contentbuf.getInt();
			
			byte[] body = new byte[contentbuf.remaining()];
			contentbuf.get(body);
			
			InputMessage msg = new InputMessage(cmdtype, playerId, validateCode, body);
			
			if(list == null) {
				list = new ArrayList<NetMessage>();
			}
			list.add(msg);
		}
		
		return list;
	}

}
