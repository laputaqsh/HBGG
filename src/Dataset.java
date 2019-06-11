import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Dataset {
	public static int U;
//	public static int G;
	public static int W;
	public static int E;
	public static int H;
	public static int TestE;
	
	
	public static int countNum(String mapfile){
		BufferedReader reader = null;
		int num = 0;
        try {
            reader = new BufferedReader(new FileReader(mapfile));
//            FileWriter fw = new FileWriter(geofile);
//            String line = null;
            while (reader.readLine() != null) {
            	num++;
            }
            reader.close();
            return num;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        } 
	}
	
	public static double[][] readRatingMatrix(String fileName,int row, int col){

		double[][] matrix = new double[row][col];
		BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = null;
            
            while ((line = reader.readLine()) != null) {
            	String[] temp = line.split(",");
            	int gid = Integer.parseInt(temp[0]);
            	int eid = Integer.parseInt(temp[1]);
            	matrix[gid][eid] = 1;
            	
            }
            reader.close();
            return matrix;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
	}
    
    public static int getEventNum(String fileName){
        int count = 0;
    	File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            while (reader.readLine()!= null) {
            	count++;
            }
            reader.close();
            return count;
        }catch (IOException e) {
	        e.printStackTrace();
	        return 0;
	        } 
    }


	public static int[][] loadCorpus(String fileName){
		Map<Integer,List<Integer>> data = new HashMap<Integer,List<Integer>>();
		Set<Integer> eset = new HashSet<Integer>();
		BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = null;
            
            while ((line = reader.readLine()) != null) {
            	String[] temp = line.split(",");
            	int gid = Integer.parseInt(temp[0]);
            	int eid = Integer.parseInt(temp[1]);
            	eset.add(eid);
            	if(data.containsKey(gid))
            		data.get(gid).add(eid);
            	else{
            		List<Integer> list = new ArrayList<Integer>();
            		list.add(eid);
            		data.put(gid, list);
            	}
            	
            }
            reader.close();
            int[][] corpus = new int[data.size()][];
            for(int gid : data.keySet()){
            	corpus[gid] = new int[data.get(gid).size()];
            	for(int i=0;i<corpus[gid].length;i++)
            		corpus[gid][i] = data.get(gid).get(i);
            }
            W = eset.size();
            return corpus;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
	}
	
	public static int[][]  readSamples(String fileName){
		List<int[]> data = new ArrayList<int[]>();
		 File file = new File(fileName);
	        BufferedReader reader = null;
	        try {
	            reader = new BufferedReader(new FileReader(file));
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	            	String[] temp = line.split(",");
	            	int[] sample = new int[temp.length];
	            	for(int i=0;i<temp.length;i++){
	            		sample[i] = Integer.parseInt(temp[i]);
	            	}
	            	data.add(sample);
	            }
	            reader.close();
	            return data.toArray(new int[][] {});
	        } catch (IOException e) {
	            e.printStackTrace();
	            return null;
	        } finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (IOException e1) {
	                }
	            }
	        }
	}
	
	public static int[] readOrganizer(String fileName){
		List<Integer> org = new ArrayList<Integer>();
		File file = new File(fileName);
        BufferedReader reader = null;
        int [] value;
        Set<Integer> oset = new HashSet<Integer>();
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
            	int oid = Integer.parseInt(line);
            	org.add(oid);
            	oset.add(oid);
            }
            reader.close();
            value = new int[org.size()];
            for(int i=0;i<org.size();i++)
            	value[i] = org.get(i);
            H = oset.size();
            return value;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
	}
	public static double[][] readGeo(String fileName){
		Map<Integer,double[]> geo = new HashMap<Integer,double[]>();
		File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;

            while ((line = reader.readLine()) != null) {
            	String[] temp = line.split(",");
            	double[] sample = new double[temp.length-1];
//            	System.out.println(line);
            	for(int i=1;i<temp.length;i++)
            		sample[i-1] =  Double.parseDouble(temp[i]);
            	geo.put(Integer.parseInt(temp[0]),sample);

            }
            reader.close();
            double[][] newgeo = new double[geo.size()][];
            for(int eid : geo.keySet())
            	newgeo[eid] = geo.get(eid);
            return newgeo;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
	}
	
	public static int[][] readCorpus(String fileName,String sep){
		List<int[]> data = new ArrayList<int[]>();
		 File file = new File(fileName);
	        BufferedReader reader = null;
	        try {
	            reader = new BufferedReader(new FileReader(file));
	            String line = null;
	            int count = 0;
	            while ((line = reader.readLine()) != null) {
	            	String[] temp = line.split(sep);
	            	int[] sample = new int[temp.length];
	            	for(int i=0;i<temp.length;i++){
	            		sample[i] =  Integer.parseInt(temp[i]);
	            		if(count<sample[i])
	            			count = sample[i];
	            	}
	            	data.add(sample);
	            }
	            reader.close();
	            W = count+1;
	            return data.toArray(new int[][] {});
	        } catch (IOException e) {
	            e.printStackTrace();
	            return null;
	        } finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (IOException e1) {
	                }
	            }
	        }
	}
	
	public static List<Integer> readCand(String fileName){
		List<Integer> list = new ArrayList<Integer>();
		File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
            	list.add(Integer.parseInt(line));
     
            }
            reader.close();
            return list;
        }catch (IOException e) {
	            e.printStackTrace();
	            return null;
	        } finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (IOException e1) {
	                }
	            }
	        }
	}
	
	public static Map<Integer,List<Integer>> readTest(String fileName){
		File file = new File(fileName);
        BufferedReader reader = null;
        Map<Integer,List<Integer>> test = new HashMap<Integer,List<Integer>>();
        Set<Integer> set = new HashSet<Integer>();
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
            	String[] temp = line.split(",");
            	Integer gid = Integer.parseInt(temp[0]);
            	Integer eid = Integer.parseInt(temp[1]);
            	set.add(eid);
            	if(test.containsKey(gid))
            		test.get(gid).add(eid);
            	else{
            		List<Integer> list = new ArrayList<Integer>();
            		list.add(eid);
            		test.put(gid, list);
            	}
            }
            reader.close();
            TestE = set.size();
            return test;
        }catch (IOException e) {
	            e.printStackTrace();
	            return null;
	        } finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (IOException e1) {
	                }
	            }
	        }
	}
	
	public static int[][] readGroups(String fileName){
		List<int[]> data = new ArrayList<int[]>();
//		int[][][] Ob;
//		Set<Integer> userset = new HashSet<Integer>();
		 File file = new File(fileName);
	        BufferedReader reader = null;
	        try {
	            reader = new BufferedReader(new FileReader(file));
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	            	String[] temp = line.split(",");
	            	int[] sample = new int[temp.length];
	            	for(int i=0;i<temp.length;i++){
	            		sample[i] =  Integer.parseInt(temp[i]);
//	            		userset.add(sample[i]);
	            	}
	            	data.add(sample);
	            }
	            reader.close();
	            return data.toArray(new int[][] {});
	        } catch (IOException e) {
	            e.printStackTrace();
	            return null;
	        } finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (IOException e1) {
	                }
	            }
	        }
	}
	
	public static int getUserNum(int[][] groups){
		Set<Integer> uset = new HashSet<Integer>();
		for(int[] g : groups)
			for(int u : g)
				uset.add(u);
		return uset.size();
	}
	
	public static double[][] readModel(String fileName, String sep){
		List<double[]> data = new ArrayList<double[]>();
		 File file = new File(fileName);
	        BufferedReader reader = null;
	        try {
	            reader = new BufferedReader(new FileReader(file));
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	            	String[] temp = line.split(sep);
	            	double[] sample = new double[temp.length];
	            	for(int i=0;i<temp.length;i++){
	            		sample[i] = Double.parseDouble(temp[i]);
	            	}
	            	data.add(sample);
	            }
	            reader.close();
	            return data.toArray(new double[][] {});
	        } catch (IOException e) {
	            e.printStackTrace();
	            return null;
	        } finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (IOException e1) {
	                }
	            }
	        }
	}
	
	public static void main(String[] args) {
		String file = "dataset\\3_testset_new.csv";
		Map<Integer,List<Integer>> test = readTest(file);
		System.out.println(test.size());
		
//		System.out.println(geo[6]);
//		for(int i=0;i<10;i++){
//			for(int j=0;j<geo[43].length;j++)
//				System.out.print(geo[43][j]+" ");
//			System.out.println();
//		}
		
//		for(int i : train.get(2))
//			System.out.println(i);
	}
}
