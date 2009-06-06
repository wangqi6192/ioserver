package example.chat.msgtask;

import java.io.IOException;

import com.yz.net.IoSession;

import example.chat.InputMessage;
import example.chat.MessageFactory;
import example.chat.MessageProcessTask;
import example.chat.OutputMessage;
import example.chat.Player;

/**
 * <p>
 * 好友例表消息任务
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class FriendList extends MessageProcessTask {

	public FriendList(IoSession session, InputMessage message) {
		super(session, message);
	}

	@Override
	public void execute() {
		Player player = manager.getPlayer(message.getPlayerId());
		Player[] friends = player.getFriends();
		OutputMessage outMsg = MessageFactory.createSFriendListRsp(friends);
		
		player.putMessage(outMsg);
		
		player.flush();
		
	}

	@Override
	public void parse() throws IOException {
		// TODO Auto-generated method stub

	}
}
