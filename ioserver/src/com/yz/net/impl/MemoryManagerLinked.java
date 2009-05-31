package com.yz.net.impl;



import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 * </p>
 * <br>
 * @author 
 *
 */
class MemoryManagerLinked implements MemoryManagerInface {
	
	/** 每个buffer所存的字节大小 */
	private int byteSize = 0;
	
	/** 可以存多少个buffer */
	private int num = 0;
	
	private boolean isDirect;
	
	/** buffer的队列 */
	public LinkedList<BufferObj> bufferObjList = null;
	
	/**
	 * 构造
	 * @param byteSize		字节大小
	 * @param num			buffer个数
	 */
	public MemoryManagerLinked(int byteSize,int num, boolean isDirect){ 
		this.byteSize = byteSize;
		this.num = num;
		
		this.isDirect = isDirect;
		
		bufferObjList = new LinkedList<BufferObj>();
		for(int i=0;i<this.num;i++){
			bufferObjList.add(new BufferObj(this.byteSize));
		}
		
		
	}
	

	/**
	 * 从队列中获得一个buffer并标示为 在该此获取未归还时 不可再次获取
	 * @return
	 */
	@Override
	public ByteBuffer allocat(){
		for(int i=0;i<this.bufferObjList.size();i++){
			BufferObj bufferObj = this.bufferObjList.get(i);
			if(bufferObj == null){
				continue;
			}
			if(bufferObj.atomicBoolean.get()){
				continue;
			}
			bufferObj.atomicBoolean.compareAndSet(false, true);
			return bufferObj.buf;
		}
		return newBuffer();
	}

	/**
	 * 产生一个新的buffer 并把它加到队列当中去
	 * @return
	 */
	private ByteBuffer newBuffer(){
		BufferObj bufferObj = new BufferObj(this.byteSize);
		
		this.bufferObjList.add(bufferObj);
		++ this.num;
		return bufferObj.buf;
	}
	

	/**
	 * 归还buffer 如果该buf已经归还则抛出异常
	 * @param buf
	 */
	@Override
	public void free(ByteBuffer buf) throws Exception {

		for(int i=0;i<this.bufferObjList.size();i++){
			BufferObj bufferObj = this.bufferObjList.get(i);
			if(bufferObj.buf != buf){
				continue;
			}
			bufferObj.buf.clear();
			boolean b = bufferObj.atomicBoolean.compareAndSet(true, false);
			if(!b){
				throw new Exception(" Buf already restitute ! ");
			}
		}
	}

	
	@Override
	public ByteBuffer allocat(int size) {
		return null;
	}

	
	@Override
	public boolean neaten() {
		// TODO 内存碎片整理
		return false;
	}
	
	
	
	public int getByteSize() {
		return byteSize;
	}

	public int getNum() {
		return num;
	}

	public LinkedList<BufferObj> getBufferObjList() {
		return bufferObjList;
	}
	
	
	class BufferObj {
		
		/** 标示该buffer已经被获取使用了，不能再被其它地方获取使用 */
		public AtomicBoolean atomicBoolean = new AtomicBoolean(false);
		
		public ByteBuffer buf = null;
		
		public BufferObj(int byteSize){
			if(MemoryManagerLinked.this.isDirect) {
				buf = ByteBuffer.allocateDirect(byteSize);
			}
			else {
				buf = ByteBuffer.allocate(byteSize);
			}
		}
	}
}



