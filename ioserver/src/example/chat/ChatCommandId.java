package example.chat;

/**
 * <p>
 * 命令ID
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class ChatCommandId {
	/**客户端登录请求**/
	public static final byte C_LOGIN_REQ = 1;
	
	/**服务器登录响应**/
	public static final byte S_LOGIN_RSP = 2;
	
	/**客户端添加好友请求*/
	public static final byte C_ADDFRIEND_REQ = 3;
	
	
	/**服务器添加好友响应*/
	public static final byte S_ADDFRIEND_RSP = 4;
	
	
	/**客户端发送消息*/
	public static final byte C_SEND_MSG = 5;
	

	/**服务器发送消息*/
	public static final byte S_SEND_MSG = 6;
	
	
	/**客户端心跳请求*/
	public static final byte C_HEARTBEAT_REQ = 7;
	
	
	/**服务器心跳响应*/
	public static final byte S_HEARTBEAT_RSP = 8;
	
	
	/**好友列表刷新请求*/
	public static final byte C_FRIENDLIST_REFURBISH_REQ = 9;
	
	
	/**好友列表刷新响应*/
	public static final byte S_FRIENDLIST_REFURBISH_RSP = 10;
	
	
	/**验证错误*/
	public static final byte S_VALIDATE_ERR = 11;
	
	/**发生错误*/
	public static final byte S_ERROR = 12;
	
	/**cmwap绑定*/
	public static final byte S_CMWAPBIND = 13;
	
	/**在线状态通知*/
	public static final byte S_ONLINESTATUS_NOTIFY = 14;
}
