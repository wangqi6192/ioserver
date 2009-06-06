package example.chat;

import com.yz.net.IoSession;

/**
 * <p>
 * 消息任务过滤器
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public interface MsgTaskFilter {
	boolean filtrate(IoSession session, InputMessage message);
}
