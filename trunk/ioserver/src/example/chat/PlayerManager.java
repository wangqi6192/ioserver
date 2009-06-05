package example.chat;

import java.util.Random;
import java.util.Timer;
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
	
	
	private Random rand = new Random();
	
	private Timer timer = new Timer(true);
	
	private ConcurrentHashMap<Long, Player> playerMap = new ConcurrentHashMap<Long, Player>();

	private ConcurrentHashMap<Long, Vector<OutputMessage>> playerDatas = 
		new ConcurrentHashMap<Long, Vector<OutputMessage>>();
	
	
	public Timer getTimer() {
		return timer;
	}
	
	
	/**
	 * <p>
	 * 获取玩家
	 * </p>
	 * <br>
	 * @param playerId
	 * @return
	 */
	public Player getPlayer(long playerId) {
		return playerMap.get(playerId);
	}
	
	public Vector<OutputMessage> getPlayerDatas(long playerId) {
		Vector<OutputMessage> vector = playerDatas.get(playerId);
		if(vector == null) {
			playerDatas.putIfAbsent(playerId, new Vector<OutputMessage>());
		}
		
		vector = playerDatas.get(playerId);
		
		return vector;
	}
	
	
	/**
	 * <p>
	 * 生成新玩家
	 * </p>
	 * <br>
	 * @param playerId
	 * @param nickName
	 * @return
	 */
	public Player newPlayer(long playerId, String nickName) {
		Player player = new Player(playerId, nickName);
		
		return player;
	}
	
	
	/**
	 * <p>
	 * 添加玩家 0:成功  1:id重复
	 * </p>
	 * <br>
	 * @param player
	 * @return
	 */
	public int addPlayer(Player player) {
		if(player == null) {
			throw new NullPointerException("player is null");
		}
		
		Player returnPlayer = playerMap.putIfAbsent(player.getPlayerId(), player);
		
		if(returnPlayer == null) {
			return 0;
		}
		else{
			return 1;
		}
	}
	
	public Random getRandom() {
		return rand;
	}
}
