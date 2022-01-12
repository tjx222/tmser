package com.tmser.log.logback;

import ch.qos.logback.classic.html.HTMLLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tmser.util.AppUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class HTMLWrapperLayout extends HTMLLayout {
    private HTMLWrapperLayout.WrapperHtmlStrategy wrapper = new HTMLWrapperLayout.DefaultStrategy();

    public HTMLWrapperLayout() {
    }

    public String doLayout(ILoggingEvent event) {
        StringBuilder builder = this.wrapper.wrap(super.doLayout(event));
        return builder.toString();
    }

    private class DefaultStrategy extends HTMLWrapperLayout.BaseStrategy {
        private DefaultStrategy() {
            super();
        }

        public StringBuilder wrap(String content) {
            StringBuilder builder = new StringBuilder();
            builder.append(AppUtils.getAppName()).append("@").append(this.getHostName()).append(" [ ").append(this.getHostAddress()).append(" ] ");
            return this.decorateColorFont(builder, "red", 3).append("<br><br>").append(content);
        }
    }

    private abstract class BaseStrategy implements HTMLWrapperLayout.WrapperHtmlStrategy {
        private BaseStrategy() {
        }

        protected String getHostName() {
            try {
                return InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException var2) {
                return "unknown-host";
            }
        }

        protected String getHostAddress() {
            try {
                return InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException var2) {
                return "unknown-host";
            }
        }

        protected StringBuilder decorateColorFont(StringBuilder content, String color, int fontSize) {
            StringBuilder builder = new StringBuilder();
            builder.append("<font size=\"").append(fontSize).append("\" color=\"").append(color).append("\">").append(content).append("</font>");
            return builder;
        }

        protected StringBuilder decorateStrong(StringBuilder content) {
            StringBuilder builder = new StringBuilder();
            builder.append("<strong>").append(content).append("</strong>");
            return builder;
        }
    }

    private interface WrapperHtmlStrategy {
        StringBuilder wrap(String var1);
    }
}
