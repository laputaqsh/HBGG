import java.util.List;
import java.util.Map;

public class Evaluation {
	public static void evaluate(Map<Integer,int[]> lists,Map<Integer,List<Integer>> testset, String metric,int n){
		if("Recall".equals(metric)){
			double hit = 0;
			int recall = 0;
			int precision =0;
			int cnt;
			for(int gid: testset.keySet()){
				recall += testset.get(gid).size();
				precision += n;
				cnt = 0;
				for(int e : lists.get(gid)){
					if(testset.get(gid).contains(e)&&cnt<n)
						hit++;
					cnt++;
				}
			}
//			System.out.println(recall);
//			recall = hit/recall;
			System.out.println("topn="+n);
			System.out.println("hit="+hit);
			System.out.println("precision="+hit/precision);
			System.out.printf("Recall=%f\n",hit/recall);
			System.out.println("number of recommend items: "+precision);
			System.out.println("number of truth: "+recall);
//			return recall;
		}
		else {
			double ndcg = 0;
			int count = 0,i;
			for(int gid : testset.keySet()){
				int[] array = new int[n];
				for(i = 0;i<n;i++)
					array[i] = lists.get(gid)[i];
				double ndcg1 = Util.ndcg(array,testset.get(gid));
				if(ndcg1>0){
					ndcg += ndcg1;
					count++;
				}
			}
			System.out.println("nDCG="+ndcg/count);

		}
	}
}
