package org.chorusbdd.chorus.results;

/**
 * User: nick
 * Date: 31/12/12
 * Time: 01:12
 *
 * Most tokens (apart from StepToken) have only three possible end states Pass, Pending or Fail
 * It's helpful to implement general handling for this and so helpful for these nodes to share an interface
 */
public interface PassPendingFailToken extends Token {

    EndState getEndState();
}
