import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;



public class process_log {

	HashMap<String,Integer> activeAddress;
	HashMap<String,Integer> resources;
	int[] busyHours;
	
	public process_log(){
		this.activeAddress = new HashMap<String,Integer>();
		this.resources = new HashMap<String,Integer>();
		this.busyHours = new int[24];
	}
	
	
	
	public void readHistory(String path) throws Exception{
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line = "";
		line = reader.readLine();
		
		while(line != null ){
			String[] parts = line.split(" ");
			//System.out.println(parts[9]);
			activeAddress.put(parts[0], activeAddress.getOrDefault(parts[0], 0)+1);
			int bandWidth = parts[9]=="-"? 0:Integer.parseInt(parts[9]);
			resources.put(parts[6], resources.getOrDefault(parts[6], 0)+ bandWidth);
			busyHours[Integer.parseInt(parts[3].split(":")[1])]++;
			line = reader.readLine(); 
		}
		reader.close();
    }
	
	
	private void minHeapify(String[] arr, HashMap<String,Integer> strMap, int size, int index){
		int left = index*2+1;
		int right = index*2+2;
		int smallest = index;
		if(left<size && strMap.get(arr[smallest])>strMap.get(arr[left])){smallest = left;}
		if(right<size && strMap.get(arr[smallest])>strMap.get(arr[right])){smallest = right;}
		if(smallest!=index){
			String temp = arr[index];
			arr[index]=arr[smallest];
			arr[smallest]=temp;
			minHeapify(arr,strMap,size,smallest);
		}
	}
	
	
    public void feature1(int num,String outputPath) throws IOException{
		String[] ans = new String[num];
		int size =0;
		for(String key:activeAddress.keySet()){
			if(size<num){
				ans[size]=key;
				size++;
				minHeapify(ans,activeAddress,size,size-1); //// not sure
			}else if(activeAddress.get(key)>activeAddress.get(ans[0])){
				ans[0]=key;
				minHeapify(ans,activeAddress,num,0);
			}
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
		for(String key:ans){
			writer.write(key+","+activeAddress.get(key)+"\n");
		}
		writer.close();
	}
	
	
    public String[] heapSort(int num,HashMap<String,Integer> strMap){
		String[] ans = new String[num];
		int size =0;
		for(String key:strMap.keySet()){
			if(size<num){
				ans[size]=key;
				size++;
				minHeapify(ans,strMap,size,size-1); //// not sure
			}else if(strMap.get(key)>strMap.get(ans[0])){
				ans[0]=key;
				minHeapify(ans,strMap,num,0);
			}
		}
		return ans;
	}
    
    
    public void feature2(int num,String outputPath) throws IOException{
    	String[] ans = heapSort(num,resources);
    	BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
		for(String key:ans){
			writer.write(key+","+resources.get(key)+"\n");
		}
		writer.close();
    }
    
	
	public static void main(String[] args){
        String path = "E:/Ellipse/JavaProject/subtestlog.txt";
        process_log test = new process_log();
        try{
        	test.readHistory(path);
        	test.feature1(2,"E:/Ellipse/JavaProject/feature1.txt");
        	test.feature2(4,"E:/Ellipse/JavaProject/feature2.txt");
        }catch(Exception e){
        	// ToDo
        }
        
        for(String key:test.activeAddress.keySet()){
        	System.out.println(key+" "+test.activeAddress.get(key));
        }
        
        for(String key:test.resources.keySet()){
        	System.out.println(key+" "+test.resources.get(key));
        }
//        
//        for(int i:test.busyHours){
//        	System.out.println(i);
//        }
        
    }
}
