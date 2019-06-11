import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class HBGG {
	int N,M,K,R,G;
	int iterNum = 20;
	double alpha=1, beta=0.01, eta=1, gamma=0.01, omega = 0.01;
	int[][] trainset;
	int[][] groups;
	
	int[] Z;
	int[] Rg;
	
	int[][] nzr;
	int[][] ngz;
	int[][] nzu;
	int[][] nzv;
	int[][] nrv;
	
	int[] ngzsum;
	int[] nzrsum;
	int[] nzusum;
	int[] nzvsum;
	int[] nrvsum;
	
	double[][] mu;
	double[][] sigma;
	
	double[][] geo;
	Map<Integer,List<Integer>> testset;
	Input input;
	public HBGG(Input input,int K, int R){
		this.K = K;
		this.R = R;
		this.input = input;
		groups = Dataset.readGroups(input.groupfile);
		trainset = Dataset.readSamples(input.trainfile);
		M = Dataset.countNum(input.umapfile);
		N = Dataset.countNum(input.emapfile);
        geo = Dataset.readGeo(input.geofile);
		G = trainset.length;
		
		Z = new int[G];
		Rg = new int[G];
		ngz = new int[G][K];
		nzu = new int[K][M];
		nzv = new int[K][N];
		nrv = new int[R][N];
		nzr = new int[K][R];
		
		ngzsum = new int[G];
		nzrsum = new int[K];
		nzusum = new int[K];
		nzvsum = new int[K];
		nrvsum = new int[R];
		mu = new double[R][2];
		sigma = new double[R][2];
		testset = Dataset.readTest(input.testfile);
	}
	
	public void initialize(){
		System.out.println("initializing model...");
		Random rand = new Random();
		int gid, e, z, r;
		for(int g=0; g<trainset.length;g++){
			e = trainset[g][1];
			gid = trainset[g][0];
			z = rand.nextInt(K);
			r = rand.nextInt(R);
			Z[g] = z;
			Rg[g] = r;
			ngz[g][z]++;
			ngzsum[g]++;
			nzv[z][e]++;
			nzvsum[z]++;
			nrv[r][e]++;
			nrvsum[r]++;
			nzr[z][r]++;
			nzrsum[z]++;
			for(int u : groups[gid]){
				nzu[z][u]++;
				nzusum[z]++;
			}
		}
		
		for(r=0;r<R;r++){
			mu[r][0] = 39.9+rand.nextFloat();
			mu[r][1] = 116.3+rand.nextFloat();
			sigma[r][0] = rand.nextFloat()*10;
			sigma[r][1] = rand.nextFloat()*10;
		}
	}
	
	public int draw(double[] a){
		double r = Math.random();
		for(int i = 0; i<a.length;i++){
			r = r - a[i];
			if(r<0) return i;
		}
		return a.length-1;
	}
	
	public void gibbs(){
		double[][] P = new double[K][R];
		int e,gid,z,r,k,rg;
		for(int g=0; g<trainset.length;g++){
			e = trainset[g][1];
			gid = trainset[g][0];
			z = Z[g];
			r = Rg[g];
			ngz[g][z]--;
			ngzsum[g]--;
			nzv[z][e]--;
			nzvsum[z]--;
			nrv[r][e]--;
			nrvsum[r]--;
			nzr[z][r]--;
			nzrsum[z]--;
			for(int u : groups[gid]){
				nzu[z][u]--;
				nzusum[z]--;
			}
			
			for(k=0;k<K;k++){
				double zp = (ngz[g][k]+alpha)/(ngzsum[g]+K*alpha)*(nzv[k][e]+gamma)/(nzvsum[k]+N*gamma);
				for(int u : groups[gid]){
					zp *= (nzu[k][u]+beta)/(nzusum[k]+M*beta);
				}
				for(rg=0;rg<R;rg++){
					P[k][rg] = (nzr[k][rg]+eta)/(nzrsum[k]+R*eta)*(nrv[rg][e]+omega)/(nrvsum[rg]+N*omega)*zp*pdf(e,rg);
				}
			}
			
			
			double[] Pravel = Util.newnorm(Util.ravel(P));
//			Util.print(Pravel);
			int index = draw(Pravel);
//			System.out.println(index);
			int[] zr = Util.unravel_index(index, K, R);
			z = zr[0];
			r = zr[1];
//			System.out.println(r);
			Z[g] = z;
			Rg[g] = r;
			ngz[g][z]++;
			ngzsum[g]++;
			nzv[z][e]++;
			nzvsum[z]++;
			nrv[r][e]++;
			nrvsum[r]++;
			nzr[z][r]++;
			nzrsum[z]++;
			for(int u : groups[gid]){
				nzu[z][u]++;
				nzusum[z]++;
			}
		}
		
		for(r=0;r<R;r++)
			updateGaussian(r);
		
	}
	
	public void train(int iterNum){
		//iterNum is the number of iterations
		for(int it=0;it<iterNum;it++){
			gibbs();
			System.out.println("iteration "+it);
		}
	}
	public double pdf(int e, int r){
		double x = geo[e][0] - mu[r][0];
		double y = geo[e][1] - mu[r][1];
		double temp = Math.exp(-0.5*((x*x)/(sigma[r][0]*sigma[r][0])+(y*y)/(sigma[r][1]*sigma[r][1])));
		return temp/(2*3.142*sigma[r][0]*sigma[r][1]);
	}
	
	public void updateGaussian(int r){
		List<Integer> lr = new ArrayList<Integer>();
		for(int g=0;g<trainset.length;g++){
			if(Rg[g]==r)
				lr.add(trainset[g][1]);
		}
		if(lr.size()<=1)
			return;
		for(int e : lr){
			mu[r][0]+= geo[e][0];
			mu[r][1]+= geo[e][1];
		}
		mu[r][0] /= lr.size();
		mu[r][1] /= lr.size();
		for(int e: lr){
			sigma[r][0] += (geo[e][0]-mu[r][0])*(geo[e][0]-mu[r][0]);
			sigma[r][1] += (geo[e][1]-mu[r][1])*(geo[e][1]-mu[r][1]);
		}
		sigma[r][0] /= lr.size()-1;
		sigma[r][1] /= lr.size()-1;
	}
	
	public double[][] estParameter(int[][] m, int[] sum, double sp){
		int r = m.length;
		int c = m[0].length;
		double[][] p = new double[r][c];
		for(int i=0;i<r;i++)
			for(int j=0;j<c;j++)
				p[i][j] = (m[i][j]+sp)/(sum[i]+c*sp);
		return p;
	}
	
	public Model getModel(){
		Model model = new Model();
//		model.theta = estParameter(ngz,ngzsum,alpha);
		model.phiZR = estParameter(nzr,nzrsum,eta);
		model.phiZV = estParameter(nzv,nzvsum,gamma);
		model.phiRV = estParameter(nrv,nrvsum,omega);
		model.phiZU = estParameter(nzu,nzusum,beta);
		return model;
	}
	
public void inference(Model model){
//		Map<Integer,double[]> theta = new HashMap<Integer,double[]>();
		Random rand = new Random();
		int gnum = groups.length;
		int[][] ngz = new int[gnum][K];
		int[] ngsum = new int[gnum];
		int[] Z = new int[gnum];
		int z;
		for(int gid : testset.keySet()){
			z = rand.nextInt(K);
			Z[gid] = z;
			ngz[gid][z]++;
			ngsum[gid]++;
		}
//		Util.print(ngz);
		double[] p = new double[K];
		for(int iter=0;iter<iterNum;iter++){
		for(int gid : testset.keySet()){
				z = Z[gid];
				ngz[gid][z]--;
				ngsum[gid]--;
				for(int k=0;k<K;k++){
					p[k] = (ngz[gid][k]+alpha);
					for(int u : groups[gid]){
						p[k] *= model.phiZU[k][u];
					}
				}
				Util.norm(p);
				z = draw(p);
				Z[gid] = z;
				ngz[gid][z]++;
				ngsum[gid]++;
		}
		}
//		Util.print(ngz);
		model.theta = estParameter(ngz,ngsum,alpha);
	}

	public Set<Integer> getCandEvent(){
		Set<Integer> cand = new HashSet<Integer>();
		for(Map.Entry<Integer, List<Integer>> entry : testset.entrySet()){
			for(Integer eid : entry.getValue())
				cand.add(eid);
		}
		return cand;
	}
	public Map<Integer,int[]> recommend(Model model,int topn){
//		Map<Integer,Set<Integer>> gh = getGroupHist();
		System.out.println("making recommendation...");
		List<Integer> candlist = new ArrayList<Integer>(getCandEvent());
		double[][] score = new double[groups.length][candlist.size()];
		Map<Integer,int[]> reclist = new HashMap<Integer,int[]>();
		int k,r;
		double s=0, sr,su;
		for(int gid : testset.keySet()){
			for(int i=0;i<candlist.size();i++){
				for(k=0;k<K;k++){
					s = model.theta[gid][k]*model.phiZV[k][candlist.get(i)];
					su = 0;
					for(int u : groups[gid]){
						su *= model.phiZU[k][u];
					}
					s *= Math.pow(su, 1/groups[gid].length);
					sr = 0;
					for(r=0;r<R;r++){
						sr += model.phiZR[k][r]*model.phiRV[r][candlist.get(i)]*pdf(candlist.get(i),r);
					}
//					System.out.println(sr);
					s *= sr;
				}
				score[gid][i] += s;
//				System.out.println(score[gid][i]);
			}
		}
		
		
		for(int i=0;i<groups.length;i++){
			int[] index = ArrayUtils.argsort(score[i],false);
			int[] array = new int[topn];
			for(int j=0;j<topn;j++)
				array[j] = candlist.get(index[j]);
			reclist.put(i, array);
		}
		return reclist;
	}
	public static void main(String[] args) {
//		long startTime = System.currentTimeMillis();
		int K=50,R=50; //K is number of topics, R is number of regions
		Input input = new Input(); 
		HBGG hbgg = new HBGG(input,K,R);
		hbgg.initialize();
		hbgg.train(20);
//		for(int i=0;i<K;i++)
//			System.out.println(hbgg.nzvsum[i]);
//		Util.print(hbgg.Rg);
//		Util.print(hbgg.sigma);
		Model model = hbgg.getModel();
		
//		Util.print(hbgg.nzu);
		
//		System.out.println(hbgg.Rg.length);

		hbgg.inference(model);
//		Util.print(model.phiZR);
		Map<Integer,int[]> list = hbgg.recommend(model, 20);
//		IO.saveRecList(list, String.format("dataset/douban/Reclist_%d.txt",2)); // save recommendation lists
//		long endTime = System.currentTimeMillis();
//		System.out.println("程序运行时间：" + (endTime - startTime) + "ms"); 
//		for(int i=0;i<50;i++)
//			Util.print(list.get(i));
//		Evaluation.evaluate(list, hbgg.testset, "Recall", 5);
		int topn = 10;
//		list = IO.readRecList("dataset/douban/Reclist_2.txt");
		Evaluation.evaluate(list,hbgg.testset,"Recall",topn); // evaluate the precision and recall of recommendations
	}
}
