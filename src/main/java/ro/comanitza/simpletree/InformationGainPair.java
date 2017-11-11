package ro.comanitza.simpletree;

/**
 *
 * @author comanitza
 */
class InformationGainPair {
    private final String fieldName;
    private final double informationGain;

    InformationGainPair(final String fieldName, final double informationGain) {
        this.fieldName = fieldName;
        this.informationGain = informationGain;
    }

    String getFieldName() {
        return fieldName;
    }

    double getInformationGain() {
        return informationGain;
    }

    @Override
    public String toString() {
        return '{' + fieldName + ", " + informationGain + '}';
    }
}
