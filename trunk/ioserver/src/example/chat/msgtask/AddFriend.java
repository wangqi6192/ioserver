package example.chat.msgtask;

import java.io.IOException;

import com.yz.net.IoSession;

import example.chat.InputMessage;
import example.chat.MessageFactory;
import example.chat.MessageProcessTask;
import example.chat.Player;

public class AddFriend extends MessageProcessTask {
	
	private long friendId;

	public AddFriend(IoSession session, InputMessage message) {
		super(session, message);
	}

	@Override
	public void execute() {
		/*Player player = manager.getPlayer(message.getPlayerId());
		if(player.getValidateCode() != message.getValidateCode()) {
			player.putMessage(MessageFactory.createValidateErr(message.getProtocolType()));
		}
		else {
			Player friend = manager.getPlayer(friendId);
			if(friend != null) {
				player.addFriend(friend);
			}
			
			player.putMessage(MessageFactory.createAddFriendRsp(message.getProtocolType(), friend));
		}

		player.flush();*/
	}

	@Override
	public void parse() throws IOException {
		//friendId = message.getStream().readLong();

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
