import java.util.Random;
import java.util.Scanner;

public class HW3 {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.print("난수의 seed값과 심볼 테이블 크기를 입력: ");
		long seed = sc.nextLong();
		int size = sc.nextInt();
		
		LinearProbingHashST<Integer,Integer> lht = new LinearProbingHashST<>(size);
		DoubleHashingST<Integer,Integer> dht = new DoubleHashingST<>(size);
		CuckooHashingST<Integer,Integer> cht = new CuckooHashingST<>(size, 0.45);
		Random rand = new Random(seed);
		
		int lcount = 0, dcount = 0, ccount = 0;
		for (int i = 0; i < size * 0.45; i++) {
			int key = rand.nextInt(), value;
			if (lht.contains(key))
				value = lht.get(key) + 1;
			else value = 1;
			lcount += lht.put(key, value);
			dcount += dht.put(key, value);
			ccount += cht.put(key, value);
		};
		
		System.out.println("Linear Probing: put count = " + lcount);
		lht.print();
		System.out.println("\nDouble Hashing: put count = " + dcount);
		dht.print();
		System.out.println("\nCuckoo Hashing: put count = " + ccount);
		cht.print();
		
		sc.close();
	}
}


class LinearProbingHashST<K,V>{
	private int N;
	private int M;
	private K[] keys;
	private V[] vals;
	private K[] table;
	private int totalLength;
	private int maxLength;
	private K maxKey;
	
	public LinearProbingHashST(){this(31);}
	public LinearProbingHashST(int M){
		this.M=minDecimal(M);
		keys = (K[]) new Object[this.M];
		table = (K[]) new Object[this.M];
		vals = (V[]) new Object[this.M];
		totalLength = 0;
		maxLength = 1;
		maxKey = null;
		N = 0;
	}
	
	public int minDecimal(int M) {
		boolean flag = false;
		for(int Decimal=M;;Decimal++) {
			for(int i=2;i<Decimal;i++) {
				if(Decimal%i==0)
					flag=true;
			}
			if(flag == false)
				return Decimal;
			else
				flag = false;
		}
	}
	
	public boolean contains(K key) {return get(key) != null;}
	public void print() {
		totalLength = 0;
		for(int i=0;table[i]!=null;i++)
			get(table[i]);
		System.out.println("테이블의 크기 = " + M);
		System.out.println("저장된 (key, value)쌍의 수 = " + N);
		System.out.println("평균 검색 길이 = " + (double)totalLength/(double)N);
		System.out.println("최대 검색 길이 = " + maxLength + " (key = " + maxKey + ")");

	}
		
	private int hash(K key) {return (key.hashCode() & 0x7fffffff) % M;}
	public V get(K key) {
		int count = 0;
		for(int i= hash(key);keys[i]!=null;i=(i+1) % M) {
			totalLength++;
			count++;
			if(keys[i].equals(key)) {
				if(count > maxLength) {
					maxLength = count;
					maxKey = key;
				}
				return vals[i];
			}	
		}
		return null;
	}
	
	public int put(K key, V value) {
		int length = 1;
		if(N >= M/2) length += resize(2*M+1);
		int i;
		for(i=hash(key);keys[i] != null; i = (i+1) % M) {
			if(keys[i].equals(key)) {
				vals[i]=value;
				return length;
			}
			length++;
		}
		keys[i] = key;
		vals[i] = value;
		table[N] = key;
		N++;

		return length;
	}
	
	private int resize(int cap) {
		int length_r= 0;
		LinearProbingHashST<K,V> t;
		t = new LinearProbingHashST<>(cap);
		for(int i=0; i < M;i++)
			if(keys[i] != null)
				length_r += t.put(keys[i], vals[i]);
		
		keys = t.keys;
		vals = t.vals;
		M = t.M;
		table = t.table;
		return length_r;
	}
}


class DoubleHashingST<K,V> {
	private int N;
	private int M;
	private int M2;
	private K[] keys;
	private V[] vals;
	private K[] table;
	private int totalLength;
	private int maxLength;
	private K maxKey;
	
	public DoubleHashingST(){this(31);}
	public DoubleHashingST(int M){
		this.M=minDecimal(M);
		this.M2=maxDecimal(this.M);
		keys = (K[]) new Object[this.M];
		vals = (V[]) new Object[this.M];
		table = (K[]) new Object[this.M];
		totalLength = 0;
		maxLength = 1;
		maxKey = null;
		N = 0;
	}
	
	public int minDecimal(int M) {
		boolean flag = false;
		for(int Decimal=M;;Decimal++) {
			for(int i=2;i<Decimal;i++) {
				if(Decimal%i==0)
					flag=true;
			}
			if(flag == false)
				return Decimal;
			else
				flag = false;
		}
	}
	public int maxDecimal(int M) {
		boolean flag = false;
		for(int Decimal=M-1;M > 0;Decimal--) {
			for(int i=2;i<Decimal;i++) {
				if(Decimal%i==0)
					flag=true;
			}
			if(flag==false)
				return Decimal;
			else
				flag = false;
		}
		return 2;
	}
	
	public void print() {
		totalLength=0;
		for(int i=0;table[i]!=null;i++)
			get(table[i]);
		System.out.println("테이블의 크기 = " + M);
		System.out.println("저장된 (key, value)쌍의 수 = " + N);
		System.out.println("평균 검색 길이 = " + (double)totalLength/(double)N);
		System.out.println("최대 검색 길이 = " + maxLength + " (key = " + maxKey + ")");

	}
	private int hash(K key, int i) {
		int h = (key.hashCode() & 0x7fffffff) % M;
		int f = 1 + ((key.hashCode() & 0x7fffffff) % M2);
		
		return ((h + i*f) & 0x7fffffff) % M;
	}
	
	public V get(K key) {
		int count = 0;
		for(int i=0;keys[hash(key,i)]!=null;i++) {
			count++;
			totalLength++;
			if(keys[hash(key,i)].equals(key)) {
				if(count > maxLength) {
					maxLength = count;
					maxKey = key;
				}
				return vals[hash(key,i)];
			}
		}
		
		return null;
	}
	
	public int put(K key, V value) {
		int length = 1;
		
		if(N >= M/2) length += resize(2*M+1);
		int i;
		for(i=0;keys[hash(key,i)]!=null;i++) {
			if(keys[hash(key,i)].equals(key)) {
				vals[hash(key,i)]=value;
				return length;
			}
			length++;
		}
		keys[hash(key,i)]=key;
		vals[hash(key,i)]=value;
		table[N]=key;
		N++;
		
		return length;
	}
	
	private int resize(int cap) {
		int length_r = 0;
		DoubleHashingST<K,V> t;
		t = new DoubleHashingST<>(cap);
		for(int i=0; i < M;i++)
			if(keys[i] != null)
				length_r += t.put(keys[i], vals[i]);
		
		keys = t.keys;
		vals = t.vals;
		M = t.M;
		M2 = t.M2;
		table = t.table;
		return length_r;
	}
}


class CuckooHashingST<K,V> {
	private int N;
	private int M;
	private int M1;
	private int M2;
	private K[] T1_keys;
	private V[] T1_vals;
	private K[] T2_keys;
	private V[] T2_vals;
	private K[] table;
	private int totalLength;
	private int maxLength;
	private K maxKey;
	private double fillfactor;
	private int MaxLoop;
	
	public CuckooHashingST(){this(31, 0.45);}
	public CuckooHashingST(int M , double fillfactor){
		this.M=minDecimal(M);
		this.M1=minDecimal((this.M/2)+1);
		this.M2 = this.M - this.M1;
		T1_keys = (K[]) new Object[this.M1];
		T1_vals = (V[]) new Object[this.M1];
		T2_keys = (K[]) new Object[this.M2];
		T2_vals = (V[]) new Object[this.M2];
		table = (K[]) new Object[this.M];
		totalLength = 0;
		maxLength = 1;
		maxKey = null;
		this.fillfactor = fillfactor;
		MaxLoop = (int) (3 * (Math.log10(M*fillfactor)/Math.log10(1+0.5-fillfactor)));
		N = 0;
	}
	
	public int minDecimal(int M) {
		boolean flag = false;
		for(int Decimal=M;;Decimal++) {
			for(int i=2;i<Decimal;i++) {
				if(Decimal%i==0)
					flag=true;
			}
			if(flag == false)
				return Decimal;
			else
				flag = false;
		}
	}

	public void print() {
		totalLength=0;
		for(int i=0;table[i]!=null;i++)
			get(table[i]);
		System.out.println("테이블 1의 크기 = " + M1 + ", 테이블 2의 크기 = " + M2);
		System.out.println("저장된 (key, value)쌍의 수 = " + N);
		System.out.println("평균 검색 길이 = " + (double)totalLength/(double)N);
		System.out.println("최대 검색 길이 = " + maxLength + " (key = " + maxKey + ")");
	}
	private int h1(K key) {
		return (key.hashCode() & 0x7fffffff) % M1;
	}
	
	private int h2(K key) {
		return (key.hashCode() & 0x7fffffff) % M2;
	}
	
	public V get(K key) {
		int count = 0;
		for(int i=0; i<MaxLoop;i++) {
			count++;
			totalLength++;
			if(T1_keys[h1(key)].equals(key)) {
				if(count > maxLength) {
					maxLength = count;
					maxKey = key;
				}
				return T1_vals[h1(key)];
			}
			count++;
			totalLength++;
			if(T2_keys[h2(key)].equals(key)) {
				if(count > maxLength) {
					maxLength = count;
					maxKey = key;
				}
				return T2_vals[h2(key)];
			}
		}
		return null;
	}
	
	public int put(K key, V value) {
		K tempk1, tempk2;
		V tempv1, tempv2;
		tempk2 = key;
		tempv2 = value;
		int length = 0;
		
		for(int i=0;i < MaxLoop;i++) {
			length++;
			if(T1_keys[h1(tempk2)] == null) {
				T1_keys[h1(tempk2)]=tempk2;
				T1_vals[h1(tempk2)]=tempv2;
				table[N] = key;
				N++;
				return length;
			}
			else {
				tempk1=T1_keys[h1(tempk2)];
				tempv1=T1_vals[h1(tempk2)];
				T1_keys[h1(tempk2)]=tempk2;
				T1_vals[h1(tempk2)]=tempv2;
			}
			
			length++;
			if(T2_keys[h2(tempk1)] == null) {
				T2_keys[h2(tempk1)]=tempk1;
				T2_vals[h2(tempk1)]=tempv1;
				table[N] = key;
				N++;
				return length;
			}
			else {
				tempk2=T2_keys[h2(tempk1)];
				tempv2=T2_vals[h2(tempk1)];
				T2_keys[h2(tempk1)]=tempk1;
				T2_vals[h2(tempk1)]=tempv1;
			}
			
		}
		
		length += resize((2*M)+1);
		length += put(key,value);
		return length;
	}
	private int resize(int cap) {
		int length_r = 0;
		CuckooHashingST<K,V> t;
		t = new CuckooHashingST<>(cap,this.fillfactor);
		
		for(int i=0; i < M1;i++)
			if(T1_keys[i] != null)
				length_r+=t.put(T1_keys[i], T1_vals[i]);
		
		for(int i=0; i < M2;i++)
			if(T2_keys[i] != null)
				length_r+=t.put(T2_keys[i], T2_vals[i]);
		
		T1_keys = t.T1_keys;
		T1_vals = t.T1_vals;
		T2_keys = t.T2_keys;
		T2_vals = t.T2_vals;
		M = t.M;
		M1 = t.M1;
		M2 = t.M2;
		MaxLoop = t.MaxLoop;
		table = t.table;
		return length_r;
	}
}

