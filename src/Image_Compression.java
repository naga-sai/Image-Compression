import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import Jama.Matrix;


public class Image_Compression {

	private static boolean checkIsTransposed;
	private static DataOutputStream d_out;
	private static DataInputStream d_in;
	private static String fileName;
	private static ArrayList<ArrayList<Integer>> image_Values;
	static int image_height = 0, image_width = 0, max_gray_value;
	private static Matrix A;
	private static Matrix U;
	private static Matrix S;
	private static Matrix V;
	private static int rank;
	
	public static ArrayList<ArrayList<Integer>> p;
	private static Scanner image_scanner;
	private static Scanner s;
	private static DataInputStream dis;


	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		if (args != null && args.length > 0) {
			int op = Integer.parseInt(args[0]);
			switch (op) {
			
			case 1: {
				File image_file_path = new File(args[1]);
				image_scanner = new Scanner(image_file_path);
				image_Values = new ArrayList<ArrayList<Integer>>();
				String image_info=image_scanner.nextLine()+" "+image_scanner.nextLine();
				String image_info_array[]=image_info.split(" ");
				File image_file = new File(args[1]);
				fileName = image_file_path.getPath();
				s = new Scanner(image_file);
				int i1=0;
				while(i1 <image_info_array.length )
				{
					s.next();
					i1++;
				}
				image_width = Integer.parseInt(s.next());
				image_height = Integer.parseInt(s.next());
				max_gray_value = Integer.parseInt(s.next());
				int i2=0;
				while(i2<image_height)
				{   
					ArrayList<Integer> temp_array = new ArrayList<Integer>();
				    for (int jm = 0; jm < image_width; jm++)
				    {
					temp_array.add(Integer.parseInt(s.next()));
				    }
				    image_Values.add(temp_array);
					i2++;
				}
				
				DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new
		                 FileOutputStream(fileName.substring(0,fileName.indexOf("."))+ "_b" + ".pgm")));
				
				out.writeShort(image_width);
				out.writeShort(image_height);
			    out.writeByte(max_gray_value);
				for (ArrayList<Integer> arr : image_Values) {
					int k=0;
					while(k<arr.size())
					{   
						out.writeByte(arr.get(k));
						k++;
					}
				}
				out.flush();
				out.close();
				System.out.println("Binary file created");   
				break;

			}
			case 2: 
				File file = new File(args[1]);
				dis = new DataInputStream(new BufferedInputStream(new FileInputStream(args[1])));
				image_Values = new ArrayList<ArrayList<Integer>>();
				fileName = file.getPath();
				try {
					PrintWriter pw = new PrintWriter(fileName.substring(0,fileName.indexOf("."))+ "2" + ".pgm");
					pw.println("P2");
					pw.println("# Created by IrfanView");
					byte hghtWdth[]=new byte[5];
					//System.out.println("h1 : "+methodClass.byteToInt(hghtWdth[0])+methodClass.byteToInt(hghtWdth[1]));
					dis.read(hghtWdth);
					image_width=integralValue( convertToBinary(byteToInt(hghtWdth[0]))+convertToBinary(byteToInt(hghtWdth[1])));
					image_height=integralValue(convertToBinary(byteToInt(hghtWdth[2]))+convertToBinary(byteToInt(hghtWdth[3])));
					//image_width=methodClass.byteToInt(hghtWdth[0])+methodClass.byteToInt(hghtWdth[1]);
					//image_height=methodClass.byteToInt(hghtWdth[2])+methodClass.byteToInt(hghtWdth[3]);
                    System.out.println("print width :"+image_width);
                    System.out.println("print height :"+image_height);

					max_gray_value=byteToInt(hghtWdth[4]);
					pw.write(image_width+" ");
					pw.write(image_height+"\n");
					pw.write(max_gray_value+"\n");
					byte image_info[]=new byte[image_width*image_height];
					dis.read(image_info);
					int p_index=0;
					int count1=0;
					
					
					for(int i=0;i<image_height;i++)
					{
						for(int j=0;j<image_width;j++)
					    {
						int pixel_val;
						if((new Byte(image_info[p_index])).intValue()<0)
						{
							pixel_val=256+new Byte(image_info[p_index]).intValue();
							count1++;
						}
						else
							pixel_val=(new Byte(image_info[p_index])).intValue();
						pw.write(pixel_val+" ");
						p_index++;
					    }
					    pw.write("\n");
						
					}
					pw.close();
					System.out.println("neg_values :"+count1);
				} 
				catch (FileNotFoundException e) {
					System.out.println("output file can't be created. . . .");
				}
				
				System.out.println("original Image retrived");
				break;
			

			case 3:
				createNewSVD(args[1],args[2]);
				BinaryConversion(args[3]);
				break;
			case 4:
				ReadSvd(args[1]);
				break;
			}
			}

		
	}
	
	public static String convertToBinary(int a)
	{
		String x=Integer.toBinaryString(a);
		if(x.length()<=8)
		{
			int i=x.length();
			while(i<8)
			{   x="0"+x;
				i++;
			}
		}
		return x;
	}
	static int byteToInt(Byte b) {
		// TODO Auto-generated method stub
		if(b.intValue()<0)
		{
			return 256+b.intValue();
		}
		else
			return b.intValue();
	}

	static int integralValue(String string) {
		// TODO Auto-generated method stub
		int intVal=0;
		StringBuffer sb=new StringBuffer(string);
		sb.reverse();
		int i1=0;
		while(i1<sb.length())
		{
			
			if(sb.charAt(i1)=='1')
				intVal+=Math.pow(2, i1);

			i1++;
		}
	    return intVal;	
		
	}
	



	private static void ReadSvd(String file) {
		// TODO Auto-generated method stub
		  try
	      {
	         d_in = new DataInputStream(
	               new FileInputStream(file));
	         if (d_in.readBoolean() == true)
	         {
	            checkIsTransposed = true;
	         }
	         else
	         {
	            checkIsTransposed = false;
	         }
	         int uRowDimension = d_in.readShort(); 
	         int vRowDimension = d_in.readShort();
	         rank = d_in.readShort();              
	         U = new Matrix(uRowDimension, rank);
	         S = new Matrix(rank, rank);
	         V = new Matrix(vRowDimension, rank);
	         	
	         for (int i = 0; i < uRowDimension; i++)
	         {
	            for (int j = 0; j < rank; j++)
	            {
	               U.set(i, j, d_in.readShort() / 32768d);
	               System.out.println("u matrix elements"+U.get(i, j));
	            }
	         }
	         for (int i = 0; i < rank; i++)
	         {
	            for (int j = 0; j < rank; j++)
	            {
	               if (i == j)
	               {
	                  S.set(i, j, d_in.readFloat());
	               }
	               else
	               {
	                  S.set(i, j, 0.0);
	               }
	            }
	         }
	         for (int i = 0; i < vRowDimension; i++)
	         {
	            for (int j = 0; j < rank; j++)
	            {
	               V.set(i, j, d_in.readShort() / 32768d);
	            }
	         }
	         d_in.close();
	         
	         A=U.times(S.times(V.transpose()));
	         if(checkIsTransposed)
	         {
	        	 A=A.transpose();
	         }
	         
	         
	         double[][] arr = A.getArray();
	         int count=0;
	         
	         int[][] grays =
		               new int[A.getRowDimension()][A.getColumnDimension()];
		         int n = 0;
		       //  System.out.println("length"+grays.length);
		         for (int i = 0; i < grays.length; i++)
		         {
		              System.out.println("width"+grays[i].length);

		        	 for (int j = 0; j < grays[i].length; j++)
		            {
		        		// System.out.println("A matrix is"+A.get(i, j));
		            	n = (int) A.get(i, j);
		            	
		               if (n < 0)
		               {
		                   count++;
		            	   grays[i][j] = 0;
		               }
		               else if (n > 255)
		               {
		                  grays[i][j] = 255;
		               }
		               else
		               {
		                  grays[i][j] = n;
		               }
		            }
		         }
		         
		         System.out.println("negative values"+count);
		         String filename = file.split("_b\\.pgm\\.")[0];
		         String target = filename + "_k.pgm";
		         SaveImage(grays.length,grays[0].length,grays,target);
		        
			
	         
	         
	      }
	      catch (Exception e)
	      {
	         e.printStackTrace();
	      }
	}
	
	public static void SaveImage(int height, int width, int[][] grays,
			String target) {
		// TODO Auto-generated method stub
	try
    {
       PrintWriter output = new PrintWriter(target);
       output.println("P2");
       output.println("#Final Image");
       output.println(width + " " + height);
       output.println(255);

       //int i = 0;
       for (int i = 0; i < grays.length; i++)
       {
          for (int j = 0; j < grays[i].length; j++)
          {
          //output.print(grays[i / width][i % width] + " ");
          
        	  
        	  
             output.print(grays[i][j] + " ");
          
       }
          output.println();
       }

       
       output.close();
    }
    catch (Exception e)
    {
       e.printStackTrace();
    }

	}


	private static void BinaryConversion(String args) {
		// TODO Auto-generated method stub
		rank=Integer.parseInt(args);
		try
	      {
			d_out = new DataOutputStream(new FileOutputStream("Image_b.pgm.SVD"));
	         if (checkIsTransposed)
	         {
	            d_out.writeBoolean(true);
	         }
	         else
	         {
	            d_out.writeBoolean(false);
	         }
	         d_out.writeShort(U.getRowDimension());     
	         d_out.writeShort(V.getColumnDimension());  
	         d_out.writeShort(rank);                    
	         for (int i = 0; i < U.getRowDimension(); i++)
	         {
	            for (int j = 0; j < rank; j++)
	            {
	               d_out.writeShort((short) (U.get(i, j) * 32768d));
	            }
	         }
	         for (int i = 0; i < rank; i++)
	         {
	            d_out.writeFloat((float) (S.get(i, i)));
	         } 
	         for (int i = 0; i < V.getRowDimension(); i++)
	         {
	            for (int j = 0; j < rank; j++)
	            {
	               d_out.writeShort((short) (V.get(i, j) * 32768d));
	            }
	         }
	         d_out.close();
	      }
	      catch (Exception e)
	      {
	         e.printStackTrace();
	      }

		
	}

	private static void createNewSVD(String filename, String file2) {
		// TODO Auto-generated method stub
		  try
	      {
	         Scanner sc = new Scanner(new File(filename));
	         int width = sc.nextInt();
	         int height = sc.nextInt();
	         if (width > height)
	         {
	            checkIsTransposed = true;
	          int x=height;
	          height=width;
	          width=x;
	         }
	         else
	         {
	            checkIsTransposed = false;
	           }
	         
	         U = new Matrix(height, width);
	            S = new Matrix(width, width);
	            V = new Matrix(width, width);
	         sc.close();

	         Scanner sc2 = new Scanner(new File(file2));
	         for (int i = 0; i < U.getRowDimension(); i++)
	         {
	            for (int j = 0; j < U.getColumnDimension(); j++)
	            {
	               U.set(i, j, sc2.nextDouble());
	            }
	         }
	         for (int i = 0; i < S.getRowDimension(); i++)
	         {
	            for (int j = 0; j < S.getColumnDimension(); j++)
	            {
	               S.set(i, j, sc2.nextDouble());
	            }
	         }
	         for (int i = 0; i < V.getRowDimension(); i++)
	         {
	            for (int j = 0; j < V.getColumnDimension(); j++)
	            {
	               V.set(i, j, sc2.nextDouble());
	            }
	         }
	         sc2.close();
	        
	         
	      }
	      catch (Exception e)
	      {
	         e.printStackTrace();
	      }

	
	}
}
