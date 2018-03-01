package com.huawei.cse.porter.gateway;

import java.util.Map;

import org.apache.servicecomb.edge.core.AbstractEdgeDispatcher;
import org.apache.servicecomb.edge.core.EdgeInvocation;

import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CookieHandler;

public class ApiDispatcher extends AbstractEdgeDispatcher {
    @Override
    public int getOrder() {
        return 10002;
    }

    @Override
    public void init(Router router) {
        String regex = "/api/([^\\/]+)/(.*)";
        router.routeWithRegex(regex).handler(CookieHandler.create());
        router.routeWithRegex(regex).handler(createBodyHandler());
        router.routeWithRegex(regex).failureHandler(this::onFailure).handler(this::onRequest);
    }

    protected void onRequest(RoutingContext context) {
        Map<String, String> pathParams = context.pathParams();
        String microserviceName = pathParams.get("param0");
        String path = "/" + pathParams.get("param1");

        EdgeInvocation invoker = new EdgeInvocation() {
            // 认证鉴权：构造Invocation的时候，设置会话信息。如果是认证请求，则添加Cookie。
            protected void createInvocation(Object[] args) {
                super.createInvocation(args);
                // 既从cookie里面读取会话ID，也从header里面读取，方便各种独立的测试工具联调
                String sessionId = context.request().getHeader("session-id");
                if (sessionId != null) {
                    this.invocation.addContext("session-id", sessionId);
                } else {
                    Cookie sessionCookie = context.getCookie("session-id");
                    if (sessionCookie != null) {
                        this.invocation.addContext("session-id", sessionCookie.getValue());
                    }
                }
            }
            // //服务端生成cookie的逻辑。这里的实现先使用js生成cookie
            //            protected void sendResponse(Response response) throws Exception {
            //                String sessionId = this.invocation.getContext("session-id");
            //                if (sessionId != null) {
            //                    Cookie sessionCookie = Cookie.cookie("session-id", sessionId);
            //                    // set cookie properties
            //                    sessionCookie.setMaxAge(10 * 60 * 1000);
            //                    context.addCookie(sessionCookie);
            //                }
            //                super.sendResponse(response);
            //            }
        };
        invoker.init(microserviceName, context, path, httpServerFilters);
        invoker.edgeInvoke();
    }
}
