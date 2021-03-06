package org.ggp.base.player.gamer.statemachine.sample.gpp_player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.apps.player.detail.SimpleDetailPanel;
import org.ggp.base.player.gamer.event.GamerSelectedMoveEvent;
import org.ggp.base.player.gamer.exception.GamePreviewException;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.cache.CachedStateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;

/**
 * SampleMonteCarloGamer is a simple state-machine-based Gamer. It will use a
 * pure Monte Carlo approach towards picking moves, doing simulations and then
 * choosing the move that has the highest expected score. It should be slightly
 * more challenging than the RandomGamer, while still playing reasonably fast.
 *
 * However, right now it isn't challenging at all. It's extremely mediocre, and
 * doesn't even block obvious one-move wins. This is partially due to the speed
 * of the default state machine (which is slow) and mostly due to the algorithm
 * assuming that the opponent plays completely randomly, which is inaccurate.
 *
 * @author Sam Schreiber
 */


public final class SampleMonteCarloGamer extends SampleGamer
{
	 @Override
	 public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	 {
	        // Do nothing.
	 }
    /**
     * Employs a simple sample "Monte Carlo" algorithm.
     */

	 public static int playoutCount=500;
	 public static void pcSet(int i) {
		 playoutCount=i;
	 }

	 @Override
    public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
    {
        StateMachine theMachine = getStateMachine();
        long start = System.currentTimeMillis();
        long finishBy = timeout - 1000;
        System.out.println("current:"+System.currentTimeMillis());
        System.out.println("timeout:"+timeout);
        System.out.println("state  :"+getCurrentState());
        List<Move> moves = theMachine.getLegalMoves(getCurrentState(), getRole());
        Move selection = moves.get(0);

        //System.out.println("!!!!!:"+moves);

        if (moves.size() > 1) {
            int[] moveTotalPoints = new int[moves.size()];
            int[] moveTotalAttempts = new int[moves.size()];

            // Perform depth charges for each candidate move, and keep track
            // of the total score and total attempts accumulated for each move.
            int count=0;
            for (int i = 0; true; i = (i+1) % moves.size()) {
            	count++;
            	//System.out.println("now:"+System.currentTimeMillis());
                if (System.currentTimeMillis() > finishBy || count>playoutCount)
                    break;
                int theScore = performDepthChargeFromMove(getCurrentState(), moves.get(i));
                moveTotalPoints[i] += theScore;
                moveTotalAttempts[i] += 1;
            }
            System.out.println("@@@@@@@sample monte carlo--count:"+count);
            // Compute the expected score for each move.
            double[] moveExpectedPoints = new double[moves.size()];
            for (int i = 0; i < moves.size(); i++) {
                moveExpectedPoints[i] = (double)moveTotalPoints[i] / moveTotalAttempts[i];
            }

            // Find the move with the best expected score.
            int bestMove = 0;
            double bestMoveScore = moveExpectedPoints[0];
            for (int i = 1; i < moves.size(); i++) {
                if (moveExpectedPoints[i] > bestMoveScore) {
                    bestMoveScore = moveExpectedPoints[i];
                    bestMove = i;
                }
            }
            selection = moves.get(bestMove);
        }

        /*
        System.out.println("endtime:"+System.currentTimeMillis());
        if(root==null)
        	root=new Node(getCurrentState());
        else {
        	Node parentNode=nodeSearch(root,parentKey);
        	//System.out.println("parentKey :"+parentKey);
        	//System.out.println("parentNode:"+parentNode.state);
        	parentNode.expand(getCurrentState());
        }

        //showAll(root);
        System.out.println("?????:"+theMachine.getRoles());
        System.out.println("?????:"+selection);
        MachineState a=theMachine.getRandomNextState(getCurrentState(),theMachine.getRoles().get(1),selection);
        System.out.println("test getRandomNextState:::"+a);
        MachineState b=theMachine.getRandomNextState(getCurrentState(),theMachine.getRoles().get(1),selection);
        System.out.println("test getRandomNextState:::"+b);
        MachineState c=theMachine.getRandomNextState(getCurrentState(),theMachine.getRoles().get(1),selection);
        System.out.println("test getRandomNextState:::"+c);
        MachineState d=theMachine.getRandomNextState(getCurrentState(),theMachine.getRoles().get(1),selection);
        System.out.println("test getRandomNextState:::"+d);

        //System.out.println(theMachine.getNextStates(getCurrentState(),getRole()));
        System.out.println("getRandomMove:"+theMachine.getRandomMove(getCurrentState(),getRole()));

        int[] testDepth=new int[1];
        //System.out.println(theMachine.performDepthCharge(getCurrentState(),testDepth));
        //System.out.println(testDepth[0]);
        parentKey=getCurrentState();
        */
        long stop = System.currentTimeMillis();
        notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
        return selection;
    }


    private int[] depth = new int[1];
    int performDepthChargeFromMove(MachineState theState, Move myMove) {
        StateMachine theMachine = getStateMachine();
        try {
            MachineState finalState = theMachine.performDepthCharge(theMachine.getRandomNextState(theState, getRole(), myMove), depth);
            return theMachine.getGoal(finalState, getRole());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public int count=0;

    public class Node{
    	int w; //times of winner
    	int n; //times of visited
    	MachineState state;//Board information
    	double uctValue;
    	double winRateSave;
    	Node child;

    	Map<MachineState ,Node>children;

    	Node(MachineState state){
    	    this.state=state;
    	    this.w=0;
    	    this.n=0;
    	    this.uctValue=0;
    	    this.child=null;
    	    this.children= new HashMap<MachineState ,SampleMonteCarloGamer.Node>();
    	}

    	public void expand(MachineState state){
    	    this.children.put(state,new Node(state));
    	}

    }

    public Node root=null;
    public MachineState parentKey;

    public Node nodeSearch(Node n,MachineState key){
    	if(n.children==null)
    		return null;
	    if(n.state.equals(key)){
	        return n;
		}

	    if(n.children.containsKey(key)) {
	    	return n.children.get(key);
	    }else {
	    	for(MachineState k:n.children.keySet()){
	    		Node next=n.children.get(k);
	    		next=nodeSearch(next,key);
	    		if(next!=null)
	    			return next;
	    	}
	    }
	    return null;
	}


    public void showAll(Node n) {
    	if(n==root)
    		System.out.println(n.state);
    	if(n.children == null){
    		return;
    	}
    	int i=0;
    	for(MachineState key:n.children.keySet()){
    		System.out.println("times of i ==== "+i++);
    		Node next=n.children.get(key);
    		System.out.println(next.state);
    		//System.out.println("n:::"+next.n+",w:::"+next.w);
    		//nxt.printItem(nxt.list);
    		showAll(next);
    	}
    }

    @Override
    public void stateMachineStop() {
        // Do nothing.
    }
    /**
     * Uses a CachedProverStateMachine
     */
    @Override
    public StateMachine getInitialStateMachine() {
        return new CachedStateMachine(new ProverStateMachine());
    }

    @Override
    public String getName() {
        return "sampleMC";
    }

    @Override
    public DetailPanel getDetailPanel() {
        return new SimpleDetailPanel();
    }

    @Override
    public void preview(Game g, long timeout) throws GamePreviewException {
        // Do nothing.
    }

    @Override
    public void stateMachineAbort() {
        // Do nothing.
    }
}