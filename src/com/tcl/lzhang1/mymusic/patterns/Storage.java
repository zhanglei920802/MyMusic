/**   
 * @Title: Storage.java 
 * @Package cn.cdut.app.design.pattern 
 * @Description: 
 *			声明:
 *			1)掌控校园是本人的大学的创业作品,也是本人的毕业设计
 *			2)本程序尚未进行开放源代码,所以禁止组织或者是个人泄露源码,否则将会追究其刑事责任
 *			3)编写本软件,我需要感谢彭老师以及其他鼓励和支持我的同学以及朋友
 *			4)本程序的最终所有权属于本人	
 * @author  张雷 794857063@qq.com
 * @date 2013-11-24 下午3:12:34 
 * @version V1.0   
 */
package com.tcl.lzhang1.mymusic.patterns;

/**
 * @ClassName: Storage
 * @Description:
 * @author 张雷 794857063@qq.com
 * @date 2013-11-24 下午3:12:34
 * 
 */
public class Storage {

	/**
	 * @Title: main
	 * @Description:
	 * @param @param args
	 * @return void
	 * @throws
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub


	}

	private Memento memento;

	public Storage(Memento memento) {
		this.memento = memento;
	}

	public Memento getMemento() {
		return memento;
	}

	public void setMemento(Memento memento) {
		this.memento = memento;
	}
}