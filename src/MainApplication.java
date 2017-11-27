import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainApplication extends Application{

    public static final int SIZE = 10;
    private Thread t1, t2;

    private double[][] waters = new double[SIZE][SIZE]; //0 water; 1 full grass
    private Pane[][] panes = new Pane[SIZE][SIZE];
    private ArrayList<NeuralNetwork> animals = new ArrayList<>();
    boolean run = true;

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Evolution");
        BorderPane border = new BorderPane();
        GridPane grid = new GridPane();
        border.setCenter(grid);
        Button pauseBtn = new Button("Pause");
        pauseBtn.setOnAction(actionEvent -> {if(run==true){run = false;}else{run = true;}});
        border.setRight(pauseBtn);
        primaryStage.setScene(new Scene(border));
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Pane p = new Pane();
                p.setMinWidth(50);
                p.setMinHeight(50);
                if(Math.random() < 0.25){
                    waters[i][j] = 0;
                    p.setBackground(new Background(new BackgroundFill(new Color(0, 0, 1, 1), CornerRadii.EMPTY, new Insets(0,0,0,0))));
                } else {
                    double r = (Math.random() * 0.9) + 0.1;
                    waters[i][j] = r;
                    p.setBackground(new Background(new BackgroundFill(new Color(0, 1, 0, r), CornerRadii.EMPTY, new Insets(0, 0, 0, 0))));
                }
                panes[i][j] = p;
                grid.add(p, i, j);
            }
        }
        for (int i = 0; i < 10; i++) {
            Map<String, Double> inputMap = new HashMap<>();
            inputMap.put("UndergroundBody", Math.random() * 2 - 1); //Value will be later 0 for Water, 1 for eatable grass, between for growing grass
            inputMap.put("UndergroundHead", Math.random() * 2 - 1); //Value will be later 0 for Water, 1 for eatable grass, between for growing grass
            inputMap.put("Age", Math.random() * 2-1);
            inputMap.put("Health", Math.random() * 2 - 1);
            inputMap.put("Nearby", Math.random() * 2 -1);
            Map<String, Double> outputMap = new HashMap<>();
            outputMap.put("Eat", Math.random() * 2 - 1);
            outputMap.put("Drink", Math.random() * 2 - 1);
            outputMap.put("MoveHeadUp", Math.random() * 2 - 1);
            outputMap.put("MoveHeadDown", Math.random() * 2 - 1);
            outputMap.put("MoveHeadLeft", Math.random() * 2 - 1);
            outputMap.put("MoveHeadRight", Math.random() * 2 - 1);
            outputMap.put("Clone", Math.random() * 2 - 1);
            outputMap.put("MoveBodyUp", Math.random() * 2 - 1);
            outputMap.put("MoveBodyDown", Math.random() * 2 - 1);
            outputMap.put("MoveBodyLeft", Math.random() * 2 - 1);
            outputMap.put("MoveBodyRight", Math.random() * 2 - 1);
            double[] hiddenWeightsArray = new double[5];
            for (int j = 0; j < 5; j++) {
                hiddenWeightsArray[j] = (int)(Math.random() * 2) - 1;
            }
            NeuralNetwork animal = new NeuralNetwork(inputMap, outputMap, hiddenWeightsArray);


            animals.add(animal);
        }
        primaryStage.show();

        t1 = new Thread(() -> {
            try {
                while (true) {
                    while(!run){
                        Thread.yield();
                    }
                    for (int i = 0; i < 10; i++) {
                        for (int j = 0; j < 10; j++) {
                            if (waters[i][j] > 0) {
                                Pane p = panes[i][j];
                                double opacity = waters[i][j];
                                opacity += 0.1;
                                if (opacity > 1) {
                                    opacity = 1;
                                }
                                waters[i][j] = opacity;
                                p.setBackground(new Background(new BackgroundFill(new Color(0, 1, 0, opacity), CornerRadii.EMPTY, new Insets(0, 0, 0, 0))));
                            }
                        }
                    }
                    Thread.sleep(500);
                }
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        });
        t2 = new Thread(() -> {
            try {
                while(true){
                    while(!run){
                        Thread.yield();
                    }
                    for (int i = 0; i < animals.size(); i++) {
                        NeuralNetwork actual = animals.get(i);

                        Platform.runLater(
                                () -> {
                                    if(panes[actual.bodyX][actual.bodyY].getChildren().isEmpty() && panes[actual.headX][actual.headY].getChildren().isEmpty()) {
                                        panes[actual.bodyX][actual.bodyY].getChildren().add(actual.animalBody);
                                        panes[actual.headX][actual.headY].getChildren().add(actual.animalHead);
                                    }
                                });
                        if(actual.health < 0){
                            Platform.runLater(()->{panes[actual.bodyX][actual.bodyY].getChildren().clear();
                                panes[actual.headX][actual.headY].getChildren().clear();
                                animals.remove(actual);
                            System.out.println("Die");});
                            i--;
                            continue;
                        }
                        Thread.sleep(40);
                        if(new Date().getTime()%10 == 0) {
                            actual.setValueForNeuron("UndergroundBody", waters[actual.bodyX][actual.bodyY]);
                            actual.setValueForNeuron("UndergroundHead", waters[actual.headX][actual.headY]);
                            actual.setValueForNeuron("Age", actual.age);
                            actual.setValueForNeuron("Health", actual.health);
                            actual.setValueForNeuron("Nearby", (otherNearby(actual))?1:0);
                            Map<String, Double> result = actual.calculateOutputValues();
                            if (result.get("MoveBodyUp") > 0.5 && actual.bodyY != 0 && actual.headY != 0 && panes[actual.bodyX][actual.bodyY-1].getChildren().isEmpty() && panes[actual.headX][actual.headY-1].getChildren().isEmpty()) {
                                Platform.runLater(() -> {
                                    panes[actual.bodyX][actual.bodyY].getChildren().clear();
                                    panes[actual.headX][actual.headY].getChildren().clear();
                                    actual.bodyY--;
                                    actual.headY--;
                                    actual.health-=(5*actual.age);
                                });

                            }
                            if (result.get("MoveBodyLeft") > 0.5 && actual.bodyX != 0 && actual.headX != 0 && panes[actual.bodyX-1][actual.bodyY].getChildren().isEmpty() && panes[actual.headX-1][actual.headY].getChildren().isEmpty()) {
                                Platform.runLater(() -> {
                                    panes[actual.bodyX][actual.bodyY].getChildren().clear();
                                    panes[actual.headX][actual.headY].getChildren().clear();
                                    actual.bodyX--;
                                    actual.headX--;
                                    actual.health-=(5*actual.age);
                                });

                            }
                            if (result.get("MoveBodyRight") > 0.5 && actual.bodyX != SIZE - 1 && actual.headX != SIZE - 1 && panes[actual.bodyX+1][actual.bodyY].getChildren().isEmpty() && panes[actual.headX+1][actual.headY].getChildren().isEmpty()) {
                                Platform.runLater(() -> {
                                    panes[actual.bodyX][actual.bodyY].getChildren().clear();
                                    panes[actual.headX][actual.headY].getChildren().clear();
                                    actual.bodyX++;
                                    actual.headX++;
                                    actual.health-=(5*actual.age);
                                });

                            }
                            if (result.get("MoveBodyDown") > 0.5 && actual.bodyY != SIZE - 1 && actual.headY != SIZE - 1 && panes[actual.bodyX][actual.bodyY+1].getChildren().isEmpty() && panes[actual.headX][actual.headY+1].getChildren().isEmpty()) {
                                Platform.runLater(() -> {
                                    panes[actual.bodyX][actual.bodyY].getChildren().clear();
                                    panes[actual.headX][actual.headY].getChildren().clear();
                                    actual.bodyY++;
                                    actual.headY++;
                                    actual.health-=(5*actual.age);
                                });

                            }

                            if(result.get("Eat") > 0.5){
                                actual.health -= (10 * actual.age);
                                if(waters[actual.headX][actual.headY] == 1){
                                    actual.health += 40;
                                }
                                waters[actual.headX][actual.headY] = 0.1;
                                panes[actual.headX][actual.headY].setBackground(new Background(new BackgroundFill(new Color(0, 1, 0, 0.1), CornerRadii.EMPTY, new Insets(0, 0, 0, 0))));
                            }

                            if(result.get("Drink") > 0.5){
                                actual.health -= (5 * actual.age);
                                if(waters[actual.headX][actual.headY] == 0){
                                    actual.health += 30;
                                }
                            }

                            if(result.get("Clone") > 0.5){
                                actual.health -= (5 * actual.age);
                                if(otherNearby(actual) && actual.age > 1){
                                    NeuralNetwork child = actual.cloneAndModifyWeights();
                                    child.animalBody.setFill(Color.RED);
                                    animals.add(child);
                                    child.health = 100;
                                    child.age = 0;
                                    if(panes[actual.bodyX+1][actual.bodyY].getChildren().isEmpty() && actual.bodyX+2 < SIZE){
                                        child.bodyX = actual.bodyX+1;
                                        child.bodyY = actual.bodyY;
                                        child.headX = actual.bodyX+2;
                                        child.headY = actual.bodyY;
                                    }
                                    else if(panes[actual.bodyX][actual.bodyY+1].getChildren().isEmpty() && actual.bodyY+2 < SIZE){
                                        child.bodyX = actual.bodyX;
                                        child.bodyY = actual.bodyY + 1;
                                        child.headX = actual.bodyX;
                                        child.headY = actual.bodyY + 2;
                                    }
                                    else if(panes[actual.bodyX-1][actual.bodyY].getChildren().isEmpty() && actual.bodyX-2 >= 0){
                                        child.bodyX = actual.bodyX-1;
                                        child.bodyY = actual.bodyY;
                                        child.headX = actual.bodyX-2;
                                        child.headY = actual.bodyY;
                                    }
                                    else if(panes[actual.bodyX][actual.bodyY-1].getChildren().isEmpty() && actual.bodyY-2 >= 0){
                                        child.bodyX = actual.bodyX;
                                        child.bodyY = actual.bodyY-1;
                                        child.headX = actual.bodyX;
                                        child.headY = actual.bodyY-2;
                                    } else {
                                        animals.remove(child);
                                    }
                                }
                            }

                            actual.health -= 20;
                            actual.age += 0.5;
                            System.out.println("result.get(\"Eat\") = " + result.get("Eat"));
                        }

                    }

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t1.start();
        t2.start();

    }

    public static void main(String[] args) {
        launch();
    }

    private boolean otherNearby(NeuralNetwork animal){
        for (NeuralNetwork other :
                animals) {
            if(other != animal){
                if(Math.abs(animal.bodyX - other.bodyX) < 2 && Math.abs(animal.bodyY - other.bodyY) < 2){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void stop() throws Exception {
        t1.stop();
        t2.stop();
        super.stop();
    }
}
