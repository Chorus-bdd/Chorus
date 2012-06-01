package org.chorusbdd.chorus.core.interpreter.results;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 23/05/12
 * Time: 18:20
 */
public class AbstractToken {

    //The token id only needs to be unique within the context of each
    //test execution. Here it will be unique to the JVM instance which is even better
    private static final AtomicLong lastId = new AtomicLong();

    private final long tokenId;

    public AbstractToken(long tokenId) {
        this.tokenId = tokenId;
    }

    public long getTokenId() {
        return tokenId;
    }

    protected static long getNextId() {
        return lastId.incrementAndGet();
    }
}
