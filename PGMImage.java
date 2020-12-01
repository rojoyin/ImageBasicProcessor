/* ********************************************************************	*
 * this class defines an object of in format P2, represents images as a	*
 * matrix of values to perform operations over this set as a whole.		*
 * For each method the documentation explains what computation performs	*
 * author: Juan Jimenez													*
 * ******************************************************************** */
import java.io.*;
import java.util.Scanner;

public class PGMImage {
	private int width;
	private int height;
	private int maxShade;
	private short[][] pixels;
	/* ************************************************* *
	 * Constructor.					   					 *
	 * @param filename the name of the file in P2 format *
	 * ************************************************* */
	public PGMImage(String filename){
		File auxiliar=new File(filename);
		try{
			Scanner sc=new Scanner(auxiliar);
			sc.next();//first character is dismissed because we
					  //know is a P2 file.
			width=sc.nextInt();
			height=sc.nextInt();
			maxShade=sc.nextInt();
			/* **********************************
			 *Filling the matrix with the values*
			 *of the scanned file.              *
			 * ******************************** */
			pixels = new short [height][width];
			for (int rows=0;rows<height;rows++){
				for (int cols=0;cols<width;cols++){
					pixels[rows][cols]=sc.nextShort();
				}
			}
			sc.close();//closing the scanner
		/**In case the file does not exists, empty array 
		 * will be generated.*/
		} catch (IOException e){
			width=0;
			height=0;
			maxShade=0;
			pixels = new short [height][width];
		}
	}
	/* ******************************************** *
	 *getters and setter							*
	 *@return the width of the image.				*
	 *@return the height of the image.				*
	 *@return the max shade value.					*
	 *@return the pixel matrix containing the values*
	 *in gray scale of all the pixels.				*
	 * ******************************************** */
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getMaxShade() {
		return maxShade;
	}

	public void setMaxShade(int maxShade) {
		this.maxShade = maxShade;
	}

	public short[][] getPixels() {
		return pixels;
	}

	public void setPixels(short[][] pixels) {
		this.pixels = pixels;
	}
	
	/* ******************************************** *
	 * method to invert the gray scale of an image. *
	 * to achieve this task, a rest must be done to *
	 * convert a given number into its complementary*
	 * representation. This method is implemented by*
	 * calling a complement method developed later. *
	 * ******************************************** */
	public void invert(){
		short [][] auxInvert=new short [getHeight()][getWidth()];
		for (int rows=0;rows<height;rows++){
			for (int cols=0;cols<width;cols++){
				auxInvert[rows][cols]=complement(pixels[rows][cols]);
			}
		}
		//call for having the graphic in P2 type
		graph("GraphInverted.pgm",getHeight(),getWidth(),auxInvert);
	}
	/* ***************************************************** *
	 * This method computes the complement representation of *
	 * a given value, referred to the max shade value.	 	 *
	 * e.g. for a pixel with a value of 140			 		 *
	 * within an image with a max value of shading of 255,	 *
	 * the number must become 255-140=115.			 		 *
	 * ***************************************************** */
	public short complement(int pixelValue){
		pixelValue=maxShade-pixelValue;
		return (short) pixelValue;
	}
	/* ***************************************************** *
	 * method to rotate the matrix by 90 degrees clockwise   *
	 * to perform this operation all the numbers inside the  *
	 * matrix change their position to have a rotated matrix *
	 * where the values in a row become column values, and   *
	 * column values become row values, there for the height *
	 * and the width are exchanged, when compared to the     *
	 * original image.					 					 *
	 * ***************************************************** */
	public void rotateClockwise(){
		short [][] cross=new short[width][height];
		for (int i=0;i<width;i++){
			for (int j=0;j<height;j++){
				cross[i][j]=pixels[height-1-j][i];
			}
		}
		//call for having the graphic in P2 type
		graph("GraphRotated.pgm",getWidth(),getHeight(),cross);
	}
	/* ********************************************************* *
	 * The method graph makes a file in format P2. This method   *
	 * will be used by invert and rotateClockwise to make a file *
	 * with the result of modified images.			         	 *
	 * @param file the name of the output file		      		 *
	 * @param height the height and, @param width 		      	 *
	 * the specific dimensions of the output		       		 *
	 * @param auxArray the array containing with the 	      	 *
	 * specific values of the pixels.			      			 *
	 * ********************************************************* */
	public void graph(String file, int height, int width, short [][] auxArray){
		File arch=new File(file);
		try{
			BufferedWriter writterBuffer=
					new BufferedWriter(new FileWriter(arch));
			writterBuffer.write("P2"+"\n");
			writterBuffer.write(width+" "+height+"\n");
			writterBuffer.write(getMaxShade()+"\n");
			byte elements=0;
			for (int i=0;i<height;i++){
				for (int j=0;j<width;j++){
					writterBuffer.write(auxArray[i][j]+" ");
					elements++;
				}
				//the file must have up to 16 elements per row
				//starting the count with element 0
				if (elements==15){
					writterBuffer.write("\n");
					elements=0;
				}
			}
			writterBuffer.write("\n");
			writterBuffer.close();
		}catch (IOException e){
            System.out.println("File does not exist");
        }
	}
	/* ********************************************************** *
	 * exportToP1 method that exports the image to a P1 file      *
	 * by comparing each value of the matrix against the treshold *
	 * defined by the calling to the average method.              *
	 * ********************************************************** */
		/* ************************************************** *
		 * format of the header for P1 files.		      	  *
		 * P1						      					  *
		 * 24 7						      				 	  *
		 * ....						      					  *
		 * P1 specifies the class of file, 24 corresponds to  *
		 * width and 7 corresponds to height. after that,     *
		 * the values are either 1 or 0, where 1 is white and *
		 * 0 is black.  				      				  *
		 * ************************************************** */
	public void exportToP1(){
		double treshold=average();
		File arch=new File("graphP1.pbm");
		try{
			BufferedWriter writterBufferP1=
					new BufferedWriter(new FileWriter(arch));
			writterBufferP1.write("P1"+"\n");
			writterBufferP1.write(getWidth()+" "+getHeight()+"\n");
			byte elements=0;
			for (int rows=0;rows<getHeight();rows++){
				for (int columns=0;columns<getWidth();columns++){
					if(pixels[rows][columns]>=treshold){
						writterBufferP1.write(1+" ");
					}else{
						writterBufferP1.write(0+" ");
					}
					elements++;
					if (elements==15){
						writterBufferP1.write("\n");
						elements=0;
					}
				}
			}writterBufferP1.close();
		}catch (IOException e){
		           System.out.println("File does not exist");
		       }
	}
	

	/* ***************************************************** *
	 * method to compute the average value of all the pixels *
	 * inside the matrix of the image.			 			 *
	 * ***************************************************** */
	public double average(){
		int counter=0,sum=0;
		for (int rows=0;rows<getHeight();rows++){
			for (int columns=0;columns<getWidth();columns++){
				sum=sum+getPixels()[rows][columns];
				counter++;
			}
		}
		return sum/counter;
	}
}
