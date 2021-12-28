package agh.simulator.gui;


import agh.simulator.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class App extends Application{
    AbstractWorldMap wrapMap;
    AbstractWorldMap boundMap;
    SimulationEngine wrapEngine;
    SimulationEngine boundEngine;
    Button runButton = new Button("RUN");
    Button stopButton = new Button("STOP");
    Button startButton = new Button("START");
    int gridPaneHeight;
    int gridPaneWidth;
    int baseHeight = 400;
    int baseWidth = 400;
    private boolean isRunning = true;
    MapElementStatistics mapElementStatistics;
    private Label dominantGenotypeLabel;

    @Override
    public void init(){
        List<String> input = getParameters().getRaw();
//        InputParser
        int animalAmount = 30;
        int mapWidth = 15;
        int mapHeight = 15;
        int plantEnergy = 50;
        int initialEnergy = 100;
        int moveEnergy = 3;
        double jungleRatio = 0.25;

        gridPaneWidth = baseWidth /mapWidth;
        gridPaneHeight = baseHeight /mapHeight;

        this.wrapMap = new WrappedMap(jungleRatio, mapWidth, mapHeight, plantEnergy,initialEnergy);
        this.wrapEngine = new SimulationEngine(wrapMap, animalAmount, initialEnergy, moveEnergy);
        Thread wrapThread = new Thread(wrapEngine);
        this.boundMap = new BoundedMap(jungleRatio, mapWidth, mapHeight, plantEnergy,initialEnergy);
        this.boundEngine = new SimulationEngine(boundMap, animalAmount, initialEnergy, moveEnergy);
        Thread boundThread = new Thread(boundEngine);

        runButton.setOnAction(actionEvent -> {
            synchronized (this){
                wrapThread.start();
                boundThread.start();
            }
        });

        stopButton.setOnAction(actionEvent -> {
            //thread waiting
            isRunning = false;
        });

        startButton.setOnAction(actionEvent -> {
            //thread running
            isRunning = true;
        });
    }

    @Override
    public void start(Stage primaryStage){
        GridPane wrapGridPane = new GridPane();
        GridPane boundGridPane = new GridPane();

        setMainGrid(wrapGridPane, wrapMap);
        setMainGrid(boundGridPane, boundMap);

        this.wrapEngine.addObserver(() -> updateLayout(wrapGridPane, wrapMap));
        this.boundEngine.addObserver(() -> updateLayout(boundGridPane, boundMap));
        this.mapElementStatistics = new MapElementStatistics(wrapMap);
        this.dominantGenotypeLabel = new Label(Arrays.toString(mapElementStatistics.dominantGenotype.getKey())
                + " " + mapElementStatistics.dominantGenotype.getValue());


        HBox hBox = new HBox();
        hBox.getChildren().add(mapElementStatistics.lineChart);
        hBox.getChildren().add(wrapGridPane);
        hBox.getChildren().add(boundGridPane);
        hBox.setAlignment(Pos.CENTER);
        VBox view = new VBox();
        view.getChildren().add(hBox);
        view.getChildren().add(dominantGenotypeLabel);
        view.getChildren().add(runButton);
        view.getChildren().add(stopButton);
        view.getChildren().add(startButton);
        Scene scene = new Scene(view, 1000, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            wrapEngine.shutdown();
            boundEngine.shutdown();
        });
    }



    private void setMainGrid(GridPane gridPane, AbstractWorldMap map){
        gridPane.setGridLinesVisible(true);
        Label label = new Label("y\\x");
        gridPane.add(label, 0, 0, 1, 1);
        gridPane.getRowConstraints().add(new RowConstraints(gridPaneHeight));
        gridPane.getColumnConstraints().add(new ColumnConstraints(gridPaneWidth));
        GridPane.setHalignment(label, HPos.CENTER);

        drawObjects(gridPane, map);
        drawGrid(gridPane, map);

    }
    private void updateLayout(GridPane gridPane, AbstractWorldMap map){
        Platform.runLater(() -> {
            gridPane.setGridLinesVisible(false);
            gridPane.getColumnConstraints().clear();
            gridPane.getRowConstraints().clear();
            gridPane.getChildren().clear();
            gridPane.setGridLinesVisible(true);
            mapElementStatistics.updateMapData(wrapEngine.era);
            dominantGenotypeLabel.setText(Arrays.toString(mapElementStatistics.dominantGenotype.getKey())
                    + " " + mapElementStatistics.dominantGenotype.getValue());
            setMainGrid(gridPane, map);
        });
    }
    private void drawGrid(GridPane gridPane, AbstractWorldMap map){
        Vector2d lowerLeft = map.getLowerLeft();
        Vector2d upperRight = map.getUpperRight();
        for (int i = 0; i <upperRight.x; i++) {
            gridPane.getColumnConstraints().add(new ColumnConstraints(gridPaneWidth));
            Label index = new Label(String.valueOf(i + lowerLeft.x));
            gridPane.add(index, i+1, 0);
            GridPane.setHalignment(index, HPos.CENTER);
        }
        for (int i = 0; i < upperRight.y; i++) {
            gridPane.getRowConstraints().add(new RowConstraints(gridPaneHeight));
            Label index = new Label(String.valueOf(i + lowerLeft.y));
            gridPane.add(index, 0, upperRight.y - i);
            GridPane.setHalignment(index, HPos.CENTER);
        }
    }

    private void drawObjects(GridPane gridPane, AbstractWorldMap map) {
        for (int i = 0; i < map.getUpperRight().x; i++) {
            for (int j = 0; j < map.getUpperRight().y; j++) {
                Vector2d position = new Vector2d(i, j);
                Object object = map.objectAt(position);

                if (object != null){
                    GuiMapElement objectElement = new GuiMapElement(object);
                    Label objectLabel = objectElement.getElementLabel();
                    objectLabel.setMinSize(gridPaneWidth, gridPaneHeight);
                    gridPane.add(objectLabel, i+1, j+1);
                    GridPane.setHalignment(objectLabel, HPos.CENTER);
                }
                else if(map.getJungle().isPositionInJungle(new Vector2d(i,j))){
                    Label jungleLabel = new Label();
                    jungleLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                    jungleLabel.setMinSize(gridPaneWidth, gridPaneHeight);
                    gridPane.add(jungleLabel, i+1, j+1);
                    GridPane.setHalignment(jungleLabel, HPos.CENTER);
                }
                else {
                    Label emptyLabel = new Label();
                    emptyLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTYELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
                    emptyLabel.setMinSize(gridPaneWidth, gridPaneHeight);
                    gridPane.add(emptyLabel, i+1, j+1);
                    GridPane.setHalignment(emptyLabel, HPos.CENTER);
                }
            }
        }
    }
}
