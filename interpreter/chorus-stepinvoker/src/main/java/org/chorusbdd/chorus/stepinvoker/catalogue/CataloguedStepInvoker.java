package org.chorusbdd.chorus.stepinvoker.catalogue;

import java.util.Objects;

/**
 * Created by nickebbutt on 15/03/2018.
 */
public class CataloguedStepInvoker {
    
    private final String category;
    private final boolean deprecated;
    private final String pattern;
    private final long invocationCount;
    private final long cumulativeTime;
    private final long maxTime;
    private final long passCount;
    private final long failCount;

    public CataloguedStepInvoker(String category, boolean deprecated, String pattern, long invocationCount, long cumulativeTime, long maxTime, long passCount, long failCount) {
        Objects.requireNonNull(category, "category cannot be null");
        Objects.requireNonNull(pattern, "pattern cannot be null");
        
        this.category = category;
        this.deprecated = deprecated;
        this.pattern = pattern;
        this.invocationCount = invocationCount;
        this.cumulativeTime = cumulativeTime;
        this.maxTime = maxTime;
        this.passCount = passCount;
        this.failCount = failCount;
    }

    public CataloguedStepInvoker(String category, boolean deprecated, String pattern) {
        this(category, deprecated, pattern, 0,0,0,0,0);
    }

    public String getCategory() {
        return category;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public String getPattern() {
        return pattern;
    }

    public long getInvocationCount() {
        return invocationCount;
    }

    public long getCumulativeTime() {
        return cumulativeTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public long getPassCount() {
        return passCount;
    }

    public long getFailCount() {
        return failCount;
    }

    public CatalogueKey getCatalogueKey() {
        return new CatalogueKey(category, deprecated, pattern);
    }

    @Override
    public String toString() {
        return "CataloguedStepInvoker{" +
                "category='" + category + '\'' +
                ", deprecated=" + deprecated +
                ", pattern='" + pattern + '\'' +
                ", invocationCount=" + invocationCount +
                ", cumulativeTime=" + cumulativeTime +
                ", maxTime=" + maxTime +
                '}';
    }
}
