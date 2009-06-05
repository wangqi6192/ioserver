package example.chat;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import com.yz.net.NetMessage;

/**
 * <p>
 * 输出消息
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class OutputMessage extends AbstractMessage implements NetMessage {
	
	private DataOutputStream dos;
	private ByteArrayOutputStream bos;

	public OutputMessage(byte cmdtype) {
		super(cmdtype, null);
		bos = new ByteArrayOutputStream(512);
		dos = new DataOutputStream(bos);
	}
	
	
	public DataOutputStream getOutputStream() {
		return dos;
	}
	

	@Override
	public byte[] getContent() {
		if(body == null) {
			body = bos.toByteArray();
		}
		
		int contentLen = 1 + body.length;
		byte[] content = new byte[2 + 2 + contentLen];
		
		content[0] = 'O';
		content[1] = 'K';
		content[2] = (byte) ((contentLen) >>> 8 & 0xFF);
		content[3] = (byte) ((contentLen) >>> 0 & 0xFF);
		content[4] = cmdtype;
		System.arraycopy(body, 0, content, 5, body.length);
		
		return content;
	}

}
