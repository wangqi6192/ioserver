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
 * 发送消息任务
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class SendMessage extends MessageProcessTask {
	private long friendId;
	
	private String msgstr;

	public SendMessage(IoSession session, InputMessage message) {
		super(session, message);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() {
		Player player = manager.getPlayer(message.getPlayerId());
		
		Player friend = manager.getPlayer(friendId);
		
		if(friend != null) {
			OutputMessage outMsg = MessageFactory.createSendMessage(msgstr);
			friend.putMessage(outMsg);
			friend.flush();
		}
		
		player.flush();
	}

	
	@Override
	public void parse() throws IOException {
		friendId = message.getInputStream().readLong();
		msgstr = message.getInputStream().readUTF();
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
