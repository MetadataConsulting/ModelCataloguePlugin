<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:mc="http://www.metadataregistry.org.uk/assets/schema/2.0/metadataregistry.xsd"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    exclude-result-prefixes="xs xd mc"
    version="2.0">
    
    <xsl:output method="xml" indent="yes" media-type="string"/> 

    <xsl:template match="/">      
        <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:UML="omg.org/UML1.3"
            xmlns:fn="http://www.w3.org/2005/xpath-functions"
            xmlns:xdt="http://www.w3.org/2005/xpath-datatypes">  
            <xs:element name="extension" type="xs:string"/>
            <xs:element name="datatypedescription" type="xs:string"/>
    
                <xsl:apply-templates select="mc:catalogue/mc:dataClass" />     
             
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
        
        <xsl:variable name="CamelCaseName">
            <xsl:call-template name="Compress">
                <xsl:with-param name="text" select="$nameValue"/>
            </xsl:call-template> 
        </xsl:variable>

                <xs:element name="DataClass">
                    <xsl:attribute name="name"><xsl:value-of select="translate($CamelCaseName,' ','')"/>
                    </xsl:attribute> 
                    <xsl:attribute name="id">
                        <xsl:attribute name="id">
                            <xsl:value-of select='replace($mcid, "^http://localhost:8080\D*/catalogue/dataClass/(\d*)$", "DataClass-$1") '/> 
                        </xsl:attribute>
                    </xsl:attribute>
                <xs:complexType >
                    <xs:all>
                        <xs:element name="DataClassContainer">
                            <xs:complexType>                              
                                <xs:sequence>
                                    <xsl:apply-templates select="$exs" />
                                    <xs:element type="xs:string">
                                        <xsl:attribute name="name">Status</xsl:attribute>
                                        <xsl:attribute name="default"><xsl:value-of select="$st"/></xsl:attribute>
                                    </xs:element>                               
                                    <xs:element type="xs:string">
                                        <xsl:attribute name="name">dataModel</xsl:attribute>
                                        <xsl:attribute name="default"><xsl:value-of select="$dm"/></xsl:attribute>
                                    </xs:element>
                                    <xsl:apply-templates select="$de" />
                                    <xsl:apply-templates select="$dc" />
                                </xs:sequence> 
                               </xs:complexType>
                            
                        </xs:element>
                    </xs:all>
                </xs:complexType>
                </xs:element>
               
         </xsl:template>
    
    <xsl:template match="mc:extensions">             
        <xs:annotation>       
            <xsl:apply-templates select="mc:extension" /> 
        </xs:annotation>
    </xsl:template>

    <xsl:template match="mc:extension">
        <xs:documentation xml:lang="en">Key=<xsl:value-of select="@key"/>:Value=<xsl:value-of select="text()"/></xs:documentation>
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
                    <xsl:attribute name="name">
                        <xsl:value-of select="translate($CamelCaseName,' ','')"/>
                    </xsl:attribute> 
                    <xsl:attribute name="id">
                        <xsl:attribute name="id">
                            <xsl:value-of select='replace($mcid, "^http://localhost:8080\D*/catalogue/dataElement/(\d*)$", "DataElement-$1") '/> 
                        </xsl:attribute>
                    </xsl:attribute>
                    <xsl:apply-templates select="$exs" />  
                        <xs:complexType>
                            <xs:all>
                                <xs:element type="xs:string">
                                    <xsl:attribute name="name">Description</xsl:attribute>
                                    <xsl:attribute name="default"><xsl:value-of select="$des"/></xsl:attribute>
                                </xs:element>
                                <xs:element type="xs:string">
                                    <xsl:attribute name="name">Status</xsl:attribute>
                                    <xsl:attribute name="default"><xsl:value-of select="$st"/></xsl:attribute>
                                </xs:element>
                                <xs:element type="xs:string">
                                    <xsl:attribute name="name">DataModel</xsl:attribute>
                                    <xsl:attribute name="default"><xsl:value-of select="$dm"/></xsl:attribute>
                                </xs:element>
                                <xs:element type="xs:string">
                                    <xsl:attribute name="name">ID</xsl:attribute>
                                    <xsl:attribute name="default"><xsl:value-of select="$mcid"/></xsl:attribute>
                                </xs:element>
                                <xs:element>
                                    <xsl:attribute name="name">DataTypeDescription</xsl:attribute> 
                                    <xsl:apply-templates select="$dt" />  
                                </xs:element>
                            </xs:all>
                        </xs:complexType>                         
                    </xs:element>

            
    </xsl:template>
    
    <xsl:template match="mc:dataType">
        <xsl:variable name="mcid" select="@id"/>
        <xsl:variable name="st" select="@status"/>
        <xsl:variable name="dm" select="@dataModel"/>
        <xsl:variable name="type" select="if (child::rule eq 'date(&quot;yyyy-MM-dd&quot;)' ) then 'xs:date' else 'xs:string'"/>
         
        <xs:simpleType>
                <xs:annotation>
                    <xs:documentation xml:lang="en">
                        Status=<xsl:value-of select="$st"/> 
                    </xs:documentation>
                    <xs:documentation xml:lang="en">
                        DataModel=<xsl:value-of select="$dm"/>
                    </xs:documentation>
                    <xs:documentation xml:lang="en">
                        ID=<xsl:value-of select="$mcid"/>
                    </xs:documentation>
                </xs:annotation>
                <xs:restriction>
                    <xsl:attribute name="base">
                        <xsl:value-of select="if (child::rule eq 'date(&quot;yyyy-MM-dd&quot;)' ) then 'xs:date' else 'xs:string'"/> 
                    </xsl:attribute>
                </xs:restriction>
         </xs:simpleType>   
    </xsl:template>
    
    <xsl:template match="mc:rule">
        
    </xsl:template>
    
    <xsl:template match="mc:enumerations">
        <xs:element>
            <xsl:apply-templates select="mc:enumeration" />  
        </xs:element>
    </xsl:template>
    
    <xsl:template match="mc:enumeration">
        <xs:element>
            <xsl:attribute name="value">
                <xsl:value-of select="@key"/>
            </xsl:attribute>
            <xsl:attribute name="id">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
            <xsl:if test="text()">
                <xsl:attribute name="value">
                    <xsl:value-of select="."/>
                </xsl:attribute>  
            </xsl:if>
        </xs:element>
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
    
    
    
    <xsl:template match="node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
</xsl:stylesheet>