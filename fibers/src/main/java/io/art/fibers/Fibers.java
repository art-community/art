package io.art.fibers;

import org.graalvm.nativeimage.*;
import org.graalvm.nativeimage.c.function.*;
import static io.art.fibers.Koishi.*;

public class Fibers {
    public static void main(String[] args) {
        koishi_coroutine_t co = koishi_create();

        koishi_init(co, 1024 * 1024 * 1024, runFiber.getFunctionPointer(), CurrentIsolate.getCurrentThread());

        koishi_resume(co, ObjectHandles.getGlobal().create(new Fiber()));

        koishi_destroy(co);
    }

    @CEntryPoint
    public static void runFiber(IsolateThread thread, ObjectHandle data) {
        Fiber f = ObjectHandles.getGlobal().get(data);
        f.run();
    }


    public static class Fiber {
        public void run() {
            System.out.println("run");
        }
    }

}
