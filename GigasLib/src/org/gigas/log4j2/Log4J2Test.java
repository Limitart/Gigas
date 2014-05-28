package org.gigas.log4j2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Log4J2Test
 * 
 * @author hank
 * 
 */
public class Log4J2Test {
	private static Logger log = LogManager.getLogger(Log4J2Test.class);

	public static void main(String[] args) {
		System.out.println("just for log4j2 test");
		log.fatal("fatal leve");
		log.error("error leve");
		log.warn("warn leve");
		log.info("info leve");
		log.debug("debug leve");
		log.trace("trace leve");
	}

}
