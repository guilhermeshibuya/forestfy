package com.example.reconhecimentoflorestal.ml;

public class ModelResults {
    public float[] results;
    public int[] indices;

    public ModelResults(float[] results, int[] indices) {
        this.results = results;
        this.indices = indices;
    }
}
