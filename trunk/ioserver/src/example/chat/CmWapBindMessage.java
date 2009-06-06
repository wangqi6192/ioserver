package example.chat;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.MessageFormat;

import com.yz.net.NetMessage;

/**
 * <p>
 * 输出消息，针对CMWAP用户的绑定消息，把所有输出消息组装成CMWAP用户能接收的消息类型
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class CmWapBindMessage extends AbstractMessage implements NetMessage {
	
	private OutputMessage[] outMessages;
	
	private byte[] content;
	
	/**是否能操作输出流 false:不能操作 true:能操作*/
	//private boolean isCanOperationOutputStream;
	
	public CmWapBindMessage(byte cmdtype, OutputMessage[] outMessages) {
		super(cmdtype, null);
		this.outMessages = outMessages;
	}
	


	@Override
	public byte[] getContent() {
		if(content == null) {
			//定议HTTP响应头
			StringBuilder sb = new StringBuilder(100);
			sb.append("HTTP/1.1 {0} {1}\r\n");
			sb.append("Content-Length: {2}\r\n");
			sb.append("\r\n");

			//计算内容的长度
			int contentLen = 1;  //命令字占一个字节
			for(int i=0; i<outMessages.length; i++) {
				int cmdsize = outMessages[i].getBody().length;
				contentLen += 2;
				contentLen += cmdsize;
			}
			

			MessageFormat mFormat = new MessageFormat(sb.toString());
			String headstr = mFormat.format(new Object[] { 200, "OK", contentLen});

			
			byte[] headdata = null;
			try {
				headdata = headstr.getBytes("ISO-8859-1");
			} catch (UnsupportedEncodingException e) {}

			ByteBuffer buffer = ByteBuffer.wrap(new byte[headdata.length + contentLen]);
			buffer.clear();
			buffer.put(headdata);
			buffer.put(cmdtype);
			for(int i=0; i<outMessages.length; i++) {
				buffer.putShort((short) outMessages[i].getBody().length);
				buffer.put(outMessages[i].getBody());
			}
			
			content = buffer.array();

		}
		return content;
	}
}
