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
 * 添加好友消息任务
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class AddFriend extends MessageProcessTask {
	
	private long friendId;

	public AddFriend(IoSession session, InputMessage message) {
		super(session, message);
	}

	@Override
	public void execute() {
		Player player = manager.getPlayer(message.getPlayerId());
		
		Player friend = manager.getPlayer(friendId);
		if(friend != null) {
			player.addFriend(friend);
		}
		
		OutputMessage outMsg = MessageFactory.createAddFriendRsp(friend);
		
		player.putMessage(outMsg);
		
		player.flush();
	}

	@Override
	public void parse() throws IOException {
		friendId = message.getInputStream().readLong();
		//friendId = message.getStream().readLong();

	}
}
