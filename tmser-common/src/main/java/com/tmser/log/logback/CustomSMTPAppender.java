package com.tmser.log.logback;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.net.SMTPAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.helpers.CyclicBuffer;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.tmser.util.AppUtils;
import com.tmser.util.DateUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by cool.chen on 2017/8/26 16:54.
 * modify history:
 */
public class CustomSMTPAppender extends SMTPAppender {
    private static Supplier<String> title = Suppliers.memoize(new Supplier() {
        public String get() {
            return AppUtils.getAppName() + "(" + AppUtils.runEvn + ")@" + AppUtils.getLocalHostName() + " [ " + AppUtils.getLocalHostAddress() + " ] ";
        }
    });
    // static final String DEFAULT_SUBJECT_PATTERN = "%logger{20} - %m";
    static final long MAX_DELAY_BETWEEN_STATUS_MESSAGES = 1228800 * CoreConstants.MILLIS_IN_ONE_SECOND;
    long delayBetweenStatusMessages = 300 * CoreConstants.MILLIS_IN_ONE_SECOND;
    long lastTrackerStatusPrint = 0;
    private int errorCount = 0;

    private Object lock = new Object();

    public CustomSMTPAppender() {
    }

    @Override
    protected Layout<ILoggingEvent> makeSubjectLayout(String subjectStr) {
        if (subjectStr == null) {
            subjectStr = "%logger{20} - %m";
        }

        CustomSMTPAppender.SubjectLayout pl = new CustomSMTPAppender.SubjectLayout();
        pl.setContext(this.getContext());
        pl.setPattern(subjectStr);
        pl.setPostCompileProcessor(null);
        pl.start();
        return pl;
    }

    protected void append(ILoggingEvent eventObject) {

        if (!checkEntryConditions()) {
            return;
        }

        String key = discriminator.getDiscriminatingValue(eventObject);
        long now = DateUtils.now();
        final CyclicBuffer<ILoggingEvent> cb = cbTracker.getOrCreate(key, now);
        subAppend(cb, eventObject);
        try {
            if (eventEvaluator.evaluate(eventObject)) {
                // clone the CyclicBuffer before sending out asynchronously
                CyclicBuffer<ILoggingEvent> cbClone = new CyclicBuffer<ILoggingEvent>(cb);
                // see http://jira.qos.ch/browse/LBCLASSIC-221
                cb.clear();

                if (isAsynchronousSending()) {
                    // perform actual sending asynchronously
                    SenderRunnable senderRunnable = new SenderRunnable(cbClone, eventObject);
                    context.getScheduledExecutorService().execute(senderRunnable);
                } else {
                    // synchronous sending
                    sendBuffer(cbClone, eventObject);
                }
            }
        } catch (EvaluationException ex) {
            errorCount++;
            if (errorCount < CoreConstants.MAX_ERROR_COUNT) {
                addError("SMTPAppender's EventEvaluator threw an Exception-", ex);
            }
        }

        // immediately remove the buffer if asked by the user
        if (eventMarksEndOfLife(eventObject)) {
            cbTracker.endOfLife(key);
        }
        cbTracker.removeStaleComponents(now);
        if (lastTrackerStatusPrint + delayBetweenStatusMessages < now) {
            addInfo("SMTPAppender [" + name + "] is tracking [" + cbTracker.getComponentCount() + "] buffers");
            lastTrackerStatusPrint = now;
            // quadruple 'delay' assuming less than max delay
            if (delayBetweenStatusMessages < MAX_DELAY_BETWEEN_STATUS_MESSAGES) {
                delayBetweenStatusMessages *= 4;
            }
        }
    }

    class SenderRunnable implements Runnable {

        final CyclicBuffer<ILoggingEvent> cyclicBuffer;
        final ILoggingEvent e;

        SenderRunnable(CyclicBuffer<ILoggingEvent> cyclicBuffer, ILoggingEvent e) {
            this.cyclicBuffer = cyclicBuffer;
            this.e = e;
        }

        public void run() {
            synchronized (lock) {
                sendBuffer(cyclicBuffer, e);
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException ignored) {

                }
            }
        }
    }

    private static AtomicInteger counter = new AtomicInteger(0);

    class SubjectLayout extends PatternLayout {
        SubjectLayout() {
        }

        public String doLayout(ILoggingEvent event) {
            return CustomSMTPAppender.title.get() + " " + counter.incrementAndGet();
        }
    }
}
