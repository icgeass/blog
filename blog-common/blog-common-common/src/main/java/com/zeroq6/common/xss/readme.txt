web.xml 配置
    <!--XssFilter-->
	<filter>
		<filter-name>XssFilter</filter-name>
		<filter-class>${groupId}.common.xss.XssFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>XssFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>