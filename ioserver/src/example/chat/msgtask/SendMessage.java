package example.chat.msgtask;

import java.io.IOException;


import com.yz.net.IoSession;

import example.chat.ChatMessage;
import example.chat.MessageFactory;
import example.chat.MessageProcessTask;
import example.chat.Player;

public class SendMessage extends MessageProcessTask {
	private long friendId;
	
	private String msgstr;

	public SendMessage(IoSession session, ChatMessage message) {
		super(session, message);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() {
		Player player = manager.getPlayer(message.getPlayerId());
		if(player.getValidateCode() != message.getValidateCode()) {
			player.putMessage(MessageFactory.createValidateErr(message.getProtocolType()));
		}
		else {
			Player friend = manager.getPlayer(friendId);
			if(friend != null) {
				friend.putMessage(MessageFactory.createSendMessage(message.getProtocolType(), msgstr));
				friend.flush();
			}
		}
		player.flush();
	}

	
	@Override
	public void parse() throws IOException {
		friendId = message.getStream().readLong();
		msgstr = message.getStream().readUTF();
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
