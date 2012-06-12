package org.chorusbdd.chorus;

import org.chorusbdd.chorus.util.config.ChorusConfig;

/**
 * Created with IntelliJ IDEA.
 * User: Nick
 * Date: 12/06/12
 * Time: 14:42
 *
 * Used to generate a new config based on a baseConfig
 *
 * This can be useful, for example, if we want to generate multiple configs from a single
 * base - in the case of the JUnit runner we have a base config which would run all features
 * we want to generate from this a config for each feature file, so that each feature file
 * can be executed in a separate interpreter run, as part of a junit suite
 */
public interface ConfigMutator {

    public static ConfigMutator NULL_MUTATOR = new ConfigMutator() {
        public ChorusConfig getNewConfig(ChorusConfig baseConfig) {
            return baseConfig.deepCopy();
        }
    };

    /**
     * @return ChorusConfig - a deep clone of the baseConfig, with some altered properties
     */
    public ChorusConfig getNewConfig(ChorusConfig baseConfig);
}
