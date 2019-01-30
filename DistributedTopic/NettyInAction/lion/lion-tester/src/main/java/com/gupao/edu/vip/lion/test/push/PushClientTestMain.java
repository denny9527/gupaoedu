
package com.gupao.edu.vip.lion.test.push;

import com.gupao.edu.vip.lion.api.push.*;
import com.gupao.edu.vip.lion.tools.log.Logs;
import org.junit.Test;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 *
 */
public class PushClientTestMain {

    public static void main(String[] args) throws Exception {
        new PushClientTestMain().testPush();
    }

    @Test
    public void testPush() throws Exception {
        Logs.init();
        PushSender sender = PushSender.create();
        sender.start().join();
        Thread.sleep(1000);

        for (int i = 0; i < 1; i++) {

            PushMsg msg = PushMsg.build(MsgType.MESSAGE, "this a first push.");
            msg.setMsgId("msgId_" + i);

            PushContext context = PushContext.build(msg)
                    .setAckModel(AckModel.AUTO_ACK)
                    .setUserId("user-" + i)
                    .setBroadcast(false)

                    //.setTags(Sets.newHashSet("test"))
                    //.setCondition("tags&&tags.indexOf('test')!=-1")
                    //.setUserIds(Arrays.asList("user-0", "user-1"))
                    .setTimeout(2000)
                    .setCallback(new PushCallback() {
                        @Override
                        public void onResult(PushResult result) {
                            System.err.println("\n\n" + result);
                        }
                    });
            FutureTask<PushResult> future = sender.send(context);

            System.err.println("\n\n" + future.get());
        }

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(30));
    }

}