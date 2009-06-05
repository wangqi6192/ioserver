package example.chat;


import java.util.HashSet;
import java.util.Iterator;
import java.util.TimerTask;
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
	
	
	public void addFriend(Player player) {
		this.friendIdSet.add(player.getPlayerId());
	}
	
	
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
	
	
	public void putMessage(OutputMessage message) {
		PlayerManager manager = PlayerManager.getInstance();
		Vector<OutputMessage> vector = manager.getPlayerDatas(playerId);
		vector.add(message);
	}
	
	
	public void flush() {
		/*ChatMessage[] msgs = null;
		PlayerManager manager = PlayerManager.getInstance();
		Vector<ChatMessage> vector = manager.getPlayerDatas(playerId);
		synchronized (vector) {
			msgs = new ChatMessage[vector.size()];
			int index = 0;
			for(ChatMessage msg : vector) {
				msgs[index ++] = msg;
			}
			vector.clear();
		}
		
		IoSession session = BootChat.service.getIoSession(this.sessionId);
		
		if(session != null) {
			if(type == ProtocolType.CMNET) {
				for(int i=0; i<msgs.length; i++) {
					session.write(msgs[i]);
				}
			}
			else {
				CmWapBindMessage msg = MessageFactory.createCmWapBindMessage(msgs);
				session.write(msg);
				
				session.addAttribute("CLOSETAG", "");
				session.close();
			}
		}*/
	}
}
