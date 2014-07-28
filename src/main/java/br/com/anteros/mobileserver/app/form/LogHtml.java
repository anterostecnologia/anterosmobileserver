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

import java.util.ArrayList;
import java.util.List;

import br.com.anteros.core.utils.StringUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.util.CachingDateFormatter;

public class LogHtml {

	private List<LogHtmlRow> logs = new ArrayList<LogHtmlRow>();
	private int maxLogs = 100;
	private static CachingDateFormatter SDF = new CachingDateFormatter("yyyy-MM-dd HH:mm:ss");

	public LogHtml(int maxLogs) {
		this.maxLogs = maxLogs;
	}

	public List<LogHtmlRow> getLogs() {
		return logs;
	}

	public void setLogs(List<LogHtmlRow> logs) {
		this.logs = logs;
	}

	public int getMaxLogs() {
		return maxLogs;
	}

	public void setMaxLogs(int maxLogs) {
		this.maxLogs = maxLogs;
	}

	public String getHtml() {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>\r\n").append("<head>\r\n");
		printCSS(sb);
		sb.append("</head>\r\n").append("<body>\r\n").append("<table class='tableLog'>");
		printLogs(sb);
		sb.append("</table>").append("</body>\r\n").append("</html>\r\n");
		return sb.toString();
	}

	public void printLogs(StringBuffer buf) {
		printHeader(buf);
		for (LogHtmlRow str : logs)
			printStatus(buf, str);
	}

	public void printHeader(StringBuffer buf) {
		buf.append("  <tr>\n\r");
		buf.append("    <th style='width:120px'>Data </th>\r\n");
		buf.append("    <th>Nível</th>\r\n");
		buf.append("    <th>Origem</th>\r\n");
		buf.append("    <th style='width:150px'>Id Cliente</th>\r\n");
		buf.append("    <th>Mensagem</th>\r\n");
		buf.append("  </tr>\r\n");
	}

	private void printStatus(StringBuffer buf, LogHtmlRow row) {
		buf.append("  <tr>\r\n");
		String dateStr = SDF.format(row.timeStamp);
		if (row.level == Level.ERROR) {
			buf.append("    <td style='color:red'>").append(dateStr).append("</td>\r\n")
					.append("    <td style='color:red'>").append(statusLevelAsString(row.level)).append("</td>\r\n")
					.append("    <td style='color:red'>").append(abbreviatedOrigin(row.loggerName)).append("</td>\r\n")
					.append("    <td style='color:red'>").append(row.clientId).append("</td>\r\n")
					.append("    <td style='color:red'>").append(row.formattedMessage)
					.append((StringUtils.isEmpty(row.traceException) == true ? "" : "\r\n" + row.traceException))
					.append("</td>\r\n");
		} else {
			buf.append("    <td>").append(dateStr).append("</td>\r\n").append("    <td>")
					.append(statusLevelAsString(row.level)).append("</td>\r\n").append("    <td>")
					.append(abbreviatedOrigin(row.loggerName)).append("</td>\r\n").append("    <td>")
					.append(row.clientId).append("</td>\r\n").append("    <td>").append(row.formattedMessage)
					.append((StringUtils.isEmpty(row.traceException) == true ? "" : "\r\n" + row.traceException))
					.append("</td>\r\n");
		}
		buf.append("  </tr>\r\n");
	}

	String abbreviatedOrigin(String loggerName) {
		int lastIndex = loggerName.lastIndexOf(CoreConstants.DOT);
		if (lastIndex != -1)
			return loggerName.substring(lastIndex + 1, loggerName.length());
		else
			return loggerName;
	}

	public void printCSS(StringBuffer output) {
		output.append("<STYLE TYPE=\"text/css\">\r\n").append(" .tableLog {").append("  font-family: helvetica;")
				.append("  background-color: #D0E2EC;").append(" border: 1px solid #D4D4D4;")
				.append(" border-collapse:collapse;").append(" color: #000000;").append(" }").append(" .tableLog th {")
				.append(" background-color: #287ECE;").append(" color: #FFFFFF;").append(" font-size: 11px;")
				.append(" padding-top: 5px;").append(" text-align: left;").append(" }")
				.append(" 	.tableLog td, .tableLog th {").append(" border: 1px solid #287ECE;")
				.append(" font-size: 11px;").append(" padding: 3px 7px 2px;").append(" }").append("</STYLE>\r\n");
	}

	String statusLevelAsString(Level level) {
		if (level == Level.ALL)
			return "ALL";
		else if (level == Level.INFO)
			return "INFO";
		else if (level == Level.DEBUG)
			return "DEBUG";
		else if (level == Level.WARN)
			return "WARN>";
		else if (level == Level.ERROR)
			return "ERROR";
		return "";
	}

	public void addLog(long timeStamp, Level level, String loggerName, String threadName, String formattedMessage,
			String traceException, String clientId) {
		LogHtmlRow htmlRow = new LogHtmlRow();
		htmlRow.timeStamp = timeStamp;
		htmlRow.level = level;
		htmlRow.loggerName = loggerName;
		htmlRow.threadName = threadName;
		htmlRow.formattedMessage = formattedMessage;
		htmlRow.traceException = traceException;
		htmlRow.clientId = clientId;
		if (logs.size() > maxLogs)
			logs.remove(0);
		logs.add(htmlRow);
	}

}
