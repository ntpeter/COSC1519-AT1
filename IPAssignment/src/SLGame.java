import javax.swing.*;
import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import java.util.*;

public class SLGame
{
   /* COSC1519 INTRODUCTION TO PROGRAMMING | AT1 
	* 	S3602814 Thien Tri Nguyen
	* 
	* -- PROGRAM SUMMARY --
	* PART 1 MODIFICATIONS
	* - Used the first names of the players for interaction
	* 	> Prompt players by name to indicate their turn/dice roll
	* 	> Prompt players when climbing a ladder/sliding down a snake/stuck on a trap
	* 	> Remind the player how many turns till they are not stuck on a trap
	* 	> Notify the player when they have won the game
	* - Used loops to allow players to take turns rolling the dice
	* - Allowed the player to roll again if a 6 was thrown
	* - Used validation to check game logic against the player's location
	* 	> Return the player to their previous location if they land over square 100
	* 	> Stop the game when a player has reached square 100
	* 
	* PART 2 MODIFICATIONS
	* - Allow players the option to create a custom board
	* 	> Total no. of ladders and snakes cannot exceed 15
	* 	> Total no. of traps cannot exceed 5 with durations between 3 and 5
	* 	> End-points of all snakes and ladders inserted must be within positions 1 to 100
	*  	> Snake head must always have a higher location than the tail. (cannot go up the snake)
	*   > Ladder bottom must have a lower value than ladder top (cannot go down a ladder)
	*   > Snake head cannot be positioned at 100 and ladder bottom cannot be positioned at 1
	*   > No snake head can have the same position as any other snake head or tail
	*   > No ladder bottom can have the same position as any other ladder bottom or top
	*   > No snake head can have the same position as any ladder bottom or top
	*   > No ladder bottom can have the same position as any snake head or snake tail
	*   > No trap can be placed on top of a snake and ladder end points
	* - Modified game logic to be implemented using arrays and loops
	* - Allow players the option to replay the board or create a new board
	* 
	* BONUS ATTEMPTED
	* No. 1 | Give players the option to lay a trap while the game is being played
	* - If the player lays a trap, their turn ends
	* - If a player lands on that trap, they will lose double the number of moves
	*/

   //DECLARING ATTRIBUTES
   //Core aspects
   int numPlayers = 2, val, win;
   
   //Creating a Board, dice and a Scanner objects
   Board bd = new Board();
   Dice dice = bd.getDice();
   Scanner scan = new Scanner(System.in);
   
   //Player features
   /* Array player[][]
	* [p] = index for player turn
	* [#] = selection of:
	* 	[p][0] player location
	* 	[p][1] player trap turn counter
	*/
   int player[][] = new int[numPlayers][2];
   String name[] = new String[2];  // array for storing the names
   int temp;
   boolean namesAsked = false;
   
   //Game features
   int choice; //Menu selection
   int snakesCount, laddersCount, trapsCount; //Counter for no. of snakes, ladders and traps
   Snake snakes[] = new Snake[10];    // array Can store up to 10 Snake objects
   Ladder ladders[] = new Ladder[10]; // array Can store up to 10 Ladder objects
   Trap traps[] = new Trap[10];
   int location, duration, head, tail, top, bottom;
   boolean ok;
   int setTrap;
   
   //Loop control
   String playerName = null;
   int newBoard, playerTurn, p, i, j;
   boolean end = false; //Allows the user to decide whether to play again or end the game
   
   
   public void setup(Board bd)
   {
	  //Collects the names of the players (only done once)
	  if(namesAsked == false){
		  //Collect the names of the players
		  name[0] = getString("Player 1 name : ");
		  name[1] = getString("Player 2 name : ");
		  namesAsked = true;
	  }
	  
	  //Prevents creating a new board midgame
	  newBoard = 0;
	  
	  //Reset the no. of snakes, ladders, and traps to 0
	  trapsCount = 0;
	  snakesCount = 0;
	  laddersCount = 0;
	  
	  //BOARD MENU
	  choice = getInt("Input the number corresponding to the board you want to play: \n1. Classic \n2. Custom", 1, 2);
	  if(choice == 1) classicBoard();
	  if(choice == 2) customBoard();
   }
   
   public void newGame(){
	   //Reset game control variables
	   temp = 0;
	   playerTurn = 0;
	   p = 0;
	   win = -1;
	   setTrap = 0;
	   
	   //Sets player pieces on the board at square labelled 1
	   player[0][0] = 1;
	   player[1][0] = 1;
	   bd.setPiece(1,player[0][0]);
	   bd.setPiece(2,player[1][0]);
	   
	   //Clears the display board
	   bd.clearMessages();
   }
   
   public void classicBoard(){
	   traps[trapsCount++] = new Trap(25,3);
	   bd.add(new Trap(traps[0].getLocation(),traps[0].getDuration()));
	   traps[trapsCount++] = new Trap(95,3);
	   bd.add(new Trap(traps[1].getLocation(),traps[1].getDuration()));
	   
	   snakes[snakesCount++] = new Snake(92,34);
	   bd.add(new Snake(snakes[0].getHead(),snakes[0].getTail()));
	   snakes[snakesCount++] = new Snake(62,12);
	   bd.add(new Snake(snakes[1].getHead(),snakes[1].getTail()));
	   snakes[snakesCount++] = new Snake(41,3);
	   bd.add(new Snake(snakes[2].getHead(),snakes[2].getTail()));
	   
	   ladders[laddersCount++] = new Ladder(7,49);
	   bd.add(new Ladder(ladders[0].getBottom(),ladders[0].getTop()));
	   ladders[laddersCount++] = new Ladder(55,90);
	   bd.add(new Ladder(ladders[1].getBottom(),ladders[1].getTop()));
	   ladders[laddersCount++] = new Ladder(38,86);
	   bd.add(new Ladder(ladders[2].getBottom(),ladders[2].getTop()));
   }
   
   public void customBoard(){
	   //Check if total no. of snakes and ladders does not exceed 15
	   do{
		   snakesCount = getInt("1/3 Enter the number of snakes you want in the game: ", 1, 10);
		   laddersCount = getInt("2/3 Enter the number of ladders you want in the game: ", 1, 10);
		   temp = snakesCount + laddersCount;
		   if(temp > 15){
			   plainMessage("Total no. of snakes and ladders exceeded 15, please put in different values.");
			   temp = 0;
		   }
	   }while(temp < 1 || temp > 15);
	   trapsCount = getInt("3/3 Enter the number of traps you want in the game: ", 1, 5);
	   
	   //Creating the snakes, ladders and traps
	   //Snakes
	   for(i = 0; i < snakesCount; i++){
		   ok = true;
		   do{
			   ok = true;
			   head = getInt("Enter head position for snake " + (i+1), 2,99);
			   tail = getInt("Enter tail position for snake " + (i+1), 2, head);
			   //VALIDATION
			   //Comparing with all previous snakes to ensure no violation
			   for(j = 0; j < i; j++){
				   if(snakes[j].getHead() == head || snakes[j].getHead() == tail || snakes[j].getTail() == head || snakes[j].getTail() == tail){
					   ok = false; //Forces player to re-enter values
					   plainMessage("Violation: Please re-enter values.");
					   break;
				   }
			   }
		   }while(ok == false);
		   snakes[i] = new Snake(head, tail);
		   bd.add(snakes[i]);
	   }
	   //Ladders
	   for(i = 0; i < laddersCount; i++){
		   ok = true;
		   do{
			   ok = true;
			   bottom = getInt("Enter the bottom position for ladder " + (i+1), 2,99);
			   top = getInt("Enter the top position for ladder " + (i+1), bottom, 99);
			   //VALIDATION
			   //Comparing with all previous snakes to ensure no violation
			   for(j = 0; j < snakesCount; j++){
				   if(snakes[j].getHead() == bottom || snakes[j].getHead() == top || snakes[j].getTail() == bottom || snakes[j].getTail() == top){
					   ok = false; //Forces player to re-enter values
					   plainMessage("Violation: Please re-enter values.");
					   break;
				   }
			   }
			   //Comparing with all previous ladders to ensure no violation
			   for(j = 0; j < i; j++){
				   if(ladders[j].getBottom() == bottom || ladders[j].getBottom() == top || ladders[j].getTop() == bottom || ladders[j].getTop() == top){
					   ok = false; //Forces player to re-enter values
					   plainMessage("Violation: Please re-enter values.");
					   break;
				   }
			   }
		   }while(ok == false);
		   ladders[i] = new Ladder(bottom, top);
		   bd.add(ladders[i]);
	   }
	   //Traps
	   for(i = 0; i < trapsCount; i++){
		   ok = true;
		   do{
			   ok = true;
			   location = getInt("Enter the location for trap " + (i+1), 2,99);
			   duration = getInt("Enter the duration for trap " + (i+1), 3,5);
			   //VALIDATION
			   //Comparing with all previous snakes to ensure no violation
			   for(j = 0; j < snakesCount; j++){
				   if(snakes[j].getHead() == location || snakes[j].getTail() == location){
					   ok = false; //Forces player to re-enter values
					   plainMessage("Violation: Please re-enter values.");
					   break;
				   }
			   }
			   //Comparing with all previous ladders to ensure no violation
			   for(j = 0; j < laddersCount; j++){
				   if(ladders[j].getBottom() == location || ladders[j].getTop() == location){
					   ok = false; //Forces player to re-enter values
					   plainMessage("Violation: Please re-enter values.");
					   break;
				   }
			   }
			   //Comparing with all previous traps to ensure no violation
			   for(j = 0; j < i; j++){
				   if(traps[j].getLocation() == location){
					   ok = false; //Forces player to re-enter values
					   plainMessage("Violation: Please re-enter values.");
					   break;
				   }
			   }
		   }while(ok == false);
		   traps[i] = new Trap(location, duration);
		   bd.add(traps[i]);
	   }   
   }
   
   public void trapValidation(){
	 //Comparing with all previous snakes to ensure no violation
	   for(j = 0; j < snakesCount; j++){
		   if(snakes[j].getHead() == location || snakes[j].getTail() == location){
			   ok = false; //Forces player to re-enter values
			   plainMessage("Violation: Please re-enter values.");
			   break;
		   }
	   }
	   //Comparing with all previous ladders to ensure no violation
	   for(j = 0; j < laddersCount; j++){
		   if(ladders[j].getBottom() == location || ladders[j].getTop() == location){
			   ok = false; //Forces player to re-enter values
			   plainMessage("Violation: Please re-enter values.");
			   break;
		   }
	   }
	   //Comparing with all previous traps to ensure no violation
	   for(j = 0; j < trapsCount; j++){
		   if(traps[j].getLocation() == location){
			   ok = false; //Forces player to re-enter values
			   plainMessage("Violation: Please re-enter values.");
			   break;
		   }
	   }
   }
   
   
   // A method to print a message and to read an int value in the range specified
   int getInt(String message, int from, int to)
   {
	   String s;
	   int n = 0;
	   boolean invalid;
	   do {
		 invalid = false;
	     s = (String)JOptionPane.showInputDialog(
	      bd,  message,  "Customized Dialog",
	          JOptionPane.PLAIN_MESSAGE);	
	      try {
	         n = Integer.parseInt(s);
	         if (n < from || n > to )
	    	     plainMessage("Re-enter: Input not in range " + from + " to " + to);
	      }
	      catch (NumberFormatException nfe)
	      {
	    	  plainMessage("Re-enter: Invalid number");
	    	  invalid = true;
	      }
	   } while ( invalid || n < from || n > to);
	   return n;
   }

   // A method to print a message and to read a String
   String getString(String message)
   {
	   String s = (String)JOptionPane.showInputDialog(
	      bd,  message,  "Customized Dialog",
	          JOptionPane.PLAIN_MESSAGE);	
	   return s;
   }   

   // A method to print a message
   void plainMessage(String message)
   {
        JOptionPane.showMessageDialog(bd,
		    message, "A prompt message",
		    JOptionPane.PLAIN_MESSAGE);
   }
   
   
   public void control()
   {
      do{
    	  //GAME CONFIGURATION
		  //Creates a new board
		  if(newBoard == 0){
			  setup(bd);
			  newBoard = -1;
		  }
		  
		  //Resets game properties
		  newGame();
    	  
		  //Display names and instructions on the display board
	      bd.addMessage("Current Players are");
	      bd.addMessage("Player 1 : " + name[0]);      
	      bd.addMessage("Player 2 : " + name[1]);
	      bd.addMessage("Continue until a");
	      bd.addMessage("player gets to 100.");
	      bd.addMessage("Remember to have fun!");   
	      bd.addMessage("Danger: Traps,Snakes");    
	      bd.addMessage("Trap: lose 3 moves");
		  
		  //PLAYING THE GAME
	      do{
			  //Adds 1 to transition to next player
	    	  playerTurn += 1;
	    	  //Change variable values according to the player's turn
	    	  if(playerTurn == 1) playerName = name[0];
	    	  else if(playerTurn == 2) playerName = name[1];
	    	  
	    	  //Check if player is trapped, roll dice if not
	    	  if(player[p][1] > 1){
	    		  player[p][1] = player[p][1] - 1;
	    		  plainMessage(playerName + " is trapped for " + player[p][1] + " more turn(s).");
	    	  }
	    	  //Decide to roll the dice or set a trap
	    	  else{
	    		  setTrap = getInt(playerName + ": Select whether you want to roll the dice or set a trap: \n1. Roll the dice \n2. Set a trap", 1, 2);
	    	      if(setTrap == 2){
	    	    	  setTrap = 0; //Resets for the next player
	    	    	  do{
	    	   			   ok = true;
	    	   			   location = player[p][0];
	    	   			   duration = getInt("Enter the duration for trap between 3 and 5 (this will double)", 3,5);
	    	   			   //VALIDATION
	    	   			   trapValidation();
	    	   		   }while(ok == false);
	    	    	   traps[trapsCount++] = new Trap(location, duration*2);
	    	   		   bd.add(traps[i]);   
	    	      }
	    	      else{
	    	    	  setTrap = 0; //Resets for the next player
	    	    	  //Roll dice and move to location, roll again if 6 is rolled
		    		  do{
		    			  //Perform a dice roll
		    			  diceRoll();
		    			  //Checks the player's position with the game logic
		    			  gameLogic();
		    			  //Notify player they get to roll again if they roll a 6 and pass position logic
		    			  if(val == 6)plainMessage(playerName + " rolled a 6! You get to roll another time!");
		    		  }while(val == 6);
	    	      }
	    	  }
	    	  p += 1;
    		  //Resets variables required for assigning player turn once reached end of cycle
    		  if(playerTurn == numPlayers){
    			  playerTurn = 0;
    			  p = 0;
    		  }
		  }while(win < 0);
		  
	      //END GAME MENU
	      choice = getInt("Input the number corresponding to the decision you made: \n1. Play the same board again \n2. Play a different board \n3. End the game", 1, 3);
	      if(choice == 2){
	    	  newBoard = 0;
	    	  bd.clear();
	      }
		  if(choice == 3){
			  end = true;
			  plainMessage("You can now close the game.");
		  }
      }while(end == false);   
   }
   
   public void diceRoll(){
	   //Assign or randomize dice value
	   val = getInt(playerName + ": Enter 0 to throw dice. Enter 1 - 6 for testing.", 0, 6);
	   if(val == 0) val = dice.roll();
	   else dice.set(val);
	   //Backs up the player position in case they land over square 100
	   temp = player[p][0];
	   //Assign player position and set piece on the board
	   player[p][0] += val;
	   plainMessage(playerName + ": Moving to square " + player[p][0]);
	   bd.setPiece(playerTurn, player[p][0]);
   }
   
   public void gameLogic(){
	 //WIN VALIDATION
	 //Checks if the player lands over square 100
	 if(player[p][0] > 100){
		 plainMessage(playerName + " landed over square 100, must go back to square " + temp + ".");
		 player[p][0] = temp;
		 bd.setPiece(playerTurn, player[p][0]);
	 }
	 //Checks if the player lands exactly on square 100
	 if(player[p][0] == 100){
		 plainMessage("Congratulations "+ playerName + "! You won the game!");
		 val = 7; //End turn, prevents re-roll if rolled a 6 prior
		 win = p; //Assigns win with the id of the player
	 }
	 
	 //SNAKES, LADDERS, & TRAPS
	 //Rules for ladders
	 for(i = 0; i < laddersCount; i++){
		 if(player[p][0] == ladders[i].getBottom()){
			 plainMessage(playerName + " is about to move up the ladder!");
			 player[p][0] = ladders[i].getTop(); //Going up the ladder
			 plainMessage(playerName + ": Going up a ladder to " + player[p][0]);
			 bd.setPiece(playerTurn, player[p][0]);
		 }
	 }
	 //Rules for snakes
	 for(i = 0; i < snakesCount; i++){
		 if(player[p][0] == snakes[i].getHead()){
			 plainMessage(playerName + " is about to slide down the snake!");
			 player[p][0] = snakes[i].getTail(); //Going down the snake
			 plainMessage(playerName + ": Sliding down a snake to " + player[p][0]);
			 bd.setPiece(playerTurn, player[p][0]);
		 }
	 }
	 //Rules when landed on traps
	 for(i = 0; i < trapsCount; i++){
		 if(player[p][0] == traps[i].getLocation()){
			 plainMessage("Trap activated! " + playerName + " is stuck for " + traps[i].getDuration() + " turns!");
			 player[p][1] = traps[i].getDuration();
			 val = 7; //Stops the player from rolling again if a 6 was thrown
		 }
	 }
   }
   
   
   // The very first method to be called
   // This method constructs a SLGame object and calls its control method 
   public static void main(String args[])
   {
       SLGame slg = new SLGame();
       slg.control();
   }


}