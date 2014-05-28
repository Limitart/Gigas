package org.gigas.log4j2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Log4J2Test
 * 
 * @author hank
 * @since 2014-05-29 01:17:14
 */
public class Log4J2Test {
	private static Logger log = LogManager.getLogger(Log4J2Test.class);

	public static void main(String[] args) {
		System.out.println("just for log4j2 test");
		try {
			while (true) {
				log.fatal("fatal level");
				log.error("error level");
				log.warn("warn level");
				log.info("info level");
				log.debug("debug level");
				log.trace("trace level");
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			log.error(e, e);
		}
	}

}
