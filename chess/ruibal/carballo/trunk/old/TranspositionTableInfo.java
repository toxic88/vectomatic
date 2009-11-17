package com.alonsoruibal.chess.tt;

import java.io.Serializable;

public class TranspositionTableInfo implements Serializable {
	private static final long serialVersionUID = 6986417059197703725L;
	
	NodeType nodeType;
	int score;
	int depthAnalyzed;
	int bestMove;
	
	public TranspositionTableInfo() {
	}
	
	public TranspositionTableInfo(NodeType nodeType, int score, int depthAnalyzed, int bestMove) {
		this.nodeType = nodeType;
		this.score = score;
		this.depthAnalyzed = depthAnalyzed;
		this.bestMove = bestMove;
	}

	public NodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getBestMove() {
		return bestMove;
	}

	public void setBestMove(int bestMove) {
		this.bestMove = bestMove;
	}

	public int getDepthAnalyzed() {
		return depthAnalyzed;
	}
	
	public void setDepthAnalyzed(int depthAnalyzed) {
		this.depthAnalyzed = depthAnalyzed;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("type = ");
		sb.append(nodeType);
		sb.append(" score = ");
		sb.append(score);
		sb.append(" depthAnalyzed = ");
		sb.append(depthAnalyzed);
		sb.append(" bestMove = ");
		sb.append(bestMove);
		return sb.toString();
	}	
}
