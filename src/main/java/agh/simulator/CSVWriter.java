package agh.simulator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class CSVWriter {
    private final AbstractWorldMap map;
    private final StringBuilder title;
    private final StringBuilder eras;
    protected final StringBuilder document;
    public CSVWriter(AbstractWorldMap map){
        this.map = map;
        this.title = generateTitle();
        this.eras = new StringBuilder();
        this.document = new StringBuilder();
    }
    public void write(){
        String fileName;
        if (map instanceof WrappedMap){
            fileName = "wrap.csv";
        }
        else if(map instanceof BoundedMap){
            fileName = "bound.csv";
        }
        else{
            fileName = "";
        }

        try (PrintWriter writer = new PrintWriter(fileName)){
            document.append(title);
            document.append(eras);
            writer.write(document.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private StringBuilder generateTitle() {
        StringBuilder title = new StringBuilder();
        title.append("animalCount");
        title.append(",");
        title.append("plantCount");
        title.append(",");
        title.append("averageEnergy");
        title.append(",");
        title.append("averageLifespan");
        title.append(",");
        title.append("averageNumOfChildren");
        title.append("\n");
        return title;
    }
    public StringBuilder generateData(AbstractWorldMap map){
        eras.append(addData(map));
        return eras;
    }
    private StringBuilder addData(AbstractWorldMap map){
        StringBuilder eraData = new StringBuilder();
        eraData.append(map.animalList.size());
        eraData.append(",");
        eraData.append(map.statistics.getPlantsSize());
        eraData.append(",");
        eraData.append(map.statistics.getAverageEnergy());
        eraData.append(",");
        eraData.append(map.statistics.getLifespan());
        eraData.append(",");
        eraData.append(map.statistics.getAverageNumOfChildren());
        eraData.append("\n");
        return eraData;
    }
}
