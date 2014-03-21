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
package br.com.anteros.mobileserver.app.form;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.AppenderBase;

public class MobileServerLogAppender extends AppenderBase<ILoggingEvent> {

	private LogForm logForm;
	private LogHtml logHtml = new LogHtml(300);

	public MobileServerLogAppender() {
	}

	@Override
	protected void append(ILoggingEvent eventLog) {
		String traceException = "";
		if ((eventLog.getThrowableProxy() != null)
				&& (eventLog.getThrowableProxy().getStackTraceElementProxyArray().length > 0)) {
			StringBuffer sb = new StringBuffer();
			boolean append = false;
			for (StackTraceElementProxy proxy : eventLog.getThrowableProxy().getStackTraceElementProxyArray()) {
				if (append)
					sb.append("\r\n");
				sb.append(proxy.getStackTraceElement());
				append = true;
			}
			traceException = sb.toString();
		}

		String clientId = "";
		String formatedMessage = eventLog.getFormattedMessage();

		if (eventLog.getFormattedMessage().contains("##")) {
			String[] splitMessage = eventLog.getFormattedMessage().split("\\##");
			formatedMessage = splitMessage[0];
			clientId = splitMessage[1];
		}

		logHtml.addLog(eventLog.getTimeStamp(), eventLog.getLevel(), eventLog.getLoggerName(),
				eventLog.getThreadName(), formatedMessage, traceException, clientId);
		refresh();
	}

	public void setLogForm(LogForm logForm) {
		this.logForm = logForm;
		refresh();

	}

	public void refresh() {
		if (logForm != null)
			this.logForm.getTextLog().setValue(logHtml.getHtml());
	}
}
