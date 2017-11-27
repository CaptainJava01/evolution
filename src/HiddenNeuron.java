import java.util.ArrayList;

public class HiddenNeuron extends Neuron{

    ArrayList<Connection> inputs = new ArrayList<>();


    public void fire(OutputNeuron[] outputs, double[] weights){
        double sum = 0;
        for (Connection connection :
                inputs) {
            sum += connection.getInput().getValue() * connection.weight;
        }
        setValue(Math.pow(Math.E, sum)/(1+ Math.pow(Math.E, sum)));
        for (int i = 0; i < outputs.length; i++) {
            outputs[i].inputs.add(new Connection(this, (i < (weights.length)) ? weights[i] : 0));
        }
    }



}
