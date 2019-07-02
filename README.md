## 发送邮件脚本

#### 1. 发送文本邮件

```shell
java -cp script-mail.jar com.script.mail.TextMailUtil email.txt
java -cp script-mail.jar com.script.mail.TextMailUtil -mailPath email.txt
```

##### 支持的参数
-mailPath 发送邮件模板（缩进只能使用空格，命令行配置项会覆盖模板内信息）
​	如果模板配置content有自定义参数（${key}），也可通过“-key value”在命令行进行赋值替换

#### 2. 发送带附件文本邮件

```shell
java -cp script-mail.jar com.script.mail.AttachsMailUtil email.txt attach1.txt,attach2.txt
java -cp script-mail.jar com.script.mail.AttachsMailUtil -mailPath email.txt -attachs attach1.txt,attach2.txt
```

##### 支持的参数
-mailPath 发送邮件模板（缩进只能使用空格，命令行配置项会覆盖模板内信息）
​	如果模板配置content有自定义参数（${key}），也可通过“-key value”在命令行进行赋值替换
-attachs 邮件附件，多个以英文逗号隔开（如果mailPath采用“-key value”的格式配置，附件配置必须也采用这种格式，即附件配置格式必须和邮件模板配置格式一样）

#### 公共配置项，如果配置会覆盖mailPath中的对应项
-subject 邮件标题
-toUser 接收者
-ccUser 抄送者
-sccUser 密送
-content 内容

> 另外，多个邮箱直接一律用英文逗号隔开。