package example.chat;


import java.util.HashSet;
import java.util.Iterator;


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
	
	
	private HashSet<Long> friendIdSet;
	
	
	public Player(long playerId, String nickName) {
		this.playerId = playerId;
		this.nickName = nickName;
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
	
}
