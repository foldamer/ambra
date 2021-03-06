/*
 * Copyright (c) 2006-2013 by Public Library of Science
 * http://plos.org
 * http://ambraproject.org
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

package org.ambraproject.service.crossref;

/**
 * <a href="http://www.crossref.org/">CrossRef</a> lookup service.
 *
 * @author Dragisa Krsmanovic
 */
public interface CrossRefLookupService {

  /**
   * Find a DOI for an article based on the passed in parameters.  If multiple articles are found that match, one is
   * arbitrarily selected.
   *
   * @param searchString the string to pass to crossref to search with
   * @return DOI of an article that matches, or null if no match is found
   * @throws Exception
   */
  public String findDoi(String searchString) throws Exception;
}
