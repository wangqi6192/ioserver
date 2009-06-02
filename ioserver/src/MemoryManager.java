



import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.logging.Logger;





/**
 * <p>
 * 实现了一个基本的内存管理器
 * </p>
 * <br>  
 *  
 * @author 胡玮@ritsky
 */
public class MemoryManager { 
	
	private static final Logger LOG = Logger.getLogger(MemoryManager.class.getName());
	
	
	

	// direct or non-direct buffer
	private boolean useDirectMemory = false;     //是否使用直接内存

	//每一块的内存为512字节
	private int chunksize = 10;
	
	private int chunknum = 3;
	
	private boolean preallocate = false;                //是否预先分配
	
	
	private ByteBuffer memoryBuffer;
	
	private FreeBuffer[] freeMemorys;
	
	
	private HashSet<ByteBuffer> newMemorySet = new HashSet();
	
	
	public static void main(String[] args) {
		MemoryManager manager = new MemoryManager(10, 10);
		
		ByteBuffer buf1 = manager.allocat(30);
		
		
		
		ByteBuffer buf2 = manager.allocat(30);
		
		System.out.println(buf2.hashCode());
		
		System.out.println(buf1.hashCode());
		
		System.out.println(System.identityHashCode(buf1));
		
		
		manager.free(buf2);
		
		buf2 = manager.allocat(24);
		
		manager.free(buf1);
		
		ByteBuffer buf3 = manager.allocat(40);
		
		ByteBuffer buf4 = manager.allocat(15);
		
		
		ByteBuffer buf5 = manager.allocat(20);
		
		manager.free(buf5);
		
		manager.free(buf3);
		
		manager.free(buf1);
		
		System.out.println();
	}
	
	
	public MemoryManager(int _chunksize, int _chunknum) {
			
	
		//this.useDirectMemory = _useDirectMemory;
		this.chunksize = _chunksize;
		this.chunknum = _chunknum;
		
		if(useDirectMemory) {
			memoryBuffer = memoryBuffer.allocateDirect(chunksize * chunknum);
		}
		else {
			memoryBuffer = memoryBuffer.allocate(chunksize * chunknum);
		}
		
		
		//把总内存分块
		freeMemorys = new FreeBuffer[chunknum];
		
		int savelimit = memoryBuffer.limit();
		for(int i=0; i<chunknum; i++) {
			freeMemorys[i] = new FreeBuffer();
			
			int limit = (i+1) * chunksize;
			
			memoryBuffer.limit(limit);
			
			ByteBuffer buf = memoryBuffer.slice();
			freeMemorys[i].membuf = buf;
			freeMemorys[i].hashcode = null;
			
			memoryBuffer.position(limit);
		}
		
		memoryBuffer.clear();
	}
	
	

	public ByteBuffer allocat(int size) {
		
		int num;
		
		if(size % chunksize == 0) {
			num = size / chunksize;
		}
		else {
			num = size / chunksize + 1;
		}
		
		int findnum = 0;
		
		int startIndex = -1;
		
		int len = freeMemorys.length;
		
		synchronized (this) {
			
			//寻找可用的内存起始块
			for(int i=0; i<len; i++) {
				if(freeMemorys[i].membuf.hasRemaining()) {
					findnum ++;
					if(startIndex < 0) {
						startIndex = i;
					}
				}
				else {
					findnum = 0;
					startIndex = -1;
				}
				
				if(findnum == num) {
					break;
				}
			}
			
			if(startIndex >= 0) {
				//标记内存不可用
				
				
				int offset = freeMemorys[startIndex].membuf.arrayOffset();
				int limit = freeMemorys[startIndex].membuf.arrayOffset() + size;
				
				memoryBuffer.clear();
				
				memoryBuffer.position(offset);
				memoryBuffer.limit(limit);
				
				ByteBuffer slicebuf = memoryBuffer.slice();
				
				//标记被占有
				for(int i=0; i<num; i++) {
					FreeBuffer free = freeMemorys[startIndex + i];
					free.membuf.position(free.membuf.limit());
					free.hashcode = System.identityHashCode(slicebuf);
				}
				
				return slicebuf;
			}

			return newBuffer(size);
		}
	}
	
	
	public void free(ByteBuffer buf) {
		synchronized(this) {
			if(!this.newMemorySet.remove(buf)) {
				buf.clear();

				int startIndex = buf.arrayOffset() / chunksize;
				
				int num = buf.limit() % chunksize == 0 ? buf.limit() / chunksize : buf.limit() / chunksize + 1; 
				
				int hashcode = System.identityHashCode(buf);
			
				for(int i=0; i<num; i++) {
					int index = startIndex + i;
					FreeBuffer free = freeMemorys[index];
					if(free.membuf.hasRemaining()) {
						throw new Error("Free Memory Error");
					}
					
					if(free.hashcode == null) {
						throw new Error("Free Memory Error");
					}
					
					if(free.hashcode != hashcode) {
						throw new Error("Free Memory Error");
					}
					
					
					free.membuf.clear();
					free.hashcode = null;
				}
			}
		}
	}
	
	
	
	
	/**
	 * creates a new buffer
	 * @param size  the size of the new buffer
	 * @return the new buffer
	 */
	private final ByteBuffer newBuffer(int size) {
		ByteBuffer buf = null;
		if(useDirectMemory) {
			buf = ByteBuffer.allocateDirect(size);
		}
		else {
			buf = ByteBuffer.allocate(size);
		}
		
		this.newMemorySet.add(buf);
		return buf;
	} 
	
	
	private class FreeBuffer {
		public ByteBuffer membuf;   
		public Integer hashcode;    //说明哪个对像占有了此内存块
	}
}