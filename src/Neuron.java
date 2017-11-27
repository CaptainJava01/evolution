public abstract class Neuron {

    private double value;
    private String name = "";

    public void setValue(double value){
        this.value = value;
    }

    public double getValue(){
        return value;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

}
