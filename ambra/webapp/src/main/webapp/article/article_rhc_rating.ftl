<#--
  $HeadURL::                                                                            $
  $Id$
  
  Copyright (c) 2007-2008 by Topaz, Inc.
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
            <#if numUsersThatRated != 1>
              <#assign char = "s">
            <#else>
              <#assign char = "">
            </#if>
            <@s.url id="ratingsURL" namespace="/rate" action="getArticleRatings" includeParams="none" articleURI="${articleURI}"/>
            <h6>Average Rating <a href="${ratingsURL}" class="rating">(${numUsersThatRated} User Rating${char})</a></h6>
            <ol>
              <li>
              <#if isResearchArticle == true>
                <ul class="star-rating rating inline-rating-rhc" title="overall">
                    <#assign overallPct = (20 * overallRoundedAverage)?string("##0")>
                    <li class="current-rating overall-rating pct${overallPct}">Currently ${overallRoundedAverage?string("0.#")}/5 Stars.</li>
                </ul>
                <a href="javascript:void(0);" onclick="return ambra.domUtil.swapDisplayTextMode(this, 'ratingAverages', null, 'Hide all categories', 'See all categories');" class="rating catAvg">See all categories</a>
                <div id="ratingAverages">
                  <ol class="ratingAvgs">
                    <li><label for="insight">Insight</label>
                      <ul class="star-rating rating" title="insight">
                        <#assign insightPct = (20 * insightRoundedAverage)?string("##0")>
                        <li class="current-rating average pct${insightPct}">Currently ${insightAverage?string("0.#")}/5 Stars.</li>
                      </ul>
                    </li>
                    <li><label for="reliability">Reliability</label>
                      <ul class="star-rating rating" title="reliability">
                        <#assign reliabilityPct = (20 * reliabilityRoundedAverage)?string("##0")>
                        <li class="current-rating average pct${reliabilityPct}">Currently ${reliabilityAverage?string("0.#")}/5 Stars.</li>
                      </ul>
                    </li>
                    <li><label for="style">Style</label>
                      <ul class="star-rating rating" title="style">
                        <#assign stylePct = (20 * styleRoundedAverage)?string("##0")>
                        <li class="current-rating average pct${stylePct}">Currently ${styleAverage?string("0.#")}/5 Stars.</li>
                      </ul>
                    </li>
                  </ol>
                </div>
                <#else>
                <ul class="star-rating rating inline-rating-rhc" title="average">
                    <#assign averagePct = (20 * singleRatingRoundedAverage)?string("##0")>
                    <li class="current-rating single-rating pct${averagePct}">Currently ${singleRatingRoundedAverage?string("0.#")}/5 Stars.</li>
                </ul>
              </#if>
              <#if Session[freemarker_config.userAttributeKey]?exists>
                <#if hasRated>
                  <a href="javascript:void(0);" onclick="return ambra.rating.show('edit');" class="rating">Edit My Rating</a>
                <#else>
                  <a href="javascript:void(0);" onclick="return ambra.rating.show();" class="rating">Rate This Article</a>
                </#if>
              <#else>
                <a href="${freemarker_config.context}/user/secure/secureRedirect.action?goTo=${thisPage}" class="rating">Rate This Article</a>
              </#if>
              </li>
            </ol>