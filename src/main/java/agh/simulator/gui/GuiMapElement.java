package agh.simulator.gui;

import agh.simulator.Animal;
import agh.simulator.Plant;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;


public class GuiMapElement extends Label {
    private final Object object;
    private Color color;
    public GuiMapElement(Object object){
        this.object = object;
        this.color = setColor();
    }

    public Label getElementLabel(){
        Label objectLabel = new Label();
        objectLabel.setAlignment(Pos.CENTER);
        objectLabel.setBackground(new Background(new BackgroundFill(this.color, CornerRadii.EMPTY, Insets.EMPTY)));
        return objectLabel;
    }

    public Color setColor(){
        if (object instanceof Plant){
            this.color = Color.GREEN;
        }
        if (object instanceof Animal animal){
            int energy = animal.getEnergy();
            if (energy <= 20){
                this.color = Color.rgb(151, 155, 148);
            }
            else if (energy <= 30){
                this.color = Color.rgb(191, 155, 128);
            }
            else if (energy <= 50){
                this.color = Color.rgb(138, 94, 62);
            }
            else if (energy <= 70) {
                this.color = Color.rgb(102, 72, 50);
            }
            else if (energy <=  1000){
                this.color = Color.rgb(77, 55, 39);
            }
            else{
                this.color = Color.BLACK;
            }
        }
        return this.color;
    }
}
