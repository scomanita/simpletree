package ro.comanitza.simpletree;

import java.util.*;


/**
 *
 * Default implementation of the {@link DecisionTree} contract
 *
 * @author comanitza
 */
public class DecisionTreeImpl<T extends Element> extends DecisionTreeBase<T> {

    private String classFieldName;

    private Node root;

    @Override
    public boolean learn (final List<T> elements) {

        if (elements == null || elements.isEmpty()) {
            return false;
        }

        /*
         * elect the class field name for the {@link Element} implementation
         */
        classFieldName = getClassFieldName (elements.get(0).getClass())
                .orElseThrow(
                        () -> new RuntimeException("Provided class " + elements.get(0).getClass() + " is missing class field.")
                    );

        try {
            root = generateRoot(elements);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }

        return true;
    }

    @Override
    public Optional<String>  outcome (final T element) {
        return outcomeBase (root, element);
    }

    /**
     *
     * Base method for obtaining the outcome
     *
     * @param node the node to start from, should be root in most cases
     * @param t the element to test for
     * @return an optional that will containg the outcome
     */
    private Optional<String> outcomeBase (final Node node, final Element t) {

        if (node == null) {
            return Optional.empty();
        }

        if (node.getChildren() == null || node.getChildren().isEmpty()) {
            return Optional.ofNullable(node.getTransportation());
        }

        String nodeFieldName = node.getFieldName();

        String fieldValue = null;

        try {
            fieldValue = getValueFromInstance(nodeFieldName, String.class, t); //fix this!
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }

        return outcomeBase(node.getChildren().get(fieldValue), t);
    }

    /**
     *
     * Method to generate root node from the provided elements
     *
     * @param elements the elements used to
     * @return the created node
     * @throws Throwable generic error/exception
     */
    private Node generateRoot (final List<T> elements) throws Throwable {

        return generateRootNode(elements, classFieldName, Collections.singletonList(classFieldName));
    }

    /**
     *
     * Method to actually generate the root node
     *
     * @param elements the elements for which to create the root node
     * @param classFieldName the anchor field, this is the
     * @param omittedFields the fields to omit from the calculation
     * @return the created root node
     * @throws Throwable generic exception/error
     */
    private Node generateRootNode (final List<T> elements, final String classFieldName, final List<String> omittedFields) throws Throwable {

        Node parent = new Node ();

        /*
         * calculate the Gini index for the provided elements
         */
        double fullTableGini = calculateGiniIndex(elements, classFieldName);

        List<InformationGainPair> informationGains = new LinkedList<>();

        /*
         * get the info gains for all the firlds, taking into account the omitted fields
         */
        for (String attributeName: getAttributesNames(elements.get(0).getClass(), omittedFields)) {
            informationGains.add(new InformationGainPair(attributeName, (fullTableGini - calculateSubTableGiniIndex(elements, attributeName, classFieldName))));
        }

        /*
         * get the max info gain, which our best option
         */
        Optional<InformationGainPair> maxGainNodeOptional = informationGains.stream().max(Comparator.comparing(InformationGainPair::getInformationGain));

        /*
         * should not really happen
         */
        InformationGainPair maxGainNode = maxGainNodeOptional.orElseThrow(() -> new RuntimeException("Could not compute root"));

        /*
         * set field name of the root
         */
        parent.setFieldName(maxGainNode.getFieldName());

        /*
         * create the children of the root
         */
        parent.setChildren(generateChildrenNode(elements, classFieldName, new LinkedList<>() {{this.add(maxGainNode.getFieldName()); this.addAll(omittedFields); }}, maxGainNode.getFieldName()));

        return parent;
    }
    
    /**
     *
     * Method to generate a map of children
     *
     * @param elements the elements to work upon
     * @param classFieldName the class field name
     * @param omittedFields the fields to omit
     * @param parentFieldName the parent field name
     * @return the create children map
     * @throws Throwable generic exception/error
     */
    private Map<String, Node> generateChildrenNode (final List<T> elements, final String classFieldName, final List<String> omittedFields, final String parentFieldName) throws Throwable {

        Map<String, Node> nodes = new HashMap<>();

        Map<String, List<T>> data = new HashMap<>();

        /*
         * make a map of the values for the provided parent field name to the list of elements that have that value
         *
         * for fields "cost" we have the value "low", "mid", "high", we will map each value to the sub list of elements containing it
         */
        for (T t: elements) {
            String classValue = String.class.cast(getValueFromInstance (parentFieldName, String.class, t));

            if (!data.containsKey(classValue)) {
                data.put(classValue, new ArrayList<>() {{ this.add(t); }});
            } else {
                data.get(classValue).add(t);
            }
        }

        for (Map.Entry<String, List<T>> e: data.entrySet()) {

            /*
             * if it's a pure class based on the targeted field and the elements list, add it as a child
             */
            if (isPureClass(e.getValue(), classFieldName)) {
                nodes.put(e.getKey(), new Node(getValueFromInstance(classFieldName, String.class, e.getValue().get(0))));
            } else {

                /*
                 * if not a pure class, continue splitting
                 */
                Optional<String> bestInfoGainFieldOptional = findBestInformationGain (e.getValue(), omittedFields);

                if (!bestInfoGainFieldOptional.isPresent()) {
                    continue;
                }

                /*
                 * calls itself recursively to fetch all the nodes
                 */
                Node localNode = new Node();
                localNode.setFieldName(bestInfoGainFieldOptional.get());
                localNode.setChildren(generateChildrenNode (e.getValue(), classFieldName, omittedFields, bestInfoGainFieldOptional.get()));
                nodes.put(e.getKey(), localNode);
            }
        }

        return nodes;
    }

    /**
     *
     * Method to calculate the field that will yield the best information gain
     *
     * @param elements the elements from which to elect the best info gain
     * @param omittedFields the list of fields that should be omitted from the info gain election
     * @return an optional that will contain the best info gain field name
     * @throws Throwable generic exception/error
     */
    private Optional<String> findBestInformationGain (final List<T> elements, final List<String> omittedFields) throws Throwable {

        /*
         * the Gini index based on the class field of the element for the provided elements list
         */
        double subTableGini = calculateGiniIndex(elements, classFieldName);
        InformationGainPair informationGainPair = null;

        /*
         * cycle all the fields (taking into account omissions) and gets the field name with yieleded the best info gain
         */
        for (String attributeName: getAttributesNames (elements.get(0).getClass(), omittedFields)) {

            double localGiniSum = 0;

            /*
             * split the fields values
             */
            Map<String, List<T>> data = splitListToMap(elements, attributeName);

            for (Map.Entry<String, List<T>> entry: data.entrySet()) {

                /*
                 * calculate the sum of all the Gini values for all the attributes of the field
                 */
                localGiniSum += (calculateSubTableGiniIndex(elements, attributeName, classFieldName) * entry.getValue().size() / elements.size());
            }

            double infoGain = (subTableGini - localGiniSum);

            if (informationGainPair == null || infoGain > informationGainPair.getInformationGain()) {
                informationGainPair = new InformationGainPair(attributeName, infoGain);
            }
        }

        /*
         * return the best info gain
         */
        return (informationGainPair != null) ? Optional.of(informationGainPair.getFieldName()) : Optional.empty();
    }
}
