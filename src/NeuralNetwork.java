import javafx.scene.shape.Circle;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NeuralNetwork {

    Circle animalBody;
    Circle animalHead;

    private static final int SIZE = 10;

    private InputNeuron[] inputs;
    private double[] inputWeight;
    private OutputNeuron[] outputs;
    private double[] outputWeight;
    private HiddenNeuron[] hiddens;
    private double[] hiddenWeight;

    public int health = 100;
    public double age = 0;
    public int bodyX = (int)(Math.random() * 10);
    public int bodyY = (int)(Math.random() * 10);
    public int headX = (int)(Math.random() * 10);
    public int headY = (int)(Math.random() * 10);


    public NeuralNetwork(NeuralNetwork old){
        inputs = old.inputs;
        inputWeight = old.inputWeight;
        outputs = old.outputs;
        outputWeight = old.outputWeight;
        hiddens = old.hiddens;
        hiddenWeight = old.hiddenWeight;
        createCircles();
    }

    public void createCircles(){
        animalBody = new Circle();
        animalBody.setRadius(25);
        animalHead = new Circle();
        animalHead.setRadius(12);
        int bodyX = ((int)(Math.random() * (SIZE - 1)))+1;
        int bodyY = ((int)(Math.random() * (SIZE - 1)))+1;
        int headX = bodyX + (((Math.random() > 0.5) ? 1 : -1));
        if(headX < 0){
            headX = 1;
        } else if (headX >= SIZE){
            headX = SIZE - 2;
        }
        int headY = bodyY + (((Math.random() > 0.5) ? 1 : -1));
        if(headY < 0){
            headY = 1;
        } else if (headY >= SIZE){
            headY = SIZE - 2;
        }
        this.bodyX = bodyX;
        this.bodyY = bodyY;
        this.headX = headX;
        this.headY = headY;
        animalBody.setCenterX(25);
        animalBody.setCenterY(25);
        animalHead.setCenterX(25);
        animalHead.setCenterY(25);
    }

    public NeuralNetwork(Map<String, Double> inputs, Map<String, Double> outputs, double[] hiddenWeights){
        this.inputs = new InputNeuron[inputs.size()];
        inputWeight = new double[inputs.size()];
        int cnt = 0;
        for (Map.Entry<String, Double> input: inputs.entrySet()){
            this.inputs[cnt] = new InputNeuron(input.getKey(), 0); //Default value is 0
            //Values will be set when the clock ticks
            inputWeight[cnt] = input.getValue();
            cnt++;
        }
        this.outputs = new OutputNeuron[outputs.size()];
        outputWeight = new double[outputs.size()];
        cnt = 0;
        for (Map.Entry<String, Double> output: outputs.entrySet()){
            this.outputs[cnt] = new OutputNeuron(output.getKey());
            outputWeight[cnt] = output.getValue();
            cnt++;
        }
        hiddens = new HiddenNeuron[hiddenWeights.length];
        for (int i = 0; i < hiddenWeights.length; i++) {
            hiddens[i] = new HiddenNeuron();
        }
        this.hiddenWeight = hiddenWeights;
        createCircles();
    }

    public void setValueForNeuron(String name, double value){
        for(InputNeuron input: inputs){
            if(input.getName().equals(name)){
                input.setValue(value);
                return;
            }
        }
        throw new IllegalArgumentException("No input neuron with name " + name + " was found");
    }

    public Map<String, Double> calculateOutputValues(){
        Map<String, Double> outValues = new HashMap<>();
        for (InputNeuron in :
                inputs) {
            in.fire(hiddens, hiddenWeight);
        }
        for(HiddenNeuron hid: hiddens){
            hid.fire(outputs, outputWeight);
        }
        for(OutputNeuron out: outputs){
            outValues.put(out.getName(), out.getValue());
        }
        return outValues;
    }

    public NeuralNetwork cloneAndModifyWeights(){
        Random r = new Random();
        NeuralNetwork newNetwork = new NeuralNetwork(this);
        for (int i = 0; i < newNetwork.inputWeight.length; i++) {
            int swap = r.nextInt(5)-2;
            newNetwork.inputWeight[i] = newNetwork.inputWeight[i]+ swap;
            if (newNetwork.inputWeight[i] > 2) {
                newNetwork.inputWeight[i] = 2;
            } else if (newNetwork.inputWeight[i] < -2){
                newNetwork.inputWeight[i] = -2;
            }
        }
        for (int i = 0; i < newNetwork.hiddenWeight.length; i++) {
            int swap = r.nextInt(5)-2;
            newNetwork.hiddenWeight[i] = newNetwork.hiddenWeight[i]+ swap;
            if (newNetwork.hiddenWeight[i] > 2) {
                newNetwork.hiddenWeight[i] = 2;
            } else if (newNetwork.hiddenWeight[i] < -2){
                newNetwork.hiddenWeight[i] = -2;
            }
        }
        for (int i = 0; i < newNetwork.outputWeight.length; i++) {
            int swap = r.nextInt(5)-2;
            newNetwork.outputWeight[i] = newNetwork.outputWeight[i]+ swap;
            if (newNetwork.outputWeight[i] > 2) {
                newNetwork.outputWeight[i] = 2;
            } else if (newNetwork.outputWeight[i] < -2){
                newNetwork.outputWeight[i] = -2;
            }
        }
        return newNetwork;
    }

}
