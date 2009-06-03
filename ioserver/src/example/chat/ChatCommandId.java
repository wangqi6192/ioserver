package example.chat;

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
	public static final byte S_HEARTBEAT_REQ = 8;
	
}
