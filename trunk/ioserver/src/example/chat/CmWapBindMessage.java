package example.chat;

import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.MessageFormat;

/**
 * <p>
 * 输出消息，针对CMWAP用户的绑定消息，把所有输出消息组装成CMWAP用户能接收的消息类型
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class CmWapBindMessage extends OutputMessage {
	
	private OutputMessage[] outMessages;
	
	/**是否能操作输出流 false:不能操作 true:能操作*/
	//private boolean isCanOperationOutputStream;
	
	public CmWapBindMessage(byte cmdtype, OutputMessage[] outMessages) {
		super(cmdtype);
		this.outMessages = outMessages;
	}
	

	@Override
	public DataOutputStream getOutputStream() {
		throw new UnsupportedOperationException("不能操作....");
	}



	@Override
	public byte[] getContent() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("HTTP/1.1 {0} {1}\r\n");
		sb.append("Content-Length: {2}\r\n");
		sb.append("\r\n");
		
		int contentLen = 0;
		for(int i=0; i<outMessages.length; i++) {
			contentLen += outMessages[i].getContent().length;
		}
		
		MessageFormat mFormat = new MessageFormat(sb.toString());
		String headstr = mFormat.format(new Object[] { 200, "OK", contentLen});
		
		byte[] headdata = null;
		try {
			headdata = headstr.getBytes("ISO-8859-1");
		} catch (UnsupportedEncodingException e) {}
		
		if(body == null) {
			ByteBuffer buffer = ByteBuffer.wrap(new byte[headdata.length + contentLen]);
			buffer.clear();
			buffer.put(headdata);
			for(int i=0; i<outMessages.length; i++) {
				buffer.put(outMessages[i].getContent());
			}
			body = buffer.array();
		}
		
		return body;
	}
}
