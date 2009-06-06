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
 * 心跳消息任务
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class HearTbeat extends MessageProcessTask {

	public HearTbeat(IoSession session, InputMessage message) {
		super(session, message);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() {
		
		Player player = manager.getPlayer(message.getPlayerId());
		OutputMessage outMsg = MessageFactory.createHearTbeatRsp();

		
		
		player.putMessage(outMsg);
		
		player.flush();
	}

	@Override
	public void parse() throws IOException {
		// TODO Auto-generated method stub

	}

}

