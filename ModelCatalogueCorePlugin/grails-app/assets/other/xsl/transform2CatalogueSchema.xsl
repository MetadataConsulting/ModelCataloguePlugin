<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:mc="http://www.metadataregistry.org.uk/assets/schema/2.0/metadataregistry.xsd"
                xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
                exclude-result-prefixes="xs mc xd xsl"
                version="2.0">

    <xsl:output method="xml" indent="yes" media-type="string"/>

    <xsl:template match="/">
        <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
                   xmlns:vc='http://www.w3.org/2007/XMLSchema-versioning'
                   xmlns='https://datamodel.metadataregistry.org.uk'
                   targetNamespace='https://datamodel.metadataregistry.org.uk'
                   vc:minVersion='1.1' elementFormDefault='qualified'>

            <xsl:apply-templates select="mc:catalogue/mc:dataClass" />
            <xsl:apply-templates select="//mc:dataType"/>

        </xs:schema>
    </xsl:template>

    <xsl:template match="/mc:catalogue/mc:relationshipTypes"/>

    <xsl:template match="mc:dataClass">
        <xsl:variable name="nameValue" select="@name"/>
        <xsl:variable name="mcid" select="@id"/>
        <xsl:variable name="st" select="@status"/>
        <xsl:variable name="dm" select="@dataModel"/>
        <xsl:variable name="dc" select="mc:dataClass"/>
        <xsl:variable name="de" select="mc:dataElement"/>
        <xsl:variable name="exs" select="mc:extensions"/>
        <xsl:variable name="planName" select="test"/>
        <xsl:variable name="version" select="exs"/>
        <xsl:variable name="description" select="test"/>
        <xsl:variable name="generated" select="test"/>

        <xsl:variable name="CamelCaseName">
            <xsl:call-template name="Compress">
                <xsl:with-param name="text" select="$nameValue"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="count(ancestor::*)=1">
                <xsl:apply-templates select="$exs" />
                <!-- DataClass element -->
                <xs:element>
                    <xsl:attribute name="name"><xsl:value-of select="translate($nameValue,' ','')"/></xsl:attribute>
                    <xs:complexType >
                        <xs:sequence>
                            <xs:element name='metadata' type='metadata' minOccurs='1' maxOccurs='1' />
                        </xs:sequence>
                    </xs:complexType>
                </xs:element>
                <!-- complexType 1 -->
                <xs:complexType>
                    <xsl:attribute name="name">metadata</xsl:attribute>
                    <xs:sequence>
                        <xs:element name='schema-name' minOccurs='1' maxOccurs='1'>
                            <xs:simpleType>
                                <xs:restriction base='xs:string'>
                                    <xs:enumeration>
                                        <xsl:attribute name="value"><xsl:value-of select="translate($nameValue,' ','')"/></xsl:attribute>
                                    </xs:enumeration>
                                </xs:restriction>
                            </xs:simpleType>
                        </xs:element>
                        <xs:element name='schema-version' minOccurs='1' maxOccurs='1'>
                            <xs:simpleType>
                                <xs:restriction base='xs:string'>
                                    <xs:enumeration>
                                        <xsl:attribute name="value"><xsl:value-of select="mc:extensions/mc:extension[1]"/></xsl:attribute>
                                    </xs:enumeration>
                                </xs:restriction>
                            </xs:simpleType>
                        </xs:element>
                        <xs:element name='date' type='xs:date' minOccurs='1' maxOccurs='1'>
                            <xs:annotation>
                                <xs:documentation>
                                    <p>The date that this file was generated</p>
                                </xs:documentation>
                            </xs:annotation>
                        </xs:element>
                        <xs:element name='source-organisation' minOccurs='1' maxOccurs='1'>
                            <xs:annotation>
                                <xs:documentation>
                                    <p>source organisation</p>
                                </xs:documentation>
                            </xs:annotation>
                            <xs:simpleType>
                                <xs:restriction base='xs:string'>
                                    <xs:minLength value='1' />
                                </xs:restriction>
                            </xs:simpleType>
                        </xs:element>
                        <xs:element name='source-system' type='xs:string' minOccurs='0' maxOccurs='1'>
                            <xs:annotation>
                                <xs:documentation>
                                    <p>Source system</p>
                                </xs:documentation>
                            </xs:annotation>
                        </xs:element>
                    </xs:sequence>
                </xs:complexType>
            </xsl:when>
            <xsl:otherwise>
                <xs:complexType >
                    <!--xsl:attribute name="name"><xsl:value-of select="translate($CamelCaseName,' ','')"/></xsl:attribute-->
                    <xsl:attribute name="name"><xsl:value-of select='translate(replace(replace(@id, "^http://localhost:\d*\D*/catalogue/dataClass/(\d*)$", "placeholder-$1.2"),"placeholder",@name )," ","-")'/></xsl:attribute>
                    <xs:sequence>
                        <xsl:for-each select="child::mc:dataClass">
                            <!--xsl:variable name="dcdtName" select="mc:dataType/@name"/-->
                            <xsl:variable name="dcdtName" select='translate(replace( mc:dataType/@name," ","-"),"([)]","")'/>
                            <xs:element>
                                <!--xsl:attribute name="level"><xsl:value-of select='count(ancestor::*)'/></xsl:attribute-->
                                <xsl:attribute name="name"><xsl:value-of select='translate(replace( @name ," ","-"),"([)]*","")'/></xsl:attribute>
                                <!--xsl:attribute name="type"><xsl:value-of select='translate(replace(replace( @id, "^http://localhost:\d*\D*/catalogue/dataClass/(\d*)$", "placeholder-$1.1"),"placeholder", @name )," ","-") '/></xsl:attribute-->
                                <xsl:choose>
                                    <xsl:when test="matches($dcdtName, '^xs:*')">
                                        <xsl:attribute name="type"><xsl:value-of select='$dcdtName'></xsl:value-of></xsl:attribute>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:if test="$dcdtName">
                                            <xsl:attribute name="type"><xsl:value-of select='translate(replace(replace( @id, "^http://localhost:\d*\D*/catalogue/dataClass/(\d*)$", "placeholder-$1.2"),"placeholder", @name )," ","-") '/></xsl:attribute>
                                        </xsl:if>
                                    </xsl:otherwise>
                                </xsl:choose>
                                <xsl:if test='mc:metadata/mc:extension'>
                                    <xsl:attribute name="minOccurs">
                                        <xsl:for-each select="mc:metadata/mc:extension">
                                            <xsl:call-template name="Min" />
                                        </xsl:for-each>
                                    </xsl:attribute>
                                    <xsl:attribute name="maxOccurs">
                                        <xsl:for-each select="mc:metadata/mc:extension">
                                            <xsl:call-template name="Max" />
                                        </xsl:for-each>
                                    </xsl:attribute>
                                </xsl:if>
                            </xs:element>
                        </xsl:for-each>
                        <xsl:for-each select="child::mc:dataElement">
                            <!--xsl:variable name="dtName" select="mc:dataType/@name"/-->
                            <xsl:variable name="deName" select='translate(replace(@name," ","-"),"([)]*","")'/>
                            <xsl:variable name="delcName">
                                <xsl:call-template name="LowerCaseWord">
                                    <xsl:with-param name="text" select="$deName"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <xsl:variable name="dtName" select='translate(replace(  mc:dataType/@name," ","-"),"([)]*","")'/>
                            <xsl:variable name="dtlcName">
                                <xsl:call-template name="LowerCaseWord">
                                    <xsl:with-param name="text" select="$dtName"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <xsl:choose>
                                <xsl:when test="not($dtName)">
                                    <xsl:variable name="dtRef" select='translate(replace(  mc:dataType/@ref," ","-"),"([)]*","")'/>
                                    <xsl:variable name="dtlcRef">
                                        <xsl:call-template name="LowerCaseWord">
                                            <xsl:with-param name="text" select="$dtRef"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xs:element>
                                        <!--xsl:attribute name="level"><xsl:value-of select='count(ancestor::*)'/></xsl:attribute-->
                                        <xsl:attribute name="name"><xsl:value-of select='translate(replace( @name  ," ","-"),"([)]*","")'/></xsl:attribute>
                                        <xsl:choose>
                                            <xsl:when test="matches($dtRef, '^http://www.w3.org/2001/XMLSchema#*')">
                                                <xsl:variable name="type1">
                                                    <xsl:call-template name="string-replace-all">
                                                        <xsl:with-param name="text" select='$dtlcRef' />
                                                        <xsl:with-param name="replace" select="'http://www.w3.org/2001/xmlschema#'" />
                                                        <xsl:with-param name="by" select="'xs:'" />
                                                    </xsl:call-template>
                                                </xsl:variable>
                                                <!--xsl:attribute name="type1"><xsl:value-of select='$dtlcRef'></xsl:value-of></xsl:attribute-->
                                                <!--xsl:attribute name="type"><xsl:value-of select='replace( $dtlcRef  ,"http://www.w3.org/2001/XMLSchema#","xs:")'></xsl:value-of></xsl:attribute-->
                                                <xsl:attribute name="type"><xsl:value-of select='$type1'/></xsl:attribute>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <!--xsl:attribute name="test2"><xsl:value-of select='$dtRef'></xsl:value-of></xsl:attribute-->
                                                <xsl:if test="$dtlcRef">
                                                    <xsl:attribute name="type"><xsl:value-of select='translate(replace(replace( mc:dataType/@id, "^http://localhost:\d*\D*/catalogue/dataType/(\d*)$", "placeholder-$1.1"),"placeholder", $dtlcRef )," ","-") '/></xsl:attribute>
                                                </xsl:if>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                        <xsl:if test='mc:metadata/mc:extension'>
                                            <xsl:attribute name="minOccurs">
                                                <xsl:for-each select="mc:metadata/mc:extension">
                                                    <xsl:call-template name="Min" />
                                                </xsl:for-each>
                                            </xsl:attribute>
                                            <xsl:attribute name="maxOccurs">
                                                <xsl:for-each select="mc:metadata/mc:extension">
                                                    <xsl:call-template name="Max" />
                                                </xsl:for-each>
                                            </xsl:attribute>
                                        </xsl:if>
                                        <xs:annotation>
                                            <xs:documentation>
                                                <xsl:value-of select='mc:description'/>
                                            </xs:documentation>
                                        </xs:annotation>

                                    </xs:element>

                                </xsl:when>
                                <xsl:otherwise>
                                    <xs:element>
                                        <!--xsl:attribute name="level"><xsl:value-of select='count(ancestor::*)'/></xsl:attribute-->
                                        <xsl:attribute name="name"><xsl:value-of select='translate(replace( $delcName ," ","-"),"([)]*","")'/></xsl:attribute>
                                        <!--xsl:attribute name="test1">
                                            <xsl:value-of select='translate(replace( @name," ","-"),"([)]","")'/>
                                        </xsl:attribute-->
                                        <xsl:choose>
                                            <xsl:when test="matches($dtName, '^xs:*')">
                                                <xsl:attribute name="type"><xsl:value-of select='$dtlcName'></xsl:value-of></xsl:attribute>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <!--xsl:attribute name="test2"><xsl:value-of select='$dtName'></xsl:value-of></xsl:attribute-->
                                                <xsl:if test='mc:dataType/@id'>
                                                    <xsl:attribute name="type"><xsl:value-of select='translate(replace(replace( mc:dataType/@id, "^http://localhost:\d*\D*/catalogue/dataType/(\d*)$", "placeholder-$1.1"),"placeholder", $dtlcName )," ","-") '/></xsl:attribute>
                                                </xsl:if>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                        <xsl:if test='mc:metadata/mc:extension'>
                                            <xsl:attribute name="minOccurs">
                                                <xsl:for-each select="mc:metadata/mc:extension">
                                                    <xsl:call-template name="Min" />
                                                </xsl:for-each>
                                            </xsl:attribute>
                                            <xsl:attribute name="maxOccurs">
                                                <xsl:for-each select="mc:metadata/mc:extension">
                                                    <xsl:call-template name="Max" />
                                                </xsl:for-each>
                                            </xsl:attribute>
                                        </xsl:if>
                                        <xs:annotation>
                                            <xs:documentation>
                                                <xsl:value-of select='mc:description'/>
                                            </xs:documentation>
                                        </xs:annotation>
                                    </xs:element>
                                </xsl:otherwise>

                            </xsl:choose>

                        </xsl:for-each>
                    </xs:sequence>
                </xs:complexType>
            </xsl:otherwise>
        </xsl:choose>

        <xsl:apply-templates select="mc:dataClass" />
        <!-- End Recurse -->


    </xsl:template>

    <xsl:template name="buildComplex">
        <xsl:variable name="nameValue" select="@name"/>
        <xsl:variable name="mcid" select="@id"/>
        <xsl:variable name="st" select="@status"/>
        <xsl:variable name="dm" select="@dataModel"/>
        <xsl:variable name="dc" select="mc:dataClass"/>
        <xsl:variable name="de" select="mc:dataElement"/>
        <xsl:variable name="exs" select="mc:extensions"/>

        <xsl:variable name="CamelCaseName">
            <xsl:call-template name="Compress">
                <xsl:with-param name="text" select="$nameValue"/>
            </xsl:call-template>
        </xsl:variable>


        <xs:complexType >
            <xsl:attribute name="name"><xsl:value-of select="translate($CamelCaseName,' ','')"/></xsl:attribute>
        </xs:complexType>



    </xsl:template>

    <xsl:template name="dcLookahead">
        <xs:element>
            <xsl:attribute name="name"><xsl:value-of select='translate(replace( @name ," ","-"),"([)]","")'/></xsl:attribute>
            <xsl:attribute name="type"><xsl:value-of select='translate(replace(replace( @id, "^http://localhost:\d*\D*/catalogue/dataClass/(\d*)$", "placeholder-$1.1"),"placeholder", @name )," ","-") '/></xsl:attribute>
            <xsl:attribute name="minOccurs">
                <xsl:for-each select="mc:metadata/mc:extension">
                    <xsl:call-template name="Min" />
                </xsl:for-each>
            </xsl:attribute>
            <xsl:attribute name="maxOccurs">
                <xsl:for-each select="mc:metadata/mc:extension">
                    <xsl:call-template name="Max" />
                </xsl:for-each>
            </xsl:attribute>
        </xs:element>
    </xsl:template>

    <xsl:template name="Min">
        <xsl:param name="text"/>
        <xsl:choose>
            <xsl:when test="@key='Min Occurs'">
                <xsl:value-of select="."/>
            </xsl:when>
            <xsl:otherwise></xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="Max">
        <xsl:param name="text"/>
        <xsl:choose>
            <xsl:when test="@key='Max Occurs'">
                <xsl:value-of select="."/>
            </xsl:when>
            <xsl:otherwise></xsl:otherwise>
        </xsl:choose>
    </xsl:template>



    <xsl:template match="child::mc:dataElement">
        <xs:element>
            <xsl:attribute name="name"><xsl:value-of select='translate(replace(@name ," ","-"),"([)]","")'/></xsl:attribute>
            <!--xsl:attribute name="type"><xsl:value-of select='replace(replace(replace(mc:dataType/@id, "^http://localhost:\d*\D*/catalogue/dataType/(\d*)$", "placeholder-$1.1"),"placeholder",mc:dataType/@name )," ","-") '/></xsl:attribute-->
            <xsl:attribute name="minOccurs">1</xsl:attribute>
            <xsl:attribute name="maxOccurs">1</xsl:attribute>
            <xs:annotation>
                <xs:documentation>
                    <!--xsl:value-of select='mc:description'/-->
                </xs:documentation>
            </xs:annotation>
        </xs:element>
    </xsl:template>

    <xsl:template match="mc:extensions">
        <xs:annotation>
            <xs:documentation>
                <xsl:apply-templates select="mc:extension" />
            </xs:documentation>
        </xs:annotation>
    </xsl:template>

    <xsl:template match="mc:extension">

        <xsl:if test="@key = 'http://xsd.modelcatalogue.org/metadata#schemaName'">
            <xsl:variable name="planName" select="@key"/>
            <h1><xsl:value-of select="text()"/></h1>
        </xsl:if>
        <xsl:if test="@key = 'http://xsd.modelcatalogue.org/metadata#schemaVersion'">
            <xsl:variable name="version" select="@key"/>
            <p>Version=<xsl:value-of select="text()"/></p>
        </xsl:if>
        <xsl:if test="@key = 'http://xsd.modelcatalogue.org/metadata#schemaVersionDescription'">
            <xsl:variable name="description" select="@key"/>
            <p>Description=<xsl:value-of select="text()"/></p>
        </xsl:if>
        <xsl:if test="@key = 'http://xsd.modelcatalogue.org/metadata#study'">
            <xsl:variable name="generated" select="@key"/>
            <p>Generated=<xsl:value-of select="text()"/></p>
        </xsl:if>

    </xsl:template>

    <xsl:template match="mc:dataElement/@name" />

    <xsl:template match="mc:dataElement">

        <xsl:variable name="nameValue" select="@name"/>
        <xsl:variable name="mcid" select="@id"/>
        <xsl:variable name="st" select="@status"/>
        <xsl:variable name="dm" select="@dataModel"/>
        <xsl:variable name="des" select="description"/>
        <xsl:variable name="dt" select="dataType"/>
        <xsl:variable name="exs" select="extensions"/>
        <xsl:variable name="xxs" >FAIL</xsl:variable>
        <xsl:variable name="CamelCaseName">
            <xsl:call-template name="Compress">
                <xsl:with-param name="text" select="$nameValue"/>
            </xsl:call-template>
        </xsl:variable>


        <xs:element>
            <xsl:attribute name="name"><xsl:value-of select='translate(replace(replace(@id, "^http://localhost:\d*\D*/catalogue/dataElement/(\d*)$", "placeholder-$1.2"),"placeholder",@name )," ","-")'/></xsl:attribute>
            <!--xsl:attribute name="type"><xsl:value-of select='replace(replace(replace(mc:dataType/@id, "^http://localhost:\d*\D*/catalogue/dataType/(\d*)$", "placeholder-$1.1"),"placeholder",mc:dataType/@name )," ","-") '/></xsl:attribute-->
            <xsl:attribute name="minOccurs">1</xsl:attribute>
            <xsl:attribute name="maxOccurs">1</xsl:attribute>
            <xs:annotation>
                <xs:documentation>
                    <xsl:value-of select='mc:description'/>
                </xs:documentation>
            </xs:annotation>
            <xs:simpleType>
                <xs:restriction> <!--base="dataType"-->
                    <xsl:attribute name="base">
                        <xsl:value-of select='translate(replace(mc:dataType/@name ," ","-"),"([)]","")'/>
                    </xsl:attribute>
                    <xs:minLength value='1' />
                </xs:restriction>
            </xs:simpleType>
        </xs:element>
    </xsl:template>

    <xsl:template match="mc:dataType">
        <xsl:variable name="nameValue" select="@name"/>
        <xsl:variable name="refValue" select="@ref"/>
        <xsl:variable name="mcid" select="@id"/>
        <xsl:variable name="st" select="@status"/>
        <xsl:variable name="dm" select="@dataModel"/>
        <xsl:variable name="enums" select="mc:enumerations"/>
        <xsl:variable name="ext" select="mc:extensions"/>
        <xsl:variable name="type" select="if (child::rule eq 'date(&quot;yyyy-MM-dd&quot;)' ) then 'xs:date' else 'xs:string'"/>

        <xsl:variable name="LowerCaseName">
            <xsl:call-template name="LowerCaseWord">
                <xsl:with-param name="text" select="$nameValue"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:if test="(not(matches($nameValue, 'xs:*'))) and (not(matches($refValue, 'http://www.w3.org/2001/XMLSchema#*')))">
            <xsl:if test="$mcid">
                <xs:simpleType>
                    <xsl:attribute name="name">
                        <xsl:value-of select='translate(replace(replace($mcid, "^http://localhost:\d*\D*/catalogue/dataType/(\d*)$", "DataType-$1.1"),"DataType",$LowerCaseName),"([)]*","")'/>
                    </xsl:attribute>
                    <!--xsl:attribute name="test"> <xsl:value-of select='replace( @id, "^http://localhost:\d*\D*/catalogue/dataType/(\d*)$", "placeholder-$1.1")'/></xsl:attribute-->
                    <!--xsl:attribute name="test2"> <xsl:value-of select="$LowerCaseName" /></xsl:attribute-->
                    <!--xsl:attribute name="testRef"> <xsl:value-of select="@ref" /></xsl:attribute-->
                    <xsl:choose>
                        <xsl:when test="(child::mc:enumerations)">
                            <xsl:apply-templates select="child::mc:enumerations" />
                        </xsl:when>
                        <xsl:otherwise>
                            <xs:restriction base="xs:string">
                            </xs:restriction>
                        </xsl:otherwise>
                    </xsl:choose>
                </xs:simpleType>
            </xsl:if>
        </xsl:if>


        <xsl:apply-templates select="/mc:enumerations" />
    </xsl:template>

    <xsl:template match="mc:rule">

    </xsl:template>

    <xsl:template match="mc:enumerations">
        <xs:restriction base="xs:token">
            <xsl:apply-templates select="mc:enumeration" />
        </xs:restriction>
    </xsl:template>

    <xsl:template match="mc:enumeration">
        <xs:enumeration>
            <xsl:attribute name="value">
                <xsl:value-of select="@value"/>
            </xsl:attribute>
            <!--xsl:attribute name="id">
                <xsl:value-of select="@id"/>
            </xsl:attribute-->
            <xs:annotation>
                <xs:appinfo>
                    <display-text><xsl:value-of select="."/> </display-text>
                </xs:appinfo>
            </xs:annotation>
        </xs:enumeration>
    </xsl:template>

    <xsl:template name="Compress">
        <xsl:param name="text"/>
        <xsl:call-template name="CamelCase">
            <xsl:with-param name="text" select="translate($text,'\s-,;&amp;)(','')"/>
        </xsl:call-template>

    </xsl:template>

    <xsl:template name="CamelCase">
        <xsl:param name="text"/>
        <xsl:choose>
            <xsl:when test="contains($text,' ')">
                <xsl:call-template name="CamelCaseWord">
                    <xsl:with-param name="text" select="substring-before($text,' ')"/>
                </xsl:call-template>
                <xsl:text> </xsl:text>
                <xsl:call-template name="CamelCase">
                    <xsl:with-param name="text" select="substring-after($text,' ')"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="CamelCaseWord">
                    <xsl:with-param name="text" select="$text"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="LowerCaseWord">
        <xsl:param name="text"/>
        <xsl:value-of select="translate(substring($text,1,1),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')" />
        <xsl:value-of select="translate(substring($text,2,string-length($text)-1),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')" />
    </xsl:template>

    <xsl:template name="CamelCaseWord">
        <xsl:param name="text"/>
        <xsl:value-of select="translate(substring($text,1,1),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')" />
        <xsl:value-of select="translate(substring($text,2,string-length($text)-1),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')" />
    </xsl:template>

    <xsl:template match="@*">
        <xsl:attribute name="{name()}">
            <xsl:value-of select="translate(., ' ', '')" />
        </xsl:attribute>
    </xsl:template>

    <xsl:template name="string-replace-all">
        <xsl:param name="text" />
        <xsl:param name="replace" />
        <xsl:param name="by" />
        <xsl:choose>
            <xsl:when test="contains($text, $replace)">
                <xsl:value-of select="substring-before($text,$replace)" />
                <xsl:value-of select="$by" />
                <xsl:call-template name="string-replace-all">
                    <xsl:with-param name="text"
                                    select="substring-after($text,$replace)" />
                    <xsl:with-param name="replace" select="$replace" />
                    <xsl:with-param name="by" select="$by" />
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$text" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


    <xsl:template match="node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
