package hw4.consensus.bft;

import java.util.ArrayList;
import java.util.List;

import hw4.net.Id;
import hw4.util.HashMapList;

public class EIGTree{
	private int numNodes;
	private int numLevel ; //level of EIG = numMalicious + 1
	private int numMalicious =  numLevel - 1;
	
	private HashMapList<Id,UnauthBFTPayload> tree;
	
	public EIGTree(int numNodes, int numLevel){
		this.numLevel = numLevel;
		this.numNodes = numNodes;
	}
	
	public int getNumNodes(){
		return numNodes;
	}
	
	public int getNumMalicious(){
		return numMalicious;
	}
	
	public int getNumLevels(){
		return numLevel;
	}
	
	public HashMapList<Id,UnauthBFTPayload> buildTree(){
		HashMapList<Id, UnauthBFTPayload> tree = new HashMapList<Id, UnauthBFTPayload>();
		int level = 0;
		
		for (int k=0; k<getNumNodes(); k++){
			List<Id> listid = new ArrayList<>();
			Id nodeid = new Id(k);
			listid.add(nodeid);
			
			Trace tr1 = new Trace(listid);
			UnauthBFTPayload pl1 = new UnauthBFTPayload(tr1, null);
			tree.put(nodeid, pl1);
			
			UnauthBFTPayload temp = pl1;
			level = level + 1;
			
			while (level<getNumLevels()){
				for (int m=0; m<getNumNodes(); m++){
					Trace oldtrace = temp.getTrace();
					if(!oldtrace.getTrace().contains(new Id(m))){
						Trace newtrace = Trace.append(oldtrace, new Id(m));
						UnauthBFTPayload pl = new UnauthBFTPayload(newtrace, null);
						tree.put(nodeid, pl);
						temp = pl;
					}
				}
				level ++ ;
			}
		}
		this.tree = tree;
		return tree;
	}
	
	public HashMapList<Id,UnauthBFTPayload> getTree(){
		return this.tree;
	}
	
	
}