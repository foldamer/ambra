<#--
  $HeadURL::                                                                            $
  $Id$

  Copyright (c) 2007-2009 by Topaz, Inc.
  http://topazproject.org

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<#include "article_variables.ftl">
<@s.url id="thisPageURL" includeParams="get" includeContext="true" encode="false"/>
<div id="content" class="article" style="visibility:visible;">
  <#include "article_rhc.ftl">

  <div id="articleContainer">
    <form action="">
      <input type="hidden" name="doi" id="doi" value="${articleURI}" />
    </form>

    <div id="researchArticle" class="content related">
      <a id="top" name="top" toc="top" title="Top"></a>
      <#include "article_blurb.ftl">
      <h1>${docTitle}</h1>
      <#assign tab="related" />
      <#include "article_tabs.ftl">

      <h2>Related Articles <a href="#" class="info">info</a></h2>

      <h3>Related Subject Categories</h3>
      <ul>
        <#list mainCategories as cat>
          <li><a href="browse.action?catName=${cat?url}">${cat}</a></li>
        </#list>
      </ul>

      <h3>Related Articles on the web <img id="relatedArticlesSpinner" src="../../images/loading_small.gif" class="loading" /></h3>
      <div id="pubMedRelatedErr" style="display:none;"></div>
      <ul>
        <li><a href="http://scholar.google.com/scholar?hl=en&lr=&q=related:${docURL?url}&btnG=Search">Google Scholar</a></li>
        <li id="pubMedRelatedLI" style="display:none;"><a id="pubMedRelatedURL">Pubmed</a></li>
      </ul>

      <h3>Cited in <img id="relatedCitesSpinner" src="../../images/loading_small.gif" class="loading" /></h3>
      <div id="relatedCites"></div>
      <div>Search for citations on <a href="http://scholar.google.com/scholar?hl=en&lr=&cites=${docURL?url}">Google Scholar</a>.</div>

      <h2>Related Blog Posts <a href="#" class="info">info</a> <img id="relatedBlogSpinner" src="../../images/loading_small.gif" class="loading" /></h2>
      <div id="relatedBlogPosts"></div>

      Search for related blog posts on <a href="http://blogsearch.google.com/blogsearch?hl=en&ie=UTF-8&q=${shortDOI?url}&btnG=Search+Blogs">Google Blogs</a>

      <h3>Trackbacks</h3>
      <div>To trackback this article use the following trackback URL:<br/>
        <@s.url namespace="/" includeParams="none" id="trackbackURL" action="trackback" trackbackId="${articleURI}"/>
        <#assign trackbackLink = Request[freemarker_config.journalContextAttributeKey].baseHostUrl + trackbackURL>
        <a href="${trackbackLink}" title="Trackback URL">${trackbackLink}</a>
      </div>
    </div>
  </div>
  <div style="visibility:hidden">
    <#include "/widget/ratingDialog.ftl">
  </div>
</div>