<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html"/>
    <xsl:template match="/testSuite">
        <html>
            <head>
                <META HTTP-EQUIV="Pragma" CONTENT="no-cache"/>
                <title><xsl:value-of select="@suiteName"/></title>
                <LINK href="testSuite.css" rel="stylesheet" type="text/css"/>
            </head>
            <body>
              <script language="javascript">
                  function hideOrShowDiv(divId, imgId) {
                    var div = document.getElementById(divId);
                    var img = document.getElementById(imgId);
                    if ( div.className == "divShown" ) {
                        div.className = "divHidden";
                        img.src = "expand.png"
                    } else {
                        div.className = "divShown";
                        img.src = "contract.png"
                    }
                  }
              </script>
             <div class="suiteDetails">
                <span class="suiteName">Test Suite: <xsl:value-of select="@suiteName"/></span><span class="suiteTimeTaken">
                 Host: <xsl:value-of select="execution/@executionHost"/>&#160;&#160;
                 Run time: <xsl:value-of select="execution/resultsSummary/@timeTakenSeconds"/> seconds
                </span>
                <div class="suiteExecutionTime"><xsl:value-of select="execution/@executionStartTime"/></div>
            </div>
                <xsl:apply-templates select="execution"/>
                <xsl:apply-templates select="features/feature"/>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="execution">
        <div class="results">
            <table class="resultsTable">
                <tr>
                    <th scope="col"/>
                    <th scope="col">Passed</th>
                    <th scope="col">Failed</th>
                    <th scope="col">Undefined</th>
                    <th scope="col">Pending</th>
                    <th scope="col">Skipped</th>
                </tr>
                <tr>
                    <th scope="row">Feature</th>
                    <td class="pass"><xsl:value-of select="resultsSummary/@featuresPassed"/></td>
                    <td class="fail"><xsl:value-of select="resultsSummary/@featuresFailed"/></td>
                    <td/>
                    <td/>
                    <td/>
                </tr>
                <tr>
                    <th scope="row">Scenario</th>
                    <td class="pass"><xsl:value-of select="resultsSummary/@scenariosPassed"/></td>
                    <td class="fail"><xsl:value-of select="resultsSummary/@scenariosFailed"/></td>
                    <td/>
                    <td/>
                    <td/>
                </tr>
                <tr>
                    <th scope="row">Step</th>
                    <td class="pass"><xsl:value-of select="resultsSummary/@stepsPassed"/></td>
                    <td class="fail"><xsl:value-of select="resultsSummary/@stepsFailed"/></td>
                    <td class="fail"><xsl:value-of select="resultsSummary/@stepsUndefined"/></td>
                    <td class="pend"><xsl:value-of select="resultsSummary/@stepsPending"/></td>
                    <td class="other"><xsl:value-of select="resultsSummary/@stepsSkipped"/></td>
                </tr>
            </table>
        </div>
    </xsl:template>

    <xsl:template match="feature">
        <!-- show the configuration next to feature name when not base configuration -->
        <xsl:variable name="configuration">
            <xsl:choose>
                <xsl:when test="@configurationName='base'">
                    <xsl:value-of select="''"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="@configurationName"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <div class="feature">
            <xsl:variable name="featureId">
                <xsl:value-of select="@tokenId"/>
            </xsl:variable>

            <div class="featureTitle">
                <a href="javascript:hideOrShowDiv('featureBody{$featureId}', 'featureShowHide{$featureId}')">
                    <img id="featureShowHide{$featureId}" src="contract.png" class="expandContractImage"/>
                </a>
                <xsl:value-of select="@name"/>&#160;<xsl:value-of select="$configuration"/>
            </div>

            <div id="featureBody{$featureId}" class="divShown">
                <div class="featureDescription">
                <xsl:value-of select="description"/>
                </div>
                <xsl:apply-templates select="scenarios/scenario"/>
            </div>
        </div>
    </xsl:template>

    <xsl:template match="scenario">
        <div class="scenario">
            <div class="scenarioTitle"><xsl:value-of select="@name"/></div>
            <div class="steps">
                <xsl:apply-templates select="steps/step"/>
            </div>
        </div>
    </xsl:template>

    <xsl:template match="step">

        <!-- show the configuration next to feature name when not base configuration -->
        <xsl:variable name="stepDivClass">
            <xsl:choose>
                <xsl:when test="ancestor::step">
                    <xsl:value-of select="'stepmacro'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'step'"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="stepId">
            <xsl:value-of select="@tokenId"/>
        </xsl:variable>

        <div class="{$stepDivClass}">
            <xsl:variable name="stepClass">
                <xsl:choose>
                    <xsl:when test="@endState='PASSED'">
                        <xsl:value-of select="'GREEN'"/>
                    </xsl:when>
                    <xsl:when test="@endState='FAILED'">
                        <xsl:value-of select="'RED'"/>
                    </xsl:when>
                    <xsl:when test="@endState='UNDEFINED'">
                        <xsl:value-of select="'RED'"/>
                    </xsl:when>
                    <xsl:when test="@endState='PENDING'">
                        <xsl:value-of select="'ORANGE'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="'GREY'"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <!-- calculate space indentation to use for step macro steps, based on the step nesting level, up to 8 levels should be plenty -->
            <xsl:variable name="stepIndent" select="substring('&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;', 1, count(ancestor::step) * 2)"/>

            <span class="step{$stepClass}">
                <span class="stepText"><xsl:value-of select="$stepIndent"/><xsl:value-of select="@type"/>&#160;<xsl:value-of select="@action"/></span>
                <span class="stepMessage"><xsl:value-of select="@message"/>
                    <xsl:if test="step">
                        <a href="javascript:hideOrShowDiv('stepMacroBody{$stepId}', 'stepMacroShowHide{$stepId}')">
                            <img id="stepMacroShowHide{$stepId}" src="expand.png" class="expandContractImage"/>
                        </a>
                    </xsl:if>
                </span>
                <span class="endState"><xsl:value-of select="@endState"/></span>
                <span class="timeTaken">(<xsl:value-of select="@timeTakenSeconds"/>s)</span>
            </span>

            <xsl:if test="step">
                <div id="stepMacroBody{$stepId}" class="divHidden">
                    <xsl:apply-templates select="step"/>
                </div>
            </xsl:if>
        </div>
    </xsl:template>

</xsl:stylesheet>