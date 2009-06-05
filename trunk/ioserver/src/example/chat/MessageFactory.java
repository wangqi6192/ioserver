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
	public static OutputMessage createSLoginRsp(int validateCode) {
		OutputMessage outMsg = new OutputMessage(ChatCommandId.S_LOGIN_RSP);
		
		try {
			outMsg.getOutputStream().writeInt(validateCode);
		} catch (IOException e) {}
		
		return outMsg;
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
	public static OutputMessage createSFriendListRsp(Player[] players) {
		
		OutputMessage outMsg = new OutputMessage(ChatCommandId.S_FRIENDLIST_REFURBISH_RSP);
		try {
			outMsg.getOutputStream().writeShort(players.length);
			for(int i=0; i<players.length; i++) {
				outMsg.getOutputStream().writeLong(players[i].getPlayerId());
				outMsg.getOutputStream().writeUTF(players[i].getNickName());
				outMsg.getOutputStream().writeByte(players[i].isOnline() ? 1 : 0);
			}
		}
		catch(IOException e) {}
		
		return outMsg;
	}
	
	
	
	/**
	 * <p>
	 * 创建验证错误
	 * </p>
	 * <br>
	 * @param type
	 * @return
	 */
	public static OutputMessage createValidateErr() {
		OutputMessage outMsg = new OutputMessage(ChatCommandId.S_VALIDATE_ERR);
		return outMsg;
	}
	
	
	/**
	 * <p>
	 * 创建心跳响应
	 * </p>
	 * <br>
	 * @param type
	 * @return
	 */
	public static OutputMessage createHearTbeatRsp() {
		OutputMessage outMsg = new OutputMessage(ChatCommandId.S_HEARTBEAT_RSP);
		return outMsg;
	}
	
	
	public static OutputMessage createError(byte errorcode) {
		OutputMessage outMsg = new OutputMessage(ChatCommandId.S_HEARTBEAT_RSP);
		try {
			outMsg.getOutputStream().writeByte(errorcode);
		}
		catch(IOException e) {}
		
		return outMsg;
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
	public static OutputMessage createAddFriendRsp(Player friend) {
		OutputMessage outMsg = new OutputMessage(ChatCommandId.S_ADDFRIEND_RSP);
		
		byte status = 0;
		if(friend == null) {
			status = 1;      //好友不存在
		}
		
		try {
			outMsg.getOutputStream().writeByte(status);
			if(status == 0) {
				outMsg.getOutputStream().writeUTF(friend.getNickName());
				outMsg.getOutputStream().writeByte(friend.isOnline()? 1 : 0);
			}
		}
		catch(IOException e) {}

		return outMsg;		
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
	public static OutputMessage createSendMessage(String message) {
		OutputMessage outMsg = new OutputMessage(ChatCommandId.S_SEND_MSG);
		
		try {
			outMsg.getOutputStream().writeUTF(message);
		}
		catch(IOException e) {}
		
		
		return outMsg;
	}

	
	/**
	 * <p>
	 * 创建CMWAP绑定消息
	 * </p>
	 * <br>
	 * @param msgs
	 * @return
	 */
	public static CmWapBindMessage createCmWapBindMessage(OutputMessage[] msgs) {
		CmWapBindMessage msg = new CmWapBindMessage(ChatCommandId.S_CMWAPBIND, msgs);
		return msg;
	}
}

