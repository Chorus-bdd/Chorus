<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html"/>
    <xsl:template match="/index">
        <html>
            <head>
                <META HTTP-EQUIV="Pragma" CONTENT="no-cache"/>
                <title>Chorus Web Agent - Main Index</title>
                <LINK href="testSuite.css" rel="stylesheet" type="text/css"/>
            </head>
            <body>
                <h2>Chorus WebAgent Index</h2>
                <xsl:apply-templates select="featureCache"/>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="featureCache">
        <div>
        <xsl:element name="a">
            <xsl:attribute name="href">
                <xsl:value-of select="@indexLink"/>
            </xsl:attribute>
            <xsl:value-of select="@name"/>
        </xsl:element>
        </div>
    </xsl:template>


</xsl:stylesheet>