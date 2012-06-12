package org.chorusbdd.chorus.util;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 12/06/12
 * Time: 08:52
 *
 * All supported command line switches, argument counts and their System Property equivalents
 */
public enum CommandLineProperties {

    FEATURE_PATHS("-featurePaths", "-f", "chorusFeaturePaths", true, 1, Integer.MAX_VALUE),
    HANDLER_PACKAGES("-handlerPackages", "-h", "chorusHandlerPackages", true, 1, Integer.MAX_VALUE),
    DRY_RUN("-dryrun", "-d", "chorusDryRun", false, 0, 0),
    SHOW_SUMMARY("-showsummay", "-s", "chorusShowSummary", false, 0, 0),
    TAG_EXPRESSION("-tagExpression", "-t", "chorusTagExpression", false, 1, Integer.MAX_VALUE),
    JMX_LISTENER("-jmxListener", "-j", "chorusJmxListener", false, 1, Integer.MAX_VALUE),
    SUITE_NAME("-suiteName", "-n", "chorusSuiteName", false, 1, Integer.MAX_VALUE),
    SHOW_ERROR("-showErrorDetails", "-e", "chorusShowErrorDetails", false, 0, 0);

    private CommandLineProperties(String switchName, String switchShortName, String systemProperty, boolean mandatory, int minValues, int maxValues) {
        this.switchName = switchName;
        this.switchShortName = switchShortName;
        this.systemProperty = systemProperty;
        this.mandatory = mandatory;
        this.minValues = minValues;
        this.maxValues = maxValues;
    }

    private String switchName;
    private String switchShortName;
    private String systemProperty;
    private boolean mandatory;
    private final int minValues;
    private final int maxValues;

    public String getSwitchName() {
        return switchName;
    }

    public String getSwitchShortName() {
        return switchShortName;
    }

    public String getSystemProperty() {
        return systemProperty;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public int getMinValues() {
        return minValues;
    }

    public int getMaxValues() {
        return maxValues;
    }
}
