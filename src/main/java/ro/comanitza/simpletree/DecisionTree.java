package ro.comanitza.simpletree;

import java.util.List;
import java.util.Optional;

/**
 * Decision tree contract
 *
 * @author comanitza
 */
public interface DecisionTree<T> {

    /**
     *
     * Method used for learning. We will construct a decision tree based on the data provided {@link Element} list
     *
     * @param elements the elements to learn from
     * @return true if learning was done
     */
    boolean learn(final List<T> elements);

    /**
     *
     * Method to interogate the outcome for a provided {@link Element} instance
     *
     * @param element the element to query for
     * @return an optional that might contain the outcome
     */
    Optional<String> outcome(final T element);
}
