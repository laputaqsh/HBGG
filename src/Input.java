
public class Input {
//	public int gsize;
	public String trainfile;
	public String testfile;
	public String geofile;
	public String groupfile;
	public String candfile;
	public String umapfile;
	public String emapfile;
//	public String theta;
//	public String phi;
//	public static String orgfile = "/home/duyulu/dataset/2_organizer.csv";
	

    public Input(){
        trainfile = "dataset/douban/gtrain_2.csv";
        testfile = "dataset/douban/gtest_2.csv";
        groupfile = "dataset/douban/groups_2.csv";
        geofile = "dataset/douban/geo.csv";
        umapfile = "dataset/douban/user_map.csv";
        emapfile = "dataset/douban/event_map.csv";
    }
}
