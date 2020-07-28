package com.yanan.frame.servlets.response;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.yanan.frame.plugin.Environment;

import sun.misc.Cleaner;

@SuppressWarnings("restriction")
public class ByteBufferPools {
	private static volatile ConcurrentLinkedDeque<ByteBuffer> bufferPools;
	private static final int DEFAULT_POOLS = 10;
	private static int Pools_Size = DEFAULT_POOLS;
	private static final int DEFAULT_BUFFER = 4096;
	private static int Buffer_Size = DEFAULT_BUFFER;
	private static String DEFAULT_BUFFER_TYPE = "heap";
	private static String Buffer_Type = DEFAULT_BUFFER_TYPE;
	private volatile static Lock lock;
	private static Logger logger = LoggerFactory.getLogger(ByteBufferPools.class);

	public static class Lock {
		public void lock() throws InterruptedException {
			this.wait();
		}
	}

	public static ByteBuffer getByteBuffer() {
		if (bufferPools == null) {
			initPools();
		}
		ByteBuffer buffer;
		synchronized (lock) {
			while ((buffer = bufferPools.poll()) == null) {
				try {
					lock.lock();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return buffer;
	}

	private static void initPools() {
		if (bufferPools == null) {
			synchronized (ByteBufferPools.class) {
				if (bufferPools == null) {
					Config config = Environment.getEnviroment().getConfigure();
					if (config != null) {
						config.allowValueNull();
						config = config.getConfig("MVC");
						if (config != null) {
							config.allowValueNull();
							config = config.getConfig("FileResponse");
							if (config != null) {
								if (config.hasPath("pools")) {
									Pools_Size = config.getInt("pools");
								}
								if (config.hasPath("buffer")) {
									Buffer_Size = config.getInt("buffer");
								}
								if (config.hasPath("bufferType")) {
									Buffer_Type = config.getString("bufferType");
								}
							}
						}
					}
					lock = new Lock();
					bufferPools = new ConcurrentLinkedDeque<ByteBuffer>();
					logger.debug("file response bytebuffer pools inited!");
					logger.debug("pools size:" + Pools_Size);
					logger.debug("buffer size:" + Buffer_Size);
					logger.debug("buffer type:" + Buffer_Type);
					for (int i = 0; i < Pools_Size; i++) {
						if (Buffer_Type.toLowerCase().trim().equals("direct")) {
							bufferPools.push(ByteBuffer.allocateDirect(Buffer_Size));
						} else {
							bufferPools.push(ByteBuffer.allocate(Buffer_Size));
						}
					}
				}
			}
		}
	}

	public static void release(ByteBuffer buffer) {
		synchronized (lock) {
			bufferPools.push(buffer);
			lock.notifyAll();
		}
	}

	@SuppressWarnings("unused")
	private static void clean(ByteBuffer byteBuffer) {
		if (byteBuffer.isDirect()) {
			Field cleanerField;
			try {
				cleanerField = byteBuffer.getClass().getDeclaredField("cleaner");
				cleanerField.setAccessible(true);
				Cleaner cleaner = (Cleaner) cleanerField.get(byteBuffer);
				cleaner.clean();
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		} else {
			byteBuffer.clear();
		}
	}

//	public static void main(String[] args) throws InterruptedException {
//		ByteBuffer buffer  = ByteBuffer.allocateDirect(1024*1024*1000);
//		for(int i = 0;i<buffer.limit();i++) {
//			buffer.put((byte) 'a');
//		}
//		Thread.sleep(10000);
////		clean(buffer);
//		System.out.println("清理内存");
//		buffer.position(0);
//		for(int i = 0;i<buffer.limit();i++) {
//			buffer.put((byte) 'a');
//		}
//		
//		while(true);
//	}
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
	}
}