package com.yz.net.impl;



import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * <p>
 * ByteBuffer子序列分配内存管理器
 * </p>
 * <br>
 * @author 皮佳@ritsky
 *
 */
public class MomoryManagerByte  implements MemoryManagerInface{
	
//	public static void main(String[] args) {
//		MomoryManagerByte newMomoryManager = new MomoryManagerByte(100);
//		
//		ByteBuffer buf_1 = newMomoryManager.allocat(10);
//		ByteBuffer buf_2 = newMomoryManager.allocat(30);
//		ByteBuffer buf_3 = newMomoryManager.allocat(30);
//		ByteBuffer buf_4 = newMomoryManager.allocat(50);
//		
//		try {
//			newMomoryManager.free(buf_2);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		ByteBuffer buf_5 = newMomoryManager.allocat(20);
//		
//		
//		System.out.println("");
//		
//	}
	
	
	
	
	
	private static final int BYTE_SIZE = 1024;
	

	private ByteBuffer byteBuffer = null;
	private int byteSize = 1024 * 1024 * 1;
	
	private TreeSet<MomoryBuffer> bufferSet = new TreeSet<MomoryBuffer>(new MomoryBufferCommpositor());
	
	public MomoryManagerByte(int byteSize){
		this.byteSize = byteSize;
		byteBuffer = ByteBuffer.allocate(this.byteSize);
	}
	
	@Override
	public ByteBuffer allocat(int size) {
		boolean bor = false;
		if(bufferSet == null || bufferSet.size()<=0){
			this.byteBuffer.position(0);
			this.byteBuffer.limit(size);
			bor = true;
		}
		else{
			synchronized (this.bufferSet) {
				Iterator<MomoryBuffer> iter =  bufferSet.iterator();
				int position = 0;
				while(iter.hasNext()){
					MomoryBuffer momoryBuffer = iter.next();
					if((momoryBuffer.getPosition() - position) > size){
						this.byteBuffer.position(position);
						this.byteBuffer.limit(momoryBuffer.getPosition());
						bor = true;
						break;
					}
					position = momoryBuffer.getLimit();
				}
				if((this.getByteSize() - position) > size){
					this.byteBuffer.position(position);
					this.byteBuffer.limit(position + size);
					bor = true;
				}
			}
		}
		ByteBuffer slicebuf = null;
		if(bor){
			slicebuf = this.byteBuffer.slice();
			this.getBufferSet().add(new MomoryBuffer(slicebuf,slicebuf.arrayOffset(),slicebuf.arrayOffset() + slicebuf.limit()));
		}
		return slicebuf;
	}
	
	@Override
	public void free(ByteBuffer buf) throws Exception {
		synchronized (this.bufferSet) {
			Iterator<MomoryBuffer> iter =  bufferSet.iterator();
			while(iter.hasNext()){
				MomoryBuffer momoryBuffer = iter.next();
				if(momoryBuffer.getBuf() != buf){
					continue;
				}
				this.bufferSet.remove(momoryBuffer);
				break;
			}
		}
	}
	
	@Override
	public ByteBuffer allocat() {
		return this.allocat(BYTE_SIZE);
	}


	@Override
	public boolean neaten() {
		//整理内存碎片
		return false;
	}
	
	
	
	

	public ByteBuffer getByteBuffer() {
		return byteBuffer;
	}

	public int getByteSize() {
		return byteSize;
	}

	public TreeSet<MomoryBuffer> getBufferSet() {
		return bufferSet;
	}
}

class MomoryBuffer{
	private ByteBuffer buf = null;
	
	private int position = 0;
	private int limit = 0;
	
	public MomoryBuffer(ByteBuffer _buf,int _position,int _limit){
		this.buf = _buf;
		this.position = _position;
		this.limit = _limit;
	}

	public ByteBuffer getBuf() {
		return buf;
	}

	public int getPosition() {
		return position;
	}

	public int getLimit() {
		return limit;
	}
}

class MomoryBufferCommpositor implements Comparator<MomoryBuffer>{

	@Override
	public int compare(MomoryBuffer o1, MomoryBuffer o2) {
		
		int position_1 = o1.getPosition();
		int position_2 = o2.getPosition();
		
		if(position_1 > position_2){
			return 1;
		}
		if(position_1 < position_2){
			return -1;
		}
		return 0;
	}
	
}



