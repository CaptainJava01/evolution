import java.util.ArrayList;

public class OutputNeuron extends Neuron{

    ArrayList<Connection> inputs = new ArrayList<>();

    public OutputNeuron(String name){
        setName(name);
    }

    @Override
    public double getValue(){
        double sum = 0;
        for (Connection connection :
                inputs) {
            sum += connection.getInput().getValue() * connection.weight;
        }
        return Math.pow(Math.E, sum)/(1+ Math.pow(Math.E, sum));
    }

}
