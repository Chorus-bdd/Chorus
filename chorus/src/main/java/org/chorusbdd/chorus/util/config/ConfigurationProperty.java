package org.chorusbdd.chorus.util.config;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 06/07/12
 * Time: 23:05
 * To change this template use File | Settings | File Templates.
 */
public interface ConfigurationProperty {

    String getSwitchName();

    String getSwitchShortName();

    String getHyphenatedSwitch();

    String getSystemProperty();

    boolean isMandatory();

    int getMinValueCount();

    int getMaxValueCount();

    String getValidatingExpression();

    String getExample();

    String getDescription();

    String[] getDefaults();

    /**
     * @return true if switchName or switchShortName matches switchName
     */
    boolean matchesSwitch(String s);
}
