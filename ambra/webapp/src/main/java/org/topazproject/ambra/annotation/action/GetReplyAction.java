/* $HeadURL::                                                                            $
 * $Id:GetReplyAction.java 722 2006-10-02 16:42:45Z viru $
 *
 * Copyright (c) 2006-2007 by Topaz, Inc.
 * http://topazproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.topazproject.ambra.annotation.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import org.topazproject.ambra.action.BaseActionSupport;
import org.topazproject.ambra.annotation.service.AnnotationConverter;
import org.topazproject.ambra.annotation.service.ReplyService;
import org.topazproject.ambra.annotation.service.WebReply;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

/**
 * Used to fetch a reply given an id.
 */
@SuppressWarnings("serial")
public class GetReplyAction extends BaseActionSupport {
  private String replyId;
  private WebReply reply;
  protected ReplyService replyService;
  protected AnnotationConverter converter;

  private static final Log log = LogFactory.getLog(GetReplyAction.class);

  @Transactional(readOnly = true)
  @Override
  public String execute() throws Exception {
    try {
      reply = converter.convert(replyService.getReply(replyId), true, true);
    } catch (final Exception e) {
      log.error("Could not retrieve reply: " + replyId, e);
      addActionError("Reply fetching failed with error message: " + e.getMessage());
      return ERROR;
    }
    return SUCCESS;
  }

  public void setReplyId(final String replyId) {
    this.replyId = replyId;
  }

  @RequiredStringValidator(message = "Reply id is a required field")
  public String getReplyId() {
    return replyId;
  }

  public WebReply getReply() {
    return reply;
  }

  @Required
  public void setReplyService(final ReplyService replyService) {
    this.replyService = replyService;
  }

  @Required
  public void setAnnotationConverter(AnnotationConverter converter) {
    this.converter = converter;
  }
}