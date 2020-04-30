import static java.lang.System.currentTimeMillis;

class Test
{
   static boolean visite[];
   public static void dfs(Graph g, int u)
	 {
		visite[u] = true;
		System.out.println("Je visite " + u);
		for (Edge e: g.next(u))
		  if (!visite[e.to])
			dfs(g,e.to);
	 }
   
   public static void testGraph()
	 {
		int n = 5;
		int i,j;
		GraphArrayList g = new GraphArrayList(n*n+2);
		
		for (i = 0; i < n-1; i++)
		  for (j = 0; j < n ; j++)
			g.addEdge(new Edge(n*i+j, n*(i+1)+j, 1664 - (i+j)));

		for (j = 0; j < n ; j++)		  
		  g.addEdge(new Edge(n*(n-1)+j, n*n, 666));
		
		for (j = 0; j < n ; j++)					
		  g.addEdge(new Edge(n*n+1, j, 0));
		
		g.addEdge(new Edge(13,17,1337));
		g.writeFile("test.dot");
		// dfs à partir du sommet 3
		visite = new boolean[n*n+2];
		dfs(g, 3);
	 }

	 // darkens the path in the image to visualize it
	public static int[][] dark_path(int[][] source, int[] path)
	{
		int[][] result = source;
   		int pixelID;
   		int x, y;
		for (int i = 0; i < path.length; i++) {
			pixelID = path[i];
			x = pixelID / source[0].length;
			y = pixelID % source[0].length;
			result[x][y] = 0; // set the pixel to black
		}
		return result;
	}
   
   public static void main(String[] args)
	 {
		 //testGraph();
		 int[][] img = SeamCarving.readpgm("resources/ex2.pgm");

		 int reduction = 100; // the number of times we reduce the image
		 for (int i = 0; i < reduction; i++) {
		 	 // calculate the interest matrix
			 int[][] itr = SeamCarving.interest(img);
			 // convert it into a graph
			 GraphArrayList g = SeamCarving.toGraph(itr);
			 // determine the costless path from top to bottom
			 long ms = currentTimeMillis();
			 int[] plusCourtChemin = SeamCarving.bellman_Ford(g, 0, g.vertices() - 1);

			 // remove the calculated path from the image
			 img = SeamCarving.truncate(img, plusCourtChemin);

			 System.out.println(currentTimeMillis() - ms + "ms");
			 // update the image with shortest path trace
			 //img = dark_path(img, plusCourtChemin);
		 }
		 // write the final image
		 SeamCarving.writepgm(img, "ex2_result.pgm");

	 }
}
