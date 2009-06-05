package example.chat;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import com.yz.net.NetMessage;

/**
 * <p>
 * 输入消息
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class InputMessage extends AbstractMessage implements NetMessage {
	/**玩家id*/
	private long playerId;
	
	/**验证码*/
	private int validateCode;
	
	private DataInputStream dis;
	
	public InputMessage(byte cmdtype, long playerId, int validateCode, byte[] body) {
		super(cmdtype, body);
	}

	public long getPlayerId(){
		return playerId;
	}
	
	public int getValidateCode() {
		return validateCode;
	}
	
	@Override
	public byte[] getContent() {
		throw new UnsupportedOperationException("不能操作...");
	}
	
	/**
	 * <p>
	 * 获得输流
	 * </p>
	 * <br>
	 * @return
	 */
	public DataInputStream getInputStream() {
		if(dis == null) {
			ByteArrayInputStream bis = new ByteArrayInputStream(body);
			dis = new DataInputStream(bis);
		}
		
		return dis;
	}
}
