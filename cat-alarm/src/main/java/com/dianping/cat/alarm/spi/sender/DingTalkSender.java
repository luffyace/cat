/*
 * Copyright (c) 2011-2018, Meituan Dianping. All Rights Reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dianping.cat.alarm.spi.sender;

import com.dianping.cat.Cat;
import com.dianping.cat.alarm.sender.entity.Sender;
import com.dianping.cat.alarm.spi.AlertChannel;
import java.net.URLEncoder;
import java.util.List;

public class DingTalkSender extends AbstractSender {

	public static final String ID = AlertChannel.DINGTALK.getName();

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public boolean send(SendMessageEntity message) {
		Sender sender = querySender();
		boolean batchSend = sender.getBatchSend();
		boolean result = false;

		if (batchSend) {
			String dingTalks = message.getReceiverString();
			result = sendDingTalk(message, dingTalks, sender);
		} else {
			List<String> dingTalks = message.getReceivers();

			for (String dingTalk : dingTalks) {
				boolean success = sendDingTalk(message, dingTalk, sender);
				result = result || success;
			}
		}
		return result;
	}

	private boolean sendDingTalk(SendMessageEntity message, String receiver, Sender sender) {
		String domain = message.getGroup();
		String title = message.getTitle().replaceAll(",", " ");
		String content = message.getContent().replaceAll(",", " ");
		String urlPrefix = sender.getUrl();
		String urlPars = m_senderConfigManager.queryParString(sender);

		try {
			urlPars = urlPars.replace("${domain}", URLEncoder.encode(domain, "utf-8"))
									.replace("${receiver}", URLEncoder.encode(receiver, "utf-8"))
									.replace("${title}", URLEncoder.encode(title, "utf-8"))
									.replace("${content}", URLEncoder.encode(content, "utf-8"))
									.replace("${type}", URLEncoder.encode(message.getType(), "utf-8"));
		} catch (Exception e) {
			Cat.logError(e);
		}

		return httpSend(sender.getSuccessCode(), sender.getType(), urlPrefix, urlPars);
	}
}
