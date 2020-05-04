import java.util.ArrayList;
import java.io.*;
import java.util.*;

import static java.lang.System.currentTimeMillis;

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

		   bw.write("P2\n");
		   bw.write("" + image[0].length + " " + image.length + "\n");
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

	public static GraphArrayList toGraph(int[][] itr)
	{
		int graphSize = itr.length * itr[0].length + 2; // Size = array size + src + dest points
		int height = itr.length;
		int width = itr[0].length;
		GraphArrayList graph = new GraphArrayList(graphSize);

		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++)
			{
				// Calculate pixel ID
				int id = getID(x, y, width);

				// Pixel y = 0, add link from graph entry to ID
				if(y == 0) graph.addEdge(new Edge(0, id, 0));

				if(y == height - 1)
				{
					graph.addEdge(new Edge(id, graphSize - 1, itr[y][x]));
				}
				else
				{
					graph.addEdge(new Edge(id, getID(x, y + 1, width), itr[y][x]));

					if(x > 0) graph.addEdge(new Edge(id, getID(x - 1, y + 1, width), itr[y][x]));
					if(x < width - 1) graph.addEdge(new Edge(id, getID(x + 1, y + 1, width), itr[y][x]));
				}
			}
		}
		return graph;
	}

	// returns an array of vertices id being the costless path from vertex s to vertex t
	public static int[] bellman_Ford(GraphArrayList g, int s, int t) {
		int numberVertices = g.vertices();

		int[] totalDistance = new int[numberVertices]; // the total cost from source to the vertex
		int[] father = new int[numberVertices]; // the predecessor of the vertex

		// Initializing arrays with negative values because nothing has been done yet :
		for (int i = 0; i < numberVertices; i++) {
			totalDistance[i] = -1;
			father[i] = -1;
		}
		totalDistance[s] = 0; // the distance from source to source is zero

		boolean distancesUpdated = true;
		// The loop where all edges are relaxed :
		for (int i = 1; i < numberVertices - 1; i++) { // we do |V| - 1 iterations
			if (distancesUpdated) {
				distancesUpdated = false;
				for (Edge e : g.edges()) { // for every edge
					if(!(totalDistance[e.to] == -1 && totalDistance[e.from] == -1)) { // we only check the edges having a path to the origin
						if (totalDistance[e.to] == -1 || totalDistance[e.to] > totalDistance[e.from] + e.cost) { // if the vertex destination is not treated or there is a costless path :
							// Relaxing the edge :
							totalDistance[e.to] = totalDistance[e.from] + e.cost;
							distancesUpdated = true;
							father[e.to] = e.from;
						}
					}
				}
			}
		}

		// Gathering the consecutives fathers from t to the source
		ArrayList<Integer> path = new ArrayList<>();
		while (father[t] != s) {
			path.add(father[t]);
			t = father[t];
		}

		// Putting them in an array
		int[] result = new int[path.size()];
		for (int i = 0; i < path.size(); i++) {
			result[i] = path.get(i);
		}

		// The result is an array of vertices representing the path to delete in the image
		return result;
	}

	public static int[][] truncate(int[][] image, int[] path)
	{
		int[][] result = new int[image.length][image[0].length - 1];
		for (int i = 0; i < path.length; i++)
		{
			int pID = path[i] - 1;

			int y = pID / image[0].length;
			int x = pID % image[0].length;

			for(int px = 0; px < x; px++) result[y][px] = image[y][px];

			for(int px = x + 1; px < image[0].length; px++) result[y][px - 1] = image[y][px];
		}

		return result;
	}

	public static boolean applySimCarving(String src, String dst, int factor)
	{
		try
		{
			int[][] img = SeamCarving.readpgm(src);

			for (int i = 0; i < factor; i++)
			{
				// calculate the interest matrix
				int[][] itr = SeamCarving.interest(img);

				// convert it into a graph
				GraphArrayList g = SeamCarving.toGraph(itr);

				int[] plusCourtChemin = SeamCarving.bellman_Ford(g, 0, g.vertices() - 1);

				// remove the calculated path from the image
				img = SeamCarving.truncate(img, plusCourtChemin);

				int pct = (int)((double)i / (double)factor * 100.0);
				System.out.print("\r" + "Prgoression : " + pct + "%");
			}
			// write the final image
			SeamCarving.writepgm(img, dst);

		}
		catch(Exception e)
		{
			System.out.println(e.getStackTrace());
			return false;
		}
		return true;
	}
}
