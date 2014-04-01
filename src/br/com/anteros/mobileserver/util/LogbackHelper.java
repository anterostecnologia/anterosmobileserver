/*******************************************************************************
 * Copyright 2012 Anteros Tecnologia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package br.com.anteros.mobileserver.util;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;

public class LogbackHelper {

	public static void disableAll() {
		for (Logger logger : getLoggerContext().getLoggerList())
			logger.setLevel(Level.OFF);
	}

	public static void enableAll(Level level) {
		for (Logger logger : getLoggerContext().getLoggerList())
			logger.setLevel(level);
	}

	public static LoggerContext getLoggerContext() {
		Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		return rootLogger.getLoggerContext();
	}

	public static void changeLevelLogger(String loggerName, Level level) {
		for (Logger logger : getLoggerContext().getLoggerList()) {
			if (logger.getName().equals(loggerName))
				logger.setLevel(level);
		}
	}
	
	public static void enableLogger(String loggerName) {
		for (Logger logger : getLoggerContext().getLoggerList()) {
			if (logger.getName().equals(loggerName))
				logger.setLevel(Level.ALL);
		}
	}

	public static void disableLogger(String loggerName) {
		for (Logger logger : getLoggerContext().getLoggerList()) {
			if (logger.getName().equals(loggerName))
				logger.setLevel(Level.OFF);
		}
	}
	
	public static Logger getRootLogger(){
		return (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
	}
	
	
	public static Appender getAppenderbyName(String name) {
		for (Logger logger : getLoggerContext().getLoggerList()) {
			Appender appender = logger.getAppender(name);
			if (appender!=null)
				return appender;
		}
		return null;
	}
}
