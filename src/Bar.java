public class Bar {
    Integer number;
    Double area;
    Double elasticity;
    Double length;
    Double sigma;

    public Integer getNumber(){
        return number;
    }

    public void setNumber(Integer number){
        this.number = number;
    }

    public Double getArea(){
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Double getElasticity(){
        return elasticity;
    }

    public void setElasticity(Double elasticity){
        this.elasticity = elasticity;
    }

    public Double getLength(){
        return length;
    }

    public void setLength(Double length){
        this.length = length;
    }

    public Double getSigma(){
        return sigma;
    }

    public void setSigma(Double sigma){
        this.sigma = sigma;
    }

    public Bar(Integer number, Double area, Double elasticity, Double length, Double sigma) {
    this.number = number;
    this.area = area;
    this.elasticity = elasticity;
    this.length = length;
    this.sigma = sigma;
    }

}
