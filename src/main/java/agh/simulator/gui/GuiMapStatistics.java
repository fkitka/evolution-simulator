package agh.simulator.gui;

import agh.simulator.AbstractWorldMap;
import javafx.collections.ObservableList;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.util.Pair;

import java.util.Arrays;

public class GuiMapStatistics {
    private final AbstractWorldMap map;

    private final XYChart.Series<Number, Number> averageLifespanSeries;
    private final XYChart.Series<Number, Number> averageEnergySeries;
    private final XYChart.Series<Number, Number> averageNumOfChildren;
    private final XYChart.Series<Number, Number> animalPopulationSeries;
    private final XYChart.Series<Number, Number> plantPopulationSeries;

    protected LineChart<Number, Number> lineChart;
    protected Pair<int[], Integer> dominantGenotype;


    public GuiMapStatistics(AbstractWorldMap map) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        this.map = map;
        lineChart = new LineChart<>(xAxis, yAxis);
        animalPopulationSeries = new XYChart.Series<>();
        plantPopulationSeries = new XYChart.Series<>();
        averageLifespanSeries = new XYChart.Series<>();
        averageEnergySeries = new XYChart.Series<>();
        averageNumOfChildren = new XYChart.Series<>();
        animalPopulationSeries.setName("Animal population");
        plantPopulationSeries.setName("Plant population");
        averageEnergySeries.setName("Average energy");
        averageLifespanSeries.setName("Average lifespan");
        averageNumOfChildren.setName("Average number of children");
        updateMapData(1);
        lineChart.getData().addAll(animalPopulationSeries,  averageEnergySeries, plantPopulationSeries, averageLifespanSeries, averageNumOfChildren);
        lineChart.setCreateSymbols(false);
    }

    private void getMapData(int era, XYChart.Series<Number, Number> series) {
        if (animalPopulationSeries.equals(series)) {
            series.getData().add(new XYChart.Data<>(era, map.statistics.getAnimalsSize()));
        } else if (plantPopulationSeries.equals(series)) {
            series.getData().add(new XYChart.Data<>(era, map.statistics.getPlantsSize()));
        } else if (averageLifespanSeries.equals(series)) {
            series.getData().add(new XYChart.Data<>(era, map.statistics.getLifespan()));
        } else if (averageEnergySeries.equals(series)) {
            series.getData().add(new XYChart.Data<>(era, map.statistics.getAverageEnergy()));
        } else if (averageNumOfChildren.equals(series)) {
            series.getData().add(new XYChart.Data<>(era, map.statistics.getAverageNumOfChildren()));
        }
    }

    protected void updateMapData(int era){
        getMapData(era, plantPopulationSeries);
        getMapData(era, animalPopulationSeries);
        getMapData(era, averageLifespanSeries);
        getMapData(era, averageEnergySeries);
        getMapData(era, averageNumOfChildren);
        dominantGenotype = map.statistics.getDominantGenotype();
    }

}
