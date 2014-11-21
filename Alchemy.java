import java.util.Arrays;
import java.util.Scanner;
/**
 * http://serjudging.vanb.org/wp-content/uploads/SER-2014-D1-Problems.pdf
 * Explanation of Alchemy 
 * The constraints on this problem are very strict, no interlocking or intersecting circles is 
 * a key piece of information. This means that given 3 nested circles, there is always a way to activate
 * the optimal number of activations for that circle. It is entirely possible to completely skip a circle.
 * Because of this, we can evaluate each circle starting from the inside and given the number of
 * parents it has, calculate the optimal score for that circle. Sorting by radius is n log n, 
 * enumerating parents is n^2 for a performance of O(n^2). Finally, we will use counting sort to
 * enumerate the traversal. All we need to know is how many rings must be unlocked before ring n,
 * from there we can make choices based on lower id so as to preserve lexicographical order.
 *
 */

public class Alchemy {
  static class Circle implements Comparable<Circle> {
    int x, y, r;
    int a, b;
    int label;
    Circle parent = null;
    
    public Circle (int x, int y, int r, int a, int b, int l) {
      this.x = x;
      this.y = y;
      this.r = r;
      this.a = a;
      this.b = b;
      this.label = l;
    }
    
    public int compareTo(Circle o) {
      return r - o.r;
    }
    
    public boolean isInside(Circle o) {
      return ((x - o.x) * (x - o.x) + (y - o.y) * (y - o.y) < o.r * o.r);
    }
  }
  
  public static void main(String[] args) {
    Scanner s = new Scanner(System.in);
      int N = s.nextInt();
      Circle[] circles = new Circle[N];
      for (int i = 0; i < N; i++) {
        circles[i] = new Circle(s.nextInt(), s.nextInt(), s.nextInt(), s.nextInt(), s.nextInt(), i+1);
      }
      //sort circles by ascending radius
      Arrays.sort(circles);
      int[] values = new int[N];
      int[] maxValues = new int[N];
      int[] ancestors = new int[N];
      int[] minAncestors = new int[N];
      int[] parent = new int[N];
      int total = 0;
      //for each circle
      for (int i = 0; i < N; i++) {
        Circle c = circles[i];
        for (int j = i + 1; j < N; j++) {
        //if this is inside the bigger circle
          if (c.isInside(circles[j])) {
        	//if the bigger circle is it's first parent, assign
        	//works because sorted by ascending
            if (parent[i] == 0) parent[i] = j;
            ancestors[i]++;
            //only add this if it's beneficial
            values[i] += (ancestors[i] % 2 == 1 ? c.a : c.b);
            maxValues[i] = Math.max(maxValues[i], values[i]);
          }
        }
        //add the max value of this circle to the total
        total += maxValues[i];
        int curValue = values[i];
        //set up for later tracing of path
        for (int j = ancestors[i]; j >= 0; j--) {
          //minimum before this ring
          //perform counting sort to order
          if (curValue == maxValues[i]) minAncestors[i] = j;
          curValue -= (j % 2 == 1 ? c.a : c.b);
        }
      }
      
      System.out.println(total);
      
      boolean[] active = new boolean[N];
      boolean[] locked = new boolean[N];
      for (int i = 0; i < N; i++) {
        if (i > 0) System.out.print(" ");
        Arrays.fill(locked, false);
        int best = -1;
        for (int j = 0; j < N; j++) {
          //if this is locked, it's parent must also be locked
          if (locked[j]) {
            locked[parent[j]] = true;
            continue;
          }
          //if it's marked active, dont lock it!
          if (active[j]) continue;
          //if we can unlock everything above this one
          //and it's the lowest possible, it's the best to unlock.
          if (values[j] == maxValues[j] && (best == -1 || circles[j].label < circles[best].label)) best = j;
          if (ancestors[j] == minAncestors[j]) locked[parent[j]] = true;
        }
        //unlock it
        active[best] = true;
        //update lower circles
        for (int j = 0; j < best; j++) {
          if (circles[j].isInside(circles[best])) {
            values[j] -= (ancestors[j] % 2 == 1 ? circles[j].a : circles[j].b);
            ancestors[j]--;
          }
        }
        System.out.print(circles[best].label);
      }
      System.out.println();
    }
}
