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

	public static int[][] bellman_Ford_Complete(GraphArrayList g, int source) {
   		int numberVertices = g.vertices();
   		// two int arrays are returned
		int[] totalDistance = new int[numberVertices]; // the total cost from source to the vertex
		int[] father = new int[numberVertices]; // the predecessor of the vertex

		// Initializing arrays with negative values because nothing has been done yet :
		for (int i = 0; i < numberVertices; i++) {
			totalDistance[i] = -1;
			father[i] = -1;
		}
		totalDistance[source] = 0; // the distance from source to source is zero

		// The loop where all edges are relaxed :
		for (int i = 1; i < numberVertices - 1; i++) { // we do |V| - 1 iterations
			for (Edge e : g.edges()) { // for every edge
				if(!(totalDistance[e.to] == -1 && totalDistance[e.from] == -1)) { // we only check the edges having a path to the origin
					if (totalDistance[e.to] == -1 || totalDistance[e.to] > totalDistance[e.from] + e.cost) { // if the vertex destination is not treated or there is a costless path :
						// Relaxing the edge :
						totalDistance[e.to] = totalDistance[e.from] + e.cost;
						father[e.to] = e.from;
					}
				}
			}
		}

		/* There is no need to check for negative-weight loops in the context of this software
		 as the given graph should be made of edges with absolute values as costs */

		int[][] arrays = new int[2][numberVertices];
		arrays[0] = totalDistance;
		arrays[1] = father;

		return arrays;
	}

	public static int[] bellman_Ford(GraphArrayList g, int s, int t) {
   		// returns an array of vertices being the costless path from vertex s to vertex t
		int[][] bellmanFordResult = bellman_Ford_Complete(g, s);
		ArrayList<Integer> path = new ArrayList<>();
		while (bellmanFordResult[1][t] != s) {
			path.add(bellmanFordResult[1][t]);
			t = bellmanFordResult[1][t];
		}
		int[] result = new int[path.size()];
		for (int i = 0; i < path.size(); i++) {
			result[i] = path.get(i);
		}
		return result;
	}
}
