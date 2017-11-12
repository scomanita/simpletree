package ro.comanitza.simpletree;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * Base class for the {@link DecisionTree} contract
 *
 * @param <T>
 */
public abstract class DecisionTreeBase<T extends Element> implements DecisionTree <T> {

    private final MethodHandles.Lookup lookup = MethodHandles.lookup();

    /**
     *
     * Method to fetch the value for a particular field from a particular instance
     *
     * @param fieldName the name of the targeted field
     * @param type the type of the field
     * @param instance the instance to use
     * @param <U> the return type/the field type
     * @return the value of the field
     * @throws Throwable generic error/exception
     */
    protected <U> U getValueFromInstance (final String fieldName, final Class<U> type, final Element instance) throws Throwable {

        MethodHandle handle = lookup.findVirtual(instance.getClass(), composeGetterMethodName (fieldName), MethodType.methodType(type));

        return type.cast(handle.invoke(instance));
    }

    /**
     *
     * Compose the name for the standard getter method based on the provided field name
     *
     * @param fieldName the targete field name
     * @return the getter method name
     */
    private String composeGetterMethodName (final String fieldName) {
        return "get" + (fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
    }

    /**
     *
     * Determine if the provided list is pure, meaning all the instances have a particular field with the same value
     *
     * @param elements the elements to test
     * @param classFieldName the class field name
     * @return true if it's pure class
     * @throws Throwable generic exception/error
     */
    protected boolean isPureClass (final List<T> elements, final String classFieldName) throws Throwable {

        if (elements == null || elements.isEmpty()) {
            return false;
        }

        if (classFieldName == null || classFieldName.isEmpty()) {
            return false;
        }

        String classValue = getValueFromInstance (classFieldName, String.class, elements.get(0));

        return elements.stream().map((t) -> {
            try {
                return String.class.cast(getValueFromInstance (classFieldName, String.class, t));
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }).allMatch(classValue::equals);
    }

    /**
     *
     * Method for calculating Gini index
     *
     * Where px is the numberOfValue/totalNumberOfElements
     *
     * Gini = 1 - sum (p1^2, p2^2...)
     *
     * Considering 10 elements, and divided by their class value they are: 4B, 3C and 3T (a total of 10 elements)
     *
     * Gini index is: 1 - (0.4^2 + 0.3^2 + 0.3^2)
     *
     * @param elements the list of elements to calculate the Gini index for
     * @param fieldName the field name
     * @param classFieldName the class field name
     * @return the gini index
     * @throws Throwable generic error/exception
     */
    protected double calculateSubTableGiniIndex (final List<T> elements, final String fieldName, final String classFieldName) throws Throwable {

        if (elements == null || elements.isEmpty() || fieldName == null || fieldName.isEmpty() || classFieldName == null || classFieldName.isEmpty()) {
            return 0;
        }

        Map<String, Map<String, Integer>> data = new HashMap<>();

        for (Element t: elements) {

            String fieldValue = String.class.cast(getValueFromInstance(fieldName, String.class, t));
            String classValue = String.class.cast(getValueFromInstance(classFieldName, String.class, t));

            if (!data.containsKey(fieldValue)) {
                data.put(fieldValue, new HashMap<>() {
                    {
                        this.put(classValue, 1);
                    }
                });
            } else {

                if (data.get(fieldValue).containsKey(classValue)) {
                    Integer foundValue = data.get(fieldValue).get(classValue);
                    data.get(fieldValue).put(classValue, (foundValue + 1));
                } else {
                    data.get(fieldValue).put(classValue, 1);
                }
            }
        }

        double sum = 0;

        for (Map.Entry<String, Map<String, Integer>> e: data.entrySet()) {

            if (e.getValue().size() == 1) {

                continue;
            }

            double localGiniSum = 0;

            int totalRows = e.getValue().values().stream().mapToInt((i) -> i).sum();


            for (Map.Entry<String, Integer> f: e.getValue().entrySet()) {
                localGiniSum += Math.pow(((double)f.getValue() / totalRows), 2);
            }

            if (localGiniSum != 0) {
                localGiniSum = 1 - localGiniSum;
            }

            if (localGiniSum != 0) {
                sum += (localGiniSum * ((double)totalRows/elements.size()));
            }
        }

        return sum;
    }

    /**
     *
     * Method for calculating the GINI index for the provided elements list and the provided field name
     *
     * @param elements the elements for which to calculate the index
     * @param fieldName the field name for which to calculate the index
     * @return the computed index
     * @throws Exception generic exception
     */
    protected double calculateGiniIndex (final List<T> elements, final String fieldName) throws Exception {

        if (elements == null || elements.isEmpty()) {

            return 0;
        }

        /*
         * collapse to a map of the valus of the provided field name from the list and it's number of occurrences
         */
        Map<String, Integer> valueToCount = elements.stream().map((e) -> {
            try {
                return String.class.cast(getValueFromInstance (fieldName, String.class, e));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toMap(Function.identity(), (String v) -> 1 , (Integer first, Integer second) -> first + second));

        /*
         * do the actual Gini index calculation 1 - sum (p1, p2 ... pn)
         *
         * where pi = pow((number of occurrence of a specific value)/(total number of provided elements elements), 2)
         *
         */
        return 1 - valueToCount.entrySet().stream().mapToInt(Map.Entry::getValue).mapToDouble((v) -> Math.pow((double) v /elements.size(), 2)).sum();
    }

    /**
     *
     * Method for fetching all the (declared) field names from a provided class
     *
     * @param clazz the type to fetch the fields from
     * @param omissions the fields to omit
     * @return the found field names
     */
    protected Set<String> getAttributesNames (final Class<?> clazz, final Collection<String> omissions) {

        Collection<String> actualOmissions = (omissions != null) ? omissions : Collections.emptyList();

        return Arrays.stream(clazz.getDeclaredFields()).map(Field::getName).filter((s) -> !actualOmissions.contains(s)).collect(Collectors.toSet());
    }

    protected Map<String, List<T>> splitListToMap (final List<T> elements, final String fieldName) throws Throwable {

        if (elements == null || elements.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, List<T>> data = new HashMap<>();

        for (T t: elements) {

            String classValue = String.class.cast(getValueFromInstance(fieldName, String.class, t));

            if (!data.containsKey(classValue)) {
                data.put(classValue, new ArrayList<T>() {{ this.add(t); }});
            } else {

                data.get(classValue).add(t);
            }
        }

        return data;
    }

    /**
     *
     * Method for finding the class field, the field annotate with the {@link ClassField}
     *
     * @param clazz the extension of {@link Element} to be tested
     * @return an optional containing the class field name
     */
    protected Optional<String> getClassFieldName (final Class<? extends Element> clazz) {

        if (clazz == null) {
            return Optional.empty();
        }

        for (Field f: clazz.getDeclaredFields()) {

            if (f.getAnnotations() == null) {
                continue;
            }

            if (Stream.of(f.getAnnotations())
                    .map(Annotation::annotationType)
                    .anyMatch(a -> a.equals(ClassField.class))) {

                return Optional.of(f.getName());
            }
        }

        return Optional.empty();
    }
}
