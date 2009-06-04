package example.chat.msgtask;

import java.io.IOException;
import com.yz.net.IoSession;

import example.chat.ChatMessage;
import example.chat.CheckPlayerTask;
import example.chat.MessageFactory;
import example.chat.MessageProcessTask;
import example.chat.Player;

public class Login extends MessageProcessTask {
	
	/**玩家昵称*/
	private String nickname;

	public Login(IoSession session, ChatMessage message) {
		super(session, message);
	}

	@Override
	public void execute() {
		
		int validateCode = manager.getRandom().nextInt(5000);
		
		Player player = manager.getPlayer(message.getPlayerId());
		if(player != null) {
			while(validateCode == 0 || validateCode == player.getValidateCode()) {
				validateCode = manager.getRandom().nextInt(5000);
			}
		}
		else {
			player = manager.newPlayer(message.getPlayerId(), nickname);
			manager.addPlayer(player);
		}
		
		player.getCheckTask().cancel();
		
		CheckPlayerTask checktask = new CheckPlayerTask(player);
		manager.getTimer().schedule(checktask, 10 * 1000, 10 * 1000);
		
		player.setCheckTask(checktask);
		
		//设置验证码
		player.setValidateCode(validateCode);
		
		//设置玩家是依靠何种协议
		player.setProtocolType(message.getProtocolType());
		
		//设置在线
		player.isOnline(true);
		
		player.setSessionId(session.getId());
		
		session.addAttribute("PLAYER", player);
		
		//TODO:创建并发送登录响应
		player.putMessage(MessageFactory.createSLoginRsp(message.getProtocolType(), validateCode));
		
		
		//TODO:创建并发送好友列表响应
		//获得好友列表
		Player[] players = player.getFriends();
		player.putMessage(MessageFactory.createSFriendListRsp(message.getProtocolType(), players));
		
		player.flush();
	}

	
	@Override
	public void parse() throws IOException {
		nickname = this.message.getStream().readUTF();
	}

	@Override
	public StringBuilder toInputString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuilder toOutputString() {
		// TODO Auto-generated method stub
		return null;
	}

}
