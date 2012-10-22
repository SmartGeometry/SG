package com.sg.control;

import com.sg.object.graph.*;


public class UndoRedoStruct {

	private OperationType operationType;
	private Graph graph;

	public UndoRedoStruct(){
		operationType = OperationType.NONE;
		graph = null;
	}

	public UndoRedoStruct(OperationType operationType, Graph graph) {
		super();
		this.operationType = operationType;
		this.graph = graph;
	}
	
		
	public OperationType getOperationType() {
		return operationType;
	}

	public Graph getGraph() {
		return graph;
	}

	public void setOperationType(OperationType data) {
		this.operationType = data;		
	}

	public void setGraph(Graph graph) {
		this.graph = graph;		
	}
}