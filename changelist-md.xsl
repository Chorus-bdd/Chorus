<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/changes">

        <xsl:apply-templates select="version"/>

    </xsl:template>

	<xsl:template match="version">
        
### Version <xsl:value-of select="versionNumber"/> ###
        
*<xsl:value-of select="comment"/>*
        
| Changes in <xsl:value-of select="versionNumber"/> |
| ------ |<xsl:apply-templates select="change"/><xsl:apply-templates select="bugfix"/>
    </xsl:template>

	<xsl:template match="change">
| <xsl:value-of select="@desc"/> |</xsl:template>

    <xsl:template match="bugfix">
| BUGFIX: <xsl:value-of select="@desc"/> |</xsl:template>

</xsl:stylesheet>