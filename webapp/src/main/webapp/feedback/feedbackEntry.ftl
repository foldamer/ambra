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
<div id="content">
  <p>Fields marked with an <span class="required">*</span> are required. </p>
  <@s.form name="feedbackForm" cssClass="pone-form" action="feedback" method="post" title="Feedback" enctype="multipart/form-data">
    <@s.hidden name="page"/>
      <fieldset>
      <legend>Feedback</legend>
      <ol>
        <@s.textfield label="Name: " name="name" size="50" required="true"/>
        <@s.textfield label="E-mail Address: " name="fromEmailAddress" size="50" required="true"/>
        <@s.textfield label="Subject: " name="subject" required="true" size="50"/>
        <@s.textarea label="Message" name="note" required="true" cols="50" rows="5" />
        <li <#if fieldErrors["captcha"]??>class="form-error"</#if>>
          <label for="recaptcha_widget_div">Text verification:</label>
          ${captchaHTML}
          <#if fieldErrors["captcha"]??>
            &nbsp;${fieldErrors["captcha"][0]?html}
          </#if>
          <div class="clearer"></div>
        </li>
      </ol>
    <@s.submit value="Submit Feedback"/>
  </@s.form>
</div>