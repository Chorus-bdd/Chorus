package org.chorusbdd.chorus.handlers.remoting;

import org.chorusbdd.chorus.handlers.util.HandlerConfigFactory;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 21/09/12
 * Time: 08:51
 */
public class RemotingConfigFactory implements HandlerConfigFactory<RemotingConfig> {

    private static ChorusLog log = ChorusLogFactory.getLog(RemotingConfigFactory.class);

    public RemotingConfig createConfig(Properties p) {
        return null;
    }
//
//    for (Map.Entry<Object, Object> entry : p.entrySet()) {
//                StringTokenizer st = new StringTokenizer(entry.getKey().toString(), ".");
//                String configName = st.nextToken();
//                String valueType = st.hasMoreTokens() ? st.nextToken() : "connection";
//
//                try {
//                    RemotingConfig remotingConfig = getOrCreateRemotingConfig(configName);
//                    remotingConfig.setName(configName);
//
//                    if ("connection".equals(valueType)) {
//                        String[] vals = String.valueOf(entry.getValue()).split(":");
//                        if (vals.length != 3) {
//                            throw new ChorusException("Could not parse remoting property");
//                        }
//
//                        remotingConfig.setProtocol(vals[0]);
//                        if (!"jmx".equalsIgnoreCase(remotingConfig.getProtocol())) {
//                            log.error("At present only jmx protocol is supported for remoting");
//                            throw new ChorusException("Could not parse remoting property");
//                        }
//                        remotingConfig.setHost(vals[1]);
//                        remotingConfig.setPort(Integer.parseInt(vals[2]));
//                    } else if ("connectionAttempts".equals(valueType)) {
//                        remotingConfig.setConnectionRetryAttempts(Integer.parseInt(entry.getValue().toString()));
//                    } else if ("connectionAttemptMillis".equals(valueType)) {
//                        remotingConfig.setConnectionRetryMillis(Integer.parseInt(entry.getValue().toString()));
//                    }
//                } catch (Exception e) {
//                    log.error(String.format(
//                            "Failed to parse remoting property, key: %s, value: %s, expecting value in form protocol:host:port",
//                            configName,
//                            entry.getValue()));
//                }
//


}
