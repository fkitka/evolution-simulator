package agh.simulator.gui;


import agh.simulator.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

    CSVMapWriter wrapWriter;
    CSVMapWriter boundWriter;

    private boolean boundIsMagic = false;
    private boolean wrapIsMagic = false;
    private Label wrapMagicLabel = new Label();
    private Label boundMagicLabel = new Label();


    private Label wrapNumOfChildrenLabel;
    private Label wrapNumOfDescendantsLabel;
    private Label wrapDeathEra;
    private boolean wrapIsTracked = false;
    private Label boundNumOfChildrenLabel;
    private Label boundNumOfDescendantsLabel;
    private Label boundDeathEra;
    private boolean boundIsTracked = false;

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

                    this.wrapEngine.addObserver(new IMapObserver() {
                        @Override
                        public void mapChanged() {
                            updateLayout(wrapGridPane, wrapMap);
                        }

                        @Override
                        public void magicWasMade() {
                            updateMagicLabel(wrapMap);
                        }
                    });
                    this.boundEngine.addObserver(new IMapObserver() {
                        @Override
                        public void mapChanged() {
                            updateLayout(boundGridPane, boundMap);
                        }

                        @Override
                        public void magicWasMade() {
                            updateMagicLabel(boundMap);
                        }
                    });
                    this.wrapMapStatistics = new GuiMapStatistics(wrapMap);
                    this.boundMapStatistics = new GuiMapStatistics(boundMap);

                    wrapWriter = new CSVMapWriter(wrapMap);
                    boundWriter = new CSVMapWriter(boundMap);
                    wrapWriter.generateData(wrapMap);
                    boundWriter.generateData(boundMap);

                    this.wrapDominantGenotypeLabel = new Label(Arrays.toString(wrapMapStatistics.dominantGenotype.getKey())
                            + " " + wrapMapStatistics.dominantGenotype.getValue());
                    this.boundDominantGenotypeLabel = new Label(Arrays.toString(boundMapStatistics.dominantGenotype.getKey())
                            + " " + boundMapStatistics.dominantGenotype.getValue());


                    Button writeWrapStatistics = new Button("Save statistics");
                    Button writeBoundStatistics = new Button("Save statistics");

                    HBox wrapMapBox = createMapBox(wrapMap, wrapGridPane, wrapMapStatistics, wrapDominantGenotypeLabel, wrapTwoWayButton, writeWrapStatistics, wrapMagicLabel);
                    HBox boundMapBox = createMapBox(boundMap, boundGridPane, boundMapStatistics, boundDominantGenotypeLabel, boundTwoWayButton, writeBoundStatistics, boundMagicLabel);

                    writeWrapStatistics.setOnAction(event ->{
                        wrapWriter.write();
                    });
                    writeBoundStatistics.setOnAction(event ->{
                        boundWriter.write();
                    });

                    if (wrapIsMagic) {
                        wrapMagicLabel.setText("MAGIC IS HERE");
                    }
                    if (boundIsMagic) {
                        boundMagicLabel.setText("MAGIC IS HERE");
                    }

                    VBox view = new VBox();
                    view.getChildren().add(wrapMapBox);
                    view.getChildren().add(boundMapBox);
                    view.getChildren().add(runButton);

                    Scene simulation = new Scene(view, 1000, 800);
                    primaryStage.setScene(simulation);
                }
            });
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
//            wrapWriter.write();
//            boundWriter.write();
            primaryStage.close();
            if(wrapEngine != null && boundEngine != null){
                wrapEngine.shutdown();
                boundEngine.shutdown();
            }
        });
    }

    private void updateMagicLabel(AbstractWorldMap map) {
        if (map == wrapMap){
            wrapMagicLabel = new Label("MAGIC HAPPENED: " + map.magicCounter);
        }
        else if(map == boundMap){
            boundMagicLabel = new Label("MAGIC HAPPENED: " + map.magicCounter);
        }
    }

    private void setSimulation() {
        gridPaneWidth = baseWidth / mapWidth;
        gridPaneHeight = baseHeight / mapHeight;
        this.wrapMap = new WrappedMap(jungleRatio, mapWidth, mapHeight, plantEnergy, initialEnergy);
        wrapMap.isMagic = wrapIsMagic;
        this.wrapEngine = new SimulationEngine(wrapMap, animalAmount, initialEnergy, moveEnergy);
        Thread wrapThread = new Thread(wrapEngine);
        this.boundMap = new BoundedMap(jungleRatio, mapWidth, mapHeight, plantEnergy, initialEnergy);
        boundMap.isMagic = boundIsMagic;
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

        HBox magicBtns = new HBox(20);
        RadioButton isMagicWrapButton = new RadioButton("Wrap Magic");
        RadioButton isMagicBoundButton = new RadioButton("Bound Magic");
        magicBtns.getChildren().add(isMagicBoundButton);
        magicBtns.getChildren().add(isMagicWrapButton);
        inputGrid.add(magicBtns, 1, 8);

        Button submit = new Button("Submit parameters");
        simulationStart = new Button("Start simulation");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(submit);
        hbBtn.getChildren().add(simulationStart);
        inputGrid.add(hbBtn, 1, 9);
        isMagicBoundButton.setOnAction(actionEvent -> {
            boundIsMagic = !boundIsMagic;
        });
        isMagicWrapButton.setOnAction(actionEvent -> {
            wrapIsMagic = !wrapIsMagic;
        });
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

    private HBox createMapBox(AbstractWorldMap map, GridPane gridPane, GuiMapStatistics mapStatistics, Label dominantGenotypeLabel, Button twoWayButton, Button saveStatistics, Label magicLabel) {
        HBox mapBox = new HBox(10);
        mapBox.getChildren().add(gridPane);
        VBox wrapStatisticsBox = new VBox();
        wrapStatisticsBox.getChildren().add(mapStatistics.lineChart);
        wrapStatisticsBox.getChildren().add(dominantGenotypeLabel);
        wrapStatisticsBox.setAlignment(Pos.CENTER);
        mapBox.getChildren().add(wrapStatisticsBox);
        VBox buttonBox = new VBox();
        buttonBox.getChildren().add(twoWayButton);
        buttonBox.getChildren().add(saveStatistics);
        VBox trackBox = createTrackingBox(map);
        mapBox.getChildren().add(buttonBox);
        mapBox.getChildren().add(trackBox);
        mapBox.getChildren().add(magicLabel);
        mapBox.setAlignment(Pos.CENTER_LEFT);
        return mapBox;
    }

    private VBox createTrackingBox(AbstractWorldMap map) {
        VBox trackBox = new VBox();
        if (map == wrapMap){
            wrapNumOfChildrenLabel = new Label();
            wrapNumOfDescendantsLabel = new Label();
            wrapDeathEra = new Label();
            trackBox.getChildren().add(wrapNumOfChildrenLabel);
            trackBox.getChildren().add(wrapNumOfDescendantsLabel);
            trackBox.getChildren().add(wrapDeathEra);
        }
        if (map == boundMap){
            boundNumOfChildrenLabel = new Label();
            boundNumOfDescendantsLabel = new Label();
            boundDeathEra = new Label();
            trackBox.getChildren().add(boundNumOfChildrenLabel);
            trackBox.getChildren().add(boundNumOfDescendantsLabel);
            trackBox.getChildren().add(boundDeathEra);
        }
        return trackBox;
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
                if (boundIsTracked){
                    updateTrackingBox(map, boundNumOfChildrenLabel, boundNumOfDescendantsLabel, boundDeathEra);
                }
            }
            else if (map == wrapMap){
                wrapMapStatistics.updateMapData(wrapEngine.era);
                wrapWriter.generateData(map);
                wrapDominantGenotypeLabel.setText(Arrays.toString(wrapMapStatistics.dominantGenotype.getKey())
                        + " " + wrapMapStatistics.dominantGenotype.getValue());
                if (wrapIsTracked){
                    updateTrackingBox(map, wrapNumOfChildrenLabel, wrapNumOfDescendantsLabel, wrapDeathEra);
                }
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
                            map.track(animal);
                            if (map instanceof WrappedMap){
                                wrapIsTracked = true;
                            }
                            else if (map instanceof  BoundedMap){
                                boundIsTracked = true;
                            }
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

    private void updateTrackingBox(AbstractWorldMap map, Label numOfChildrenLabel, Label numOfDescendantsLabel, Label deathEra) {
        numOfChildrenLabel.setText("Children number: " + map.trackedAnimal.trackedChildrenNum);
        numOfDescendantsLabel.setText("Descendants number: " + map.trackedAnimal.descendants.size());
        deathEra.setText("Death era: " + map.trackedAnimal.getDeathEra());
    }
}
