package com.yanan.framework.token;

import java.lang.ref.Reference;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Token生命周期管理守护线程
 * @author yanan
 *
 */
public class TokenLifeDeamon implements Runnable{
	private volatile boolean available;
	private int Intervals = 1000;
	private TokenManager tokenManager;
	public TokenLifeDeamon(TokenManager tokenManager){
		available = true;
		this.tokenManager = tokenManager;
	}
	@Override
	public void run() {
		while(available){
			Iterator<Entry<String, Reference<Token>>> tokenEntryIterator = TokenPool.getTokenMap().entrySet().iterator();
			Reference<Token> reference;
			while(tokenEntryIterator.hasNext()){
				reference = tokenEntryIterator.next().getValue();
				Token token;	
				if(reference == null || (token = reference.get()) == null) {
					tokenEntryIterator.remove();
					continue;
				}
				int times = (int) ((System.currentTimeMillis()-token.getLastuse())/1000);
				if(times > tokenManager.getTimeout()){
					token.destory();
				}
			}
			try {
				Thread.sleep(Intervals);
			} catch (InterruptedException e) {
			}
		}
	}
	public void shutdown(){
		available = false;
	}
}