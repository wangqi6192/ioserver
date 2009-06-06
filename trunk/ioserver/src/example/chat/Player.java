package example.chat;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import com.yz.net.IoSession;



/**
 * <p>
 * 玩家
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class Player {

	/**玩家id*/
	private long playerId;
	
	/**玩家昵称*/
	private String nickName;
	
	/**IoSessionId*/
	private long sessionId;
	
	/**验证码*/
	private int validateCode;
	
	/**好友ID列表*/
	private HashSet<Long> friendIdSet;
	
	/**最近访问时间*/
	public long lastAccessTime = System.currentTimeMillis(); 
	
	/**Player检查任务*/
	private CheckPlayerTask checktask;
	
	/**是否在线*/
	private boolean isOnline;
	
	
	public Player(long playerId, String nickName) {
		this.playerId = playerId;
		this.nickName = nickName;
	}
	
	public CheckPlayerTask getCheckTask() {
		synchronized (this) {
			return checktask;
		}
	}
	
	public void setCheckTask(CheckPlayerTask checkTask) {
		synchronized (this) {
			this.checktask = checkTask;
		}
	}
	
	public void isOnline(boolean val) {
		this.isOnline = val;
	}
	
	public boolean isOnline() {
		return isOnline;
	}
	
	public int getValidateCode(){
		return validateCode;
	}
	
	public void setValidateCode(int validateCode){
		this.validateCode = validateCode;
	}
	
	public long getSessionId() {
		return sessionId;
	}
	
	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}
	
	
	
	public long getPlayerId() {
		return playerId;
	}
	
	public String getNickName() {
		return nickName;
	}
	
	/**
	 * <p>
	 * 添加好友
	 * </p>
	 * <br>
	 * @param player
	 */
	public void addFriend(Player player) {
		this.friendIdSet.add(player.getPlayerId());
	}
	
	
	/**
	 * <p>
	 * 获取好友列表
	 * </p>
	 * <br>
	 * @return
	 */
	public Player[] getFriends() {
		Player[] players = null;
		synchronized (friendIdSet) {
			players = new Player[friendIdSet.size()];
			int index = 0;
			Iterator<Long> iter = friendIdSet.iterator();
			
			PlayerManager manager = PlayerManager.getInstance();
			
			while(iter.hasNext()) {
				Player player = manager.getPlayer(iter.next());
				players[index++] = player;
			}
		}
		return players;
	}
	
	/**
	 * <p>
	 * 放入消息，由于要支持CMWAP与CMNET，与网络交互的是数据管理层，我们这里把消息放入到数据管理层
	 * </p>
	 * <br>
	 * @param message
	 */
	public void putMessage(OutputMessage message) {
		PlayerManager manager = PlayerManager.getInstance();
		Vector<OutputMessage> vector = manager.getPlayerDatas(playerId);
		vector.add(message);
	}
	
	
	/**
	 * <p>
	 * 刷新消息，会根据session的类型来具体组织消息
	 * </p>
	 * <br>
	 */
	public void flush() {
		IoSession session = BootChat.service.getIoSession(sessionId);
		if(session == null || session.isCloseing()){
			return;
		}
		
		ProtocolType type = (ProtocolType) session.getAttribute("TYPE");
		
		PlayerManager manager = PlayerManager.getInstance();
		Vector<OutputMessage> vector = manager.getPlayerDatas(playerId);
		
		switch(type) {
		case CMWAP:
			OutputMessage[] outMsgList = null;
			synchronized (vector) {
				outMsgList = new OutputMessage[vector.size()];
				for(int i=0; i<vector.size(); i++) {
					outMsgList[i] = vector.get(i);
				}
				
				vector.clear();
			}
			
			CmWapBindMessage wapMessage = MessageFactory.createCmWapBindMessage(outMsgList);
			session.write(wapMessage);
			
			break;
		case CMNET:
			synchronized (vector) {
				for(OutputMessage outMsg : vector) {
					session.write(outMsg);
				}
				vector.clear();
			}
			break;
		}
	}
}
