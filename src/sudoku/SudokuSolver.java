package sudoku;

import java.util.ArrayList;
import java.util.List;
/**
 * Place for your code.
 */
public class SudokuSolver {

	
	private int foundSolution=99;
	
	/**
	 * @return "Pedreira, Matheus 56423149\n" + "Mazarini, Pedro 56398143\n"
	 */
	public String authors() {
		// TODO write it;
		return "Pedreira, Matheus 56423149\n" + "Mazarini, Pedro 56398143\n" ;
	}

	/**
	 * Performs constraint satisfaction on the given Sudoku board using Arc Consistency and Domain Splitting.
	 * 
	 * @param board the 2d int array representing the Sudoku board. Zeros indicate unfilled cells.
	 * @return the solved Sudoku board
	 */
	public int[][] solve(int[][] board) {
		int i=0,j=0,k=0,l=0;
		foundSolution=99;
		Cell[][] tempboard = new Cell[9][9];
		//Start tempboard with board
		
		for(i=0;i<9;i++){
			for(j=0;j<9;j++){
				tempboard[i][j]=new Cell();
				if(board[i][j]!=0){
					tempboard[i][j].removeNotValue(board[i][j]);
				}
			}
		}
		
		tempboard = constrainVerification(0,0,tempboard[0][0].valueDomain,tempboard);
		//Return result
		int[][] finalBoard = new int[9][9];
		for(i=0;i<9;i++){
			for(j=0;j<9;j++){
				finalBoard[i][j]=tempboard[i][j].fixedValue;
			}
		}
		return finalBoard;
	}
	
	private Cell[][] constrainVerification(int cellX, int cellY, List<Integer> domainSplit,Cell[][] board){
		int constraintValidationCounter=0;
		int i=0,j=0,k=0,l=0;
		int x=0,y=0;
		int size=9;
		
		Cell[][] tempboard = new Cell[9][9];
		Cell[][] tempboardLeft = new Cell[9][9];
		Cell[][] tempboardRight = new Cell[9][9];
		Cell[][] tempboard2 = new Cell[9][9];
		for(i=0;i<9;i++){
			for(j=0;j<9;j++){
				tempboard[i][j]=new Cell();
				if(board[i][j].fixedValue!=0){
					tempboard[i][j].removeNotValue(board[i][j].fixedValue);
				}
			}
		}
		//Atribute Split Domain
		tempboard[cellX][cellY].valueDomain.clear();
		for(i=0;i<domainSplit.size();i++){
			tempboard[cellX][cellY].valueDomain.add(domainSplit.get(i));
		}
		if(tempboard[cellX][cellY].valueDomain.size()==1){
			tempboard[cellX][cellY].fixedValue=tempboard[cellX][cellY].valueDomain.get(0);
		}
		//Perform constrain verification
		for(i=0;i<9;i++){
			for(j=0;j<9;j++){
				//1. Verify line constraint
				for(k=0;k<9;k++){ 
					if(tempboard[k][j].fixedValue!=0 && k!=i){
						if(tempboard[i][j].removeValue(tempboard[k][j].fixedValue)==-1){
							return tempboard;
						}
					} 
				}
				//2. Verify column constraint
				for(l=0;l<9;l++){ 
					if(tempboard[i][l].fixedValue!=0 && l!=j){
						if(tempboard[i][j].removeValue(tempboard[i][l].fixedValue)==-1){
							return tempboard;
						}
					} 
				}
				//3. Verify block constraint
				//Discover left_top corner of the block
				int xOfBlock=i-(i%3);  
				int yOfBlock=j-(j%3); 
				//Run at the block
				for(k=xOfBlock;k<xOfBlock+3;k++){
				      for(l=yOfBlock;l<yOfBlock+3;l++){
				         if(tempboard[k][l].fixedValue!=0){
				        	 if( k!=i || l!=j){
				        		 if(tempboard[i][j].removeValue(tempboard[k][l].fixedValue)==-1){
				        			 return tempboard; 
				        		 } 
				        	 }
				         }
				    }
				}
				constraintValidationCounter=constraintValidationCounter+(tempboard[i][j].valueDomain.size()-1);
			}
		}
		if(constraintValidationCounter==0){
			foundSolution=0;
			return tempboard;
		}
		//Choose cell to split by evaluating the smallest size domain.
		for(i=0;i<9;i++){
			for(j=0;j<9;j++){
		      	if(tempboard[i][j].valueDomain.size()<size && tempboard[i][j].valueDomain.size()>1){
		      		size=tempboard[i][j].valueDomain.size();
		      		x=i;
		      		y=j;
		      	}
		      	if(size==2){
		      		break;
		      	}
			}
			if(size==2){
	      		break;
	      	}
		}
		//Divide list
		if(size!=9){
			
			//Copy board to left and right
			for(i=0;i<9;i++){
				for(j=0;j<9;j++){
					tempboardLeft[i][j]= new Cell();
					tempboardRight[i][j]= new Cell();
					tempboardLeft[i][j].valueDomain.clear();
					tempboardRight[i][j].valueDomain.clear();
					for(k=0;k<tempboard[i][j].valueDomain.size();k++){
						tempboardLeft[i][j].valueDomain.add(tempboard[i][j].valueDomain.get(k));
						tempboardRight[i][j].valueDomain.add(tempboard[i][j].valueDomain.get(k));
					}
					if(tempboard[i][j].valueDomain.size()==1){
						tempboardLeft[i][j].fixedValue=tempboardLeft[i][j].valueDomain.get(0);
						tempboardRight[i][j].fixedValue=tempboardRight[i][j].valueDomain.get(0);
					}
				}
			}
			List<Integer> leftList = new ArrayList<Integer>();
			List<Integer> rightList = new ArrayList<Integer>();
			for(i=0;i<size/2;i++){
				leftList.add(tempboard[x][y].valueDomain.get(i));
			}
			for(i=size/2;i<size;i++){
				rightList.add(tempboard[x][y].valueDomain.get(i));
			}	
			tempboard2 = constrainVerification(x,y,leftList,tempboardLeft);
			if(foundSolution==0){
				return tempboard2;
			}else{
				tempboard2 = constrainVerification(x,y,rightList,tempboardRight);
			}
		}
		return tempboard2;
	}
	
}

class Cell{
	List<Integer> valueDomain; 
	int fixedValue=0;
	public Cell(){
		int i=1;
		valueDomain = new ArrayList<Integer>();
		for(i=1;i<10;i++){
			valueDomain.add(i);
		}
	}
	
	public int removeValue(int boardCell){
		for(int i=0;i<valueDomain.size();i++){
			if(boardCell==valueDomain.get(i)){
				valueDomain.remove(i);
			}
			if(valueDomain.size()==1){
				fixedValue=valueDomain.get(0);
			}else if(valueDomain.size()==0){
				fixedValue=0;
				return -1;
			}
		}
		return valueDomain.size();
	}
	
	public int removeNotValue(int boardCell){
		for(int i=0;i<valueDomain.size();i++){
			if(boardCell!=valueDomain.get(i)){
				valueDomain.remove(i);
				i--;
			}
		}
		if(valueDomain.size()==1){
			fixedValue=valueDomain.get(0);
		}
		return valueDomain.size();
	}
	
}