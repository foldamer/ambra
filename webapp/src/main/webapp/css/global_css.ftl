<#--
  $HeadURL::                                                                            $
  $Id$
  
  Copyright (c) 2007-2010 by Public Library of Science
  http://plos.org
  http://ambraproject.org
  
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

<!-- global_css.ftl -->
<#list freemarker_config.getCss(templateFile, journalContext) as x>
  <#if x?contains("dojo.css")>
<@versionedCSS file="${freemarker_config.context}/css/dojo/dijit/themes/tundra/tundra.css" media="screen" />
  <#else>
<@versionedCSS file="${x}" media="all" />
  </#if>
</#list>
<@versionedCSS file="${freemarker_config.context}/css/print.css" media="print" />
