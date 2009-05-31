import com.yz.net.IoFuture;
import com.yz.net.IoHandler;
import com.yz.net.IoSession;
import com.yz.net.NetMessage;


public class LogicHandler implements IoHandler {

	@Override
	public void ioSessionClosed(IoFuture future) {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageReceived(IoSession session, NetMessage msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageReceived(IoSession session, byte[] msgdata) {
		System.out.println("xxxxxxxxxx");
	}

}
