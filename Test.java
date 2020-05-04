import static java.lang.System.currentTimeMillis;
import static java.lang.System.exit;

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
		if(args.length == 0) // No argument passed, print help
		{
			System.out.println("Use : <src_image> <factor> <(optional)dst_image>");
			System.out.println("Accepted image formats : .pgm");
			System.out.println("<factor> must be less than <src_img> width");
			exit(1);
		}
		else if(args.length == 1) // too few arguments
		{
			System.out.println("Too few arguments");
			exit(1);
		}
		else if(args.length > 3) // too much arguments
		{
			System.out.println("Too many arguments");
			exit(1);
		}

		String src = args[0];

		int factor = 0;
		try {
			factor = Integer.parseInt(args[1]);
		}
		catch(Exception e)
		{
			System.out.println("<factor> parameter of value " + args[1] + " is not a valid number !");
			exit(1);
		}

		String dst = "";
		if(args.length == 3) dst = args[2];
		else
		{
			dst = src.substring(0, src.lastIndexOf('.')) + "_res.pgm";
		}

		 long start = currentTimeMillis();
		SeamCarving.applySimCarving(src, dst, factor);

		 System.out.println("Total time : " + (currentTimeMillis() - start) + " ms");
	 }
}
