这个项目帮助开发者学习如何使用CSE开发完整的微服务。 对应的指导文档参考：https://java.huaweicse.com/zhuan-ti-wen-zhang/shi-yong-cse-kai-fa-wei-fu-wu-ying-yong.html


这个分支对EdgeService demo做了改造，用于对接[https://github.com/yhs0092/javachassis-demo-loadbalance-isolation](https://github.com/yhs0092/javachassis-demo-loadbalance-isolation)中的demo，改造点包括：
1. 去掉handler链中的auth，不使用登录认证功能（sessionId功能）
2. 去掉上传文件缓存目录的配置
3. 去掉HTTPS配置
4. 在SPI加载配置文件中去掉`com.huawei.cse.porter.gateway.UiDispatcher`的配置<br/>
这个Dispatcher原本是用来给web微服务转发请求的，在本demo中用不到，可以看到这个Dispatcher和ApiDispatcher不同，后者用于将请求转发给CSE框架的微服务

## 附加说明

### 调用服务

#### 直接调用client端

POST  localhost:18080/clientconsole/startRequest
<br/>
body：
```
{
	"name":"hehe",
	"number":20,
	"times":10,
	"interval":0
}
```

#### 直接调用server端

PUT  localhost:18081/hello/sayHello?number=10
<br/>
body:
```
{
	"index":1,
	"name":"ha?"
}
```

#### 通过EdgeService调client

POST  localhost:9090/api/loadbalance-isolation-client/clientconsole/startRequest
<br/>
body：
```
{
	"name":"hehe",
	"number":20,
	"times":10,
	"interval":0
}
```

#### 通过EdgeService调server

PUT  localhost:9090/api/loadbalance-isolation-server/hello/sayHello?number=10
<br/>
body:
```
{
	"index":1,
	"name":"ha?"
}
```


### 限流

对edgeService下发动态配置：
```
cse.flowcontrol.Consumer.qps.enabled=true
cse.flowcontrol.Consumer.qps.limit.loadbalance-isolation-server.hello.sayHello=2
```
即可把EdgeService调用loadbalance-isolation-server的QPS设置为2