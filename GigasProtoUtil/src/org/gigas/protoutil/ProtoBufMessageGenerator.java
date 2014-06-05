package org.gigas.protoutil;

import java.io.IOException;

import org.gigas.protoutil.compilerbat.ProtoBufBatGenerator;
import org.gigas.protoutil.compilerbat.ProtoXmlParser;
import org.jdom2.JDOMException;

/**
 * 消息生成工具
 * 
 * @author hank
 * 
 */
public class ProtoBufMessageGenerator {

	public static void main(String[] args) {
		try {
			// 1.编译xml配置文件，转化为.proto文件
			// 2.根据xml配置文件生成对应的java文件来封装原始protojava文件
			// 3.根据xml配置文件来生成消息的handler
			ProtoXmlParser.xmlParse("G://GitWork//Gigas//GigasProtoUtil//protosrc//xml//chat//chat_message.xml");
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 1.用已经生成好的.proto源文件 编译成 原始protojava文件
		ProtoBufBatGenerator.getInstance().excute();
	}

}
