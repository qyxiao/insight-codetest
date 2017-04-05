import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;



public class process_log {

	HashMap<String,Integer> activeAddress;
	HashMap<String,Integer> resources;
	String inputPath;
	String outputPath;
	
	public process_log(String inputPath, String outputPath){
		this.activeAddress = new HashMap<String,Integer>();
		this.resources = new HashMap<String,Integer>();
		this.inputPath = inputPath;
		this.outputPath = outputPath;
	}
	
		
	private static void minHeapify(String[] arr, HashMap<String,Integer> strMap, int size, int index){
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
	
	
    public static String[] heapGen(int num,HashMap<String,Integer> strMap){
		String[] ans = new String[num];
		int size =0;
		for(String key:strMap.keySet()){
			if(size<num){
				ans[size]=key;
				size++;
				if(size==num){
					heapSort(ans,strMap);
				}
			}else if(strMap.get(key)>strMap.get(ans[0])){
				ans[0]=key;
				minHeapify(ans,strMap,num,0);
			}
		}
		return ans;
	}
    
    
    public static void heapSort(String[] ans,HashMap<String,Integer> strMap){
    	int len=ans.length;
    	for(int i=len/2-1;i>=0;i--){   //// not sure len/2-1
    		minHeapify(ans, strMap, len, i);
    	}
    }
    
///////////////////////////////////////////////////////////////////////////////////    
	public void readHistory(String path) throws Exception{
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line;
		line = reader.readLine();
		
		while(line != null ){
			String[] parts = line.split(" ");
			//System.out.println(parts[9]);
			activeAddress.put(parts[0], activeAddress.getOrDefault(parts[0], 0)+1);
			int bandWidth = parts[9]=="-"? 0:Integer.parseInt(parts[9]);
			resources.put(parts[6], resources.getOrDefault(parts[6], 0)+ bandWidth);
			line = reader.readLine(); 
		}
		reader.close();
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
	
    public void feature2(int num,String outputPath) throws IOException{
    	String[] ans = heapGen(num,resources);
    	BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
		for(String key:ans){
			writer.write(key+","+resources.get(key)+"\n");
		}
		writer.close();
    }
    
/////////////////////////////////////////////////// 
    private class TimeRecord{
    	private String text;
    	private int basisGap;
    	private int frequency;
    	
    	TimeRecord(String text, int gap, int frequency){
    		this.text = text;
    		this.basisGap = gap;
    		this.frequency = frequency;
    	}
    	
    	boolean textEquals(String testText){
    		return text.equals(testText);
    	}
    	
    	void frequenAddOne(){
    		frequency++;
    	}
    	
    	int getBasisGap(){
    		return basisGap;
    	}
    	
    	int getFrequency(){
    		return frequency;
    	}
    	
    	String getText(){
    		return text;
    	}
    	
    }
    
    
    public void feature3(int num) throws IOException{
    	BufferedReader reader = new BufferedReader(new FileReader(inputPath));
		String line = reader.readLine();
		
		LinkedList<TimeRecord> intervalList = new LinkedList<TimeRecord>();
		int basisDay = 0, basisHour = 0, basisMinute = 0, basisSecond = 0;
		String[] strHeap = new String[num];
		int count = 0;
		int frequenSum=0;
		HashMap<String, Integer> strRecord = new HashMap<String, Integer>();
		
		if(line!=null){
			String parts = line.split(" ")[3];
			basisDay = Integer.parseInt(parts.substring(1,3)); 
			basisHour = Integer.parseInt(parts.substring(13,15)); 
			basisMinute = Integer.parseInt(parts.substring(16,18)); 
			basisSecond = Integer.parseInt(parts.substring(19,21)); 
			intervalList.add(new TimeRecord(parts,0,1));
			frequenSum++;
			line = reader.readLine();
		}
		while(line != null){
			
			String parts = line.split(" ")[3];
			if(intervalList.getLast().textEquals(parts)){
				intervalList.getLast().frequenAddOne();
				frequenSum++;
			}else{
				int gap = strConvertTime(parts, basisDay,  basisHour,  basisMinute,  basisSecond);
				System.out.println(gap); //// test
				if(gap - intervalList.getFirst().getBasisGap() > 3600){
					//TODO
					if(count<num){
						strHeap[count]=intervalList.getFirst().getText();
						strRecord.put(strHeap[count], frequenSum);
						count++;
						if(count==num){
							heapSort(strHeap,strRecord);
						}
					}else if(frequenSum > strRecord.get(strHeap[0])){
						System.out.println(frequenSum); //// test
						strRecord.remove(strHeap[0]);
						strHeap[0]=intervalList.getFirst().getText();
						strRecord.put(strHeap[0], frequenSum);
						minHeapify(strHeap,strRecord,num,0);
					}
					frequenSum -= intervalList.getFirst().getFrequency();
					intervalList.removeFirst();
					while(intervalList.size()>0 && gap - intervalList.getFirst().getBasisGap() > 360){
						frequenSum -= intervalList.getFirst().getFrequency();
						intervalList.removeFirst();
					}
				}
				intervalList.add(new TimeRecord(parts,gap,1));
				frequenSum++;
			}
			line = reader.readLine();
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("E:/Ellipse/JavaProject/feature3.txt"));
		for(String key:strHeap){
			writer.write(key+","+strRecord.get(key)+"  \n");    //// more format work here
		}
		writer.close();
    }
    

    private int strConvertTime(String part,int basisDay, int basisHour, int basisMinute, int basisSecond){
    	int ans = 0;
    	ans+= (Integer.parseInt(part.substring(1,3))-basisDay)*86400;
    	ans+= (Integer.parseInt(part.substring(13,15))-basisHour)*3600;
    	ans+= (Integer.parseInt(part.substring(16,18))-basisMinute)*60;
    	ans+= (Integer.parseInt(part.substring(19,21))-basisSecond);
    	return ans;
    }
    
////////////////////////////////////////////////////	
	public static void main(String[] args){
        String inputPath = "E:/Ellipse/JavaProject/subtestlog.txt";
        String outputPath = "E:/Ellipse/JavaProject/"; 
        process_log test = new process_log(inputPath,outputPath);   ///// need fix
        try{
        	test.feature3(4);
//        	test.readHistory(inputPath);
//        	test.feature1(2,"E:/Ellipse/JavaProject/feature1.txt");
//        	test.feature2(4,"E:/Ellipse/JavaProject/feature2.txt");
        	
//        	BufferedReader reader = new BufferedReader(new FileReader(inputPath));
//    		String line;
//    		line = reader.readLine();
//        	while(line!=null){
//        		String[] parts = line.split(" ");
//        		System.out.println(parts[3].substring(13, 15));
//        		System.out.println(parts[3].substring(19, 21));
//        		line = reader.readLine();
        		
//        	}
        }catch(Exception e){
        	// ToDo
        }
        
//        for(String key:test.activeAddress.keySet()){
//        	System.out.println(key+" "+test.activeAddress.get(key));
//        }
//        
//        for(String key:test.resources.keySet()){
//        	System.out.println(key+" "+test.resources.get(key));
//        }
//        
//        for(int i:test.busyHours){
//        	System.out.println(i);
//        }
        
    }
}
