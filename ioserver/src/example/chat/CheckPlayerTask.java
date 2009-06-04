package example.chat;

import java.util.TimerTask;

public class CheckPlayerTask extends TimerTask {
	
	private Player player;
	
	public CheckPlayerTask(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		if(System.currentTimeMillis() - player.lastAccessTime > 1 * 60 * 1000) {
			player.isOnline(false);
			this.cancel();
		}
	}

}
