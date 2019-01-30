
package com.gupao.edu.vip.lion.core.handler;

import com.google.common.base.Strings;
import com.gupao.edu.vip.lion.common.router.RemoteRouter;
import com.gupao.edu.vip.lion.core.LionServer;
import com.gupao.edu.vip.lion.tools.Jsons;
import com.gupao.edu.vip.lion.tools.common.Profiler;
import com.gupao.edu.vip.lion.tools.config.CC;
import com.gupao.edu.vip.lion.tools.config.ConfigTools;
import com.typesafe.config.ConfigRenderOptions;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@ChannelHandler.Sharable
public final class AdminHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminHandler.class);

    private static final String EOL = "\r\n";

    private final LocalDateTime startTime = LocalDateTime.now();

    private final Map<String, OptionHandler> optionHandlers = new HashMap<>();

    private final OptionHandler unsupported_handler = (_1, _2) -> "unsupported option";

    private final LionServer lionServer;

    public AdminHandler(LionServer lionServer) {
        this.lionServer = lionServer;
        init();
    }

    public void init() {
        register("help", (ctx, args) ->
                "Option                               Description" + EOL +
                        "------                               -----------" + EOL +
                        "help                                 show help" + EOL +
                        "quit                                 exit console mode" + EOL +
                        "shutdown                             stop lion server" + EOL +
                        "restart                              restart lion server" + EOL +
                        "zk:<redis, cs ,gs>                   query zk node" + EOL +
                        "count:<conn, online>                 count conn num or online user count" + EOL +
                        "route:<uid>                          show user route info" + EOL +
                        "push:<uid>, <msg>                    push test msg to client" + EOL +
                        "conf:[key]                           show config info" + EOL +
                        "monitor:[mxBean]                     show system monitor" + EOL +
                        "profile:<1,0>                        enable/disable profile" + EOL
        );

        register("quit", (ctx, args) -> "have a good day!");

        register("shutdown", (ctx, args) -> {
            new Thread(() -> System.exit(0)).start();
            return "try close connect server...";
        });

        register("count", (ctx, args) -> {
            switch (args) {
                case "conn":
                    return lionServer.getConnectionServer().getConnectionManager().getConnNum();
                case "online": {
                    return lionServer.getRouterCenter().getUserEventConsumer().getUserManager().getOnlineUserNum();
                }

            }
            return "[" + args + "] unsupported, try help.";
        });

        register("route", (ctx, args) -> {
            if (Strings.isNullOrEmpty(args)) return "please input userId";
            Set<RemoteRouter> routers = lionServer.getRouterCenter().getRemoteRouterManager().lookupAll(args);
            if (routers.isEmpty()) return "user [" + args + "] offline now.";
            return Jsons.toJson(routers);
        });

        register("conf", (ctx, args) -> {
            if (Strings.isNullOrEmpty(args)) {
                return CC.cfg.root().render(ConfigRenderOptions.concise().setFormatted(true));
            }
            if (CC.cfg.hasPath(args)) {
                return CC.cfg.getAnyRef(args).toString();
            }
            return "key [" + args + "] not find in config";
        });

        register("profile", (ctx, args) -> {
            if (args == null || "0".equals(args)) {
                Profiler.enable(false);
                return "Profiler disabled";
            } else {
                Profiler.enable(true);
                return "Profiler enabled";
            }
        });
    }


    private void register(String option, OptionHandler handler) {
        optionHandlers.put(option, handler);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {
        String option = "help";
        String arg = null;
        String[] args = null;
        if (!Strings.isNullOrEmpty(request)) {
            String[] cmd_args = request.split(" ");
            option = cmd_args[0].trim().toLowerCase();
            if (cmd_args.length == 2) {
                arg = cmd_args[1];
            } else if (cmd_args.length > 2) {
                args = Arrays.copyOfRange(cmd_args, 1, cmd_args.length);
            }
        }
        try {
            Object result = optionHandlers.getOrDefault(option, unsupported_handler).handle(ctx, arg);
            ChannelFuture future = ctx.writeAndFlush(result + EOL + EOL);
            if (option.equals("quit")) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        } catch (Throwable throwable) {
            ctx.writeAndFlush(throwable.getLocalizedMessage() + EOL + EOL);
            StringWriter writer = new StringWriter(1024);
            throwable.printStackTrace(new PrintWriter(writer));
            ctx.writeAndFlush(writer.toString());
        }
        LOGGER.info("receive admin command={}", request);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.write("Welcome to Lion Console [" + ConfigTools.getLocalIp() + "]!" + EOL);
        ctx.write("since " + startTime + " has running " + startTime.until(LocalDateTime.now(), ChronoUnit.HOURS) + "(h)" + EOL + EOL);
        ctx.write("It is " + new Date() + " now." + EOL + EOL);
        ctx.flush();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    public interface OptionHandler {
        Object handle(ChannelHandlerContext ctx, String args) throws Exception;
    }
}
