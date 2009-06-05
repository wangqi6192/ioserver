package example.chat;

import java.io.IOException;
import com.yz.net.IoSession;

public abstract class MessageProcessTask implements Runnable{
	
	protected IoSession session;
	
	protected InputMessage message;
	
	protected PlayerManager manager = PlayerManager.getInstance();
	
	
	public MessageProcessTask(IoSession session, InputMessage message) {
		this.session = session;
		this.message = message;
	}
	
	/**
	 * <p>
	 * 解析
	 * </p>
	 * <br>
	 * @throws IOException
	 */
	public abstract void parse() throws IOException;
	
	
	/**
	 * <p>
	 * 执行
	 * </p>
	 * <br>
	 */
	public abstract void execute();
	 
	/**
	 * <p>
	 * 需要打印输入日志的就在这里给出日志内容
	 * </p>
	 * <br>
	 * @return
	 */
	public abstract StringBuilder toInputString();
	
	/**
	 * <p>
	 * 需要打印输出日志的就在这里给出日志内容
	 * </p>
	 * <br>
	 * @return
	 */
	public abstract StringBuilder toOutputString();
	 

	
	@Override
	public void run() {
		try {
			//先解析
			parse();
			StringBuilder inputStrBuffer = toInputString();
			if(inputStrBuffer != null) {
				printLog(inputStrBuffer.toString());
			}
			
			Player player = manager.getPlayer(message.getPlayerId());
			if(player != null) {
				player.lastAccessTime = System.currentTimeMillis();
			}
			
			execute();
			
			StringBuilder outputStrBuffer = toOutputString();
			if(outputStrBuffer != null) {
				printLog(outputStrBuffer.toString());
			}
		}
		catch(Exception e) {
			//TODO:做一些事发生错误时的处理，一般是发生了不可恢复的错误
			e.printStackTrace();
		}
	}
	
	
	protected final void printLog(String logmsg) {
		//TODO:这里可以放入打印，可以使用Log4j，或其它的一些打印日志的方法
	}
	
}
