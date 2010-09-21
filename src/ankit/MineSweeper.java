///////////////////////////////////////////
// licensed under LGPL3.0 
// http://www.gnu.org/licenses/lgpl-3.0.txt
// @author Ankit Sinha
//////////////////////////////////////////
package ankit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class MineSweeper
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MineButton[][] buttons;
	//private MineLabel[][] m_Labels;
	private int nRows;
	private int nCols;
	private int nMines;
	private int[][] minePositions;
	private long beginTime =-1;
	//private MyMouseListener theML;
	
	private final JPanel top = new JPanel();
	private final JPanel buttonArea = new JPanel();
	private final JFrame frame = new JFrame();
	
	private int numFlagged = 0;
	private final JLabel numMinesField = new JLabel();{
		numMinesField.setText("0");
		numMinesField.setForeground(Color.RED);
		numMinesField.setFont(new Font("Courier", Font.BOLD, 25));
		numMinesField.setBorder(new EmptyBorder(0,0,0,20));//1,100,1,0));
		numMinesField.setBackground(new Color(100,0,0));
	}
	private final JLabel timeField = new JLabel();{
		timeField.setText("000");
		timeField.setForeground(Color.RED);
		timeField.setFont(new Font("Courier", Font.BOLD, 25));
		timeField.setBorder(new EmptyBorder(0,20,0,0));//1,100,1,0));
		timeField.setBackground(new Color(100,0,0));
		//top.setBorder(new EmptyBorder(0,0,0,0));
		//top.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
	}
	
	final ClassLoader cl = this.getClass().getClassLoader();
	final ImageIcon smile = new ImageIcon(cl.getResource("ankit/img/face-smile.png"));
	final Icon exploded = new ImageIcon(cl.getResource("ankit/img/bang.png"));
	final Icon cool     = new ImageIcon(cl.getResource("ankit/img/face-cool.png"));
	final Icon win     = new ImageIcon(cl.getResource("ankit/img/face-win.png"));
	final Icon sad      = new ImageIcon(cl.getResource("ankit/img/face-sad.png"));
	final Icon mine = new ImageIcon(cl.getResource("ankit/img/mine.png"));
	final Icon badFlag = new ImageIcon(cl.getResource("ankit/img/warning.png"));
	final ImageIcon flagIcon = new ImageIcon(cl.getResource("ankit/img/flag.png"));{
		Image x = flagIcon.getImage();
		x=x.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
		flagIcon.setImage(x);
	}
	
	private final JButton topButton = new JButton(smile);{
		//topButton.setPreferredSize(new Dimension(40, 40));
		topButton.setMargin(new Insets(0, 0, 0, 0));
	}
	
	
	private int time = 0;
	private static MineSweeper game ; 
	private final ActionListener timerActionListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			game.time = (int)((System.currentTimeMillis() - beginTime)/1000);
			String x = String.format("%03d", game.time);
			game.timeField.setText(x);
		}
	};
	
	private Timer timer = new Timer(1000, timerActionListener);
	
	private boolean won;
	private boolean lost;
	private MineSweeper(int NRows,int NCols,int NMines)
	{
		super();
		game = this;
		nRows=NRows;
		nCols=NCols;
		nMines=NMines;
		buttons=new MineButton[nRows][nCols];
		//m_Labels=new MineLabel[m_NRows][m_NCols];
		minePositions=new int[nMines][2];
		//theML=new MyMouseListener();
		numMinesField.setText(String.format("%03d", nMines - numFlagged));

		for(int i=0;i<nRows;i++)
			for(int j=0;j<nCols;j++)
			{
				buttons[i][j]=new MineButton(i,j);
			
				//m_Labels[i][j]=new MineLabel(i,j);							
				//m_Labels[i][j].setSize(20,20);
				//m_Labels[i][j].setPreferredSize(new Dimension(20,20));
				//m_Labels[i][j].setBackground(new Color(200,200,200));
			}


		int set=0;
		while(set<nMines)
		{
			int pos =(int)Math.round(Math.random()*(nRows*nCols-1));
			int flag=1;
			for(int i=0;i<set;i++)
			{
				if(minePositions[i][0]==pos/nCols && minePositions[i][1]==pos%nCols)
				{
					flag=0;
					break;
				}
			}
			if(flag==1)
			{
				minePositions[set][0]=pos/nCols;
				minePositions[set][1]=pos%nCols;
				buttons[pos/nCols][pos%nCols].setMine(true);
				//m_Labels[pos/m_NCols][pos%m_NCols].setText();
				set++;
			}
		}

		for(int i=0;i<nRows;i++)
			for(int j=0;j<nCols;j++)
			{
				if(buttons[i][j].hasMine())
				{
					for(MineButton surroundingButton: getSurroundingButtons(i, j))
						surroundingButton.incrementCount();
				}
			}

		/*for(int i=0;i<m_NRows;i++)
			for(int j=0;j<m_NCols;j++)
			{
				if(m_Buttons[i][j].hasMine())
					m_Labels[i][j].setText("*");
				else if(m_Buttons[i][j].getCount()==0)
						m_Labels[i][j].setText("");
					else
						m_Labels[i][j].setText(""+m_Buttons[i][j].getCount());
			}*/


		GridLayout theGridLayoutManager=new GridLayout(nRows,nCols);
		buttonArea.setLayout(theGridLayoutManager);
		theGridLayoutManager.setHgap(1);
		theGridLayoutManager.setVgap(1);
		buttonArea.setBackground(new Color(100,100,100));
		

		for(int i=0;i<nRows;i++)
			for(int j=0;j<nCols;j++)
			{
				buttonArea.add(buttons[i][j].button);
				buttons[i][j].button.addMouseListener(new MyMouseListener(i, j));
				//m_Labels[i][j].addMouseListener(theML);
			}

		
		
		top.add(numMinesField);
		top.add(topButton);
		top.add(timeField);
		
		
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
		frame.add(top,0);
		frame.add(buttonArea,1);
		
		//Dimension d=theGridLayoutManager.preferredLayoutSize(this);
		//this.setSize(d);
		frame.pack();
		frame.setLocation(300,100);
		frame.setResizable(true);
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
				
			}
			
		});
	}


	public static void main(String args[])
	{
		MineSweeper theMineSweeper;
		if(args.length!=3)
			theMineSweeper=new MineSweeper(10,10,10);
		else
			theMineSweeper=new MineSweeper(Integer.parseInt(args[0]),Integer.parseInt(args[1]),Integer.parseInt(args[2]));
		theMineSweeper.frame.setVisible(true);
		//theMineSweeper.m_BeginTime=System.currentTimeMillis();
	}

	private class MyMouseListener extends MouseAdapter
	{
		int row;
		int col;
		
		
		public MyMouseListener(int row, int col) {
			super();
			this.row = row;
			this.col = col;
		}


		void handleUnopenedButton(MineButton mineButton) {
			
			if(mineButton.isFlagged())
				return;

			mineButton.setStatus(false);
			//remove(hitMineButton);
			int rowNo=mineButton.getRowNo();
			int colNo=mineButton.getColNo();
			if(mineButton.hasMine())
			{
				timer.stop();
				topButton.setIcon(sad);
				lost=true;
				//add(m_Labels[rowNo][colNo],rowNo*m_NCols+colNo);
				mineButton.button.setBackground(new Color(200,200,200));
				mineButton.setStatus(false);//source.labelled=true;
				mineButton.button.setIcon(exploded);//Text("*");
				//validate();
				System.out.println("      YOU      LOST     ");
				System.out.println("HIT ANY KEY TO EXIT");
				//try{System.in.read(new byte[1]);}catch(Exception Ex){}
				//System.exit(0);
				return;
			}
			else if(mineButton.getCount()!=0)
				{
				//add(m_Labels[rowNo][colNo],rowNo*m_NCols+colNo);
				mineButton.button.setBackground(new Color(200,200,200));
				mineButton.setStatus(false);//source.labelled=true;
				mineButton.button.setText(""+mineButton.getCount());
				//validate();
				}
				else
				{
					//add(m_Labels[rowNo][colNo],rowNo*m_NCols+colNo);
					mineButton.button.setBackground(new Color(200,200,200));
					mineButton.setStatus(false);//source.labelled=true;
					mineButton.button.setText("");//+source.getCount());
					
					for(MineButton neighborButton: getSurroundingButtons(rowNo, colNo)) {
						if(neighborButton.getStatus() && ! neighborButton.isFlagged())
							handleUnopenedButton(neighborButton);
					}
					
				}

			int count=0;
			for(int i=0;i<nRows;i++)
				for(int j=0;j<nCols;j++)
				{
					if(buttons[i][j].getStatus()) count++;
				}
			if(count==nMines)
			{
				timer.stop();
				won=true;
				topButton.setIcon(win);
				System.out.println("    YOU    WON");
				System.out.println("YOU TOOK "+(System.currentTimeMillis()-beginTime)/1000+" SECONDS");
				System.out.println("HIT ANY KEY TO EXIT");
				try
				{
				//	System.in.read(new byte[1]);
				}
				catch(Exception Ex)
				{}
				//System.exit(0);
			}
			
		}
		
		void handleOpenedButton(MineButton mineButton)
		{
			if(mineButton.getCount()==mineButton.getSurroundingFlaggedMinesCount())
			{
				mineButton.button.removeMouseListener(this);
				
				for(MineButton neighborButton: getSurroundingButtons(row, col)) {
					if(neighborButton.getStatus() && ! neighborButton.isFlagged())
						handleUnopenedButton(neighborButton);
				}				
				
			}
			
		}
		
		public void mouseReleased(MouseEvent e)
		{
			
			if(won || lost)
				return;
			
			if(beginTime<0) {
				beginTime = System.currentTimeMillis();
				timer.start();
			}
			
			int button=e.getButton();
			MineButton source=buttons[row][col];
			
			
			if(!source.getStatus())//revealed button clicked again
			{
				if( !(button==MouseEvent.BUTTON2 || ((button==MouseEvent.BUTTON1 && (e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK)== MouseEvent.BUTTON3_DOWN_MASK) || (button==MouseEvent.BUTTON3 && (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK)== MouseEvent.BUTTON1_DOWN_MASK ))))
				{
				//	System.out.println(MouseEvent.getModifiersExText(e.getModifiersEx()));
					return;
				}

				handleOpenedButton(source);
				
				return;
			}
			
			if( !(button==MouseEvent.BUTTON1 && source.getStatus()))// !source.labelled))
				return;
			else
				handleUnopenedButton(source);
			

		}
		
		void handleFlag(MineButton mineButton) {
			
			mineButton.setFlagged(! mineButton.isFlagged());	
			
			if( mineButton.isFlagged())
			{				
				numFlagged++;

				for(MineButton neighborButton: getSurroundingButtons(row, col))
					neighborButton.incrementSurroundingFlaggedMinesCount();
			}
			else
			{
				numFlagged--;
				
				for(MineButton neighborButton: getSurroundingButtons(row, col))
					neighborButton.decrementSurroundingFlaggedMinesCount();
			}
			
			numMinesField.setText(String.format("%03d", nMines - numFlagged));
			
		}

		public void mousePressed(MouseEvent e)
		{
			if(won || lost)
				return;
			
			int button=e.getButton();
			MineButton source = buttons[row][col];
			
			if( !source.getStatus())//labelled
				return;
			
			if( ! (button==MouseEvent.BUTTON3 ))
				return;
			
			handleFlag(source);
			//repaint();
		}
	}
	
	private List<MineButton> getSurroundingButtons(int k, int l){
		ArrayList<MineButton> result = new ArrayList<MineSweeper.MineButton>(8);
		k--;
		l--;
		if(checkBounds(k,l)) result.add(buttons[k][l]);
		l++;
		if(checkBounds(k,l)) result.add(buttons[k][l]);
		l++;
		if(checkBounds(k,l)) result.add(buttons[k][l]);
		k++;
		if(checkBounds(k,l)) result.add(buttons[k][l]);
		l=l-2;
		if(checkBounds(k,l)) result.add(buttons[k][l]);
		k++;
		if(checkBounds(k,l)) result.add(buttons[k][l]);
		l++;
		if(checkBounds(k,l)) result.add(buttons[k][l]);
		l++;
		if(checkBounds(k,l)) result.add(buttons[k][l]);
		
		return result;
	}
	
	private boolean checkBounds(int k, int l){
		return k>=0 && l>=0 && k<nRows && l<nCols;
	}

	private class MineButton {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int rowNo;
		private int colNo;
		private int count;
		private boolean isMine;
		private boolean Status;
		private boolean flagged;
		private int surroundingFlaggedMinesCount;
		
		
		public JButton button = new JButton("");

		public MineButton(int rowNo,int colNo)
		{
			
			this.rowNo=rowNo;
			this.colNo=colNo;
			this.count=0;
			this.setMine(false);
			this.setStatus(true);
			this.setFlagged(false);
			this.surroundingFlaggedMinesCount=0;			

			button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
			button.setMargin(new Insets(0,0,0,0));
			button.setPreferredSize(new Dimension(20,20));
			button.setMaximumSize(new Dimension(20,20));
			button.setMinimumSize(new Dimension(20,20));
			button.setBackground(new Color(230,230,230));
		}
		public void incrementCount()
		{
			this.count++;
		}
		public int getCount()
		{
			return this.count;
		}
		public int getSurroundingFlaggedMinesCount()
		{
			return this.surroundingFlaggedMinesCount;
		}
		public void incrementSurroundingFlaggedMinesCount()
		{
			this.surroundingFlaggedMinesCount++;
		}
		public void decrementSurroundingFlaggedMinesCount()
		{
			this.surroundingFlaggedMinesCount--;
		}
		public int getRowNo()
		{
			return this.rowNo;
		}
		public int getColNo()
		{
			return this.colNo;
		}
		/*public void doProxyAction()
		{
			Point p = getLocation();
			MouseEvent theMouseEvent=new MouseEvent(this,MouseEvent.MOUSE_RELEASED,System.
				currentTimeMillis(),MouseEvent.BUTTON1_MASK,p.x,p.y,1,false,MouseEvent.BUTTON1);
			processMouseEvent(theMouseEvent);
			//theEQ.postEvent(theMouseEvent);
		}*/
		public void setMine(boolean value)
		{
			this.isMine=value;
		}
		public boolean hasMine()
		{
			return this.isMine;
		}
		public void setStatus(boolean Status)
		{
			this.Status=Status;
		}
		public boolean getStatus()
		{
			return this.Status;
		}
		public boolean isFlagged()
		{
			return this.flagged;
		}
		public void setFlagged(boolean flag)
		{
			this.flagged=flag;
			button.setIcon(flagged ? flagIcon:null);
		}
	}

	/*private class MineLabel extends Label
	{
		*//**
		 * 
		 *//*
		private static final long serialVersionUID = 1L;
		private int rowNo;
		private int colNo;
		public MineLabel(int rowNo,int colNo)
		{
			super("");
			this.rowNo=rowNo;
			this.colNo=colNo;
		}
		public int getRowNo()
		{
			return this.rowNo;
		}
		public int getColNo()
		{
			return this.colNo;
		}
	}*/


}
