package example.chat;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p>
 * 消息工厂，这里可以为每一个消息定一个类型，也可以像下面一下统一用ChatMessage替代
 * </p>
 * <br>
 * @author 胡玮@ritsky 
 *
 */
public class MessageFactory {
	
	/**
	 * <p>
	 * 创建服务器端登录响应
	 * </p>
	 * <br>
	 * @param type 协议类型
	 * @param status 状态
	 * @return
	 */
	public static ChatMessage createSLoginRsp(ProtocolType type, int validateCode) {
		byte[] rspdata  = new byte[4];
		
		rspdata[0] = (byte) ((validateCode >>> 24) & 0xFF); 
		rspdata[1] = (byte) ((validateCode >>> 16) & 0xFF);
		rspdata[2] = (byte) ((validateCode >>>  8) & 0xFF);
		rspdata[3] = (byte) ((validateCode >>>  0) & 0xFF);
		
		
		ChatMessage msg = new ChatMessage(ChatCommandId.S_LOGIN_RSP,0, 0,type, rspdata);
		
		return msg;
	
	}
	
	/**
	 * <p>
	 * 创建好友例表刷新响应
	 * </p>
	 * <br>
	 * @param type
	 * @param players
	 * @return
	 */
	public static ChatMessage createSFriendListRsp(ProtocolType type, Player[] players) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(512);
		DataOutputStream dos = new DataOutputStream(bos);
		
		ChatMessage msg = null;
		try {
			dos.writeShort(players.length);
			for(int i=0; i<players.length; i++) {
				dos.writeLong(players[i].getPlayerId());
				dos.writeUTF(players[i].getNickName());
				dos.writeByte(players[i].isOnline() ? 1 : 0);
			}
			
			msg = new ChatMessage(ChatCommandId.S_FRIENDLIST_REFURBISH_RSP, 0, 0, type, bos.toByteArray());
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}       //好友个数
		
		return msg;
	}
	
	
	/**
	 * <p>
	 * 创建验证错误
	 * </p>
	 * <br>
	 * @param type
	 * @return
	 */
	public static ChatMessage createValidateErr(ProtocolType type) {
		ChatMessage msg = new ChatMessage(ChatCommandId.S_VALIDATE_ERR, 0, 0, type, new byte[]{});
		
		return msg;
	}
	
	
	/**
	 * <p>
	 * 创建心跳响应
	 * </p>
	 * <br>
	 * @param type
	 * @return
	 */
	public static ChatMessage createHearTbeatRsp(ProtocolType type) {
		ChatMessage msg = new ChatMessage(ChatCommandId.S_HEARTBEAT_RSP, 0, 0, type, new byte[]{});
		return msg;
	}
	
	public static ChatMessage createError(ProtocolType type, byte errorcode) {
		ChatMessage msg = new ChatMessage(ChatCommandId.S_HEARTBEAT_RSP, 0, 0, type, new byte[]{errorcode});
		return msg;
	}
	
	
	/**
	 * <p>
	 * 创建添加好友响应
	 * </p>
	 * <br>
	 * @param type
	 * @param friend
	 * @return
	 */
	public static ChatMessage createAddFriendRsp(ProtocolType type, Player friend) {
		byte status = 0;
		if(friend == null) {
			status = 1;      //好友不存在
		}
		
		byte[] rspdata = null;
		if(status == 1) {
			rspdata = new byte[1];
			rspdata[0] = status;
		}
		else {
			ByteArrayOutputStream bos = new ByteArrayOutputStream(512);
			DataOutputStream dos = new DataOutputStream(bos);
			try {
				dos.writeByte(status);
				dos.writeUTF(friend.getNickName());
				dos.writeByte(friend.isOnline()? 1 : 0);
			}
			catch(IOException e) {
				e.printStackTrace();
			}
			rspdata = bos.toByteArray();
		}
		
		ChatMessage msg = new ChatMessage(ChatCommandId.S_ADDFRIEND_RSP, 0, 0, type, rspdata);
		return msg;
	}
	
	
	/**
	 * <p>
	 * 创建消息发送
	 * </p>
	 * <br>
	 * @param type
	 * @param message
	 * @return
	 */
	public static ChatMessage createSendMessage(ProtocolType type, String message) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(512);
		DataOutputStream dos = new DataOutputStream(bos);
		
		
		try {
			dos.writeUTF(message);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		ChatMessage msg = new ChatMessage(ChatCommandId.S_SEND_MSG, 0, 0, type, bos.toByteArray());
		return msg;
	}

	
	/**
	 * <p>
	 * 创建CMWAP绑定消息
	 * </p>
	 * <br>
	 * @param msgs
	 * @return
	 */
	public static CmWapBindMessage createCmWapBindMessage(ChatMessage[] msgs) {
		CmWapBindMessage msg = new CmWapBindMessage(ChatCommandId.S_CMWAPBIND, msgs);
		return msg;
	}
}

