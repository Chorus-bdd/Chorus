<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/changes">
		<html>
			<head>
				<META HTTP-EQUIV="Pragma" CONTENT="no-cache"/>
				<title>Chorus Changelist</title>
				<style type="text/css">
                    p {
                        font-family: verdana,arial,sans-serif;
                        font-size:12px;
                    }

                    table.changeTable {
                    	font-family: verdana,arial,sans-serif;
                    	font-size:10px;
                    	color:#333333;
                    	border-width: 1px;
                    	border-color: #999999;
                    	border-collapse: collapse;
                        width: 1024px;
                    }
                    table.changeTable th {
                    	background:#b5cfd2 url('cell-blue.jpg');
                    	border-width: 1px;
                    	padding: 8px;
                    	border-style: solid;
                    	border-color: #999999;
                        text-align: left;
                    }
                    table.changeTable td {
                    	background:#dcddc0 url('cell-grey.jpg');
                    	border-width: 1px;
                    	padding: 8px;
                    	border-style: solid;
                    	border-color: #999999;
                    }

				</style>	
			</head>
			<body>
				<xsl:apply-templates select="version"/>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="version">
        <p>
        <font size="+1">Chorus Version: <xsl:value-of select="versionNumber"/></font><br/><br/>
        <xsl:value-of select="comment"/>
        </p>
		<table class='changeTable'>
		<tr><th>Changes in <xsl:value-of select="versionNumber"/></th></tr>
		<xsl:apply-templates select="change"/>
        <xsl:apply-templates select="bugfix"/>
		</table><br/>
        <p/>
	</xsl:template>

	<xsl:template match="change">
		<tr><td>CHANGE: <xsl:value-of select="@desc"/></td></tr>
	</xsl:template>

    <xsl:template match="bugfix">
    	<tr><td>BUGFIX: <xsl:value-of select="@desc"/></td></tr>
    </xsl:template>

</xsl:stylesheet>