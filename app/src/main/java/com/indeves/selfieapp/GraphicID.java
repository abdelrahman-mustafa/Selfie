package com.indeves.selfieapp;


import java.util.ArrayList;
import java.util.List;

public class GraphicID {
List <CameraButtons> list = new ArrayList<>();

    public GraphicID(List<CameraButtons> list) {
        this.list = list;
    }

    public GraphicID() {
    }

    public List<CameraButtons> getList() {
        return list;
    }

    public void setList(List<CameraButtons> list) {
        this.list = list;
    }
}