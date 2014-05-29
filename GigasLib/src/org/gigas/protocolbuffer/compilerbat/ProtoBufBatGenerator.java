package org.gigas.protocolbuffer.compilerbat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.gigas.utils.Symbol;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * ProtoBuf批处理编译文件生成器(windows)
 * 
 * @author hank
 */
public class ProtoBufBatGenerator {

	private static ProtoBufBatGenerator instance;
	private static Object lock = new Object();

	// 配置文件地址
	private String xmlpath;
	// 要编译的目录
	private String protosrc;
	// 输出目录
	private String outputPath;
	// 编译器地址
	private String compilerPath;
	// 临时bat文件存放地址
	private String batfilePath;

	private ProtoBufBatGenerator() {
		this.xmlpath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "protobuf.xml";
		File file = new File(this.xmlpath);
		if (!file.exists()) {
			this.xmlpath = System.getProperty("user.dir") + File.separator + "bin" + File.separator + "protobuf.xml";
		}
		xmlParse();
	}

	private ProtoBufBatGenerator(String xmlpath) {
		this.xmlpath = xmlpath;
		xmlParse();
	}

	private ProtoBufBatGenerator(String compileSrc, String output, String compiler, String batpath) {
		this.protosrc = compileSrc;
		this.outputPath = output;
		this.compilerPath = compiler;
		this.batfilePath = batpath;
	}

	/**
	 * xml解析
	 * 
	 * @throws IOException
	 * @throws JDOMException
	 */
	private void xmlParse() {
		try {
			System.out.println("start parse->" + this.xmlpath);
			SAXBuilder builder = new SAXBuilder();
			InputStream file = new FileInputStream(this.xmlpath);
			Document document = builder.build(file);// 获得对象
			Element root = document.getRootElement();// 获得根节点
			List<Element> list = root.getChildren();
			for (Element e : list) {
				String name = e.getName();
				String value = e.getAttribute("path").getValue();
				if ("compiler".equalsIgnoreCase(name)) {
					this.compilerPath = value;
				} else if ("protosrc".equalsIgnoreCase(name)) {
					this.protosrc = value;
				} else if ("output".equalsIgnoreCase(name)) {
					this.outputPath = value;
				} else if ("batpath".equalsIgnoreCase(name)) {
					this.batfilePath = value;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得实例
	 * 
	 * @return
	 */
	public static ProtoBufBatGenerator getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new ProtoBufBatGenerator();
				}
			}
		}
		return instance;
	}

	/**
	 * 获得实例
	 * 
	 * @param xmlpath
	 *            xml配置文件路径
	 * @return
	 */
	public static ProtoBufBatGenerator getInstance(String xmlpath) {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new ProtoBufBatGenerator(xmlpath);
				}
			}
		} else {
			instance.xmlpath = xmlpath;
		}
		return instance;
	}

	/**
	 * 获得实例
	 * 
	 * @param compileSrc
	 *            要编译的目录
	 * @param output
	 *            //输出地址
	 * @param compiler
	 *            //编译器地址
	 * @param batpath
	 *            //bat批处理文件地址
	 * @return
	 */
	public static ProtoBufBatGenerator getInstance(String compileSrc, String output, String compiler, String batpath) {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new ProtoBufBatGenerator(compileSrc, output, compiler, batpath);
				}
			}
		} else {
			instance.protosrc = compileSrc;
			instance.outputPath = output;
			instance.compilerPath = compiler;
			instance.batfilePath = batpath;
		}
		return instance;
	}

	/**
	 * 文件递归处理
	 * 
	 * @param file
	 * @param buffer
	 */
	private void fileIteration(File file, StringBuffer buffer) {
		if (!file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			File[] listFiles = file.listFiles();
			for (File temp : listFiles) {
				fileIteration(temp, buffer);
			}
		} else {
			String filename = file.getName();
			String[] split = filename.split(Symbol.DOT_REG);
			if (split.length < 2) {
				return;
			}
			if (!split[1].equalsIgnoreCase("proto")) {
				return;
			}
			String[] cmd = { "-I=" + file.getParent(), "--java_out=" + outputPath, file.getAbsolutePath() };
			System.out.print(compilerPath + " ");
			buffer.append(compilerPath).append(" ");
			for (String cmt : cmd) {
				buffer.append(cmt).append(" ");
				System.out.print(cmt + " ");
			}
			System.out.println();
			buffer.append("\r\n");// windows下
			// buffer.append("\n");// linux下
		}
	}

	/**
	 * 生成编译命令文本
	 */
	public void compile() {
		System.out.println("Starting Generating Message..........");
		StringBuffer buffer = new StringBuffer();
		fileIteration(new File(protosrc), buffer);
		buffer.append("\r\n").append("pause");
		File file = new File(batfilePath + "\\run_generator.bat");
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(file);
			output.write(buffer.toString().getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			output = null;
		}
		System.out.println("Ending Generating Message..........");
	}

	public void excute() {
		compile();
	}

	public static void main(String[] args) {
		ProtoBufBatGenerator.getInstance().excute();
	}
}
