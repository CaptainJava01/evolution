import java.util.ArrayList;

public class InputNeuron extends Neuron{

    public InputNeuron(String name, double value){
        setValue(value);
        setName(name);
    }

    public void fire(HiddenNeuron[] hiddens, double[] weights){
        for (int i = 0; i < hiddens.length; i++) {
            hiddens[i].inputs.add(new Connection(this, (i < (weights.length)) ? weights[i] : 0));
        }
    }


}
