import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import Jama.SingularValueDecomposition;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Scanner;
import Jama.Matrix;


class mysvd {

	   private Matrix u;
	   private Matrix s;
	   private Matrix v;
	   private int rank;
	   private boolean transposed;
	   private final double FACTOR = 32768.0;
	   
	   public Matrix getU()
	   {
	      return u;
	   }
	   public void setU(Matrix u)
	   {
	      this.u = u;
	   }

	   public Matrix getS()
	   {
	      return s;
	   }

	   public  void setS(Matrix s)
	   {
	      this.s = s;
	   }

	  public  Matrix getV()
	   {
	      return v;
	   }

	   public  void setV(Matrix v)
	   {
	      this.v = v;
	   }
	   
	   public void writeText(String filename)
	   {
	      try
	      {
	         PrintWriter output = new PrintWriter(filename);
	         for (int i = 0; i < u.getRowDimension(); i++)
	         {
	            for (int j = 0; j < v.getRowDimension(); j++)
	            {
	               output.print(u.get(i, j) + " ");
	            }
	         }
	         for (int i = 0; i < v.getRowDimension(); i++)
	         {
	            for (int j = 0; j < v.getRowDimension(); j++)
	            {
	               output.print(s.get(i, j) + " ");
	            }
	         }
	         for (int i = 0; i < v.getRowDimension(); i++)
	         {
	            for (int j = 0; j < v.getRowDimension(); j++)
	            {
	               output.print(v.get(i, j) + " ");
	            }
	         }
	         output.close();
	      }
	      catch (Exception e)
	      {
	         e.printStackTrace();
	      }
	   }
	   
	   //method to read the SVD file
	   public void readText(String filename)
	   {
	      try
	      {
	         Scanner scanner = new Scanner(new File(filename));
	         for (int i = 0; i < u.getRowDimension(); i++)
	         {
	            for (int j = 0; j < u.getColumnDimension(); j++)
	            {
	               u.set(i, j, scanner.nextDouble());
	            }
	         }
	         for (int i = 0; i < s.getRowDimension(); i++)
	         {
	            for (int j = 0; j < s.getColumnDimension(); j++)
	            {
	               s.set(i, j, scanner.nextDouble());
	            }
	         }
	         for (int i = 0; i < v.getRowDimension(); i++)
	         {
	            for (int j = 0; j < v.getColumnDimension(); j++)
	            {
	               v.set(i, j, scanner.nextDouble());
	            }
	         }
	         scanner.close();
	      }
	      catch (Exception e)
	      {
	         e.printStackTrace();
	      }
	   }
	   
	   //Metod to read the header file
	   public void readHeader(String filename)
	   {
	      try
	      {
	         Scanner scanner = new Scanner(new File(filename));
	         int width = scanner.nextInt();
	         int height = scanner.nextInt();
	         if (width > height)
	         {
	            transposed = true;
	            u = new Matrix(width, height);
	            s = new Matrix(height, height);
	            v = new Matrix(height, height);
	         }
	         else
	         {
	            transposed = false;
	            u = new Matrix(height, width);
	            s = new Matrix(width, width);
	            v = new Matrix(width, width);
	         }

	         scanner.close();
	      }
	      catch (Exception e)
	      {
	         e.printStackTrace();
	      }
	   }

	   public void setRank(int rank)
	   {
	      this.rank = rank;
	   }
	   
	   public void writeBinary(String filename)
	   {
	      try
	      {
	         DataOutputStream output = new DataOutputStream(new FileOutputStream(filename));
	         if (transposed)
	         {
	            output.writeBoolean(true);
	         }
	         else
	         {
	            output.writeBoolean(false);
	         }
	         output.writeShort(u.getRowDimension());     // height
	         output.writeShort(v.getColumnDimension());  // width
	         output.writeShort(rank);                    // rank
	         for (int i = 0; i < u.getRowDimension(); i++)
	         {
	            for (int j = 0; j < rank; j++)
	            {
	               output.writeShort((short) (u.get(i, j) * FACTOR));
	            }
	         }
	         for (int i = 0; i < rank; i++)
	         {
	            output.writeFloat((float) (s.get(i, i)));
	         }
	         for (int i = 0; i < v.getRowDimension(); i++)
	         {
	            for (int j = 0; j < rank; j++)
	            {
	               output.writeShort((short) (v.get(i, j) * FACTOR));
	            }
	         }
	         output.close();
	      }
	      catch (Exception e)
	      {
	         e.printStackTrace();
	      }
	   }

	   
	   public void readBinary(String filename)
	   {
	      try
	      {
	         DataInputStream input = new DataInputStream(
	               new FileInputStream(filename));
	         if (input.readBoolean() == true)
	         {
	            transposed = true;
	         }
	         else
	         {
	            transposed = false;
	         }
	         int uRowDimension = input.readShort(); // First number is row dimension
	         int vRowDimension = input.readShort(); // Second is column dimension
	         rank = input.readShort();              // Third number is rank
	         u = new Matrix(uRowDimension, rank);
	         s = new Matrix(rank, rank);
	         v = new Matrix(vRowDimension, rank);
	         for (int i = 0; i < uRowDimension; i++)
	         {
	            for (int j = 0; j < rank; j++)
	            {
	               u.set(i, j, input.readShort() / FACTOR);
	            }
	         }
	         for (int i = 0; i < rank; i++)
	         {
	            for (int j = 0; j < rank; j++)
	            {
	               if (i == j)
	               {
	                  s.set(i, j, input.readFloat());
	               }
	               else
	               {
	                  s.set(i, j, 0.0);
	               }
	            }
	         }
	         for (int i = 0; i < vRowDimension; i++)
	         {
	            for (int j = 0; j < rank; j++)
	            {
	               v.set(i, j, input.readShort() / FACTOR);
	            }
	         }
	         input.close();
	      }
	      catch (Exception e)
	      {
	         e.printStackTrace();
	      }
	   }

	   public double normError()
	   {
	      int size = Math.min(u.getRowDimension(), v.getRowDimension());
	      
	      double norm1 = 0;
	      for (int i = 0; i < size; i++)
	      {
	         norm1 += s.get(i, i) * s.get(i, i);
	      }

	      double norm2 = 0;
	      for (int i = rank; i < size; i++)
	      {
	         norm2 += s.get(i, i) * s.get(i, i);
	      }
	      return Math.sqrt(norm2 / norm1);
	   }

	   
	   public boolean isTransposed()
	   {
	      return transposed;
	   }
	   

}



public class SVD_Decomposition {
	static int height = 0, width = 0, maxPixVal;
	public static void main(String[] args) throws FileNotFoundException {
		File file = new File(args[0]);
		Scanner sc = new Scanner(file);
		String line = sc.nextLine();
		if(line.startsWith("P")||line.startsWith("#"))
		sc.nextLine();
		width = Integer.parseInt(sc.next());
		height = Integer.parseInt(sc.next());
		maxPixVal = Integer.parseInt(sc.next());
		  try
	      {
	         PrintWriter output = new PrintWriter("Header.txt");
	         
	         output.println(width + " " + height + " " + maxPixVal);
	         output.close();
	      }
	      catch (Exception e)
	      {
	         e.printStackTrace();
	      }
		
		Matrix mat = new Matrix(height, width);
		for(int i=0;i<height;i++)
			for(int j=0;j<width;j++)
				mat.set(i,j,Integer.parseInt(sc.next()));
        
		if(width>height)
		{
			mat=mat.transpose();
		}
		
		SingularValueDecomposition svd = new SingularValueDecomposition(mat);
		mysvd ms = new mysvd();
        Matrix u = svd.getU();
        Matrix s = svd.getS();
        Matrix v = svd.getV();
        try
	      { 
	         PrintWriter output = new PrintWriter("SVD.txt");
	         System.out.println("u row dim"+u.getRowDimension()+u.getColumnDimension());
	         System.out.println("v row dem"+v.getRowDimension()+""+v.getColumnDimension());
	         System.out.println("s row dem"+s.getRowDimension()+""+s.getColumnDimension());

	         for (int i = 0; i < u.getRowDimension(); i++)
	         {
	            for (int j = 0; j < v.getRowDimension(); j++)
	            {
	               output.print(u.get(i, j) + " ");
	            }
	         }
	         for (int i = 0; i < v.getRowDimension(); i++)
	         {
	            for (int j = 0; j < v.getRowDimension(); j++)
	            {
	               output.print(s.get(i, j) + " ");
	            }
	         }
	         for (int i = 0; i < v.getRowDimension(); i++)
	         {
	            for (int j = 0; j < v.getRowDimension(); j++)
	            {
	               output.print(v.get(i, j) + " ");
	            }
	         }
	         output.close();
	         System.out.println("Completed");
	      }
	      catch (Exception e)
	      {
	         e.printStackTrace();
	      }
	   
		
		
	}
	

}
