# hotX
热部署、开发环境高效问题排查

### 安装与启动

1. 下载安装idea插件：http://076100.oss-cn-hangzhou.aliyuncs.com/hotX/fastdev.jar
2. 登陆到目标应用服务器，执行以下命令安装hotX：curl -sLk http://076100.oss-cn-hangzhou.aliyuncs.com/hotX/install.sh | sh

3. 安装完成后执行 ./hotX.sh （需要admin权限）
```
[ziqi.gzq@stationmobile010176072182.pre.et2 /home/ziqi.gzq]
$./hotX.sh
curl: (7) couldn't connect to host
[sudo] password for ziqi.gzq:
226406
```

看到最后那串数字，表示hotX启动成功，到此hotX安装和启动全部完成


### 功能简介
**热部署**

1. 在idea编辑器，右键点击相应类文件，选择Send To Server，等待代码编译完成并弹出对话框
2. 如代码编译失败可以尝试先build整个工程，成功后重试步骤1
3. 在弹出对话框的Server Url一栏，输入http://ip|host:8080
```
对于预发环境ip|host:8080需要换成已配置域名：
110.75.98.154 stationplatform.hot.taobao.com
110.75.98.154 stationmobile.hot.taobao.com
110.75.98.154 poststation.hot.taobao.com
110.75.98.154 stationconsole.hot.cainiao-inc.com
例如 http://stationplatform.hot.taobao.com
```
4. 在Ext Options 一栏，输入option=hotswap
5. 点击OK，idea消息框中出现hotswap --> [ok] 表示热部署成功

---

**执行一段test脚本**
```
@Resource
StationStationService stationService;
public void test(String[] args) {
    //do something
    //stationService.query();
}
```
1. 编写一个名为test，入参为String[] 的方法
2. 其他步骤类似热部署，在Ext Options 一栏，输入option=runtest

---


**热部署vm文件**

1. 在idea编辑器，右键点击相应vm文件，选择Send To Server，等待弹出对话框
2. 在Ext Options 一栏，输入option=savefile


