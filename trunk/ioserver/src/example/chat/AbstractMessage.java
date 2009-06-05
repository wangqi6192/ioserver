package example.chat;

/**
 * <p>
 * 抽象消息类
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public abstract class AbstractMessage {
	/**消息类型*/
	protected byte cmdtype;
	
	/**包体*/
	protected byte[] body;
	
	
	public AbstractMessage(byte cmdtype, byte[] body) {
		this.cmdtype = cmdtype;
		this.body = body;
	}
	
	public byte getCmdType() {
		return cmdtype;
	}
}
