/* $$HeadURL::                                                                            $$
 * $$Id$$
 *
 * Copyright (c) 2006-2009 by Topaz, Inc.
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

package org.topazproject.ambra.admin.action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.beans.factory.annotation.Required;
import org.topazproject.ambra.admin.service.AdminService;
import org.topazproject.ambra.admin.service.AdminService.JournalInfo;

@SuppressWarnings("serial")
public class PublishArchivesAction extends BaseAdminActionSupport {
  private static final Log log = LogFactory.getLog(PublishArchivesAction.class);

  // Fields Used by template
  private String[] articlesToPublish;
  private String[] articlesInVirtualJournals;
  private String[] articlesToDelete;
  private JournalInfo journalInfo;

  // Necessary Services
  private AdminService adminService;

  /**
   * Deletes and publishes checked articles from the admin console.  Note that delete has priority
   * over publish.
   */
  @Override
  @Transactional(rollbackFor = { Throwable.class })
  public String execute() {
    try {
      deleteArticles();
      publishArticles();
    } catch (Exception e) {
      addActionError("Exception: " + e);
      log.error(e);
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return ERROR;
    }

    // create a faux journal object for template
    journalInfo = adminService.createJournalInfo();
    return base();
  }

  /**
   * Publishes articles from the admin console.
   */
  private void publishArticles() {
    if (articlesToPublish == null)
      return;

    Map<String, Set<String>> vjMap = new HashMap<String, Set<String>>();
    if (articlesInVirtualJournals != null) {
      for (String articleInVirtualJournal : articlesInVirtualJournals) {
        // form builds checkbox value as "article" + "::" + "virtualJournal"
        String[] parts = articleInVirtualJournal.split("::");
        Set<String> vjList = vjMap.get(parts[0]);
        if (vjList == null)
          vjMap.put(parts[0], vjList = new HashSet<String>());
        vjList.add(parts[1]);
      }
    }

    List<String> msgs = getDocumentManagementService().publish(articlesToPublish, vjMap);
    for (String msg : msgs)
      addActionMessage(msg);
  }

  /**
   * Deletes the checked articles from the admin console.
   */
  private void deleteArticles() {
    if (articlesToDelete == null)
      return;

    List<String> msgs = getDocumentManagementService().delete(articlesToDelete);
    for (String msg : msgs)
      addActionMessage(msg);

    for (String article : articlesToDelete) {
      try {
        getDocumentManagementService().revertIngestedQueue(article);
      } catch (Exception ioe) {
        log.warn("Error cleaning up spool directories for '" + article +
                 "' - manual cleanup required", ioe);
        addActionMessage("Failed to move " + article + " back to ingestion queue: " + ioe);
      }
    }
  }

  /**
   *
   * @param articles array of articles to publish
   */
  public void setArticlesToPublish(String[] articles) {
    articlesToPublish = articles;
  }

  /**
   *
   * @param articlesInVirtualJournals array of ${virtualJournal} + "::" + ${article} to publish.
   */
  public void setArticlesInVirtualJournals(String[] articlesInVirtualJournals) {
    this.articlesInVirtualJournals = articlesInVirtualJournals;
  }

  /**
   *
   * @param articles array of articles to delete
   */
  public void setArticlesToDelete(String[] articles) {
    articlesToDelete= articles;
  }

  /**
   * Gets the JournalInfo value object for access in the view.
   *
   * @return Current virtual Journal value object.
   */
  public AdminService.JournalInfo getJournal() {
    return journalInfo;
  }

  /**
   * Sets the AdminService.
   *
   * @param  adminService The adminService to set.
   */
  @Required
  public void setAdminService(AdminService adminService) {
    this.adminService = adminService;
  }
}