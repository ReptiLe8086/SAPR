public class Component {
    Double x;
    Double N;
    Double U;
    Double Sigma;

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getN(){
        return N;
    }

    public void setN(Double N){
        this.N = N;
    }

    public Double getU(){
        return U;
    }

    public void setU(Double U){
        this.U = U;
    }

    public Double getSigma(){
        return Sigma;
    }

    public void setSigma(Double Sigma){
        this.Sigma = Sigma;
    }

    public Component(Double x, Double N, Double U, Double Sigma){
        this.x = x;
        this.N = N;
        this.U = U;
        this.Sigma = Sigma;
    }
}
