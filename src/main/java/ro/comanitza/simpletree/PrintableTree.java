package ro.comanitza.simpletree;

/**
 *
 * Contract for a tree that is able to present it's internal state a readable string
 *
 * @author comanitza
 */
public interface PrintableTree {

    /**
     *
     * Method to display the internal state of the targeted decision tree as a string
     *
     * @return a string representing the state of the decision tree
     */
    String display ();
}
