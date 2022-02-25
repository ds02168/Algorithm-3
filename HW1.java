import java.lang.Comparable;
import java.util.Random;
import java.util.Scanner;

abstract class AbstractSort {
	public static void sort(Comparable[] a) {
	};

	protected static boolean less(Comparable v, Comparable w) {
		return v.compareTo(w) < 0;

	}

	protected static void exch(Comparable[] a, int i, int j) {
		Comparable t = a[i];
		a[i] = a[j];
		a[j] = t;
	}

	protected static void show(Comparable[] a) {
		for (int i = 0; i < a.length; i++)
			System.out.print(a[i] + " ");
		System.out.println();
	}

	protected static boolean isSorted(Comparable[] a) {
		for (int i = 1; i < a.length; i++)
			if (less(a[i], a[i - 1]))
				return false;
		return true;
	}

}

class Selection extends AbstractSort{
	public static void sort(Comparable[] a) {
		int N = a.length;
		for(int i=0;i<N-1;i++) {
			int min =i;
			for(int j=i+1;j<N;j++) {
				if(less(a[j],a[min]))
					min=j;
			}
			exch(a,i,min);
		}
		assert isSorted(a);
	}
}

class Insertion extends AbstractSort {
	public static void sort(Comparable[] a) {
		int N = a.length;
		for (int i = 1; i < N; i++) {
			for (int j = i; j > 0 && less(a[j], a[j - 1]); j--) {
				exch(a, j, j - 1);
			}
		}
		assert isSorted(a);
	}

}

class Shell extends AbstractSort {
	public static void sort(Comparable[] a) {
		int N = a.length;
		int h=1;
		while(h<N/3) h=3*h+1;
		
		while(h>=1) {
			for(int i=h;i<N;i++)
				for(int j=i;j>=h&&less(a[j],a[j-h]);j-=h)
					exch(a,j,j-h);
			h/=3;
		}
		
		assert isSorted(a);
	}
}


class MergeTD extends AbstractSort{
	private static void merge(Comparable[] a, Comparable[] aux, int lo, int mid, int hi) {
		for(int k=lo;k<=hi;k++)
			aux[k]=a[k];
		
		int i=lo, j=mid+1;
		for(int k=lo;k<=hi;k++) {
			if(i>mid)	a[k]=aux[j++];
			else if(j>hi) a[k]=aux[i++];
			else if(less(aux[j],aux[i])) a[k]=aux[j++];
			else a[k]=aux[i++];
		}
	}
	
	public static void sort(Comparable[] a) {
		Comparable[] aux = new Comparable[a.length];
		sort(a,aux,0,a.length-1);
	}
	private static void sort(Comparable[] a, Comparable[] aux, int lo, int hi) {
		if(hi<=lo) return;
		int mid = lo+(hi-lo)/2;
		sort(a,aux,lo,mid);
		sort(a,aux,mid+1,hi);
		merge(a,aux,lo,mid,hi);
	}
	
}

class MergeBU extends AbstractSort{
	private static void merge(Comparable[] in, Comparable[] out, int lo, int mid, int hi) {
		int i=lo, j=mid+1;
		for(int k=lo;k<=hi;k++) {
			if(i>mid) out[k] = in[j++];
			else if(j>hi) out[k] = in[i++];
			else if(less(in[j],in[i])) out[k]=in[j++];
			else out[k]=in[i++];
		}
	}
	public static void sort(Comparable[] a) {
		Comparable[] src = a, dst = new Comparable[a.length],tmp;
		int N = a.length;
		for(int n=1;n<N;n*=2) {
			for(int i=0;i<N;i+=2*n)
				merge(src,dst,i,i+n-1,Math.min(i+2*n-1, N-1));
			tmp=src;src=dst;dst=tmp;
		}
		if(src != a) System.arraycopy(src, 0, a, 0, N);
	}
}


class Point implements Comparable{
	double x,y;
	private Double distance;
	Point(double x, double y) {
		this.x=x; this.y=y;
		distance = new Double(Math.sqrt((x * x) + (y * y)));
	}

	Double getDistance() {
		return distance;
	}

	@Override
	public int compareTo(Object o) {
		return (int)(this.getDistance()-((Point)o).getDistance());
	}
}

public class HW1 {
	public static void main(String[] args) {
		long Start,Finish,Duration;
		
		
		System.out.print("점의 수? ");
		Scanner sc = new Scanner(System.in);
		int numPoints = sc.nextInt();
		
		Random rand = new Random(numPoints);
		
		Point[] A = new Point[numPoints];
		for (int i = 0; i < numPoints; i++)
			A[i] = new Point(rand.nextDouble() * 100, rand.nextDouble() * 100);
		
		Point[] Backup = new Point[numPoints];
		for(int i=0;i<numPoints;i++)
			Backup[i] = A[i];
		
		Start=System.currentTimeMillis();
		Selection.sort(A);
		Finish=System.currentTimeMillis();
		Duration = (Finish - Start);
		System.out.println("선택정렬: 정렬여부 = " + Selection.isSorted(A) + ", 소요 시간 = " + Duration + "ms");
		for(int i=0;i<numPoints;i++)
			A[i] = Backup[i];
		
		Start=System.currentTimeMillis();
		Insertion.sort(A);
		Finish=System.currentTimeMillis();
		Duration = (Finish - Start);
		System.out.println("삽입정렬: 정렬여부 = " + Insertion.isSorted(A) + ", 소요 시간 = " + Duration + "ms");
		for(int i=0;i<numPoints;i++)
			A[i] = Backup[i];
		
		Start=System.currentTimeMillis();
		Shell.sort(A);
		Finish=System.currentTimeMillis();
		Duration = (Finish - Start);
		System.out.println("Shell정렬: 정렬여부 = " + Shell.isSorted(A) + ", 소요 시간 = " + Duration + "ms");
		for(int i=0;i<numPoints;i++)
			A[i] = Backup[i];
		
		Start=System.currentTimeMillis();
		MergeTD.sort(A);
		Finish=System.currentTimeMillis();
		Duration = (Finish - Start);
		System.out.println("Top Down 합병정렬: 정렬여부 = " + MergeTD.isSorted(A) + ", 소요 시간 = " + Duration + "ms");
		for(int i=0;i<numPoints;i++)
			A[i] = Backup[i];
		
		Start=System.currentTimeMillis();
		MergeBU.sort(A);
		Finish=System.currentTimeMillis();
		Duration = (Finish - Start);
		System.out.println("Bottom Up 합병정렬: 정렬여부 = " + MergeBU.isSorted(A) + ", 소요 시간 = " + Duration + "ms");
		
	}
}
