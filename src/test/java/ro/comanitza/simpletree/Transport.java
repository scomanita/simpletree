package ro.comanitza.simpletree;

/**
 *
 * @author comanitza
 */
public class Transport implements Element {

    private String gender;
    private String hasCar;
    private String costPerKm;
    private String income;
    @ClassField
    private String transportation;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHasCar() {
        return hasCar;
    }

    public void setHasCar(String hasCar) {
        this.hasCar = hasCar;
    }

    public String getCostPerKm() {
        return costPerKm;
    }

    public void setCostPerKm(String costPerKm) {
        this.costPerKm = costPerKm;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getTransportation() {
        return transportation;
    }

    public void setTransportation(String transportation) {
        this.transportation = transportation;
    }

    @Override
    public String toString() {
        return "Transport{" +
                "gender='" + gender + '\'' +
                ", hasCar='" + hasCar + '\'' +
                ", costPerKm='" + costPerKm + '\'' +
                ", income='" + income + '\'' +
                ", transportation='" + transportation + '\'' +
                '}';
    }
}
