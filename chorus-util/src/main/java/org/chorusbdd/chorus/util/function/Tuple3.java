package org.chorusbdd.chorus.util.function;

/**
 * Created by nick on 12/01/15.
 */
public class Tuple3<A,B,C> {

    private A a;
    private B b;
    private C c;

    public Tuple3(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public A getOne() {
        return a;
    }

    public B getTwo() {
        return b;
    }

    public C getThree() { return c; }

    public static <A,B,C> Tuple3<A,B,C> tuple3(A a, B b, C c) {
        return new Tuple3<A,B,C>(a,b,c);
    }
}
