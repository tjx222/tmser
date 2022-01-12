package com.tmser.core;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.tmser.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

public class ServerManager implements ServerManagement, ServiceRecycle{
    private static final Logger logger = LoggerFactory.getLogger(ServerManager.class);
    private static final Supplier<ServerManagement> holder = Suppliers.memoize(new Supplier() {
        public ServerManagement get() {
            return (ServerManagement) ServiceFinder.getService(ServerManagement.class);
        }
    });
    private static final AtomicReference<ServerManager> instance = new AtomicReference();
    private final ArrayList<ServiceRecycle> recycles = new ArrayList();
    private final ServerContext context;

    public static ServerManager getInstance() {
        holder.get();
        return instance.get();
    }

    public ServerManager() {
        if(!instance.compareAndSet(null, this)) {
            throw new IllegalStateException("ServerManager只能被初始化一次.");
        } else {
            this.context = (new ServerContextInitializer()).getContext();
            this.logAppConfig();
        }
    }

    private void logAppConfig() {
        AppConfig config = this.context.getAppConfig();
        LogUtil.log();
        LogUtil.log(new Object[] { "APP CONFIG" });
        LogUtil.log();
        LogUtil.log(new Object[]{"Organization    : ", config.getOrganization()});
        LogUtil.log(new Object[]{"AppParent Name  : ", config.getParentName()});
        LogUtil.log(new Object[]{"App Name        : ", config.getName()});
        LogUtil.log(new Object[]{"Server Type     : ", config.getServer().getType()});
        LogUtil.log(new Object[]{"Author          : ", config.getAuthor()  });
        LogUtil.log(new Object[]{"AuthorMobile    : ", config.getAuthorMobile()  });
        LogUtil.log(new Object[]{"Department      : ", config.getDepartment()  });
        LogUtil.log();
    }


    public void addServiceRecycle(ServiceRecycle recycle) {
        if(recycle != null) {
            synchronized(this.recycles) {
                this.recycles.add(recycle);
            }
        }
    }

    public void destroy() {
        ArrayList list;
        synchronized(this.recycles) {
            list = new ArrayList(this.recycles);
            this.recycles.clear();
        }

        Collections.reverse(list);
        Iterator i$1 = list.iterator();

        while(i$1.hasNext()) {
            ServiceRecycle rc = (ServiceRecycle)i$1.next();

            try {
                rc.destroy();
            } catch (Exception var5) {
                logger.info("组件[{}]销毁过程中出现异常", rc, var5);
            }
        }

    }

    public static int getPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return Integer.valueOf(name.substring(0, name.indexOf(64)));
    }


    public AppConfig getAppConfig() {
        return this.context.getAppConfig();
    }


    public static class Tool {
        public Tool() {
        }

        public static AppConfig getAppConfig() {
            return ServerManager.getInstance().getAppConfig();
        }
    }
}
