/* $HeadURL::                                                                            $
 * $Id:ListReplyAction.java 722 2006-10-02 16:42:45Z viru $
 *
 * Copyright (c) 2006 by Topaz, Inc.
 * http://topazproject.org
 *
 * Licensed under the Educational Community License version 1.0
 * http://opensource.org/licenses/ecl1.php
 */
package org.plos.annotation.action;

import com.opensymphony.xwork.validator.annotations.RequiredStringValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.plos.ApplicationException;
import org.plos.annotation.service.Annotation;
import org.plos.annotation.service.Reply;
import org.plos.article.service.ArticleWebService;
import org.topazproject.ws.article.ObjectInfo;

/**
 * Action class to get a list of replies to annotations.
 */
public class ListReplyAction extends AnnotationActionSupport {
  private String root;
  private String inReplyTo;
  private Reply[] replies;
  private Annotation baseAnnotation;
  private ArticleWebService articleWebService;
  private ObjectInfo articleInfo;
  
  private static final Log log = LogFactory.getLog(ListReplyAction.class);

  public String execute() throws Exception {
    try {
      replies = getAnnotationService().listReplies(root, inReplyTo);
    } catch (final ApplicationException e) {
      log.error("ListReplyAction.execute() failed for root: " + root, e);
      addActionError("Reply fetching failed with error message: " + e.getMessage());
      return ERROR;
    }
    return SUCCESS;
  }

  /**
   * List all the replies for a given root and inRelyTo in a threaded tree structure.
   * @return webwork status for the call
   * @throws Exception Exception
   */
  public String listAllReplies() throws Exception {
    try {
      if (log.isDebugEnabled()){
        log.debug("listing all Replies for root: " + root);
      }
      baseAnnotation = getAnnotationService().getAnnotation(root);
      replies = getAnnotationService().listAllReplies(root, inReplyTo);
      articleInfo = getArticleWebService().getObjectInfo(baseAnnotation.getAnnotates());
    } catch (final ApplicationException e) {
      log.error("Could not list all replies for root:" + root, e);
      addActionError("Reply fetching failed with error message: " + e.getMessage());
      return ERROR;
    }
    return SUCCESS;
  }

  public void setRoot(final String root) {
    this.root = root;
  }

  public void setInReplyTo(final String inReplyTo) {
    this.inReplyTo = inReplyTo;
  }

  public Reply[] getReplies() {
    return replies;
  }

  @RequiredStringValidator(message = "root is required")
  public String getRoot() {
    return root;
  }

  @RequiredStringValidator(message = "InReplyTo is required")
  public String getInReplyTo() {
    return inReplyTo;
  }

  /**
   * @return Returns the baseAnnotation.
   */
  public Annotation getBaseAnnotation() {
    return baseAnnotation;
  }

  /**
   * @param baseAnnotation The baseAnnotation to set.
   */
  public void setBaseAnnotation(Annotation baseAnnotation) {
    this.baseAnnotation = baseAnnotation;
  }

  /**
   * @return Returns the articleWebService.
   */
  public ArticleWebService getArticleWebService() {
    return articleWebService;
  }

  /**
   * @param articleWebService The articleWebService to set.
   */
  public void setArticleWebService(ArticleWebService articleWebService) {
    this.articleWebService = articleWebService;
  }

  /**
   * @return Returns the articleInfo.
   */
  public ObjectInfo getArticleInfo() {
    return articleInfo;
  }

  /**
   * @param articleInfo The articleInfo to set.
   */
  public void setArticleInfo(ObjectInfo articleInfo) {
    this.articleInfo = articleInfo;
  }
}