package example.chat;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import com.yz.net.NetMessage;


/**
 * 玩家管理
 * @author 胡玮@ritsky
 *
 */
public class PlayerManager {
	
	private static PlayerManager instance;
	
	public static PlayerManager getInstance() {
		if(instance == null) {
			instance = new PlayerManager();
		}
		
		return instance;
	}
	
	private PlayerManager() {}
	
	private ConcurrentHashMap<Long, Player> playerMap = new ConcurrentHashMap<Long, Player>();

	private ConcurrentHashMap<Long, Vector<NetMessage>> playerDatas = 
		new ConcurrentHashMap<Long, Vector<NetMessage>>();
	
	
	public Player getPlayer(long playerId) {
		return playerMap.get(playerId);
	}
	
	
	public Player newPlayer(long playerId, String nickName) {
		Player player = new Player(playerId, nickName);
		
		Player returnPlayer = playerMap.putIfAbsent(player.getPlayerId(), player);
		if(returnPlayer == null) {
			return player;
		}
		else {
			return returnPlayer;
		}
	}
}
