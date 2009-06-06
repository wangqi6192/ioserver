package example.chat;

import com.yz.net.IoSession;

/**
 * <p>
 * 消息过滤器的一个实现，负责过虑掉登录请求的某些执行
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class FiltrateTask implements MsgTaskFilter {

	@Override
	public boolean filtrate(IoSession session, InputMessage message) {
		
		PlayerManager manager = PlayerManager.getInstance();
		Player player = manager.getPlayer(message.getPlayerId());
		
		if(player != null) {
			player.lastAccessTime = System.currentTimeMillis();
			player.setSessionId(session.getId());
			if(message.getCmdType() != ChatCommandId.C_LOGIN_REQ) {
				if(player.getValidateCode() != message.getValidateCode()) {
					OutputMessage outMsg = MessageFactory.createValidateErr();
					player.putMessage(outMsg);
					player.flush();
					return false;
				}
			}
		}
		
		return true;
	}

}
