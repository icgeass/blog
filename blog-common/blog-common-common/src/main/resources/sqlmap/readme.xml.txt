    <!--！！！除了自定义的SQL，其他均不直接访问DAO层，而通过Manager层访问DAO层。
    因为框架在manager层对数据库访问有封装，简化了一些处理；并且service组织数据manager直接拿数据访问数据库，缩小事务-->

    <!--关联表查询-->

    <!--方式一（强烈不推荐，这样会在domain里面添加一个A类型的属性，字段名为a，破坏domain单表映射结构，并且如果很多这类查询，会使domain混乱；
    其他使用这个domain的人还可能认为BaseA的每次查询都是关联填充了A，但事实上可能只是其中一次sql关联填充了A；
    如果必须要包含对象，则新建domain，不要在原domain上修改）-->
    <resultMap id="aResultMap" type="com.A" autoMapping="true"  >
        <result property="fieldName" column="field_name"/>
    </resultMap>
    <!--方式二（推荐，只需xml修改，页面即可取值）-->
    <resultMap type="com.BaseA" id="selectListExtendMap">
        <id column="id" property="id"/>
        <result column="extendMap_field_name" property="extendMap.fieldName"/>
        <!--<association property="a" resultMap="aResultMap" columnPrefix="a_"/>-->
    </resultMap>
    <!-- 查询,通过条件 -->
    <select id="selectListExtend" parameterType="com.BaseA" resultMap="selectListExtendMap">
        <![CDATA[
           /*,a.field_name AS 'a_field_name'*/
           ,a.field_name AS 'extendMap_field_name'
        ]]>
    </select>