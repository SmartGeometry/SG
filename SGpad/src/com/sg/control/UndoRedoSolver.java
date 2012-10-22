/*
 * 撤销恢复类
 * 该类用于图形的撤销及恢复操作
 * */
package com.sg.control;

import java.util.Stack;

import com.sg.object.graph.Graph;

public class UndoRedoSolver {
	
	private static UndoRedoSolver instance = new UndoRedoSolver();
	private Stack<UndoRedoStruct> UndoStack;
	private Stack<UndoRedoStruct> RedoStack;

	public UndoRedoSolver(){
		UndoStack = new Stack<UndoRedoStruct>();
		RedoStack = new Stack<UndoRedoStruct>();
	}

	public void EnUndoStack(UndoRedoStruct data){
		UndoStack.push(data);
	}
	
	public void EnRedoStack(UndoRedoStruct data){
		RedoStack.push(data);
	}
	
	public UndoRedoStruct popUndoStack() {
		RedoStack.push(UndoStack.peek());
		return UndoStack.pop();
	}	
	
	public UndoRedoStruct peekUndoStack(){
		return UndoStack.peek();
	}
	
	public UndoRedoStruct popRedoStack() {
		UndoStack.push(RedoStack.peek());
		return RedoStack.pop();
	}

	public static UndoRedoSolver getInstance() {
		return instance;
	}

	public void RedoStackClear() {
		RedoStack.clear();		
	}
	
	public void UndoStackClear() {
		UndoStack.clear();		
	}

	public boolean isRedoStackEmpty() {
		return RedoStack.empty();
	}
	
	public boolean isUndoStackEmpty() {
		return UndoStack.empty();
	}
	
	public Graph getFrontGraph(Graph graph){
		Graph temp = null;
		//找到改变前的图形
		/*
		for(UndoRedoStruct struct : UndoStack){
			if(struct.getGraph().getID() == graph.getID()){
				temp = struct.getGraph();
			}
		}
		*/
		//不需要全遍历一遍，只要从后向前找到第一个即可
		int num = UndoStack.size();
		for(int index = num - 1; index >= 0; index--){
			temp = UndoStack.get(index).getGraph();
			if(temp.getID() == graph.getID()){
				return temp;
			}
		}
		return temp;
	}
}