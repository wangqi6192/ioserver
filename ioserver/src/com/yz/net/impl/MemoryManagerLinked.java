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
	
	/** ÿ��buffer������ֽڴ�С */
	private int byteSize = 0;
	
	/** ���Դ���ٸ�buffer */
	private int num = 0;
	
	private boolean isDirect;
	
	/** buffer�Ķ��� */
	public LinkedList<BufferObj> bufferObjList = null;
	
	/**
	 * ����
	 * @param byteSize		�ֽڴ�С
	 * @param num			buffer����
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
	 * �Ӷ����л��һ��buffer����ʾΪ �ڸô˻�ȡδ�黹ʱ �����ٴλ�ȡ
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
	 * ����һ���µ�buffer �������ӵ����е���ȥ
	 * @return
	 */
	private ByteBuffer newBuffer(){
		BufferObj bufferObj = new BufferObj(this.byteSize);
		
		this.bufferObjList.add(bufferObj);
		++ this.num;
		return bufferObj.buf;
	}
	

	/**
	 * �黹buffer �����buf�Ѿ��黹���׳��쳣
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
		// TODO �ڴ���Ƭ����
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
		
		/** ��ʾ��buffer�Ѿ�����ȡʹ���ˣ������ٱ������ط���ȡʹ�� */
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



