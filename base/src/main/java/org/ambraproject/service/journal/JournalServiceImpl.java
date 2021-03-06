/*
 * $HeadURL$
 * $Id$
 *
 * Copyright (c) 2006-2010 by Public Library of Science
 *     http://plos.org
 *     http://ambraproject.org
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
package org.ambraproject.service.journal;

import org.ambraproject.models.Article;
import org.apache.commons.configuration.Configuration;
import org.apache.struts2.ServletActionContext;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.ambraproject.models.Journal;
import org.ambraproject.service.hibernate.HibernateServiceImpl;
import org.ambraproject.web.VirtualJournalContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A collection of methods for manipulating and querying journal information
 *
 * @author Joe Osowski
 */
public class JournalServiceImpl extends HibernateServiceImpl implements JournalService {
  private static final Logger log = LoggerFactory.getLogger(JournalServiceImpl.class);

  private Configuration configuration;

  /**
   * Create a new journal-service instance. One and only one of these should be created for every
   * session-factory instance, and this must be done before the first session instance is created.
   */
  public JournalServiceImpl() {
  }

  /**
   * Get the specified journal.
   *
   * @param journalKey the journal's key
   * @return the journal, or null if not found
   */
  @Override
  @Transactional(readOnly = true)
  @SuppressWarnings("unchecked")
  public Journal getJournal(final String journalKey) {
    List<Journal> journals = hibernateTemplate.findByCriteria(
        DetachedCriteria.forClass(Journal.class)
            .add(Restrictions.eq("journalKey", journalKey))
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
    );

    if(journals.size() == 0)
      return null;

    return journals.get(0);
  }

  /**
   * Get the Journal from its <strong>eIssn</strong>.
   *
   * @param eIssn the journal's eIssn value.
   * @return the journal, or null if not found
   */
  @Transactional(readOnly = true)
  @SuppressWarnings("unchecked")
  public synchronized Journal getJournalByEissn(final String eIssn) {
    List<Journal> journals = hibernateTemplate.findByCriteria(
      DetachedCriteria.forClass(Journal.class)
        .add(Restrictions.eq("eIssn", eIssn)),0, 1);

    if(journals.size() == 0)
      return null;

    return journals.get(0);
  }

  /**
   * Get the set of all the known journals.
   *
   * @return all the journals, or the empty set if there are none
   */
  @Transactional(readOnly = true)
  public Set<Journal> getAllJournals() {
    Set<Journal> res = new HashSet<Journal>();
    for (String jName : getAllJournalNames())
      res.add(getJournal(jName));

    res.remove(null);  // in case something got deleted from under us
    return res;
  }

  /**
   * Get the names of all the known journals.
   *
   * @return the list of names; may be empty if there are no known journals
   */
  @Transactional(readOnly = true)
  @SuppressWarnings("unchecked")
  public Set<String> getAllJournalNames() {
    return new HashSet<String>(configuration.getList("ambra.virtualJournals.journals"));
  }

  /**
   * This method makes services dependent on servlet context.
   * Use getCurrentJournal() method in Action class instead.
   * .
   * Get the name of the current journal.
   *
   * @return the name of the current journal, or null if there is no current journal
   */
  @Deprecated

  public String getCurrentJournalName() {
    VirtualJournalContext vjc = (VirtualJournalContext) ServletActionContext.getRequest()
        .getAttribute(VirtualJournalContext.PUB_VIRTUALJOURNAL_CONTEXT);
    return (vjc == null) ? null : vjc.getJournal();
  }

  @Transactional(readOnly = true)
  @Override
  @SuppressWarnings("unchecked")
  public Set<Journal> getJournalsForObject(final String doi) {
    Set<Journal> journals = new HashSet<Journal>(2); //probably only going to be one journal

    //Is there a more efficient way to do this without using inline SQL?
    List<Article> articles = hibernateTemplate.findByCriteria(
      DetachedCriteria.forClass(Article.class)
        .add(Restrictions.eq("doi", doi)));

    if(articles.size() > 0) {
      journals.addAll(articles.get(0).getJournals());
    }

    return journals;

//    // this is for cross published objects
//    journals.addAll(hibernateTemplate.executeFind(new HibernateCallback() {
//      @Override
//      public Object doInHibernate(Session session) throws HibernateException, SQLException {
//      return session.createSQLQuery(
//        "SELECT {j.*} FROM journal j" +
//          " join articlePublishedJournals apj on apj.journalID = j.journalID" +
//          " join article a on a.articleID = apj.articleID" +
//          " WHERE a.doi = :doi")
//        .addEntity("j", Journal.class)
//        .setParameter("doi", doi)
//        .list();
//      }
//    }));
//
//    return journals;
  }


  /**
   * Get the list of journalNames which carry the given object (e.g. article).
   *
   * @param doi the info:&lt;oid&gt; uri of the object
   * @return the list of journal Name which carry this object; will be empty if this object
   *         doesn't belong to any journal
   */
  @Transactional(readOnly = true)
  @SuppressWarnings("unchecked")
  public Set<String> getJournalKeysForObject(final String doi) {
    Set<String> journalNames = new HashSet();
    Set<Journal> journals = getJournalsForObject(doi);

    for(Journal j : journals) {
      journalNames.add(j.getJournalKey());
    }

    return journalNames;
  }

  /**
   * Setter method for configuration. Injected through Spring.
   *
   * @param configuration Ambra configuration
   */
  @Required
  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }
}
