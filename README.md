# blog
示例网站: https://www.6zeroq.com/


```bash

# 创建应用数据库目录
sudo mkdir -p /export/db
sudo chmod 777 -R /export/

# 下载
cd ~
git clone https://github.com/icgeass/blog.git
cd blog

# 创建sqlite数据库
sqlite3 /export/db/blog.db
sqlite> .read doc/db/sqlite/blog.table.sqlite.sql
sqlite> .read doc/db/blog.data.init.sql
sqlite> .quit


# 修改字典表中相关配置，删除不需要的配置，比如邮件接收
# 字典表中的社交信息可以写weibo, twitter, github, mail

```