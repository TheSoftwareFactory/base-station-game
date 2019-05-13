package com.example.base_station_game.sampling.structs;

import java.io.Serializable;
import java.util.List;

public class CellDetails implements Serializable {
    private List<String> cells;

    public CellDetails(List<String> cells) {
        this.cells = cells;
    }

    public List<String> getCells() {
        return cells;
    }

    public void setCells(List<String> cells) {
        this.cells = cells;
    }

    @Override
    public String toString() {
        return cells.toString();
    }
}
