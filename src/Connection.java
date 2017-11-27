public class Connection {

    private Neuron input;
    double weight;

    public Connection(Neuron input, double weight){
        this.input = input;
        this.weight = weight;
    }

    public Neuron getInput(){
        return input;
    }

}
