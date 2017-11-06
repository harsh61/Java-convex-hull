import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.awt.Color; 
import org.jfree.chart.ChartPanel; 
import org.jfree.chart.JFreeChart; 
import org.jfree.data.xy.XYDataset; 
import org.jfree.data.xy.XYSeries; 
import org.jfree.ui.ApplicationFrame; 
import org.jfree.ui.RefineryUtilities; 
import org.jfree.chart.plot.XYPlot; 
import org.jfree.chart.ChartFactory; 
import org.jfree.chart.plot.PlotOrientation; 
import org.jfree.data.xy.XYSeriesCollection; 
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

public class Hull extends ApplicationFrame {
	
	//LinkedLists are created
	private DLList xList;
	private DLList yList;
	private DLList hullXList;
	private DLList hullYList;

	//other instance variables initialized 
	private int leastXIndex;
	private static String exercise;
	
	//Hull constructor
	public Hull(DLList xList, DLList yList, DLList hullXList, DLList hullYList) {
		super("ConvexHull");
		
		//list that contains all data points
		this.xList = xList;
		this.yList = yList;
		
		//list that contains hull points
		this.hullXList = hullXList;
		this.hullYList = hullYList;
		leastX(); //find the least X point 
	}
	
	//datasets for all data and hull data are created
	private XYDataset createDataset() {     
		final XYSeries hull = new XYSeries("Convex Hull",false, true);
	    for (int i = 0; i < hullXList.size(); i++) {
	    	hull.add(hullXList.get(i), hullYList.get(i));
	    }
	           
	    final XYSeries data = new XYSeries("All Data",false, true);
	    for (int i = 0; i < xList.size(); i++) {
	    	data.add(xList.get(i), yList.get(i));
	    }
	    
	    final XYSeriesCollection dataset = new XYSeriesCollection( );    
	    dataset.addSeries(data);
	    dataset.addSeries(hull);  
	    return dataset;
	}
	
	//plot is drawn
	public void createPlot() {
		String currentExercise = " ";
		
		//create label depending on the exercise number
		if (exercise.equals("C:/Users/100583357/workspace/ConvexHull/exercise-1.csv")) {
			currentExercise = "Exercise 1";
		} else if (exercise.equals("C:/Users/100583357/workspace/ConvexHull/exercise-2.csv")) {
			currentExercise = "Exercise 2";
		} else if (exercise.equals("C:/Users/100583357/workspace/ConvexHull/exercise-3.csv")) {
			currentExercise = "Exercise 3";
		} else if (exercise.equals("C:/Users/100583357/workspace/ConvexHull/exercise-4.csv")) {
			currentExercise = "Exercise 4";
		} else if (exercise.equals("C:/Users/100583357/workspace/ConvexHull/exercise-5.csv")) {
			currentExercise = "Exercise 5";
		} else {
			currentExercise = "Exercise 6";
		}
		
		//create and plot the solution
		JFreeChart xylineChart = ChartFactory.createXYLineChart(
				currentExercise,
				"X-Axis",
		        "Y-Axis",
		        createDataset(),
		        PlotOrientation.VERTICAL, true, true, false);
		
		ChartPanel chartPanel = new ChartPanel(xylineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(800 , 800));
		
		final XYPlot plot = xylineChart.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(0, Color.BLUE);
		renderer.setSeriesPaint(1, Color.RED);
		renderer.setSeriesLinesVisible(0, false);
		renderer.setSeriesShapesVisible(1, false);
		plot.setRenderer(renderer); 
		
		setContentPane(chartPanel);
	}
	
	//method that finds the least x value
	public void leastX() {
		double leastX = xList.get(0);
		for (int i = 1; i < xList.size(); i++) {
			if (xList.get(i) <= leastX) {
				leastX = xList.get(i);
				leastXIndex = i;
			}
		}
	}
	
	//method that finds the next most counterclockwise point on the hull
	public boolean checkCCW(double x1, double y1, double x2, double y2, double x3, double y3) {
		if (((y2 - y1) * (x3 - x2) - (x2 - x1) * (y3 - y2)) >= 0) {
			return false;
		} else {
			return true;
		}
	}

	//method that runs the main algorithm
	public void drawNextLine() {
		int current = leastXIndex, next;
		
		//for plotting purposes to ensure that it is looped back to least x point
		boolean reachedOnce = false;
		boolean reachedAgain = false;
		
		//loop that runs through the main algorithm
		do {
			if (reachedOnce) {
				reachedAgain = true;;
			}
			
            next = (current + 1) % xList.size();
            for (int i = 0; i < xList.size(); i++) {
              if (checkCCW(xList.get(current), yList.get(current), 
            		  xList.get(i), yList.get(i), 
            		  xList.get(next), yList.get(next))) {
                 next = i;
              }
            }
            
            //add the next hull point to the hull list
            hullXList.add(xList.get(next));
            hullYList.add(yList.get(next));
            current = next; 
            
            if (current == leastXIndex) {
            	reachedOnce = true;
            }
        } while (!reachedAgain);
	}
	
	public static void main(String[] args) {
		
		final long startTime = System.nanoTime();
		
		//all four linkedlists are created
		DLList xList = new DLList();
		DLList yList = new DLList();
		DLList hullXList = new DLList();
		DLList hullYList = new DLList();
		
		String csvFile = "exercise-1.csv";//file to retrive data points from
		exercise = csvFile;
		
        BufferedReader br = null;
        String line = "";
        
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

            	String[] entries = line.split(",");
            	if (!entries[0].equalsIgnoreCase("X")) {            		
            		xList.add(Double.parseDouble(entries[0]));
            		yList.add(Double.parseDouble(entries[1]));
            	}
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Hull hull = new Hull(xList, yList, hullXList, hullYList); //Hull object is created with the four linkedlists
        hull.pack( );          
        RefineryUtilities.centerFrameOnScreen(hull);          
        hull.setVisible( true ); 
        
		hull.drawNextLine(); 
		hull.createPlot();
		
		final long duration = System.nanoTime() - startTime;
		System.out.println(duration + " ns");
	}
}
