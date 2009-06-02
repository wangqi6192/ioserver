package com.yz.net.impl;


/**
 * 用来创建内存管理的工厂类 
 * 提供了两种内存管理的对象，
 * @author 皮佳
 *
 */
public class MemoryObjFactory {
	
	
	/**
	 * 创建一个链表式的内存管理
	 * 通过该内存管理对象的方法可以每次从这个内存中取出一块内存出来使用
	 * 如果预先分配的内存已经全部被使用将创建一块新的内存放到链表中并标示为正在使用
	 * @param byteSize		链表中每块内存所分配的字节大小
	 * @param num			初始分配多少个内存
	 * @param isDirect		是否在虚拟机所所管理的范围内创建内存
	 */
	public static MemoryManagerLinked createMemoryManagerLinked(int byteSize,int num, boolean isDirect){
		
		MemoryManagerLinked memoryManagerLinked = new MemoryManagerLinked(byteSize,num,isDirect);
		
		return memoryManagerLinked;
		
	}
	
	/**
	 * 创建一个预先分配好的大小内存
	 * 通过该内存管理对象的方法可以每次从这个内存中取出指定大小的内存出来使用
	 * 当预先分配的内存已经不够时将重新产生一块新的内存进行分配
	 * @param byteSize			总的内存大小
	 * @param defaultSize		每次获取内存时的默认大小
	 * @param dilatancySize		每次扩充的内存大小
	 * @return
	 */
	public static MomoryManagerByte createMomoryManagerByte(int byteSize,int defaultSize,int dilatancySize,boolean isDirect){
		MomoryManagerByte momoryManagerByte = new MomoryManagerByte(byteSize,defaultSize,dilatancySize,isDirect);
		return momoryManagerByte;
	}
	
	
	
}
