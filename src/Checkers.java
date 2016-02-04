import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
//---------------------------------------------------------------------------------------------

public class Checkers extends JPanel {
	
   public static void main(String[] args) {
      JFrame window = new JFrame("Checkers2");
      Checkers content = new Checkers();
      window.setContentPane(content);
      window.pack();
      Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
      window.setLocation( (screensize.width - window.getWidth())/2,(screensize.height - window.getHeight())/2 );
      window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      window.setResizable(true);  
      window.setVisible(true);
   }
   
   private JButton resignButton;  
   private JButton Player_red;
   private JButton Player_blue;
   private JLabel message;  
   public Checkers() {
      setLayout(null);  
      setPreferredSize( new Dimension(1230,720) );
      setBackground(new Color(0,150,0));  
      Board board = new Board();  
      add(board);
      add(Player_red);
      add(Player_blue);
      add(resignButton);
      add(message);
      board.setBounds(50,0,716,716); 
      resignButton.setBounds(910, 375 , 120, 30);
      message.setBounds(765,250,400,30);
      Player_blue.setBounds(800,10,350,100);
      Player_red.setBounds(800,610,350,100);
   } 
 //---------------------------------------------------------------------------------------------   
   
   private static class CheckersMove {
      int fromRow, fromCol,toRow, toCol;
      CheckersMove(int r1, int c1, int r2, int c2) {
         fromRow = r1;
         fromCol = c1;
         toRow = r2;
         toCol = c2;
      }
      boolean isJump() {
         return (fromRow - toRow == 2 || fromRow - toRow == -2);
      }
   } 
 //--------------------------------------------------------------------------------------------- 
   
   private class Board extends JPanel implements ActionListener, MouseListener {
      CheckersData board;  
      boolean gameInProgress;
      int currentPlayer;      
      int selectedRow, selectedCol; 
      CheckersMove[] legalMoves;  
      Board() {
         setBackground(Color.red);
         addMouseListener(this);
         resignButton = new JButton("Restart Game");
         resignButton.addActionListener(this);
         
         Player_red = new JButton("Player : Red");
         Player_red.setEnabled(false);
         Player_red.setBackground(Color.red);
         Player_red.setFont(new Font("Tahoma", Font.BOLD,48));
         Player_red.setForeground(Color.white);
         
         Player_blue = new JButton("Player : Blue");
         Player_blue.setEnabled(false);
         Player_blue.setBackground(Color.blue);
         Player_blue.setFont(new Font("Tahoma", Font.BOLD,48));
         Player_blue.setForeground(Color.white);
         
         message = new JLabel("",JLabel.CENTER);
         message.setFont(new  Font("Serif", Font.BOLD, 20));
         message.setForeground(Color.black);
         board = new CheckersData();
         doNewGame();
      }
      public void actionPerformed(ActionEvent evt) {
         Object src = evt.getSource();
         if (src == resignButton)
            doResign();
      }
      void doNewGame() {
         if (gameInProgress == true) {
            message.setText("Finish the current game first!");
            return;
         }
         board.setUpGame();   
         currentPlayer = CheckersData.RED;   
         legalMoves = board.getLegalMoves(CheckersData.RED);  
         selectedRow = -1;  
         message.setText("Red:  Make your move.");
         gameInProgress = true;
         resignButton.setEnabled(true);
         repaint();
      }
      void doResign() {
         if (gameInProgress == false) {
            message.setText("");
            return;
         }
         if (currentPlayer == CheckersData.RED)
            gameOver("Red restart game.  Blue wins.");
         else
            gameOver("Blue restart game.  Red wins.");
      }
      void gameOver(String str) {
    	
         message.setText(str);
         resignButton.setEnabled(true);
         gameInProgress = false;
         JOptionPane.showMessageDialog(null,str);
         doNewGame();
      }
      void doClickSquare(int row, int col) {
         for (int i = 0; i < legalMoves.length; i++)
            if (legalMoves[i].fromRow == row && legalMoves[i].fromCol == col) {
               selectedRow = row;
               selectedCol = col;
               repaint();
               return;
            }
         if (selectedRow < 0) {
            return;
         }
         for (int i = 0; i < legalMoves.length; i++)
            if (legalMoves[i].fromRow == selectedRow && legalMoves[i].fromCol == selectedCol&& legalMoves[i].toRow == row && legalMoves[i].toCol == col) {
               doMakeMove(legalMoves[i]);
               return;
            }
      }  
      
      void doMakeMove(CheckersMove move) {    
         board.makeMove(move);
         if (move.isJump()) {
            legalMoves = board.getLegalJumpsFrom(currentPlayer,move.toRow,move.toCol);
            if (legalMoves != null) {
               if (currentPlayer == CheckersData.RED)
            	   message.setText("Red:  You must continue jumping.");
               else
            	   message.setText("Blue:  You must continue jumping.");
               selectedRow = move.toRow;  
               selectedCol = move.toCol;
               repaint();
               return;
            }
         }
         if (currentPlayer == CheckersData.RED) {
            currentPlayer = CheckersData.BLACK;
            legalMoves = board.getLegalMoves(currentPlayer);
            if (legalMoves == null)
            	gameOver("Blue has no moves.  Red wins.");
            else if (legalMoves[0].isJump())
            	message.setText("Blue:  Make your move.  You must jump.");
            else
            	message.setText("Blue:  Make your move.");
         }
         else {
            currentPlayer = CheckersData.RED;
            legalMoves = board.getLegalMoves(currentPlayer);
            if (legalMoves == null)
            	gameOver("Red has no moves.  Blue wins.");
            else if (legalMoves[0].isJump())
            	message.setText("Red:  Make your move.  You must jump.");
            else
            	message.setText("Red:  Make your move.");
         }
         selectedRow = -1;
         if (legalMoves != null) {
            boolean sameStartSquare = true;
            for (int i = 1; i < legalMoves.length; i++)
            	if (legalMoves[i].fromRow != legalMoves[0].fromRow|| legalMoves[i].fromCol != legalMoves[0].fromCol) {
                  sameStartSquare = false;
                  break;
               }
            if (sameStartSquare) {
            	selectedRow = legalMoves[0].fromRow;
            	selectedCol = legalMoves[0].fromCol;
            }
         }
         repaint();   
      } 
      
      public void paintComponent(Graphics g) {
         g.setColor(Color.white);
         g.drawRect(0,0,getSize().width-2,getSize().height-2);
         g.drawRect(1,1,getSize().width-3,getSize().height-3);
         for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
               if ( row % 2 == col % 2 )
            	   g.setColor(Color.LIGHT_GRAY);
               else
            	   g.setColor(Color.GRAY);
               g.fillRect(2 + col*89, 2 + row*89, 89, 89);
               switch (board.pieceAt(row,col)) {
               case CheckersData.RED:
            	   g.setColor(Color.red);
                   g.fillOval(15 + col*89, 15 + row*89, 60, 60);
                   break;
               case CheckersData.BLACK:
            	   g.setColor(Color.blue);
            	   g.fillOval(15 + col*89, 15 + row*89, 60, 60);
            	   break;
               case CheckersData.RED_KING:
            	   g.setColor(Color.orange);
            	   g.fillOval(25 + col*89, 15 + row*89, 60, 60);
            	   g.setColor(Color.red);
            	   g.fillOval(5 + col*89,15 + row*89, 60, 60);
            	   break;
               case CheckersData.BLACK_KING:
            	   g.setColor(Color.cyan);
            	   g.fillOval(25 + col*89, 15 + row*89, 60, 60);
            	   g.setColor(Color.blue);
            	   g.fillOval(5 + col*89,15 + row*89, 60, 60);
            	   break;
               }
            }
         }
         if (gameInProgress) {
            g.setColor(Color.yellow);
            for (int i = 0; i < legalMoves.length; i++) {
               g.drawRect(1 + legalMoves[i].fromCol*89, 1 + legalMoves[i].fromRow*89, 88, 88);
               g.drawRect(2 + legalMoves[i].fromCol*89, 2 + legalMoves[i].fromRow*89, 88, 88);
               g.drawRect(3 + legalMoves[i].fromCol*89, 3 + legalMoves[i].fromRow*89, 88, 88);
               g.drawRect(4 + legalMoves[i].fromCol*89, 4 + legalMoves[i].fromRow*89, 86, 86);
            }
            if (selectedRow >= 0) {
               g.setColor(Color.white);
               g.drawRect(1 + selectedCol*89, 1 + selectedRow*89, 88, 88);
               g.drawRect(2 + selectedCol*89, 2 + selectedRow*89, 88, 88);
               g.drawRect(3 + selectedCol*89, 3 + selectedRow*89, 88, 88);
               g.drawRect(4 + selectedCol*89, 4 + selectedRow*89 ,86, 86);
               g.setColor(Color.green);
               for (int i = 0; i < legalMoves.length; i++) {
                  if (legalMoves[i].fromCol == selectedCol && legalMoves[i].fromRow == selectedRow) {
                     g.drawRect(1 + legalMoves[i].toCol*89, 1 + legalMoves[i].toRow*89, 88, 88);
                     g.drawRect(2 + legalMoves[i].toCol*89, 2 + legalMoves[i].toRow*89, 88, 88);
                     g.drawRect(3 + legalMoves[i].toCol*89, 3 + legalMoves[i].toRow*89, 88, 88);
                     g.drawRect(4 + legalMoves[i].toCol*89, 4 + legalMoves[i].toRow*89, 86, 86);
                  }
               }
            }
         }
      }  
      public void mousePressed(MouseEvent evt) {
         if (gameInProgress == false) 
        	 message.setText("Game is Over");
         else {
        	 int col = (evt.getX() - 2) / 89;
        	 int row = (evt.getY() - 2) / 89;
        	 if (col >= 0 && col < 8 && row >= 0 && row < 8)
        		 doClickSquare(row,col);
         }
      }    
      public void mouseReleased(MouseEvent evt) { }
      public void mouseClicked(MouseEvent evt) { }
      public void mouseEntered(MouseEvent evt) { }
      public void mouseExited(MouseEvent evt) { } 
   } 
//---------------------------------------------------------------------------------------------   
   
   private static class CheckersData {
	   int count;
      static final int EMPTY = 0,RED = 1,RED_KING = 2,BLACK = 3,BLACK_KING = 4;
      int[][] board;    
      CheckersData() {
         board = new int[8][8];
         setUpGame();
      }
      void setUpGame() {
         for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
               if ( row % 2 == col % 2 ) {
                  if (row < 2)
                     board[row][col] = BLACK;
                  else if (row > 5)
                     board[row][col] = RED;
                  else
                     board[row][col] = EMPTY;
               }
               else {
                  board[row][col] = EMPTY;
               }
            }
         }
      }  
      
      int pieceAt(int row, int col) {
         return board[row][col];
      }
      void makeMove(CheckersMove move) {
         makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol);
      }
      void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
    	  
         board[toRow][toCol] = board[fromRow][fromCol];
         board[fromRow][fromCol] = EMPTY;
         
       if(board[toRow][toCol]==RED||board[toRow][toCol]==BLACK)
         if (fromRow - toRow == 2 || fromRow - toRow == -2) {
            int jumpRow = (fromRow + toRow) / 2;  
            int jumpCol = (fromCol + toCol) / 2;  
            board[jumpRow][jumpCol] = EMPTY;
         }
          
         if(board[toRow][toCol] ==RED_KING ||board[toRow][toCol]== BLACK_KING )
         {
        	 if(fromCol==fromRow)
        	 {
        		
        		 if (fromRow>toRow&&fromCol<toCol) {
        			
        			 int jumpRow = (toRow+1) ;
        			 int jumpcol = (toCol-1);
        			 board[jumpRow][jumpcol] = EMPTY;
        		
        		 }else if(fromRow>toRow&&fromCol>toCol)
        		 {
        			
        			 int jumpRow = (toRow+1) ;
        			 int jumpcol = (toCol+1);
        			 board[jumpRow][jumpcol] = EMPTY;
        			
        		 }else if(fromRow<toRow&&fromCol>toCol)
        		 {
        			
        			 int jumpRow = (toRow-1) ;
        			 int jumpcol = (toCol+1);
        			 board[jumpRow][jumpcol] = EMPTY;
        			 
        		 }else
        			 if(fromRow<toRow&&fromCol<toCol)
        		 {
        			
        			 int jumpRow = (toRow-1) ;
        			 int jumpcol = (toCol-1);
        			 board[jumpRow][jumpcol] = EMPTY;
        			 
        		 }
        	 }else if(fromRow>fromCol)
        	 {
        		 	if(fromRow>toRow&&fromCol>toCol)
        		 {
        			 int jumpRow = (toRow+1) ;
        			 int jumpcol = (toCol+1);
        			 board[jumpRow][jumpcol] = EMPTY;
        		 }else
        			if(fromRow>toRow&&fromCol<toCol)
            	 {
            		 int jumpRow = (toRow+1) ;
           			 int jumpcol = (toCol-1);
           			 board[jumpRow][jumpcol] = EMPTY;
        		 }else
        			 if(toRow>fromRow&&toCol<fromCol)
        		{
        			 int jumpRow = (toRow-1) ;
        			 int jumpcol = (toCol+1);
        			 board[jumpRow][jumpcol] = EMPTY;
        		}else 
        			if(fromRow<toRow&&fromCol>toCol)
       		 	{
	       			 int jumpRow = (toRow-1) ;
	       			 int jumpcol = (toCol+1);
	       			 board[jumpRow][jumpcol] = EMPTY;
        		}else 
            		if(fromRow<toRow&&fromCol<toCol)
           		 	{
    	       			 int jumpRow = (toRow-1) ;
    	       			 int jumpcol = (toCol-1);
    	       			 board[jumpRow][jumpcol] = EMPTY;
            		}
        	 }else if(fromCol>fromRow)
        	 {
        		 if(fromRow>toRow&&fromCol>toCol)
        		 {
        			 int jumpRow = (toRow+1) ;
        			 int jumpcol = (toCol+1);
        			 board[jumpRow][jumpcol] = EMPTY;
        		 }else
        			 if(fromRow>toRow&&fromCol<toCol)
            		 {
            			 int jumpRow = (toRow+1) ;
            			 int jumpcol = (toCol-1);
            			 board[jumpRow][jumpcol] = EMPTY;
        		 }else 
        			 if(fromRow<toRow&&fromCol>toCol)
            		 {
            			 int jumpRow = (toRow-1) ;
            			 int jumpcol = (toCol+1);
            			 board[jumpRow][jumpcol] = EMPTY;
        		 }else 
        			 if(fromRow<toRow&&fromCol<toCol)
            		 {
            			 int jumpRow = (toRow-1) ;
            			 int jumpcol = (toCol-1);
            			 board[jumpRow][jumpcol] = EMPTY;
        		 }

        	 }
         }
         if (toRow == 0 && board[toRow][toCol] == RED)
        	 board[toRow][toCol] = RED_KING;
         if (toRow == 7 && board[toRow][toCol] == BLACK)
        	 board[toRow][toCol] = BLACK_KING;
      }
      CheckersMove[] getLegalMoves(int player) {
         
         if (player != RED && player != BLACK)
            return null;
         int playerKing;  
         if (player == RED)
        	 playerKing = RED_KING;
         else
        	 playerKing = BLACK_KING;
         
         ArrayList<CheckersMove> moves = new ArrayList<CheckersMove>();  
         for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
               if (board[row][col] == player ) {
                  if (canJump(player, row, col, row+1, col+1, row+2, col+2))
                     moves.add(new CheckersMove(row, col, row+2, col+2));
                  if (canJump(player, row, col, row-1, col+1, row-2, col+2))
                     moves.add(new CheckersMove(row, col, row-2, col+2));
                  if (canJump(player, row, col, row+1, col-1, row+2, col-2))
                     moves.add(new CheckersMove(row, col, row+2, col-2));
                  if (canJump(player, row, col, row-1, col-1, row-2, col-2))
                     moves.add(new CheckersMove(row, col, row-2, col-2));
               }else if(board[row][col] == playerKing)
               {
            	   for(int i = 1; i <=7; i++)
            	   {
            		   count = 0;
            	   if (canJump(player, row, col, row+i, col+i, row+(i+1), col+(i+1)))
                       moves.add(new CheckersMove(row, col, row+(i+1), col+(i+1)));
            	   if(count>0)
             			 break;
            	   }
            	   for(int i = 1; i <=7; i++)
            	   {
            		   count = 0;
            		   if (canJump(player, row, col, row-i, col+i, row-(i+1), col+(i+1)))
                           moves.add(new CheckersMove(row, col, row-(i+1), col+(i+1)));
            		   if(count>0)
                 			 break;
            	   }
            	   for(int i = 1; i <=7; i++)
            	   {
            		   count = 0;
            		   if (canJump(player, row, col, row+i, col-i, row+(i+1), col-(i+1)))
                           moves.add(new CheckersMove(row, col, row+(i+1), col-(i+1)));
            		   if(count>0)
                 			 break;
            	   }
            	   for(int i = 1; i <=7; i++)
            	   {
            		   count = 0;
            		   if (canJump(player, row, col, row-i, col-i, row-(i+1), col-(i+1)))
                           moves.add(new CheckersMove(row, col, row-(i+1), col-(i+1)));
            		   if(count>0)
                 			 break;
            	   }
               }
            }
         }
         if (moves.size() == 0) {
            for (int row = 0; row < 8; row++) {
               for (int col = 0; col < 8; col++) {
                  if (board[row][col] == player ) {
                     if (canMove(player,row,col,row+1,col+1))
                        moves.add(new CheckersMove(row,col,row+1,col+1));
                     if (canMove(player,row,col,row-1,col+1))
                        moves.add(new CheckersMove(row,col,row-1,col+1));
                     if (canMove(player,row,col,row+1,col-1))
                        moves.add(new CheckersMove(row,col,row+1,col-1));
                     if (canMove(player,row,col,row-1,col-1))
                        moves.add(new CheckersMove(row,col,row-1,col-1));
                  }else if(board[row][col] == playerKing)
                  {
               	   for(int i = 1; i <=7; i++)
               	   {
               		   count = 0;
               		 if (canMove(player,row,col,row+i,col+i))
                         moves.add(new CheckersMove(row,col,row+i,col+i));
               		 if(count>0)
               			 break;
               	   }
               	   for(int i = 1; i <=7; i++)
               	   {
               		   count = 0;
               		 if (canMove(player,row,col,row-i,col+i))
                         moves.add(new CheckersMove(row,col,row-i,col+i));
               		 if(count>0)
               			 break;
               	   }
               	   for(int i = 1; i <=7; i++)
               	   {	
               		   count = 0;
               		 if (canMove(player,row,col,row+i,col-i))
                         moves.add(new CheckersMove(row,col,row+i,col-i));
               		 if(count>0)
              			 break;
               	   }
               	   for(int i = 1; i <=7; i++)
               	   {
               		   count = 0;
               		 if (canMove(player,row,col,row-i,col-i))
                         moves.add(new CheckersMove(row,col,row-i,col-i));
               		 if(count>0)
              			 break;
               	   }
                  }
               }
            }
         }
         if (moves.size() == 0)
            return null;
         else {
            CheckersMove[] moveArray = new CheckersMove[moves.size()];
            for (int i = 0; i < moves.size(); i++)
               moveArray[i] = moves.get(i);
            return moveArray;
         } 
      }  
      
      CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {
         if (player != RED && player != BLACK)
            return null;
         int playerKing;  
         if (player == RED)
            playerKing = RED_KING;
         else
            playerKing = BLACK_KING;
         ArrayList<CheckersMove> moves = new ArrayList<CheckersMove>();  
         if (board[row][col] == player) {
            if (canJump(player, row, col, row+1, col+1, row+2, col+2))
               moves.add(new CheckersMove(row, col, row+2, col+2));
            if (canJump(player, row, col, row-1, col+1, row-2, col+2))
               moves.add(new CheckersMove(row, col, row-2, col+2));
            if (canJump(player, row, col, row+1, col-1, row+2, col-2))
               moves.add(new CheckersMove(row, col, row+2, col-2));
            if (canJump(player, row, col, row-1, col-1, row-2, col-2))
               moves.add(new CheckersMove(row, col, row-2, col-2));
         }
        	 else if(board[row][col] == playerKing)
             {
          	   for(int i = 1; i <=7; i++)
          	   {
          		   count = 0;
          	   if (canJump(player, row, col, row+i, col+i, row+(i+1), col+(i+1)))
                     moves.add(new CheckersMove(row, col, row+(i+1), col+(i+1)));
          	   if(count>0)
       			 break;
          	   }
          	   for(int i = 1; i <=7; i++)
          	   {
          		   count = 0;
          		   if (canJump(player, row, col, row-i, col+i, row-(i+1), col+(i+1)))
                         moves.add(new CheckersMove(row, col, row-(i+1), col+(i+1)));
          		 if(count>0)
           			 break;
          	   }
          	   for(int i = 1; i <=7; i++)
          	   {
          		   count = 0;
          		   if (canJump(player, row, col, row+i, col-i, row+(i+1), col-(i+1)))
                         moves.add(new CheckersMove(row, col, row+(i+1), col-(i+1)));
          		 if(count>0)
           			 break;
          	   }
          	   for(int i = 1; i <=7; i++)
          	   {
          		   count = 0;
          		   if (canJump(player, row, col, row-i, col-i, row-(i+1), col-(i+1)))
                         moves.add(new CheckersMove(row, col, row-(i+1), col-(i+1)));
          		 if(count>0)
           			 break;
          	   }
         }
         if (moves.size() == 0)
            return null;
         else {
            CheckersMove[] moveArray = new CheckersMove[moves.size()];
            for (int i = 0; i < moves.size(); i++)
               moveArray[i] = moves.get(i);
            return moveArray;
         }
      }  
      
      private boolean canJump(int player, int r1, int c1, int r2, int c2, int r3, int c3) {
         if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8)
            return false;  
        
         if (board[r3][c3] != EMPTY)
         {
        	 count++;
            return false;  
         }
         if (player == RED) {
            if (board[r1][c1] == RED && r3 > r1)
               return false;  
            if (board[r2][c2] != BLACK && board[r2][c2] != BLACK_KING)
               return false;
            if(board[r1][c1] == RED_KING )
            {
	           	 if((board[r2][c2]==RED||board[r2][c2]==RED_KING ))
	           	 {
	           		 count++;
	           		 return false;
	           	 }
	           	 else if(( board[r2][c2]==BLACK||board[r2][c2]==BLACK_KING) && (board[r3][c3] == EMPTY) )  
	           		 return true;
            }
            return true;  
         }
         else {
            if (board[r1][c1] == BLACK && r3 < r1)
               return false;  
            if (board[r2][c2] != RED && board[r2][c2] != RED_KING)
               return false;
            if(board[r1][c1] == BLACK_KING )
            {
           	 if(board[r2][c2]==BLACK||board[r2][c2]==BLACK_KING)
           	 {
           		 count++;
           		 return false;
           	 }
           	 else if(( board[r2][c2]==RED||board[r2][c2]==RED_KING) && (board[r3][c3] == EMPTY) )  
           		 return true;         }
            return true; 
         }
      }  
      
      private boolean canMove(int player, int r1, int c1, int r2, int c2) {
         if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8)
            return false;  
         if (board[r2][c2] != EMPTY)
         {
        	 count++;
            return false;  
         }
         if (player == RED) {
            if (board[r1][c1] == RED && r2 > r1)
               return false;  
            else if(board[r1][c1] == RED_KING )
            {
            	if((board[r2][c2]==RED||board[r2][c2]==RED_KING) )
	           	 {
	           		 count++;
	           		 return false;
	           	 }
            }
            return true;  
         }
         else {
            if (board[r1][c1] == BLACK && r2 < r1)
               return false;

            else if(board[r1][c1] == BLACK_KING )
            {
            	if((board[r2][c2]==BLACK||board[r2][c2]==BLACK_KING) )
           	 	{
           		 	count++;
           		 	return false;
           	 	}
            }
            return true;  
         } 
      }  
   } 
} 
//---------------------------------------------------------------------------------------------