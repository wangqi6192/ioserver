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
	
	private boolean isCanUseOutputStream = true;
	
	private byte[] content;

	public OutputMessage(byte cmdtype) {
		super(cmdtype, null);
		bos = new ByteArrayOutputStream(512);
		dos = new DataOutputStream(bos);
	}
	
	
	public DataOutputStream getOutputStream() {
		if(!isCanUseOutputStream) {
			throw new UnsupportedOperationException("不能操作");
		}
		return dos;
	}
	
	
	public byte[] getBody() {
		if(body == null) {
			byte[] bosdata = bos.toByteArray();
			body = new byte[1 + bosdata.length];
			body[0] = cmdtype;
			
			System.arraycopy(bosdata, 0, body, 1, bosdata.length);
		}
		
		isCanUseOutputStream = false;
		
		return body;
	}

	@Override
	public byte[] getContent() {
		if(content == null) {
			byte[] _body = getBody();
			content = new byte[2 + 2 + _body.length];
			content[0] = 'O';
			content[1] = 'K';
			content[2] = (byte) ((_body.length) >>> 8 & 0xFF);
			content[3] = (byte) ((_body.length) >>> 0 & 0xFF);
			System.arraycopy(_body, 0, content, 4, _body.length);
		}
		
		return content;
	}

}
