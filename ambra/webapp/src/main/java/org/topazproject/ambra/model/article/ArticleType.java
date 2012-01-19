/* $HeadURL$
 * $Id$
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
package org.topazproject.ambra.model.article;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.topazproject.ambra.configuration.ConfigurationStore;

@SuppressWarnings("serial")
public class ArticleType implements Serializable {
  /**
   * The article heading denoting a research type article.
   */
  public static final String ARTICLE_TYPE_HEADING_RESEARCH = "Research Article";

  private static Map<URI, ArticleType> knownArticleTypes = new HashMap<URI, ArticleType>();
  private static Map<URI, ArticleType> newArticleTypes = new HashMap<URI, ArticleType>();
  private static List<ArticleType>     articleTypeOrder = new ArrayList<ArticleType>();
  private static ArticleType           theDefaultArticleType = null;

  static {
    configureArticleTypes(ConfigurationStore.getInstance().getConfiguration());
  }

  private final URI uri;
  private final String heading;

  private ArticleType(URI articleTypeUri, String displayHeading) {
    uri = articleTypeUri;
    heading = displayHeading;
  }

  /**
   * Returns an ArticleType if configured in defaults.xml (etc) or null otherwise
   *
   * @param uri 
   * @return the article type
   */
  public static ArticleType getKnownArticleTypeForURI(URI uri) {
    return knownArticleTypes.get(uri);
  }

  /**
   * Returns an ArticleType object for the given URI. If one does not exist for that URI and
   * createIfAbsent is true, a new ArticleType shall be created and added to a list of types
   * (although shall not be recognized as an official ArticleType by getKnownArticleTypeForURI).
   * If createIfAbsent is false, an ArticleType shall not be created and null shall be returned.
   *
   * @param uri
   * @param createIfAbsent
   * @return The ArticleType for the given URI
   */
  public static ArticleType getArticleTypeForURI(URI uri, boolean createIfAbsent) {
    ArticleType at = knownArticleTypes.get(uri);
    if (at == null) {
      at = newArticleTypes.get(uri);
      if ((at == null) && createIfAbsent) {
        String uriStr = uri.toString();
        if (uriStr.contains("/")) {
          uriStr = uriStr.substring(uriStr.indexOf('/'));
          try {
            uriStr = URLDecoder.decode(uriStr, "UTF-8");
          } catch (UnsupportedEncodingException e) {
            // ignore and just use encoded uriStr :(
          }
          at = new ArticleType(uri, uriStr);
          newArticleTypes.put(uri, at);
        }
      }
    }
    return at;
  }

  /**
   * Ensures that the same object instance is used for identical URIs. The readResolve() method is
   * called when deserializing an object. ArticleType objects are serialized and deserialized when
   * propagated over the ehcache from one VM to another.
   *
   * @return
   * @throws ObjectStreamException
   */
  private Object readResolve() throws ObjectStreamException {
    return getArticleTypeForURI(this.uri, true);
  }

  public static ArticleType addArticleType(URI uri, String heading) {
    if (knownArticleTypes.containsKey(uri)) {
      return knownArticleTypes.get(uri);
    }
    ArticleType at = new ArticleType(uri, heading);
    knownArticleTypes.put(uri, at);
    articleTypeOrder.add(at);
    return at;
  }

  public URI getUri() {
    return uri;
  }

  public String getHeading() {
    return heading;
  }

  /**
   * Returns an unmodifiable ordered list of known ArticleTypes as read in order from the
   * configuration in configureArticleTypes().
   *
   * @return Collection of ArticleType(s)
   */
  public static List<ArticleType> getOrderedListForDisplay() {
    return Collections.unmodifiableList(articleTypeOrder);
  }

  /**
   * Read in the ArticleTypes from the pubApp configuration (hint: normally defined in defauls.xml)
   * and add them to the list of known ArticleType(s). The order of article types found in the
   * configuration is significant and is returned in a Collection from getOrderedListForDisplay().
   * The defaultArticleType is set to the first article type defined unless configured explicitly.
   */
  public static void configureArticleTypes(Configuration myConfig) {
    int count = 0;
    String basePath = "ambra.articleTypeList.articleType";
    String uriStr;

    /*
     * Iterate through the defined article types. This is ugly since the index needs
     * to be given in xpath format to access the element, so we calculate a base string
     * like: ambra.articleTypeList.articleType(x) and check if it's non-null for typeUri
     */
    do {
      String baseString = basePath + "(" + count + ").";
      uriStr = myConfig.getString(baseString + "typeUri");
      String headingStr = myConfig.getString(baseString + "typeHeading");
      if ((uriStr != null) && (headingStr != null)) {
        ArticleType at = addArticleType(URI.create(uriStr), headingStr);
        if (("true".equalsIgnoreCase(myConfig.getString(baseString + "default"))) ||
            (theDefaultArticleType == null)) {
          theDefaultArticleType = at;
        }
      }
      count++;
    } while (uriStr != null);
  }

  public static ArticleType getDefaultArticleType() {
    return theDefaultArticleType;
  }

  /**
   * Is the given {@link ArticleType} research related?
   * @param articleType
   * @return true/false
   */
  public static boolean isResearchArticle(ArticleType articleType) {
    return articleType == null ? false :
                                 ARTICLE_TYPE_HEADING_RESEARCH.equals(articleType.getHeading());
  }

  /**
   * Is the collection of {@link ArticleType}s research related?
   * <p>
   * This method returns <code>true</code> when one occurrence of
   * {@link ArticleType#ARTICLE_TYPE_HEADING_RESEARCH} is encountered.
   *
   * @param articleTypeURIs
   * @return true/false
   */
  public static boolean isResearchArticle(Collection<ArticleType> articleTypes) {
    if (articleTypes != null) {
      for (ArticleType at : articleTypes) {
        if (isResearchArticle(at))
          return true;
      }
    }
    return false;
  }

  /**
   * Override equals() to verify if the the compared ArticleType is equal to this instance.
   * If super.equals() returns false, we compare the uri. Note that readResolve() is implemented
   * above so that the super.equals() object-identity comparison should succeed if this
   * object was deserialized. This implementation is a safety net in case that fails.
   */
  @Override
  public boolean equals(Object obj) {
    if (super.equals(obj)) {
      return true;
    }
    if (obj instanceof ArticleType) {
      if (this.getUri() != null) {
        return (this.getUri().equals(((ArticleType)obj).getUri()));
      }
    }
    return false;
  }

  @Override
  public int hashCode() {
    return getUri().hashCode();
  }
}