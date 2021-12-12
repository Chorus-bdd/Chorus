/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows named resources to be injected into handlers by the interpreter. Supported resources are:
 * 
 * <table>
 *     <caption>ChorusResource resources by id</caption>
 * <tr>
 * <td>feature.dir</td>
 * <td>File</td>
 * <td>The directory of the feature file which is currently being executed</td>
 * </tr>
 * <tr>
 * <td>feature.file</td>
 * <td>File</td>
 * <td>The feature file which is currently being executed</td>
 * </tr>
 * <tr>
 * <td>feature.token</td>
 * <td>FeatureToken</td>
 * <td>The FeatureToken with metadata relating to the current running feature</td>
 * </tr>
 *  <tr>
 * <td>scenario.token</td>
 * <td>ScenarioToken</td>
 * <td>The ScenarioToken with metadata relating to the current running scenario</td>
 * </tr>
 * <tr>
 * <tr>
 * <td>profile</td>
 * <td>Profile</td>
 * <td>The Profile which is being used for the Chorus suite</td>
 * </tr>
 * <tr>
 * <td>subsystem.processManager</td>
 * <td>ProcessManager</td>
 * <td>Chorus' ProcessManager subsystem</td>
 * </tr> 
 * <tr>
 * <td>subsystem.remotingManager</td>
 * <td>RemotingManager</td>
 * <td>Chorus' RemotingManager subsystem</td>
 * </tr>
 * <tr>
 * <td>subsystem.configurationManager</td>
 * <td>ConfigurationManager</td>
 * <td>Chorus' ConfigurationManager subsystem</td>
 * </tr>
 * </table>
 *
 * Sometimes handlers need to be provided with state from the running interpreter, so that they can locate resources relative
 * to the feature files, for example.
 * This annotation is provided so that Handler's can annotate
 * fields with resources which will be initialized by the interpreter, and describe aspects
 * of the current test execution state, current supported values:
 *
 * feature.dir - annotation for a File field, interpreter will set this to the directory of the executing feature
 * feature.file - annotation for a File field, interpreter will set this to the executing feature file
 * feature.token - annotation for a FeatureToken field, interpreter will set this to the FeatureToken which contains details of the currently running Feature
 * scenario.token - annotation for a ScenarioToken field, interpreter will set this to the ScenarioToken which contains details of the currently running Scenario
 * subsystem.processManager - interpreter will set this to ProcessManager subsystem in use
 * subsystem.remotingManager - interpreter will set this to RemotingManager subsystem in use
 * subsystem.configurationManager - interpreter will set this to ConfigurationManager subsystem in use
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChorusResource {
    public static final String featureFile = "feature.file";
    public static final String featureDir = "feature.dir";
    public static final String featureToken = "feature.token";
    public static final String scenarioToken = "scenario.token";
    public static final String profile = "profile";
    public static final String processManager = "subsystem.processManager";
    public static final String remotingManager = "subsystem.remotingManager";
    public static final String configurationManager = "subsystem.configurationManager";

    //append Handler name to inject a handler instance
    public static final String handlerPrefix = "handler.";

    public String value() default "";
}
