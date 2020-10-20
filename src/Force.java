public class Force {
    Integer number;
    Double value;

    public Integer getNumber(){
        return number;
    }

    public void setNumber(Integer number){
        this.number = number;
    }

    public Double getValue(){
        return value;
    }

    public void setValue(Double value){
        this.value = value;
    }

    public Force(Integer number, Double value){
        this.number = number;
        this.value = value;
    }
}
