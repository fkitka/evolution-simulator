package agh.simulator.gui;


import agh.simulator.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Arrays;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class App extends Application{
    AbstractWorldMap wrapMap;
    AbstractWorldMap boundMap;
    SimulationEngine wrapEngine;
    SimulationEngine boundEngine;
    Button runButton = new Button("RUN");
    Button wrapTwoWayButton = new Button("STOP");
    Button boundTwoWayButton = new Button("STOP");
    int gridPaneHeight;
    int gridPaneWidth;
    int baseHeight = 300;
    int baseWidth = 300;
    int animalAmount;
    int mapWidth;
    int mapHeight;
    int plantEnergy;
    int initialEnergy;
    int moveEnergy;
    double jungleRatio;
    GuiMapStatistics wrapMapStatistics;
    GuiMapStatistics boundMapStatistics;
    Label wrapDominantGenotypeLabel;
    Label boundDominantGenotypeLabel;
    Scene inputScene;
    Button simulationStart;
    boolean parametersSubmitted = false;
    CSVWriter wrapWriter;
    CSVWriter boundWriter;

    @Override
    public void init(){
        GridPane inputGrid = createInputGrid();
        inputScene = new Scene(inputGrid, 400, 400);
    }

    @Override
    public void start(Stage primaryStage){

        primaryStage.setScene(inputScene);

            simulationStart.setOnAction(actionEvent -> {
                if (parametersSubmitted) {
                    GridPane wrapGridPane = new GridPane();
                    GridPane boundGridPane = new GridPane();

                    setMainGrid(wrapGridPane, wrapMap);
                    setMainGrid(boundGridPane, boundMap);

                    this.wrapEngine.addObserver(() -> updateLayout(wrapGridPane, wrapMap));
                    this.boundEngine.addObserver(() -> updateLayout(boundGridPane, boundMap));
                    this.wrapMapStatistics = new GuiMapStatistics(wrapMap);
                    this.boundMapStatistics = new GuiMapStatistics(boundMap);

                    wrapWriter = new CSVWriter(wrapMap);
                    boundWriter = new CSVWriter(boundMap);
                    wrapWriter.generateData(wrapMap);
                    boundWriter.generateData(boundMap);

                    this.wrapDominantGenotypeLabel = new Label(Arrays.toString(wrapMapStatistics.dominantGenotype.getKey())
                            + " " + wrapMapStatistics.dominantGenotype.getValue());
                    this.boundDominantGenotypeLabel = new Label(Arrays.toString(boundMapStatistics.dominantGenotype.getKey())
                            + " " + boundMapStatistics.dominantGenotype.getValue());

                    HBox wrapMapBox = createMapBox(wrapGridPane, wrapMapStatistics, wrapDominantGenotypeLabel, wrapTwoWayButton);
                    HBox boundMapBox = createMapBox(boundGridPane, boundMapStatistics, boundDominantGenotypeLabel, boundTwoWayButton);

                    Button writeStatistics = new Button("Save statistics");
                    writeStatistics.setOnAction(event ->{
                        wrapWriter.write();
                        boundWriter.write();
                    });

                    VBox view = new VBox();
                    view.getChildren().add(boundMapBox);
                    view.getChildren().add(wrapMapBox);
                    view.getChildren().add(runButton);
                    view.getChildren().add(writeStatistics);

                    Scene simulation = new Scene(view, 1000, 500);
                    primaryStage.setScene(simulation);
                }
            });
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
//            wrapWriter.write();
//            boundWriter.write();
            wrapEngine.shutdown();
            boundEngine.shutdown();
        });
    }

    private void setSimulation() {
        gridPaneWidth = baseWidth / mapWidth;
        gridPaneHeight = baseHeight / mapHeight;
        this.wrapMap = new WrappedMap(jungleRatio, mapWidth, mapHeight, plantEnergy, initialEnergy);
        this.wrapEngine = new SimulationEngine(wrapMap, animalAmount, initialEnergy, moveEnergy);
        Thread wrapThread = new Thread(wrapEngine);
        this.boundMap = new BoundedMap(jungleRatio, mapWidth, mapHeight, plantEnergy, initialEnergy);
        this.boundEngine = new SimulationEngine(boundMap, animalAmount, initialEnergy, moveEnergy);
        Thread boundThread = new Thread(boundEngine);

        runButton.setOnAction(actionEvent -> {
            synchronized (this){
                wrapThread.start();
                boundThread.start();
            }
        });

        boundTwoWayButton.setOnAction(actionEvent -> {
            //thread waiting
            if (boundEngine.isRunning) {
                boundEngine.stop();
                boundTwoWayButton.setText("START");
            }
            else {
                boundEngine.start();
                boundTwoWayButton.setText("STOP");
            }
        });

        wrapTwoWayButton.setOnAction(actionEvent -> {
            //thread running
            if (wrapEngine.isRunning) {
                wrapEngine.stop();
                wrapTwoWayButton.setText("START");
            }
            else {
                wrapEngine.start();
                wrapTwoWayButton.setText("STOP");
            }
        });
    }

    private GridPane createInputGrid(){
        GridPane inputGrid = new GridPane();
        inputGrid.setAlignment(Pos.CENTER);
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);
        inputGrid.setPadding(new Insets(25, 25, 25, 25));
        Text scenetitle = new Text("Evolution Simulator");
        inputGrid.add(scenetitle, 0, 0, 2, 1);

        Label mapWidthLabel = new Label("Map width:");
        inputGrid.add(mapWidthLabel, 0, 1);
        TextField mapWidthField = new TextField("15");
        inputGrid.add(mapWidthField, 1, 1);

        Label mapHeightLabel = new Label("Map height:");
        inputGrid.add(mapHeightLabel, 0, 2);
        TextField mapHeightField = new TextField("15");
        inputGrid.add(mapHeightField, 1, 2);

        Label animalAmountLabel = new Label("Amount of animals:");
        inputGrid.add(animalAmountLabel, 0, 3);
        TextField animalAmountField = new TextField("30");
        inputGrid.add(animalAmountField, 1, 3);

        Label plantEnergyLabel = new Label("Plant energy:");
        inputGrid.add(plantEnergyLabel, 0, 4);
        TextField plantEnergyField = new TextField("50");
        inputGrid.add(plantEnergyField, 1, 4);

        Label animalEnergyLabel = new Label("Animal energy:");
        inputGrid.add(animalEnergyLabel, 0, 5);
        TextField animalEnergyField = new TextField("100");
        inputGrid.add(animalEnergyField, 1, 5);

        Label moveEnergyLabel = new Label("Move energy:");
        inputGrid.add(moveEnergyLabel, 0, 6);
        TextField moveEnergyField = new TextField("3");
        inputGrid.add(moveEnergyField, 1, 6);

        Label jungleRatioLabel = new Label("Jungle ratio:");
        inputGrid.add(jungleRatioLabel, 0, 7);
        TextField jungleRatioField = new TextField("0.25");
        inputGrid.add(jungleRatioField, 1, 7);
        Button submit = new Button("Submit parameters");
        simulationStart = new Button("Start simulation");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(submit);
        hbBtn.getChildren().add(simulationStart);
        inputGrid.add(hbBtn, 1, 8);

        submit.setOnAction(actionEvent -> {
            this.mapWidth = parseInt(mapWidthField.getText());
            this.mapHeight = parseInt(mapHeightField.getText());
            this.animalAmount = parseInt(animalAmountField.getText());
            this.plantEnergy = parseInt(plantEnergyField.getText());
            this.initialEnergy = parseInt(animalEnergyField.getText());
            this.moveEnergy = parseInt(moveEnergyField.getText());
            this.jungleRatio = parseDouble(jungleRatioField.getText());
            parametersSubmitted = true;
            setSimulation();
        });

        return inputGrid;
    }

    private HBox createMapBox(GridPane gridPane, GuiMapStatistics mapStatistics, Label dominantGenotypeLabel, Button twoWayButton) {
        HBox mapBox = new HBox();
        mapBox.getChildren().add(gridPane);
        VBox wrapStatisticsBox = new VBox();
        wrapStatisticsBox.getChildren().add(mapStatistics.lineChart);
        wrapStatisticsBox.getChildren().add(dominantGenotypeLabel);
        wrapStatisticsBox.setAlignment(Pos.CENTER);
        mapBox.getChildren().add(wrapStatisticsBox);
        mapBox.getChildren().add(twoWayButton);
        mapBox.setAlignment(Pos.CENTER_LEFT);
        return mapBox;
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
            if (map == boundMap){
                boundMapStatistics.updateMapData(boundEngine.era);
                boundWriter.generateData(map);
                boundDominantGenotypeLabel.setText(Arrays.toString(boundMapStatistics.dominantGenotype.getKey())
                    + " " + boundMapStatistics.dominantGenotype.getValue());
            }
            else if (map == wrapMap){
            wrapMapStatistics.updateMapData(wrapEngine.era);
            wrapWriter.generateData(map);
            wrapDominantGenotypeLabel.setText(Arrays.toString(wrapMapStatistics.dominantGenotype.getKey())
                    + " " + wrapMapStatistics.dominantGenotype.getValue());
            }
            setMainGrid(gridPane, map);
        });
    }
    private void drawGrid(GridPane gridPane, AbstractWorldMap map){
        Vector2d lowerLeft = map.getLowerLeft();
        Vector2d upperRight = map.getUpperRight();
        for (int i = 0; i <upperRight.x+1; i++) {
            gridPane.getColumnConstraints().add(new ColumnConstraints(gridPaneWidth));
            Label index = new Label(String.valueOf(i + lowerLeft.x));
            gridPane.add(index, i+1, 0);
            GridPane.setHalignment(index, HPos.CENTER);
        }
        for (int i = 0; i < upperRight.y+1; i++) {
            gridPane.getRowConstraints().add(new RowConstraints(gridPaneHeight));
            Label index = new Label(String.valueOf(i + lowerLeft.y));
            gridPane.add(index, 0, upperRight.y - i+1);
            GridPane.setHalignment(index, HPos.CENTER);
        }
    }

    private void drawObjects(GridPane gridPane, AbstractWorldMap map) {
        for (int i = 0; i < map.getUpperRight().x + 1; i++) {
            for (int j = 0; j < map.getUpperRight().y + 1; j++) {
                Vector2d position = new Vector2d(i, j);
                Object object = map.objectAt(position);
                if (object != null){
                    GuiMapElement objectElement = new GuiMapElement(object);
                    Pane objectPane = objectElement.getElementPane();
                    objectPane.setMinSize(gridPaneWidth, gridPaneHeight);
                    if (object instanceof Animal animal) {
                        objectPane.setOnMouseClicked(event -> {
                            Alert a = new Alert(Alert.AlertType.INFORMATION);
                            a.setTitle("Genome");
                            a.setHeaderText("");
                            a.setContentText(Arrays.toString(animal.getGenotype().genes));
                            a.show();
//                            map.statistics.track(animal);
                        });
                    }
                    gridPane.add(objectPane, i+1, j+1);
                    GridPane.setHalignment(objectPane, HPos.CENTER);
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
