import java.util.ArrayList;
import java.io.*;
import java.util.*;
public class SeamCarving
{

   public static int[][] readpgm(String fn)
	 {		
        try {
            InputStream f = ClassLoader.getSystemClassLoader().getResourceAsStream(fn);
            BufferedReader d = new BufferedReader(new InputStreamReader(f));
            String magic = d.readLine();
            String line = d.readLine();
		   while (line.startsWith("#")) {
			  line = d.readLine();
		   }
		   Scanner s = new Scanner(line);
		   int width = s.nextInt();
		   int height = s.nextInt();		   
		   line = d.readLine();
		   s = new Scanner(line);
		   int maxVal = s.nextInt();
		   int[][] im = new int[height][width];
		   s = new Scanner(d);
		   int count = 0;
		   while (count < height*width) {
			  im[count / width][count % width] = s.nextInt();
			  count++;
		   }
		   return im;
        }
		
        catch(Throwable t) {
            t.printStackTrace(System.err) ;
            return null;
        }
    }

   public static void writepgm(int[][] image, String filename)
   {
	   try {
		   FileWriter fw = new FileWriter(filename);
		   BufferedWriter bw = new BufferedWriter(fw);

		   bw.write("" + image.length + " " + image[0].length + "\n");
		   bw.write("255\n");

		   for(int i = 0; i < image.length; i++)
		   {
		   		for(int j = 0; j < image[i].length; j++)
				{
					if(j > 0) bw.write(" ");
					bw.write("" + image[i][j]);
				}
		   		bw.write("\n");
		   }

		   bw.close();
		   fw.close();
	   } catch (IOException e) {
		   e.printStackTrace();
	   }
   }

	public static int[][] interest(int[][] image)
	{
		int[][] interestTable = new int[image.length][image[0].length];
		for(int i = 0; i < image.length; i++)
		{
			for(int j = 0; j < image[i].length; j++)
			{
				interestTable[i][j] = Math.abs(image[i][j] - (j == 0 ? image[i][j + 1] : j == image[i].length - 1 ? image[i][j - 1] : (image[i][j - 1] + image[i][j + 1]) / 2));
			}
		}
		return interestTable;
	}

	private static int getID(int i, int j, int width)
	{
		return 1 + (j * width + i);
	}

	public static Graph toGraph(int[][] itr)
	{
		int graphSize = itr.length * itr[0].length + 2; // Size = array size + src + dest points
		int width = itr.length;
		int height = itr[0].length;
		GraphArrayList graph = new GraphArrayList(graphSize);

		for(int i = 0; i < width; i++)
		{
			for(int j = 0; j < height; j++)
			{
				// Calculate pixel ID
				int id = getID(i, j, width);

				// If pixel is top of a row, add edge from 0 to current with cost 0
				if(j == 0) graph.addEdge(new Edge(0, id, 0));

				// If pixel is bottom of a row, add edge from pixel to graph end with cost
				if(j == height - 1) graph.addEdge(new Edge(id, graphSize-1, itr[i][j]));
				else
				{
					graph.addEdge(new Edge(id, getID(i, j + 1, width), itr[i][j]));

					// If pixel isn't in the left column, add edge to bottom left pixel
					if(i > 0) graph.addEdge(new Edge(id, getID(i - 1, j + 1, width), itr[i][j]));

					// If pixel isn't in the left column, add edge to bottom left pixel
					if(i < width - 1) graph.addEdge(new Edge(id, getID(i + 1, j + 1, width), itr[i][j]));
				}
			}
		}

		return graph;
	}
}
