package example.chat;

import java.io.IOException;
import java.util.HashSet;

import com.yz.net.IoSession;

/**
 * <p>
 * 消息处理任务
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public abstract class MessageProcessTask implements Runnable{
	
	/**过滤器视图*/
	public static final HashSet<MsgTaskFilter> filterSet = new HashSet<MsgTaskFilter>();
	
	/**
	 * <p>
	 * 添加过滤器
	 * </p>
	 * <br>
	 * @param filter
	 */
	public static void addFilter(MsgTaskFilter filter) {
		if(filter == null) {
			filterSet.add(filter);
		}
	}
	
	/**
	 * <p>
	 * 过滤
	 * </p>
	 * <br>
	 * @param task
	 * @return
	 */
	public static boolean filtrate(MessageProcessTask task) {
		for(MsgTaskFilter filter : filterSet) {
			if(!filter.filtrate(task.session, task.message)) {
				return false;
			}
		}
		
		return true;
	}
	
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
	public StringBuilder toInputString(){
		return null;
	}
	
	/**
	 * <p>
	 * 需要打印输出日志的就在这里给出日志内容
	 * </p>
	 * <br>
	 * @return
	 */
	public StringBuilder toOutputString() {
		return null;
	}
	 

	
	@Override
	public void run() {
		try {
			if(!MessageProcessTask.filtrate(this)){
				//消息任务被过滤掉了
				return;
			}
			
			//先解析
			parse();
			
			StringBuilder inputStrBuffer = toInputString();
			if(inputStrBuffer != null) {
				printLog(inputStrBuffer.toString());
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
