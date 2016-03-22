package zj.zfenlly.bluetooth;

import android.os.Message;

import zj.zfenlly.state.State;
import zj.zfenlly.state.StateMachine;


public class HelloWorld extends StateMachine {
    HelloWorld(String name) {
        super(name);
        addState(mState1);
        setInitialState(mState1);
    }

    public static HelloWorld makeHelloWorld() {
        HelloWorld hw = new HelloWorld("helloworld");
        hw.start();
        return hw;
    }

    class State1 extends State {
        public boolean processMessage(Message message) {
            log("Hello World");
            return HANDLED;
        }
    }
    State1 mState1 = new State1();
}

//void testHelloWorld() {
    
//}
