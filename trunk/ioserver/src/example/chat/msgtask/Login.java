package example.chat.msgtask;

import java.io.IOException;
import com.yz.net.IoSession;
import example.chat.CheckPlayerTask;
import example.chat.InputMessage;
import example.chat.MessageFactory;
import example.chat.MessageProcessTask;
import example.chat.OutputMessage;
import example.chat.Player;

/**
 * <p>
 * 登录消息处理任务
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class Login extends MessageProcessTask {
	
	/**玩家昵称*/
	private String nickname;

	public Login(IoSession session, InputMessage message) {
		super(session, message);
	}

	@Override
	public void execute() {
		//验证码(登录时给出验证码)
		int validateCode = manager.getRandom().nextInt(5000);
		
		Player player = manager.getPlayer(message.getPlayerId());
		if(player != null) {
			while(validateCode == 0 || validateCode == player.getValidateCode()) {
				validateCode = manager.getRandom().nextInt(5000);
			}
			//先取消掉检查
			player.getCheckTask().cancel(); 
		}
		else {
			player = manager.newPlayer(message.getPlayerId(), nickname);
			manager.addPlayer(player);
		}
		
		synchronized (player) {
			player.setValidateCode(validateCode);   //设置验证码
			player.isOnline(true);         //设置在线
			
			CheckPlayerTask checktask = new CheckPlayerTask(player);
			player.setCheckTask(checktask);
			
			//设定定时检查，每十秒检查一次
			manager.getTimer().schedule(checktask, 10 * 1000, 10 * 1000);
			
			OutputMessage outMsg = MessageFactory.createSLoginRsp(validateCode);
			player.putMessage(outMsg);
			
			
			Player[] friends = player.getFriends();
			OutputMessage outMsg2 = MessageFactory.createSFriendListRsp(friends);
			player.putMessage(outMsg2);
			
			//通知所有朋友已经上线.
			OutputMessage outMsg3 = MessageFactory.createOnlineStatusNotify(player.getPlayerId(), true);
			for(int i=0; i<friends.length; i++) {
				friends[i].putMessage(outMsg3);
				friends[i].flush();
			}
			
			player.flush();
		}
	}

	
	@Override
	public void parse() throws IOException {
		nickname = this.message.getInputStream().readUTF();
	}

	@Override
	public StringBuilder toInputString() {
		//演示如何打印日志
		StringBuilder sb = new StringBuilder();
		sb.append("NICKNAME = " + nickname);
		return sb;
	}
}
