package example.chat.msgtask;

import java.io.IOException;

import com.yz.net.IoSession;

import example.chat.InputMessage;
import example.chat.MessageFactory;
import example.chat.MessageProcessTask;
import example.chat.Player;

public class FriendList extends MessageProcessTask {

	public FriendList(IoSession session, InputMessage message) {
		super(session, message);
	}

	@Override
	public void execute() {
		/*Player player = manager.getPlayer(message.getPlayerId());
		if(player.getValidateCode() != message.getValidateCode()) {
			player.putMessage(MessageFactory.createValidateErr(message.getProtocolType()));
		}
		else {
			Player[] players = player.getFriends();
			player.putMessage(MessageFactory.createSFriendListRsp(message.getProtocolType(), players));
		}
		
		player.flush();*/
	}

	@Override
	public void parse() throws IOException {
		// TODO Auto-generated method stub

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